package daripher.skilltree.client.widget;

import com.mojang.blaze3d.vertex.PoseStack;

import daripher.skilltree.client.screen.SkillTreeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SkillTreeButton extends Button {
	public SkillTreeButton(int x, int y, int width, int height, Component message, OnPress pressFunction) {
		super(x, y, width, height, message, pressFunction, (b, s, mx, my) -> {});
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		SkillTreeScreen.prepareTextureRendering(SkillTreeScreen.WIDGETS_LOCATION);
		int v = !isActive() ? 24 : isHoveredOrFocused() ? 38 : 10;
		blit(poseStack, x, y, 0, v, width, height);
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		int textColor = getFGColor();
		drawCenteredString(poseStack, font, getMessage(), x + width / 2, y + (height - 8) / 2, textColor | Mth.ceil(alpha * 255F) << 24);
	}
}
