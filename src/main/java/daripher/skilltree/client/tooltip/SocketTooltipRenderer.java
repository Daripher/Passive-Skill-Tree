package daripher.skilltree.client.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.GemBonusHandler;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

// Slightly modified code from https://github.com/Shadows-of-Fire/Apotheosis
public class SocketTooltipRenderer implements ClientTooltipComponent {
  private final SocketComponent component;
  private final int spacing = Minecraft.getInstance().font.lineHeight + 2;

  public SocketTooltipRenderer(SocketComponent comp) {
    this.component = comp;
  }

  public static Component getSocketDesc(ItemStack stack, int socket) {
    if (!GemBonusHandler.hasGem(stack, socket)) {
      return Component.translatable("gem.socket");
    }
    return GemBonusHandler.getBonuses(stack).get(socket).getTooltip();
  }

  @Override
  public int getHeight() {
    return spacing * component.sockets;
  }

  @Override
  public int getWidth(@NotNull Font font) {
    int width = 0;
    for (int i = 0; i < component.sockets; i++) {
      width = Math.max(width, font.width(getSocketDesc(component.stack, i)) + 12);
    }
    return width;
  }

  @Override
  public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics graphics) {
    ResourceLocation texture =
        new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/socket.png");
    for (int i = 0; i < component.sockets; i++) {
      graphics.blit(texture, x, y + spacing * i, 0, 0, 0, 9, 9, 9, 9);
    }
    for (ItemStack gem : component.gems) {
      if (!gem.isEmpty()) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.scale(0.5f, 0.5f, 1f);
        graphics.renderFakeItem(gem, 2 * x + 1, 2 * y + 1);
        pose.popPose();
      }
      y += spacing;
    }
  }

  @Override
  public void renderText(
      @NotNull Font font,
      int x,
      int y,
      @NotNull Matrix4f matrix,
      @NotNull MultiBufferSource.BufferSource bufferSource) {
    for (int i = 0; i < component.sockets; i++) {
      font.drawInBatch(
          getSocketDesc(component.stack, i),
          x + 12,
          y + 1 + this.spacing * i,
          0xAABBCC,
          true,
          matrix,
          bufferSource,
          Font.DisplayMode.NORMAL,
          0,
          15728880);
    }
  }

  public static class SocketComponent implements TooltipComponent {
    private final ItemStack stack;
    private final List<ItemStack> gems;
    private int sockets;

    public SocketComponent(ItemStack stack) {
      this.stack = stack;
      this.gems = GemBonusHandler.getGems(stack);
      this.sockets = ItemHelper.getMaximumSockets(stack, Minecraft.getInstance().player);
      if (sockets < gems.size()) sockets = gems.size();
    }
  }
}
