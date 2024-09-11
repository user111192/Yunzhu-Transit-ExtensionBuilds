package top.xfunny;


import org.mtr.core.tool.Utilities;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.ResourceManagerHelper;


import org.mtr.mod.config.Config;
import org.mtr.mod.config.LanguageDisplay;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.MainRenderer;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.text.AttributedString;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public class TextureCache {
	public static TextureCache instance = new TextureCache();

	public Font font1;
	public Font fontCjk1;
	public Font testfont;
	private final Object2ObjectLinkedOpenHashMap<String, DynamicResource> dynamicResources = new Object2ObjectLinkedOpenHashMap<>();
	private final ObjectOpenHashSet<String> generatingResources = new ObjectOpenHashSet<>();
	private final ObjectArrayList<Runnable> resourceRegistryQueue = new ObjectArrayList<>();
	private static final int COOL_DOWN_TIME = 10000;
	public static final float LINE_HEIGHT_MULTIPLIER = 1.25F;
	private static final Identifier DEFAULT_BLACK_RESOURCE = new Identifier(Init.MOD_ID, "textures/block/black.png");
	private static final Identifier DEFAULT_WHITE_RESOURCE = new Identifier(Init.MOD_ID, "textures/block/white.png");
	private static final Identifier DEFAULT_TRANSPARENT_RESOURCE = new Identifier(Init.MOD_ID, "textures/block/transparent.png");



	public DynamicResource getTestLiftButtonsDisplay(String originalText, int textColor) {

		return getResource(String.format("lift_panel_display_%s", originalText), () -> YteRouteMapGenerator.generateTestLiftPanel(originalText, textColor), DefaultRenderingColor.BLACK);

	}





	public void reload() {
		font1 = null;
		fontCjk1 = null;
		Init.LOGGER.debug("Refreshing dynamic resources");
		dynamicResources.values().forEach(dynamicResource -> dynamicResource.needsRefresh = true);
		generatingResources.clear();
	}



	/**
	 * 根据给定的文本和格式参数，生成一个包含文本像素的字节数组。
	 *
	 * @param text           需要转换为像素的文本。
	 * @param dimensions     用于存储生成图像的宽度和高度的数组。
	 * @param maxWidth       图像的最大宽度。
	 * @param maxHeight      图像的最大高度。
	 * @param fontSizeCjk    CJK（中日韩）文本的字体大小。
	 * @param fontSize       非CJK文本的字体大小。
	 * @param padding        文本周围的填充值。
	 * @param horizontalAlignment  文本的水平对齐方式，如果为null则表示单行显示，默认为左对齐。
	 * @param font           非CJK文本的字体。
	 * @param fontCjk        CJK文本的字体。
	 * @return               包含文本像素的字节数组。如果最大宽度小于等于0，则返回空数组。
	 */
public byte[] getTextPixels(String text, int[] dimensions, int maxWidth, int maxHeight, int fontSizeCjk, int fontSize, int padding, @Nullable IGui.HorizontalAlignment horizontalAlignment, Font font, Font fontCjk) {
    if (maxWidth <= 0) {
        dimensions[0] = 0;
        dimensions[1] = 0;
        return new byte[0];
    }

    final boolean oneRow = horizontalAlignment == null;
    final String[] defaultTextSplit = IGui.textOrUntitled(text).split("\\|");
    final String[] textSplit;
    if (Config.getClient().getLanguageDisplay() == LanguageDisplay.NORMAL) {
        textSplit = defaultTextSplit;
    } else {
        final String[] tempTextSplit = Arrays.stream(IGui.textOrUntitled(text).split("\\|"))
                                             .filter(textPart -> IGui.isCjk(textPart) == (Config.getClient().getLanguageDisplay() == LanguageDisplay.CJK_ONLY))
                                             .toArray(String[]::new);
        textSplit = tempTextSplit.length == 0 ? defaultTextSplit : tempTextSplit;
    }

    final AttributedString[] attributedStrings = new AttributedString[textSplit.length];
    final int[] textWidths = new int[textSplit.length];
    final int[] fontSizes = new int[textSplit.length];
    final FontRenderContext context = new FontRenderContext(new AffineTransform(), false, false);

    int totalHeight = 0;
    int maxWidthActual = 0;

    for (int index = 0; index < textSplit.length; index++) {
        final int newFontSize = IGui.isCjk(textSplit[index]) || font.canDisplayUpTo(textSplit[index]) >= 0 ? fontSizeCjk : fontSize;
        final Font fontSized = font.deriveFont(Font.PLAIN, newFontSize);
        final Font fontCjkSized = fontCjk.deriveFont(Font.PLAIN, newFontSize);

        attributedStrings[index] = new AttributedString(textSplit[index]);
        fontSizes[index] = newFontSize;

        int textWidth = 0;
        double maxCharHeight = 0;

        for (int characterIndex = 0; characterIndex < textSplit[index].length(); characterIndex++) {
            final char character = textSplit[index].charAt(characterIndex);
            final Font newFont = chooseFont(character, fontSized, fontCjkSized, newFontSize);

            Rectangle2D charBounds = newFont.getStringBounds(textSplit[index].substring(characterIndex, characterIndex + 1), context);
            textWidth += charBounds.getWidth();
            maxCharHeight = Math.max(maxCharHeight, charBounds.getHeight());

            attributedStrings[index].addAttribute(TextAttribute.FONT, newFont, characterIndex, characterIndex + 1);
        }

        textWidths[index] = textWidth;
        totalHeight += maxCharHeight;  // 累计文本高度
        maxWidthActual = Math.max(maxWidthActual, Math.min(maxWidth, textWidth));
    }

    int imageWidth = Math.min(maxWidthActual + (oneRow ? 0 : padding * 2), maxWidth);
    int imageHeight = Math.min(totalHeight + (oneRow ? 0 : padding * 2), maxHeight);

    BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics2D = image.createGraphics();
    graphics2D.setColor(Color.WHITE);
    graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // 设置绘制边界，确保字符不会超出图像边界
    graphics2D.setClip(0, 0, imageWidth, imageHeight);

    // 确保字符垂直居中渲染
    int yOffset = (imageHeight - totalHeight) / 2;  // 计算初始Y轴偏移，确保文本区域垂直居中

    for (int index = 0; index < textSplit.length; index++) {
        int xOffset = (imageWidth - textWidths[index]) / 2;  // 水平居中，计算X轴偏移

        // 使用FontMetrics来获取正确的基线位置
        FontMetrics fontMetrics = graphics2D.getFontMetrics(font.deriveFont(Font.PLAIN, fontSizes[index]));
        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int lineHeight = ascent + descent;  // 获取行高，确保文本基线的正确对齐

        // 确保文本基线按照行高绘制
        for (int characterIndex = 0; characterIndex < textSplit[index].length(); characterIndex++) {
            final char character = textSplit[index].charAt(characterIndex);
            final Font newFont = chooseFont(character, font.deriveFont(Font.PLAIN, fontSizes[index]), fontCjk.deriveFont(Font.PLAIN, fontSizes[index]), fontSizes[index]);

            Rectangle2D charBounds = newFont.getStringBounds(textSplit[index].substring(characterIndex, characterIndex + 1), context);

            // 绘制字符并更新X轴偏移，防止字符重叠
            graphics2D.setFont(newFont);
            graphics2D.drawString(String.valueOf(character), xOffset, yOffset + ascent);  // 使用基线对齐渲染
            xOffset += charBounds.getWidth() + padding;  // 加入字符宽度和填充间隔

            // 防止字符超出图像宽度边界
            if (xOffset > imageWidth) {
                break;
            }
        }

        yOffset += lineHeight;  // 更新Y轴偏移，每行按行高递增
    }

    dimensions[0] = imageWidth;
    dimensions[1] = imageHeight;
    byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    graphics2D.dispose();
    image.flush();
    return pixels;
}


	/**
	 * 选择合适的字体。
	 *
	 * @param character      需要显示的字符。
	 * @param fontSized      当前大小的非CJK字体。
	 * @param fontCjkSized   当前大小的CJK字体。
	 * @param newFontSize    字体大小。
	 * @return               适合显示字符的字体。
	 */
	private Font chooseFont(char character, Font fontSized, Font fontCjkSized, int newFontSize) {
		if (fontSized.canDisplay(character)) {
			return fontSized;
		} else if (fontCjkSized.canDisplay(character)) {
			return fontCjkSized;
		} else {
			Font defaultFont = null;
			for (final Font testFont : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
				if (testFont.canDisplay(character)) {
					defaultFont = testFont;
					break;
				}
			}
			return (defaultFont == null ? new Font(null) : defaultFont).deriveFont(Font.PLAIN, newFontSize);
		}
	}

	private DynamicResource getResource(String key, Supplier<NativeImage> supplier, DefaultRenderingColor defaultRenderingColor) {
		Init.LOGGER.debug("执行getResource");
		if (!resourceRegistryQueue.isEmpty()) {
			final Runnable runnable = resourceRegistryQueue.remove(0);
			if (runnable != null) {
				runnable.run();
			}
		}

		final DynamicResource dynamicResource = dynamicResources.get(key);

		if (dynamicResource != null && !dynamicResource.needsRefresh) {
			dynamicResource.expiryTime = System.currentTimeMillis() + COOL_DOWN_TIME;
			return dynamicResource;
		}

		if (generatingResources.contains(key)) {
			return defaultRenderingColor.dynamicResource;
		}

		MainRenderer.WORKER_THREAD.scheduleDynamicTextures(() -> {
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

			final NativeImage nativeImage = supplier.get();

			resourceRegistryQueue.add(() -> {
				final DynamicResource staticTextureProviderOld = dynamicResources.get(key);
				if (staticTextureProviderOld != null) {
					staticTextureProviderOld.remove();
				}

				final DynamicResource dynamicResourceNew;
				if (nativeImage != null) {
					final NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(nativeImage);
					final Identifier identifier = new Identifier(Init.MOD_ID, "id_" + Utilities.numberToPaddedHexString(new Random().nextLong()).toLowerCase(Locale.ENGLISH));
					MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new AbstractTexture(nativeImageBackedTexture.data));
					dynamicResourceNew = new DynamicResource(identifier, nativeImageBackedTexture);
					dynamicResources.put(key, dynamicResourceNew);
				}

				generatingResources.remove(key);
			});
		});
		YteRouteMapGenerator.setConstants();
		generatingResources.add(key);

		if (dynamicResource == null) {
			return defaultRenderingColor.dynamicResource;
		} else {
			dynamicResource.expiryTime = System.currentTimeMillis() + COOL_DOWN_TIME;
			dynamicResource.needsRefresh = false;
			return dynamicResource;
		}
	}

	private enum DefaultRenderingColor {
		BLACK(DEFAULT_BLACK_RESOURCE),
		WHITE(DEFAULT_WHITE_RESOURCE),


		TRANSPARENT(DEFAULT_TRANSPARENT_RESOURCE);

		private final DynamicResource dynamicResource;

		DefaultRenderingColor(Identifier identifier) {
			dynamicResource = new DynamicResource(identifier, null);
		}
	}

	public static class DynamicResource {

		private long expiryTime;
		private boolean needsRefresh;
		public final int width;
		public final int height;
		public final Identifier identifier;

		private DynamicResource(Identifier identifier, @Nullable NativeImageBackedTexture nativeImageBackedTexture) {
			this.identifier = identifier;
			if (nativeImageBackedTexture != null) {
				final NativeImage nativeImage = nativeImageBackedTexture.getImage();
				if (nativeImage != null) {
					width = nativeImage.getWidth();
					height = nativeImage.getHeight();
				} else {
					width = 16;
					height = 16;

				}
			} else {
				width = 16;
				height = 16 ;

			}
		}


		private void remove() {
			MinecraftClient.getInstance().getTextureManager().destroyTexture(identifier);
			MainRenderer.cancelRender(identifier);
		}
	}






}

