package daripher.skilltree.item;

import java.awt.Color;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class RainbowGemstoneItem extends Item {
	public RainbowGemstoneItem() {
		super(new Properties().tab(CreativeModeTab.TAB_MATERIALS));
	}

	@Override
	public Component getName(ItemStack itemStack) {
		return applyAnimatedRainbowStyle(super.getName(itemStack));
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level level, List<Component> components, TooltipFlag tooltipFlag) {
		components.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.DARK_PURPLE).withStyle(ChatFormatting.ITALIC));
	}

	private Component applyAnimatedRainbowStyle(Component component) {
		var minecraft = Minecraft.getInstance();
		var player = minecraft.player;
		if (player == null) {
			return component;
		}
		var styledComponent = Component.empty();
		var hue = (player.tickCount % 360) / 360F;
		var characters = component.getString().toCharArray();
		for (var character : characters) {
			var color = Color.getHSBColor(hue, 0.6F, 1F).getRGB();
			var characterComponent = Component.literal("" + character);
			var colorStyle = Style.EMPTY.withColor(color);
			var coloredCharacter = characterComponent.withStyle(colorStyle);
			styledComponent.append(coloredCharacter);
			hue += 0.05F;
		}
		return styledComponent;
	}
}
