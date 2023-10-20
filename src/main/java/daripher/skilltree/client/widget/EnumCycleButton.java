package daripher.skilltree.client.widget;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.network.chat.Component;

public class EnumCycleButton<E extends Enum<E>> extends PSTButton {
  private final Class<E> type;
  private int index;
  private E value;

  @SuppressWarnings("unchecked")
  public EnumCycleButton(int x, int y, int width, int height, E value) {
    super(x, y, width, height, Component.literal(value.toString()));
    this.type = (Class<E>) value.getClass();
    this.value = value;
  }

  @Override
  public void onPress() {
    List<E> values = new ArrayList<>(EnumSet.allOf(type));
    if (index == values.size() - 1) index = 0;
    else index++;
    value = values.get(index);
    setMessage(Component.literal(value.toString()));
    super.onPress();
  }

  public E getValue() {
    return value;
  }
}
