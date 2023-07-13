package daripher.skilltree.util;

import java.lang.reflect.InvocationTargetException;

import daripher.skilltree.gem.GemHelper;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeItems;
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
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class PlayerHelper {
	public static float getDamageMultiplier(Player player, LivingEntity target, boolean melee) {
		var multiplier = 1F;
		if (target.hasEffect(MobEffects.POISON)) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_AGAINST_POISONED.get()) - 1;
		}
		if (ItemHelper.isShield(player.getOffhandItem())) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_WITH_SHIELD.get()) - 1;
			if (melee) multiplier += (float) player.getAttributeValue(SkillTreeAttributes.MELEE_DAMAGE_WITH_SHIELD.get()) - 1;
			else multiplier += (float) player.getAttributeValue(SkillTreeAttributes.PROJECTILE_DAMAGE_WITH_SHIELD.get()) - 1;
		}
		if (ItemHelper.isWeapon(player.getMainHandItem()) && player.getMainHandItem().isEnchanted()) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_WITH_ENCHANTED_WEAPON.get()) - 1;
		}
		if (ItemHelper.isBow(player.getMainHandItem())) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.BOW_DAMAGE.get()) - 1;
			if (player.getMainHandItem().isEnchanted()) {
				multiplier += (float) player.getAttributeValue(SkillTreeAttributes.ENCHANTED_BOW_DAMAGE.get()) - 1;
			}
		}
		if (ItemHelper.isCrossbow(player.getMainHandItem())) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.CROSSBOW_DAMAGE.get()) - 1;
			if (player.getMainHandItem().isEnchanted()) {
				multiplier += (float) player.getAttributeValue(SkillTreeAttributes.ENCHANTED_CROSSBOW_DAMAGE.get()) - 1;
			}
		}
		if (ItemHelper.isAxe(player.getMainHandItem())) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.AXE_DAMAGE.get()) - 1;
			if (player.getMainHandItem().isEnchanted()) {
				multiplier += (float) player.getAttributeValue(SkillTreeAttributes.ENCHANTED_AXE_DAMAGE.get()) - 1;
			}
		}
		if (ItemHelper.isSword(player.getMainHandItem())) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.SWORD_DAMAGE.get()) - 1;
			if (player.getMainHandItem().isEnchanted()) {
				multiplier += (float) player.getAttributeValue(SkillTreeAttributes.ENCHANTED_SWORD_DAMAGE.get()) - 1;
			}
		}
		if (ItemHelper.isTrident(player.getMainHandItem()) && melee) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.TRIDENT_DAMAGE.get()) - 1;
			if (player.getMainHandItem().isEnchanted()) {
				multiplier += (float) player.getAttributeValue(SkillTreeAttributes.ENCHANTED_TRIDENT_DAMAGE.get()) - 1;
			}
		}
		if (ItemHelper.hasPoisons(player.getMainHandItem())) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_WITH_POISONED_WEAPON.get()) - 1;
		}
		if (GemHelper.hasGem(player.getMainHandItem(), 0)) {
			var gems = GemHelper.getGemsCount(player.getMainHandItem());
			var damagePerGem = player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_GEM_IN_WEAPON.get()) - 1;
			multiplier += damagePerGem * gems;
			multiplier += player.getAttributeValue(SkillTreeAttributes.DAMAGE_WITH_GEM_IN_WEAPON.get()) - 1;
			var adamites = GemHelper.getGemsCount(player.getMainHandItem(), SkillTreeItems.ADAMITE.get());
			var damagePerAdamite = player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_ADAMITE_IN_WEAPON.get()) - 1;
			multiplier += damagePerAdamite * adamites;
			var iriscites = GemHelper.getGemsCount(player.getMainHandItem(), SkillTreeItems.IRISCITE.get());
			var damagePerIriscite = player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_IRISCITE_IN_WEAPON.get()) - 1;
			multiplier += damagePerIriscite * iriscites;
			if (GemHelper.hasOnlyDifferentGems(player.getMainHandItem())) {
				multiplier += player.getAttributeValue(SkillTreeAttributes.DAMAGE_WITH_DIFFERENT_WEAPON_GEMS.get()) - 1;
			}
			if (GemHelper.hasOnlySameGems(player.getMainHandItem())) {
				multiplier += player.getAttributeValue(SkillTreeAttributes.DAMAGE_WITH_SAME_WEAPON_GEMS.get()) - 1;
			}
			if (melee) {
				var meleeDamagePerGem = player.getAttributeValue(SkillTreeAttributes.MELEE_DAMAGE_PER_GEM_IN_WEAPON.get()) - 1;
				multiplier += meleeDamagePerGem * gems;
			} else {
				var projectileDamagePerGem = player.getAttributeValue(SkillTreeAttributes.PROJECTILE_DAMAGE_PER_GEM_IN_WEAPON.get()) - 1;
				multiplier += projectileDamagePerGem * gems;
			}
		}
		if (player.getFoodData().getFoodLevel() >= 10) {
			multiplier += player.getAttributeValue(SkillTreeAttributes.DAMAGE_IF_NOT_HUNGRY.get()) - 1;
			if (melee) multiplier += player.getAttributeValue(SkillTreeAttributes.MELEE_DAMAGE_IF_NOT_HUNGRY.get()) - 1;
			else multiplier += player.getAttributeValue(SkillTreeAttributes.PROJECTILE_DAMAGE_IF_NOT_HUNGRY.get()) - 1;
		}
		if (!player.getActiveEffects().isEmpty()) {
			var damagePerEffect = player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_POTION_EFFECT.get()) - 1;
			multiplier += player.getActiveEffects().size() * damagePerEffect;
			multiplier += player.getAttributeValue(SkillTreeAttributes.DAMAGE_UNDER_POTION_EFFECT.get()) - 1;
		}
		for (var slot : EquipmentSlot.values()) {
			var itemInSlot = player.getItemBySlot(slot);
			if (!itemInSlot.isEnchanted()) {
				continue;
			}
			var damagePerEnchantment = (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_ENCHANTMENT.get()) - 1;
			var enchantments = itemInSlot.getAllEnchantments().size();
			multiplier += damagePerEnchantment * enchantments;
		}
		for (var enchantmentLevel : player.getMainHandItem().getAllEnchantments().values()) {
			var damagePerEnchantmentLevel = (float) player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL.get()) - 1;
			multiplier += damagePerEnchantmentLevel * enchantmentLevel;
		}
		var foodLevel = player.getFoodData().getFoodLevel();
		if (foodLevel > 0) {
			var damagePerSatisfiedHunger = player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_SATISFIED_HUNGER.get()) - 1;
			multiplier += foodLevel * damagePerSatisfiedHunger;
		}
		var damagePerArrowInEnemy = player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_ARROW_IN_ENEMY.get()) - 1;
		multiplier += damagePerArrowInEnemy * target.getArrowCount();
		var damageBonusPerDistance = player.getAttributeValue(SkillTreeAttributes.DAMAGE_PER_DISTANCE.get()) - 1;
		multiplier += damageBonusPerDistance * player.distanceTo(target);
		return multiplier;
	}

	public static float getProjectileDamageMultiplier(Player player, LivingEntity target, Projectile projectile) {
		var critChance = (float) (player.getAttributeValue(SkillTreeAttributes.PROJECTILE_CRIT_CHANCE.get()) - 1);
		critChance += getCritChance(player, target);
		var multiplier = (float) player.getAttributeValue(SkillTreeAttributes.PROJECTILE_DAMAGE.get());
		if (target.hasEffect(MobEffects.POISON)) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.PROJECTILE_DAMAGE_AGAINST_POISONED.get()) - 1;
		}
		if (projectile instanceof ThrownPotion) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.SPLASH_POTION_DAMAGE.get()) - 1;
		}
		if (projectile instanceof ThrownTrident) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.TRIDENT_DAMAGE.get()) - 1;
		}
		if (isArrow(projectile)) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.ARROW_DAMAGE.get()) - 1;
		}
		if (isTippedArrow(projectile)) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.TIPPED_ARROW_DAMAGE.get()) - 1;
		}
		var criticalHit = player.getRandom().nextFloat() < critChance;
		if (criticalHit) {
			multiplier += getCritDamageMultiplier(player, target);
			multiplier += player.getAttributeValue(SkillTreeAttributes.PROJECTILE_CRIT_DAMAGE.get()) - 1;
		}
		multiplier += getDamageMultiplier(player, target, false) - 1;
		return multiplier;
	}

	private static boolean isTippedArrow(Projectile projectile) {
		if (!(projectile instanceof Arrow)) return false;
		var arrowStack = ItemStack.EMPTY;
		try {
			var getPickupItemMethod = ObfuscationReflectionHelper.findMethod(AbstractArrow.class, "m_7941_");
			arrowStack = (ItemStack) getPickupItemMethod.invoke(projectile);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return arrowStack.getItem() == Items.TIPPED_ARROW;
	}

	protected static boolean isArrow(Projectile projectile) {
		if (!(projectile instanceof Arrow)) return false;
		var arrowStack = ItemStack.EMPTY;
		try {
			var getPickupItemMethod = ObfuscationReflectionHelper.findMethod(AbstractArrow.class, "m_7941_");
			arrowStack = (ItemStack) getPickupItemMethod.invoke(projectile);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return arrowStack.getItem() instanceof ArrowItem;
	}

	public static float getCritChance(Player player, LivingEntity target) {
		var chance = (float) (player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE.get()) - 1);
		if (target.hasEffect(MobEffects.POISON)) {
			chance += (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_AGAINST_POISONED.get()) - 1;
		}
		if (ItemHelper.isShield(player.getOffhandItem())) {
			chance += (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_WITH_SHIELD.get()) - 1;
		}
		for (var slot : EquipmentSlot.values()) {
			var itemInSlot = player.getItemBySlot(slot);
			if (!itemInSlot.isEnchanted()) {
				continue;
			}
			var critChancePerEnchantment = (float) player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_PER_ENCHANTMENT.get()) - 1;
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
			var critDamagePerWeaponEnchantment = (float) player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_PER_WEAPON_ENCHANTMENT.get()) - 1;
			var enchantments = mainhandItem.getAllEnchantments().size();
			multiplier += critDamagePerWeaponEnchantment * enchantments;
		}
		if (ItemHelper.isShield(player.getOffhandItem())) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_WITH_SHIELD.get()) - 1;
		}
		var foodLevel = player.getFoodData().getFoodLevel();
		if (foodLevel > 0) {
			var critDamagePerSatisfiedHunger = player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_PER_SATISFIED_HUNGER.get()) - 1;
			multiplier += foodLevel * critDamagePerSatisfiedHunger;
		}
		if (target.hasEffect(MobEffects.POISON)) {
			multiplier += (float) player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_AGAINST_POISONED.get()) - 1;
		}
		return multiplier;
	}

	public static boolean canEvadeDamage(DamageSource source) {
		if (!(source instanceof EntityDamageSource damageSource)) return false;
		return damageSource.getDirectEntity() instanceof LivingEntity || damageSource.getEntity() instanceof LivingEntity;
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
