package daripher.skilltree.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.client.widget.SkillTreeWidgets;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.stream.Stream;

public class SkillTreeScreen extends Screen {
  public static final int BACKGROUND_SIZE = 2048;
  private final PassiveSkillTree skillTree;
  private final SkillButtons skills;
  private final SkillTreeWidgets skillTreeWidgets;
  public float renderAnimation;
  private int prevMouseX;
  private int prevMouseY;

  public SkillTreeScreen(ResourceLocation skillTreeId) {
    super(Component.empty());
    this.skillTree = SkillTreesReloader.getSkillTreeById(skillTreeId);
    this.minecraft = Minecraft.getInstance();
    this.skills = new SkillButtons(skillTree, () -> renderAnimation);
    this.skillTreeWidgets = new SkillTreeWidgets(getLocalPlayer(), skills, skillTree);
    this.skills.setRebuildFunc(this::rebuildWidgets);
    this.skillTreeWidgets.setRebuildFunc(this::rebuildWidgets);
  }

  @Override
  public void init() {
    clearWidgets();
    skillTreeWidgets.clearWidgets();
    skillTreeWidgets.setWidth(width);
    skillTreeWidgets.setHeight(height);
    skills.setWidth(width);
    skills.setHeight(height);
    skills.clearWidgets();
    getTreeSkills().forEach(skill -> skillTreeWidgets.addSkillButton(skill, () -> renderAnimation));
    skills.updateSkillConnections();
    skillTreeWidgets.init();
    calculateMaxScroll();
    addRenderableWidget(skillTreeWidgets);
    addRenderableWidget(skills);
  }

  private Stream<PassiveSkill> getTreeSkills() {
    return skillTree.getSkillIds()
        .stream()
        .map(SkillsReloader::getSkillById);
  }

  @Override
  protected void rebuildWidgets() {
    getMinecraft().tell(super::rebuildWidgets);
  }

  private void calculateMaxScroll() {
    skills.setMaxScrollX(Math.min(0, width / 2 - 350));
    skills.setMaxScrollY(Math.min(0, height / 2 - 350));
    skills.getWidgets()
        .forEach(button -> {
          float skillX = button.skill.getPositionX();
          float skillY = button.skill.getPositionY();
          int maxScrollX = (int) Math.max(skills.getMaxScrollX(), Mth.abs(skillX));
          int maxScrollY = (int) Math.max(skills.getMaxScrollY(), Mth.abs(skillY));
          skills.setMaxScrollX(maxScrollX);
          skills.setMaxScrollY(maxScrollY);
        });
  }

  @Override
  public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    renderAnimation += partialTick;
    renderBackground(graphics);
    skills.render(graphics, mouseX, mouseY, partialTick);
    renderOverlay(graphics);
    skillTreeWidgets.render(graphics, mouseX, mouseY, partialTick);
    float tooltipX = mouseX + (prevMouseX - mouseX) * partialTick;
    float tooltipY = mouseY + (prevMouseY - mouseY) * partialTick;
    skills.renderTooltip(graphics, tooltipX, tooltipY);
    prevMouseX = mouseX;
    prevMouseY = mouseY;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (skillTreeWidgets.mouseClicked(mouseX, mouseY, button)) {
      return true;
    }
    return skills.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public void tick() {
    skillTreeWidgets.onWidgetTick();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (skillTreeWidgets.keyPressed(keyCode, scanCode, modifiers)) {
      return true;
    }
    if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
      onClose();
      return true;
    }
    return false;
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    return skillTreeWidgets.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char character, int keyCode) {
    return skillTreeWidgets.charTyped(character, keyCode);
  }

  private void renderOverlay(GuiGraphics graphics) {
    ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/skill_tree_overlay.png");
    RenderSystem.enableBlend();
    graphics.blit(texture, 0, 0, 0, 0F, 0F, width, height, width, height);
    RenderSystem.disableBlend();
  }

  @Override
  public void renderBackground(GuiGraphics graphics) {
    ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/skill_tree_background.png");
    PoseStack poseStack = graphics.pose();
    poseStack.pushPose();
    poseStack.translate(skills.getScrollX() / 3F, skills.getScrollY() / 3F, 0);
    int size = BACKGROUND_SIZE;
    graphics.blit(texture, (width - size) / 2, (height - size) / 2, 0, 0F, 0F, size, size, size, size);
    poseStack.popPose();
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragAmountX, double dragAmountY) {
    return skills.mouseDragged(mouseX, mouseY, mouseButton, dragAmountX, dragAmountY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    return skills.mouseScrolled(mouseX, mouseY, amount);
  }

  private @Nonnull LocalPlayer getLocalPlayer() {
    return Objects.requireNonNull(getMinecraft().player);
  }

  public void updateSkillPoints(int skillPoints) {
    skillTreeWidgets.updateSkillPoints(skillPoints);
  }
}
