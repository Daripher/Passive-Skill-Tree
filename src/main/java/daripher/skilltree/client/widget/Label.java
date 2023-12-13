package daripher.skilltree.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class Label extends AbstractWidget {
  public static final ResourceLocation WIDGETS_TEXTURE =
      new ResourceLocation("skilltree:textures/screen/widgets.png");
  private boolean hasBackground;

  public Label(int x, int y, Component message) {
    super(x, y, 0, 14, message);
  }

  public Label(int x, int y, int width, int height, Component message) {
    super(x, y, width, height, message);
    setHasBackground(true);
  }

  @Override
  public void renderWidget(@NotNull GuiGraphics graphics, int m, int pMouseY, float partialTick) {
    Minecraft minecraft = Minecraft.getInstance();
    Font font = minecraft.font;
    if (hasBackground) {
      graphics.blit(WIDGETS_TEXTURE, getX(), getY(), 0, 14, width / 2, height);
      graphics.blit(
          WIDGETS_TEXTURE, getX() + width / 2, getY(), 256 - width / 2, 14, width / 2, height);
      int textColor = getFGColor() | Mth.ceil(alpha * 255F) << 24;
      graphics.drawCenteredString(
          font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, textColor);
    } else {
      graphics.drawString(font, getMessage(), getX(), getY() + 3, getFGColor());
    }
  }

  @Override
  protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {}

  public void setHasBackground(boolean hasBackground) {
    this.hasBackground = hasBackground;
  }
}
