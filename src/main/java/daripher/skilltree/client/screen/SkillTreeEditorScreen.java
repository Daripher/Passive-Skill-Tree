package daripher.skilltree.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.skill.SkillTreeClientData;
import daripher.skilltree.client.widget.*;
import daripher.skilltree.client.widget.Button;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class SkillTreeEditorScreen extends Screen implements SkillTreeEditor {
  private final Map<ResourceLocation, SkillButton> skillButtons = new HashMap<>();
  private final List<SkillConnection> skillConnections = new ArrayList<>();
  private final List<SkillConnection> gatewayConnections = new ArrayList<>();
  private final Set<ResourceLocation> selectedSkills = new HashSet<>();
  private final PassiveSkillTree skillTree;
  protected double scrollSpeedX;
  protected double scrollSpeedY;
  protected double scrollX;
  protected double scrollY;
  protected int maxScrollX;
  protected int maxScrollY;
  protected int toolsY;
  protected int toolsX;
  private boolean closeOnEsc = true;
  private int prevMouseX;
  private int prevMouseY;
  private float zoom = 1F;

  public SkillTreeEditorScreen(ResourceLocation skillTreeId) {
    super(Component.empty());
    this.skillTree = SkillTreeClientData.getOrCreateEditorTree(skillTreeId);
    this.minecraft = Minecraft.getInstance();
  }

  @Override
  public void init() {
    clearWidgets();
    addSkillButtons();
    maxScrollX -= width / 2 - 350;
    maxScrollY -= height / 2 - 350;
    if (maxScrollX < 0) maxScrollX = 0;
    if (maxScrollY < 0) maxScrollY = 0;
    addSkillConnections();
    addGatewayConnections();
    addToolButtons();
  }

  @Override
  public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    updateScreen(partialTick);
    renderBackground(poseStack);
    renderConnections(poseStack, mouseX, mouseY);
    renderSkills(poseStack, mouseX, mouseY, partialTick);
    renderOverlay(poseStack);
    renderWidgets(poseStack, mouseX, mouseY, partialTick);
    renderSkillTooltip(poseStack, mouseX, mouseY, partialTick);
    prevMouseX = mouseX;
    prevMouseY = mouseY;
  }

  private void renderWidgets(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    if (!selectedSkills.isEmpty()) {
      fill(poseStack, toolsX - 5, 0, width, toolsY, 0xDD000000);
    }
    for (Widget widget : renderables) {
      if (widget instanceof SkillButton) continue;
      widget.render(poseStack, mouseX, mouseY, partialTick);
    }
  }

  private void renderSkillTooltip(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    if (getWidgetAt(mouseX, mouseY).isPresent()) return;
    SkillButton skillAtMouse = getSkillAt(mouseX, mouseY);
    float tooltipX = ((prevMouseX - mouseX) * partialTick);
    float tooltipY = ((prevMouseY - mouseY) * partialTick);
    if (skillAtMouse == null) return;
    ScreenHelper.renderSkillTooltip(
        skillAtMouse, poseStack, tooltipX, tooltipY, width, height, itemRenderer);
  }

  private void renderSkills(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    poseStack.pushPose();
    poseStack.translate(scrollX, scrollY, 0);
    for (SkillButton widget : skillButtons.values()) {
      poseStack.pushPose();
      double widgetCenterX = widget.x + widget.getWidth() / 2f;
      double widgetCenterY = widget.y + widget.getHeight() / 2f;
      poseStack.translate(widgetCenterX, widgetCenterY, 0F);
      poseStack.scale(zoom, zoom, 1F);
      poseStack.translate(-widgetCenterX, -widgetCenterY, 0F);
      widget.render(poseStack, mouseX, mouseY, partialTick);
      if (selectedSkills.contains(widget.skill.getId())) {
        poseStack.pushPose();
        poseStack.translate(widget.x, widget.y, 0);
        renderSkillSelection(poseStack, widget);
        poseStack.popPose();
      }
      poseStack.popPose();
    }
    poseStack.popPose();
  }

  private void renderSkillSelection(PoseStack poseStack, SkillButton widget) {
    ScreenHelper.drawRectangle(
        poseStack, -1, -1, widget.getWidth() + 2, widget.getHeight() + 2, 0xAA32FF00);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (clickedOnEditBox(mouseX, mouseY, button)) return true;
    Optional<? extends GuiEventListener> widget = getWidgetAt(mouseX, mouseY);
    if (widget.isPresent()) return widget.get().mouseClicked(mouseX, mouseY, button);
    SkillButton skillAtMouse = getSkillAt(mouseX, mouseY);
    if (skillAtMouse == null) return false;
    return skillAtMouse.mouseClicked(skillAtMouse.x + 1, skillAtMouse.y + 1, button);
  }

  private boolean clickedOnEditBox(double mouseX, double mouseY, int button) {
    return editBoxesStream().anyMatch(b -> b.mouseClicked(mouseX, mouseY, button));
  }

  private Stream<EditBox> editBoxesStream() {
    return children().stream().filter(EditBox.class::isInstance).map(EditBox.class::cast);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_ESCAPE && !selectedSkills.isEmpty()) {
      selectedSkills.clear();
      rebuildWidgets();
      closeOnEsc = false;
      return true;
    }
    if (keyCode == GLFW.GLFW_KEY_DELETE && Screen.hasControlDown() && !selectedSkills.isEmpty()) {
      deleteSelectedSkills();
      return true;
    }
    if (keyCode == GLFW.GLFW_KEY_N && Screen.hasControlDown()) {
      createNewSkill(0, 0, null);
      rebuildWidgets();
      return true;
    }
    if (keyPressedOnTextBox(keyCode, scanCode, modifiers)) {
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  private boolean keyPressedOnTextBox(int keyCode, int scanCode, int modifiers) {
    return editBoxesStream().anyMatch(b -> b.keyPressed(keyCode, scanCode, modifiers));
  }

  @Override
  public boolean shouldCloseOnEsc() {
    if (!closeOnEsc) {
      closeOnEsc = true;
      return false;
    }
    return super.shouldCloseOnEsc();
  }

  private void deleteSelectedSkills() {
    getSelectedSkills()
        .forEach(
            skill -> {
              skillTree.getSkillIds().remove(skill.getId());
              SkillTreeClientData.deleteEditorSkill(skill);
              SkillTreeClientData.saveEditorSkillTree(skillTree);
            });
    selectedSkills.clear();
    rebuildWidgets();
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    editBoxesStream().forEach(b -> b.keyReleased(keyCode, scanCode, modifiers));
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char character, int keyCode) {
    editBoxesStream().forEach(b -> b.charTyped(character, keyCode));
    return super.charTyped(character, keyCode);
  }

  private Optional<? extends GuiEventListener> getWidgetAt(double mouseX, double mouseY) {
    return widgetsStream().filter(e -> e.isMouseOver(mouseX, mouseY)).findFirst();
  }

  private Stream<? extends GuiEventListener> widgetsStream() {
    Predicate<GuiEventListener> isSkillButton = SkillButton.class::isInstance;
    return children().stream().filter(isSkillButton.negate());
  }

  private @Nullable SkillButton getSkillAt(double mouseX, double mouseY) {
    mouseX -= scrollX;
    mouseY -= scrollY;
    for (SkillButton button : skillButtons.values()) {
      double skillSize = button.skill.getButtonSize() * zoom;
      double skillX = button.x + button.getWidth() / 2d - skillSize / 2;
      double skillY = button.y + button.getHeight() / 2d - skillSize / 2;
      if (mouseX >= skillX
          && mouseY >= skillY
          && mouseX < skillX + skillSize
          && mouseY < skillY + skillSize) {
        return button;
      }
    }
    return null;
  }

  private void addSkillButtons() {
    skillButtons.clear();
    getTreeSkills().forEach(this::addSkillButton);
  }

  private void addToolButtons() {
    toolsX = width - 379;
    toolsY = 0;
    if (!selectedSkills.isEmpty()) {
      toolsY += 5;
      addAttributeToolsButtons();
      addButtonToolsButtons();
      addTextureToolsButtons();
      addNodeToolsButtons();
    }
    if (selectedSkills.size() == 2) {
      addConnectionToolsButton();
    }
  }

  private void addNodeToolsButtons() {
    ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
    PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
    if (selectedSkills.size() == 1) {
      addLabel(toolsX, toolsY, "Attach New Skill", ChatFormatting.GREEN);
      toolsY += 19;
      addLabel(toolsX, toolsY, "• Distance", ChatFormatting.GOLD);
      addLabel(toolsX + 65, toolsY, "• Angle", ChatFormatting.GOLD);
      toolsY += 19;
      NumericTextField distanceEditor = new NumericTextField(font, toolsX, toolsY, 60, 14, 10);
      addRenderableWidget(distanceEditor);
      NumericTextField angleEditor = new NumericTextField(font, toolsX + 65, toolsY, 60, 14, 0);
      addRenderableWidget(angleEditor);
      Button addButton = new Button(toolsX + 130, toolsY, 60, 14, Component.literal("Add"));
      addButton.setPressFunc(b -> createNewSkill(angleEditor, distanceEditor, skill));
      addRenderableWidget(addButton);
      Button copyButton = new Button(toolsX + 195, toolsY, 60, 14, Component.literal("Copy"));
      copyButton.setPressFunc(b -> createSelectedSkillCopy(angleEditor, distanceEditor, skill));
      addRenderableWidget(copyButton);
      toolsY += 19;
    }
    addLabel(toolsX, toolsY, "To remove selected skills press CTRL+DELETE", ChatFormatting.RED);
    toolsY += 19;
  }

  private void createSelectedSkillCopy(
      NumericTextField angleEditor, NumericTextField distanceEditor, PassiveSkill skill) {
    float angle = (float) (angleEditor.getNumericValue() * Mth.PI / 180F);
    float distance = (float) distanceEditor.getNumericValue();
    distance += skill.getButtonSize() / 2f + 8;
    float skillX = skill.getPositionX() + Mth.sin(angle) * distance;
    float skillY = skill.getPositionY() + Mth.cos(angle) * distance;
    createCopiedSkill(skillX, skillY, skill);
    rebuildWidgets();
  }

  private void createNewSkill(
      NumericTextField angleEditor, NumericTextField distanceEditor, PassiveSkill skill) {
    float angle = (float) (angleEditor.getNumericValue() * Mth.PI / 180F);
    float distance = (float) distanceEditor.getNumericValue();
    distance += skill.getButtonSize() / 2f + 8;
    float skillX = skill.getPositionX() + Mth.sin(angle) * distance;
    float skillY = skill.getPositionY() + Mth.cos(angle) * distance;
    createNewSkill(skillX, skillY, skill);
    rebuildWidgets();
  }

  private void createCopiedSkill(float x, float y, PassiveSkill other) {
    PassiveSkill skill =
        new PassiveSkill(
            createNewSkillId(),
            other.getButtonSize(),
            other.getBackgroundTexture(),
            other.getIconTexture(),
            other.getBorderTexture(),
            other.isStartingPoint());
    skill.setPosition(x, y);
    skill.setConnectedTree(other.getConnectedTreeId());
    skill.setStartingPoint(other.isStartingPoint());
    other.getBonuses().stream().map(SkillBonus::copy).forEach(skill::addSkillBonus);
    skill.connect(other);
    SkillTreeClientData.saveEditorSkill(skill);
    SkillTreeClientData.loadEditorSkill(skill.getId());
    skillTree.getSkillIds().add(skill.getId());
    SkillTreeClientData.saveEditorSkillTree(skillTree);
  }

  private void createNewSkill(float x, float y, @Nullable PassiveSkill other) {
    ResourceLocation background =
        new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/background/lesser.png");
    ResourceLocation icon = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/void.png");
    ResourceLocation border =
        new ResourceLocation(SkillTreeMod.MOD_ID, "textures/tooltip/lesser.png");
    PassiveSkill skill = new PassiveSkill(createNewSkillId(), 16, background, icon, border, false);
    skill.setPosition(x, y);
    if (other != null) skill.connect(other);
    SkillTreeClientData.saveEditorSkill(skill);
    SkillTreeClientData.loadEditorSkill(skill.getId());
    skillTree.getSkillIds().add(skill.getId());
    SkillTreeClientData.saveEditorSkillTree(skillTree);
  }

  private ResourceLocation createNewSkillId() {
    ResourceLocation id;
    int counter = 1;
    do {
      id = new ResourceLocation("skilltree", "new_skill_" + counter++);
    } while (SkillTreeClientData.getEditorSkill(id) != null);
    return id;
  }

  private void addButtonToolsButtons() {
    ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
    PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
    if (getSelectedSkills().allMatch(otherSkill -> sameSize(skill, otherSkill))) {
      addLabel(toolsX, toolsY, "• Size", ChatFormatting.GOLD);
      toolsY += 19;
      NumericTextField sizeEditor =
          new NumericTextField(font, toolsX, toolsY, 40, 14, skill.getButtonSize());
      sizeEditor.setNumericFilter(d -> d >= 2);
      sizeEditor.setResponder(s -> skillSizeEditorChanged(sizeEditor));
      addRenderableWidget(sizeEditor);
      toolsY += 19;
    }
    if (selectedSkills.size() == 1) {
      toolsY -= 38;
      addLabel(toolsX + 65, toolsY, "• Position", ChatFormatting.GOLD);
      toolsY += 19;
      NumericTextField xPosEditor =
          new NumericTextField(font, toolsX + 65, toolsY, 60, 14, skill.getPositionX());
      xPosEditor.setResponder(s -> skillXPosEditorChanged(skill, xPosEditor));
      addRenderableWidget(xPosEditor);
      NumericTextField yPosEditor =
          new NumericTextField(font, toolsX + 130, toolsY, 60, 14, skill.getPositionY());
      yPosEditor.setResponder(s -> skillYPosEditorChanged(skill, yPosEditor));
      addRenderableWidget(yPosEditor);
      toolsY += 19;
    }
  }

  private void skillXPosEditorChanged(PassiveSkill skill, NumericTextField xPosEditor) {
    skill.setPosition((float) xPosEditor.getNumericValue(), skill.getPositionY());
    getSelectedSkillButtons()
        .forEach(button -> button.setPosition(getSkillButtonX(skill), getSkillButtonY(skill)));
    saveSelectedSkills();
  }

  private void skillYPosEditorChanged(PassiveSkill skill, NumericTextField yPosEditor) {
    skill.setPosition(skill.getPositionX(), (float) yPosEditor.getNumericValue());
    getSelectedSkillButtons()
        .forEach(button -> button.setPosition(getSkillButtonX(skill), getSkillButtonY(skill)));
    saveSelectedSkills();
  }

  private void skillSizeEditorChanged(NumericTextField editor) {
    getSelectedSkills().forEach(skill -> skill.setButtonSize((int) editor.getNumericValue()));
    getSelectedSkillButtons()
        .forEach(button -> button.setButtonSize((int) editor.getNumericValue()));
    saveSelectedSkills();
  }

  private void addAttributeToolsButtons() {
    ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
    PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
    if (getSelectedSkills().allMatch(otherSkill -> sameSkillBonuses(skill, otherSkill))) {
      addLabel(toolsX, toolsY, "• Skill Bonuses", ChatFormatting.GOLD);
      toolsY += 19;
      for (int row = 0; row < skill.getBonuses().size(); row++) {
        addSkillBonusToolWidgets(skill, row);
      }
      addAddBonusButton();
      toolsY += 19;
    }
  }

  private void addTextureToolsButtons() {
    ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
    PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
    if (getSelectedSkills().allMatch(otherSkill -> sameTextures(skill, otherSkill))) {
      addLabel(toolsX, toolsY, "Textures", ChatFormatting.GREEN);
      toolsY += 19;
      addLabel(toolsX, toolsY, "• Border", ChatFormatting.GOLD);
      toolsY += 19;
      TextField backgroundEditor =
          new TextField(font, toolsX, toolsY, 355, 14, skill.getBackgroundTexture().toString());
      backgroundEditor.setResponder(
          value -> {
            getSelectedSkills().forEach(s -> s.setBackgroundTexture(new ResourceLocation(value)));
            saveSelectedSkills();
          });
      addRenderableWidget(backgroundEditor);
      toolsY += 19;
      addLabel(toolsX, toolsY, "• Icon", ChatFormatting.GOLD);
      toolsY += 19;
      TextField iconEditor =
          new TextField(font, toolsX, toolsY, 355, 14, skill.getIconTexture().toString());
      iconEditor.setResponder(
          value -> {
            getSelectedSkills().forEach(s -> s.setIconTexture(new ResourceLocation(value)));
            saveSelectedSkills();
          });
      addRenderableWidget(iconEditor);
      toolsY += 19;
      addLabel(toolsX, toolsY, "• Tooltip Background", ChatFormatting.GOLD);
      toolsY += 19;
      TextField borderEditor =
          new TextField(font, toolsX, toolsY, 355, 14, skill.getBorderTexture().toString());
      borderEditor.setResponder(
          value -> {
            getSelectedSkills().forEach(s -> s.setBorderTexture(new ResourceLocation(value)));
            saveSelectedSkills();
          });
      addRenderableWidget(borderEditor);
      toolsY += 19;
    }
  }

  private Stream<SkillButton> getSelectedSkillButtons() {
    return selectedSkills.stream().map(skillButtons::get);
  }

  private void addAddBonusButton() {
    Button addModifierButton =
        new Button(toolsX, toolsY, 100, 14, Component.translatable("Add Bonus"));
    addModifierButton.setPressFunc(
        b ->
            getSelectedSkills()
                .forEach(
                    skill -> {
                      skill
                          .getBonuses()
                          .add(
                              new AttributeBonus(
                                  Attributes.ARMOR,
                                  new AttributeModifier(
                                      UUID.randomUUID(), "SkillTree", 1, Operation.ADDITION)));
                      SkillTreeClientData.saveEditorSkill(skill);
                      rebuildWidgets();
                    }));
    addRenderableWidget(addModifierButton);
  }

  private void addSkillBonusToolWidgets(PassiveSkill skill, int row) {
    skill.getBonuses().get(row).addEditorWidgets(this, row);
  }

  private void addConnectionToolsButton() {
    addLabel(toolsX, toolsY, "• Connections", ChatFormatting.GOLD);
    toolsY += 19;
    ResourceLocation first = (ResourceLocation) selectedSkills.toArray()[0];
    ResourceLocation second = (ResourceLocation) selectedSkills.toArray()[1];
    PassiveSkill firstSkill = SkillTreeClientData.getEditorSkill(first);
    PassiveSkill secondSkill = SkillTreeClientData.getEditorSkill(second);
    if (areSkillsConnected(firstSkill, secondSkill)) {
      Button disconnectButton =
          new Button(toolsX, toolsY, 100, 14, Component.literal("Disconnect"));
      addRenderableWidget(disconnectButton);
      disconnectButton.setPressFunc(
          b -> {
            firstSkill.getConnectedSkills().remove(second);
            firstSkill.getConnectedAsGateways().remove(second);
            secondSkill.getConnectedSkills().remove(first);
            secondSkill.getConnectedAsGateways().remove(first);
            saveSelectedSkills();
            rebuildWidgets();
          });
    } else {
      Button connectButton = new Button(toolsX, toolsY, 110, 14, Component.literal("Connect"));
      addRenderableWidget(connectButton);
      connectButton.setPressFunc(
          b -> {
            skillButtons.get(first).skill.getConnectedSkills().add(second);
            saveSelectedSkills();
            rebuildWidgets();
          });
      Button connectAsGatewayButton =
          new Button(toolsX + 115, toolsY, 110, 14, Component.literal("Connect as Gateway"));
      connectAsGatewayButton.setPressFunc(
          b -> {
            skillButtons.get(first).skill.getConnectedAsGateways().add(second);
            saveSelectedSkills();
            rebuildWidgets();
          });
      addRenderableWidget(connectAsGatewayButton);
    }
    toolsY += 19;
  }

  private boolean areSkillsConnected(PassiveSkill first, PassiveSkill second) {
    return first.getConnectedSkills().contains(second.getId())
        || second.getConnectedSkills().contains(first.getId())
        || first.getConnectedAsGateways().contains(second.getId())
        || second.getConnectedAsGateways().contains(first.getId());
  }

  private void saveSelectedSkills() {
    selectedSkills.stream()
        .map(skillButtons::get)
        .map(button -> button.skill)
        .forEach(SkillTreeClientData::saveEditorSkill);
  }

  protected void addSkillButton(PassiveSkill skill) {
    float skillX = skill.getPositionX();
    float skillY = skill.getPositionY();
    SkillButton button =
        new SkillButton(
            () -> 0F, getSkillButtonX(skill), getSkillButtonY(skill), skill, this::buttonPressed);
    addRenderableWidget(button);
    button.highlighted = true;
    skillButtons.put(skill.getId(), button);
    if (maxScrollX < Mth.abs(skillX)) maxScrollX = (int) Mth.abs(skillX);
    if (maxScrollY < Mth.abs(skillY)) maxScrollY = (int) Mth.abs(skillY);
  }

  private float getSkillButtonX(PassiveSkill skill) {
    float skillX = skill.getPositionX();
    return skillX - skill.getButtonSize() / 2F + width / 2F + skillX * (zoom - 1);
  }

  private float getSkillButtonY(PassiveSkill skill) {
    float skillY = skill.getPositionY();
    return skillY - skill.getButtonSize() / 2F + height / 2F + skillY * (zoom - 1);
  }

  public void addSkillConnections() {
    skillConnections.clear();
    getTreeSkills().forEach(this::addSkillConnections);
  }

  public void addGatewayConnections() {
    gatewayConnections.clear();
    getTreeSkills().forEach(this::addGatewayConnections);
  }

  private Stream<PassiveSkill> getTreeSkills() {
    return skillTree.getSkillIds().stream().map(SkillTreeClientData::getEditorSkill);
  }

  private void addSkillConnections(PassiveSkill skill) {
    for (ResourceLocation connectedSkillId : new ArrayList<>(skill.getConnectedSkills())) {
      if (SkillTreeClientData.getEditorSkill(connectedSkillId) == null) {
        skill.getConnectedSkills().remove(connectedSkillId);
        SkillTreeClientData.saveEditorSkill(skill);
        continue;
      }
      connectSkills(skillConnections, skill.getId(), connectedSkillId);
    }
  }

  private void addGatewayConnections(PassiveSkill skill) {
    skill
        .getConnectedAsGateways()
        .forEach(
            connectedSkillId -> connectSkills(gatewayConnections, skill.getId(), connectedSkillId));
  }

  protected void connectSkills(
      List<SkillConnection> connections, ResourceLocation skillId1, ResourceLocation skillId2) {
    SkillButton button1 = skillButtons.get(skillId1);
    SkillButton button2 = skillButtons.get(skillId2);
    connections.add(new SkillConnection(button1, button2));
  }

  public void buttonPressed(net.minecraft.client.gui.components.Button button) {
    if (button instanceof SkillButton skillButton) skillButtonPressed(skillButton);
  }

  protected void skillButtonPressed(SkillButton button) {
    if (!hasShiftDown() && !selectedSkills.isEmpty()) {
      selectedSkills.clear();
    }
    ResourceLocation skillId = button.skill.getId();
    if (selectedSkills.contains(skillId)) selectedSkills.remove(skillId);
    else selectedSkills.add(skillId);
    rebuildWidgets();
  }

  private void updateScreen(float partialTick) {
    scrollX += scrollSpeedX * partialTick;
    scrollX = Math.max(-maxScrollX * zoom, Math.min(maxScrollX * zoom, scrollX));
    scrollSpeedX *= 0.8;
    scrollY += scrollSpeedY * partialTick;
    scrollY = Math.max(-maxScrollY * zoom, Math.min(maxScrollY * zoom, scrollY));
    scrollSpeedY *= 0.8;
  }

  private void renderOverlay(PoseStack poseStack) {
    ScreenHelper.prepareTextureRendering(
        new ResourceLocation("skilltree:textures/screen/skill_tree_overlay.png"));
    blit(poseStack, 0, 0, 0, 0F, 0F, width, height, width, height);
  }

  @Override
  public void renderBackground(PoseStack poseStack) {
    ScreenHelper.prepareTextureRendering(
        new ResourceLocation("skilltree:textures/screen/skill_tree_background.png"));
    poseStack.pushPose();
    poseStack.translate(scrollX / 3F, scrollY / 3F, 0);
    int size = SkillTreeScreen.BACKGROUND_SIZE;
    blit(poseStack, (width - size) / 2, (height - size) / 2, 0, 0F, 0F, size, size, size, size);
    poseStack.popPose();
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int mouseButton, double dragAmountX, double dragAmountY) {
    if (mouseButton != 0 && mouseButton != 2) return false;
    if (maxScrollX > 0) scrollSpeedX += dragAmountX * 0.25;
    if (maxScrollY > 0) scrollSpeedY += dragAmountY * 0.25;
    return true;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    if (getWidgetAt(mouseX, mouseY).filter(SkillBonusList.class::isInstance).isEmpty()) {
      if (amount > 0 && zoom < 2F) zoom += 0.05f;
      if (amount < 0 && zoom > 0.25F) zoom -= 0.05f;
      rebuildWidgets();
    }
    return super.mouseScrolled(mouseX, mouseY, amount);
  }

  @Override
  public Stream<PassiveSkill> getSelectedSkills() {
    return selectedSkills.stream().map(SkillTreeClientData::getEditorSkill);
  }

  @Override
  public void rebuildWidgets() {
    super.rebuildWidgets();
  }

  @Override
  public void shiftWidgets(int x, int y) {
    toolsX += x;
    toolsY += y;
  }

  @Override
  public TextField addTextField(int x, int y, int width, int height, String defaultValue) {
    assert minecraft != null;
    return addRenderableWidget(
        new TextField(minecraft.font, toolsX + x, toolsY + y, width, height, defaultValue));
  }

  @Override
  public NumericTextField addNumericTextField(
      int x, int y, int width, int height, double defaultValue) {
    assert minecraft != null;
    return addRenderableWidget(
        new NumericTextField(minecraft.font, toolsX + x, toolsY + y, width, height, defaultValue));
  }

  @Override
  public Button addRemoveButton(int x, int y, int width, int height) {
    return addRenderableWidget(new RemoveButton(toolsX + x, toolsY + y, width, height));
  }

  @Override
  public Label addLabel(int x, int y, String text, ChatFormatting... styles) {
    MutableComponent message = Component.literal(text);
    for (ChatFormatting style : styles) {
      message.withStyle(style);
    }
    return addRenderableOnly(new Label(x, y, message));
  }

  @Override
  public <T extends Enum<T>> EnumCycleButton<T> addEnumCycleButton(
      int x, int y, int width, int height, T defaultValue) {
    return addRenderableWidget(
        new EnumCycleButton<>(toolsX + x, toolsY + y, width, height, defaultValue));
  }

  protected void renderConnections(PoseStack poseStack, int mouseX, int mouseY) {
    ScreenHelper.prepareTextureRendering(
        new ResourceLocation("skilltree:textures/screen/skill_connection.png"));
    skillConnections.forEach(connection -> renderConnection(poseStack, connection));
    ScreenHelper.prepareTextureRendering(
        new ResourceLocation("skilltree:textures/screen/gateway_connection.png"));
    gatewayConnections.forEach(
        connection -> renderGatewayConnection(poseStack, connection, mouseX, mouseY));
  }

  private void renderGatewayConnection(
      PoseStack poseStack, SkillConnection connection, int mouseX, int mouseY) {
    SkillButton button1 = connection.getFirstButton();
    SkillButton button2 = connection.getSecondButton();
    SkillButton skillAtMouse = getSkillAt(mouseX, mouseY);
    boolean selected =
        selectedSkills.contains(button1.skill.getId())
            || selectedSkills.contains(button2.skill.getId());
    if (selected) {
      if (selectedSkills.contains(button2.skill.getId())) {
        renderGatewayConnection(poseStack, button2, button1);
      } else {
        renderGatewayConnection(poseStack, button1, button2);
      }
      return;
    }
    boolean hovered = skillAtMouse == button1 || skillAtMouse == button2;
    if (hovered) {
      if (skillAtMouse == button2) {
        renderGatewayConnection(poseStack, button2, button1);
      } else {
        renderGatewayConnection(poseStack, button1, button2);
      }
    }
  }

  private void renderGatewayConnection(
      PoseStack poseStack, SkillButton button1, SkillButton button2) {
    poseStack.pushPose();
    double connectionX = button1.x + button1.getWidth() / 2F;
    double connectionY = button1.y + button1.getHeight() / 2F;
    poseStack.translate(connectionX + scrollX, connectionY + scrollY, 0);
    float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
    poseStack.mulPose(Vector3f.ZP.rotation(rotation));
    int length = (int) (ScreenHelper.getDistanceBetweenButtons(button1, button2) / zoom);
    poseStack.scale(zoom, zoom, 1F);
    blit(poseStack, 0, -3, length, 6, 0, 0, length, 6, 30, 12);
    poseStack.popPose();
  }

  private void renderConnection(PoseStack poseStack, SkillConnection connection) {
    poseStack.pushPose();
    SkillButton button1 = connection.getFirstButton();
    SkillButton button2 = connection.getSecondButton();
    double connectionX = button1.x + button1.getWidth() / 2F;
    double connectionY = button1.y + button1.getHeight() / 2F;
    poseStack.translate(connectionX + scrollX, connectionY + scrollY, 0);
    float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
    poseStack.mulPose(Vector3f.ZP.rotation(rotation));
    int length = (int) ScreenHelper.getDistanceBetweenButtons(button1, button2);
    poseStack.scale(1F, zoom, 1F);
    blit(poseStack, 0, -3, length, 6, 0, 0, length, 6, 50, 12);
    poseStack.popPose();
  }

  protected boolean sameSkillBonuses(PassiveSkill skill, PassiveSkill otherSkill) {
    if (skill == otherSkill) return true;
    List<SkillBonus<?>> modifiers = skill.getBonuses();
    List<SkillBonus<?>> otherModifiers = otherSkill.getBonuses();
    if (modifiers.size() != otherModifiers.size()) return false;
    for (int i = 0; i < modifiers.size(); i++) {
      if (!modifiers.get(i).sameBonus(otherModifiers.get(i))) return false;
    }
    return true;
  }

  protected boolean sameTextures(PassiveSkill skill, PassiveSkill otherSkill) {
    if (skill == otherSkill) return true;
    if (!skill.getBackgroundTexture().equals(otherSkill.getBackgroundTexture())) return false;
    if (!skill.getIconTexture().equals(otherSkill.getIconTexture())) return false;
    return skill.getBorderTexture().equals(otherSkill.getBorderTexture());
  }

  protected boolean sameSize(PassiveSkill skill, PassiveSkill otherSkill) {
    if (skill == otherSkill) return true;
    return skill.getButtonSize() == otherSkill.getButtonSize();
  }
}
