package daripher.skilltree.enchantment;

import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.EnchantmentAmplificationBonus;
import daripher.skilltree.skill.bonus.player.EnchantmentRequirementBonus;
import daripher.skilltree.skill.bonus.player.FreeEnchantmentBonus;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

public class EnchantmentHelper {
  public static void amplifyEnchantments(
      List<EnchantmentInstance> enchantments, RandomSource random, Player player) {
    enchantments.replaceAll(
        enchantmentInstance -> amplifyEnchantment(enchantmentInstance, random, player));
  }

  private static EnchantmentInstance amplifyEnchantment(
      EnchantmentInstance enchantment, RandomSource random, Player player) {
    float amplificationChance = getAmplificationChance(enchantment, player);
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

  public static int adjustEnchantmentCost(int cost, Player player) {
    cost = (int) (cost * getEnchantmentCostMultiplier(player));
    if (cost < 1) cost = 1;
    return cost;
  }

  public static float getFreeEnchantmentChance(Player player) {
    return SkillBonusHandler.getSkillBonuses(player, FreeEnchantmentBonus.class).stream()
        .map(FreeEnchantmentBonus::chance)
        .reduce(Float::sum)
        .orElse(1f);
  }

  private static double getEnchantmentCostMultiplier(Player player) {
    return SkillBonusHandler.getSkillBonuses(player, EnchantmentRequirementBonus.class).stream()
        .map(EnchantmentRequirementBonus::multiplier)
        .reduce(Float::sum)
        .orElse(1f);
  }

  private static float getAmplificationChance(EnchantmentInstance enchantment, Player player) {
    return SkillBonusHandler.getSkillBonuses(player, EnchantmentAmplificationBonus.class).stream()
        .filter(bonus -> bonus.condition().met(enchantment.enchantment.category))
        .map(EnchantmentAmplificationBonus::chance)
        .reduce(Float::sum)
        .orElse(0f);
  }
}
