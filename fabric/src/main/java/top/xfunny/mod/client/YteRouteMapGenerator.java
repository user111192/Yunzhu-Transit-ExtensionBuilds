package top.xfunny.mod.client;

import org.mtr.core.tool.Utilities;
import org.mtr.mapping.holder.NativeImage;
import org.mtr.mapping.holder.NativeImageFormat;
import org.mtr.mod.config.Config;
import org.mtr.mod.data.IGui;

import java.awt.*;
import java.util.Locale;

public class YteRouteMapGenerator implements IGui {
    private static int scale;
    private static int lineSize;
    private static int fontSizeBig;
    private static int fontSizeSmall;

    public static NativeImage generateImage(String text, int textColor, Font font, float fontSize, int padding, int letterSpacing) {
        setConstants();
        try {
            final int[] dimensions = new int[2];
            final byte[] pixels = DynamicTextureCache.instance.getTextPixels(
                    text.toUpperCase(Locale.ENGLISH),
                    dimensions,
                    90,
                    fontSizeSmall * fontSize,
                    padding,
                    font,
                    letterSpacing
            );

            final int totalWidth = dimensions[0];
            final int height = Math.round(scale * 1.5F);

            final NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), totalWidth, height, true);
            nativeImage.fillRect(0, 0, totalWidth, height, 0);

            // 使用优化的绘制方法
            drawStringOptimized(nativeImage, pixels, totalWidth / 2, height / 2, dimensions,
                    HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
                    0x00000000, textColor, false);

            return nativeImage;
        } catch (Exception e) {
            // 记录错误但不抛出异常
        }
        return null;
    }

    public static void setConstants() {
        scale = (int) Math.pow(2, Config.getClient().getDynamicTextureResolution() + 5);
        lineSize = scale / 8;
        fontSizeBig = lineSize * 2;
        fontSizeSmall = fontSizeBig / 2;
    }

    // 优化的绘制方法
    private static void drawStringOptimized(NativeImage nativeImage, byte[] pixels, int x, int y, int[] textDimensions,
                                            HorizontalAlignment horizontalAlignment,
                                            VerticalAlignment verticalAlignment,
                                            int backgroundColor, int textColor, boolean rotate90) {
        // 提前计算颜色分量
        final int textR = (textColor >> 16) & 0xFF;
        final int textG = (textColor >> 8) & 0xFF;
        final int textB = textColor & 0xFF;

        // 背景不透明时绘制背景
        if (((backgroundColor >> 24) & 0xFF) > 0) {
            for (int drawY = 0; drawY < textDimensions[rotate90 ? 0 : 1]; drawY++) {
                for (int drawX = 0; drawX < textDimensions[rotate90 ? 1 : 0]; drawX++) {
                    drawPixelSafe(nativeImage,
                            (int) horizontalAlignment.getOffset(drawX + x, textDimensions[rotate90 ? 1 : 0]),
                            (int) verticalAlignment.getOffset(drawY + y, textDimensions[rotate90 ? 0 : 1]),
                            backgroundColor);
                }
            }
        }

        // 优化像素绘制循环
        int drawX = 0;
        int drawY = rotate90 ? textDimensions[0] - 1 : 0;

        for (int i = 0; i < textDimensions[0] * textDimensions[1]; i++) {
            final int alpha = pixels[i] & 0xFF;
            if (alpha > 0) { // 只处理非透明像素
                final int xPos = (int) horizontalAlignment.getOffset(x + drawX, textDimensions[rotate90 ? 1 : 0]);
                final int yPos = (int) verticalAlignment.getOffset(y + drawY, textDimensions[rotate90 ? 0 : 1]);

                if (Utilities.isBetween(xPos, 0, nativeImage.getWidth() - 1) &&
                        Utilities.isBetween(yPos, 0, nativeImage.getHeight() - 1)) {

                    final int existingPixel = nativeImage.getColor(xPos, yPos);
                    final int existingA = (existingPixel >> 24) & 0xFF;

                    if (existingA == 0) {
                        // 背景完全透明，直接设置颜色
                        final int newColor = (alpha << 24) | (textB << 16) | (textG << 8) | textR;
                        nativeImage.setPixelColor(xPos, yPos, newColor);
                    } else {
                        // 需要混合时使用优化后的混合算法
                        blendPixelOptimized(nativeImage, xPos, yPos, alpha, textR, textG, textB);
                    }
                }
            }

            // 更新坐标
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

    // 优化的混合算法
    private static void blendPixelOptimized(NativeImage nativeImage, int x, int y, int alpha, int r, int g, int b) {
        final int existingPixel = nativeImage.getColor(x, y);
        final int existingA = (existingPixel >> 24) & 0xFF;
        final int existingR = existingPixel & 0xFF;
        final int existingG = (existingPixel >> 8) & 0xFF;
        final int existingB = (existingPixel >> 16) & 0xFF;

        // 使用整数运算替代浮点运算
        final int invAlpha = 255 - alpha;
        final int newR = (existingR * invAlpha + r * alpha) / 255;
        final int newG = (existingG * invAlpha + g * alpha) / 255;
        final int newB = (existingB * invAlpha + b * alpha) / 255;
        final int newA = Math.min(255, existingA + alpha);

        final int finalColor = (newA << 24) | (newB << 16) | (newG << 8) | newR;
        nativeImage.setPixelColor(x, y, finalColor);
    }

    private static void drawPixelSafe(NativeImage nativeImage, int x, int y, int color) {
        if (Utilities.isBetween(x, 0, nativeImage.getWidth() - 1) && Utilities.isBetween(y, 0, nativeImage.getHeight() - 1)) {
            nativeImage.setPixelColor(x, y, color);
        }
    }
}