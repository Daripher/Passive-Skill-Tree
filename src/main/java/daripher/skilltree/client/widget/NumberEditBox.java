package daripher.skilltree.client.widget;

import java.util.function.Predicate;

import net.minecraft.client.gui.Font;

public class NumberEditBox extends PSTEditBox {
	private static final Predicate<String> DEFAULT_FILTER = s -> {
		try {
			Double.parseDouble(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	};

	public NumberEditBox(Font font, int x, int y, int width, int height, double defaultValue) {
		super(font, x, y, width, height, formatDefaultValue(defaultValue));
		setFilter(DEFAULT_FILTER);
	}

	public void setNumericFilter(Predicate<Double> filter) {
		setFilter(DEFAULT_FILTER.and(createNumericFilter(filter)));
	}

	public double getNumericValue() {
		return Double.parseDouble(getValue());
	}

	private Predicate<String> createNumericFilter(Predicate<Double> filter) {
		return s -> filter.test(Double.parseDouble(s));
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
}
