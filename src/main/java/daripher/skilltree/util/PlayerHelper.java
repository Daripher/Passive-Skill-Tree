package daripher.skilltree.util;

import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.GemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;

public class PlayerHelper {
  public static float getDamageMultiplier(ServerPlayer player, LivingEntity target, boolean melee) {
    double multiplier = 1D;
    if (target.hasEffect(MobEffects.POISON)) {
      multiplier += player.getAttributeValue(PSTAttributes.DAMAGE_AGAINST_POISONED.get()) - 1;
    }
    if (ItemHelper.isShield(player.getOffhandItem())) {
      multiplier += player.getAttributeValue(PSTAttributes.DAMAGE_WITH_SHIELD.get()) - 1;
    }
    if (ItemHelper.isWeapon(player.getMainHandItem()) && player.getMainHandItem().isEnchanted()) {
      multiplier += player.getAttributeValue(PSTAttributes.DAMAGE_WITH_ENCHANTED_WEAPON.get()) - 1;
    }
    if (GemHelper.hasGem(player.getMainHandItem(), 0)) {
      int gems = GemHelper.getGemsCount(player.getMainHandItem());
      double damagePerGem =
          player.getAttributeValue(PSTAttributes.DAMAGE_PER_GEM_IN_WEAPON.get()) - 1;
      multiplier += damagePerGem * gems;
      multiplier += player.getAttributeValue(PSTAttributes.DAMAGE_WITH_GEM_IN_WEAPON.get()) - 1;
    }
    if (player.getFoodData().getFoodLevel() >= 10) {
      multiplier += player.getAttributeValue(PSTAttributes.DAMAGE_IF_NOT_HUNGRY.get()) - 1;
    }
    if (!player.getActiveEffects().isEmpty()) {
      double damagePerEffect =
          player.getAttributeValue(PSTAttributes.DAMAGE_PER_POTION_EFFECT.get()) - 1;
      multiplier += player.getActiveEffects().size() * damagePerEffect;
      multiplier += player.getAttributeValue(PSTAttributes.DAMAGE_UNDER_POTION_EFFECT.get()) - 1;
    }
    for (EquipmentSlot slot : EquipmentSlot.values()) {
      ItemStack itemInSlot = player.getItemBySlot(slot);
      if (!itemInSlot.isEnchanted()) continue;
      double damagePerEnchantment =
          player.getAttributeValue(PSTAttributes.DAMAGE_PER_ENCHANTMENT.get()) - 1;
      int enchantments = itemInSlot.getAllEnchantments().size();
      multiplier += damagePerEnchantment * enchantments;
    }
    for (int enchantmentLevel : player.getMainHandItem().getAllEnchantments().values()) {
      double damagePerEnchantmentLevel =
          player.getAttributeValue(PSTAttributes.DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL.get()) - 1;
      multiplier += damagePerEnchantmentLevel * enchantmentLevel;
    }
    int foodLevel = player.getFoodData().getFoodLevel();
    if (foodLevel > 0) {
      double damagePerSatisfiedHunger =
          player.getAttributeValue(PSTAttributes.DAMAGE_PER_SATISFIED_HUNGER.get()) - 1;
      multiplier += foodLevel * damagePerSatisfiedHunger;
    }
    if (player.getHealth() < player.getMaxHealth()) {
      multiplier += player.getAttributeValue(PSTAttributes.DAMAGE_IF_DAMAGED.get()) - 1;
    }
    if (player.getHealth() < player.getMaxHealth() / 2) {
      multiplier += player.getAttributeValue(PSTAttributes.DAMAGE_IF_WOUNDED.get()) - 1;
    }
    if (target.isOnFire()) {
      multiplier += player.getAttributeValue(PSTAttributes.DAMAGE_AGAINST_BURNING.get()) - 1;
    }
    if (melee) multiplier += player.getAttributeValue(PSTAttributes.MELEE_DAMAGE.get()) - 1;
    double damageBonusPerDistance =
        player.getAttributeValue(PSTAttributes.DAMAGE_PER_DISTANCE_TO_ENEMY.get()) - 1;
    multiplier += damageBonusPerDistance * player.distanceTo(target);
    multiplier += getDamagePerDistanceToSpawn(player);
    return (float) multiplier;
  }

  public static float getProjectileDamageMultiplier(
      ServerPlayer player, LivingEntity target, Entity projectile, DamageSource damageSource) {
    double critChance = player.getAttributeValue(PSTAttributes.PROJECTILE_CRIT_CHANCE.get()) - 1;
    critChance += getCritChance(player, target);
    double multiplier = player.getAttributeValue(PSTAttributes.PROJECTILE_DAMAGE.get());
    boolean criticalHit = player.getRandom().nextFloat() < critChance;
    if (criticalHit) {
      multiplier += 0.5F;
      multiplier += player.getAttributeValue(PSTAttributes.PROJECTILE_CRIT_DAMAGE.get()) - 1;
      multiplier += getCritDamage(player, target, false);
    }
    multiplier += getDamageMultiplier(player, target, false) - 1;
    return (float) multiplier;
  }

  public static float getCritChance(ServerPlayer player, LivingEntity target) {
    double chance = player.getAttributeValue(PSTAttributes.CRIT_CHANCE.get()) - 1;
    if (target.hasEffect(MobEffects.POISON)) {
      chance += player.getAttributeValue(PSTAttributes.CRIT_CHANCE_AGAINST_POISONED.get()) - 1;
    }
    if (ItemHelper.isShield(player.getOffhandItem())) {
      chance += player.getAttributeValue(PSTAttributes.CRIT_CHANCE_WITH_SHIELD.get()) - 1;
    }
    if (target.isOnFire()) {
      chance += player.getAttributeValue(PSTAttributes.CRIT_CHANCE_AGAINST_BURNING.get()) - 1;
    }
    for (EquipmentSlot slot : EquipmentSlot.values()) {
      ItemStack itemInSlot = player.getItemBySlot(slot);
      if (!itemInSlot.isEnchanted()) continue;
      double critChancePerEnchantment =
          player.getAttributeValue(PSTAttributes.CRIT_CHANCE_PER_ENCHANTMENT.get()) - 1;
      int enchantmentCount = itemInSlot.getAllEnchantments().size();
      chance += critChancePerEnchantment * enchantmentCount;
    }
    return (float) chance;
  }

  public static float getCritDamage(ServerPlayer player, LivingEntity target, boolean melee) {
    double multiplier = player.getAttributeValue(PSTAttributes.CRIT_DAMAGE.get()) - 1;
    ItemStack mainhandItem = player.getMainHandItem();
    if (ItemHelper.isWeapon(mainhandItem)) {
      double critDamagePerEnchantment =
          player.getAttributeValue(PSTAttributes.CRIT_DAMAGE_PER_WEAPON_ENCHANTMENT.get()) - 1;
      int enchantments = mainhandItem.getAllEnchantments().size();
      multiplier += critDamagePerEnchantment * enchantments;
    }
    if (ItemHelper.isShield(player.getOffhandItem())) {
      multiplier += player.getAttributeValue(PSTAttributes.CRIT_DAMAGE_WITH_SHIELD.get()) - 1;
    }
    int foodLevel = player.getFoodData().getFoodLevel();
    if (foodLevel > 0) {
      double critDamagePerSatisfiedHunger =
          player.getAttributeValue(PSTAttributes.CRIT_DAMAGE_PER_SATISFIED_HUNGER.get()) - 1;
      multiplier += foodLevel * critDamagePerSatisfiedHunger;
    }
    if (target.hasEffect(MobEffects.POISON)) {
      multiplier += player.getAttributeValue(PSTAttributes.CRIT_DAMAGE_AGAINST_POISONED.get()) - 1;
    }
    if (melee) multiplier += player.getAttributeValue(PSTAttributes.MELEE_CRIT_DAMAGE.get()) - 1;
    return (float) multiplier;
  }

  private static double getDamagePerDistanceToSpawn(ServerPlayer player) {
    double damagePerDistance =
        player.getAttributeValue(PSTAttributes.DAMAGE_PER_DISTANCE_TO_SPAWN.get()) - 1;
    if (damagePerDistance == 0) return 0D;
    BlockPos spawnPos = player.getRespawnPosition();
    double distance;
    if (spawnPos != null) {
      distance = player.distanceToSqr(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
    } else {
      BlockPos worldSpawnPos = player.level.getSharedSpawnPos();
      distance =
          player.distanceToSqr(worldSpawnPos.getX(), worldSpawnPos.getY(), worldSpawnPos.getZ());
    }
    distance = Math.sqrt(distance);
    distance = Math.min(5000, distance);
    return distance * damagePerDistance;
  }

  public static boolean canEvadeDamage(DamageSource source) {
    if (!(source instanceof EntityDamageSource damageSource)) return false;
    return damageSource.getDirectEntity() instanceof LivingEntity
        || damageSource.getEntity() instanceof LivingEntity;
  }

  public static float getGemPower(Player player, ItemStack itemStack) {
    float power = (float) player.getAttributeValue(PSTAttributes.GEM_POWER.get());
    if (ItemHelper.isArmor(itemStack) || ItemHelper.isShield(itemStack)) {
      power += (float) (player.getAttributeValue(PSTAttributes.GEM_POWER_IN_ARMOR.get()) - 1);
    }
    if (ItemHelper.isWeapon(itemStack)) {
      power += (float) (player.getAttributeValue(PSTAttributes.GEM_POWER_IN_WEAPON.get()) - 1);
    }
    if (ItemHelper.isJewelry(itemStack)) {
      power += (float) (player.getAttributeValue(PSTAttributes.GEM_POWER_IN_JEWELRY.get()) - 1);
    }
    return power;
  }

  public static void hurtShield(Player player, final ItemStack shield, float amount) {
    if (!shield.canPerformAction(ToolActions.SHIELD_BLOCK)) return;
    if (!player.level.isClientSide) {
      player.awardStat(Stats.ITEM_USED.get(shield.getItem()));
    }
    if (amount < 3) return;
    amount = 1 + Mth.floor(amount);
    shield.hurtAndBreak(
        (int) amount,
        player,
        (player_) -> {
          player_.broadcastBreakEvent(InteractionHand.OFF_HAND);
          ForgeEventFactory.onPlayerDestroyItem(player, shield, InteractionHand.OFF_HAND);
        });
    if (shield.isEmpty()) {
      player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
      player.playSound(
          SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + player.level.random.nextFloat() * 0.4F);
    }
  }
}
