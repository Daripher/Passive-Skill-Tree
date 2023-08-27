package daripher.skilltree.client.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;

public class ScreenHelper {
	public static void drawCenteredOutlinedText(GuiGraphics graphics, String text, Font font, int x, int y, int color) {
		x -= font.width(text) / 2;
		graphics.drawString(font, text, x + 1, y, 0);
		graphics.drawString(font, text, x - 1, y, 0);
		graphics.drawString(font, text, x, y + 1, 0);
		graphics.drawString(font, text, x, y - 1, 0);
		graphics.drawString(font, text, x, y, color);
	}

	public static void drawRectangle(GuiGraphics graphics, int x, int y, int width, int height, int color) {
		graphics.fill(x, y, x + width, y + 1, color);
		graphics.fill(x, y + height - 1, x + width, y + height, color);
		graphics.fill(x, y + 1, x + 1, y + height - 1, color);
		graphics.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
	}

	public static float getAngleBetweenButtons(Button button1, Button button2) {
		float x1 = button1.getX() + button1.getWidth() / 2F;
		float y1 = button1.getY() + button1.getHeight() / 2F;
		float x2 = button2.getX() + button2.getWidth() / 2F;
		float y2 = button2.getY() + button2.getHeight() / 2F;
		return getAngleBetweenPoints(x1, y1, x2, y2);
	}

	public static float getDistanceBetweenButtons(Button button1, Button button2) {
		float x1 = button1.getX() + button1.getWidth() / 2F;
		float y1 = button1.getY() + button1.getHeight() / 2F;
		float x2 = button2.getX() + button2.getWidth() / 2F;
		float y2 = button2.getY() + button2.getHeight() / 2F;
		return getDistanceBetweenPoints(x1, y1, x2, y2);
	}

	public static float getDistanceBetweenPoints(float x1, float y1, float x2, float y2) {
		return Mth.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	public static float getAngleBetweenPoints(float x1, float y1, float x2, float y2) {
		return (float) Mth.atan2(y2 - y1, x2 - x1);
	}
}
