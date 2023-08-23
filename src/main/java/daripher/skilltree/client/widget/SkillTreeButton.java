package daripher.skilltree.client.widget;

import com.mojang.blaze3d.vertex.PoseStack;

import daripher.skilltree.client.screen.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SkillTreeButton extends Button {
	public SkillTreeButton(int x, int y, int width, int height, Component message, OnPress pressFunction) {
		super(x, y, width, height, message, pressFunction, (b, s, mx, my) -> {
		});
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		ScreenHelper.prepareTextureRendering(new ResourceLocation("skilltree:textures/screen/widgets/buttons.png"));
		int v = !isActive() ? 0 : isHoveredOrFocused() ? 28 : 14;
		blit(poseStack, x, y, 0, v, width / 2, height);
		blit(poseStack, x + width / 2, y, 140 - width / 2, v, width / 2, height);
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		int textColor = getFGColor();
		drawCenteredString(poseStack, font, getMessage(), x + width / 2, y + (height - 8) / 2, textColor | Mth.ceil(alpha * 255F) << 24);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return visible && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}
}
