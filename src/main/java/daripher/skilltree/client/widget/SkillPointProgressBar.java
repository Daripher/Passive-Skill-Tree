package daripher.skilltree.client.widget;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.config.Config;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.GainSkillPointMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SkillPointProgressBar extends Button {
	private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/skill_tree_widgets.png");
	private static final Style EXPERIENCE_COLOR = Style.EMPTY.withColor(0xFCE266);
	private final SkillTreeScreen parentScreen;

	public SkillPointProgressBar(SkillTreeScreen parentScreen, int x, int y) {
		super(x, y, 235, 19, Component.empty(), SkillPointProgressBar::buttonPressed);
		this.parentScreen = parentScreen;
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		renderExperienceBar(poseStack);
		if (isHoveredOrFocused()) {
			renderToolTip(poseStack, mouseX, mouseY);
		}
	}

	protected void renderExperienceBar(PoseStack poseStack) {
		renderExperienceBarBackground(poseStack);
		renderCurrentLevel(poseStack);
		renderNextLevel(poseStack);
		renderExperienceProgress(poseStack);
	}

	protected void renderExperienceBarBackground(PoseStack poseStack) {
		var experienceProgress = getExperienceProgress();
		var filledBarWidth = (int) (experienceProgress * 183);
		parentScreen.prepareTextureRendering(WIDGETS_LOCATION);
		blit(poseStack, x + 26, y + 7, 0, 0, 182, 5);
		if (filledBarWidth == 0) {
			return;
		}
		blit(poseStack, x + 26, y + 7, 0, 5, filledBarWidth, 5);
	}

	protected void renderExperienceProgress(PoseStack poseStack) {
		var experienceProgress = getExperienceProgress();
		var percentageText = "" + (int) (experienceProgress * 100) + "%";
		drawCenteredOutlinedText(poseStack, percentageText, getMinecraftFont(), x + width / 2, getTextY());
	}

	protected void renderNextLevel(PoseStack poseStack) {
		var currentLevel = getCurrentLevel();
		if (isMaxLevel(currentLevel)) {
			currentLevel--;
		}
		var nextLevel = currentLevel + 1;
		drawCenteredOutlinedText(poseStack, "" + nextLevel, getMinecraftFont(), x + width - 17, getTextY());
	}

	protected void renderCurrentLevel(PoseStack poseStack) {
		var currentLevel = getCurrentLevel();
		if (isMaxLevel(currentLevel)) {
			currentLevel--;
		}
		drawCenteredOutlinedText(poseStack, "" + currentLevel, getMinecraftFont(), x + 17, getTextY());
	}

	@Override
	public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
		var borderStyleStack = new ItemStack(Items.STONE);
		var tooltip = new ArrayList<MutableComponent>();
		var minecraft = Minecraft.getInstance();
		var skillsCapability = PlayerSkillsProvider.get(minecraft.player);
		var skillPointsAvailable = skillsCapability.getSkillPoints();
		tooltip.add(Component.translatable("widget.skill_point_progress_bar.text").withStyle(ChatFormatting.GRAY));
		var skillPointsComponent = Component.literal("" + skillPointsAvailable).withStyle(EXPERIENCE_COLOR);
		tooltip.add(Component.translatable("widget.skill_point_progress_bar.points", skillPointsComponent).withStyle(ChatFormatting.GRAY));
		var currentLevel = getCurrentLevel();
		if (canBuySkillPoint(currentLevel)) {
			var levelupCost = getLevelupCost(currentLevel);
			var costComponent = Component.literal("" + levelupCost).withStyle(EXPERIENCE_COLOR);
			tooltip.add(Component.translatable("widget.skill_point_progress_bar.buy", costComponent).withStyle(ChatFormatting.GRAY));
		}
		parentScreen.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY, borderStyleStack);
	}

	private static void buttonPressed(Button button) {
		var currentLevel = getCurrentLevel();
		if (canBuySkillPoint(currentLevel)) {
			var minecraft = Minecraft.getInstance();
			var levelupCosts = Config.COMMON_CONFIG.getSkillPointCosts();
			NetworkDispatcher.network_channel.sendToServer(new GainSkillPointMessage());
			minecraft.player.giveExperiencePoints(-levelupCosts.get(currentLevel));
		}
	}

	private static boolean canBuySkillPoint(int currentLevel) {
		if (!Config.COMMON_CONFIG.experienceGainEnabled()) {
			return false;
		}
		if (isMaxLevel(currentLevel)) {
			return false;
		}
		var minecraft = Minecraft.getInstance();
		var levelupCosts = Config.COMMON_CONFIG.getSkillPointCosts();
		return minecraft.player.totalExperience >= levelupCosts.get(currentLevel);
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
		var levelupCosts = Config.COMMON_CONFIG.getSkillPointCosts();
		return currentLevel >= levelupCosts.size();
	}

	private float getExperienceProgress() {
		var minecraft = Minecraft.getInstance();
		var skillsCapability = PlayerSkillsProvider.get(minecraft.player);
		var currentLevel = skillsCapability.getPlayerSkills().size() + skillsCapability.getSkillPoints();
		var levelupCosts = Config.COMMON_CONFIG.getSkillPointCosts();
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

	private int getLevelupCost(int currentLevel) {
		var levelupCosts = Config.COMMON_CONFIG.getSkillPointCosts();
		return levelupCosts.get(currentLevel);
	}

	protected void drawCenteredOutlinedText(PoseStack poseStack, String text, Font font, int x, int y) {
		x -= font.width(text) / 2;
		font.draw(poseStack, text, x + 1, y, 0);
		font.draw(poseStack, text, x - 1, y, 0);
		font.draw(poseStack, text, x, y + 1, 0);
		font.draw(poseStack, text, x, y - 1, 0);
		font.draw(poseStack, text, x, y, 0xFCE266);
	}
}
