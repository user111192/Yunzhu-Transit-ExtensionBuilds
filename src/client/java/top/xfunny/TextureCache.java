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
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.text.AttributedString;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public class TextureCache {
	public static TextureCache instance = new TextureCache();

    private Font font;
	private Font fontCjk;
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
		font = null;
		fontCjk = null;
		Init.LOGGER.debug("Refreshing dynamic resources");
		dynamicResources.values().forEach(dynamicResource -> dynamicResource.needsRefresh = true);
		generatingResources.clear();
	}

	public byte[] getTextPixels(String text, int[] dimensions, int fontSizeCjk, int fontSize) {
		return getTextPixels(text, dimensions, Integer.MAX_VALUE, (int) (Math.max(fontSizeCjk, fontSize) * LINE_HEIGHT_MULTIPLIER), fontSizeCjk, fontSize, 0, null);
	}

	public byte[] getTextPixels(String text, int[] dimensions, int maxWidth, int maxHeight, int fontSizeCjk, int fontSize, int padding, @Nullable IGui.HorizontalAlignment horizontalAlignment) {
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
			final String[] tempTextSplit = Arrays.stream(IGui.textOrUntitled(text).split("\\|")).filter(textPart -> IGui.isCjk(textPart) == (Config.getClient().getLanguageDisplay() == LanguageDisplay.CJK_ONLY)).toArray(String[]::new);
			textSplit = tempTextSplit.length == 0 ? defaultTextSplit : tempTextSplit;
		}
		final AttributedString[] attributedStrings = new AttributedString[textSplit.length];
		final int[] textWidths = new int[textSplit.length];
		final int[] fontSizes = new int[textSplit.length];
		final FontRenderContext context = new FontRenderContext(new AffineTransform(), false, false);
		int width = 0;
		int height = 0;

		for (int index = 0; index < textSplit.length; index++) {
			final int newFontSize = IGui.isCjk(textSplit[index]) || font.canDisplayUpTo(textSplit[index]) >= 0 ? fontSizeCjk : fontSize;
			attributedStrings[index] = new AttributedString(textSplit[index]);
			fontSizes[index] = newFontSize;

			final Font fontSized = font.deriveFont(Font.PLAIN, newFontSize);
			final Font fontCjkSized = fontCjk.deriveFont(Font.PLAIN, newFontSize);

			for (int characterIndex = 0; characterIndex < textSplit[index].length(); characterIndex++) {
				final char character = textSplit[index].charAt(characterIndex);
				final Font newFont;
				if (fontSized.canDisplay(character)) {
					newFont = fontSized;
				} else if (fontCjkSized.canDisplay(character)) {
					newFont = fontCjkSized;
				} else {
					Font defaultFont = null;
					for (final Font testFont : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
						if (testFont.canDisplay(character)) {
							defaultFont = testFont;
							break;
						}
					}
					newFont = (defaultFont == null ? new Font(null) : defaultFont).deriveFont(Font.PLAIN, newFontSize);
				}
				textWidths[index] += newFont.getStringBounds(textSplit[index].substring(characterIndex, characterIndex + 1), context).getBounds().width;
				attributedStrings[index].addAttribute(TextAttribute.FONT, newFont, characterIndex, characterIndex + 1);
			}

			if (oneRow) {
				if (index > 0) {

					width += padding;
				}
				width += textWidths[index];
				height = Math.max(height, (int) (fontSizes[index] * LINE_HEIGHT_MULTIPLIER));
			} else {
				width = Math.max(width, Math.min(maxWidth, textWidths[index]));
				height += (int) (fontSizes[index] * LINE_HEIGHT_MULTIPLIER);
			}
		}

		int textOffset = 0;
		final int imageHeight = Math.min(height, maxHeight);
		final BufferedImage image = new BufferedImage(width + (oneRow ? 0 : padding * 2), imageHeight + (oneRow ? 0 : padding * 2), BufferedImage.TYPE_BYTE_GRAY);
		final Graphics2D graphics2D = image.createGraphics();
		graphics2D.setColor(Color.WHITE);
		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		for (int index = 0; index < textSplit.length; index++) {
			if (oneRow) {
				graphics2D.drawString(attributedStrings[index].getIterator(), textOffset, height / LINE_HEIGHT_MULTIPLIER);
				textOffset += textWidths[index] + padding;
			} else {
				final float scaleY = (float) imageHeight / height;
				final float textWidth = Math.min(maxWidth, textWidths[index] * scaleY);
				final float scaleX = textWidth / textWidths[index];
				final AffineTransform stretch = new AffineTransform();
				stretch.concatenate(AffineTransform.getScaleInstance(scaleX, scaleY));
				graphics2D.setTransform(stretch);
				graphics2D.drawString(attributedStrings[index].getIterator(), horizontalAlignment.getOffset(0, textWidth - width) / scaleY + padding / scaleX, textOffset + fontSizes[index] + padding / scaleY);
				textOffset += (int) (fontSizes[index] * LINE_HEIGHT_MULTIPLIER);
			}
		}

		dimensions[0] = width + (oneRow ? 0 : padding * 2);
		dimensions[1] = imageHeight + (oneRow ? 0 : padding * 2);
		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		graphics2D.dispose();
		image.flush();
		return pixels;
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
			while (font == null) {
				ResourceManagerHelper.readResource(new Identifier(org.mtr.mod.Init.MOD_ID, "font/noto-sans-semibold.ttf"), inputStream -> {
					try {
						font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
					} catch (Exception e) {
						Init.LOGGER.error("失败", e);
					}
				});
			}

			while (fontCjk == null) {
				ResourceManagerHelper.readResource(new Identifier(org.mtr.mod.Init.MOD_ID, "font/noto-serif-cjk-tc-semibold.ttf"), inputStream -> {
					try {
						fontCjk = Font.createFont(Font.TRUETYPE_FONT, inputStream);
					} catch (Exception e) {
						Init.LOGGER.error("失败", e);
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
				height = 16;
			}
		}


		private void remove() {
			MinecraftClient.getInstance().getTextureManager().destroyTexture(identifier);
			MainRenderer.cancelRender(identifier);
		}
	}






}

