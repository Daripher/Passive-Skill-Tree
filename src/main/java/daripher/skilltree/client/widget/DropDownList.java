package daripher.skilltree.client.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DropDownList<T> extends AbstractButton {
  public static final ResourceLocation WIDGETS_LOCATION =
      new ResourceLocation("skilltree:textures/screen/widgets.png");
  private Function<T, Component> toNameFunc = t -> Component.literal(t.toString());
  private Consumer<T> responder = t -> {};
  private final List<T> possibleValues;
  private T value;
  private String search = "";
  private final int maxDisplayed;
  private final int maxScroll;
  private int scroll;
  private int searchTimer;
  private boolean opened;

  public DropDownList(
      int x,
      int y,
      int width,
      int height,
      int maxDisplayed,
      Collection<T> possibleValues,
      T value) {
    super(x, y, width, height, Component.empty());
    this.possibleValues = new ArrayList<>(possibleValues);
    this.value = value;
    maxDisplayed = Math.min(maxDisplayed, possibleValues.size());
    this.maxDisplayed = maxDisplayed;
    this.maxScroll = possibleValues.size() - maxDisplayed;
  }

  @Override
  public void renderWidget(
      @NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    if (!visible) return;
    int v = isHoveredOrFocused() ? 42 : 56;
    graphics.blit(WIDGETS_LOCATION, getX(), getY(), 0, v, width / 2, height);
    graphics.blit(WIDGETS_LOCATION, getX() + width / 2, getY(), -width / 2, v, width / 2, height);
    Minecraft minecraft = Minecraft.getInstance();
    Font font = minecraft.font;
    String visibleText = font.plainSubstrByWidth(toNameFunc.apply(value).getString(), width - 8);
    graphics.drawString(font, visibleText, getX() + 5, getY() + 3, 0xe0e0e0, true);
  }

  public void renderList(GuiGraphics graphics) {
    if (!opened) return;
    int y = this.getY() + height + 1;
    graphics.blit(WIDGETS_LOCATION, getX(), y, 0, 42, width / 2, height / 2);
    graphics.blit(WIDGETS_LOCATION, getX() + width / 2, y, -width / 2, 42, width / 2, height / 2);
    for (int i = 0; i < maxDisplayed - 1; i++) {
      int rowY = y + height / 2 + i * height;
      graphics.blit(WIDGETS_LOCATION, getX(), rowY, 0, 70, width / 2, height);
      graphics.blit(WIDGETS_LOCATION, getX() + width / 2, rowY, -width / 2, 70, width / 2, height);
    }
    y += (maxDisplayed - 1) * height + height / 2;
    graphics.blit(WIDGETS_LOCATION, getX(), y, 0, 42 + height / 2, width / 2, height / 2);
    graphics.blit(
        WIDGETS_LOCATION,
        getX() + width / 2,
        y,
        -width / 2,
        42 + height / 2,
        width / 2,
        height / 2);
    y -= (maxDisplayed - 1) * height + height / 2;
    Minecraft minecraft = Minecraft.getInstance();
    Font font = minecraft.font;
    for (int i = 0; i < maxDisplayed; i++) {
      String line = toNameFunc.apply(possibleValues.get(i + scroll)).getString();
      if (font.width(line) > width - 10) {
        while (font.width(line + "...") > width - 10) {
          line = line.substring(0, line.length() - 1);
        }
        line += "...";
      }
      int textX = getX() + 5;
      int textY = y + 3 + i * height;
      String lowerCase = line.toLowerCase();
      if (!search.isEmpty() && lowerCase.contains(search)) {
        String split1 = line.substring(0, lowerCase.indexOf(search));
        graphics.drawString(font, split1, textX, textY, 0xe0e0e0);
        textX += font.width(split1);
        String split2 =
            line.substring(lowerCase.indexOf(search), lowerCase.indexOf(search) + search.length());
        graphics.drawString(font, split2, textX, textY, 0xFFD642);
        textX += font.width(split2);
        String split3 = line.substring(lowerCase.indexOf(search) + search.length());
        graphics.drawString(font, split3, textX, textY, 0xe0e0e0);
      } else {
        graphics.drawString(font, line, textX, textY, 0xe0e0e0);
      }
    }
  }

  @Override
  protected boolean clicked(double mouseX, double mouseY) {
    boolean clicked = super.clicked(mouseX, mouseY) || clickedList(mouseX, mouseY);
    if (!clicked) opened = false;
    return clicked;
  }

  @Override
  public void onClick(double mouseX, double mouseY) {
    if (clickedList(mouseX, mouseY)) {
      int clickedLine = ((int) mouseY - getY() - height - 1) / height + scroll;
      value = possibleValues.get(clickedLine);
      responder.accept(value);
      opened = false;
      return;
    }
    if (mouseX >= getX()
        && mouseX < getX() + width
        && mouseY >= getY()
        && mouseY < getY() + height) {
      this.onPress();
    } else {
      opened = false;
    }
  }

  private boolean clickedList(double mouseX, double mouseY) {
    return opened
        && mouseX >= getX()
        && mouseX < getX() + width
        && mouseY >= getY() + height + 1
        && mouseY < getY() + height + 1 + maxDisplayed * height;
  }

  @Override
  public void onPress() {
    opened ^= true;
  }

  @Override
  public void mouseMoved(double mouseX, double mouseY) {
    super.mouseMoved(mouseX, mouseY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (!opened) return false;
    setScroll(scroll - Mth.sign(delta));
    return true;
  }

  private void setScroll(int scroll) {
    this.scroll = Math.min(maxScroll, Math.max(0, scroll));
  }

  @Override
  public boolean charTyped(char codePoint, int modifiers) {
    if (!opened) {
      return false;
    } else if (SharedConstants.isAllowedChatCharacter(codePoint)) {
      searchTimer = 40;
      search += Character.toString(codePoint).toLowerCase();
      possibleValues.stream()
          .filter(v -> toNameFunc.apply(v).getString().toLowerCase().contains(search))
          .findFirst()
          .ifPresent(v -> setScroll(possibleValues.indexOf(v)));
      return true;
    } else {
      return false;
    }
  }

  public void tick() {
    if (searchTimer == 0) {
      search = "";
    }
    if (searchTimer > 0) {
      searchTimer--;
    }
  }

  public DropDownList<T> setToNameFunc(Function<T, Component> function) {
    this.toNameFunc = function;
    possibleValues.sort(
        (v1, v2) -> {
          String name1 = function.apply(v1).getString();
          String name2 = function.apply(v2).getString();
          return name1.compareTo(name2);
        });
    return this;
  }

  public DropDownList<T> setResponder(Consumer<T> responder) {
    this.responder = responder;
    return this;
  }

  public T getValue() {
    return value;
  }

  @Override
  public boolean isHoveredOrFocused() {
    return super.isHoveredOrFocused() || opened;
  }

  @Override
  protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {}
}
