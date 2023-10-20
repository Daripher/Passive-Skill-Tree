package daripher.skilltree.util;

import daripher.skilltree.init.PSTAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FoodHelper {
  private static final String RESTORATION_BONUS_TAG = "RestorationBonus";
  private static final String LIFE_REGENERATION_BONUS_TAG = "LifeRegenBonus";
  private static final String DAMAGE_BONUS_TAG = "DamageBonus";
  private static final String CRIT_DAMAGE_BONUS_TAG = "CritDamageBonus";
  private static final String HEALING_BONUS_TAG = "HealingBonus";

  public static void setCraftedFoodBonuses(@NotNull ItemStack craftedItem, Player player) {
    var restorationBonus = player.getAttributeValue(PSTAttributes.COOKED_FOOD_SATURATION.get()) - 1;
    if (restorationBonus > 0) {
      FoodHelper.setRestorationBonus(craftedItem, (float) restorationBonus);
    }
    var lifeRegenerationBonus =
        player.getAttributeValue(PSTAttributes.COOKED_FOOD_LIFE_REGENERATION.get());
    if (lifeRegenerationBonus > 0) {
      FoodHelper.setLifeRegenerationBonus(craftedItem, (float) lifeRegenerationBonus);
    }
    var damagePerRestorationBonus =
        player.getAttributeValue(PSTAttributes.COOKED_FOOD_DAMAGE_PER_SATURATION.get()) - 1;
    if (damagePerRestorationBonus > 0) {
      FoodHelper.setDamageBonus(craftedItem, (float) damagePerRestorationBonus);
    }
    var critDamagePerRestorationBonus =
        player.getAttributeValue(PSTAttributes.COOKED_FOOD_CRIT_DAMAGE_PER_SATURATION.get()) - 1;
    if (critDamagePerRestorationBonus > 0) {
      FoodHelper.setCritDamageBonus(craftedItem, (float) critDamagePerRestorationBonus);
    }
    var healingPerRestorationBonus =
        player.getAttributeValue(PSTAttributes.COOKED_FOOD_HEALING_PER_SATURATION.get()) - 1;
    if (healingPerRestorationBonus > 0) {
      FoodHelper.setHealingBonus(craftedItem, (float) healingPerRestorationBonus);
    }
  }

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
