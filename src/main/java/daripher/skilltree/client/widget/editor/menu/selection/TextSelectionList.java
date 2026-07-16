package daripher.skilltree.client.widget.editor.menu.selection;

import daripher.skilltree.client.tooltip.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Locale;

public class TextSelectionList<T> extends SelectionList<T> {
    public TextSelectionList(int x, int y, int elementWidth, int elementHeight, Collection<T> elementsList) {
        super(x, y, elementWidth, elementHeight, elementsList);
    }

    @Override
    protected void renderElement(@NotNull GuiGraphics graphics, int elementIndex, int x, int y) {
        T element = getDisplayedElements().get(elementIndex);
        String elementName = getElementName(element);
        String selectedElementName = getElementName(getSelectedElement());
        int textColor = elementName.equals(selectedElementName) ? 0x55ff55 : 0xe0e0e0;
        elementName = TooltipHelper.getTrimmedString(elementName, width - 10);
        Font font = Minecraft.getInstance().font;
        String lowerCase = elementName.toLowerCase(Locale.ROOT);
        int textX = x + 4;
        int textY = y + 3;
        String search = getSearchString();
        if (!search.isEmpty() && lowerCase.contains(search)) {
            int highlightStart = lowerCase.indexOf(search);
            int highlightEnd = highlightStart + search.length();
            int highlightColor = 0xFFD642;
            drawPartiallyHighlightedString(graphics, font, elementName, textX, textY, highlightStart, highlightEnd, textColor, highlightColor);
        } else {
            graphics.drawString(font, elementName, textX, textY, textColor);
        }
    }

    private void drawPartiallyHighlightedString(@NotNull GuiGraphics graphics, Font font, String string, int x, int y, int highlightStart, int highlightEnd, int textColor, int highlightColor) {
        String split1 = string.substring(0, highlightStart);
        graphics.drawString(font, split1, x, y, textColor);
        x += font.width(split1);
        String split2 = string.substring(highlightStart, highlightEnd);
        graphics.drawString(font, split2, x, y, highlightColor);
        x += font.width(split2);
        String split3 = string.substring(highlightEnd);
        graphics.drawString(font, split3, x, y, textColor);
    }
}
