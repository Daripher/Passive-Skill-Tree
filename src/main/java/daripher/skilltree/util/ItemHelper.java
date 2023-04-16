package daripher.skilltree.util;

import java.util.function.Function;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.ItemAttributeModifierEvent;

public class ItemHelper {
	private static final String DEFENCE_BONUS_TAG = "DefenceBonus";
	private static final String DAMAGE_BONUS_TAG = "DamageBonus";
	private static final String ATTACK_SPEED_BONUS_TAG = "AttackSpeedBonus";
	private static final String TOUGHNESS_BONUS_TAG = "ToughnessBonus";
	private static final String DOUBLE_DAMAGE_CHANCE_TAG = "DoubleDamageChance";
	private static final String ARMOR_REDUCTION_TAG = "ArmorReduction";

	public static void setDefenceBonus(ItemStack itemStack, double bonus) {
		itemStack.getOrCreateTag().putDouble(DEFENCE_BONUS_TAG, bonus);
	}

	public static boolean hasDefenceBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(DEFENCE_BONUS_TAG);
	}

	public static double getDefenceBonus(ItemStack itemStack) {
		return itemStack.getTag().getDouble(DEFENCE_BONUS_TAG);
	}

	public static void setToughnessBonus(ItemStack itemStack, double bonus) {
		itemStack.getOrCreateTag().putDouble(TOUGHNESS_BONUS_TAG, bonus);
	}

	public static boolean hasToughnessBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(TOUGHNESS_BONUS_TAG);
	}

	public static double getToughnessBonus(ItemStack itemStack) {
		return itemStack.getTag().getDouble(TOUGHNESS_BONUS_TAG);
	}

	public static void setDamageBonus(ItemStack itemStack, double bonus) {
		itemStack.getOrCreateTag().putDouble(DAMAGE_BONUS_TAG, bonus);
	}

	public static boolean hasDamageBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(DAMAGE_BONUS_TAG);
	}

	public static double getDamageBonus(ItemStack itemStack) {
		return itemStack.getTag().getDouble(DAMAGE_BONUS_TAG);
	}

	public static void setAttackSpeedBonus(ItemStack itemStack, double bonus) {
		itemStack.getOrCreateTag().putDouble(ATTACK_SPEED_BONUS_TAG, bonus);
	}

	public static boolean hasAttackSpeedBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(ATTACK_SPEED_BONUS_TAG);
	}

	public static double getAttackSpeedBonus(ItemStack itemStack) {
		return itemStack.getTag().getDouble(ATTACK_SPEED_BONUS_TAG);
	}

	public static void setDoubleDamageChanceBonus(ItemStack itemStack, double bonus) {
		itemStack.getOrCreateTag().putDouble(DOUBLE_DAMAGE_CHANCE_TAG, bonus);
	}

	public static boolean hasDoubleDamageChanceBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(DOUBLE_DAMAGE_CHANCE_TAG);
	}

	public static double getDoubleDamageChanceBonus(ItemStack itemStack) {
		return itemStack.getTag().getDouble(DOUBLE_DAMAGE_CHANCE_TAG);
	}

	public static void setArmorReductionBonus(ItemStack itemStack, double bonus) {
		itemStack.getOrCreateTag().putDouble(ARMOR_REDUCTION_TAG, bonus);
	}

	public static boolean hasArmorReductionBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(ARMOR_REDUCTION_TAG);
	}

	public static double getArmorReductionBonus(ItemStack itemStack) {
		return itemStack.getTag().getDouble(ARMOR_REDUCTION_TAG);
	}

	public static void applyBaseModifierBonus(ItemAttributeModifierEvent event, Attribute attribute, Function<Double, Double> amountModifier) {
		var modifiers = event.getOriginalModifiers().get(attribute);

		modifiers.forEach(modifier -> {
			event.removeModifier(attribute, modifier);
			modifier = new AttributeModifier(modifier.getId(), modifier.getName(), amountModifier.apply(modifier.getAmount()), modifier.getOperation());
			event.addModifier(attribute, modifier);
		});
	}

	public static boolean isArmor(ItemStack itemStack) {
		return itemStack.getItem() instanceof ArmorItem;
	}

	public static boolean isShield(ItemStack itemStack) {
		return itemStack.getItem() instanceof ShieldItem;
	}

	public static boolean isWeapon(ItemStack itemStack) {
		return itemStack.getItem().getAttributeModifiers(EquipmentSlot.MAINHAND, itemStack).containsKey(Attributes.ATTACK_DAMAGE);
	}

	public static boolean isBow(ItemStack itemStack) {
		return itemStack.getItem() instanceof BowItem;
	}

	public static boolean canApplyGemstone(ItemStack itemStack) {
		return isArmor(itemStack) || isShield(itemStack) || isWeapon(itemStack) || isBow(itemStack);
	}

	public static boolean isHelmet(ItemStack itemStack) {
		return isArmor(itemStack) && ((ArmorItem) itemStack.getItem()).getSlot() == EquipmentSlot.HEAD;
	}

	public static boolean isChestplate(ItemStack itemStack) {
		return isArmor(itemStack) && ((ArmorItem) itemStack.getItem()).getSlot() == EquipmentSlot.CHEST;
	}

	public static boolean isLeggings(ItemStack itemStack) {
		return isArmor(itemStack) && ((ArmorItem) itemStack.getItem()).getSlot() == EquipmentSlot.LEGS;
	}

	public static boolean isBoots(ItemStack itemStack) {
		return isArmor(itemStack) && ((ArmorItem) itemStack.getItem()).getSlot() == EquipmentSlot.FEET;
	}

	public static boolean isPickaxe(ItemStack itemStack) {
		return itemStack.getItem() instanceof PickaxeItem;
	}

	public static boolean isFood(ItemStack itemStack) {
		return itemStack.getFoodProperties(null) != null;
	}
}
