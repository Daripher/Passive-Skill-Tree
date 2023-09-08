package daripher.skilltree.client.widget;

import java.util.Objects;
import java.util.function.Predicate;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class PSTEditBox extends EditBox {
	private Predicate<String> filter = Objects::nonNull;

	public PSTEditBox(Font font, int x, int y, int width, int height, String defaultText) {
		super(font, x, y, width, height, Component.empty());
		setMaxLength(80);
		setValue(defaultText);
	}

	@Override
	public void setFilter(Predicate<String> filter) {
		this.filter = filter;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		if (!isValueValid()) setTextColor(0xD80000);
		else setTextColor(DEFAULT_TEXT_COLOR);
		super.render(graphics, mouseX, mouseY, partialTick);
	}

	public boolean isValueValid() {
		return filter.test(getValue());
	}
}
