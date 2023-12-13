package daripher.skilltree.client.widget;

import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CheckBox extends Button {
  private boolean value;
  private Consumer<Boolean> responder = b -> {};

  public CheckBox(int x, int y, boolean defaultValue) {
    super(x, y, 14, 14, Component.empty());
    this.value = defaultValue;
  }

  @Override
  public void onPress() {
    value ^= true;
    responder.accept(value);
  }

  @Override
  protected void renderBackground(@NotNull GuiGraphics graphics) {
    super.renderBackground(graphics);
    ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/widgets.png");
    if (value) {
      graphics.blit(texture, getX(), getY(), 0, 242, width, height);
    }
  }

  protected int getTextureVariant() {
    return isHoveredOrFocused() ? 3 : 4;
  }

  public void setResponder(Consumer<Boolean> responder) {
    this.responder = responder;
  }
}
