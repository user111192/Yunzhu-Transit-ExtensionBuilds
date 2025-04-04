package top.xfunny.mod.client.resource;

import top.xfunny.mod.client.TextureCache;
import top.xfunny.mod.client.YteRouteMapGenerator;

import java.awt.*;


public class TextureList {
    public static TextureList instance = new TextureList();


    public TextureCache.DynamicResource renderFont(String id, String originalText, int textColor, Font font, int fontSize, int letterSpacing) {
        return TextureCache.instance.getResource(String.format("%s_%s", id, originalText), () -> YteRouteMapGenerator.generateImage(originalText, textColor, font, fontSize, 0, letterSpacing), TextureCache.DefaultRenderingColor.BLACK);
    }

    public TextureCache.DynamicResource getTestLiftButtonsDisplay(String originalText, int textColor) {
        return TextureCache.instance.getResource(String.format("test_lift_buttons_display_%s", originalText), () -> YteRouteMapGenerator.generateImage(originalText, textColor, FontList.instance.getFont("testfont"), 4, 0, 4), TextureCache.DefaultRenderingColor.BLACK);
    }

    public TextureCache.DynamicResource getTestLiftPanelDisplay(String originalText, int textColor) {
        return TextureCache.instance.getResource(String.format("test_lift_panel_display_%s", originalText), () -> YteRouteMapGenerator.generateImage(originalText, textColor, FontList.instance.getFont("testfont"), 4, 0, 4), TextureCache.DefaultRenderingColor.BLACK);
    }
}
