package top.xfunny;


import org.mtr.core.tool.Utilities;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.*;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.MainRenderer;
import top.xfunny.resource.FontList;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;
import java.util.List;

import static org.mtr.mod.data.IGui.ARGB_BLACK;


public class TextureCache {
	public static TextureCache instance = new TextureCache();
	public int totalWidth;
	private final Object2ObjectLinkedOpenHashMap<String, DynamicResource> dynamicResources = new Object2ObjectLinkedOpenHashMap<>();
	private final ObjectOpenHashSet<String> generatingResources = new ObjectOpenHashSet<>();
	private final ObjectArrayList<Runnable> resourceRegistryQueue = new ObjectArrayList<>();
	private static final int COOL_DOWN_TIME = 10000;
	private static final Identifier DEFAULT_BLACK_RESOURCE = new Identifier(Init.MOD_ID, "textures/block/black.png");
	private static final Identifier DEFAULT_WHITE_RESOURCE = new Identifier(Init.MOD_ID, "textures/block/white.png");
	private static final Identifier DEFAULT_TRANSPARENT_RESOURCE = new Identifier(Init.MOD_ID, "textures/block/transparent.png");




	public void reload() {
		FontList.instance.FontReload();
		Init.LOGGER.debug("Refreshing dynamic resources");
		dynamicResources.values().forEach(dynamicResource -> dynamicResource.needsRefresh = true);
		generatingResources.clear();
	}

	public byte[] getTextPixels(String text, int[] dimensions, int maxWidth, int maxHeight, int fontSizeCjk, int fontSize, int padding, Font font, Font fontCjk) {
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

        // 计算整个文本的宽度和高度
        Rectangle2D textBounds = selectedFont.getStringBounds(text, context);
        int textWidth = (int) textBounds.getWidth() + 2 * padding;
        int textHeight = (int) textBounds.getHeight() + 2 * padding;
		totalWidth = textWidth;
		Init.LOGGER.info("Text dimensions: " + textWidth + "x" + textHeight);

        // 创建灰度图像
        BufferedImage textImage = new BufferedImage(textWidth, textHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics2D = textImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 设置字体和颜色
        graphics2D.setFont(selectedFont);
        graphics2D.setColor(Color.WHITE);

        // 绘制文本
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int ascent = fontMetrics.getAscent();
        int horizontalOffset = (textWidth - (int) textBounds.getWidth()) / 2;
        int verticalOffset = textHeight - padding - (textHeight - ascent) / 2;

        graphics2D.drawString(text, horizontalOffset, verticalOffset);

        // 释放资源
        graphics2D.dispose();

        // 设置输出尺寸
        dimensions[0] = textWidth;
        dimensions[1] = textHeight;

        // 返回图像字节数组
        byte[] pixels = ((DataBufferByte) textImage.getRaster().getDataBuffer()).getData();
        textImage.flush();
        return pixels;
    }

	public DynamicResource getResource(String key, Supplier<NativeImage> supplier, DefaultRenderingColor defaultRenderingColor) {
		if (!resourceRegistryQueue.isEmpty()) {
			final Runnable runnable = resourceRegistryQueue.remove(0);
			if (runnable != null) {
				runnable.run();
			}
		}

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
			resourceRegistryQueue.add(() -> {
				final DynamicResource staticTextureProviderOld = dynamicResources.get(key);
				if (staticTextureProviderOld != null) {
					staticTextureProviderOld.remove();
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

		private long expiryTime;
		private boolean needsRefresh;
		public final int width;
		public final int height;
		public final Identifier identifier;

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
				height = 16 ;

			}
		}


		private void remove() {
			MinecraftClient.getInstance().getTextureManager().destroyTexture(identifier);
			MainRenderer.cancelRender(identifier);
		}
	}






}

