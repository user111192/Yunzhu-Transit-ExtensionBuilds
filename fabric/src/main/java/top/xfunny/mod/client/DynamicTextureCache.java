package top.xfunny.mod.client;


import org.mtr.core.servlet.MessageQueue;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.*;
import org.mtr.mod.render.MainRenderer;
import top.xfunny.mod.Init;
import top.xfunny.mod.client.resource.FontList;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;


public class DynamicTextureCache {
    private static final int COOL_DOWN_TIME = 10000;
    private static final Identifier DEFAULT_BLACK_RESOURCE = new Identifier(Init.MOD_ID, "textures/block/black.png");
    private static final Identifier DEFAULT_WHITE_RESOURCE = new Identifier(Init.MOD_ID, "textures/block/white.png");
    private static final Identifier DEFAULT_TRANSPARENT_RESOURCE = new Identifier(Init.MOD_ID, "textures/block/transparent.png");
    public static DynamicTextureCache instance = new DynamicTextureCache();
    private final Object2ObjectLinkedOpenHashMap<String, DynamicResource> dynamicResources = new Object2ObjectLinkedOpenHashMap<>();
    private final Object2LongArrayMap<Identifier> deletedResources = new Object2LongArrayMap<>();
    private final ObjectOpenHashSet<String> generatingResources = new ObjectOpenHashSet<>();
    private final MessageQueue<Runnable> resourceRegistryQueue = new MessageQueue<>();
    public int totalWidth;

    public void reload() {
        FontList.instance.FontReload();
        dynamicResources.values().forEach(dynamicResource -> dynamicResource.needsRefresh = true);
        generatingResources.clear();
    }

    public void tick() {
		final ObjectArrayList<String> keysToRemove = new ObjectArrayList<>();
		dynamicResources.forEach((checkKey, checkDynamicResource) -> {
			if (checkDynamicResource.expiryTime < System.currentTimeMillis()) {
				checkDynamicResource.remove();
				deletedResources.put(checkDynamicResource.identifier, System.currentTimeMillis() + COOL_DOWN_TIME);
				keysToRemove.add(checkKey);
			}
		});
		keysToRemove.forEach(dynamicResources::remove);

		final ObjectArrayList<Identifier> deletedResourcesToRemove = new ObjectArrayList<>();
		deletedResources.forEach((identifier, expiryTime) -> {
			if (expiryTime < System.currentTimeMillis()) {
				MinecraftClient.getInstance().getTextureManager().destroyTexture(identifier);
				deletedResourcesToRemove.add(identifier);
			}
		});
		deletedResourcesToRemove.forEach(deletedResources::removeLong);
	}


    public byte[] getTextPixels(String text, int[] dimensions, int maxWidth, float fontSize, int padding, Font font, int letterSpacing) {
        if (maxWidth <= 0) {
            dimensions[0] = 0;
            dimensions[1] = 0;
            return new byte[0];
        }
        totalWidth = 0;

        // 初始化字体渲染上下文
        final FontRenderContext context = new FontRenderContext(new AffineTransform(), false, false);

        // 选择合适的字体
        Font selectedFont = font.deriveFont(Font.PLAIN, fontSize);

        // 计算每个字符的宽度和总宽度（包括间距）
        FontMetrics fontMetrics = new Canvas().getFontMetrics(selectedFont);
        int totalTextWidth = 0;
        for (char c : text.toCharArray()) {
            totalTextWidth += fontMetrics.charWidth(c) + letterSpacing;
        }
        totalTextWidth -= letterSpacing; // 去掉最后一个字符后多加的间距
        totalTextWidth += 2 * padding;

        // 计算文本高度
        int textHeight = fontMetrics.getHeight() + 2 * padding;
        totalWidth = totalTextWidth;

        // 创建灰度图像
        BufferedImage textImage = new BufferedImage(totalTextWidth, textHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics2D = textImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 设置字体和颜色
        graphics2D.setFont(selectedFont);
        graphics2D.setColor(Color.WHITE);

        // 绘制文本，手动控制字符位置
        int x = padding; // 初始水平位置
        int y = padding + fontMetrics.getAscent(); // 垂直位置
        for (char c : text.toCharArray()) {
            graphics2D.drawString(String.valueOf(c), x, y);
            x += fontMetrics.charWidth(c) + letterSpacing;
        }

        // 释放资源
        graphics2D.dispose();

        // 设置输出尺寸
        dimensions[0] = totalTextWidth;
        dimensions[1] = textHeight;

        // 返回图像字节数组
        byte[] pixels = ((DataBufferByte) textImage.getRaster().getDataBuffer()).getData();
        textImage.flush();
        return pixels;
    }


    public DynamicResource getResource(String key, Supplier<NativeImage> supplier, DefaultRenderingColor defaultRenderingColor) {
        resourceRegistryQueue.process(Runnable::run);
        final DynamicResource dynamicResource = dynamicResources.get(key);

        if (dynamicResource != null && !dynamicResource.needsRefresh) {
            dynamicResource.expiryTime = System.currentTimeMillis() + COOL_DOWN_TIME;
            return dynamicResource;
        }

        if (generatingResources.contains(key)) {
            return defaultRenderingColor.dynamicResource;
        }

        MainRenderer.WORKER_THREAD.scheduleDynamicTextures(() -> {
            FontList.instance.FlonList();
            final NativeImage nativeImage = supplier.get();
            resourceRegistryQueue.put(() -> {
                final DynamicResource staticTextureProviderOld = dynamicResources.get(key);
                if (staticTextureProviderOld != null) {
                    staticTextureProviderOld.remove();
                    deletedResources.put(staticTextureProviderOld.identifier, System.currentTimeMillis() + COOL_DOWN_TIME);

                }

                final DynamicResource dynamicResourceNew;
                if (nativeImage != null) {
                    final NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(nativeImage);
                    final Identifier identifier = new Identifier(Init.MOD_ID, "id_" + Utilities.numberToPaddedHexString(new Random().nextLong()).toLowerCase(Locale.ENGLISH));
                    MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new AbstractTexture(nativeImageBackedTexture.data));
                    dynamicResourceNew = new DynamicResource(identifier, nativeImageBackedTexture);
                    dynamicResources.put(key, dynamicResourceNew);
                }
                generatingResources.remove(key);
            });
        });
        YteRouteMapGenerator.setConstants();
        generatingResources.add(key);

        if (dynamicResource == null) {
            return defaultRenderingColor.dynamicResource;
        } else {
            dynamicResource.expiryTime = System.currentTimeMillis() + COOL_DOWN_TIME;
            dynamicResource.needsRefresh = false;
            return dynamicResource;
        }
    }

    public enum DefaultRenderingColor {
        BLACK(DEFAULT_BLACK_RESOURCE),
        WHITE(DEFAULT_WHITE_RESOURCE),


        TRANSPARENT(DEFAULT_TRANSPARENT_RESOURCE);

        private final DynamicResource dynamicResource;

        DefaultRenderingColor(Identifier identifier) {
            dynamicResource = new DynamicResource(identifier, null);
        }
    }

    public static class DynamicResource {

        public final int width;
        public final int height;
        public final Identifier identifier;
        private long expiryTime;
        private boolean needsRefresh;

        private DynamicResource(Identifier identifier, @Nullable NativeImageBackedTexture nativeImageBackedTexture) {
            this.identifier = identifier;
            if (nativeImageBackedTexture != null) {
                final NativeImage nativeImage = nativeImageBackedTexture.getImage();
                if (nativeImage != null) {
                    width = nativeImage.getWidth();
                    height = nativeImage.getHeight();
                } else {
                    width = 16;
                    height = 16;
                }
            } else {
                width = 16;
                height = 16;
            }
        }


        private void remove() {
            MinecraftClient.getInstance().getTextureManager().destroyTexture(identifier);
            MainRenderer.cancelRender(identifier);
        }
    }


}

