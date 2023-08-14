package daripher.skilltree.client.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class ScreenHelper {
	public static void drawCenteredOutlinedText(GuiGraphics graphics, String text, Font font, int x, int y, int color) {
		x -= font.width(text) / 2;
		graphics.drawString(font, text, x + 1, y, 0);
		graphics.drawString(font, text, x - 1, y, 0);
		graphics.drawString(font, text, x, y + 1, 0);
		graphics.drawString(font, text, x, y - 1, 0);
		graphics.drawString(font, text, x, y, color);
	}
}
