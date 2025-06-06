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
            final int totalWidth;
            final int height = Math.round(scale * 1.5F);
            final int[] dimensions = new int[2];
            final byte[] pixels = DynamicTextureCache.instance.getTextPixels(text.toUpperCase(Locale.ENGLISH),
                    dimensions,
                    90,
                    fontSizeSmall * fontSize,
                    padding,
                    font,
                    letterSpacing);

            totalWidth = DynamicTextureCache.instance.totalWidth;


            final NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), totalWidth, height, true);
            nativeImage.fillRect(0, 0, totalWidth, height, 0);
            YteRouteMapGenerator.drawString(nativeImage, pixels, totalWidth / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0x00000000, textColor, false);
            //YteRouteMapGenerator.clearColor(nativeImage, YteRouteMapGenerator.invertColor(ARGB_BLACK), 19);


            //top.xfunny.mod.util.ImageGenerator.saveNativeImageAsPng(nativeImage, "output_image.png");
            return nativeImage;
        } catch (Exception e) {

        }
        return null;
    }

    public static void setConstants() {
        scale = (int) Math.pow(2, Config.getClient().getDynamicTextureResolution() + 5);
        lineSize = scale / 8;
        fontSizeBig = lineSize * 2;
        fontSizeSmall = fontSizeBig / 2;
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
            // 1. 读取ABGR格式的像素并正确解析通道
            final int existingPixel = nativeImage.getColor(x, y);
            final int a1 = (existingPixel >> 24) & 0xFF;
            final int r1 = (existingPixel) & 0xFF;       // ABGR中的R分量（实际是存储的B分量）
            final int g1 = (existingPixel >> 8) & 0xFF;  // G分量
            final int b1 = (existingPixel >> 16) & 0xFF; // ABGR中的B分量（实际是存储的R分量）

            // 2. 正确解析文本颜色(ARGB格式)
            final int a2 = (color >>> 24) & 0xFF;
            final int r2 = (color >> 16) & 0xFF; // ARGB中的R分量
            final int g2 = (color >> 8) & 0xFF;  // G分量
            final int b2 = color & 0xFF;         // B分量

            // 3. 透明背景特殊处理：当背景透明时，RGB设为0而不是255
            if (a1 == 0) {
                // 使用标准混合公式计算新颜色
                if (a2 > 0) {
                    final float alpha = a2 / 255.0f;
                    final int r = (int) (r2 * alpha);
                    final int g = (int) (g2 * alpha);
                    final int b = (int) (b2 * alpha);

                    // 注意：这里直接使用原色值，因为背景透明不需要混合
                    final int finalColor = (a2 << 24) | (b << 16) | (g << 8) | r; // 转换为ABGR格式
                    nativeImage.setPixelColor(x, y, finalColor);
                }
            } else {
                // 4. 非透明背景使用正确混合公式
                final float alpha1 = a1 / 255.0f;
                final float alpha2 = a2 / 255.0f;
                final float outAlpha = alpha1 + alpha2 * (1 - alpha1);

                if (outAlpha > 0) {
                    final float factor = alpha2 * (1 - alpha1) / outAlpha;
                    final int r = (int) ((r1 * (1 - factor)) + (r2 * factor));
                    final int g = (int) ((g1 * (1 - factor)) + (g2 * factor));
                    final int b = (int) ((b1 * (1 - factor)) + (b2 * factor));
                    final int a = (int) (outAlpha * 255);

                    // 转换为ABGR格式存储
                    final int finalColor = (a << 24) | (b << 16) | (g << 8) | r;
                    nativeImage.setPixelColor(x, y, finalColor);
                }
            }
        }
    }


    private static void drawPixelSafe(NativeImage nativeImage, int x, int y, int color) {
        if (Utilities.isBetween(x, 0, nativeImage.getWidth() - 1) && Utilities.isBetween(y, 0, nativeImage.getHeight() - 1)) {
            // 6. 直接写入颜色（不再调用invertColor）
            nativeImage.setPixelColor(x, y, color);
        }
    }

}

