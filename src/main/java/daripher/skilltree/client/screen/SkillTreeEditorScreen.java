package daripher.skilltree.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotAttribute;

public class SkillTreeEditorScreen extends Screen {
  private final Map<ResourceLocation, SkillButton> skillButtons = new HashMap<>();
  private final List<SkillConnection> skillConnections = new ArrayList<>();
  private final Set<ResourceLocation> selectedSkills = new LinkedHashSet<>();
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
    addToolButtons();
  }

  @Override
  public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    updateScroll(partialTick);
    renderBackground(graphics);
    renderConnections(graphics, mouseX, mouseY);
    renderSkills(graphics, mouseX, mouseY, partialTick);
    renderOverlay(graphics);
    renderWidgets(graphics, mouseX, mouseY, partialTick);
    renderSkillTooltip(graphics, mouseX, mouseY, partialTick);
    prevMouseX = mouseX;
    prevMouseY = mouseY;
  }

  private void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    if (!selectedSkills.isEmpty()) {
      graphics.fill(toolsX - 10, 0, width, toolsY, 0xDD000000);
    }
    for (Renderable widget : renderables) {
      if (widget instanceof SkillButton) continue;
      widget.render(graphics, mouseX, mouseY, partialTick);
    }
    graphics.pose().pushPose();
    graphics.pose().translate(0, 0, 1f);
    widgets()
        .filter(DropDownList.class::isInstance)
        .map(DropDownList.class::cast)
        .forEach(w -> w.renderList(graphics));
    graphics.pose().popPose();
  }

  private void renderSkillTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    if (getWidgetAt(mouseX, mouseY).isPresent()) return;
    SkillButton skillAtMouse = getSkillAt(mouseX, mouseY);
    float tooltipX = mouseX + (prevMouseX - mouseX) * partialTick;
    float tooltipY = mouseY + (prevMouseY - mouseY) * partialTick;
    if (skillAtMouse == null) return;
    ScreenHelper.renderSkillTooltip(skillAtMouse, graphics, tooltipX, tooltipY, width, height);
  }

  private void renderSkills(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    graphics.pose().pushPose();
    graphics.pose().translate(scrollX, scrollY, 0);
    for (SkillButton widget : skillButtons.values()) {
      graphics.pose().pushPose();
      double widgetCenterX = widget.x + widget.getWidth() / 2f;
      double widgetCenterY = widget.y + widget.getHeight() / 2f;
      graphics.pose().translate(widgetCenterX, widgetCenterY, 0F);
      graphics.pose().scale(zoom, zoom, 1F);
      graphics.pose().translate(-widgetCenterX, -widgetCenterY, 0F);
      widget.render(graphics, mouseX, mouseY, partialTick);
      if (selectedSkills.contains(widget.skill.getId())) {
        graphics.pose().pushPose();
        graphics.pose().translate(widget.x, widget.y, 0);
        renderSkillSelection(graphics, widget);
        graphics.pose().popPose();
      }
      graphics.pose().popPose();
    }
    graphics.pose().popPose();
  }

  private void renderSkillSelection(GuiGraphics graphics, SkillButton widget) {
    ScreenHelper.drawRectangle(
        graphics, -1, -1, widget.getWidth() + 2, widget.getHeight() + 2, 0xAA32FF00);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    return clickedWidget(mouseX, mouseY, button) || clickedSkill(mouseX, mouseY, button);
  }

  private boolean clickedWidget(double mouseX, double mouseY, int button) {
    boolean clicked = false;
    for (GuiEventListener child : widgets().toList()) {
      if (child.mouseClicked(mouseX, mouseY, button)) {
        setFocused(child);
        clicked = true;
      }
    }
    return clicked;
  }

  private boolean clickedSkill(double mouseX, double mouseY, int button) {
    SkillButton skillAtMouse = getSkillAt(mouseX, mouseY);
    if (skillAtMouse == null) return false;
    return skillAtMouse.mouseClicked(skillAtMouse.x + 1, skillAtMouse.y + 1, button);
  }

  private Stream<EditBox> textFields() {
    return children().stream().filter(EditBox.class::isInstance).map(EditBox.class::cast);
  }

  @SuppressWarnings("rawtypes")
  private Stream<DropDownList> dropDownLists() {
    return children().stream().filter(DropDownList.class::isInstance).map(DropDownList.class::cast);
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
    return super.keyReleased(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char character, int keyCode) {
    for (EditBox textField : textFields().toList()) {
      if (textField.charTyped(character, keyCode)) {
        return true;
      }
    }
    for (DropDownList<?> dropDownList : dropDownLists().toList()) {
      if (dropDownList.charTyped(character, keyCode)) {
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
                selectedSkills().forEach(s -> removeSkillBonus(s, selectedBonus));
                selectEditedBonus(-1);
                saveSelectedSkills();
                rebuildWidgets();
              });
    }
    shiftWidgets(0, 29);
    PassiveSkill skill = getFirstSelectedSkill();
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
                selectedSkills().forEach(s -> s.getBonuses().set(selectedBonus, b.copy()));
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
    if (!selectedSkills.isEmpty()) {
      addButton(0, 0, buttonsWidth, 14, "New Skill").setPressFunc(b -> selectTools(Tools.NODE));
      shiftWidgets(0, 19);
    }
    if (selectedSkills.size() >= 2) {
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
    if (selectedSkills.isEmpty()) return;
    addLabel(0, 0, "Distance", ChatFormatting.GOLD);
    addLabel(65, 0, "Angle", ChatFormatting.GOLD);
    toolsY += 19;
    NumericTextField distanceEditor = addNumericTextField(0, 0, 60, 14, 10);
    NumericTextField angleEditor = addNumericTextField(65, 0, 60, 14, 0);
    toolsY += 19;
    addButton(0, 0, 60, 14, "Add").setPressFunc(b -> createNewSkills(angleEditor, distanceEditor));
    addButton(65, 0, 60, 14, "Copy")
        .setPressFunc(b -> createSelectedSkillsCopies(angleEditor, distanceEditor));
    toolsY += 19;
  }

  private void createSelectedSkillsCopies(
      NumericTextField angleEditor, NumericTextField distanceEditor) {
    float angle = (float) (angleEditor.getNumericValue() * Mth.PI / 180F);
    selectedSkills.forEach(
        skillId -> {
          PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
          float distance = (float) distanceEditor.getNumericValue();
          distance += skill.getButtonSize() / 2f + 8;
          float skillX = skill.getPositionX() + Mth.sin(angle) * distance;
          float skillY = skill.getPositionY() + Mth.cos(angle) * distance;
          createCopiedSkill(skillX, skillY, skill);
        });
    rebuildWidgets();
  }

  private void createNewSkills(NumericTextField angleEditor, NumericTextField distanceEditor) {
    float angle = (float) (angleEditor.getNumericValue() * Mth.PI / 180F);
    selectedSkills.forEach(
        skillId -> {
          PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
          float distance = (float) distanceEditor.getNumericValue();
          distance += skill.getButtonSize() / 2f + 8;
          float skillX = skill.getPositionX() + Mth.sin(angle) * distance;
          float skillY = skill.getPositionY() + Mth.cos(angle) * distance;
          createNewSkill(skillX, skillY, skill);
        });
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
    PassiveSkill firstSelectedSkill = getFirstSelectedSkill();
    if (canEdit(PassiveSkill::getButtonSize)) {
      addLabel(0, 0, "Size", ChatFormatting.GOLD);
      toolsY += 19;
      addNumericTextField(0, 0, 40, 14, firstSelectedSkill.getButtonSize())
          .setNumericFilter(d -> d >= 2)
          .setNumericResponder(this::setSelectedSkillsSize);
      toolsY += 19;
    }
    if (selectedSkills.size() == 1) {
      toolsY -= 38;
      addLabel(65, 0, "Position", ChatFormatting.GOLD);
      toolsY += 19;
      addNumericTextField(65, 0, 60, 14, firstSelectedSkill.getPositionX())
          .setNumericResponder(
              v ->
                  setSkillPosition(
                      getFirstSelectedSkill(), v.floatValue(), firstSelectedSkill.getPositionY()));
      addNumericTextField(130, 0, 60, 14, firstSelectedSkill.getPositionY())
          .setNumericResponder(
              v ->
                  setSkillPosition(
                      getFirstSelectedSkill(), firstSelectedSkill.getPositionX(), v.floatValue()));
      toolsY += 19;
    }
    if (canEdit(PassiveSkill::getTitle)) {
      addLabel(0, 0, "Title", ChatFormatting.GOLD);
      toolsY += 19;
      addTextField(0, 0, 200, 14, firstSelectedSkill.getTitle())
          .setResponder(this::setSelectedSkillsTitle);
      toolsY += 19;
    }
    boolean canEditTitleColor = canEdit(PassiveSkill::getTitleColor);
    if (canEditTitleColor) {
      addLabel(0, 0, "Title Color", ChatFormatting.GOLD);
      toolsY += 19;
      addTextField(0, 0, 80, 14, firstSelectedSkill.getTitleColor())
          .setSoftFilter(v -> v.matches("^#?[a-fA-F0-9]{6}") || v.isEmpty())
          .setResponder(this::setSelectedSkillsTitleColor);
      toolsY += 19;
    }
    if (canEdit(PassiveSkill::isStartingPoint)) {
      if (canEditTitleColor) {
        shiftWidgets(100, -38);
      }
      addLabel(0, 0, "Starting Point", ChatFormatting.GOLD);
      toolsY += 19;
      addCheckBox(0, 0, firstSelectedSkill.isStartingPoint())
          .setResponder(
              v -> {
                selectedSkills().forEach(s -> s.setStartingPoint(v));
                saveSelectedSkills();
              });
      if (canEditTitleColor) {
        shiftWidgets(-100, 0);
      }
      toolsY += 19;
    }
  }

  private void setSelectedSkillsSize(double size) {
    selectedSkills()
        .forEach(
            skill -> {
              skill.setButtonSize((int) size);
              reAddSkillButton(skill);
            });
    addSkillConnections();
    saveSelectedSkills();
  }

  private void setSkillPosition(PassiveSkill skill, float x, float y) {
    skill.setPosition(x, y);
    reAddSkillButton(skill);
    addSkillConnections();
    saveSelectedSkills();
  }

  private void moveSelectedSkills(float x, float y) {
    selectedSkills.forEach(
        skillId -> {
          PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
          skill.setPosition(skill.getPositionX() + x, skill.getPositionY() + y);
          reAddSkillButton(skill);
        });
    addSkillConnections();
    saveSelectedSkills();
  }

  private PassiveSkill getFirstSelectedSkill() {
    ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
    return SkillTreeClientData.getEditorSkill(skillId);
  }

  private void setSelectedSkillsTitle(String title) {
    selectedSkills().forEach(skill -> skill.setTitle(title));
    saveSelectedSkills();
  }

  private void setSelectedSkillsTitleColor(String color) {
    if (color.startsWith("#")) color = color.substring(1);
    String finalColor = color;
    selectedSkills().forEach(skill -> skill.setTitleColor(finalColor));
    saveSelectedSkills();
  }

  private void addTexturesTools() {
    addButton(0, 0, 100, 14, "Back").setPressFunc(b -> selectTools(Tools.MAIN));
    shiftWidgets(0, 29);
    if (!canEdit(
        PassiveSkill::getBackgroundTexture,
        PassiveSkill::getBorderTexture,
        PassiveSkill::getIconTexture)) {
      return;
    }
    PassiveSkill skill = getFirstSelectedSkill();
    addLabel(0, 0, "Border", ChatFormatting.GOLD);
    addLabel(105, 0, "Tooltip", ChatFormatting.GOLD);
    toolsY += 19;
    addDropDownList(0, 0, 95, 14, 10, skill.getBackgroundTexture(), SkillTexturesData.BORDERS)
        .setToNameFunc(TooltipHelper::getTextureName)
        .setResponder(
            value -> {
              selectedSkills().forEach(s -> s.setBackgroundTexture(value));
              saveSelectedSkills();
            });
    addDropDownList(
            105, 0, 95, 14, 10, skill.getBorderTexture(), SkillTexturesData.TOOLTIP_BACKGROUNDS)
        .setToNameFunc(TooltipHelper::getTextureName)
        .setResponder(
            value -> {
              selectedSkills().forEach(s -> s.setBorderTexture(value));
              saveSelectedSkills();
            });
    toolsY += 19;
    addLabel(0, 0, "Icon", ChatFormatting.GOLD);
    toolsY += 19;
    addDropDownList(0, 0, 200, 14, 10, skill.getIconTexture(), SkillTexturesData.ICONS)
        .setToNameFunc(TooltipHelper::getTextureName)
        .setResponder(
            value -> {
              selectedSkills().forEach(s -> s.setIconTexture(value));
              saveSelectedSkills();
            });
    toolsY += 19;
  }

  private void addConnectionToolsButton() {
    addButton(0, 0, 100, 14, "Back").setPressFunc(b -> selectTools(Tools.MAIN));
    shiftWidgets(0, 29);
    if (selectedSkills.size() < 2) return;
    if (selectedSkillsConnected()) {
      Button disconnectButton =
          new Button(toolsX, toolsY, 100, 14, Component.literal("Disconnect"));
      addRenderableWidget(disconnectButton);
      disconnectButton.setPressFunc(b -> disconnectSelectedSkills());
    } else {
      addLabel(0, 0, "Connect");
      shiftWidgets(0, 19);
      addButton(0, 0, 100, 14, "Direct")
          .setPressFunc(b -> connectSelectedSkills(SkillConnection.Type.DIRECT));
      shiftWidgets(0, 19);
      addButton(0, 0, 100, 14, "Long")
          .setPressFunc(b -> connectSelectedSkills(SkillConnection.Type.LONG));
      shiftWidgets(0, 19);
      addButton(0, 0, 100, 14, "One Way")
          .setPressFunc(b -> connectSelectedSkills(SkillConnection.Type.ONE_WAY));
    }
    shiftWidgets(0, 19);
  }

  private void connectSelectedSkills(SkillConnection.Type connectionType) {
    ResourceLocation[] selectedSkillsArray = selectedSkills.toArray(new ResourceLocation[0]);
    for (int i = 0; i < selectedSkills.size() - 1; i++) {
      PassiveSkill skill = SkillTreeClientData.getEditorSkill(selectedSkillsArray[i]);
      List<ResourceLocation> connections =
          switch (connectionType) {
            case DIRECT -> skill.getDirectConnections();
            case LONG -> skill.getLongConnections();
            case ONE_WAY -> skill.getOneWayConnections();
          };
      connections.add(selectedSkillsArray[i + 1]);
    }
    saveSelectedSkills();
    rebuildWidgets();
  }

  private void disconnectSelectedSkills() {
    ResourceLocation[] selectedSkillsArray = selectedSkills.toArray(new ResourceLocation[0]);
    for (int i = 0; i < selectedSkills.size() - 1; i++) {
      PassiveSkill skill1 = SkillTreeClientData.getEditorSkill(selectedSkillsArray[i]);
      PassiveSkill skill2 = SkillTreeClientData.getEditorSkill(selectedSkillsArray[i + 1]);
      skill1.getDirectConnections().remove(skill2.getId());
      skill2.getDirectConnections().remove(skill1.getId());
      skill1.getLongConnections().remove(skill2.getId());
      skill2.getLongConnections().remove(skill1.getId());
      skill1.getOneWayConnections().remove(skill2.getId());
      skill2.getOneWayConnections().remove(skill1.getId());
    }
    saveSelectedSkills();
    rebuildWidgets();
  }

  private boolean selectedSkillsConnected() {
    ResourceLocation[] selectedSkillsArray = selectedSkills.toArray(new ResourceLocation[0]);
    for (int i = 0; i < selectedSkills.size() - 1; i++) {
      PassiveSkill skill1 = SkillTreeClientData.getEditorSkill(selectedSkillsArray[i]);
      PassiveSkill skill2 = SkillTreeClientData.getEditorSkill(selectedSkillsArray[i + 1]);
      if (!skillsConnected(skill1, skill2)) return false;
    }
    return true;
  }

  private boolean skillsConnected(PassiveSkill first, PassiveSkill second) {
    return first.getDirectConnections().contains(second.getId())
        || second.getDirectConnections().contains(first.getId())
        || first.getLongConnections().contains(second.getId())
        || second.getLongConnections().contains(first.getId())
        || first.getOneWayConnections().contains(second.getId())
        || second.getOneWayConnections().contains(first.getId());
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

  protected void reAddSkillButton(PassiveSkill skill) {
    children().removeIf(w -> w instanceof SkillButton b && b.skill == skill);
    skillButtons.remove(skill.getId());
    addSkillButton(skill);
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

  private Stream<PassiveSkill> getTreeSkills() {
    return skillTree.getSkillIds().stream().map(SkillTreeClientData::getEditorSkill);
  }

  private void addSkillConnections(PassiveSkill skill) {
    readSkillConnections(skill, SkillConnection.Type.DIRECT, skill.getDirectConnections());
    readSkillConnections(skill, SkillConnection.Type.LONG, skill.getLongConnections());
    readSkillConnections(skill, SkillConnection.Type.ONE_WAY, skill.getOneWayConnections());
  }

  private void readSkillConnections(
      PassiveSkill skill, SkillConnection.Type type, List<ResourceLocation> connections) {
    for (ResourceLocation connectedSkillId : new ArrayList<>(connections)) {
      if (SkillTreeClientData.getEditorSkill(connectedSkillId) == null) {
        connections.remove(connectedSkillId);
        SkillTreeClientData.saveEditorSkill(skill);
        continue;
      }
      connectSkills(type, skill.getId(), connectedSkillId);
    }
  }

  protected void connectSkills(
      SkillConnection.Type type, ResourceLocation skillId1, ResourceLocation skillId2) {
    SkillButton button1 = skillButtons.get(skillId1);
    SkillButton button2 = skillButtons.get(skillId2);
    skillConnections.add(new SkillConnection(type, button1, button2));
  }

  protected void skillButtonPressed(SkillButton button) {
    if (hasControlDown()) return;
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
    dropDownLists().forEach(DropDownList::tick);
  }

  private void updateScroll(float partialTick) {
    scrollX += scrollSpeedX * partialTick;
    scrollX = Math.max(-maxScrollX * zoom, Math.min(maxScrollX * zoom, scrollX));
    scrollSpeedX *= 0.8;
    scrollY += scrollSpeedY * partialTick;
    scrollY = Math.max(-maxScrollY * zoom, Math.min(maxScrollY * zoom, scrollY));
    scrollSpeedY *= 0.8;
  }

  private void renderOverlay(GuiGraphics graphics) {
    ResourceLocation texture =
        new ResourceLocation("skilltree:textures/screen/skill_tree_overlay.png");
    RenderSystem.enableBlend();
    graphics.blit(texture, 0, 0, 0, 0F, 0F, width, height, width, height);
    RenderSystem.disableBlend();
  }

  @Override
  public void renderBackground(GuiGraphics graphics) {
    ResourceLocation texture =
        new ResourceLocation("skilltree:textures/screen/skill_tree_background.png");
    graphics.pose().pushPose();
    graphics.pose().translate(scrollX / 3F, scrollY / 3F, 0);
    int size = SkillTreeScreen.BACKGROUND_SIZE;
    graphics.blit(
        texture, (width - size) / 2, (height - size) / 2, 0, 0F, 0F, size, size, size, size);
    graphics.pose().popPose();
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int mouseButton, double dragAmountX, double dragAmountY) {
    if (mouseButton != 0 && mouseButton != 2) return false;
    if (mouseButton == 0 && hasControlDown() && !selectedSkills.isEmpty()) {
      moveSelectedSkills((float) dragAmountX / zoom, (float) dragAmountY / zoom);
      return true;
    }
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

  public CheckBox addCheckBox(int x, int y, boolean value) {
    return addRenderableWidget(new CheckBox(toolsX + x, toolsY + y, value));
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
    Class<T> enumClass = (Class<T>) defaultValue.getClass();
    List<T> enums = Stream.of(enumClass.getEnumConstants()).map(enumClass::cast).toList();
    return addRenderableWidget(
        new DropDownList<>(
            toolsX + x, toolsY + y, width, height, maxDisplayed, enums, defaultValue));
  }

  public DropDownList<Attribute> addAttributePicker(
      int x, int y, int width, int height, int maxDisplayed, Attribute defaultValue) {
    return addDropDownList(x, y, width, height, maxDisplayed, defaultValue, getEditableAttributes())
        .setToNameFunc(
            attribute -> {
              ResourceLocation id = ForgeRegistries.ATTRIBUTES.getKey(attribute);
              if (attribute instanceof SlotAttribute slotAttribute) {
                id = new ResourceLocation("curios", slotAttribute.getIdentifier());
              }
              Objects.requireNonNull(id);
              return Component.literal(id.toString());
            });
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

  private static final Set<Attribute> EDITABLE_ATTRIBUTES = new HashSet<>();

  @SuppressWarnings("deprecation")
  @NotNull
  private static Collection<Attribute> getEditableAttributes() {
    if (EDITABLE_ATTRIBUTES.isEmpty()) {
      ForgeRegistries.ATTRIBUTES.getValues().stream()
          .filter(ForgeHooks.getAttributesView().get(EntityType.PLAYER)::hasAttribute)
          .forEach(EDITABLE_ATTRIBUTES::add);
      CuriosApi.getSlots().keySet().stream()
          .map(SlotAttribute::getOrCreate)
          .forEach(EDITABLE_ATTRIBUTES::add);
    }
    return EDITABLE_ATTRIBUTES;
  }

  protected void renderConnections(GuiGraphics graphics, int mouseX, int mouseY) {
    skillConnections.stream()
        .filter(c -> c.getType() == SkillConnection.Type.DIRECT)
        .forEach(c -> renderDirectConnection(graphics, c));
    skillConnections.stream()
        .filter(c -> c.getType() == SkillConnection.Type.LONG)
        .forEach(c -> renderLongConnection(graphics, c, mouseX, mouseY));
    skillConnections.stream()
        .filter(c -> c.getType() == SkillConnection.Type.ONE_WAY)
        .forEach(c -> renderOneWayConnection(graphics, c));
  }

  private void renderDirectConnection(GuiGraphics graphics, SkillConnection c) {
    ScreenHelper.renderConnection(graphics, scrollX, scrollY, c, zoom, 0);
  }

  private void renderLongConnection(
      GuiGraphics graphics, SkillConnection connection, int mouseX, int mouseY) {
    SkillButton hoveredSkill = getSkillAt(mouseX, mouseY);
    if (hoveredSkill != connection.getFirstButton()
        && hoveredSkill != connection.getSecondButton()) {
      return;
    }
    ScreenHelper.renderGatewayConnection(graphics, scrollX, scrollY, connection, true, zoom, 0);
  }

  private void renderOneWayConnection(GuiGraphics graphics, SkillConnection connection) {
    ScreenHelper.renderOneWayConnection(graphics, scrollX, scrollY, connection, true, zoom, 0);
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

  @SafeVarargs
  protected final boolean canEdit(Function<PassiveSkill, ?>... functions) {
    return selectedSkills()
        .allMatch(
            skill -> {
              PassiveSkill firstSkill = getFirstSelectedSkill();
              if (skill == firstSkill) return true;
              for (Function<PassiveSkill, ?> function : functions) {
                if (!function.apply(firstSkill).equals(function.apply(skill))) return false;
              }
              return true;
            });
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
