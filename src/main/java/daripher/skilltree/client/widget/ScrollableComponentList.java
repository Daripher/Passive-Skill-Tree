package daripher.skilltree.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScrollableComponentList extends AbstractWidget {
    private final int maxHeight;
    private List<Component> components = new ArrayList<>();
    private int maxLines;
    private int scroll;

    public ScrollableComponentList(int y, int maxHeight) {
        super(0, y, 0, 0, Component.empty());
        this.maxHeight = maxHeight;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (components.isEmpty()) {
            return;
        }
        renderBackground(graphics);
        renderText(graphics);
        renderScrollBar(graphics);
    }

    private void renderBackground(@NotNull GuiGraphics graphics) {
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xDD000000);
    }

    private void renderText(@NotNull GuiGraphics graphics) {
        Font font = Minecraft.getInstance().font;
        for (int i = scroll; i < maxLines + scroll; i++) {
            Component component = components.get(i);
            int x = getX() + 5;
            int y = getY() + 5 + (i - scroll) * (font.lineHeight + 3);
            graphics.drawString(font, component, x, y, 0x7B7BE5);
        }
    }

    private void renderScrollBar(@NotNull GuiGraphics graphics) {
        if (components.size() > maxLines) {
            int scrollSize = height * maxLines / components.size();
            int maxScroll = components.size() - maxLines;
            int scrollShift = (int) ((height - scrollSize) / (float) maxScroll * scroll);
            int x = getX() + width - 3;
            int y = getY() + scrollShift;
            graphics.fill(x, getY(), getX() + width, getY() + height, 0xDD222222);
            graphics.fill(x, y, getX() + width, getY() + scrollShift + scrollSize, 0xDD888888);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int maxScroll = components.size() - maxLines;
        if (amount < 0 && scroll < maxScroll) {
            scroll++;
        }
        if (amount > 0 && scroll > 0) {
            scroll--;
        }
        return true;
    }

    public void setComponents(List<Component> components) {
        maxLines = components.size();
        this.components = components;
        width = 0;
        Font font = Minecraft.getInstance().font;
        for (Component stat : components) {
            int statWidth = font.width(stat);
            if (statWidth > width) {
                width = statWidth;
            }
        }
        width += 14;
        height = components.size() * (font.lineHeight + 3) + 10;
        while (height > maxHeight) {
            height -= font.lineHeight + 3;
            maxLines--;
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }
}
