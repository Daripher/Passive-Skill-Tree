package daripher.skilltree.item.gem;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.util.ItemHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class SimpleGemstoneItem extends GemItem {
	public SimpleGemstoneItem(int gemstoneColor) {
		super(gemstoneColor);
	}

	@Override
	public Triple<Attribute, Double, Operation> getGemBonus(Player player, ItemStack itemStack) {
		var itemSlot = ItemHelper.getSlotForItem(itemStack);
		if (ItemHelper.isWeapon(itemStack) && itemSlot == EquipmentSlot.MAINHAND) {
			return getWeaponBonus();
		} else if (ItemHelper.isShield(itemStack) && itemSlot == EquipmentSlot.OFFHAND) {
			return getShieldBonus();
		} else if (ItemHelper.isHelmet(itemStack) && itemSlot == EquipmentSlot.HEAD) {
			return getHelmetBonus();
		} else if (ItemHelper.isChestplate(itemStack) && itemSlot == EquipmentSlot.CHEST) {
			return getChestplateBonus();
		} else if (ItemHelper.isLeggings(itemStack) && itemSlot == EquipmentSlot.LEGS) {
			return getLeggingsBonus();
		} else if (ItemHelper.isBoots(itemStack) && itemSlot == EquipmentSlot.FEET) {
			return getBootsBonus();
		} else if (ItemHelper.isBow(itemStack) && itemSlot == EquipmentSlot.MAINHAND) {
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
