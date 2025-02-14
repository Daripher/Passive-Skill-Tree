package daripher.skilltree.client.widget.editor;

import daripher.skilltree.client.screen.ScreenHelper;
import daripher.skilltree.client.widget.skill.SkillButton;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.geom.Rectangle2D;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SkillSelector extends AbstractWidget {
  private static final int SELECTION_COLOR = 0xEE95EB34;
  private final Set<PassiveSkill> selectedSkills = new LinkedHashSet<>();
  private final SkillButtons skillButtons;
  private final SkillTreeEditor editor;
  private int selectionStartX;
  private int selectionStartY;

  public SkillSelector(SkillTreeEditor editor, SkillButtons skillButtons) {
    super(0, 0, 0, 0, Component.empty());
    this.skillButtons = skillButtons;
    this.editor = editor;
    this.active = false;
  }

  @Override
  protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    if (active) {
      renderSelectionArea(graphics, mouseX, mouseY);
    }
    renderSelectedSkillsHighlight(graphics);
  }

  private void renderSelectedSkillsHighlight(@NotNull GuiGraphics graphics) {
    graphics.pose()
        .pushPose();
    graphics.pose()
        .translate(skillButtons.getScrollX(), skillButtons.getScrollY(), 0);
    float zoom = skillButtons.getZoom();
    for (SkillButton widget : getSelectedButtons()) {
      renderSkillSelection(graphics, widget, zoom);
    }
    graphics.pose()
        .popPose();
  }

  private void renderSkillSelection(@NotNull GuiGraphics graphics, SkillButton widget, float zoom) {
    graphics.pose()
        .pushPose();
    double widgetCenterX = widget.getX() + widget.getWidth() / 2f;
    double widgetCenterY = widget.getY() + widget.getHeight() / 2f;
    graphics.pose()
        .translate(widgetCenterX, widgetCenterY, 0F);
    graphics.pose()
        .scale(zoom, zoom, 1F);
    graphics.pose()
        .translate(-widgetCenterX, -widgetCenterY, 0F);
    int x = widget.getX() - 1;
    int y = widget.getY() - 1;
    int width = widget.getWidth() + 2;
    int height = widget.getHeight() + 2;
    ScreenHelper.drawRectangle(graphics, x, y, width, height, SELECTION_COLOR);
    graphics.pose()
        .popPose();
  }

  private void renderSelectionArea(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
    ScreenHelper.drawRectangle(graphics, selectionStartX, selectionStartY, mouseX - selectionStartX, mouseY - selectionStartY, SELECTION_COLOR);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
      return false;
    }
    if (editor.getArea()
        .contains(mouseX, mouseY)) {
      return false;
    }
    if (Screen.hasControlDown()) {
      return false;
    }
    if (Screen.hasShiftDown()) {
      active = true;
      selectionStartX = (int) mouseX;
      selectionStartY = (int) mouseY;
    }
    else {
      if (!selectedSkills.isEmpty()) {
        clearSelection();
      }
      SkillButton clickedWidget = skillButtons.getWidgetAt(mouseX, mouseY);
      if (clickedWidget == null) {
        return false;
      }
      PassiveSkill clickedSkill = clickedWidget.skill;
      if (selectedSkills.contains(clickedSkill)) {
        selectedSkills.remove(clickedSkill);
      }
      else {
        selectedSkills.add(clickedSkill);
      }
      editor.rebuildWidgets();
    }
    return true;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (active) {
      addSelectedSkills(mouseX, mouseY);
      active = false;
      editor.rebuildWidgets();
      return true;
    }
    return false;
  }

  private void addSelectedSkills(double mouseX, double mouseY) {
    Rectangle2D selectedArea = getSelectionArea(mouseX, mouseY);
    for (SkillButton skillButton : skillButtons.getWidgets()) {
      Rectangle2D skillArea = getSkillArea(skillButton);
      if (selectedArea.intersects(skillArea)) {
        selectedSkills.add(skillButton.skill);
      }
    }
    editor.rebuildWidgets();
  }

  @NotNull
  private Rectangle2D getSelectionArea(double mouseX, double mouseY) {
    double x = Math.min(mouseX, selectionStartX) - skillButtons.getScrollX();
    double y = Math.min(mouseY, selectionStartY) - skillButtons.getScrollY();
    double width = Math.abs(mouseX - selectionStartX);
    double height = Math.abs(mouseY - selectionStartY);
    return new Rectangle2D.Double(x, y, width, height);
  }

  @NotNull
  private Rectangle2D getSkillArea(SkillButton skill) {
    double skillSize = skill.skill.getSkillSize() * skillButtons.getZoom();
    double skillX = skill.x + skill.getWidth() / 2d - skillSize / 2;
    double skillY = skill.y + skill.getHeight() / 2d - skillSize / 2;
    return new Rectangle2D.Double(skillX, skillY, skillSize, skillSize);
  }

  public Set<PassiveSkill> getSelectedSkills() {
    return selectedSkills;
  }

  public void clearSelection() {
    selectedSkills.clear();
    editor.rebuildWidgets();
  }

  public @Nullable PassiveSkill getFirstSelectedSkill() {
    if (selectedSkills.isEmpty()) return null;
    return (PassiveSkill) selectedSkills.toArray()[0];
  }

  @NotNull
  private List<SkillButton> getSelectedButtons() {
    return selectedSkills.stream()
        .map(PassiveSkill::getId)
        .map(skillButtons::getWidgetById)
        .toList();
  }

  @Override
  protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
  }
}
