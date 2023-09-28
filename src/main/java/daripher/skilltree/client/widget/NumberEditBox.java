package daripher.skilltree.client.widget;

import net.minecraft.client.gui.Font;

public class NumberEditBox extends PSTEditBox {
	public NumberEditBox(Font font, int x, int y, int width, int height, double defaultValue) {
		super(font, x, y, width, height, formatDefaultValue(defaultValue));
		setFilter(s -> {
			try {
				Double.parseDouble(s);
				return true;
			} catch (Exception e) {
				return false;
			}
		});
	}

	private static String formatDefaultValue(double defaultValue) {
		String formatted = String.format("%.2f", defaultValue);
		while (formatted.endsWith("0")) {
			formatted = formatted.substring(0, formatted.length() - 1);
		}
		if (formatted.endsWith(".")) {
			formatted = formatted.substring(0, formatted.length() - 1);
		}
		return formatted;
	}

	public double getNumericValue() {
		return Double.parseDouble(getValue());
	}
}
