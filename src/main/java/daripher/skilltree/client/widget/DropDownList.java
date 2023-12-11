package daripher.skilltree.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.client.screen.ScreenHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
  public void renderButton(
      @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    if (!visible) return;
    ScreenHelper.prepareTextureRendering(WIDGETS_LOCATION);
    int v = isHoveredOrFocused() ? 42 : 56;
    blit(poseStack, x, y, 0, v, width / 2, height);
    blit(poseStack, x + width / 2, y, -width / 2, v, width / 2, height);
    Minecraft minecraft = Minecraft.getInstance();
    Font font = minecraft.font;
    String visibleText = font.plainSubstrByWidth(toNameFunc.apply(value).getString(), width - 8);
    font.drawShadow(poseStack, visibleText, x + 5, y + 3, 0xe0e0e0);
  }

  public void renderList(PoseStack poseStack) {
    if (!opened) return;
    RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
    int y = this.y + height + 1;
    blit(poseStack, x, y, 0, 42, width / 2, height / 2);
    blit(poseStack, x + width / 2, y, -width / 2, 42, width / 2, height / 2);
    for (int i = 0; i < maxDisplayed - 1; i++) {
      int rowY = y + height / 2 + i * height;
      blit(poseStack, x, rowY, 0, 70, width / 2, height);
      blit(poseStack, x + width / 2, rowY, -width / 2, 70, width / 2, height);
    }
    y += (maxDisplayed - 1) * height + height / 2;
    blit(poseStack, x, y, 0, 42 + height / 2, width / 2, height / 2);
    blit(poseStack, x + width / 2, y, -width / 2, 42 + height / 2, width / 2, height / 2);
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
      if (!search.isEmpty() && line.toLowerCase().startsWith(search)) {
        String search = line.substring(0, this.search.length());
        font.drawShadow(poseStack, search, x + 5, y + 3 + i * height, 0xFFD642);
        line = line.substring(search.length());
        font.drawShadow(poseStack, line, x + 5 + font.width(search), y + 3 + i * height, 0xe0e0e0);
      } else {
        font.drawShadow(poseStack, line, x + 5, y + 3 + i * height, 0xe0e0e0);
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
      int clickedLine = ((int) mouseY - y - height - 1) / height + scroll;
      value = possibleValues.get(clickedLine);
      responder.accept(value);
      opened = false;
      return;
    }
    if (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
      this.onPress();
    } else {
      opened = false;
    }
  }

  private boolean clickedList(double mouseX, double mouseY) {
    return opened
        && mouseX >= x
        && mouseX < x + width
        && mouseY >= y + height + 1
        && mouseY < y + height + 1 + maxDisplayed * height;
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
          .filter(v -> toNameFunc.apply(v).getString().toLowerCase().startsWith(search))
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
  public void updateNarration(@NotNull NarrationElementOutput output) {}
}
