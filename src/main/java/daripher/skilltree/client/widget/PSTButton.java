package daripher.skilltree.client.widget;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PSTButton extends Button {
	protected Button.OnPress pressFunc;

	public PSTButton(int x, int y, int width, int height, Component message, OnPress pressFunc) {
		super(x, y, width, height, message, pressFunc, Supplier::get);
		this.pressFunc = pressFunc;
	}

	public PSTButton(int x, int y, int width, int height, Component message) {
		this(x, y, width, height, message, b -> {});
	}

	public void setPressFunc(Button.OnPress pressFunc) {
		this.pressFunc = pressFunc;
	}

	@Override
	public void onPress() {
		pressFunc.onPress(this);
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
