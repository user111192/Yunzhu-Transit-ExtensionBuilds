package top.xfunny.resource;

import org.mtr.mapping.holder.NativeImage;
import org.mtr.mapping.holder.NativeImageFormat;
import org.mtr.mod.config.Config;
import org.mtr.mod.data.IGui;
import top.xfunny.Init;
import top.xfunny.TextureCache;
import top.xfunny.YteRouteMapGenerator;

import java.util.Locale;

public class RouteMapList implements IGui {
    private static int scale;
    private static int lineSize;
    private static int fontSizeBig;
    private static int fontSizeSmall;


    public static NativeImage generateTestLiftButtons(String text, int textColor) {
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
                    height,
                    fontSizeSmall*4 ,
                    fontSizeSmall*10 ,
                    0,
                    FontList.instance.koneModernization,
                    FontList.instance.fontCjk1);//fontsize：字体大小
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
            //top.xfunny.Util.img.saveNativeImageAsPng(nativeImage, "output_image.png");
            return nativeImage;
        } catch (Exception e) {
            Init.LOGGER.error("贴图生成失败");
            Init.LOGGER.error("", e);
        }
        return null;
    }

    public static NativeImage generateTestLiftPanel(String text, int textColor) {
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
                    height,
                    fontSizeSmall*4 ,
                    fontSizeSmall*10 ,
                    0,
                    FontList.instance.testfont,
                    FontList.instance.fontCjk1);//fontsize：字体大小

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
            //top.xfunny.Util.img.saveNativeImageAsPng(nativeImage, "output_image.png");
            return nativeImage;
        } catch (Exception e) {
            Init.LOGGER.error("贴图生成失败");
            Init.LOGGER.error("", e);
        }
        return null;
    }

}
