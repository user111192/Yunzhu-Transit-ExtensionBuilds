package top.xfunny.client.resource;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.ResourceManagerHelper;
import top.xfunny.Init;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FontList {
    public static FontList instance = new FontList();

    private Map<String, Font> fonts = new HashMap<>();
    private boolean fontsLoaded = false;

    public void FontReload() {
        fonts.clear();
        fontsLoaded = false;
    }

    public void FlonList() {
        if (!fontsLoaded) {//todo:在此处添加字体
            loadFont("font1", "font/noto-sans-semibold.ttf");
            loadFont("fontCjk1", "font/noto-serif-cjk-tc-semibold.ttf");
            loadFont("testfont", "font/schindler-m-series-lop-nz-thin-1-beta.ttf");
            loadFont("acmeled", "font/acme-led.ttf");
            loadFont("koneModernization", "font/kone-modernization.ttf");
            loadFont("schindler_m_series", "font/schindler-m-line-led.ttf");
            loadFont("mitsubishi_modern", "font/mitsubishi_modern.ttf");
            loadFont("kone-m-series", "font/kone-m-series.ttf");
            loadFont("otis_series1", "font/series1.otf");
            loadFont("schindler_lcd", "font/schindler_lcd.ttf");
            loadFont("schindler_led", "font/schindler-led.ttf");
            loadFont("hitachi_b85", "font/hitachi_b85.ttf");
            fontsLoaded = true;
        }
    }

    private void loadFont(String fontName, String resourcePath) {
        ResourceManagerHelper.readResource(new Identifier(Init.MOD_ID, resourcePath), inputStream -> {
            try {
                Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(font);
                fonts.put(fontName, font);
                Init.LOGGER.info(fontName + " font loaded successfully.");
            } catch (Exception e) {
                Init.LOGGER.error("Failed to load " + fontName + " font: ", e);
            }
        });
    }

    public Font getFont(String fontId) {
        Font font = fonts.get(fontId);
        if (font != null) {
            return font;
        } else {
            if (Objects.equals(fontId, "Arial")) {
                return new Font("Arial", Font.PLAIN, 12);//单独加载Arial字体，用户自行安装。
            } else {
                Init.LOGGER.warn("Font with ID " + fontId + " not found. Retrying to load fonts.");
                FlonList(); // 重新加载所有字体
                font = fonts.get(fontId); // 再次尝试获取字体
                if (font != null) {
                    return font;
                } else {
                    Init.LOGGER.warn("Font with ID " + fontId + " still not found after retry. Returning default font.");
                    return new Font("Arial", Font.PLAIN, 12); // 默认字体
                }
            }
        }
    }
}
