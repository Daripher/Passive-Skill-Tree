package daripher.skilltree.client.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.client.screen.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
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
  public void renderButton(@NotNull PoseStack poseStack, int m, int pMouseY, float partialTick) {
    Minecraft minecraft = Minecraft.getInstance();
    Font font = minecraft.font;
    if (hasBackground) {
      ScreenHelper.prepareTextureRendering(WIDGETS_TEXTURE);
      blit(poseStack, x, y, 0, 14, width / 2, height);
      blit(poseStack, x + width / 2, y, 256 - width / 2, 14, width / 2, height);
      int textColor = getFGColor() | Mth.ceil(alpha * 255F) << 24;
      drawCenteredString(
          poseStack, font, getMessage(), x + width / 2, y + (height - 8) / 2, textColor);
    } else {
      drawString(poseStack, font, getMessage(), x, y + 3, getFGColor());
    }
  }

  @Override
  public void updateNarration(NarrationElementOutput output) {
    output.add(NarratedElementType.TITLE, getMessage());
  }

  public void setHasBackground(boolean hasBackground) {
    this.hasBackground = hasBackground;
  }
}
