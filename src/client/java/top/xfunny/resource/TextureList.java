package top.xfunny.resource;

import top.xfunny.TextureCache;


public class TextureList {
	public static TextureList instance = new TextureList();
	public TextureCache.DynamicResource getTestLiftButtonsDisplay(String originalText, int textColor) {
		return TextureCache.instance.getResource(String.format("test_lift_panel_display_%s", originalText), () -> RouteMapList.generateTestLiftButtons(originalText, textColor), TextureCache.DefaultRenderingColor.BLACK);
	}

	public TextureCache.DynamicResource getTestLiftPanelDisplay(String originalText, int textColor) {
		return TextureCache.instance.getResource(String.format("test_lift_panel_display_%s", originalText), () -> RouteMapList.generateTestLiftPanel(originalText, textColor), TextureCache.DefaultRenderingColor.BLACK);
	}

	public TextureCache.DynamicResource getOtisSeries1ScreenDisplay(String originalText, int textColor) {
		return TextureCache.instance.getResource(String.format("test_lift_panel_display_%s", originalText), () -> RouteMapList.generateTestLiftPanel(originalText, textColor), TextureCache.DefaultRenderingColor.BLACK);
	}

}
