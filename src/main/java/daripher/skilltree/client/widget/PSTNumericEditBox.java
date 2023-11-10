package daripher.skilltree.client.widget;

import java.util.Locale;
import java.util.function.Predicate;
import net.minecraft.client.gui.Font;

public class PSTNumericEditBox extends PSTEditBox {
  private static final Predicate<String> DEFAULT_FILTER =
      s -> {
        try {
          Double.parseDouble(s);
          return true;
        } catch (Exception e) {
          return false;
        }
      };
  private double defaultValue;

  public PSTNumericEditBox(Font font, int x, int y, int width, int height, double defaultValue) {
    super(font, x, y, width, height, formatDefaultValue(defaultValue));
    setDefaultValue(defaultValue);
    setSoftFilter(DEFAULT_FILTER);
  }

  private static String formatDefaultValue(double defaultValue) {
    String formatted = String.format(Locale.ENGLISH, "%.2f", defaultValue);
    while (formatted.endsWith("0")) {
      formatted = formatted.substring(0, formatted.length() - 1);
    }
    if (formatted.endsWith(".")) {
      formatted = formatted.substring(0, formatted.length() - 1);
    }
    return formatted;
  }

  public void setNumericFilter(Predicate<Double> filter) {
    setSoftFilter(DEFAULT_FILTER.and(createNumericFilter(filter)));
  }

  public void setDefaultValue(double defaultValue) {
    this.defaultValue = defaultValue;
  }

  public double getNumericValue() {
    try {
      return Double.parseDouble(getValue());
    } catch (NumberFormatException exception) {
      return defaultValue;
    }
  }

  private Predicate<String> createNumericFilter(Predicate<Double> filter) {
    return s -> filter.test(Double.parseDouble(s));
  }
}
