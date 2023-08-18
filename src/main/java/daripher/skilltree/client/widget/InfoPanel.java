package daripher.skilltree.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class InfoPanel extends AbstractWidget {
	public InfoPanel(int x, int y, int width, int height, Component message) {
		super(x, y, width, height, message);
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/widgets/buttons.png");
		graphics.blit(texture, getX(), getY(), 0, 14, width / 2, height);
		graphics.blit(texture, getX() + width / 2, getY(), 140 - width / 2, 14, width / 2, height);
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		int textColor = getFGColor();
		graphics.drawCenteredString(font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, textColor | Mth.ceil(alpha * 255F) << 24);
	}

	@Override
	public void updateWidgetNarration(NarrationElementOutput output) {
	}
}
