package top.xfunny.Resource;

import top.xfunny.TextureCache;


public class TextureList {
	public static TextureList instance = new TextureList();
	public TextureCache.DynamicResource getTestLiftButtonsDisplay(String originalText, int textColor) {
		return TextureCache.instance.getResource(String.format("test_lift_panel_display_%s", originalText), () -> RouteMapList.generateTestLiftPanel(originalText, textColor), TextureCache.DefaultRenderingColor.BLACK);

	}

}
