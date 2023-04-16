package daripher.skilltree.util;

import net.minecraft.world.item.ItemStack;

public class FoodHelper {
	private static final String RESTORATION_BONUS_TAG = "RestorationBonus";
	private static final String HEALING_BONUS_TAG = "HealingBonus";
	private static final String ATTACK_SPEED_BONUS_TAG = "AttackSpeedBonus";
	private static final String DAMAGE_PER_RESTORATION_BONUS_TAG = "DamagePerRestorationBonus";
	private static final String CRIT_CHANCE_BONUS_TAG = "CritChanceBonus";

	public static void setRestorationBonus(ItemStack itemStack, float bonus) {
		itemStack.getOrCreateTag().putFloat(RESTORATION_BONUS_TAG, bonus);
	}

	public static boolean hasRestorationBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(RESTORATION_BONUS_TAG);
	}

	public static float getRestorationBonus(ItemStack itemStack) {
		return itemStack.getTag().getFloat(RESTORATION_BONUS_TAG);
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
	
	public static void setAttackSpeedBonus(ItemStack itemStack, float bonus) {
		itemStack.getOrCreateTag().putFloat(ATTACK_SPEED_BONUS_TAG, bonus);
	}

	public static boolean hasAttackSpeedBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(ATTACK_SPEED_BONUS_TAG);
	}

	public static float getAttackSpeedBonus(ItemStack itemStack) {
		return itemStack.getTag().getFloat(ATTACK_SPEED_BONUS_TAG);
	}
	
	public static void setDamagePerRestorationBonus(ItemStack itemStack, float bonus) {
		itemStack.getOrCreateTag().putFloat(DAMAGE_PER_RESTORATION_BONUS_TAG, bonus);
	}

	public static boolean hasDamagePerRestorationBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(DAMAGE_PER_RESTORATION_BONUS_TAG);
	}

	public static float getDamagePerRestorationBonus(ItemStack itemStack) {
		return itemStack.getTag().getFloat(DAMAGE_PER_RESTORATION_BONUS_TAG);
	}
	
	public static void setCritChanceBonus(ItemStack itemStack, float bonus) {
		itemStack.getOrCreateTag().putFloat(CRIT_CHANCE_BONUS_TAG, bonus);
	}

	public static boolean hasCritChanceBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(CRIT_CHANCE_BONUS_TAG);
	}

	public static float getCritChanceBonus(ItemStack itemStack) {
		return itemStack.getTag().getFloat(CRIT_CHANCE_BONUS_TAG);
	}
}
