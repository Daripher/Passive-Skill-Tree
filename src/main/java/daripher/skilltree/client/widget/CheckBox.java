package daripher.skilltree.client.widget;

import net.minecraft.network.chat.Component;

public class CheckBox extends Button {
  private boolean value;

  public CheckBox(int pX, int pY, int pWidth, int pHeight, boolean defaultValue) {
    super(pX, pY, pWidth, pHeight, Component.literal(""));
    this.value = defaultValue;
  }

  @Override
  public void onPress() {
    value ^= true;
  }

  public boolean getValue() {
    return value;
  }
}
