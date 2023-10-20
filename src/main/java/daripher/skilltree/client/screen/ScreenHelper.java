package daripher.skilltree.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScreenHelper {
  public static void drawCenteredOutlinedText(
      PoseStack poseStack, String text, Font font, int x, int y, int color) {
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
}
