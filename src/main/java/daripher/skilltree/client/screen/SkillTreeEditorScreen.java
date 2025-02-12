package daripher.skilltree.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.SkillNodeEditor;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class SkillTreeEditorScreen extends Screen {
  private final PassiveSkillTree skillTree;
  private final SkillButtons skillButtons;
  private final SkillTreeEditor editorWidgets;
  private boolean shouldCloseOnEsc = true;
  private int prevMouseX;
  private int prevMouseY;

  public SkillTreeEditorScreen(ResourceLocation skillTreeId) {
    super(Component.empty());
    this.minecraft = Minecraft.getInstance();
    this.skillTree = SkillTreeClientData.getOrCreateEditorTree(skillTreeId);
    this.skillButtons = new SkillButtons(skillTree, () -> 0f);
    this.editorWidgets = new SkillTreeEditor(skillButtons);
  }

  @Override
  public void init() {
    if (skillTree == null) {
      getMinecraft().setScreen(null);
      return;
    }
    clearWidgets();
    skillButtons.setWidth(width);
    skillButtons.setHeight(height);
    editorWidgets.setWidth(210);
    editorWidgets.setHeight(10);
    editorWidgets.setX(width - editorWidgets.getWidth());
    editorWidgets.init();
    editorWidgets.increaseHeight(5);
    editorWidgets.setRebuildFunc(this::rebuildWidgets);
    skillButtons.setRebuildFunc(this::rebuildWidgets);
    skillButtons.clearWidgets();
    editorWidgets.getSkills()
        .forEach(editorWidgets::addSkillButton);
    skillButtons.updateSkillConnections();
    calculateMaxScroll();
    addRenderableWidget(skillButtons);
    addRenderableWidget(editorWidgets);
  }

  @Override
  protected void rebuildWidgets() {
    getMinecraft().tell(super::rebuildWidgets);
  }

  private void calculateMaxScroll() {
    skillButtons.setMaxScrollX(Math.min(0, width / 2 - 350));
    skillButtons.setMaxScrollY(Math.min(0, height / 2 - 350));
    skillButtons.getWidgets()
        .forEach(button -> {
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
    renderBackground(graphics);
    skillButtons.render(graphics, mouseX, mouseY, partialTick);
    renderOverlay(graphics);
    editorWidgets.render(graphics, mouseX, mouseY, partialTick);
    if (mouseX < editorWidgets.getX() || mouseY > editorWidgets.getHeight()) {
      float tooltipX = mouseX + (prevMouseX - mouseX) * partialTick;
      float tooltipY = mouseY + (prevMouseY - mouseY) * partialTick;
      skillButtons.renderTooltip(graphics, tooltipX, tooltipY);
    }
    prevMouseX = mouseX;
    prevMouseY = mouseY;
  }

  private void createBlankSkill() {
    ResourceLocation background = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/background/lesser.png");
    ResourceLocation icon = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/void.png");
    ResourceLocation border = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/tooltip/lesser.png");
    ResourceLocation skillId = SkillNodeEditor.createNewSkillId();
    PassiveSkill skill = new PassiveSkill(skillId, 16, background, icon, border, false);
    skill.setPosition(0, 0);
    SkillTreeClientData.saveEditorSkill(skill);
    SkillTreeClientData.loadEditorSkill(skill.getId());
    editorWidgets.getSkillTree()
        .getSkillIds()
        .add(skill.getId());
    SkillTreeClientData.saveEditorSkillTree(editorWidgets.getSkillTree());
  }

  @Override
  public boolean shouldCloseOnEsc() {
    if (!shouldCloseOnEsc) {
      shouldCloseOnEsc = true;
      return false;
    }
    return super.shouldCloseOnEsc();
  }

  @Override
  public void tick() {
    editorWidgets.onWidgetTick();
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
    poseStack.translate(skillButtons.getScrollX() / 3F, skillButtons.getScrollY() / 3F, 0);
    int size = SkillTreeScreen.BACKGROUND_SIZE;
    graphics.blit(texture, (width - size) / 2, (height - size) / 2, 0, 0F, 0F, size, size, size, size);
    poseStack.popPose();
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    return editorWidgets.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    return editorWidgets.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    return editorWidgets.mouseScrolled(mouseX, mouseY, amount) || skillButtons.mouseScrolled(mouseX, mouseY, amount);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
    return editorWidgets.mouseDragged(mouseX, mouseY, button, dragX, dragY) | skillButtons.mouseDragged(mouseX, mouseY, button, dragX, dragY);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (editorWidgets.keyPressed(keyCode, scanCode, modifiers)) {
      if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
        shouldCloseOnEsc = false;
      }
      return true;
    }
    if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
      if (shouldCloseOnEsc()) {
        onClose();
        return true;
      }
    }
    if (keyCode == GLFW.GLFW_KEY_N && Screen.hasControlDown()) {
      createBlankSkill();
      rebuildWidgets();
      return true;
    }
    return false;
  }

  @Override
  public boolean charTyped(char codePoint, int modifiers) {
    return editorWidgets.charTyped(codePoint, modifiers);
  }
}
