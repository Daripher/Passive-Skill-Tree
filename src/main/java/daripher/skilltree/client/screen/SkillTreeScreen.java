package daripher.skilltree.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.client.widget.SkillTreeWidgets;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import daripher.skilltree.client.screen.StatsUpdateListener;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class SkillTreeScreen extends Screen implements StatsUpdateListener {
  public static final int BACKGROUND_SIZE = 2048;
  private final PassiveSkillTree skillTree;
  private final SkillButtons skillButtons;
  private final SkillTreeWidgets skillTreeWidgets;
  public float renderAnimation;
  private int prevMouseX;
  private int prevMouseY;
  private boolean statsUpdated;

  public SkillTreeScreen(ResourceLocation skillTreeId) {
    super(Component.empty());
    this.skillTree = SkillTreesReloader.getSkillTreeById(skillTreeId);
    this.minecraft = Minecraft.getInstance();
    this.skillButtons = new SkillButtons(skillTree, () -> renderAnimation);
    this.skillTreeWidgets = new SkillTreeWidgets(getLocalPlayer(), skillButtons, skillTree);
    this.skillButtons.setRebuildFunc(this::rebuildWidgets);
    this.skillTreeWidgets.setRebuildFunc(this::rebuildWidgets);
  }

  @Override
  public void init() {
    if (!statsUpdated) {
      ClientPacketListener connection = getMinecraft().getConnection();
      Objects.requireNonNull(connection);
      connection.send(
          new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
    }
    clearWidgets();
    skillTreeWidgets.clearWidgets();
    skillTreeWidgets.setWidth(width);
    skillTreeWidgets.setHeight(height);
    skillButtons.setWidth(width);
    skillButtons.setHeight(height);
    skillButtons.clearWidgets();
    addSkillButtons();
    skillTreeWidgets.init();
    calculateMaxScroll();
    addRenderableWidget(skillTreeWidgets);
    addRenderableWidget(skillButtons);
  }

  private void addSkillButtons() {
    Stream<PassiveSkill> passiveSkills =
        skillTree.getSkillIds().stream().map(SkillsReloader::getSkillById).filter(Objects::nonNull);
    passiveSkills.forEach(skill -> skillTreeWidgets.addSkillButton(skill, () -> renderAnimation));
    skillButtons.updateSkillConnections();
  }

  @Override
  protected void rebuildWidgets() {
    getMinecraft().tell(super::rebuildWidgets);
  }

  private void calculateMaxScroll() {
    skillButtons.setMaxScrollX(Math.min(0, width / 2 - 350));
    skillButtons.setMaxScrollY(Math.min(0, height / 2 - 350));
    skillButtons
        .getWidgets()
        .forEach(
            button -> {
              float skillX = button.skill.getPositionX();
              float skillY = button.skill.getPositionY();
              int maxScrollX = (int) Math.max(skillButtons.getMaxScrollX(), Mth.abs(skillX));
              int maxScrollY = (int) Math.max(skillButtons.getMaxScrollY(), Mth.abs(skillY));
              skillButtons.setMaxScrollX(maxScrollX);
              skillButtons.setMaxScrollY(maxScrollY);
            });
  }

  @Override
  public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    renderAnimation += partialTick;
    renderBackground(graphics, mouseX, mouseY, partialTick);
    skillButtons.render(graphics, mouseX, mouseY, partialTick);
    renderOverlay(graphics);
    skillTreeWidgets.render(graphics, mouseX, mouseY, partialTick);
    float tooltipX = mouseX + (prevMouseX - mouseX) * partialTick;
    float tooltipY = mouseY + (prevMouseY - mouseY) * partialTick;
    skillButtons.renderTooltip(graphics, tooltipX, tooltipY);
    prevMouseX = mouseX;
    prevMouseY = mouseY;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (skillTreeWidgets.mouseClicked(mouseX, mouseY, button)) {
      return true;
    }
    return skillButtons.mouseClicked(mouseX, mouseY, button);
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
      if (SkillTreesReloader.getSkillTrees().size() == 1) {
        onClose();
      } else {
        getMinecraft().setScreen(new SkillTreeSelectionScreen());
      }
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
    ResourceLocation texture =
        ResourceLocation.parse("skilltree:textures/screen/skill_tree_overlay.png");
    RenderSystem.enableBlend();
    graphics.blit(texture, 0, 0, 0, 0F, 0F, width, height, width, height);
    RenderSystem.disableBlend();
  }

  @Override
  public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    ResourceLocation texture =
        ResourceLocation.parse("skilltree:textures/screen/skill_tree_background.png");
    PoseStack poseStack = graphics.pose();
    poseStack.pushPose();
    float x = skillButtons.getScrollX();
    float y = skillButtons.getScrollY();
    if (ClientConfig.skill_tree_background_parallax) {
      x /= 3f;
      y /= 3f;
    }
    poseStack.translate(x, y, 0);
    int size = BACKGROUND_SIZE;
    graphics.blit(
        texture, (width - size) / 2, (height - size) / 2, 0, 0F, 0F, size, size, size, size);
    poseStack.popPose();
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int mouseButton, double dragAmountX, double dragAmountY) {
    return skillButtons.mouseDragged(mouseX, mouseY, mouseButton, dragAmountX, dragAmountY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    return skillButtons.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
  }

  private @Nonnull LocalPlayer getLocalPlayer() {
    return Objects.requireNonNull(getMinecraft().player);
  }

  public void updateSkillPoints(int skillPoints) {
    skillTreeWidgets.updateSkillPoints(skillPoints);
  }

  @Override
  public void onStatsUpdated() {
    statsUpdated = true;
    init();
  }
}
