package daripher.skilltree.client.widget.group;

import daripher.skilltree.client.widget.TickingWidget;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class WidgetGroup<T extends AbstractWidget> extends AbstractWidget implements TickingWidget {
  protected final Set<T> widgets = new HashSet<>();
  protected Runnable rebuildFunc = () -> {};

  public WidgetGroup(int x, int y, int width, int height) {
    super(x, y, width, height, Component.empty());
  }

  @Override
  protected void renderWidget(
      @NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    widgetsCopy().forEach(widget -> widget.render(graphics, mouseX, mouseY, partialTick));
    graphics.pose().pushPose();
    graphics.pose().translate(0, 0, 1f);
    graphics.pose().popPose();
  }

  @Override
  protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {}

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    boolean result = false;
    for (T widget : widgetsCopy()) {
      if (widget.keyPressed(keyCode, scanCode, modifiers)) {
        result = true;
      }
    }
    return result;
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    boolean result = false;
    for (T widget : widgetsCopy()) {
      if (widget.keyReleased(keyCode, scanCode, modifiers)) {
        result = true;
      }
    }
    return result;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    boolean result = false;
    for (T widget : widgetsCopy()) {
      if (widget.mouseClicked(mouseX, mouseY, button)) {
        result = true;
      }
    }
    return result;
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double dragX, double dragY) {
    boolean result = false;
    for (T widget : widgetsCopy()) {
      if (widget.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
        result = true;
      }
    }
    return result;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    boolean result = false;
    for (T widget : widgetsCopy()) {
      if (widget.mouseReleased(mouseX, mouseY, button)) {
        result = true;
      }
    }
    return result;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    boolean result = false;
    for (T widget : widgetsCopy()) {
      if (widget.mouseScrolled(mouseX, mouseY, delta)) {
        result = true;
      }
    }
    return result;
  }

  @Override
  public boolean charTyped(char codePoint, int modifiers) {
    boolean result = false;
    for (T widget : widgetsCopy()) {
      if (widget.charTyped(codePoint, modifiers)) {
        result = true;
      }
    }
    return result;
  }

  @Override
  public void mouseMoved(double mouseX, double mouseY) {
    widgetsCopy().forEach(widget -> widget.mouseMoved(mouseX, mouseY));
  }

  public void onWidgetTick() {
    for (T t : widgetsCopy()) {
      if (t instanceof TickingWidget tickingWidget) {
        tickingWidget.onWidgetTick();
      }
    }
  }

  public <W extends T> @NotNull W addWidget(@NotNull W widget) {
    widgets.add(widget);
    return widget;
  }

  public Set<T> getWidgets() {
    return widgets;
  }

  public void clearWidgets() {
    widgets.clear();
  }

  public void setRebuildFunc(Runnable rebuildFunc) {
    this.rebuildFunc = rebuildFunc;
  }

  public void rebuildWidgets() {
    rebuildFunc.run();
  }

  protected HashSet<T> widgetsCopy() {
    return new HashSet<>(widgets);
  }

  public Rectangle2D.Float getArea() {
    return new Rectangle2D.Float(getX(), getY(), width, height);
  }

  public @Nullable T getWidgetAt(double mouseX, double mouseY) {
    for (T widget : widgets) {
      Rectangle2D.Double widgetArea = getWidgetArea(widget);
      if (widgetArea.contains(mouseX, mouseY)) return widget;
    }
    return null;
  }

  protected @NotNull Rectangle2D.Double getWidgetArea(T widget) {
    double width = widget.getWidth();
    double height = widget.getHeight();
    double x = widget.getX() + width / 2d - width / 2;
    double y = widget.getY() + height / 2d - height / 2;
    return new Rectangle2D.Double(x, y, width, height);
  }
}
