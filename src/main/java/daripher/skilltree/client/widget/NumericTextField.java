package daripher.skilltree.client.widget;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import org.jetbrains.annotations.NotNull;

public class NumericTextField extends TextField {
  private static final Predicate<String> DEFAULT_FILTER = NumericTextField::isNumericString;
  private @Nullable Consumer<Double> numericResponder;
  private double defaultValue;

  public NumericTextField(Font font, int x, int y, int width, int height, double defaultValue) {
    super(font, x, y, width, height, formatDefaultValue(defaultValue));
    setDefaultValue(defaultValue);
    setSoftFilter(DEFAULT_FILTER);
  }

  @Override
  public void insertText(@NotNull String text) {
    super.insertText(text);
    onNumericValueChange();
  }

  @Override
  public void setMaxLength(int length) {
    super.setMaxLength(length);
    onNumericValueChange();
  }

  @Override
  public void setValue(@NotNull String text) {
    super.setValue(text);
    onNumericValueChange();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    boolean pressed = super.keyPressed(keyCode, scanCode, modifiers);
    onNumericValueChange();
    return pressed;
  }

  public NumericTextField setNumericFilter(Predicate<Double> filter) {
    setSoftFilter(DEFAULT_FILTER.and(createNumericFilter(filter)));
    return this;
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

  public void setNumericResponder(@Nullable Consumer<Double> numericResponder) {
    this.numericResponder = numericResponder;
  }

  private void onNumericValueChange() {
    if (numericResponder != null) {
      numericResponder.accept(getNumericValue());
    }
  }

  private Predicate<String> createNumericFilter(Predicate<Double> filter) {
    return s -> filter.test(Double.parseDouble(s));
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

  private static boolean isNumericString(String s) {
    try {
      Double.parseDouble(s);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
