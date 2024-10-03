package top.xfunny;

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

	public static NativeImage generateImage(String text, int textColor, Font font,int fontSize,int padding) {
		scale = (int) Math.pow(2, Config.getClient().getDynamicTextureResolution() + 5);
        lineSize = scale / 8;
        fontSizeBig = lineSize * 2;
        fontSizeSmall = fontSizeBig / 2;
        try {
            Init.LOGGER.info("贴图生成中");
            final int totalWidth;
            final int height = Math.round(scale * 1.5F);
            final int[] dimensions = new int[2];
            final byte[] pixels = TextureCache.instance.getTextPixels(text.toUpperCase(Locale.ENGLISH),
                    dimensions,
                    90,
                    fontSizeSmall*fontSize,
                    padding,
                    font);
            if (TextureCache.instance.totalWidth > scale * 1.5F) {
                totalWidth = TextureCache.instance.totalWidth;
                Init.LOGGER.info("超出范围width"+totalWidth);
            } else {
                totalWidth = Math.round(scale * 1.5F);
                Init.LOGGER.info("未超出范围width"+totalWidth+"TextureCache.instance.totalWidth:"+TextureCache.instance.totalWidth);
            }
            Init.LOGGER.info("scale:"+scale);
            Init.LOGGER.info("width"+totalWidth+"|height"+height);
            final NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), totalWidth, height, false);
            nativeImage.fillRect(0, 0, totalWidth, height, 0);
            YteRouteMapGenerator.drawString(nativeImage, pixels, totalWidth / 2, height / 2 , dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, ARGB_BLACK, textColor, false);
            YteRouteMapGenerator.clearColor(nativeImage, YteRouteMapGenerator.invertColor(ARGB_BLACK));
            Init.LOGGER.info("nativeImageWidth"+nativeImage.getWidth());
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
	public static void clearColor(NativeImage nativeImage, int color) {
		for (int x = 0; x < nativeImage.getWidth(); x++) {
			for (int y = 0; y < nativeImage.getHeight(); y++) {
				if (nativeImage.getColor(x, y) == color) {
					nativeImage.setPixelColor(x, y, 0);
				}
			}
		}
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

