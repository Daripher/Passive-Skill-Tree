package daripher.skilltree.util;

import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class PlayerHelper {
	public static float getDamageMultiplier(Player player, LivingEntity target) {
		var damageMultiplier = 1F;
		if (target.hasEffect(MobEffects.POISON)) {
			damageMultiplier += PlayerHelper.getDamageBonusAgainsPoisoned(player);
		}
		if (!player.getActiveEffects().isEmpty()) {
			damageMultiplier += PlayerHelper.getDamageBonusUnderPotionEffect(player);
		}
		if (ItemHelper.isShield(player.getOffhandItem())) {
			damageMultiplier += PlayerHelper.getDamageBonusWithShield(player);
		}
		if (ItemHelper.isPickaxe(player.getMainHandItem())) {
			damageMultiplier += PlayerHelper.getDamageBonusWithPickaxe(player);
		}
		if (player.hasEffect(SkillTreeEffects.DELICACY.get())) {
			damageMultiplier += PlayerHelper.getDamageBonusIfAteRecently(player);
		}
		if (!player.getActiveEffects().isEmpty()) {
			var damagePerEffect = player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_POTION_EFFECT_MULTIPLIER.get()) - 1;
			damageMultiplier += player.getActiveEffects().size() * damagePerEffect;
		}
		for (var slot : EquipmentSlot.values()) {
			var itemInSlot = player.getItemBySlot(slot);
			if (!itemInSlot.isEnchanted()) {
				continue;
			}
			var damagePerEnchantment = PlayerHelper.getDamageBonusPerEnchantment(player);
			var enchantmentCount = itemInSlot.getAllEnchantments().size();
			damageMultiplier += damagePerEnchantment * enchantmentCount;
		}
		for (var enchantmentLevel : player.getMainHandItem().getAllEnchantments().values()) {
			var damagePerEnchantmentLevel = PlayerHelper.getDamageBonusPerWeaponEnchantmentLevel(player);
			damageMultiplier += damagePerEnchantmentLevel * enchantmentLevel;
		}
		return damageMultiplier;
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
		var damageBonusPerDistance = player.getAttributeValue(SkillTreeAttributes.ARROW_DAMAGE_PER_DISTANCE_MULTIPLIER.get()) - 1;
		damageMultiplier += damageBonusPerDistance * player.distanceTo(target);
		damageMultiplier += getDamageMultiplier(player, target) - 1;
		return damageMultiplier;
	}

	public static float getCritChance(Player player, LivingEntity target) {
		var chance = 0F;
		if (target.hasEffect(MobEffects.POISON)) {
			chance += PlayerHelper.getCritChanceAgainstPoisoned(player);
		}
		if (ItemHelper.isShield(player.getOffhandItem())) {
			chance += PlayerHelper.getCritChanceWithShield(player);
		}
		if (ItemHelper.isAxe(player.getMainHandItem())) {
			chance += PlayerHelper.getCritChanceWithAxe(player);
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
		multiplier += player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_MULTIPLIER.get()) - 1;
		for (var slot : EquipmentSlot.values()) {
			var itemInSlot = player.getItemBySlot(slot);
			if (!itemInSlot.isEnchanted()) {
				continue;
			}
			var critDamagePerEnchantment = PlayerHelper.getCritDamagePerEnchantment(player);
			var enchantmentCount = itemInSlot.getAllEnchantments().size();
			multiplier += critDamagePerEnchantment * enchantmentCount;
		}
		if (ItemHelper.isShield(player.getOffhandItem())) {
			multiplier += getCritDamageWithShield(player);
		}
		var foodLevel = player.getFoodData().getFoodLevel();
		if (foodLevel > 0) {
			var critDamagePerSatisfiedHunger = player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_MULTIPLIER_PER_SATISFIED_HUNGER.get()) - 1;
			multiplier += foodLevel * critDamagePerSatisfiedHunger;
		}
		return multiplier;
	}

	public static float getAttackSpeedBonus(Player player) {
		var bonus = 0F;
		if (ItemHelper.isPickaxe(player.getMainHandItem())) {
			bonus += getAttackSpeedBonusWithPickaxe(player);
		}
		if (ItemHelper.isAxe(player.getMainHandItem())) {
			bonus += getAttackSpeedBonusWithAxe(player);
		}
		return bonus;
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
		return (float) player.getAttributeValue(SkillTreeAttributes.ARROW_DAMAGE_MULTIPLIER.get()) - 1;
	}

	public static double getArrowCritDamageBonus(Player player) {
		return player.getAttributeValue(SkillTreeAttributes.ARROW_CRIT_DAMAGE_MULTIPLIER.get()) - 1;
	}

	public static float getDamageBonusPerEnchantment(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_ENCHANTMENT_MULTIPLIER.get()) - 1;
	}

	public static float getDamageBonusPerWeaponEnchantmentLevel(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL_MULTIPLIER.get()) - 1;
	}

	public static float getDamageBonusUnderPotionEffect(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_UNDER_POTION_EFFECT_MULTIPLIER.get()) - 1;
	}

	public static float getDamageBonusAgainsPoisoned(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_AGAINST_POISONED_MULTIPLIER.get()) - 1;
	}

	public static float getDamageBonusWithShield(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_WITH_SHIELD_MULTIPLIER.get()) - 1;
	}

	public static float getDamageBonusWithPickaxe(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.PICKAXE_DAMAGE_MULTIPLIER.get()) - 1;
	}

	public static float getDamageBonusIfAteRecently(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_MULTIPLIER_IF_ATE_RECENTLY.get()) - 1;
	}

	public static float getCritChanceAgainstPoisoned(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_AGAINST_POISONED_MULTIPLIER.get()) - 1;
	}

	public static float getCritChancePerEnchantment(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_PER_ENCHANTMENT_MULTIPLIER.get()) - 1;
	}

	public static float getCritChanceWithShield(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_WITH_SHIELD_MULTIPLIER.get()) - 1;
	}

	public static float getCritChanceWithAxe(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.AXE_CRIT_CHANCE_MULTIPLIER.get()) - 1;
	}

	public static float getCritDamagePerEnchantment(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_PER_ENCHANTMENT_MULTIPLIER.get()) - 1;
	}

	public static float getCritDamageWithShield(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_WITH_SHIELD_MULTIPLIER.get()) - 1;
	}

	public static float getAttackSpeedBonusWithPickaxe(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.PICKAXE_ATTACK_SPEED_MULTIPLIER.get()) - 1;
	}

	public static float getAttackSpeedBonusWithAxe(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.AXE_ATTACK_SPEED_MULTIPLIER.get()) - 1;
	}

	public static boolean canEvadeDamage(DamageSource source) {
		return source instanceof EntityDamageSource entityDamageSource && entityDamageSource.getDirectEntity() instanceof LivingEntity;
	}
}
