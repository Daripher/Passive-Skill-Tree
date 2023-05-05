package daripher.skilltree.item;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VoidGemstoneItem extends GemstoneItem {
	public VoidGemstoneItem() {
		super(0x796767);
	}

	@Override
	public void insertInto(Player player, ItemStack itemStack, int gemstoneSlot, double gemstoneStrength) {
		itemStack.getTag().remove(GEMSTONES_TAG);
	}

	@Override
	public boolean canInsertInto(Player currentPlayer, ItemStack baseItem, int gemstoneSlot) {
		return GemstoneItem.hasGemstone(baseItem, 0);
	}

	@Override
	protected Triple<Attribute, Double, Operation> getGemstoneBonus(Player player, ItemStack itemStack) {
		return null;
	}
}
