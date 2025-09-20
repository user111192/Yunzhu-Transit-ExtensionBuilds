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

    // 添加字符级别缓存
    private final Object2ObjectLinkedOpenHashMap<String, NativeImage> charImageCache = new Object2ObjectLinkedOpenHashMap<>();
    private final Object2ObjectLinkedOpenHashMap<String, int[]> charDimensionCache = new Object2ObjectLinkedOpenHashMap<>();

    // 最近的贴图缓存：最多保留两个 id
    // 外层：id
    // 内层：每个 id 对应一个 LRU Map，最多缓存 2 个 originalText
    private final Object2ObjectLinkedOpenHashMap<String, Object2ObjectLinkedOpenHashMap<String, DynamicResource>> lastResourceById = new Object2ObjectLinkedOpenHashMap<>();

    public int totalWidth;

    public void reload() {
        FontList.instance.FlonList();
        FontList.instance.FontReload();
        dynamicResources.values().forEach(dynamicResource -> dynamicResource.needsRefresh = true);
        generatingResources.clear();

        // 清空字符缓存
        charImageCache.clear();
        charDimensionCache.clear();

        // 清空 lastResourceById
        lastResourceById.clear();
    }

    public void tick() {
        final ObjectArrayList<String> keysToRemove = new ObjectArrayList<>();
        dynamicResources.forEach((checkKey, checkDynamicResource) -> {// 检查每个资源，如果过期则删除
            String idOnly = checkKey.contains("$") ? checkKey.substring(0, checkKey.indexOf("$")) : checkKey;
            String originalText = checkKey.contains("$") ? checkKey.substring(checkKey.indexOf("$") + 1) : checkKey;

            // 如果在缓存中存在originalText，则不删除资源
            if (lastResourceById.containsKey(idOnly)) {
                Object2ObjectLinkedOpenHashMap<String, DynamicResource> pool = lastResourceById.get(idOnly);
                if (pool.containsKey(originalText)) {
                    return;
                }
            }

            if (checkDynamicResource.expiryTime < System.currentTimeMillis()) {
                checkDynamicResource.remove();
                deletedResources.put(checkDynamicResource.identifier, System.currentTimeMillis() + COOL_DOWN_TIME);
                keysToRemove.add(checkKey);
            }
        });
        keysToRemove.forEach(dynamicResources::remove);

        final ObjectArrayList<Identifier> deletedResourcesToRemove = new ObjectArrayList<>();
        deletedResources.forEach((identifier, expiryTime) -> {// 检查每个资源，如果过期则删除
            if (expiryTime < System.currentTimeMillis()) {
                MinecraftClient.getInstance().getTextureManager().destroyTexture(identifier);
                deletedResourcesToRemove.add(identifier);
            }
        });
        deletedResourcesToRemove.forEach(deletedResources::removeLong);// 删除

        // 清理字符缓存
        if (charImageCache.size() > 1000) {
            charImageCache.remove(charImageCache.firstKey());
            charDimensionCache.remove(charDimensionCache.firstKey());
        }
    }

    public byte[] getTextPixels(String text, int[] dimensions, int maxWidth, float fontSize, int padding, Font font, int letterSpacing) {
        if (maxWidth <= 0) {
            dimensions[0] = 0;
            dimensions[1] = 0;
            return new byte[0];
        }
        totalWidth = 0;

        // 尝试从缓存获取字符图像
        if (letterSpacing == 0 && text.length() == 1) {
            String cacheKey = font.getName() + "_" + fontSize + "_" + text;
            NativeImage cachedImage = charImageCache.get(cacheKey);
            int[] cachedDims = charDimensionCache.get(cacheKey);

            if (cachedImage != null && cachedDims != null) {
                dimensions[0] = cachedDims[0];
                dimensions[1] = cachedDims[1];
                return getPixelsFromNativeImage(cachedImage);
            }
        }

        try {
            BufferedImage textImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2d = textImage.createGraphics();

            Font renderFont = font.deriveFont(Font.PLAIN, fontSize);
            g2d.setFont(renderFont);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = 0;
            if (letterSpacing == 0) {
                textWidth = metrics.stringWidth(text);
            } else {
                for (char c : text.toCharArray()) {
                    textWidth += metrics.charWidth(c) + letterSpacing;
                }
                textWidth -= letterSpacing;
            }

            int textHeight = metrics.getHeight();
            int totalWidth = textWidth + 2 * padding;
            int totalHeight = textHeight + 2 * padding;

            dimensions[0] = totalWidth;
            dimensions[1] = totalHeight;

            textImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_BYTE_GRAY);
            g2d = textImage.createGraphics();
            g2d.setFont(renderFont);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setColor(Color.WHITE);

            int x = padding;
            int y = padding + metrics.getAscent();

            if (letterSpacing == 0) {
                g2d.drawString(text, x, y);
            } else {
                for (char c : text.toCharArray()) {
                    g2d.drawString(String.valueOf(c), x, y);
                    x += metrics.charWidth(c) + letterSpacing;
                }
            }

            g2d.dispose();

            byte[] pixels = ((DataBufferByte) textImage.getRaster().getDataBuffer()).getData();
            textImage.flush();

            if (letterSpacing == 0 && text.length() == 1) {
                String cacheKey = font.getName() + "_" + fontSize + "_" + text;
                NativeImage charImage = createNativeImageFromPixels(pixels, dimensions[0], dimensions[1]);
                charImageCache.put(cacheKey, charImage);
                charDimensionCache.put(cacheKey, new int[]{dimensions[0], dimensions[1]});
            }

            return pixels;
        } catch (Exception e) {
            dimensions[0] = 0;
            dimensions[1] = 0;
            return new byte[0];
        }
    }

    private byte[] getPixelsFromNativeImage(NativeImage nativeImage) {
        byte[] pixels = new byte[nativeImage.getWidth() * nativeImage.getHeight()];
        for (int y = 0; y < nativeImage.getHeight(); y++) {
            for (int x = 0; x < nativeImage.getWidth(); x++) {
                int color = nativeImage.getColor(x, y);
                pixels[y * nativeImage.getWidth() + x] = (byte) ((color >> 24) & 0xFF);
            }
        }
        return pixels;
    }

    private NativeImage createNativeImageFromPixels(byte[] pixels, int width, int height) {
        try {
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, true);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int alpha = pixels[y * width + x] & 0xFF;
                    nativeImage.setPixelColor(x, y, (alpha << 24) | 0x00FFFFFF);
                }
            }
            return nativeImage;
        } catch (Exception e) {
            return null;
        }
    }


    // 只保留每个id下的两个originalText缓存
    private void putLastResource(String id, String originalText, DynamicResource resource) {
        Object2ObjectLinkedOpenHashMap<String, DynamicResource> pool =
                lastResourceById.computeIfAbsent(id, k -> new Object2ObjectLinkedOpenHashMap<>());
        pool.clear(); // 只存一个
        pool.put(originalText, resource);
    }


    public DynamicResource getResource(String key, Supplier<NativeImage> supplier, DefaultRenderingColor defaultRenderingColor) {
        resourceRegistryQueue.process(Runnable::run);
        final DynamicResource dynamicResource = dynamicResources.get(key);

        String idOnly = key.contains("$") ? key.substring(0, key.indexOf("$")) : key;
        String originalText = key.contains("$") ? key.substring(key.indexOf("$") + 1) : key;

        if (dynamicResource != null && !dynamicResource.needsRefresh) {// 已生成且不需要刷新
            dynamicResource.expiryTime = System.currentTimeMillis() + COOL_DOWN_TIME;
            //putLastResource(idOnly, originalText, dynamicResource);
            return dynamicResource;
        }

        if (generatingResources.contains(key)) {// 正在生成中
            Object2ObjectLinkedOpenHashMap<String, DynamicResource> pool = lastResourceById.get(idOnly);
            if (pool !=null && !pool.isEmpty()) {
                return pool.get(pool.lastKey());
            }
            return defaultRenderingColor.dynamicResource;
        }

        MainRenderer.WORKER_THREAD.scheduleDynamicTextures(() -> {
            final NativeImage nativeImage = supplier.get();
            resourceRegistryQueue.put(() -> {
                final DynamicResource staticTextureProviderOld = dynamicResources.get(key);
                if (staticTextureProviderOld != null) {
                    staticTextureProviderOld.remove();
                    deletedResources.put(staticTextureProviderOld.identifier, System.currentTimeMillis() + COOL_DOWN_TIME);
                }

                if (nativeImage != null) {
                    final NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(nativeImage);
                    final Identifier identifier = new Identifier(Init.MOD_ID, "id_" + Utilities.numberToPaddedHexString(new Random().nextLong()).toLowerCase(Locale.ENGLISH));
                    MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new AbstractTexture(nativeImageBackedTexture.data));
                    final DynamicResource dynamicResourceNew = new DynamicResource(identifier, nativeImageBackedTexture);
                    dynamicResources.put(key, dynamicResourceNew);

                    putLastResource(idOnly, originalText, dynamicResourceNew);
                }
                generatingResources.remove(key);
            });
        });

        generatingResources.add(key);

        if (dynamicResource == null) {
            Object2ObjectLinkedOpenHashMap<String, DynamicResource> pool = lastResourceById.get(idOnly);
            if (pool != null && !pool.isEmpty()) {
                return pool.get(pool.lastKey());
            }
            return defaultRenderingColor.dynamicResource;
        } else {// 现在正在显示的字符
            dynamicResource.expiryTime = System.currentTimeMillis() + COOL_DOWN_TIME;
            dynamicResource.needsRefresh = false;
            //putLastResource(idOnly, originalText, dynamicResource);
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
