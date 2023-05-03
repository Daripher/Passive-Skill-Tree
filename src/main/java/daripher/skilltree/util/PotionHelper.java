package daripher.skilltree.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionHelper {
	private static final String CUSTOM_POTION_COLOR_TAG = "CustomPotionColor";
	private static final String ACTUAL_POTION_TAG = "ActualPotion";

	public static boolean isHarmfulPotion(ItemStack itemStack) {
		var potion = PotionUtils.getPotion(itemStack);
		var effects = potion.getEffects();
		for (var effect : effects) {
			if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
				return true;
			}
		}
		return false;
	}

	public static boolean isBeneficialPotion(ItemStack itemStack) {
		var potion = PotionUtils.getPotion(itemStack);
		var effects = potion.getEffects();
		for (var effect : effects) {
			if (effect.getEffect().getCategory() == MobEffectCategory.BENEFICIAL) {
				return true;
			}
		}
		return false;
	}

	public static void setPotionColor(ItemStack itemStack, int color) {
		var itemTag = itemStack.getTag();

		if (itemTag != null) {
			itemTag.putInt(CUSTOM_POTION_COLOR_TAG, color);
		}
	}

	public static void setActualPotion(ItemStack itemStack, Potion potion) {
		var potionId = ForgeRegistries.POTIONS.getKey(potion);

		if (potion == Potions.EMPTY) {
			itemStack.removeTagKey(ACTUAL_POTION_TAG);
		} else {
			itemStack.getOrCreateTag().putString(ACTUAL_POTION_TAG, potionId.toString());
		}
	}

	public static ItemStack getActualPotionStack(ItemStack itemStack) {
		if (!itemStack.hasTag()) {
			return ItemStack.EMPTY;
		}

		if (!itemStack.getTag().contains(ACTUAL_POTION_TAG)) {
			return itemStack;
		}

		var actualPotion = getActualPotion(itemStack);
		var actualPotionStack = new ItemStack(Items.POTION);
		PotionUtils.setPotion(actualPotionStack, actualPotion);
		return actualPotionStack;
	}

	public static Potion getActualPotion(ItemStack potionStack) {
		if (!potionStack.hasTag()) {
			return Potions.EMPTY;
		}

		if (!potionStack.getTag().contains(ACTUAL_POTION_TAG)) {
			return PotionUtils.getPotion(potionStack);
		}

		var actualPotionId = potionStack.getTag().getString(ACTUAL_POTION_TAG);
		var actualPotion = Potion.byName(actualPotionId);
		return actualPotion;
	}

	public static boolean isSuperiorPotion(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(ACTUAL_POTION_TAG);
	}

	public static boolean isPotion(ItemStack itemStack) {
		return itemStack.getItem() instanceof PotionItem;
	}

	public static void enhancePotion(ItemStack itemStack, float amplificationChance, float durationBonus) {
		var potion = PotionUtils.getPotion(itemStack);
		var effects = potion.getEffects();

		if (effects.isEmpty()) {
			return;
		}

		var enhancedEffects = enhanceEffects(effects, amplificationChance, durationBonus);
		PotionUtils.setPotion(itemStack, Potions.EMPTY);
		PotionUtils.setCustomEffects(itemStack, enhancedEffects);
		PotionHelper.setActualPotion(itemStack, potion);
		PotionHelper.setPotionColor(itemStack, PotionUtils.getColor(potion));
	}

	private static List<MobEffectInstance> enhanceEffects(List<MobEffectInstance> effects, float amplificationChance, float durationBonus) {
		var random = new Random();
		var enhancedEffects = new ArrayList<MobEffectInstance>();

		effects.forEach(effect -> {
			var duration = (int) (effect.getDuration() * (1 + durationBonus));
			var amplifier = effect.getAmplifier();
			var chance = amplificationChance;

			while (chance > 1) {
				chance--;
				amplifier++;
			}

			if (random.nextFloat() < chance) {
				amplifier += 1;
			}

			enhancedEffects.add(new MobEffectInstance(effect.getEffect(), duration, amplifier));
		});

		return enhancedEffects;
	}
}
