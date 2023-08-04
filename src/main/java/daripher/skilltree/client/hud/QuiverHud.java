package daripher.skilltree.client.hud;

import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.quiver.QuiverItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
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

	private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");

	@Override
	public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
		LocalPlayer player = Minecraft.getInstance().player;
		Optional<SlotResult> quiverCurio = CuriosApi.getCuriosHelper().findFirstCurio(player, QuiverItem::isQuiver);
		quiverCurio.map(SlotResult::stack).ifPresent(quiver -> {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			HumanoidArm offhand = player.getMainArm().getOpposite();
			int center = screenWidth / 2;
			if (QuiverItem.containsArrows(quiver)) {
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
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				if (offhand == HumanoidArm.LEFT) {
					int slotX = center - 91 - 29 - (hasOffhandItem ? 29 : 0);
					renderSlot(graphics, slotX + 3, slotY, partialTick, player, arrows, 1, arrowsCount);
				} else {
					int slotX = center + 91 + (hasOffhandItem ? 29 : 0);
					renderSlot(graphics, slotX + 10, slotY, partialTick, player, arrows, 1, arrowsCount);
				}
			}
		});
	}

	private void renderSlot(GuiGraphics graphics, int x, int y, float partialTick, Player player, ItemStack stack, int i, int count) {
		if (stack.isEmpty()) return;
		PoseStack posestack = RenderSystem.getModelViewStack();
		float f = (float) stack.getPopTime() - partialTick;
		if (f > 0.0F) {
			float f1 = 1.0F + f / 5.0F;
			posestack.pushPose();
			posestack.translate((double) (x + 8), (double) (y + 12), 0.0D);
			posestack.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
			posestack.translate((double) (-(x + 8)), (double) (-(y + 12)), 0.0D);
			RenderSystem.applyModelViewMatrix();
		}
		Minecraft minecraft = Minecraft.getInstance();
		graphics.renderItem(player, stack, x, y, i);
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		if (f > 0.0F) {
			posestack.popPose();
			RenderSystem.applyModelViewMatrix();
		}
		graphics.renderItemDecorations(minecraft.font, stack, x, y, "" + count);
	}

	@SubscribeEvent
	public static void register(RegisterGuiOverlaysEvent event) {
		event.registerBelowAll("quiver_contents", INSTANCE);
	}
}
