package daripher.skilltree.client.widget;

import net.minecraft.network.chat.Component;

public class ConfirmationButton extends PSTButton {
  protected boolean confirming;

  public ConfirmationButton(
      int x, int y, int width, int height, Component message, OnPress pressFunc) {
    super(x, y, width, height, message, pressFunc);
  }

  @Override
  public void onPress() {
    if (!confirming) {
      confirming = true;
      return;
    }
    pressFunc.onPress(this);
  }

  @Override
  public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
    boolean clicked = super.mouseClicked(pMouseX, pMouseY, pButton);
    if (!clicked) confirming = false;
    return clicked;
  }
}
