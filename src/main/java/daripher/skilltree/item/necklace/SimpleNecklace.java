package daripher.skilltree.item.necklace;

import java.util.List;

import daripher.skilltree.api.HasAdditionalSockets;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class SimpleNecklace extends NecklaceItem implements HasAdditionalSockets {
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> components, TooltipFlag tooltipFlag) {
		components.add(Component.translatable("gem.additional_socket").withStyle(ChatFormatting.YELLOW));
	}

	@Override
	public int getAdditionalSockets() {
		return 1;
	}
}
