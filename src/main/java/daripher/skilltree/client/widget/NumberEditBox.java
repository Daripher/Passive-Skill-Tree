package daripher.skilltree.client.widget;

import net.minecraft.client.gui.Font;

public class NumberEditBox extends PSTEditBox {
	public NumberEditBox(Font font, int x, int y, int width, int height, double defaultValue) {
		super(font, x, y, width, height, "" + defaultValue);
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
