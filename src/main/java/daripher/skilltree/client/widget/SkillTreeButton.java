package daripher.skilltree.client.widget;

import java.util.function.Supplier;

import daripher.skilltree.client.screen.SkillTreeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SkillTreeButton extends Button {
	public SkillTreeButton(int x, int y, int width, int height, Component message, OnPress pressFunction) {
		super(x, y, width, height, message, pressFunction, Supplier::get);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		int v = !isActive() ? 24 : isHoveredOrFocused() ? 38 : 10;
		graphics.blit(SkillTreeScreen.WIDGETS_TEXTURE, getX(), getY(), 0, v, width, height);
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		int textColor = getFGColor();
		graphics.drawCenteredString(font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, textColor | Mth.ceil(alpha * 255F) << 24);
	}
}
