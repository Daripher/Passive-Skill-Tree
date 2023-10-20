package daripher.skilltree.client.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.client.screen.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class InfoPanel extends AbstractWidget {
  public InfoPanel(int x, int y, int width, int height, Component message) {
    super(x, y, width, height, message);
  }

  @Override
  public void renderButton(
      @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    ScreenHelper.prepareTextureRendering(
        new ResourceLocation("skilltree:textures/screen/widgets/buttons.png"));
    blit(poseStack, x, y, 0, 14, width / 2, height);
    blit(poseStack, x + width / 2, y, 140 - width / 2, 14, width / 2, height);
    Minecraft minecraft = Minecraft.getInstance();
    Font font = minecraft.font;
    int textColor = getFGColor();
    drawCenteredString(
        poseStack,
        font,
        getMessage(),
        x + width / 2,
        y + (height - 8) / 2,
        textColor | Mth.ceil(alpha * 255F) << 24);
  }

  @Override
  public void updateNarration(@NotNull NarrationElementOutput output) {}
}
