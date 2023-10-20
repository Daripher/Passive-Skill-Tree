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
    for (var i = 0; i < enchantments.size(); i++) {
      var enchantment = enchantments.get(i);
      var amplifiedEnchantment = amplifyEnchantment(enchantment, random, player);
      enchantments.set(i, amplifiedEnchantment);
    }
  }

  private static EnchantmentInstance amplifyEnchantment(
      EnchantmentInstance enchantment, RandomSource random, Player player) {
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

  private static double getEnchantmentAmplificationChance(
      EnchantmentInstance enchantment, Player player) {
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

  public static int adjustLevelRequirement(int requirement, Player player) {
    requirement *= getLevelRequirement(player);
    if (requirement < 1) requirement = 1;
    return requirement;
  }

  private static double getLevelRequirement(Player player) {
    double requirement =
        player.getAttributeValue(PSTAttributes.ENCHANTMENT_LEVEL_REQUIREMENT.get());
    return Math.round(requirement * 100D) / 100D;
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
