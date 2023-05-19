package daripher.skilltree.enchantment;

import java.util.List;

import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

public class EnchantmentHelper {
	public static void amplifyEnchantments(List<EnchantmentInstance> enchantments, RandomSource random, Player player) {
		for (var i = 0; i < enchantments.size(); i++) {
			var enchantment = enchantments.get(i);
			var amplifiedEnchantment = amplifyEnchantment(enchantment, random, player);
			enchantments.set(i, amplifiedEnchantment);
		}
	}

	private static EnchantmentInstance amplifyEnchantment(EnchantmentInstance enchantment, RandomSource random, Player player) {
		var amplificationChance = getEnchantmentAmplificationChance(enchantment, player);
		if (amplificationChance == 0) {
			return enchantment;
		}
		var levelBonus = (int) amplificationChance;
		amplificationChance -= levelBonus;
		var enchantmentLevel = enchantment.level + levelBonus;
		if (random.nextFloat() < amplificationChance) {
			enchantmentLevel++;
		}
		return new EnchantmentInstance(enchantment.enchantment, enchantmentLevel);
	}

	private static double getEnchantmentAmplificationChance(EnchantmentInstance enchantment, Player player) {
		var amplificationChance = getBaseEnchantmentAmplificationChance(player);
		var category = enchantment.enchantment.category;
		if (isArmorEnchantment(category)) {
			amplificationChance += getArmorEnchantmentAmplificationChance(player);
		}
		if (isWeaponEnchantment(category)) {
			amplificationChance += getWeaponEnchantmentAmplificationChance(player);
		}
		return amplificationChance;
	}

	public static int decreaseLevelRequirement(int levelRequirement, Player player) {
		var decrease = getLevelRequirementDecrease(player);
		if (decrease == 0) {
			return levelRequirement;
		}
		return decreaseRequirement(levelRequirement, decrease);
	}

	private static int decreaseRequirement(int cost, double reduction) {
		if (cost == 0) {
			return cost;
		}
		cost *= (1 - reduction);
		if (cost < 1) {
			cost = 1;
		}
		return cost;
	}

	private static double getLevelRequirementDecrease(Player player) {
		return player.getAttributeValue(SkillTreeAttributes.ENCHANTMENT_LEVEL_REQUIREMENT_DECREASE.get()) - 1;
	}
	
	private static double getWeaponEnchantmentAmplificationChance(Player player) {
		return player.getAttributeValue(SkillTreeAttributes.WEAPON_ENCHANTMENTS_AMPLIFICATION_CHANCE.get());
	}

	private static double getArmorEnchantmentAmplificationChance(Player player) {
		return player.getAttributeValue(SkillTreeAttributes.ARMOR_ENCHANTMENTS_AMPLIFICATION_CHANCE.get());
	}

	private static double getBaseEnchantmentAmplificationChance(Player player) {
		return player.getAttributeValue(SkillTreeAttributes.ENCHANTMENTS_AMPLIFICATION_CHANCE.get());
	}
	
	private static boolean isWeaponEnchantment(EnchantmentCategory enchantmentCategory) {
		return enchantmentCategory == EnchantmentCategory.WEAPON
			|| enchantmentCategory == EnchantmentCategory.BOW
			|| enchantmentCategory == EnchantmentCategory.CROSSBOW;
	}

	private static boolean isArmorEnchantment(EnchantmentCategory enchantmentCategory) {
		return enchantmentCategory == EnchantmentCategory.ARMOR
			|| enchantmentCategory == EnchantmentCategory.ARMOR_FEET
			|| enchantmentCategory == EnchantmentCategory.ARMOR_LEGS
			|| enchantmentCategory == EnchantmentCategory.ARMOR_HEAD;
	}
}
