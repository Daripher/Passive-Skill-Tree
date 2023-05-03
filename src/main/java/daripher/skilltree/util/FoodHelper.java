package daripher.skilltree.util;

import net.minecraft.world.item.ItemStack;

public class FoodHelper {
	private static final String RESTORATION_BONUS_TAG = "RestorationBonus";
	private static final String LIFE_REGENERATION_BONUS_TAG = "LifeRegenBonus";
	private static final String DAMAGE_BONUS_TAG = "DamageBonus";
	private static final String CRIT_DAMAGE_BONUS_TAG = "CritDamageBonus";
	private static final String HEALING_BONUS_TAG = "HealingBonus";

	public static void setRestorationBonus(ItemStack itemStack, float bonus) {
		itemStack.getOrCreateTag().putFloat(RESTORATION_BONUS_TAG, bonus);
	}

	public static boolean hasRestorationBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(RESTORATION_BONUS_TAG);
	}

	public static float getRestorationBonus(ItemStack itemStack) {
		return itemStack.getTag().getFloat(RESTORATION_BONUS_TAG);
	}

	public static void setLifeRegenerationBonus(ItemStack itemStack, float bonus) {
		itemStack.getOrCreateTag().putFloat(LIFE_REGENERATION_BONUS_TAG, bonus);
	}

	public static boolean hasLifeRegenerationBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(LIFE_REGENERATION_BONUS_TAG);
	}

	public static float getLifeRegenerationBonus(ItemStack itemStack) {
		return itemStack.getTag().getFloat(LIFE_REGENERATION_BONUS_TAG);
	}

	public static void setDamageBonus(ItemStack itemStack, float bonus) {
		itemStack.getOrCreateTag().putFloat(DAMAGE_BONUS_TAG, bonus);
	}

	public static boolean hasDamageBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(DAMAGE_BONUS_TAG);
	}

	public static float getDamageBonus(ItemStack itemStack) {
		return itemStack.getTag().getFloat(DAMAGE_BONUS_TAG);
	}

	public static void setCritDamageBonus(ItemStack itemStack, float bonus) {
		itemStack.getOrCreateTag().putFloat(CRIT_DAMAGE_BONUS_TAG, bonus);
	}

	public static boolean hasCritDamageBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(CRIT_DAMAGE_BONUS_TAG);
	}

	public static float getCritDamageBonus(ItemStack itemStack) {
		return itemStack.getTag().getFloat(CRIT_DAMAGE_BONUS_TAG);
	}

	public static void setHealingBonus(ItemStack itemStack, float bonus) {
		itemStack.getOrCreateTag().putFloat(HEALING_BONUS_TAG, bonus);
	}

	public static boolean hasHealingBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(HEALING_BONUS_TAG);
	}

	public static float getHealingBonus(ItemStack itemStack) {
		return itemStack.getTag().getFloat(HEALING_BONUS_TAG);
	}
}
