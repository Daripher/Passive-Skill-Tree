package daripher.skilltree.client.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.ScreenHelper;
import daripher.skilltree.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ProgressBar extends Button {
  public boolean showProgressInNumbers;

  public ProgressBar(int x, int y, OnPress pressFunc) {
    super(x, y, 235, 19, Component.empty(), pressFunc);
  }

  private static int getCurrentLevel() {
    IPlayerSkills capability = PlayerSkillsProvider.get(getLocalPlayer());
    int skills = capability.getPlayerSkills().size();
    int points = capability.getSkillPoints();
    return skills + points;
  }

  private static boolean isMaxLevel(int currentLevel) {
    return currentLevel >= Config.max_skill_points;
  }

  @Override
  public void renderButton(
      @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    renderBackground(poseStack);
    renderCurrentLevel(poseStack);
    renderNextLevel(poseStack);
    renderProgress(poseStack);
  }

  protected void renderBackground(PoseStack poseStack) {
    float experienceProgress = getExperienceProgress();
    int filledBarWidth = (int) (experienceProgress * 183);
    ScreenHelper.prepareTextureRendering(
        new ResourceLocation("skilltree:textures/screen/progress_bars.png"));
    blit(poseStack, x + 26, y + 7, 0, 0, 182, 5);
    if (filledBarWidth == 0) return;
    blit(poseStack, x + 26, y + 7, 0, 5, filledBarWidth, 5);
  }

  protected void renderProgress(PoseStack poseStack) {
    if (showProgressInNumbers) {
      int cost = Config.getSkillPointCost(getCurrentLevel());
      int exp = getLocalPlayer().totalExperience;
      String text = exp + "/" + cost;
      ScreenHelper.drawCenteredOutlinedText(poseStack, text, x + width / 2, getTextY(), 0xFCE266);
    } else {
      float experienceProgress = getExperienceProgress();
      String text = (int) (experienceProgress * 100) + "%";
      ScreenHelper.drawCenteredOutlinedText(poseStack, text, x + width / 2, getTextY(), 0xFCE266);
    }
  }

  protected void renderNextLevel(PoseStack poseStack) {
    int currentLevel = getCurrentLevel();
    if (isMaxLevel(currentLevel)) currentLevel--;
    int nextLevel = currentLevel + 1;
    ScreenHelper.drawCenteredOutlinedText(
        poseStack, "" + nextLevel, x + width - 17, getTextY(), 0xFCE266);
  }

  protected void renderCurrentLevel(PoseStack poseStack) {
    int currentLevel = getCurrentLevel();
    if (isMaxLevel(currentLevel)) currentLevel--;
    ScreenHelper.drawCenteredOutlinedText(
        poseStack, "" + currentLevel, x + 17, getTextY(), 0xFCE266);
  }

  protected int getTextY() {
    return y + 5;
  }

  private float getExperienceProgress() {
    int level = getCurrentLevel();
    float progress = 1F;
    if (level < Config.max_skill_points) {
      int levelupCost = Config.getSkillPointCost(level);
      progress = (float) getLocalPlayer().totalExperience / levelupCost;
      progress = Math.min(1F, progress);
    }
    return progress;
  }

  private static LocalPlayer getLocalPlayer() {
    return Minecraft.getInstance().player;
  }
}
