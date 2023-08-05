package daripher.skilltree.potion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;

public class PotionHelper {
	private static final String CUSTOM_COLOR_TAG = "CustomPotionColor";
	private static final String MIXED_POTION_TAG = "MixedPotion";
	private static final String EFFECT_STRENGTH_TAG = "EffectStrength";
	private static final String EFFECT_DURATION_TAG = "EffectDuration";

	public static boolean isHarmfulPotion(ItemStack stack) {
		for (MobEffectInstance effect : PotionUtils.getMobEffects(stack)) {
			if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) return true;
		}
		return false;
	}

	public static boolean isBeneficialPotion(ItemStack stack) {
		for (MobEffectInstance effect : PotionUtils.getMobEffects(stack)) {
			if (effect.getEffect().getCategory() == MobEffectCategory.BENEFICIAL) return true;
		}
		return false;
	}

	public static boolean isPoison(ItemStack stack) {
		for (MobEffectInstance effect : PotionUtils.getMobEffects(stack)) {
			if (effect.getEffect() == MobEffects.POISON || effect.getEffect() == MobEffects.HARM) return true;
		}
		return false;
	}

	public static boolean isHealingPotion(ItemStack stack) {
		for (MobEffectInstance effect : PotionUtils.getMobEffects(stack)) {
			if (effect.getEffect() == MobEffects.REGENERATION || effect.getEffect() == MobEffects.HEAL
					|| effect.getEffect() == MobEffects.HEALTH_BOOST)
				return true;
		}
		return false;
	}

	public static boolean isSuperiorPotion(ItemStack stack) {
		return stack.hasTag() && (stack.getTag().contains(EFFECT_STRENGTH_TAG) || stack.getTag().contains(EFFECT_DURATION_TAG));
	}

	public static boolean isMixture(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains(MIXED_POTION_TAG);
	}

	public static boolean isPotion(ItemStack stack) {
		return stack.getItem() instanceof PotionItem;
	}

	public static void enhancePotion(ItemStack stack, float amplificationChance, float durationBonus) {
		if (PotionUtils.getMobEffects(stack).isEmpty()) return;
		int strength = 0;
		if (amplificationChance > 1) {
			strength += (int) amplificationChance;
			amplificationChance -= strength;
		}
		if (amplificationChance != 0) {
			var random = new Random();
			if (random.nextFloat() < amplificationChance) strength++;
		}
		stack.getOrCreateTag().putInt(EFFECT_STRENGTH_TAG, strength);
		stack.getOrCreateTag().putFloat(EFFECT_DURATION_TAG, durationBonus);
	}

	public static float getDurationBonus(ItemStack stack) {
		return stack.hasTag() ? stack.getTag().getFloat(EFFECT_DURATION_TAG) : 0F;
	}

	public static int getStrengthBonus(ItemStack stack) {
		return stack.hasTag() ? stack.getTag().getInt(EFFECT_STRENGTH_TAG) : 0;
	}

	public static void setPotionColor(ItemStack itemStack, int color) {
		CompoundTag tag = itemStack.getTag();
		if (tag != null) tag.putInt(CUSTOM_COLOR_TAG, color);
	}

	public static ItemStack mixPotions(ItemStack potionStack1, ItemStack potionStack2) {
		List<ItemStack> potions = Arrays.asList(potionStack1, potionStack2);
		potions = potions.stream().sorted(PotionHelper::comparePotions).toList();
		var result = new ItemStack(potionStack1.getItem(), 2);
		var effects = new ArrayList<MobEffectInstance>();
		potions.stream().map(PotionUtils::getMobEffects).forEach(effects::addAll);
		PotionUtils.setCustomEffects(result, effects);
		setPotionColor(result, PotionUtils.getColor(potions.get(0)));
		result.getOrCreateTag().putBoolean(MIXED_POTION_TAG, true);
		return result;
	}

	private static int comparePotions(ItemStack potionStack1, ItemStack potionStack2) {
		return potionStack1.getDescriptionId().compareTo(potionStack2.getDescriptionId());
	}
}
