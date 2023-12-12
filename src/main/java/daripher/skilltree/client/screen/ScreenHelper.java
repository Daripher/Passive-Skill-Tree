package daripher.skilltree.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import daripher.skilltree.client.widget.SkillButton;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScreenHelper {
  public static void drawCenteredOutlinedText(
      PoseStack poseStack, String text, int x, int y, int color) {
    Font font = Minecraft.getInstance().font;
    x -= font.width(text) / 2;
    font.draw(poseStack, text, x + 1, y, 0);
    font.draw(poseStack, text, x - 1, y, 0);
    font.draw(poseStack, text, x, y + 1, 0);
    font.draw(poseStack, text, x, y - 1, 0);
    font.draw(poseStack, text, x, y, color);
  }

  public static void drawRectangle(
      PoseStack poseStack, int x, int y, int width, int height, int color) {
    GuiComponent.fill(poseStack, x, y, x + width, y + 1, color);
    GuiComponent.fill(poseStack, x, y + height - 1, x + width, y + height, color);
    GuiComponent.fill(poseStack, x, y + 1, x + 1, y + height - 1, color);
    GuiComponent.fill(poseStack, x + width - 1, y + 1, x + width, y + height - 1, color);
  }

  public static void prepareTextureRendering(ResourceLocation textureLocation) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, textureLocation);
    RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.enableDepthTest();
  }

  public static float getAngleBetweenButtons(Button button1, Button button2) {
    float x1 = button1.x + button1.getWidth() / 2F;
    float y1 = button1.y + button1.getHeight() / 2F;
    float x2 = button2.x + button2.getWidth() / 2F;
    float y2 = button2.y + button2.getHeight() / 2F;
    return getAngleBetweenPoints(x1, y1, x2, y2);
  }

  public static float getDistanceBetweenButtons(Button button1, Button button2) {
    float x1 = button1.x + button1.getWidth() / 2F;
    float y1 = button1.y + button1.getHeight() / 2F;
    float x2 = button2.x + button2.getWidth() / 2F;
    float y2 = button2.y + button2.getHeight() / 2F;
    return getDistanceBetweenPoints(x1, y1, x2, y2);
  }

  public static float getDistanceBetweenPoints(float x1, float y1, float x2, float y2) {
    return Mth.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
  }

  public static float getAngleBetweenPoints(float x1, float y1, float x2, float y2) {
    return (float) Mth.atan2(y2 - y1, x2 - x1);
  }

  public static void renderSkillTooltip(
      SkillButton button,
      PoseStack poseStack,
      float x,
      float y,
      int width,
      int height,
      ItemRenderer itemRenderer) {
    Font font = Minecraft.getInstance().font;
    List<MutableComponent> tooltip = button.getTooltip();
    if (tooltip.isEmpty()) return;
    int tooltipWidth = 0;
    int tooltipHeight = tooltip.size() == 1 ? 8 : 10;
    for (MutableComponent component : tooltip) {
      int k = font.width(component);
      if (k > tooltipWidth) tooltipWidth = k;
      tooltipHeight += font.lineHeight;
    }
    tooltipWidth += 42;
    float tooltipX = x + 12;
    float tooltipY = y - 12;
    if (tooltipX + tooltipWidth > width) {
      tooltipX -= 28 + tooltipWidth;
    }
    if (tooltipY + tooltipHeight + 6 > height) {
      tooltipY = height - tooltipHeight - 6;
    }
    poseStack.pushPose();
    poseStack.translate(tooltipX, tooltipY, 0);
    float zOffset = itemRenderer.blitOffset;
    itemRenderer.blitOffset = 400.0F;
    GuiComponent.fill(poseStack, 1, 4, tooltipWidth - 1, tooltipHeight + 4, 0xDD000000);
    RenderSystem.enableTexture();
    MultiBufferSource.BufferSource buffer =
        MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
    poseStack.translate(0.0D, 0.0D, 400.0D);
    int textX = 5;
    int textY = 2;
    ScreenHelper.prepareTextureRendering(button.skill.getBorderTexture());
    GuiComponent.blit(poseStack, -4, -4, 0, 0, 21, 20, 110, 20);
    GuiComponent.blit(poseStack, tooltipWidth + 4 - 21, -4, -21, 0, 21, 20, 110, 20);
    int centerWidth = tooltipWidth + 8 - 42;
    int centerX = -4 + 21;
    while (centerWidth > 0) {
      int partWidth = Math.min(centerWidth, 68);
      GuiComponent.blit(poseStack, centerX, -4, 21, 0, partWidth, 20, 110, 20);
      centerX += partWidth;
      centerWidth -= partWidth;
    }
    MutableComponent title = tooltip.remove(0);
    GuiComponent.drawCenteredString(poseStack, font, title, tooltipWidth / 2, textY, 0xFFFFFF);
    textY += 17;
    for (MutableComponent component : tooltip) {
      font.draw(poseStack, component, textX, textY, 0xFFFFFF);
      textY += font.lineHeight + 1;
    }
    buffer.endBatch();
    poseStack.popPose();
    itemRenderer.blitOffset = zOffset;
  }
}
