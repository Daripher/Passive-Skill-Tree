package daripher.skilltree.client.screen;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.skill.SkillTreeClientData;
import daripher.skilltree.client.widget.*;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.AttributeSkillBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
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
    renderConnections(poseStack, mouseX, mouseY, partialTick);
    renderSkills(poseStack, mouseX, mouseY, partialTick);
    renderOverlay(poseStack, mouseX, mouseY, partialTick);
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
    getSkillAt(mouseX, mouseY)
        .ifPresent(
            button -> {
              renderSkillTooltip(
                  button,
                  poseStack,
                  mouseX + (prevMouseX - mouseX) * partialTick,
                  mouseY + (prevMouseY - mouseY) * partialTick);
            });
  }

  private void renderSkills(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    poseStack.pushPose();
    poseStack.translate(scrollX, scrollY, 0);
    for (SkillButton widget : skillButtons.values()) {
      poseStack.pushPose();
      poseStack.translate(widget.x + widget.getWidth() / 2, widget.y + widget.getHeight() / 2, 0F);
      poseStack.scale(zoom, zoom, 1F);
      poseStack.translate(
          -widget.x - widget.getWidth() / 2, -widget.y - widget.getHeight() / 2, 0F);
      widget.render(poseStack, mouseX, mouseY, partialTick);
      if (selectedSkills.contains(widget.skill.getId())) {
        poseStack.pushPose();
        poseStack.translate(widget.x, widget.y, 0);
        ScreenHelper.drawRectangle(
            poseStack, -1, -1, widget.getWidth() + 2, widget.getHeight() + 2, 0xAA32FF00);
        poseStack.popPose();
      }
      poseStack.popPose();
    }
    poseStack.popPose();
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (clickedOnEditBox(mouseX, mouseY, button)) return true;
    Optional<? extends GuiEventListener> widget = getWidgetAt(mouseX, mouseY);
    if (widget.isPresent()) return widget.get().mouseClicked(mouseX, mouseY, button);
    Optional<SkillButton> skill = getSkillAt(mouseX, mouseY);
    if (skill.isPresent())
      return skill.get().mouseClicked(skill.get().x + 1, skill.get().y + 1, button);
    return false;
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
    return children().stream().filter(Predicates.not(SkillButton.class::isInstance));
  }

  private Optional<SkillButton> getSkillAt(double mouseX, double mouseY) {
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
        return Optional.of(button);
      }
    }
    return Optional.empty();
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
      addRenderableOnly(
          new PSTLabel(
              toolsX,
              toolsY,
              Component.literal("Attach New Skill").withStyle(ChatFormatting.GREEN)));
      toolsY += 19;
      addRenderableOnly(
          new PSTLabel(
              toolsX, toolsY, Component.literal("• Distance").withStyle(ChatFormatting.GOLD)));
      addRenderableOnly(
          new PSTLabel(
              toolsX + 65, toolsY, Component.literal("• Angle").withStyle(ChatFormatting.GOLD)));
      toolsY += 19;
      PSTNumericEditBox distanceEditor = new PSTNumericEditBox(font, toolsX, toolsY, 60, 14, 10);
      addRenderableWidget(distanceEditor);
      PSTNumericEditBox angleEditor = new PSTNumericEditBox(font, toolsX + 65, toolsY, 60, 14, 0);
      addRenderableWidget(angleEditor);
      PSTButton addButton = new PSTButton(toolsX + 130, toolsY, 60, 14, Component.literal("Add"));
      addButton.setPressFunc(
          b -> {
            float angle = (float) (angleEditor.getNumericValue() * Mth.PI / 180F);
            float distance = (float) distanceEditor.getNumericValue();
            distance += skill.getButtonSize() / 2f + 8;
            float skillX = skill.getPositionX() + Mth.sin(angle) * distance;
            float skillY = skill.getPositionY() + Mth.cos(angle) * distance;
            createNewSkill(skillX, skillY, skill);
            rebuildWidgets();
          });
      addRenderableWidget(addButton);
      PSTButton copyButton = new PSTButton(toolsX + 195, toolsY, 60, 14, Component.literal("Copy"));
      copyButton.setPressFunc(
          b -> {
            float angle = (float) (angleEditor.getNumericValue() * Mth.PI / 180F);
            float distance = (float) distanceEditor.getNumericValue();
            distance += skill.getButtonSize() / 2 + 8;
            float skillX = skill.getPositionX() + Mth.sin(angle) * distance;
            float skillY = skill.getPositionY() + Mth.cos(angle) * distance;
            createCopiedSkill(skillX, skillY, skill);
            rebuildWidgets();
          });
      addRenderableWidget(copyButton);
      toolsY += 19;
    }
    addRenderableOnly(
        new PSTLabel(
            toolsX,
            toolsY,
            Component.literal("To remove selected skills press CTRL+DELETE")
                .withStyle(ChatFormatting.RED)));
    toolsY += 19;
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
      addRenderableOnly(
          new PSTLabel(toolsX, toolsY, Component.literal("• Size").withStyle(ChatFormatting.GOLD)));
      toolsY += 19;
      PSTNumericEditBox sizeEditor =
          new PSTNumericEditBox(font, toolsX, toolsY, 40, 14, skill.getButtonSize());
      sizeEditor.setNumericFilter(d -> d >= 2);
      sizeEditor.setResponder(
          s -> {
            getSelectedSkills()
                .forEach(
                    skill_ -> {
                      skill_.setButtonSize((int) sizeEditor.getNumericValue());
                    });
            getSelectedSkillButtons()
                .forEach(
                    button -> {
                      button.setButtonSize((int) sizeEditor.getNumericValue());
                    });
            saveSelectedSkills();
          });
      addRenderableWidget(sizeEditor);
      toolsY += 19;
    }
    if (selectedSkills.size() == 1) {
      toolsY -= 38;
      addRenderableOnly(
          new PSTLabel(
              toolsX + 65, toolsY, Component.literal("• Position").withStyle(ChatFormatting.GOLD)));
      toolsY += 19;
      PSTNumericEditBox xPosEditor =
          new PSTNumericEditBox(font, toolsX + 65, toolsY, 60, 14, skill.getPositionX());
      xPosEditor.setResponder(
          s -> {
            skill.setPosition((float) xPosEditor.getNumericValue(), skill.getPositionY());
            getSelectedSkillButtons()
                .forEach(
                    button -> {
                      button.setPosition(getSkillButtonX(skill), getSkillButtonY(skill));
                    });
            saveSelectedSkills();
          });
      addRenderableWidget(xPosEditor);
      PSTNumericEditBox yPosEditor =
          new PSTNumericEditBox(font, toolsX + 130, toolsY, 60, 14, skill.getPositionY());
      yPosEditor.setResponder(
          s -> {
            skill.setPosition(skill.getPositionX(), (float) yPosEditor.getNumericValue());
            getSelectedSkillButtons()
                .forEach(
                    button -> {
                      button.setPosition(getSkillButtonX(skill), getSkillButtonY(skill));
                    });
            saveSelectedSkills();
          });
      addRenderableWidget(yPosEditor);
      toolsY += 19;
    }
  }

  private void addAttributeToolsButtons() {
    ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
    PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
    if (getSelectedSkills().allMatch(otherSkill -> sameSkillBonuses(skill, otherSkill))) {
      addRenderableOnly(
          new PSTLabel(
              toolsX, toolsY, Component.literal("• Skill Bonuses").withStyle(ChatFormatting.GOLD)));
      toolsY += 19;
      for (int row = 0; row < skill.getBonuses().size(); row++) {
        addSkillBonusToolWidgets(skill, row);
        toolsY += 19;
      }
      addAddModifierButton();
      toolsY += 19;
    }
  }

  private void addTextureToolsButtons() {
    ResourceLocation skillId = (ResourceLocation) selectedSkills.toArray()[0];
    PassiveSkill skill = SkillTreeClientData.getEditorSkill(skillId);
    if (getSelectedSkills().allMatch(otherSkill -> sameTextures(skill, otherSkill))) {
      addRenderableOnly(
          new PSTLabel(
              toolsX, toolsY, Component.literal("Textures").withStyle(ChatFormatting.GREEN)));
      toolsY += 19;
      addRenderableOnly(
          new PSTLabel(
              toolsX, toolsY, Component.literal("• Border").withStyle(ChatFormatting.GOLD)));
      toolsY += 19;
      PSTEditBox backgroundEditor =
          new PSTEditBox(font, toolsX, toolsY, 355, 14, skill.getBackgroundTexture().toString());
      backgroundEditor.setResponder(
          value -> {
            getSelectedSkills()
                .forEach(
                    s -> {
                      s.setBackgroundTexture(new ResourceLocation(value));
                    });
            saveSelectedSkills();
          });
      addRenderableWidget(backgroundEditor);
      toolsY += 19;
      addRenderableOnly(
          new PSTLabel(toolsX, toolsY, Component.literal("• Icon").withStyle(ChatFormatting.GOLD)));
      toolsY += 19;
      PSTEditBox iconEditor =
          new PSTEditBox(font, toolsX, toolsY, 355, 14, skill.getIconTexture().toString());
      iconEditor.setResponder(
          value -> {
            getSelectedSkills()
                .forEach(
                    s -> {
                      s.setIconTexture(new ResourceLocation(value));
                    });
            saveSelectedSkills();
          });
      addRenderableWidget(iconEditor);
      toolsY += 19;
      addRenderableOnly(
          new PSTLabel(
              toolsX,
              toolsY,
              Component.literal("• Tooltip Background").withStyle(ChatFormatting.GOLD)));
      toolsY += 19;
      PSTEditBox borderEditor =
          new PSTEditBox(font, toolsX, toolsY, 355, 14, skill.getBorderTexture().toString());
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

  private void addAddModifierButton() {
    PSTButton addModifierButton =
        new PSTButton(toolsX, toolsY, 100, 14, Component.translatable("Add Modifier"));
    addModifierButton.setPressFunc(
        b -> {
          getSelectedSkills()
              .forEach(
                  skill -> {
                    skill
                        .getBonuses()
                        .add(
                            new AttributeSkillBonus(
                                Attributes.ARMOR,
                                new AttributeModifier(
                                    UUID.randomUUID(), "SkillTree", 1, Operation.ADDITION)));
                    SkillTreeClientData.saveEditorSkill(skill);
                    rebuildWidgets();
                  });
        });
    addRenderableWidget(addModifierButton);
  }

  private void addSkillBonusToolWidgets(PassiveSkill skill, int row) {
    skill.getBonuses().get(row).addEditorWidgets(this, row);
  }

  private void addConnectionToolsButton() {
    addRenderableOnly(
        new PSTLabel(
            toolsX, toolsY, Component.literal("• Connections").withStyle(ChatFormatting.GOLD)));
    toolsY += 19;
    ResourceLocation first = (ResourceLocation) selectedSkills.toArray()[0];
    ResourceLocation second = (ResourceLocation) selectedSkills.toArray()[1];
    PassiveSkill firstSkill = SkillTreeClientData.getEditorSkill(first);
    PassiveSkill secondSkill = SkillTreeClientData.getEditorSkill(second);
    if (areSkillsConnected(firstSkill, secondSkill)) {
      PSTButton disconnectButton =
          new PSTButton(toolsX, toolsY, 100, 14, Component.literal("Disconnect"));
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
      PSTButton connectButton =
          new PSTButton(toolsX, toolsY, 110, 14, Component.literal("Connect"));
      addRenderableWidget(connectButton);
      connectButton.setPressFunc(
          b -> {
            skillButtons.get(first).skill.getConnectedSkills().add(second);
            saveSelectedSkills();
            rebuildWidgets();
          });
      PSTButton connectAsGatewayButton =
          new PSTButton(toolsX + 115, toolsY, 110, 14, Component.literal("Connect as Gateway"));
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
            connectedSkillId -> {
              connectSkills(gatewayConnections, skill.getId(), connectedSkillId);
            });
  }

  protected void connectSkills(
      List<SkillConnection> connections, ResourceLocation skillId1, ResourceLocation skillId2) {
    SkillButton button1 = skillButtons.get(skillId1);
    SkillButton button2 = skillButtons.get(skillId2);
    connections.add(new SkillConnection(button1, button2));
  }

  public void renderSkillTooltip(
      SkillButton button, PoseStack poseStack, float mouseX, float mouseY) {
    List<MutableComponent> tooltip = button.getTooltip();
    if (tooltip.isEmpty()) return;
    int tooltipWidth = 0;
    int tooltipHeight = tooltip.size() == 1 ? 8 : 10;
    for (MutableComponent component : tooltip) {
      int k = font.width(component);
      if (k > tooltipWidth) tooltipWidth = k;
      tooltipHeight += font.lineHeight;
    }
    tooltipWidth += 42;
    float tooltipX = mouseX + 12;
    float tooltipY = mouseY - 12;
    if (tooltipX + tooltipWidth > width) {
      tooltipX -= 28 + tooltipWidth;
    }
    if (tooltipY + tooltipHeight + 6 > height) {
      tooltipY = height - tooltipHeight - 6;
    }
    poseStack.pushPose();
    poseStack.translate(tooltipX, tooltipY, 0);
    float zOffset = itemRenderer.blitOffset;
    itemRenderer.blitOffset = 400.0F;
    fill(poseStack, 1, 4, tooltipWidth - 1, tooltipHeight + 4, 0xDD000000);
    RenderSystem.enableTexture();
    MultiBufferSource.BufferSource buffer =
        MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
    poseStack.translate(0.0D, 0.0D, 400.0D);
    int textX = 5;
    int textY = 2;
    ScreenHelper.prepareTextureRendering(button.skill.getBorderTexture());
    blit(poseStack, -4, -4, 0, 0, 21, 20, 110, 20);
    blit(poseStack, tooltipWidth + 4 - 21, -4, -21, 0, 21, 20, 110, 20);
    int centerWidth = tooltipWidth + 8 - 42;
    int centerX = -4 + 21;
    while (centerWidth > 0) {
      int partWidth = Math.min(centerWidth, 68);
      blit(poseStack, centerX, -4, 21, 0, partWidth, 20, 110, 20);
      centerX += partWidth;
      centerWidth -= partWidth;
    }
    MutableComponent title = tooltip.remove(0);
    drawCenteredString(poseStack, font, title, tooltipWidth / 2, textY, 0xFFFFFF);
    textY += 17;
    for (MutableComponent component : tooltip) {
      font.draw(poseStack, component, textX, textY, 0xFFFFFF);
      textY += font.lineHeight;
    }
    buffer.endBatch();
    poseStack.popPose();
    itemRenderer.blitOffset = zOffset;
  }

  public void buttonPressed(Button button) {
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

  private void renderOverlay(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
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
    int size = getBackgroundSize();
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
  public PSTEditBox addTextField(int x, int y, int width, int height, String defaultValue) {
    assert minecraft != null;
    return addRenderableWidget(
        new PSTEditBox(minecraft.font, toolsX + x, toolsY + y, width, height, defaultValue));
  }

  @Override
  public PSTNumericEditBox addNumericTextField(
      int x, int y, int width, int height, double defaultValue) {
    assert minecraft != null;
    return addRenderableWidget(
        new PSTNumericEditBox(minecraft.font, toolsX + x, toolsY + y, width, height, defaultValue));
  }

  @Override
  public PSTButton addRemoveButton(int x, int y, int width, int height) {
    return addRenderableWidget(new RemoveButton(toolsX + x, toolsY + y, width, height));
  }

  @Override
  public <T extends Enum<T>> EnumCycleButton<T> addEnumCycleButton(
      int x, int y, int width, int height, T defaultValue) {
    return addRenderableWidget(
        new EnumCycleButton<>(toolsX + x, toolsY + y, width, height, defaultValue));
  }

  protected void renderConnections(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
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
    Optional<SkillButton> hoveredSkill = getSkillAt(mouseX, mouseY);
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
    boolean hovered =
        !hoveredSkill.isEmpty() && (hoveredSkill.get() == button1 || hoveredSkill.get() == button2);
    if (hovered) {
      if (hoveredSkill.get() == button2) renderGatewayConnection(poseStack, button2, button1);
      else renderGatewayConnection(poseStack, button1, button2);
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

  protected int getBackgroundSize() {
    return 2048;
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
