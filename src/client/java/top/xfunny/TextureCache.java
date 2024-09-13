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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

import java.util.List;


public class TextureCache {
	public static TextureCache instance = new TextureCache();

	public Font font1;
	public Font fontCjk1;
	public Font testfont;
	public Font testfont1;
	public int totalWidth;
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







public byte[] getTextPixels(String text, int[] dimensions, int maxWidth, int maxHeight, int fontSizeCjk, int fontSize, int padding, Font font, Font fontCjk) {
    if (maxWidth <= 0) {
        dimensions[0] = 0;
        dimensions[1] = 0;
        return new byte[0];
    }

    // 初始化字体渲染上下文
    final FontRenderContext context = new FontRenderContext(new AffineTransform(), false, false);

    // 初始化总宽度和高度
    totalWidth = 0;
    int totalHeight = maxHeight;

    // 计算每个字符的宽度和缩放后的高度
    List<BufferedImage> characterImages = new ArrayList<>();

    for (int i = 0; i < text.length(); i++) {
        char character = text.charAt(i);
        // 选择合适的字体
        Font selectedFont = IGui.isCjk(Character.toString(character)) ? fontCjk.deriveFont(Font.PLAIN, fontSizeCjk) : font.deriveFont(Font.PLAIN, fontSize);
        Rectangle2D charBounds = selectedFont.getStringBounds(Character.toString(character), context);

        // 获取字符原始宽度和高度
        double charWidth = charBounds.getWidth();
        double charHeight = charBounds.getHeight();

        // 计算缩放比例
        double scaleFactor = maxHeight / charHeight;

        // 缩放后的字符宽度
        int scaledWidth = (int) (charWidth * scaleFactor);

        // 创建灰度图像，宽度为缩放后的宽度，高度为 maxHeight
        BufferedImage charImage = new BufferedImage(scaledWidth, maxHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics2D = charImage.createGraphics();
        graphics2D.setColor(Color.WHITE);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 设置垂直居中的偏移量
        FontMetrics fontMetrics = graphics2D.getFontMetrics(selectedFont);
        int ascent = fontMetrics.getAscent();
        int verticalOffset = (maxHeight - (int) charHeight) / 2 + ascent;

		//设置水平居中偏移量
		int horizontalOffset = (scaledWidth - (int) charWidth) / 2;

        // 设置字体
        graphics2D.setFont(selectedFont);

        // 绘制字符，保证垂直居中
        graphics2D.drawString(Character.toString(character), horizontalOffset, verticalOffset);

        // 添加字符宽度的 padding
        totalWidth += scaledWidth + padding;

        // 将处理后的字符图像保存
        characterImages.add(charImage);

        // 释放资源
        graphics2D.dispose();
    }

    // 如果总宽度超出了 maxWidth，则需要缩放
    /*if (totalWidth > maxWidth) {
        double scaleDownFactor = (double) maxWidth / totalWidth;
        totalWidth = maxWidth;

        // 按比例缩放每个字符的宽度
        for (int i = 0; i < characterImages.size(); i++) {
            BufferedImage charImage = characterImages.get(i);
            int newWidth = (int) (charImage.getWidth() * scaleDownFactor);
            BufferedImage scaledImage = new BufferedImage(newWidth, maxHeight, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.drawImage(charImage, 0, 0, newWidth, maxHeight, null);
            g2d.dispose();
            characterImages.set(i, scaledImage);
        }
    }*/

    // 创建总图像
    BufferedImage finalImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D finalGraphics = finalImage.createGraphics();
    finalGraphics.setColor(Color.WHITE);

    // 拼接每个字符的图像
    int xOffset = 0;
    for (BufferedImage charImage : characterImages) {
        finalGraphics.drawImage(charImage, xOffset, 0, null);
        xOffset += charImage.getWidth() + padding;
    }

    finalGraphics.dispose();

    // 返回图像字节数组
    dimensions[0] = totalWidth;
    dimensions[1] = totalHeight;
    byte[] pixels = ((DataBufferByte) finalImage.getRaster().getDataBuffer()).getData();
    finalImage.flush();

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

			while (testfont1 == null) {
				ResourceManagerHelper.readResource(new Identifier(Init.MOD_ID, "font/kone-modernization.ttf"), inputStream -> {
					try {
						testfont1 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
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

