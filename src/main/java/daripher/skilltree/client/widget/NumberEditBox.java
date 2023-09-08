package daripher.skilltree.client.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class NumberEditBox extends EditBox {
	public NumberEditBox(Font font, int x, int y, int width, int height, Component title) {
		super(font, x, y, width, height, title);
		setFilter(s -> {
			try {
				Double.parseDouble(s);
				return true;
			} catch (Exception e) {
				return false;
			}
		});
	}

	public double getNumericValue() {
		return Double.parseDouble(getValue());
	}
}
