package daripher.skilltree.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class VoidGemstoneItem extends Item {
	public VoidGemstoneItem() {
		super(new Properties().tab(CreativeModeTab.TAB_MATERIALS));
	}

	@Override
	public MutableComponent getName(ItemStack itemStack) {
		return Component.literal("").append(super.getName(itemStack)).withStyle(Style.EMPTY.withColor(0x796767));
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level level, List<Component> components, TooltipFlag tooltipFlag) {
		var tooltip = Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.DARK_PURPLE).withStyle(ChatFormatting.ITALIC);
		components.add(tooltip);
	}
}
