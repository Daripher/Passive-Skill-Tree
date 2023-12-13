package daripher.skilltree.client.hud;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.quiver.QuiverItem;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public enum QuiverHud implements IGuiOverlay {
  INSTANCE;

  private static final ResourceLocation WIDGETS_LOCATION =
      new ResourceLocation("textures/gui/widgets.png");

  @SubscribeEvent
  public static void register(RegisterGuiOverlaysEvent event) {
    event.registerBelowAll("quiver_contents", INSTANCE);
  }

  @Override
  public void render(
      ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
    LocalPlayer player = Minecraft.getInstance().player;
    Objects.requireNonNull(player);
    CuriosApi.getCuriosInventory(player)
        .ifPresent(
            inv -> {
              Optional<SlotResult> curio = inv.findFirstCurio(ItemHelper::isQuiver);
              if (curio.isEmpty()) return;
              ItemStack quiver = curio.get().stack();
              if (quiver.isEmpty()) return;
              renderArrows(gui, graphics, partialTick, screenWidth, screenHeight, quiver, player);
            });
  }

  private void renderArrows(
      ForgeGui gui,
      GuiGraphics graphics,
      float partialTick,
      int screenWidth,
      int screenHeight,
      ItemStack quiver,
      LocalPlayer player) {
    if (!QuiverItem.containsArrows(quiver)) return;
    graphics.setColor(1f, 1f, 1f, 1f);
    HumanoidArm offhand = player.getMainArm().getOpposite();
    int center = screenWidth / 2;
    ItemStack arrows = QuiverItem.getArrows(quiver);
    int slotY = screenHeight - 16 - 3;
    boolean hasOffhandItem = !player.getOffhandItem().isEmpty();
    int arrowsCount = QuiverItem.getArrowsCount(quiver);
    if (offhand == HumanoidArm.LEFT) {
      int slotX = center - 91 - 29 - (hasOffhandItem ? 29 : 0);
      graphics.blit(WIDGETS_LOCATION, slotX, screenHeight - 23, 24, 22, 29, 24);
    } else {
      int slotX = center + 91 + (hasOffhandItem ? 29 : 0);
      graphics.blit(WIDGETS_LOCATION, slotX, screenHeight - 23, 53, 22, 29, 24);
    }
    gui.setupOverlayRenderState(true, false);
    if (offhand == HumanoidArm.LEFT) {
      int slotX = center - 91 - 29 - (hasOffhandItem ? 29 : 0);
      renderSlot(graphics, slotX + 3, slotY, partialTick, player, arrows, 1, arrowsCount);
    } else {
      int slotX = center + 91 + (hasOffhandItem ? 29 : 0);
      renderSlot(graphics, slotX + 10, slotY, partialTick, player, arrows, 1, arrowsCount);
    }
  }

  private void renderSlot(
      GuiGraphics graphics,
      int x,
      int y,
      float partialTick,
      Player player,
      ItemStack stack,
      int seed,
      int count) {
    if (stack.isEmpty()) return;
    float f = stack.getPopTime() - partialTick;
    if (f > 0) {
      float f1 = 1f + f / 5f;
      graphics.pose().pushPose();
      graphics.pose().translate(x + 8, y + 12, 0f);
      graphics.pose().scale(1f / f1, f1 / 2f + 0.5f, 1f);
      graphics.pose().translate(-x - 8, -y - 12, 0f);
    }
    graphics.renderItem(player, stack, x, y, seed);
    if (f > 0) {
      graphics.pose().popPose();
    }
    graphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y, "" + count);
  }
}
