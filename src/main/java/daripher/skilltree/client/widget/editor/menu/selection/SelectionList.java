package daripher.skilltree.client.widget.editor.menu.selection;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class SelectionList<T> extends AbstractButton {
  public static final ResourceLocation WIDGETS_TEXTURE =
      ResourceLocation.parse("skilltree:textures/screen/widgets.png");
  private Function<T, Component> nameGetter = t -> Component.literal(t.toString());
  private Consumer<T> responder = t -> {};
  private List<T> elementsList;
  private String search = "";
  private T selectedElement;
  protected int elementHeight;
  protected int elementWidth;
  private int rows = 1;
  private int columns = 1;
  private int maxScroll;
  private int scroll;

  public SelectionList(
      int x, int y, int elementWidth, int elementHeight, Collection<T> elementsList) {
    super(x, y, elementWidth, elementHeight, Component.empty());
    this.elementsList = new ArrayList<>(elementsList);
    this.elementWidth = elementWidth;
    this.elementHeight = elementHeight;
    setRows(Math.min(elementsList.size(), 10));
    setColumns(1);
  }

  @Override
  public void onPress() {
    responder.accept(selectedElement);
  }

  @Override
  public void renderWidget(
      @NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    if (!visible) {
      return;
    }
    RenderSystem.enableBlend();
    renderBackground(graphics, mouseX, mouseY);
    renderElements(graphics);
    renderScroll(graphics);
    RenderSystem.disableBlend();
  }

  private void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
    renderBackgroundLine(graphics, getX(), getY(), 42, width, 7);
    renderBackgroundLine(graphics, getX(), getY() + getHeight() - 7, 49, width, 7);
    int centerHeight = getHeight() - 14;
    for (int height = centerHeight; height > 0; height -= 14) {
      int centerLineY = getY() + 7 + centerHeight - height;
      int centerLineHeight = Math.min(14, height);
      renderBackgroundLine(graphics, getX(), centerLineY, 70, width, centerLineHeight);
    }
    renderElementHover(graphics, mouseX, mouseY);
  }

  private void renderElementHover(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
    int slotX = (mouseX - getX() - 5) / elementWidth;
    int slotY = (mouseY - getY() - 5) / elementHeight;
    slotX = Math.min(columns - 1, Math.max(0, slotX));
    slotY = Math.min(rows - 1, Math.max(0, slotY));
    int x = getX() + 5 + elementWidth * slotX;
    int y = getY() + 5 + elementHeight * slotY;
    if (elementHeight > 14) {
      renderBackgroundLine(graphics, x, y, 84, elementWidth, 7);
      renderBackgroundLine(graphics, x, y + elementHeight - 7, 91, elementWidth, 7);
      renderBackgroundLine(graphics, x, y + 7, 98, elementWidth, elementHeight - 14);
    } else {
      renderBackgroundLine(graphics, x, y, 84, elementWidth, elementHeight);
    }
  }

  private void renderBackgroundLine(
      @NotNull GuiGraphics graphics, int x, int y, int textureOffset, int width, int height) {
    ResourceLocation texture = WIDGETS_TEXTURE;
    graphics.blit(texture, x, y, 0, textureOffset, width / 2, height);
    graphics.blit(texture, x + width / 2, y, -width / 2, textureOffset, width / 2, height);
  }

  private void renderElements(@NotNull GuiGraphics graphics) {
    List<T> displayedElements = getDisplayedElements();
    int elementIndex = 0;
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        if (elementIndex + scroll * columns >= displayedElements.size()) {
          break;
        }
        int x = getX() + 5 + column * elementWidth;
        int y = getY() + 5 + row * elementHeight;
        renderElement(graphics, elementIndex + scroll * columns, x, y);
        elementIndex++;
      }
    }
  }

  protected abstract void renderElement(
      @NotNull GuiGraphics graphics, int elementIndex, int x, int y);

  protected List<T> getDisplayedElements() {
    if (!search.isEmpty()) {
      return elementsList.stream().filter(this::shouldDisplay).toList();
    }
    return elementsList;
  }

  private boolean shouldDisplay(T value) {
    return nameGetter.apply(value).getString().toLowerCase(Locale.ROOT).contains(search);
  }

  private void renderScroll(GuiGraphics graphics) {
    if (maxScroll == 0) {
      return;
    }
    int maxScrollSize = height - 8;
    int scrollSize = maxScrollSize / (maxScroll + 1);
    int x = getX() + width - 4;
    int y = getY() + 3 + (maxScrollSize - scrollSize) * scroll / Math.max(maxScroll, 1);
    graphics.fill(x, y, x + 1, y + scrollSize + 1, 0xffaaaaaa);
  }

  @Override
  public void onClick(double mouseX, double mouseY) {
    if (!clicked(mouseX, mouseY)) {
      return;
    }
    int hoveredElement = getHoveredElement((int) mouseX, (int) mouseY);
    List<T> displayedElements = getDisplayedElements();
    if (hoveredElement >= displayedElements.size()) {
      return;
    }
    selectedElement = displayedElements.get(hoveredElement);
    onPress();
  }

  private int getHoveredElement(int mouseX, int mouseY) {
    return (mouseX - getX() - 5) / elementWidth
        + ((mouseY - getY() - 5) / elementHeight + scroll) * columns;
  }

  @Override
  public void mouseMoved(double mouseX, double mouseY) {
    super.mouseMoved(mouseX, mouseY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (isMouseOver(mouseX, mouseY)) {
      setScroll(scroll - Mth.sign(delta));
      return true;
    }
    return false;
  }

  private void setScroll(int scroll) {
    this.scroll = Math.min(maxScroll, Math.max(0, scroll));
  }

  public SelectionList<T> setNameGetter(Function<T, Component> nameGetter) {
    this.nameGetter = nameGetter;
    sortValues();
    setScrollToSelection();
    return this;
  }

  private void sortValues() {
    getDisplayedElements().sort(Comparator.comparing(t -> nameGetter.apply(t).getString()));
  }

  public Function<T, Component> getNameGetter() {
    return nameGetter;
  }

  public SelectionList<T> setResponder(Consumer<T> responder) {
    this.responder = responder;
    return this;
  }

  public T getSelectedElement() {
    return selectedElement;
  }

  public SelectionList<T> selectElement(T element) {
    this.selectedElement = element;
    setScrollToSelection();
    return this;
  }

  public SelectionList<T> setRows(int rows) {
    rows = Math.max(1, Math.min(elementsList.size() / columns, rows));
    this.rows = rows;
    updateSize();
    updateMaxScroll();
    return this;
  }

  public SelectionList<T> setColumns(int columns) {
    this.columns = Math.max(1, columns);
    updateSize();
    updateMaxScroll();
    return this;
  }

  private void updateMaxScroll() {
    this.maxScroll =
        (int) Math.max(Math.ceil((float) getDisplayedElements().size() / columns) - rows, 0);
  }

  public void setScrollToSelection() {
    setScroll(getDisplayedElements().indexOf(selectedElement));
  }

  public String getElementName(T element) {
    return nameGetter.apply(element).getString();
  }

  public String getSearchString() {
    return search;
  }

  public void setSearchString(String search) {
    this.search = search.toLowerCase(Locale.ROOT);
    setScrollToSelection();
  }

  @Override
  protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {}

  public void setElementSize(int width, int height) {
    elementWidth = width;
    elementHeight = height;
    updateSize();
    updateMaxScroll();
  }

  private void updateSize() {
    setHeight(elementHeight * rows + 10);
    setWidth(elementWidth * columns + 10);
  }

  public void setElementsList(Collection<T> elements) {
    this.elementsList = new ArrayList<>(elements);
    elementsList.sort(Comparator.comparing(element -> getNameGetter().apply(element).getString()));
    updateMaxScroll();
    setScrollToSelection();
  }
}
