package daripher.skilltree.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.quiver.QuiverItem;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
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
      ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
    LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
    Optional<SlotResult> quiverCurio =
        CuriosApi.getCuriosHelper().findFirstCurio(player, ItemHelper::isQuiver);
    quiverCurio
        .map(SlotResult::stack)
        .ifPresent(
            quiver ->
                renderArrows(
                    gui, poseStack, partialTick, screenWidth, screenHeight, quiver, player));
  }

  private void renderArrows(
      ForgeGui gui,
      PoseStack poseStack,
      float partialTick,
      int screenWidth,
      int screenHeight,
      ItemStack quiver,
      LocalPlayer player) {
    if (!QuiverItem.containsArrows(quiver)) return;
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
    HumanoidArm offhand = player.getMainArm().getOpposite();
    int center = screenWidth / 2;
    ItemStack arrows = QuiverItem.getArrows(quiver);
    int slotY = screenHeight - 16 - 3;
    boolean hasOffhandItem = !player.getOffhandItem().isEmpty();
    int arrowsCount = QuiverItem.getArrowsCount(quiver);
    if (offhand == HumanoidArm.LEFT) {
      int slotX = center - 91 - 29 - (hasOffhandItem ? 29 : 0);
      gui.blit(poseStack, slotX, screenHeight - 23, 24, 22, 29, 24);
    } else {
      int slotX = center + 91 + (hasOffhandItem ? 29 : 0);
      gui.blit(poseStack, slotX, screenHeight - 23, 53, 22, 29, 24);
    }
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    if (offhand == HumanoidArm.LEFT) {
      int slotX = center - 91 - 29 - (hasOffhandItem ? 29 : 0);
      renderSlot(slotX + 3, slotY, partialTick, player, arrows, 1, arrowsCount);
    } else {
      int slotX = center + 91 + (hasOffhandItem ? 29 : 0);
      renderSlot(slotX + 10, slotY, partialTick, player, arrows, 1, arrowsCount);
    }
  }

  private void renderSlot(
      int x, int y, float partialTick, Player player, ItemStack stack, int slot, int count) {
    if (stack.isEmpty()) return;
    PoseStack posestack = RenderSystem.getModelViewStack();
    float f = (float) stack.getPopTime() - partialTick;
    if (f > 0) {
      float f1 = 1f + f / 5f;
      posestack.pushPose();
      posestack.translate(x + 8, y + 12, 0.0D);
      posestack.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
      posestack.translate(-(x + 8), -(y + 12), 0.0D);
      RenderSystem.applyModelViewMatrix();
    }
    Minecraft minecraft = Minecraft.getInstance();
    ItemRenderer itemRenderer = minecraft.getItemRenderer();
    itemRenderer.renderAndDecorateItem(player, stack, x, y, slot);
    RenderSystem.setShader(GameRenderer::getPositionColorShader);
    if (f > 0f) {
      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
    }
    itemRenderer.renderGuiItemDecorations(minecraft.font, stack, x, y, "" + count);
  }
}
