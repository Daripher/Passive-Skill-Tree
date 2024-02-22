package daripher.skilltree.client.widget;

import daripher.skilltree.mixin.EditBoxAccessor;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class TextField extends EditBox {
  public static final int INVALID_TEXT_COLOR = 0xD80000;
  private static final int HINT_COLOR = 0x575757;
  private Predicate<String> softFilter = Objects::nonNull;
  private Function<String, @Nullable String> suggestionProvider = s -> null;
  private String hint = null;

  public TextField(Font font, int x, int y, int width, int height, String defaultText) {
    super(font, x, y, width, height, Component.empty());
    setMaxLength(80);
    setValue(defaultText);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    EditBoxAccessor accessor = (EditBoxAccessor) this;
    if (keyCode == GLFW.GLFW_KEY_TAB && accessor.getSuggestion() != null) {
      setValue(getValue() + accessor.getSuggestion());
      setSuggestion(null);
      return true;
    }
    boolean result = super.keyPressed(keyCode, scanCode, modifiers);
    setSuggestion(suggestionProvider.apply(getValue()));
    return result;
  }

  @Override
  public boolean charTyped(char codePoint, int modifiers) {
    boolean result = super.charTyped(codePoint, modifiers);
    setSuggestion(suggestionProvider.apply(getValue()));
    return result;
  }

  @Override
  public void setResponder(@NotNull Consumer<String> responder) {
    super.setResponder(
        s -> {
          if (!isValueValid()) return;
          responder.accept(s);
        });
  }

  public void setSuggestionProvider(Function<String, @Nullable String> suggestionProvider) {
    this.suggestionProvider = suggestionProvider;
  }

  public TextField setSoftFilter(Predicate<String> filter) {
    this.softFilter = filter;
    return this;
  }

  @Override
  public void renderWidget(
      @NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    EditBoxAccessor accessor = (EditBoxAccessor) this;
    if (!isVisible()) return;
    ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/widgets.png");
    int v = isHoveredOrFocused() ? 42 : 56;
    graphics.blit(texture, getX(), getY(), 0, v, width / 2, height);
    graphics.blit(texture, getX() + width / 2, getY(), -width / 2, v, width / 2, height);
    int textColor = getTextColor();
    int cursorVisiblePosition = getCursorPosition() - accessor.getDisplayPos();
    int highlightWidth = accessor.getHighlightPos() - accessor.getDisplayPos();
    Minecraft minecraft = Minecraft.getInstance();
    Font font = minecraft.font;
    String visibleText =
        font.plainSubstrByWidth(getValue().substring(accessor.getDisplayPos()), getInnerWidth());
    boolean isTextSplitByCursor =
        cursorVisiblePosition >= 0 && cursorVisiblePosition <= visibleText.length();
    boolean isCursorVisible =
        isFocused() && accessor.getFrame() / 6 % 2 == 0 && isTextSplitByCursor;
    if (visibleText.isEmpty() && hint != null && !isFocused()) {
      visibleText = hint;
    }
    int textX = getX() + 5;
    int textStartX = textX;
    int textY = getY() + 3;
    if (highlightWidth > visibleText.length()) highlightWidth = visibleText.length();
    if (!visibleText.isEmpty()) {
      String s1 =
          isTextSplitByCursor ? visibleText.substring(0, cursorVisiblePosition) : visibleText;
      textX =
          graphics.drawString(
              font,
              accessor.getFormatter().apply(s1, accessor.getDisplayPos()),
              textX,
              textY,
              textColor,
              true);
    }
    boolean isCursorSurrounded =
        getCursorPosition() < getValue().length() || getValue().length() >= accessor.getMaxLength();
    int cursorX = textX;
    if (!isTextSplitByCursor) {
      cursorX = cursorVisiblePosition > 0 ? getX() + this.width : getX();
    } else if (isCursorSurrounded) {
      cursorX = textX - 1;
      --textX;
    }
    if (!visibleText.isEmpty()
        && isTextSplitByCursor
        && cursorVisiblePosition < visibleText.length()) {
      graphics.drawString(
          font,
          accessor
              .getFormatter()
              .apply(visibleText.substring(cursorVisiblePosition), getCursorPosition()),
          textX,
          textY,
          textColor,
          true);
    }
    if (!isCursorSurrounded && accessor.getSuggestion() != null) {
      graphics.drawString(font, accessor.getSuggestion(), cursorX - 1, textY, -8355712, true);
    }
    if (isCursorVisible) {
      if (isCursorSurrounded) graphics.fill(cursorX, textY - 1, cursorX + 1, textY + 9, -3092272);
      else graphics.drawString(font, "_", cursorX, textY, textColor, true);
    }
    if (highlightWidth != cursorVisiblePosition) {
      int highlightEndX = textStartX + font.width(visibleText.substring(0, highlightWidth));
      accessor.invokeRenderHighlight(graphics, cursorX, textY - 1, highlightEndX - 1, textY + 9);
    }
  }

  public boolean isValueValid() {
    return softFilter.test(getValue());
  }

  public TextField setHint(@Nullable String hint) {
    this.hint = hint;
    return this;
  }

  private int getTextColor() {
    return getValue().isEmpty()
        ? HINT_COLOR
        : isValueValid() ? DEFAULT_TEXT_COLOR : INVALID_TEXT_COLOR;
  }
}
