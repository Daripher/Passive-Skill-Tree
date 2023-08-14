package daripher.skilltree.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;

public class ScreenHelper {
	public static void drawCenteredOutlinedText(PoseStack poseStack, String text, Font font, int x, int y, int color) {
		x -= font.width(text) / 2;
		font.draw(poseStack, text, x + 1, y, 0);
		font.draw(poseStack, text, x - 1, y, 0);
		font.draw(poseStack, text, x, y + 1, 0);
		font.draw(poseStack, text, x, y - 1, 0);
		font.draw(poseStack, text, x, y, color);
	}
}
