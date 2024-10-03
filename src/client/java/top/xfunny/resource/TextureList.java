package top.xfunny.resource;

import top.xfunny.TextureCache;
import top.xfunny.YteRouteMapGenerator;


public class TextureList {
	public static TextureList instance = new TextureList();
	public TextureCache.DynamicResource getTestLiftButtonsDisplay(String originalText, int textColor) {
		return TextureCache.instance.getResource(String.format("test_lift_buttons_display_%s", originalText), () -> YteRouteMapGenerator.generateImage(originalText, textColor,FontList.instance.testfont,4,0), TextureCache.DefaultRenderingColor.BLACK);
	}

	public TextureCache.DynamicResource getTestLiftPanelDisplay(String originalText, int textColor) {
		return TextureCache.instance.getResource(String.format("test_lift_panel_display_%s", originalText), () -> YteRouteMapGenerator.generateImage(originalText, textColor,FontList.instance.testfont,4,0), TextureCache.DefaultRenderingColor.BLACK);
	}

	public TextureCache.DynamicResource getOtisSeries1ScreenDisplay(String originalText, int textColor) {
		return TextureCache.instance.getResource(String.format("otis_series1_screen_display_%s", originalText), () -> YteRouteMapGenerator.generateImage(originalText, textColor,FontList.instance.koneModernization,8,0), TextureCache.DefaultRenderingColor.BLACK);
	}

}
