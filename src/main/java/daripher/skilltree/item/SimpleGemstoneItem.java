package daripher.skilltree.item;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.util.ItemHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class SimpleGemstoneItem extends GemstoneItem {
	public SimpleGemstoneItem(int gemstoneColor) {
		super(gemstoneColor);
	}

	@Override
	protected Triple<Attribute, Double, Operation> getGemstoneBonus(Player player, ItemStack itemStack) {
		var slotType = Player.getEquipmentSlotForItem(itemStack);
		if (ItemHelper.isWeapon(itemStack) && slotType == EquipmentSlot.MAINHAND) {
			return getWeaponBonus();
		} else if (ItemHelper.isShield(itemStack) && slotType == EquipmentSlot.OFFHAND) {
			return getShieldBonus();
		} else if (ItemHelper.isHelmet(itemStack) && slotType == EquipmentSlot.HEAD) {
			return getHelmetBonus();
		} else if (ItemHelper.isChestplate(itemStack) && slotType == EquipmentSlot.CHEST) {
			return getChestplateBonus();
		} else if (ItemHelper.isLeggings(itemStack) && slotType == EquipmentSlot.LEGS) {
			return getLeggingsBonus();
		} else if (ItemHelper.isBoots(itemStack) && slotType == EquipmentSlot.FEET) {
			return getBootsBonus();
		} else if (ItemHelper.isBow(itemStack) && slotType == EquipmentSlot.MAINHAND) {
			return getBowBonus();
		}
		return null;
	}

	public abstract Triple<Attribute, Double, Operation> getHelmetBonus();

	public abstract Triple<Attribute, Double, Operation> getChestplateBonus();

	public abstract Triple<Attribute, Double, Operation> getLeggingsBonus();

	public abstract Triple<Attribute, Double, Operation> getBootsBonus();

	public abstract Triple<Attribute, Double, Operation> getWeaponBonus();

	public abstract Triple<Attribute, Double, Operation> getShieldBonus();

	public abstract Triple<Attribute, Double, Operation> getBowBonus();
}
