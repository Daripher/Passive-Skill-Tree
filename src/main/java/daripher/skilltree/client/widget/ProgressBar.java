package daripher.skilltree.client.widget;

import java.util.function.Supplier;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.ScreenHelper;
import daripher.skilltree.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ProgressBar extends Button {
	public boolean showProgressInNumbers;

	public ProgressBar(int x, int y, OnPress pressFunc) {
		super(x, y, 235, 19, Component.empty(), pressFunc, Supplier::get);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics);
		renderCurrentLevel(graphics);
		renderNextLevel(graphics);
		renderProgress(graphics);
	}

	protected void renderBackground(GuiGraphics graphics) {
		ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/widgets/progress_bars.png");
		float experienceProgress = getExperienceProgress();
		int filledBarWidth = (int) (experienceProgress * 183);
		graphics.blit(texture, getX() + 26, getY() + 7, 0, 0, 182, 5);
		if (filledBarWidth == 0) return;
		graphics.blit(texture, getX() + 26, getY() + 7, 0, 5, filledBarWidth, 5);
	}

	protected void renderProgress(GuiGraphics graphics) {
		if (showProgressInNumbers) {
			int cost = Config.getSkillPointCost(getCurrentLevel());
			int exp = Minecraft.getInstance().player.totalExperience;
			String text = "" + exp + "/" + cost;
			ScreenHelper.drawCenteredOutlinedText(graphics, text, Minecraft.getInstance().font, getX() + width / 2, getTextY(), 0xFCE266);
		} else {
			float experienceProgress = getExperienceProgress();
			String text = "" + (int) (experienceProgress * 100) + "%";
			ScreenHelper.drawCenteredOutlinedText(graphics, text, Minecraft.getInstance().font, getX() + width / 2, getTextY(), 0xFCE266);
		}
	}

	protected void renderNextLevel(GuiGraphics graphics) {
		int currentLevel = getCurrentLevel();
		if (isMaxLevel(currentLevel)) currentLevel--;
		int nextLevel = currentLevel + 1;
		ScreenHelper.drawCenteredOutlinedText(graphics, "" + nextLevel, Minecraft.getInstance().font, getX() + width - 17, getTextY(), 0xFCE266);
	}

	protected void renderCurrentLevel(GuiGraphics graphics) {
		int currentLevel = getCurrentLevel();
		if (isMaxLevel(currentLevel)) currentLevel--;
		ScreenHelper.drawCenteredOutlinedText(graphics, "" + currentLevel, Minecraft.getInstance().font, getX() + 17, getTextY(), 0xFCE266);
	}

	protected int getTextY() {
		return getY() + 5;
	}

	private static int getCurrentLevel() {
		Minecraft minecraft = Minecraft.getInstance();
		IPlayerSkills capability = PlayerSkillsProvider.get(minecraft.player);
		int skills = capability.getPlayerSkills().size();
		int points = capability.getSkillPoints();
		return skills + points;
	}

	private static boolean isMaxLevel(int currentLevel) {
		return currentLevel >= Config.max_skill_points;
	}

	private float getExperienceProgress() {
		int level = getCurrentLevel();
		float progress = 1F;
		if (level < Config.max_skill_points) {
			int levelupCost = Config.getSkillPointCost(level);
			progress = (float) Minecraft.getInstance().player.totalExperience / levelupCost;
			progress = Math.min(1F, progress);
		}
		return progress;
	}
}
