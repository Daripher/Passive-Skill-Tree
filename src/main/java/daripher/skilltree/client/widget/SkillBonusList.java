package daripher.skilltree.client.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class SkillBonusList extends AbstractWidget {
  private final int maxHeight;
  private List<MutableComponent> stats = new ArrayList<>();
  private int maxLines;
  private int scroll;

  public SkillBonusList(int y, int maxHeight) {
    super(0, y, 0, 0, Component.empty());
    this.maxHeight = maxHeight;
  }

  @Override
  public void renderButton(
      @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    if (stats.isEmpty()) return;
    fill(poseStack, x, y, x + width, y + height, 0xDD000000);
    Font font = Minecraft.getInstance().font;
    for (int i = scroll; i < maxLines + scroll; i++) {
      drawString(
          poseStack,
          font,
          stats.get(i),
          x + 5,
          y + 5 + (i - scroll) * (font.lineHeight + 3),
          0x7B7BE5);
    }
    if (stats.size() > maxLines) {
      fill(poseStack, x + width - 3, y, x + width, y + height, 0xDD222222);
      int scrollSize = height * maxLines / stats.size();
      int maxScroll = stats.size() - maxLines;
      int scrollShift = (int) ((height - scrollSize) / (float) maxScroll * scroll);
      fill(
          poseStack,
          x + width - 3,
          y + scrollShift,
          x + width,
          y + scrollShift + scrollSize,
          0xDD888888);
    }
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    int maxScroll = stats.size() - maxLines;
    if (amount < 0 && scroll < maxScroll) scroll++;
    if (amount > 0 && scroll > 0) scroll--;
    return true;
  }

  public void setStats(List<MutableComponent> stats) {
    maxLines = stats.size();
    this.stats = stats;
    width = 0;
    Font font = Minecraft.getInstance().font;
    for (Component stat : stats) {
      int statWidth = font.width(stat);
      if (statWidth > width) width = statWidth;
    }
    width += 14;
    height = stats.size() * (font.lineHeight + 3) + 10;
    while (height > maxHeight) {
      height -= font.lineHeight + 3;
      maxLines--;
    }
  }

  @Override
  public void updateNarration(@NotNull NarrationElementOutput output) {}
}
