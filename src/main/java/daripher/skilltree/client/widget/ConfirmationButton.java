package daripher.skilltree.client.widget;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ConfirmationButton extends Button {
  protected boolean confirming;
  private Component confirmationMessage;

  public ConfirmationButton(int x, int y, int width, int height, Component message) {
    super(x, y, width, height, message);
  }

  @Override
  public @NotNull Component getMessage() {
    if (confirming && confirmationMessage != null) {
      return confirmationMessage;
    }
    return super.getMessage();
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

  public void setConfirmationMessage(Component message) {
    this.confirmationMessage = message;
  }
}
