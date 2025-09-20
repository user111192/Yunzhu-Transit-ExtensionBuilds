package top.xfunny.mod.client.resource;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.ResourceManagerHelper;
import top.xfunny.mod.Init;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FontList {
    public static FontList instance = new FontList();

    private Map<String, Font> fonts = new HashMap<String, Font>();
    private boolean fontsLoaded = false;

    public void FontReload() {
        fonts.clear();
        fontsLoaded = false;
    }

    public void FlonList() {
        if (!fontsLoaded) {
            // 字体列表
            loadFont("ces-14x7", "font/ces-14x7.ttf");
            loadFont("testfont", "font/schindler-m-series-lop-nz-thin-1-beta.ttf");
            loadFont("acmeled", "font/acme-led.ttf");
            loadFont("koneModernization", "font/kone-modernization.ttf");
            loadFont("schindler_m_series", "font/schindler-m-line-led.ttf");
            loadFont("mitsubishi_modern", "font/mitsubishi_modern.ttf");
            loadFont("mitsubishi_small_regular", "font/mitsubishi-dot-matrix-small-generic.ttf");
            loadFont("mitsubishi_small_sht", "font/mitsubishi-dot-matrix-small-shanghai-tower.ttf");
            loadFont("mitsubishi_seg_universal", "font/mitsubishi_seg_universal.ttf");
            loadFont("kone-m-series", "font/kone-m-series.ttf");
            loadFont("otis_series1", "font/series1.otf");
            loadFont("schindler_lcd", "font/schindler_lcd.ttf");
            loadFont("schindler_led", "font/schindler-led.ttf");
            loadFont("schindler_linea", "font/schindler-linea.ttf");
            loadFont("schindler_linea_large", "font/schindler-linea-large.ttf");
            loadFont("hitachi_b85", "font/hitachi_b85.ttf");
            loadFont("hitachi_b85_left", "font/hitachi_b85_1_left.ttf");
            loadFont("nimbus_sans_bold", "font/nimbus_sans_bold.ttf");
            loadFont("gill_sans_mt", "font/gill_sans_mt.ttf");
            loadFont("hitachi_modern", "font/hitachi_modern.ttf");
            loadFont("kone-lcd-segment", "font/kone-lcd-segment.ttf");
            loadFont("otis_series_3", "font/otis_series_3.ttf");
            loadFont("otis-segmented", "font/otis-segmented.ttf");
            loadFont("wqy-microhei", "font/wqy-microhei.ttc");
            loadFont("thyssenkrupp_lcd", "font/new-thyssenkrupp.ttf");
            loadFont("kone-kss", "font/kone-kss-800-signalization.ttf");
            loadFont("hitachi-led-seg", "font/hitachi-cip71-led.ttf");
            loadFont("hitachi-led-seg-fix", "font/hitachi-cip71-led-left.ttf");
            loadFont("hitachi-led-dot_matrix", "font/hitachi-dot-matrix-regular.ttf"); // 已弃用
            loadFont("hitachi-led-dot_matrix_small", "font/hitachi-dot-matrix-small.ttf"); // 已弃用
            loadFont("hitachi-bxsclc5", "font/hitachi-bxsclc5-led.ttf");
            loadFont("hitachi-bxsclc5-compact", "font/hitachi-bxsclc5-led-compact.ttf");
            loadFont("hitachi-bxsclc5-pafc-compact", "font/hitachi-bxsclc5-led-pafc-compact.ttf"); // 深圳 PAFC 使用
            loadFont("hitachi-lcd-seg", "font/hitachi-hip31-lcd.ttf");
            loadFont("hitachi-japan-lcd", "font/hitachi-hip32-lcd.ttf");
            loadFont("hitachi-hip43", "font/hitachi-hip43-lcd.ttf");
            loadFont("hitachi-sclva043", "font/hitachi-sclva043-lcd.ttf");
            loadFont("shanghai_mitsubishi_segmented_1","font/shanghai_mitsubishi_segmented_1.ttf");
            loadFont("shanghai_mitsubishi_segmented_1_sp","font/shanghai_mitsubishi_segmented_1_sp.ttf");
            loadFont("shanghai_mitsubishi_lcd_1","font/shanghai_mitsubishi_lcd_1.ttf");
            loadFont("schindler_m_series_segment", "font/schindler_m_series_segment.ttf");
            loadFont("tonic-led", "font/tonic-led.ttf");
            loadFont("tonic-led-thin", "font/tonic-thin.ttf");
            loadFont("mitsubishi_old_segmented_1", "font/mitsubishi_old_segmented_1.ttf");
            loadFont("ryoden-led", "font/ryoden-led.ttf");
            loadFont("ryoden-led-small", "font/ryoden-led-small.ttf");
            loadFont("helvetica", "font/helvetica.ttf");
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
            } catch (Exception e) {
            }
        });
    }

    public Font getFont(String fontId) {
        Font font = fonts.get(fontId);
        if (font != null) {
            return font;
        } else {
            if (Objects.equals(fontId, "Arial")) {
                return new Font("Arial", Font.PLAIN, 12);
            } else {
                FlonList();
                font = fonts.get(fontId);
                if (font != null) {
                    return font;
                } else {
                    return new Font("Arial", Font.PLAIN, 12);
                }
            }
        }
    }
}