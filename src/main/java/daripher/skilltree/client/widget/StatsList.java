package daripher.skilltree.client.widget;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class StatsList extends AbstractWidget {
	private List<Component> stats = new ArrayList<>();
	private final int maxHeight;
	private int maxLines;
	private int scroll;

	public StatsList(int y, int maxHeight) {
		super(0, y, 0, 0, Component.empty());
		this.maxHeight = maxHeight;
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		if (stats.isEmpty()) return;
		graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xDD000000);
		Font font = Minecraft.getInstance().font;
		for (int i = scroll; i < maxLines + scroll; i++) {
			graphics.drawString(font, stats.get(i), getX() + 5, getY() + 5 + (i - scroll) * (font.lineHeight + 3), 0x7B7BE5);
		}
		if (stats.size() > maxLines) {
			graphics.fill(getX() + width - 3, getY(), getX() + width, getY() + height, 0xDD222222);
			int scrollSize = height * maxLines / stats.size();
			int maxScroll = stats.size() - maxLines;
			int scrollShift = (int) ((height - scrollSize) / (float) maxScroll * scroll);
			graphics.fill(getX() + width - 3, getY() + scrollShift, getX() + width, getY() + scrollShift + scrollSize, 0xDD888888);
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		int maxScroll = stats.size() - maxLines;
		if (amount < 0 && scroll < maxScroll) scroll++;
		if (amount > 0 && scroll > 0) scroll--;
		return true;
	}

	public void setStats(List<Component> stats) {
		maxLines = stats.size();
		this.stats = stats;
		width = 0;
		Font font = Minecraft.getInstance().font;
		for (Component stat : stats) {
			int statWidth = font.width(stat);
			if (statWidth > width) width = statWidth;
		}
		width += 14;
		height = stats.size() * (font.lineHeight + 3) + 10;
		while (height > maxHeight) {
			height -= font.lineHeight + 3;
			maxLines--;
		}
	}

	@Override
	public void updateWidgetNarration(NarrationElementOutput output) {
	}
}
