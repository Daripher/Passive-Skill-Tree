package daripher.skilltree.client.tooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.gem.GemHelper;
import daripher.skilltree.util.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

// Slightly modified code from https://github.com/Shadows-of-Fire/Apotheosis
public class SocketTooltipRenderer implements ClientTooltipComponent {
	public static final ResourceLocation SOCKET = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/socket.png");
	private final SocketComponent component;
	private final int spacing = Minecraft.getInstance().font.lineHeight + 2;

	public SocketTooltipRenderer(SocketComponent comp) {
		this.component = comp;
	}

	@Override
	public int getHeight() {
		return spacing * component.sockets;
	}

	@Override
	public int getWidth(Font font) {
		int width = 0;
		for (int i = 0; i < component.sockets; i++) {
			width = Math.max(width, font.width(getSocketDesc(component.stack, i)) + 12);
		}
		return width;
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		for (int i = 0; i < component.sockets; i++) {
			graphics.blit(SOCKET, x, y + spacing * i, 0, 0, 9, 9, 9, 9);
		}
		for (ItemStack gem : component.gems) {
			if (!gem.isEmpty()) {
				graphics.pose().pushPose();
				graphics.pose().scale(0.5F, 0.5F, 1);
				graphics.renderItem(gem, 2 * x + 1, 2 * y + 1);
				graphics.pose().popPose();
			}
			y += spacing;
		}
	}

	@Override
	public void renderText(Font font, int x, int y, Matrix4f matrix, BufferSource buffer) {
		for (int i = 0; i < component.sockets; i++) {
			font.drawInBatch(getSocketDesc(component.stack, i), x + 12, y + 1 + spacing * i, 0xAABBCC, true, matrix, buffer, DisplayMode.NORMAL, 0,
					15728880);
		}
	}

	public static Component getSocketDesc(ItemStack stack, int socket) {
		if (!GemHelper.hasGem(stack, socket)) return Component.translatable("gem.socket");
		Optional<Pair<Attribute, AttributeModifier>> gemBonus = GemHelper.getAttributeBonus(stack, socket);
		return TooltipHelper.getAttributeBonusTooltip(gemBonus);
	}

	public static class SocketComponent implements TooltipComponent {
		private final ItemStack stack;
		private final List<ItemStack> gems;
		private int sockets;

		public SocketComponent(ItemStack stack) {
			this.stack = stack;
			this.gems = new ArrayList<ItemStack>();
			this.sockets = GemHelper.getMaximumSockets(stack, Minecraft.getInstance().player);
			int socket = 0;
			while (GemHelper.hasGem(stack, socket)) {
				ItemStack gem = new ItemStack(GemHelper.getGem(stack, socket).get());
				gems.add(gem);
				socket++;
			}
			if (sockets < gems.size()) sockets = gems.size();
		}
	}
}