package daripher.skilltree.client.widget;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TextArea extends MultiLineEditBox implements TickingWidget {
  public TextArea(int x, int y, int width, int height, String defaultValue) {
    super(Minecraft.getInstance().font, x, y, width, height, Component.empty(), Component.empty());
    setValue(defaultValue);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return isFocused() && super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    return isFocused() && super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    setFocused(clicked(mouseX, mouseY));
    return super.mouseClicked(mouseX, mouseY, button);
  }

  public TextArea setResponder(@NotNull Consumer<String> responder) {
    super.setValueListener(responder);
    return this;
  }

  @Override
  public void onWidgetTick() {}
}
