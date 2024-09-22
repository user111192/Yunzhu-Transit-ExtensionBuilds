package top.xfunny.resource;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.ResourceManagerHelper;
import top.xfunny.Init;
import java.awt.*;

public class FontList {
	public static FontList instance = new FontList();

	Font font1;
	Font fontCjk1;
	Font testfont;
	Font testfont1;

	public void FontReload(){
		font1 = null;
		fontCjk1 = null;
		testfont = null;
		testfont1 = null;
	}

	public void FlonList(){

		while (font1 == null) {

			ResourceManagerHelper.readResource(new Identifier(Init.MOD_ID, "font/noto-sans-semibold.ttf"), inputStream -> {
				try {
					font1 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
				} catch (Exception e) {
					Init.LOGGER.error("", e);
				}
			});
		}

		while (fontCjk1 == null) {
			ResourceManagerHelper.readResource(new Identifier(Init.MOD_ID, "font/noto-serif-cjk-tc-semibold.ttf"), inputStream -> {
				try {
					fontCjk1 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
				} catch (Exception e) {
					Init.LOGGER.error("", e);
				}
			});
		}

		while (testfont == null) {
			ResourceManagerHelper.readResource(new Identifier(Init.MOD_ID, "font/schindler-m-series-lop-nz-thin-1-beta.ttf"), inputStream -> {
				try {
					testfont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
				} catch (Exception e) {
					Init.LOGGER.error("", e);
				}
			});
		}

		while (testfont1 == null) {
			ResourceManagerHelper.readResource(new Identifier(Init.MOD_ID, "font/kone-modernization.ttf"), inputStream -> {
				try {
					testfont1 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
				} catch (Exception e) {
					Init.LOGGER.error("", e);
				}
			});
		}
	}
}
