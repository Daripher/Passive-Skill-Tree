package daripher.skilltree.util;

import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.item.ItemHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;

public class PlayerHelper {
	public static float getDamageMultiplier(Player player, LivingEntity target) {
		var multiplier = 1F;
		if (target.hasEffect(MobEffects.POISON)) {
			multiplier += PlayerHelper.getDamageBonusAgainsPoisoned(player);
		}
		if (!player.getActiveEffects().isEmpty()) {
			multiplier += PlayerHelper.getDamageBonusUnderPotionEffect(player);
		}
		if (ItemHelper.isShield(player.getOffhandItem())) {
			multiplier += PlayerHelper.getDamageBonusWithShield(player);
		}
		if (ItemHelper.isWeapon(player.getMainHandItem()) && player.getMainHandItem().isEnchanted()) {
			multiplier += PlayerHelper.getDamageBonusWithEnchantedWeapon(player);
		}
		if (ItemHelper.isPickaxe(player.getMainHandItem())) {
			multiplier += PlayerHelper.getDamageBonusWithPickaxe(player);
		}
		if (player.getFoodData().getFoodLevel() >= 10) {
			multiplier += player.getAttributeValue(SkillTreeAttributes.DAMAGE_IF_NOT_HUNGRY.get()) - 1;
		}
		if (!player.getActiveEffects().isEmpty()) {
			var damagePerEffect = player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_POTION_EFFECT.get()) - 1;
			multiplier += player.getActiveEffects().size() * damagePerEffect;
		}
		for (var slot : EquipmentSlot.values()) {
			var itemInSlot = player.getItemBySlot(slot);
			if (!itemInSlot.isEnchanted()) {
				continue;
			}
			var damagePerEnchantment = PlayerHelper.getDamageBonusPerEnchantment(player);
			var enchantmentCount = itemInSlot.getAllEnchantments().size();
			multiplier += damagePerEnchantment * enchantmentCount;
		}
		for (var enchantmentLevel : player.getMainHandItem().getAllEnchantments().values()) {
			var damagePerEnchantmentLevel = PlayerHelper.getDamageBonusPerWeaponEnchantmentLevel(player);
			multiplier += damagePerEnchantmentLevel * enchantmentLevel;
		}
		var foodLevel = player.getFoodData().getFoodLevel();
		if (foodLevel > 0) {
			var damagePerSatisfiedHunger = player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_SATISFIED_HUNGER.get()) - 1;
			multiplier += foodLevel * damagePerSatisfiedHunger;
		}
		var damagePerArrowInEnemy = player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_ARROW_IN_ENEMY.get()) - 1;
		multiplier += damagePerArrowInEnemy * target.getArrowCount();
		return multiplier;
	}

	public static float getArrowDamageMultiplier(Player player, LivingEntity target, AbstractArrow arrow) {
		if (!arrow.isCritArrow()) {
			var critChance = getCritChance(player, target);
			if (player.getRandom().nextFloat() < critChance) {
				arrow.setCritArrow(true);
			}
		}
		var damageMultiplier = getArrowDamageBonus(player) + 1;
		if (arrow.isCritArrow()) {
			var arrowCritDamageBonus = getArrowCritDamageBonus(player);
			damageMultiplier += arrowCritDamageBonus;
			damageMultiplier += getCritDamageMultiplier(player, target) - 1.5F;
		}
		var damageBonusPerDistance = player.getAttributeValue(SkillTreeAttributes.ARROW_DAMAGE_PER_DISTANCE.get()) - 1;
		damageMultiplier += damageBonusPerDistance * player.distanceTo(target);
		damageMultiplier += getDamageMultiplier(player, target) - 1;
		return damageMultiplier;
	}

	public static float getCritChance(Player player, LivingEntity target) {
		var chance = (float) (player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE.get()) - 1);
		if (target.hasEffect(MobEffects.POISON)) {
			chance += PlayerHelper.getCritChanceAgainstPoisoned(player);
		}
		if (ItemHelper.isShield(player.getOffhandItem())) {
			chance += PlayerHelper.getCritChanceWithShield(player);
		}
		for (var slot : EquipmentSlot.values()) {
			var itemInSlot = player.getItemBySlot(slot);
			if (!itemInSlot.isEnchanted()) {
				continue;
			}
			var critChancePerEnchantment = PlayerHelper.getCritChancePerEnchantment(player);
			var enchantmentCount = itemInSlot.getAllEnchantments().size();
			chance += critChancePerEnchantment * enchantmentCount;
		}
		return chance;
	}

	public static float getCritDamageMultiplier(Player player, LivingEntity target) {
		var multiplier = 1.5F;
		multiplier += player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE.get()) - 1;
		var mainhandItem = player.getMainHandItem();
		if (ItemHelper.isWeapon(mainhandItem)) {
			var critDamagePerWeaponEnchantment = PlayerHelper.getCritDamagePerWeaponEnchantment(player);
			var enchantmentCount = mainhandItem.getAllEnchantments().size();
			multiplier += critDamagePerWeaponEnchantment * enchantmentCount;
		}
		if (ItemHelper.isShield(player.getOffhandItem())) {
			multiplier += getCritDamageWithShield(player);
		}
		var foodLevel = player.getFoodData().getFoodLevel();
		if (foodLevel > 0) {
			var critDamagePerSatisfiedHunger = player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_PER_SATISFIED_HUNGER.get()) - 1;
			multiplier += foodLevel * critDamagePerSatisfiedHunger;
		}
		if (target.hasEffect(MobEffects.POISON)) {
			multiplier += getCritDamageAgainstPoisoned(player);
		}
		return multiplier;
	}

	public static float getFlatDamageBonus(Player player) {
		var bonus = 0F;
		if (ItemHelper.isPickaxe(player.getMainHandItem())) {
			bonus += getFlatPickaxeDamageBonus(player);
		}
		return bonus;
	}

	public static float getFlatArrowDamageBonus(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.ARROW_DAMAGE_BONUS.get());
	}

	public static float getFlatPickaxeDamageBonus(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.PICKAXE_DAMAGE_BONUS.get());
	}

	public static float getArrowDamageBonus(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.ARROW_DAMAGE.get()) - 1;
	}

	public static double getArrowCritDamageBonus(Player player) {
		return player.getAttributeValue(SkillTreeAttributes.ARROW_CRIT_DAMAGE.get()) - 1;
	}

	public static float getDamageBonusPerEnchantment(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_ENCHANTMENT.get()) - 1;
	}

	public static float getDamageBonusPerWeaponEnchantmentLevel(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL.get()) - 1;
	}

	public static float getDamageBonusUnderPotionEffect(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_UNDER_POTION_EFFECT.get()) - 1;
	}

	public static float getDamageBonusAgainsPoisoned(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_AGAINST_POISONED.get()) - 1;
	}

	public static float getDamageBonusWithShield(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_WITH_SHIELD.get()) - 1;
	}

	public static float getDamageBonusWithEnchantedWeapon(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_WITH_ENCHANTED_WEAPON.get()) - 1;
	}

	public static float getDamageBonusWithPickaxe(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.PICKAXE_DAMAGE.get()) - 1;
	}

	public static float getCritChanceAgainstPoisoned(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_AGAINST_POISONED.get()) - 1;
	}

	public static float getCritChancePerEnchantment(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_PER_ENCHANTMENT.get()) - 1;
	}

	public static float getCritChanceWithShield(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_WITH_SHIELD.get()) - 1;
	}

	public static float getCritDamagePerWeaponEnchantment(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_PER_WEAPON_ENCHANTMENT.get()) - 1;
	}

	public static float getCritDamageWithShield(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_WITH_SHIELD.get()) - 1;
	}

	public static float getCritDamageAgainstPoisoned(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_AGAINST_POISONED.get()) - 1;
	}

	public static boolean canEvadeDamage(DamageSource source) {
		return source instanceof EntityDamageSource entityDamageSource && entityDamageSource.getDirectEntity() instanceof LivingEntity;
	}

	public static float getGemPower(Player player, ItemStack itemStack) {
		var gemPower = player.getAttributeValue(SkillTreeAttributes.GEM_POWER.get());
		var craftingArmor = ItemHelper.isArmor(itemStack) || ItemHelper.isShield(itemStack);
		var craftingWeapon = ItemHelper.isWeapon(itemStack);
		if (craftingArmor) {
			var gemPowerInArmor = player.getAttributeValue(SkillTreeAttributes.GEM_POWER_IN_ARMOR.get()) - 1;
			gemPower += gemPowerInArmor;
		} else if (craftingWeapon) {
			var gemPowerInWeapon = player.getAttributeValue(SkillTreeAttributes.GEM_POWER_IN_WEAPON.get()) - 1;
			gemPower += gemPowerInWeapon;
		}
		return (float) gemPower;
	}

	public static void hurtShield(Player player, final ItemStack shield, float amount) {
		if (shield.canPerformAction(ToolActions.SHIELD_BLOCK)) {
			if (!player.level.isClientSide) {
				player.awardStat(Stats.ITEM_USED.get(shield.getItem()));
			}
			if (amount >= 3.0F) {
				int i = 1 + Mth.floor(amount);
				shield.hurtAndBreak(i, player, (player_) -> {
					player_.broadcastBreakEvent(InteractionHand.OFF_HAND);
					ForgeEventFactory.onPlayerDestroyItem(player, shield, InteractionHand.OFF_HAND);
				});
				if (shield.isEmpty()) {
					player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
					player.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + player.level.random.nextFloat() * 0.4F);
				}
			}
		}
	}
}
