package top.xfunny.mod.client.resource;

import top.xfunny.mod.client.DynamicTextureCache;
import top.xfunny.mod.client.YteRouteMapGenerator;

import java.awt.*;


public class TextureList {
    public static TextureList instance = new TextureList();


    public DynamicTextureCache.DynamicResource renderFont(String id, String originalText, int textColor, Font font, float fontSize, int letterSpacing) {
        return DynamicTextureCache.instance.getResource(String.format("%s_%s", id, originalText), () -> YteRouteMapGenerator.generateImage(originalText, textColor, font, fontSize, 0, letterSpacing), DynamicTextureCache.DefaultRenderingColor.TRANSPARENT);
    }

    public DynamicTextureCache.DynamicResource getTestLiftButtonsDisplay(String originalText, int textColor) {
        return DynamicTextureCache.instance.getResource(String.format("test_lift_buttons_display_%s", originalText), () -> YteRouteMapGenerator.generateImage(originalText, textColor, FontList.instance.getFont("testfont"), 4, 0, 4), DynamicTextureCache.DefaultRenderingColor.BLACK);
    }

    public DynamicTextureCache.DynamicResource getTestLiftPanelDisplay(String originalText, int textColor) {
        return DynamicTextureCache.instance.getResource(String.format("test_lift_panel_display_%s", originalText), () -> YteRouteMapGenerator.generateImage(originalText, textColor, FontList.instance.getFont("testfont"), 4, 0, 4), DynamicTextureCache.DefaultRenderingColor.BLACK);
    }
}
