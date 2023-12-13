package daripher.skilltree.client.screen;

import daripher.skilltree.client.widget.SkillButton;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScreenHelper {
  public static void drawCenteredOutlinedText(
      GuiGraphics graphics, String text, int x, int y, int color) {
    Font font = Minecraft.getInstance().font;
    x -= font.width(text) / 2;
    graphics.drawString(font, text, x + 1, y, 0);
    graphics.drawString(font, text, x - 1, y, 0);
    graphics.drawString(font, text, x, y + 1, 0);
    graphics.drawString(font, text, x, y - 1, 0);
    graphics.drawString(font, text, x, y, color);
  }

  public static void drawRectangle(
      GuiGraphics graphics, int x, int y, int width, int height, int color) {
    graphics.fill(x, y, x + width, y + 1, color);
    graphics.fill(x, y + height - 1, x + width, y + height, color);
    graphics.fill(x, y + 1, x + 1, y + height - 1, color);
    graphics.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
  }

  public static float getAngleBetweenButtons(Button button1, Button button2) {
    float x1 = button1.getX() + button1.getWidth() / 2F;
    float y1 = button1.getY() + button1.getHeight() / 2F;
    float x2 = button2.getX() + button2.getWidth() / 2F;
    float y2 = button2.getY() + button2.getHeight() / 2F;
    return getAngleBetweenPoints(x1, y1, x2, y2);
  }

  public static float getDistanceBetweenButtons(Button button1, Button button2) {
    float x1 = button1.getX() + button1.getWidth() / 2F;
    float y1 = button1.getY() + button1.getHeight() / 2F;
    float x2 = button2.getX() + button2.getWidth() / 2F;
    float y2 = button2.getY() + button2.getHeight() / 2F;
    return getDistanceBetweenPoints(x1, y1, x2, y2);
  }

  public static float getDistanceBetweenPoints(float x1, float y1, float x2, float y2) {
    return Mth.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
  }

  public static float getAngleBetweenPoints(float x1, float y1, float x2, float y2) {
    return (float) Mth.atan2(y2 - y1, x2 - x1);
  }

  public static void renderSkillTooltip(
      SkillButton button, GuiGraphics graphics, float x, float y, int width, int height) {
    List<MutableComponent> tooltip = button.getSkillTooltip();
    if (tooltip.isEmpty()) return;
    Font font = Minecraft.getInstance().font;
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
    graphics.pose().pushPose();
    graphics.pose().translate(tooltipX, tooltipY, 10);
    graphics.fill(1, 4, tooltipWidth - 1, tooltipHeight + 4, 0xDD000000);
    int textX = 5;
    int textY = 2;
    ResourceLocation texture = button.skill.getBorderTexture();
    graphics.blit(texture, -4, -4, 0, 0, 21, 20, 110, 20);
    graphics.blit(texture, tooltipWidth + 4 - 21, -4, -21, 0, 21, 20, 110, 20);
    int centerWidth = tooltipWidth + 8 - 42;
    int centerX = -4 + 21;
    while (centerWidth > 0) {
      int partWidth = Math.min(centerWidth, 68);
      graphics.blit(texture, centerX, -4, 21, 0, partWidth, 20, 110, 20);
      centerX += partWidth;
      centerWidth -= partWidth;
    }
    MutableComponent title = tooltip.remove(0);
    graphics.drawCenteredString(font, title, tooltipWidth / 2, textY, 0xFFFFFF);
    textY += 17;
    for (MutableComponent component : tooltip) {
      graphics.drawString(font, component, textX, textY, 0xFFFFFF);
      textY += font.lineHeight + 1;
    }
    graphics.pose().popPose();
  }
}
