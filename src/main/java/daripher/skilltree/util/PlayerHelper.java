package daripher.skilltree.util;

import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeEffects;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ShieldItem;

public class PlayerHelper {
	public static float getDamageMultiplier(Player player, LivingEntity target) {
		var multiplier = 1F;
		var doubleDamageChance = getDoubleDamageChance(player);

		if (doubleDamageChance > 0) {
			if (player.getRandom().nextFloat() < doubleDamageChance) {
				multiplier += 1;
			}
		}

		if (target.hasEffect(MobEffects.POISON)) {
			multiplier += PlayerHelper.getDamageMultiplierAgainsPoisoned(player);
		}

		if (!player.getActiveEffects().isEmpty()) {
			multiplier += PlayerHelper.getDamageMultiplierUnderPotionEffect(player);
		}

		if (ItemHelper.isShield(player.getOffhandItem())) {
			multiplier += PlayerHelper.getDamageMultiplierWithShield(player);
		}

		if (ItemHelper.isPickaxe(player.getMainHandItem())) {
			multiplier += PlayerHelper.getDamageMultiplierWithPickaxe(player);
		}

		for (var slot : EquipmentSlot.values()) {
			if (player.getItemBySlot(slot).isEnchanted()) {
				multiplier += PlayerHelper.getDamageMultiplierPerEnchantedItem(player);
			}
		}

		if (!player.getFoodData().needsFood()) {
			multiplier += PlayerHelper.getDamageMultiplierWhenFull(player);
		}

		return multiplier;
	}

	public static float getArrowDamageMultiplier(Player player, LivingEntity target, AbstractArrow arrow) {
		var damageMultiplier = player.getAttributeValue(SkillTreeAttributes.ARROW_DAMAGE_MULTIPLIER.get());
		var doubleDamageChance = player.getAttributeValue(SkillTreeAttributes.ARROW_DOUBLE_DAMAGE_CHANCE_MULTIPLIER.get()) - 1;
		doubleDamageChance += getDoubleDamageChance(player);

		if (player.getRandom().nextFloat() < doubleDamageChance) {
			damageMultiplier += 1;
		}

		if (!arrow.isCritArrow()) {
			var critChance = getCritChance(player, target);

			if (player.getRandom().nextFloat() < critChance) {
				arrow.setCritArrow(true);
			}
		}

		if (arrow.isCritArrow()) {
			var arrowCritDamageBonus = player.getAttributeValue(SkillTreeAttributes.ARROW_CRIT_DAMAGE_BONUS.get());
			damageMultiplier += arrowCritDamageBonus;
			damageMultiplier += getCritDamageMultiplier(player, target) - 1.5F;
		}

		var damageBonusPerDistance = player.getAttributeValue(SkillTreeAttributes.ARROW_DAMAGE_MULTIPLIER_PER_DISTANCE.get()) - 1;
		damageMultiplier += damageBonusPerDistance * player.distanceTo(target);
		damageMultiplier += getDamageMultiplier(player, target) - 1;
		return (float) damageMultiplier;
	}

	public static float getCritChance(Player player, LivingEntity target) {
		var chance = getBaseCritChance(player);

		if (target.hasEffect(MobEffects.POISON)) {
			chance += PlayerHelper.getCritChanceAgainsPoisoned(player);
		}

		for (var slot : EquipmentSlot.values()) {
			if (player.getItemBySlot(slot).isEnchanted()) {
				chance += PlayerHelper.getCritChancePerEnchantedItem(player);
			}
		}

		var playerArmor = player.getAttributeValue(Attributes.ARMOR);

		if (playerArmor > 0) {
			var critChancePerArmorPoint = getCritChancePerArmorPoint(player);
			chance += playerArmor * critChancePerArmorPoint;
		}

		if (!player.getFoodData().needsFood()) {
			chance += getCritChanceWhenFull(player);
		}

		return chance;
	}

	public static float getCritDamageMultiplier(Player player, LivingEntity target) {
		var multiplier = 1.5F;

		for (var slot : EquipmentSlot.values()) {
			if (player.getItemBySlot(slot).isEnchanted()) {
				multiplier += getCritDamagePerEnchantedItem(player);
			}
		}

		if (player.hasEffect(SkillTreeEffects.GOURMET.get())) {
			multiplier += getCritDamageIfAteRecently(player);
		}

		return multiplier;
	}

	public static float getDoubleDamageChance(Player player) {
		var chance = 0F;

		if (player.getOffhandItem().getItem() instanceof ShieldItem) {
			chance += getDoubleDamageChanceWithShield(player);
		}

		return chance;
	}

	public static float getAttackSpeedBonus(Player player) {
		var bonus = 0F;

		if (ItemHelper.isPickaxe(player.getMainHandItem())) {
			bonus += getAttackSpeedBonusWithPickaxe(player);
		}

		return bonus;
	}

	public static float getDoubleDamageChanceWithShield(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DOUBLE_DAMAGE_CHANCE_WITH_SHIELD.get());
	}

	public static float getDamageMultiplierPerEnchantedItem(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_BONUS_PER_ENCHANTED_ITEM.get());
	}

	public static float getDamageMultiplierUnderPotionEffect(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_BONUS_UNDER_POTION_EFFECT.get());
	}

	public static float getDamageMultiplierAgainsPoisoned(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_BONUS_AGAINST_POISONED.get());
	}

	public static float getDamageMultiplierWithShield(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_BONUS_WITH_SHIELD.get());
	}

	public static float getDamageMultiplierWithPickaxe(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_BONUS_WITH_PICKAXE.get());
	}

	public static float getDamageMultiplierWhenFull(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_BONUS_WHEN_FULL.get());
	}

	public static float getBaseCritChance(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_BONUS.get());
	}

	public static float getCritChanceAgainsPoisoned(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_BONUS_AGAINST_POISONED.get());
	}

	public static float getCritChancePerEnchantedItem(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_BONUS_PER_ENCHANTED_ITEM.get());
	}

	public static float getCritChancePerArmorPoint(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_BONUS_PER_ARMOR_POINT.get());
	}

	public static float getCritChanceWhenFull(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_BONUS_WHEN_FULL.get());
	}

	public static float getCritDamagePerEnchantedItem(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_BONUS_PER_ENCHANTED_ITEM.get());
	}

	public static float getCritDamageIfAteRecently(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_BONUS_IF_ATE_RECENTLY.get());
	}

	public static float getAttackSpeedBonusWithPickaxe(Player player) {
		return (float) player.getAttributeValue(SkillTreeAttributes.ATTACK_SPEED_BONUS_WITH_PICKAXE.get());
	}
}
