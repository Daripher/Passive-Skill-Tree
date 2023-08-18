package daripher.skilltree.client.widget;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SkillTreeButton extends Button {
	public SkillTreeButton(int x, int y, int width, int height, Component message, OnPress pressFunction) {
		super(x, y, width, height, message, pressFunction, Supplier::get);
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/widgets/buttons.png");
		int v = !isActive() ? 0 : isHoveredOrFocused() ? 28 : 14;
		graphics.blit(texture, getX(), getY(), 0, v, width / 2, height);
		graphics.blit(texture, getX() + width / 2, getY(), 140 - width / 2, v, width / 2, height);
		int textColor = getFGColor() | Mth.ceil(alpha * 255F) << 24;
		graphics.drawCenteredString(Minecraft.getInstance().font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, textColor);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return visible && mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
	}
}
