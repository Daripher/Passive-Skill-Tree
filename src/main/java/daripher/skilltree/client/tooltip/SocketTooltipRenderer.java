package daripher.skilltree.client.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.ItemHelper;

import java.util.List;

import daripher.skilltree.item.gem.GemItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

// Slightly modified code from https://github.com/Shadows-of-Fire/Apotheosis
public class SocketTooltipRenderer implements ClientTooltipComponent {
  public static final ResourceLocation SOCKET =
      new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/socket.png");
  private final SocketComponent component;
  private final int spacing = Minecraft.getInstance().font.lineHeight + 2;

  public SocketTooltipRenderer(SocketComponent comp) {
    this.component = comp;
  }

  public static Component getSocketDesc(ItemStack stack, int socket) {
    if (!GemItem.hasGem(stack, socket)) {
      return Component.translatable("gem.socket");
    }
    return GemItem.getGemBonuses(stack).get(socket).getTooltip();
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
  public void renderImage(
      @NotNull Font pFont,
      int x,
      int y,
      @NotNull PoseStack stack,
      @NotNull ItemRenderer itemRenderer,
      int pBlitOffset) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, SOCKET);
    for (int i = 0; i < component.sockets; i++) {
      GuiComponent.blit(stack, x, y + spacing * i, pBlitOffset, 0, 0, 9, 9, 9, 9);
    }
    for (ItemStack gem : component.gems) {
      if (!gem.isEmpty()) {
        PoseStack mvStack = RenderSystem.getModelViewStack();
        mvStack.pushPose();
        mvStack.scale(0.5F, 0.5F, 1);
        itemRenderer.renderAndDecorateFakeItem(gem, 2 * x + 1, 2 * y + 1);
        mvStack.popPose();
        RenderSystem.applyModelViewMatrix();
      }
      y += spacing;
    }
  }

  @Override
  public void renderText(
      @NotNull Font font, int x, int y, @NotNull Matrix4f matrix, @NotNull BufferSource buffer) {
    for (int i = 0; i < component.sockets; i++) {
      font.drawInBatch(
          getSocketDesc(component.stack, i),
          x + 12,
          y + 1 + spacing * i,
          0xAABBCC,
          true,
          matrix,
          buffer,
          false,
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
      this.gems = GemItem.getGems(stack);
      this.sockets = ItemHelper.getMaximumSockets(stack, Minecraft.getInstance().player);
      if (sockets < gems.size()) sockets = gems.size();
    }
  }
}
