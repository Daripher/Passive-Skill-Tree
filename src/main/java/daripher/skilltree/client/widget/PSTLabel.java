package daripher.skilltree.client.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class PSTLabel extends AbstractWidget {
  public PSTLabel(int x, int y, Component message) {
    super(x, y, 0, 14, message);
  }

  @Override
  public void renderButton(PoseStack poseStack, int m, int pMouseY, float partialTick) {
    Minecraft minecraft = Minecraft.getInstance();
    Font font = minecraft.font;
    drawString(poseStack, font, getMessage(), x, y + 3, getFGColor());
  }

  @Override
  public void updateNarration(NarrationElementOutput output) {
    output.add(NarratedElementType.TITLE, getMessage());
  }
}
