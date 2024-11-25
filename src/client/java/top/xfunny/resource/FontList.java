package top.xfunny.resource;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.ResourceManagerHelper;
import top.xfunny.Init;

import java.awt.*;

public class FontList {
	public static FontList instance = new FontList();

	Font font1;
	Font fontCjk1;
	Font otis_series1;
	Font testfont;
	Font acmeled;
	Font koneModernization;
	Font schindler_m_series;
	Font mitsubishi_modern;

	public void FontReload(){
		font1 = null;
		fontCjk1 = null;
		testfont = null;
		koneModernization = null;
		mitsubishi_modern = null;
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

		while (acmeled == null) {
			ResourceManagerHelper.readResource(new Identifier(Init.MOD_ID, "font/acme-led.ttf"), inputStream -> {
				try {
					acmeled = Font.createFont(Font.TRUETYPE_FONT, inputStream);
				} catch (Exception e) {
					Init.LOGGER.error("", e);
				}
			});
		}

		while (koneModernization == null) {
			ResourceManagerHelper.readResource(new Identifier(Init.MOD_ID, "font/kone-modernization.ttf"), inputStream -> {
				try {
					koneModernization = Font.createFont(Font.TRUETYPE_FONT, inputStream);
				} catch (Exception e) {
					Init.LOGGER.error("", e);
				}
			});
		}
		while (schindler_m_series == null) {
			ResourceManagerHelper.readResource(new Identifier(Init.MOD_ID, "font/schindler_mseries_font.ttf"), inputStream -> {
				try {
					schindler_m_series = Font.createFont(Font.TRUETYPE_FONT, inputStream);
				} catch (Exception e) {
					Init.LOGGER.error("", e);
				}
			});
		}
		while (mitsubishi_modern == null) {
			ResourceManagerHelper.readResource(new Identifier(Init.MOD_ID, "font/mitsubishi_modern.ttf"), inputStream -> {
				try {
					mitsubishi_modern = Font.createFont(Font.TRUETYPE_FONT, inputStream);
				} catch (Exception e) {
					Init.LOGGER.error("", e);
				}
			});
		}
	}
	public Font getFont(String fontId) {
		switch (fontId) {
			case "font1":
				return font1;
			case "fontCjk1":
				return fontCjk1;
			case "otis_series1":
				return otis_series1;
			case "testfont":
				return testfont;
			case "acmeled":
				return acmeled;
			case "koneModernization":
				return koneModernization;
			case "schindler_m_series":
				return schindler_m_series;
			case "mitsubishi_modern":
				return mitsubishi_modern;
			default:
				// 返回默认字体或抛出异常
				return new Font("Arial", Font.PLAIN, 12); // 默认字体
			// 或者抛出异常
			// throw new IllegalArgumentException("Unknown fontId: " + fontId);
		}
	}
}
