package daripher.skilltree.item.gem;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.gem.GemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VacuciteItem extends GemItem {
	public VacuciteItem() {
		super(0x35866E);
	}

	@Override
	public void insertInto(Player player, ItemStack itemStack, int gemstoneSlot, double gemstoneStrength) {
		GemHelper.removeGems(itemStack);
	}

	@Override
	public boolean canInsertInto(Player currentPlayer, ItemStack baseItem, int gemstoneSlot) {
		return GemHelper.hasGem(baseItem, 0);
	}

	@Override
	public Triple<Attribute, Double, Operation> getGemBonus(Player player, ItemStack itemStack) {
		return null;
	}

	@Override
	protected void appenBonusesTooltip(List<Component> components) {
		components.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GOLD));
	}
}
