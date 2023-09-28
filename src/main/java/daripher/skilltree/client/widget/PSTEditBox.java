package daripher.skilltree.client.widget;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import com.mojang.blaze3d.vertex.PoseStack;

import daripher.skilltree.client.screen.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class PSTEditBox extends EditBox {
	private Predicate<String> softFilter = Objects::nonNull;

	public PSTEditBox(Font font, int x, int y, int width, int height, String defaultText) {
		super(font, x, y, width, height, Component.empty());
		setMaxLength(80);
		setValue(defaultText);
	}

	public void setSoftFilter(Predicate<String> filter) {
		this.softFilter = filter;
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		if (this.isVisible()) {
			ScreenHelper.prepareTextureRendering(new ResourceLocation("skilltree:textures/screen/widgets/buttons.png"));
			int v = isHoveredOrFocused() ? 42 : 56;
			blit(poseStack, x, y, 0, v, width / 2, height);
			blit(poseStack, x + width / 2, y, -width / 2, v, width / 2, height);
			int textColor = isValueValid() ? DEFAULT_TEXT_COLOR : 0xD80000;
			int cursorVisiblePosition = getCursorPosition() - getDisplayPosition();
			int highlightWidth = getHighlightPos() - getDisplayPosition();
			Minecraft minecraft = Minecraft.getInstance();
			Font font = minecraft.font;
			String visibleText = font.plainSubstrByWidth(getValue().substring(getDisplayPosition()), getInnerWidth());
			boolean isTextSplitByCursor = cursorVisiblePosition >= 0 && cursorVisiblePosition <= visibleText.length();
			boolean isCursorVisible = isFocused() && getFrame() / 6 % 2 == 0 && isTextSplitByCursor;
			int textX = x + 5;
			int textStartX = textX;
			int textY = y + 3;
			if (highlightWidth > visibleText.length())
				highlightWidth = visibleText.length();
			if (!visibleText.isEmpty()) {
				String s1 = isTextSplitByCursor ? visibleText.substring(0, cursorVisiblePosition) : visibleText;
				textX = font.drawShadow(poseStack, getFormatter().apply(s1, getDisplayPosition()), textX, textY,
						textColor);
			}
			boolean isCursorSurrounded = getCursorPosition() < getValue().length()
					|| getValue().length() >= getMaxLength();
			int cursorX = textX;
			if (!isTextSplitByCursor) {
				cursorX = cursorVisiblePosition > 0 ? x + this.width : x;
			} else if (isCursorSurrounded) {
				cursorX = textX - 1;
				--textX;
			}
			if (!visibleText.isEmpty() && isTextSplitByCursor && cursorVisiblePosition < visibleText.length()) {
				font.drawShadow(poseStack,
						getFormatter().apply(visibleText.substring(cursorVisiblePosition), getCursorPosition()), textX,
						textY, textColor);
			}
			if (!isCursorSurrounded && getSuggestion() != null) {
				font.drawShadow(poseStack, getSuggestion(), cursorX - 1, textY, -8355712);
			}
			if (isCursorVisible) {
				if (isCursorSurrounded)
					GuiComponent.fill(poseStack, cursorX, textY - 1, cursorX + 1, textY + 9, -3092272);
				else
					font.drawShadow(poseStack, "_", cursorX, textY, textColor);
			}
			if (highlightWidth != cursorVisiblePosition) {
				int highlightEndX = textStartX + font.width(visibleText.substring(0, highlightWidth));
				renderHighlight(cursorX, textY - 1, highlightEndX - 1, textY + 9);
			}
		}
	}

	public BiFunction<String, Integer, FormattedCharSequence> getFormatter() {
		return ObfuscationReflectionHelper.getPrivateValue(EditBox.class, this, "f_94091_");
	}

	public void renderHighlight(int startX, int startY, int endX, int endY) {
		try {
			ObfuscationReflectionHelper
					.findMethod(EditBox.class, "m_94135_", int.class, int.class, int.class, int.class)
					.invoke(this, startX, startY, endX, endY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getMaxLength() {
		try {
			return (Integer) ObfuscationReflectionHelper.findMethod(EditBox.class, "m_94216_").invoke(this);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int getFrame() {
		return ObfuscationReflectionHelper.getPrivateValue(EditBox.class, this, "f_94095_");
	}

	public int getHighlightPos() {
		return ObfuscationReflectionHelper.getPrivateValue(EditBox.class, this, "f_94102_");
	}

	public int getDisplayPosition() {
		return ObfuscationReflectionHelper.getPrivateValue(EditBox.class, this, "f_94100_");
	}

	public String getSuggestion() {
		return ObfuscationReflectionHelper.getPrivateValue(EditBox.class, this, "f_94088_");
	}

	public boolean isValueValid() {
		return softFilter.test(getValue());
	}
}
