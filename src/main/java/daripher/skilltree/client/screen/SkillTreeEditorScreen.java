package daripher.skilltree.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.data.SkillTexturesData;
import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.*;
import daripher.skilltree.client.widget.Button;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.*;
import java.util.function.Function;
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
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class SkillTreeEditorScreen extends Screen {
  private final Map<ResourceLocation, SkillButton> skillButtons = new HashMap<>();
  private final List<SkillConnection> skillConnections = new ArrayList<>();
  private final List<SkillConnection> gatewayConnections = new ArrayList<>();
  private final Set<ResourceLocation> selectedSkills = new HashSet<>();
  private final PassiveSkillTree skillTree;
  private Tools selectedTools = Tools.MAIN;
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
  private int selectedBonus = -1;

  public SkillTreeEditorScreen(ResourceLocation skillTreeId) {
    super(Component.empty());
    this.minecraft = Minecraft.getInstance();
    this.skillTree = SkillTreeClientData.getOrCreateEditorTree(skillTreeId);
  }

  @Override
  public void init() {
    if (skillTree == null) {
      getMinecraft().setScreen(null);
      return;
    }
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
    updateScroll(partialTick);
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
      fill(poseStack, toolsX - 10, 0, width, toolsY, 0xDD000000);
    }
    for (Widget widget : renderables) {
      if (widget instanceof SkillButton) continue;
      widget.render(poseStack, mouseX, mouseY, partialTick);
    }
    widgets()
        .filter(DropDownList.class::isInstance)
        .map(DropDownList.class::cast)
        .forEach(w -> w.renderList(poseStack, mouseX, mouseY, partialTick));
  }

  private void renderSkillTooltip(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    if (getWidgetAt(mouseX, mouseY).isPresent()) return;
    SkillButton skillAtMouse = getSkillAt(mouseX, mouseY);
    float tooltipX = mouseX + (prevMouseX - mouseX) * partialTick;
    float tooltipY = mouseY + (prevMouseY - mouseY) * partialTick;
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
    if (clickedWidget(mouseX, mouseY, button)) {
      return true;
    }
    SkillButton skillAtMouse = getSkillAt(mouseX, mouseY);
    if (skillAtMouse == null) return false;
    return skillAtMouse.mouseClicked(skillAtMouse.x + 1, skillAtMouse.y + 1, button);
  }

  private boolean clickedWidget(double mouseX, double mouseY, int button) {
    boolean clicked = false;
    for (GuiEventListener child : widgets().toList()) {
      if (child.mouseClicked(mouseX, mouseY, button)) clicked = true;
    }
    return clicked;
  }

  private Stream<EditBox> textFields() {
    return children().stream().filter(EditBox.class::isInstance).map(EditBox.class::cast);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
      if (selectedBonus != -1) {
        selectEditedBonus(-1);
        closeOnEsc = false;
        return true;
      }
      if (selectedTools != Tools.MAIN) {
        selectTools(Tools.MAIN);
        closeOnEsc = false;
        return true;
      }
      if (!selectedSkills.isEmpty()) {
        selectedSkills.clear();
        rebuildWidgets();
        closeOnEsc = false;
        return true;
      }
    }
    if (keyCode == GLFW.GLFW_KEY_N && Screen.hasControlDown()) {
      createNewSkill(0, 0, null);
      rebuildWidgets();
      return true;
    }
    if (keyPressedOnTextField(keyCode, scanCode, modifiers)) {
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  private boolean keyPressedOnTextField(int keyCode, int scanCode, int modifiers) {
    return textFields().toList().stream().anyMatch(b -> b.keyPressed(keyCode, scanCode, modifiers));
  }

  @Override
  public boolean shouldCloseOnEsc() {
    if (!closeOnEsc) {
      closeOnEsc = true;
      return false;
    }
    return super.shouldCloseOnEsc();
  }

  private void removeSelectedSkills() {
    selectedSkills()
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
    textFields().toList().forEach(b -> b.keyReleased(keyCode, scanCode, modifiers));
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char character, int keyCode) {
    for (EditBox textField : textFields().toList()) {
      if (textField.charTyped(character, keyCode)) {
        return true;
      }
    }
    return false;
  }

  private Optional<? extends GuiEventListener> getWidgetAt(double mouseX, double mouseY) {
    return widgets().filter(e -> e.isMouseOver(mouseX, mouseY)).findFirst();
  }

  private Stream<? extends GuiEventListener> widgets() {
    return children().stream().filter(Predicate.not(SkillButton.class::isInstance));
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
    if (selectedSkills.isEmpty()) return;
    toolsX = width - 210;
    toolsY = 10;
    switch (selectedTools) {
      case MAIN -> addMainTools();
      case TEXTURES -> addTexturesTools();
      case BUTTON -> addButtonTools();
      case BONUSES -> addBonusesTools();
      case NODE -> addNodeToolsButtons();
      case CONNECTIONS -> addConnectionToolsButton();
    }
    toolsY += 5;
  }

  private void addBonusesTools() {
    Button backButton = addButton(0, 0, 90, 14, "Back");
    if (selectedBonus == -1) {
      backButton.setPressFunc(b -> selectTools(Tools.MAIN));
    } else {
      backButton.setPressFunc(b -> selectEditedBonus(-1));
      addConfirmationButton(110, 0, 90, 14, "Remove", "Confirm")
          .setPressFunc(
              b -> {
                selectedSkills()
                    .forEach(
                        s -> {
                          removeSkillBonus(s, selectedBonus);
                          selectEditedBonus(-1);
                        });
                saveSelectedSkills();
                rebuildWidgets();
              });
    }
    shiftWidgets(0, 29);
    ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
    PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
    if (selectedSkills().anyMatch(otherSkill -> !sameBonuses(skill, otherSkill))) return;
    List<SkillBonus<?>> bonuses = skill.getBonuses();
    if (selectedBonus >= bonuses.size()) {
      selectedBonus = -1;
    }
    if (selectedBonus == -1) {
      for (int i = 0; i < bonuses.size(); i++) {
        SkillBonus<?> bonus = bonuses.get(i);
        String message = bonus.getTooltip().getString();
        if (font.width(message) > 190) {
          while (font.width(message + "...") > 190) {
            message = message.substring(0, message.length() - 1);
          }
          message += "...";
        }
        final int index = i;
        addButton(0, 0, 200, 14, message).setPressFunc(b -> selectEditedBonus(index));
        shiftWidgets(0, 19);
      }
    } else {
      skill
          .getBonuses()
          .get(selectedBonus)
          .addEditorWidgets(
              this,
              selectedBonus,
              b -> {
                selectedSkills().forEach(s -> s.getBonuses().set(selectedBonus, b));
                saveSelectedSkills();
              });
    }
    if (selectedBonus == -1) {
      shiftWidgets(0, 10);
      addLabel(0, 0, "Add Bonus", ChatFormatting.GOLD);
      shiftWidgets(0, 19);
      SkillBonus<?> defaultBonusType = PSTSkillBonuses.ATTRIBUTE.get().createDefaultInstance();
      @SuppressWarnings("rawtypes")
      DropDownList<SkillBonus> bonusTypeSelection =
          addDropDownList(0, 0, 200, 14, 10, defaultBonusType, PSTSkillBonuses.bonusList())
              .setToNameFunc(b -> Component.literal(PSTSkillBonuses.getName(b)));
      shiftWidgets(0, 19);
      addButton(0, 0, 90, 14, "Add")
          .setPressFunc(
              b -> {
                selectedSkills()
                    .forEach(s -> s.getBonuses().add(bonusTypeSelection.getValue().copy()));
                rebuildWidgets();
                saveSelectedSkills();
              });
      shiftWidgets(0, 19);
    }
  }

  private void selectEditedBonus(int index) {
    selectedBonus = index;
    rebuildWidgets();
  }

  private void addMainTools() {
    int buttonsWidth = 200;
    addButton(0, 0, buttonsWidth, 14, "Bonuses").setPressFunc(b -> selectTools(Tools.BONUSES));
    shiftWidgets(0, 19);
    addButton(0, 0, buttonsWidth, 14, "Textures").setPressFunc(b -> selectTools(Tools.TEXTURES));
    shiftWidgets(0, 19);
    addButton(0, 0, buttonsWidth, 14, "Button").setPressFunc(b -> selectTools(Tools.BUTTON));
    shiftWidgets(0, 19);
    if (selectedSkills.size() == 1) {
      addButton(0, 0, buttonsWidth, 14, "New Skill").setPressFunc(b -> selectTools(Tools.NODE));
      shiftWidgets(0, 19);
    }
    if (selectedSkills.size() == 2) {
      addButton(0, 0, buttonsWidth, 14, "Connections")
          .setPressFunc(b -> selectTools(Tools.CONNECTIONS));
      shiftWidgets(0, 19);
    }
    addConfirmationButton(0, 0, buttonsWidth, 14, "Remove", "Confirm")
        .setPressFunc(b -> removeSelectedSkills());
    shiftWidgets(0, 19);
  }

  private void selectTools(Tools tools) {
    selectedTools = tools;
    rebuildWidgets();
  }

  private void addNodeToolsButtons() {
    addButton(0, 0, 100, 14, "Back").setPressFunc(b -> selectTools(Tools.MAIN));
    shiftWidgets(0, 29);
    if (selectedSkills.size() != 1) return;
    ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
    PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
    addLabel(0, 0, "Distance", ChatFormatting.GOLD);
    addLabel(65, 0, "Angle", ChatFormatting.GOLD);
    toolsY += 19;
    NumericTextField distanceEditor = addNumericTextField(0, 0, 60, 14, 10);
    NumericTextField angleEditor = addNumericTextField(65, 0, 60, 14, 0);
    toolsY += 19;
    addButton(0, 0, 60, 14, "Add")
        .setPressFunc(b -> createNewSkill(angleEditor, distanceEditor, skill));
    addButton(65, 0, 60, 14, "Copy")
        .setPressFunc(b -> createSelectedSkillCopy(angleEditor, distanceEditor, skill));
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

  private void addButtonTools() {
    addButton(0, 0, 100, 14, "Back").setPressFunc(b -> selectTools(Tools.MAIN));
    shiftWidgets(0, 29);
    ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
    PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
    if (selectedSkills().allMatch(otherSkill -> sameSize(skill, otherSkill))) {
      addLabel(0, 0, "Size", ChatFormatting.GOLD);
      toolsY += 19;
      NumericTextField sizeEditor = addNumericTextField(0, 0, 40, 14, skill.getButtonSize());
      sizeEditor.setNumericFilter(d -> d >= 2);
      sizeEditor.setResponder(s -> setSelectedSkillsSize((int) sizeEditor.getNumericValue()));
      addRenderableWidget(sizeEditor);
      toolsY += 19;
    }
    if (selectedSkills.size() == 1) {
      toolsY -= 38;
      addLabel(65, 0, "Position", ChatFormatting.GOLD);
      toolsY += 19;
      NumericTextField xPosEditor = addNumericTextField(65, 0, 60, 14, skill.getPositionX());
      xPosEditor.setResponder(s -> skillXPosEditorChanged(skill, xPosEditor));
      addRenderableWidget(xPosEditor);
      NumericTextField yPosEditor = addNumericTextField(130, 0, 60, 14, skill.getPositionY());
      yPosEditor.setResponder(s -> skillYPosEditorChanged(skill, yPosEditor));
      addRenderableWidget(yPosEditor);
      toolsY += 19;
    }
    if (selectedSkills().allMatch(otherSkill -> sameTitle(skill, otherSkill))) {
      addLabel(0, 0, "Title", ChatFormatting.GOLD);
      toolsY += 19;
      TextField titleEditor = addTextField(0, 0, 200, 14, skill.getTitle());
      titleEditor.setResponder(this::setSelectedSkillsTitle);
      addRenderableWidget(titleEditor);
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

  private void setSelectedSkillsSize(int size) {
    selectedSkills().forEach(skill -> skill.setButtonSize(size));
    getSelectedSkillButtons().forEach(button -> button.setButtonSize(size));
    saveSelectedSkills();
  }

  private void setSelectedSkillsTitle(String title) {
    selectedSkills().forEach(skill -> skill.setTitle(title));
    saveSelectedSkills();
  }

  private void addTexturesTools() {
    addButton(0, 0, 100, 14, "Back").setPressFunc(b -> selectTools(Tools.MAIN));
    shiftWidgets(0, 29);
    ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
    PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
    if (selectedSkills().anyMatch(otherSkill -> !sameTextures(skill, otherSkill))) return;
    Function<ResourceLocation, Component> textureNameFunc =
        l -> {
          String texture = l.getPath();
          texture = texture.substring(texture.lastIndexOf("/") + 1);
          texture = texture.replace(".png", "");
          texture = TooltipHelper.idToName(texture);
          return Component.literal(texture);
        };
    addLabel(0, 0, "Border", ChatFormatting.GOLD);
    toolsY += 19;
    addDropDownList(0, 0, 200, 14, 10, skill.getBackgroundTexture(), SkillTexturesData.BORDERS)
        .setToNameFunc(textureNameFunc)
        .setResponder(
            value -> {
              selectedSkills().forEach(s -> s.setBackgroundTexture(value));
              saveSelectedSkills();
            });
    toolsY += 19;
    addLabel(0, 0, "Tooltip", ChatFormatting.GOLD);
    toolsY += 19;
    addDropDownList(
            0, 0, 200, 14, 10, skill.getBorderTexture(), SkillTexturesData.TOOLTIP_BACKGROUNDS)
        .setToNameFunc(textureNameFunc)
        .setResponder(
            value -> {
              selectedSkills().forEach(s -> s.setBorderTexture(value));
              saveSelectedSkills();
            });
    toolsY += 19;
    addLabel(0, 0, "Icon", ChatFormatting.GOLD);
    toolsY += 19;
    addDropDownList(0, 0, 200, 14, 10, skill.getIconTexture(), SkillTexturesData.ICONS)
        .setToNameFunc(textureNameFunc)
        .setResponder(
            value -> {
              selectedSkills().forEach(s -> s.setIconTexture(value));
              saveSelectedSkills();
            });
    toolsY += 19;
  }

  private Stream<SkillButton> getSelectedSkillButtons() {
    return selectedSkills.stream().map(skillButtons::get);
  }

  private void addConnectionToolsButton() {
    addButton(0, 0, 100, 14, "Back").setPressFunc(b -> selectTools(Tools.MAIN));
    shiftWidgets(0, 29);
    if (selectedSkills.size() != 2) return;
    ResourceLocation first = (ResourceLocation) selectedSkills.toArray()[0];
    ResourceLocation second = (ResourceLocation) selectedSkills.toArray()[1];
    PassiveSkill firstSkill = SkillTreeClientData.getEditorSkill(first);
    PassiveSkill secondSkill = SkillTreeClientData.getEditorSkill(second);
    if (skillsConnected(firstSkill, secondSkill)) {
      Button disconnectButton =
          new Button(toolsX, toolsY, 100, 14, Component.literal("Disconnect"));
      addRenderableWidget(disconnectButton);
      disconnectButton.setPressFunc(
          b -> {
            firstSkill.getConnections().remove(second);
            secondSkill.getConnections().remove(first);
            firstSkill.getGatewayConnections().remove(second);
            secondSkill.getGatewayConnections().remove(first);
            saveSelectedSkills();
            rebuildWidgets();
          });
    } else {
      addButton(0, 0, 100, 14, "Connect")
          .setPressFunc(
              b -> {
                skillButtons.get(first).skill.getConnections().add(second);
                saveSelectedSkills();
                rebuildWidgets();
              });
      addButton(105, 0, 100, 14, "Connect Gateways")
          .setPressFunc(
              b -> {
                skillButtons.get(first).skill.getGatewayConnections().add(second);
                saveSelectedSkills();
                rebuildWidgets();
              });
    }
    toolsY += 19;
  }

  private boolean skillsConnected(PassiveSkill first, PassiveSkill second) {
    return first.getConnections().contains(second.getId())
        || second.getConnections().contains(first.getId())
        || first.getGatewayConnections().contains(second.getId())
        || second.getGatewayConnections().contains(first.getId());
  }

  private void saveSelectedSkills() {
    selectedSkills.stream()
        .map(skillButtons::get)
        .map(button -> button.skill)
        .forEach(SkillTreeClientData::saveEditorSkill);
  }

  protected void addSkillButton(PassiveSkill skill) {
    SkillButton button =
        new SkillButton(
            () -> 0f,
            getSkillButtonX(skill),
            getSkillButtonY(skill),
            skill,
            b -> skillButtonPressed((SkillButton) b));
    addRenderableWidget(button);
    button.highlighted = true;
    skillButtons.put(skill.getId(), button);
    float skillX = skill.getPositionX();
    float skillY = skill.getPositionY();
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
    for (ResourceLocation connectedSkillId : new ArrayList<>(skill.getConnections())) {
      if (SkillTreeClientData.getEditorSkill(connectedSkillId) == null) {
        skill.getConnections().remove(connectedSkillId);
        SkillTreeClientData.saveEditorSkill(skill);
        continue;
      }
      connectSkills(skillConnections, skill.getId(), connectedSkillId);
    }
  }

  private void addGatewayConnections(PassiveSkill skill) {
    skill
        .getGatewayConnections()
        .forEach(
            connectedSkillId -> connectSkills(gatewayConnections, skill.getId(), connectedSkillId));
  }

  protected void connectSkills(
      List<SkillConnection> connections, ResourceLocation skillId1, ResourceLocation skillId2) {
    SkillButton button1 = skillButtons.get(skillId1);
    SkillButton button2 = skillButtons.get(skillId2);
    if (button1 == null || button2 == null) return;
    connections.add(new SkillConnection(button1, button2));
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

  @Override
  public void tick() {
    textFields().forEach(EditBox::tick);
  }

  private void updateScroll(float partialTick) {
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
    if (widgets().anyMatch(w -> w.mouseScrolled(mouseX, mouseY, amount))) return true;
    if (amount > 0 && zoom < 2F) zoom += 0.05f;
    if (amount < 0 && zoom > 0.25F) zoom -= 0.05f;
    rebuildWidgets();
    return super.mouseScrolled(mouseX, mouseY, amount);
  }

  public Stream<PassiveSkill> selectedSkills() {
    return selectedSkills.stream().map(SkillTreeClientData::getEditorSkill);
  }

  public void shiftWidgets(int x, int y) {
    toolsX += x;
    toolsY += y;
  }

  private static void removeSkillBonus(PassiveSkill skill, int index) {
    skill.getBonuses().remove(index);
    SkillTreeClientData.saveEditorSkill(skill);
  }

  public TextField addTextField(int x, int y, int width, int height, String defaultValue) {
    assert minecraft != null;
    return addRenderableWidget(
        new TextField(minecraft.font, toolsX + x, toolsY + y, width, height, defaultValue));
  }

  public NumericTextField addNumericTextField(
      int x, int y, int width, int height, double defaultValue) {
    assert minecraft != null;
    return addRenderableWidget(
        new NumericTextField(minecraft.font, toolsX + x, toolsY + y, width, height, defaultValue));
  }

  public void rebuildWidgets() {
    super.rebuildWidgets();
  }

  public Label addLabel(int x, int y, String text, ChatFormatting... styles) {
    MutableComponent message = Component.literal(text);
    for (ChatFormatting style : styles) {
      message.withStyle(style);
    }
    return addRenderableOnly(new Label(toolsX + x, toolsY + y, message));
  }

  public <T> DropDownList<T> addDropDownList(
      int x,
      int y,
      int width,
      int height,
      int maxDisplayed,
      T defaultValue,
      Collection<T> possibleValues) {
    return addRenderableWidget(
        new DropDownList<>(
            toolsX + x, toolsY + y, width, height, maxDisplayed, possibleValues, defaultValue));
  }

  public <T extends Enum<T>> DropDownList<T> addDropDownList(
      int x, int y, int width, int height, int maxDisplayed, T defaultValue) {
    @SuppressWarnings("unchecked")
    Class<T> enumClass = (Class<T>) defaultValue.getClass();
    List<T> enums = Stream.of(enumClass.getEnumConstants()).map(enumClass::cast).toList();
    return addRenderableWidget(
        new DropDownList<>(
            toolsX + x, toolsY + y, width, height, maxDisplayed, enums, defaultValue));
  }

  public Button addButton(int x, int y, int width, int height, String message) {
    return addRenderableWidget(
        new Button(toolsX + x, toolsY + y, width, height, Component.literal(message)));
  }

  public ConfirmationButton addConfirmationButton(
      int x, int y, int width, int height, String message, String confirmationMessage) {
    ConfirmationButton button =
        new ConfirmationButton(toolsX + x, toolsY + y, width, height, Component.literal(message));
    button.setConfirmationMessage(Component.literal(confirmationMessage));
    return addRenderableWidget(button);
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

  protected boolean sameBonuses(PassiveSkill skill, PassiveSkill otherSkill) {
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

  protected boolean sameTitle(PassiveSkill skill, PassiveSkill otherSkill) {
    if (skill == otherSkill) return true;
    return skill.getTitle().equals(otherSkill.getTitle());
  }

  private enum Tools {
    MAIN,
    BONUSES,
    TEXTURES,
    BUTTON,
    CONNECTIONS,
    NODE
  }
}
