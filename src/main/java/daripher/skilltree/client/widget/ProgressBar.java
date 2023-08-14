package daripher.skilltree.client.widget;

import com.mojang.blaze3d.vertex.PoseStack;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.ScreenHelper;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ProgressBar extends Button {
	public ProgressBar(int x, int y) {
		super(x, y, 235, 19, Component.empty(), b -> {
		});
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		renderBackground(poseStack);
		renderCurrentLevel(poseStack);
		renderNextLevel(poseStack);
		renderProgress(poseStack);
	}

	protected void renderBackground(PoseStack poseStack) {
		var experienceProgress = getExperienceProgress();
		var filledBarWidth = (int) (experienceProgress * 183);
		SkillTreeScreen.prepareTextureRendering(SkillTreeScreen.WIDGETS_TEXTURE);
		blit(poseStack, x + 26, y + 7, 0, 0, 182, 5);
		if (filledBarWidth == 0) return;
		blit(poseStack, x + 26, y + 7, 0, 5, filledBarWidth, 5);
	}

	protected void renderProgress(PoseStack poseStack) {
		var experienceProgress = getExperienceProgress();
		var percentageText = "" + (int) (experienceProgress * 100) + "%";
		ScreenHelper.drawCenteredOutlinedText(poseStack, percentageText, getMinecraftFont(), x + width / 2, getTextY(), 0xFCE266);
	}

	protected void renderNextLevel(PoseStack poseStack) {
		var currentLevel = getCurrentLevel();
		if (isMaxLevel(currentLevel)) currentLevel--;
		var nextLevel = currentLevel + 1;
		ScreenHelper.drawCenteredOutlinedText(poseStack, "" + nextLevel, getMinecraftFont(), x + width - 17, getTextY(), 0xFCE266);
	}

	protected void renderCurrentLevel(PoseStack poseStack) {
		var currentLevel = getCurrentLevel();
		if (isMaxLevel(currentLevel)) currentLevel--;
		ScreenHelper.drawCenteredOutlinedText(poseStack, "" + currentLevel, getMinecraftFont(), x + 17, getTextY(), 0xFCE266);
	}

	protected int getTextY() {
		return y + 6;
	}

	private static int getCurrentLevel() {
		var minecraft = Minecraft.getInstance();
		var skillsCapability = PlayerSkillsProvider.get(minecraft.player);
		var learnedSkills = skillsCapability.getPlayerSkills().size();
		var skillPoints = skillsCapability.getSkillPoints();
		var currentLevel = learnedSkills + skillPoints;
		return currentLevel;
	}

	private static boolean isMaxLevel(int currentLevel) {
		var levelupCosts = Config.level_up_costs;
		return currentLevel >= levelupCosts.size();
	}

	private float getExperienceProgress() {
		var minecraft = Minecraft.getInstance();
		var skillsCapability = PlayerSkillsProvider.get(minecraft.player);
		var currentLevel = skillsCapability.getPlayerSkills().size() + skillsCapability.getSkillPoints();
		var levelupCosts = Config.level_up_costs;
		var experienceProgress = 1F;
		if (currentLevel < levelupCosts.size()) {
			var levelupCost = levelupCosts.get(currentLevel);
			experienceProgress = (float) minecraft.player.totalExperience / levelupCost;
			experienceProgress = Math.min(1F, experienceProgress);
		}
		return experienceProgress;
	}

	protected Font getMinecraftFont() {
		var minecraft = Minecraft.getInstance();
		return minecraft.font;
	}
}
