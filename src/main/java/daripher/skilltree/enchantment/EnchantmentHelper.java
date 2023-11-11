package daripher.skilltree.enchantment;

import daripher.skilltree.init.PSTAttributes;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

public class EnchantmentHelper {
  public static void amplifyEnchantments(
      List<EnchantmentInstance> enchantments, RandomSource random, Player player) {
    enchantments.replaceAll(
        enchantmentInstance -> amplifyEnchantment(enchantmentInstance, random, player));
  }

  private static EnchantmentInstance amplifyEnchantment(
      EnchantmentInstance enchantment, RandomSource random, Player player) {
    double amplificationChance = getEnchantmentAmplificationChance(enchantment, player);
    if (amplificationChance == 0) {
      return enchantment;
    }
    int levelBonus = (int) amplificationChance;
    amplificationChance -= levelBonus;
    int enchantmentLevel = enchantment.level + levelBonus;
    if (random.nextFloat() < amplificationChance) {
      enchantmentLevel++;
    }
    return new EnchantmentInstance(enchantment.enchantment, enchantmentLevel);
  }

  private static double getEnchantmentAmplificationChance(
      EnchantmentInstance enchantment, Player player) {
    double amplificationChance = getBaseEnchantmentAmplificationChance(player);
    EnchantmentCategory category = enchantment.enchantment.category;
    if (isArmorEnchantment(category)) {
      amplificationChance += getArmorEnchantmentAmplificationChance(player);
    }
    if (isWeaponEnchantment(category)) {
      amplificationChance += getWeaponEnchantmentAmplificationChance(player);
    }
    return amplificationChance;
  }

  public static int adjustEnchantmentCost(int cost, Player player) {
    cost = (int) (cost * getEnchantmentCostMultiplier(player));
    if (cost < 1) cost = 1;
    return cost;
  }

  private static double getEnchantmentCostMultiplier(Player player) {
    double multiplier = player.getAttributeValue(PSTAttributes.ENCHANTMENT_LEVEL_REQUIREMENT.get());
    return Math.round(multiplier * 100D) / 100D;
  }

  private static double getWeaponEnchantmentAmplificationChance(Player player) {
    return player.getAttributeValue(PSTAttributes.WEAPON_ENCHANTMENT_POWER.get()) - 1;
  }

  private static double getArmorEnchantmentAmplificationChance(Player player) {
    return player.getAttributeValue(PSTAttributes.ARMOR_ENCHANTMENT_POWER.get()) - 1;
  }

  private static double getBaseEnchantmentAmplificationChance(Player player) {
    return player.getAttributeValue(PSTAttributes.ENCHANTMENT_POWER.get()) - 1;
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
