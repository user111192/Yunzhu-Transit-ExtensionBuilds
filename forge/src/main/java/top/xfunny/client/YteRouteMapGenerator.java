package top.xfunny.client;

import org.mtr.core.tool.Utilities;
import org.mtr.mapping.holder.NativeImage;
import org.mtr.mapping.holder.NativeImageFormat;
import org.mtr.mod.config.Config;
import org.mtr.mod.data.IGui;
import top.xfunny.Init;

import java.awt.*;
import java.util.Locale;


public class YteRouteMapGenerator implements IGui {
    private static int scale;
    private static int lineSize;
    private static int fontSizeBig;
    private static int fontSizeSmall;

    public static NativeImage generateImage(String text, int textColor, Font font, int fontSize, int padding, int letterSpacing) {
        setConstants();
        try {
            Init.LOGGER.info("贴图生成中");
            final int totalWidth;
            final int height = Math.round(scale * 1.5F);
            final int[] dimensions = new int[2];
            final byte[] pixels = TextureCache.instance.getTextPixels(text.toUpperCase(Locale.ENGLISH),
                    dimensions,
                    90,
                    fontSizeSmall * fontSize,
                    padding,
                    font,
                    letterSpacing);

            totalWidth = TextureCache.instance.totalWidth;


            Init.LOGGER.info("scale:" + scale);
            Init.LOGGER.info("width" + totalWidth + "|height" + height);
            final NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), totalWidth, height, false);
            nativeImage.fillRect(0, 0, totalWidth, height, 0);
            YteRouteMapGenerator.drawString(nativeImage, pixels, totalWidth / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, ARGB_BLACK, textColor, false);
            YteRouteMapGenerator.clearColor(nativeImage, YteRouteMapGenerator.invertColor(ARGB_BLACK), 19);

            Init.LOGGER.info("nativeImageWidth" + nativeImage.getWidth());
            //top.xfunny.util.ImageGenerator.saveNativeImageAsPng(nativeImage, "output_image.png");
            return nativeImage;
        } catch (Exception e) {
            Init.LOGGER.error("贴图生成失败");
            Init.LOGGER.error("", e);
        }
        return null;
    }

    public static void setConstants() {
        scale = (int) Math.pow(2, Config.getClient().getDynamicTextureResolution() + 5);
        lineSize = scale / 8;
        fontSizeBig = lineSize * 2;
        fontSizeSmall = fontSizeBig / 2;
    }

    public static void clearColor(NativeImage nativeImage, int targetColor, int tolerance) {
        int width = nativeImage.getWidth();
        int height = nativeImage.getHeight();

        int targetR = (targetColor >> 16) & 0xFF;
        int targetG = (targetColor >> 8) & 0xFF;
        int targetB = targetColor & 0xFF;
        int targetA = (targetColor >> 24) & 0xFF;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int currentColor = nativeImage.getColor(x, y);

                int currentR = (currentColor >> 16) & 0xFF;
                int currentG = (currentColor >> 8) & 0xFF;
                int currentB = currentColor & 0xFF;
                int currentA = (currentColor >> 24) & 0xFF;

                int deltaR = Math.abs(currentR - targetR);
                int deltaG = Math.abs(currentG - targetG);
                int deltaB = Math.abs(currentB - targetB);
                int deltaA = Math.abs(currentA - targetA);

                if (deltaR <= tolerance && deltaG <= tolerance && deltaB <= tolerance && deltaA <= tolerance) {
                    // 将目标像素直接设置为完全透明
                    nativeImage.setPixelColor(x, y, 0);
                } else {
                    // 对非匹配区域应用 FXAA 抗锯齿处理
                    int blurredColor = applyFXAA(nativeImage, x, y);
                    nativeImage.setPixelColor(x, y, blurredColor);
                }
            }
        }
    }

    private static int applyFXAA(NativeImage image, int x, int y) {
        int width = image.getWidth();
        int height = image.getHeight();

        // 当前像素及其四周像素的颜色值
        int colorCenter = image.getColor(x, y);
        int colorLeft = x > 0 ? image.getColor(x - 1, y) : colorCenter;
        int colorRight = x < width - 1 ? image.getColor(x + 1, y) : colorCenter;
        int colorUp = y > 0 ? image.getColor(x, y - 1) : colorCenter;
        int colorDown = y < height - 1 ? image.getColor(x, y + 1) : colorCenter;

        // 检查透明度是否为 0（完全透明）
        if (((colorCenter >> 24) & 0xFF) == 0) {
            return 0; // 保持完全透明
        }

        // 计算加权平均颜色
        int blendedR = (
                ((colorLeft >> 16) & 0xFF) +
                        ((colorRight >> 16) & 0xFF) +
                        ((colorUp >> 16) & 0xFF) +
                        ((colorDown >> 16) & 0xFF) +
                        ((colorCenter >> 16) & 0xFF)
        ) / 5;

        int blendedG = (
                ((colorLeft >> 8) & 0xFF) +
                        ((colorRight >> 8) & 0xFF) +
                        ((colorUp >> 8) & 0xFF) +
                        ((colorDown >> 8) & 0xFF) +
                        ((colorCenter >> 8) & 0xFF)
        ) / 5;

        int blendedB = (
                (colorLeft & 0xFF) +
                        (colorRight & 0xFF) +
                        (colorUp & 0xFF) +
                        (colorDown & 0xFF) +
                        (colorCenter & 0xFF)
        ) / 5;

        int blendedA = (
                ((colorLeft >> 24) & 0xFF) +
                        ((colorRight >> 24) & 0xFF) +
                        ((colorUp >> 24) & 0xFF) +
                        ((colorDown >> 24) & 0xFF) +
                        ((colorCenter >> 24) & 0xFF)
        ) / 5;

        // 返回模糊后的颜色
        return (blendedA << 24) | (blendedR << 16) | (blendedG << 8) | blendedB;
    }


    public static void drawString(NativeImage nativeImage, byte[] pixels, int x, int y, int[] textDimensions, IGui.HorizontalAlignment horizontalAlignment, IGui.VerticalAlignment verticalAlignment, int backgroundColor, int textColor, boolean rotate90) {
        if (((backgroundColor >> 24) & 0xFF) > 0) {
            for (int drawX = 0; drawX < textDimensions[rotate90 ? 1 : 0]; drawX++) {
                for (int drawY = 0; drawY < textDimensions[rotate90 ? 0 : 1]; drawY++) {
                    drawPixelSafe(nativeImage, (int) horizontalAlignment.getOffset(drawX + x, textDimensions[rotate90 ? 1 : 0]), (int) verticalAlignment.getOffset(drawY + y, textDimensions[rotate90 ? 0 : 1]), backgroundColor);
                }
            }
        }
        int drawX = 0;
        int drawY = rotate90 ? textDimensions[0] - 1 : 0;
        for (int i = 0; i < textDimensions[0] * textDimensions[1]; i++) {
            blendPixel(nativeImage, (int) horizontalAlignment.getOffset(x + drawX, textDimensions[rotate90 ? 1 : 0]), (int) verticalAlignment.getOffset(y + drawY, textDimensions[rotate90 ? 0 : 1]), ((pixels[i] & 0xFF) << 24) + (textColor & RGB_WHITE));
            if (rotate90) {
                drawY--;
                if (drawY < 0) {
                    drawY = textDimensions[0] - 1;
                    drawX++;
                }
            } else {
                drawX++;
                if (drawX == textDimensions[0]) {
                    drawX = 0;
                    drawY++;
                }
            }
        }
    }

    private static void blendPixel(NativeImage nativeImage, int x, int y, int color) {
        if (Utilities.isBetween(x, 0, nativeImage.getWidth() - 1) && Utilities.isBetween(y, 0, nativeImage.getHeight() - 1)) {
            final float percent = (float) ((color >> 24) & 0xFF) / 0xFF;
            if (percent > 0) {
                final int existingPixel = nativeImage.getColor(x, y);
                final boolean existingTransparent = ((existingPixel >> 24) & 0xFF) == 0;
                final int r1 = existingTransparent ? 0xFF : (existingPixel & 0xFF);
                final int g1 = existingTransparent ? 0xFF : ((existingPixel >> 8) & 0xFF);
                final int b1 = existingTransparent ? 0xFF : ((existingPixel >> 16) & 0xFF);
                final int r2 = (color >> 16) & 0xFF;
                final int g2 = (color >> 8) & 0xFF;
                final int b2 = color & 0xFF;
                final float inversePercent = 1 - percent;
                final int finalColor = ARGB_BLACK | (((int) (r1 * inversePercent + r2 * percent) << 16) + ((int) (g1 * inversePercent + g2 * percent) << 8) + (int) (b1 * inversePercent + b2 * percent));
                drawPixelSafe(nativeImage, x, y, finalColor);
            }
        }
    }

    private static void drawPixelSafe(NativeImage nativeImage, int x, int y, int color) {
        if (Utilities.isBetween(x, 0, nativeImage.getWidth() - 1) && Utilities.isBetween(y, 0, nativeImage.getHeight() - 1)) {
            nativeImage.setPixelColor(x, y, invertColor(color));
        }
    }

    public static int invertColor(int color) {
        return ((color & ARGB_BLACK) != 0 ? ARGB_BLACK : 0) + ((color & 0xFF) << 16) + (color & 0xFF00) + ((color & 0xFF0000) >> 16);
    }


}

