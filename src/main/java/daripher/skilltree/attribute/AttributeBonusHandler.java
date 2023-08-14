package daripher.skilltree.attribute;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.google.common.util.concurrent.AtomicDouble;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.api.EquipmentContainer;
import daripher.skilltree.api.HasAdditionalSockets;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.init.PSTEffects;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.GemHelper;
import daripher.skilltree.item.quiver.QuiverItem;
import daripher.skilltree.util.FoodHelper;
import daripher.skilltree.util.PlayerHelper;
import daripher.skilltree.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class AttributeBonusHandler {
	@SubscribeEvent
	public static void applyDamageBonus(LivingHurtEvent event) {
		if (!(event.getSource().getDirectEntity() instanceof ServerPlayer player)) return;
		float damage = event.getAmount();
		float multiplier = PlayerHelper.getDamageMultiplier(player, event.getEntity(), true);
		event.setAmount(damage * multiplier);
	}

	@SubscribeEvent
	public static void applyProjectileDamageBonus(LivingHurtEvent event) {
		if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
		if (!(event.getSource().getDirectEntity() instanceof Projectile projectile)) return;
		float damage = event.getAmount();
		float multiplier = PlayerHelper.getProjectileDamageMultiplier(player, event.getEntity(), projectile, event.getSource());
		event.setAmount(damage * multiplier);
	}

	@SubscribeEvent
	public static void applyCritBonus(CriticalHitEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;
		if (!(event.getTarget() instanceof LivingEntity target)) return;
		float critChance = PlayerHelper.getCritChance(player, target);
		if (!event.isVanillaCritical() && event.getEntity().getRandom().nextFloat() >= critChance) return;
		float critDamage = PlayerHelper.getCritDamage(player, target, true);
		event.setDamageModifier(event.getDamageModifier() + critDamage);
		if (!event.isVanillaCritical()) {
			event.setDamageModifier(event.getDamageModifier() + 0.5F);
			event.setResult(Result.ALLOW);
		}
	}

	@SubscribeEvent
	public static void applyDynamicAttributeBonuses(PlayerTickEvent event) {
		if (event.player.level.isClientSide) return;
		ServerPlayer player = (ServerPlayer) event.player;
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "d1f7e78b-3368-409c-aa89-90f0f89a5524",
				AttributeBonusHandler::getMaximumLifePerEvasion);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "b68181bd-fbc4-4a63-95d4-df386fe3f71f",
				AttributeBonusHandler::getMaximumLifePerGemInArmor);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "7cb71ee5-8715-40ae-a877-72ec3b49b33e",
				AttributeBonusHandler::getArmorPerEvasion);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "66eae15c-53eb-4a4a-b511-2ab94f81324b",
				AttributeBonusHandler::getArmorPerGemInChestplate);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "1080308c-bdd4-4693-876c-a36390b66b73",
				AttributeBonusHandler::getArmorPerGemInHelmet);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_DAMAGE, Operation.ADDITION, "d1079882-dd8c-42b7-9a43-3928553193c8",
				AttributeBonusHandler::getDamagePerArmor);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "9199d7cf-c7e4-4123-b636-6f6591e1137d",
				AttributeBonusHandler::getMaximumLifePerArmor);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "8810227f-9798-4890-8400-91c0941a3fc0",
				AttributeBonusHandler::getMaximumLifePerBootsArmor);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "98a17cd0-68c8-4808-8981-1796c33295e7",
				AttributeBonusHandler::getMaximumLifePerSatisfiedHunger);
		applyDynamicAttributeBonus(player, PSTAttributes.EVASION.get(), Operation.MULTIPLY_BASE, "4aa87d74-b729-4e1d-9c76-893495050416",
				AttributeBonusHandler::getEvasionUnderPotionEffect);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_TOTAL, "a4daf7f8-29e3-404d-8277-9215a16ef4c8",
				AttributeBonusHandler::getAttackSpeedUnderPotionEffect);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "de712f9d-9f47-475c-8b86-188bca70d1df",
				AttributeBonusHandler::getMaximumLifeUnderPotionEffect);
		applyDynamicAttributeBonus(player, PSTAttributes.EVASION.get(), Operation.MULTIPLY_BASE, "282c4f81-7b6d-48e0-82c9-c4ebd58265cb",
				AttributeBonusHandler::getEvasionPerPotionEffect);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "16c35c5c-56da-4d21-ad56-bd6618fee711",
				AttributeBonusHandler::getMaximumLifeWithEnchantedItem);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "8b836bea-4c28-4430-8184-7330530239f6",
				AttributeBonusHandler::getArmorWithEnchantedShield);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "27b4644b-96a0-4443-89e5-1700af61d602",
				AttributeBonusHandler::getMaximumLifePerEnchantment);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "55c3cb58-c09e-465a-a812-6a18ae587ec0",
				AttributeBonusHandler::getArmorPerChestplateEnchantment);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "9b1e9aac-fa58-4343-ba88-7541eca2836f",
				AttributeBonusHandler::getMaximumLifePerArmorEnchantment);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_TOTAL, "5e2d6a95-bc70-4f3d-a348-307b49f5bc84",
				AttributeBonusHandler::getAttackSpeedWithGemInWeapon);
		applyDynamicAttributeBonus(player, PSTAttributes.BLOCK_CHANCE.get(), Operation.MULTIPLY_BASE, "bfbc3d6b-7c37-498b-888c-3b05c921f24a",
				AttributeBonusHandler::getBlockChanceWithEnchantedShield);
		applyDynamicAttributeBonus(player, PSTAttributes.EVASION.get(), Operation.MULTIPLY_BASE, "d2865c2c-d5cc-4de9-a793-752349d27da0",
				AttributeBonusHandler::getEvasionChanceWhenWounded);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_BASE, "f6dbc327-88c0-4704-b230-91fe1642dc7a",
				AttributeBonusHandler::getAttackSpeedIfNotHungry);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_BASE, "5d449ea8-12dd-4596-a6e1-e4837946acb6",
				AttributeBonusHandler::getAttackSpeedWithRangedWeapon);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_BASE, "e37a2257-8511-4ffb-a5dd-913b591dd520",
				AttributeBonusHandler::getAttackSpeedPerGemInWeapon);
		applyDynamicAttributeBonus(player, PSTAttributes.CRIT_CHANCE.get(), Operation.MULTIPLY_BASE, "44984187-74c8-4927-be18-1e187ca9babe",
				AttributeBonusHandler::getCritChanceIfNotHungry);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_BASE, "7bd1d9fb-4a20-41f3-89df-7cb42e849c5f",
				AttributeBonusHandler::getAttackWithEnchantedWeapon);
		applyDynamicAttributeBonus(player, PSTAttributes.LIFE_PER_HIT.get(), Operation.ADDITION, "9c36d4dc-06e3-4f42-b8e6-abb0fb6b344c",
				AttributeBonusHandler::getLifePerHitUnderPotionEffect);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "77353761-61e2-4f3c-b0e4-2abef4b75d76",
				AttributeBonusHandler::getMaximumLifePerGemInHelmet);
		applyDynamicAttributeBonus(player, PSTAttributes.CRIT_CHANCE.get(), Operation.MULTIPLY_BASE, "636b118d-478b-4c4e-9785-b6e7da876828",
				AttributeBonusHandler::getCritChancePerGemInWeapon);
		applyDynamicAttributeBonus(player, PSTAttributes.CRIT_DAMAGE.get(), Operation.MULTIPLY_BASE, "3051c828-7281-458c-b6fc-df9d93b31d30",
				AttributeBonusHandler::getCritDamagePerGemInWeapon);
		applyDynamicAttributeBonus(player, PSTAttributes.LIFE_REGENERATION.get(), Operation.ADDITION, "6732aed2-1948-4e86-a83c-aad617cd4387",
				AttributeBonusHandler::getLifeRegenerationPerGemInHelmet);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_BASE, "b983bec3-a049-49d7-855e-3025b283c7d2",
				AttributeBonusHandler::getAttackSpeedWithShield);
		applyDynamicAttributeBonus(player, PSTAttributes.LIFE_REGENERATION.get(), Operation.ADDITION, "d86d8efb-4539-46f3-b157-672b2e1241d6",
				AttributeBonusHandler::getLifeRegenerationWithShield);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "f11460ca-56f9-4cff-98ea-791ed27f6639",
				AttributeBonusHandler::getBonusChestplateArmor);
		applyDynamicAttributeBonus(player, PSTAttributes.LIFE_ON_BLOCK.get(), Operation.ADDITION, "6dccce60-76e9-4ca0-8497-8352ba26620d",
				AttributeBonusHandler::getLifeOnBlockPerShieldEnchantment);
		applyDynamicAttributeBonus(player, PSTAttributes.BLOCK_CHANCE.get(), Operation.MULTIPLY_BASE, "7ea18323-13e4-43ee-bb30-decfdc1b1299",
				AttributeBonusHandler::getBlockChancePerShieldEnchantment);
		applyDynamicAttributeBonus(player, PSTAttributes.BLOCK_CHANCE.get(), Operation.MULTIPLY_BASE, "629ecea2-51b4-415a-80ff-4137a0f0dce1",
				AttributeBonusHandler::getBlockChanceIfNotHungry);
		applyDynamicAttributeBonus(player, PSTAttributes.BLOCK_CHANCE.get(), Operation.MULTIPLY_BASE, "fb0934db-cdbb-4fa6-bf4f-d3c0c12f50be",
				AttributeBonusHandler::getBlockChancePerSatisfiedHunger);
		applyDynamicAttributeBonus(player, PSTAttributes.LIFE_ON_BLOCK.get(), Operation.ADDITION, "793d56a0-7d0c-4bec-a328-65d6d681ec44",
				AttributeBonusHandler::getLifeOnBlockIfNotHungry);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "e529b2e9-2170-430c-b77d-cfe829f66c69",
				AttributeBonusHandler::getMaximumLifeIfNotHungry);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "c5fae151-36ee-4f93-a917-500b524587ec",
				AttributeBonusHandler::getMaximumLifePerEquippedJewelry);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_DAMAGE, Operation.MULTIPLY_BASE, "7a6b2991-006d-4858-8369-185169fc72a4",
				AttributeBonusHandler::getAttackSpeedIfWounded);
		applyDynamicAttributeBonus(player, PSTAttributes.CRIT_CHANCE.get(), Operation.MULTIPLY_BASE, "f752700c-d928-40c2-8f53-870e89669fc4",
				AttributeBonusHandler::getCritChanceIfWounded);
		applyDynamicAttributeBonus(player, PSTAttributes.LIFE_PER_HIT.get(), Operation.ADDITION, "40b4ee8a-0547-47dd-838b-022f9fd67428",
				AttributeBonusHandler::getLifePerHitIfWounded);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "b9d32a75-969c-4261-8d3e-91bc87fcffe0",
				AttributeBonusHandler::getMaximumLifePerArrowInQuiver);
		applyDynamicAttributeBonus(player, Attributes.LUCK, Operation.ADDITION, "f1d6e303-1682-4e13-9548-cde588b4e306",
				AttributeBonusHandler::getLuckWhileFishing);
	}

	private static void applyDynamicAttributeBonus(ServerPlayer player, Attribute attribute, Operation operation, String id,
			Function<ServerPlayer, Double> function) {
		double bonus = function.apply(player);
		UUID modifierId = UUID.fromString(id);
		AttributeInstance playerAttribute = player.getAttribute(attribute);
		AttributeModifier oldModifier = playerAttribute.getModifier(modifierId);
		if (oldModifier != null) {
			if (oldModifier.getAmount() == bonus) return;
			playerAttribute.removeModifier(modifierId);
		}
		if (attribute == Attributes.MAX_HEALTH) {
			playerAttribute.addPermanentModifier(new AttributeModifier(modifierId, "Skill Tree Bonus", bonus, operation));
			player.setHealth(player.getHealth());
		} else {
			playerAttribute.addTransientModifier(new AttributeModifier(modifierId, "Skill Tree Bonus", bonus, operation));
		}
	}

	private static double getMaximumLifePerEvasion(ServerPlayer player) {
		var lifePerEvasion = player.getAttributeValue(PSTAttributes.MAXIMUM_LIFE_PER_EVASION.get());
		var evasion = player.getAttributeValue(PSTAttributes.EVASION.get()) - 1;
		return evasion * lifePerEvasion * 100;
	}

	private static double getMaximumLifePerGemInArmor(ServerPlayer player) {
		var lifePerGemInArmor = player.getAttributeValue(PSTAttributes.MAXIMUM_LIFE_PER_GEM_IN_ARMOR.get());
		var gemstonesInArmor = 0;
		for (var slot = 0; slot < 4; slot++) {
			var itemInSlot = player.getItemBySlot(EquipmentSlot.byTypeAndIndex(Type.ARMOR, slot));
			gemstonesInArmor += GemHelper.getGemsCount(itemInSlot);
		}
		return lifePerGemInArmor * gemstonesInArmor;
	}

	private static double getMaximumLifePerGemInHelmet(ServerPlayer player) {
		var lifePerGemInHelmet = player.getAttributeValue(PSTAttributes.MAXIMUM_LIFE_PER_GEM_IN_HELMET.get());
		var helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		var gemstonesInHelmet = GemHelper.getGemsCount(helmet);
		return lifePerGemInHelmet * gemstonesInHelmet;
	}

	private static double getArmorPerEvasion(ServerPlayer player) {
		var armorPerEvasion = player.getAttributeValue(PSTAttributes.ARMOR_PER_EVASION.get());
		var evasion = player.getAttributeValue(PSTAttributes.EVASION.get()) - 1;
		return evasion * armorPerEvasion * 100;
	}

	private static double getArmorPerGemInChestplate(ServerPlayer player) {
		var armorPerGemInChestplate = player.getAttributeValue(PSTAttributes.ARMOR_PER_GEM_IN_CHESTPLATE.get());
		var getmstonesInChestplate = GemHelper.getGemsCount(player.getItemBySlot(EquipmentSlot.CHEST));
		return armorPerGemInChestplate * getmstonesInChestplate;
	}

	private static double getArmorPerGemInHelmet(ServerPlayer player) {
		var armorPerGemInHelmet = player.getAttributeValue(PSTAttributes.ARMOR_PER_GEM_IN_HELMET.get());
		var getmstonesInHelmet = GemHelper.getGemsCount(player.getItemBySlot(EquipmentSlot.HEAD));
		return armorPerGemInHelmet * getmstonesInHelmet;
	}

	private static double getDamagePerArmor(ServerPlayer player) {
		var damagePerArmor = player.getAttributeValue(PSTAttributes.ATTACK_DAMAGE_PER_ARMOR.get());
		var armor = player.getAttributeValue(Attributes.ARMOR);
		return damagePerArmor * armor;
	}

	private static double getMaximumLifePerArmor(ServerPlayer player) {
		var maximumLifePerArmor = player.getAttributeValue(PSTAttributes.MAXIMUM_LIFE_PER_ARMOR.get());
		var armor = player.getAttributeValue(Attributes.ARMOR);
		return maximumLifePerArmor * armor;
	}

	private static double getMaximumLifePerBootsArmor(ServerPlayer player) {
		var maximumLifePerBootsArmor = player.getAttributeValue(PSTAttributes.MAXIMUM_LIFE_PER_BOOTS_ARMOR.get());
		var boots = player.getItemBySlot(EquipmentSlot.FEET);
		var bootsArmor = new AtomicDouble();
		boots.getAttributeModifiers(EquipmentSlot.FEET).forEach((attribute, modifier) -> {
			if (attribute == Attributes.ARMOR && modifier.getOperation() == Operation.ADDITION) {
				bootsArmor.addAndGet(modifier.getAmount());
			}
		});
		if (bootsArmor.get() == 0) return 0;
		return bootsArmor.doubleValue() * maximumLifePerBootsArmor;
	}

	private static double getMaximumLifePerSatisfiedHunger(ServerPlayer player) {
		var maximumLifePerSatisfiedHunger = player.getAttributeValue(PSTAttributes.MAXIMUM_LIFE_PER_SATISFIED_HUNGER.get());
		var satisfiedHunger = player.getFoodData().getFoodLevel();
		return satisfiedHunger * maximumLifePerSatisfiedHunger;
	}

	private static double getEvasionUnderPotionEffect(ServerPlayer player) {
		var evasionUnderPotionEffect = player.getAttributeValue(PSTAttributes.EVASION_UNDER_POTION_EFFECT.get()) - 1;
		var hasPotionEffect = !player.getActiveEffects().isEmpty();
		return hasPotionEffect ? evasionUnderPotionEffect : 0;
	}

	private static double getAttackSpeedUnderPotionEffect(ServerPlayer player) {
		var attackSpeedUnderPotionEffect = player.getAttributeValue(PSTAttributes.ATTACK_SPEED_UNDER_POTION_EFFECT.get()) - 1;
		var hasPotionEffect = !player.getActiveEffects().isEmpty();
		return hasPotionEffect ? attackSpeedUnderPotionEffect : 0;
	}

	private static double getMaximumLifeUnderPotionEffect(ServerPlayer player) {
		var maximumLifeUnderPotionEffect = player.getAttributeValue(PSTAttributes.MAXIMUM_LIFE_UNDER_POTION_EFFECT.get());
		var hasPotionEffect = !player.getActiveEffects().isEmpty();
		return hasPotionEffect ? maximumLifeUnderPotionEffect : 0;
	}

	private static double getEvasionPerPotionEffect(ServerPlayer player) {
		var evasionPerPotionEffect = player.getAttributeValue(PSTAttributes.EVASION_PER_POTION_EFFECT.get()) - 1;
		var potionEffectCount = player.getActiveEffects().size();
		return potionEffectCount * evasionPerPotionEffect;
	}

	private static double getMaximumLifeWithEnchantedItem(ServerPlayer player) {
		var maximumLifeWithEnchantedItem = player.getAttributeValue(PSTAttributes.MAXIMUM_LIFE_WITH_ENCHANTED_ITEM.get());
		for (var slot : EquipmentSlot.values()) {
			var isItemEnchanted = player.getItemBySlot(slot).isEnchanted();
			if (isItemEnchanted) return maximumLifeWithEnchantedItem;
		}
		return 0D;
	}

	private static double getArmorWithEnchantedShield(ServerPlayer player) {
		var offhandItem = player.getOffhandItem();
		if (!ItemHelper.isShield(offhandItem) || !offhandItem.isEnchanted()) return 0D;
		var armorWithEnchantedShield = player.getAttributeValue(PSTAttributes.ARMOR_WITH_ENCHANTED_SHIELD.get());
		return armorWithEnchantedShield;
	}

	private static double getMaximumLifePerEnchantment(ServerPlayer player) {
		var maximumLifePerEnchantment = player.getAttributeValue(PSTAttributes.MAXIMUM_LIFE_PER_ENCHANTMENT.get());
		var enchantmentCount = 0;
		for (var slot : EquipmentSlot.values()) {
			var itemInSlot = player.getItemBySlot(slot);
			if (!itemInSlot.isEnchanted()) continue;
			enchantmentCount += itemInSlot.getAllEnchantments().size();
		}
		return maximumLifePerEnchantment * enchantmentCount;
	}

	private static double getArmorPerChestplateEnchantment(ServerPlayer player) {
		var armorPerChestplateEnchantment = player.getAttributeValue(PSTAttributes.ARMOR_PER_CHESTPLATE_ENCHANTMENT.get());
		var chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		var enchantmentCount = chestplate.getAllEnchantments().size();
		return armorPerChestplateEnchantment * enchantmentCount;
	}

	private static double getMaximumLifePerArmorEnchantment(ServerPlayer player) {
		var maximumLifePerArmorEnchantment = player.getAttributeValue(PSTAttributes.MAXIMUM_LIFE_PER_ARMOR_ENCHANTMENT.get());
		var enchantmentCount = 0;
		for (var slot = 0; slot < 4; slot++) {
			var itemInSlot = player.getItemBySlot(EquipmentSlot.byTypeAndIndex(Type.ARMOR, slot));
			if (!itemInSlot.isEnchanted()) continue;
			enchantmentCount += itemInSlot.getAllEnchantments().size();
		}
		return maximumLifePerArmorEnchantment * enchantmentCount;
	}

	private static double getAttackSpeedWithGemInWeapon(ServerPlayer player) {
		if (!GemHelper.hasGem(player.getMainHandItem(), 0)) return 0;
		return player.getAttributeValue(PSTAttributes.ATTACK_SPEED_WITH_GEM_IN_WEAPON.get()) - 1;
	}

	private static double getBlockChanceWithEnchantedShield(ServerPlayer player) {
		var offhandItem = player.getOffhandItem();
		if (!ItemHelper.isShield(offhandItem) || !offhandItem.isEnchanted()) return 0D;
		return player.getAttributeValue(PSTAttributes.BLOCK_CHANCE_WITH_ENCHANTED_SHIELD.get()) - 1;
	}

	private static double getEvasionChanceWhenWounded(ServerPlayer player) {
		var isWounded = player.getHealth() < player.getMaxHealth() / 2;
		if (!isWounded) {
			return 0D;
		}
		var evasionChanceWhenWounded = player.getAttributeValue(PSTAttributes.EVASION_CHANCE_WHEN_WOUNDED.get()) - 1;
		return evasionChanceWhenWounded;
	}

	private static double getAttackSpeedIfNotHungry(ServerPlayer player) {
		var isHungry = player.getFoodData().getFoodLevel() < 10;
		if (isHungry) {
			return 0D;
		}
		var attackSpeedIfNotHungry = player.getAttributeValue(PSTAttributes.ATTACK_SPEED_IF_NOT_HUNGRY.get()) - 1;
		return attackSpeedIfNotHungry;
	}

	private static double getAttackSpeedWithRangedWeapon(ServerPlayer player) {
		var hasRangedWeapon = ItemHelper.isRangedWeapon(player.getMainHandItem());
		if (!hasRangedWeapon) return 0D;
		var attackSpeedWithBow = player.getAttributeValue(PSTAttributes.ATTACK_SPEED_WITH_RANGED_WEAPON.get()) - 1;
		return attackSpeedWithBow;
	}

	private static double getAttackSpeedPerGemInWeapon(ServerPlayer player) {
		var mainHandItem = player.getMainHandItem();
		if (!ItemHelper.isWeapon(mainHandItem)) return 0D;
		var gemstonesInWeapon = GemHelper.getGemsCount(mainHandItem);
		if (gemstonesInWeapon == 0) return 0D;
		var attackSpeedPerGemInWeapon = player.getAttributeValue(PSTAttributes.ATTACK_SPEED_PER_GEM_IN_WEAPON.get()) - 1;
		return attackSpeedPerGemInWeapon * gemstonesInWeapon;
	}

	private static double getCritChancePerGemInWeapon(ServerPlayer player) {
		var mainHandItem = player.getMainHandItem();
		if (!ItemHelper.isWeapon(mainHandItem)) {
			return 0D;
		}
		var gemstonesInWeapon = GemHelper.getGemsCount(mainHandItem);
		if (gemstonesInWeapon == 0) {
			return 0D;
		}
		var critChancePerGemInWeapon = player.getAttributeValue(PSTAttributes.CRIT_CHANCE_PER_GEM_IN_WEAPON.get()) - 1;
		return critChancePerGemInWeapon * gemstonesInWeapon;
	}

	private static double getCritChanceIfNotHungry(ServerPlayer player) {
		var isHungry = player.getFoodData().getFoodLevel() < 10;
		if (isHungry) {
			return 0D;
		}
		var critChanceIfNotHungry = player.getAttributeValue(PSTAttributes.CRIT_CHANCE_IF_NOT_HUNGRY.get()) - 1;
		return critChanceIfNotHungry;
	}

	private static double getAttackWithEnchantedWeapon(ServerPlayer player) {
		var mainHandItem = player.getMainHandItem();
		if (!ItemHelper.isWeapon(mainHandItem) || !mainHandItem.isEnchanted()) {
			return 0D;
		}
		var attackWithEnchantedWeapon = player.getAttributeValue(PSTAttributes.ATTACK_SPEED_WITH_ENCHANTED_WEAPON.get()) - 1;
		return attackWithEnchantedWeapon;
	}

	private static double getLifePerHitUnderPotionEffect(ServerPlayer player) {
		var lifePerHitUnderPotionEffect = player.getAttributeValue(PSTAttributes.LIFE_PER_HIT_UNDER_POTION_EFFECT.get());
		var hasPotionEffect = !player.getActiveEffects().isEmpty();
		return hasPotionEffect ? lifePerHitUnderPotionEffect : 0;
	}

	private static double getCritDamagePerGemInWeapon(ServerPlayer player) {
		var mainHandItem = player.getMainHandItem();
		if (!ItemHelper.isWeapon(mainHandItem)) {
			return 0D;
		}
		var gemstonesInWeapon = GemHelper.getGemsCount(mainHandItem);
		if (gemstonesInWeapon == 0) {
			return 0D;
		}
		var critDamagePerGemInWeapon = player.getAttributeValue(PSTAttributes.CRIT_DAMAGE_PER_GEM_IN_WEAPON.get()) - 1;
		return critDamagePerGemInWeapon * gemstonesInWeapon;
	}

	private static double getLifeRegenerationPerGemInHelmet(ServerPlayer player) {
		var lifeRegenerationPerGemInHelmet = player.getAttributeValue(PSTAttributes.LIFE_REGENERATION_PER_GEM_IN_HELMET.get());
		var getmstonesInHelmet = GemHelper.getGemsCount(player.getItemBySlot(EquipmentSlot.HEAD));
		return lifeRegenerationPerGemInHelmet * getmstonesInHelmet;
	}

	private static double getAttackSpeedWithShield(ServerPlayer player) {
		var offhandItem = player.getOffhandItem();
		if (!ItemHelper.isShield(offhandItem)) {
			return 0D;
		}
		var attackSpeedWithShield = player.getAttributeValue(PSTAttributes.ATTACK_SPEED_WITH_SHIELD.get()) - 1;
		return attackSpeedWithShield;
	}

	private static double getLifeRegenerationWithShield(ServerPlayer player) {
		var offhandItem = player.getOffhandItem();
		if (!ItemHelper.isShield(offhandItem)) {
			return 0D;
		}
		var lifeRegenerationWithShield = player.getAttributeValue(PSTAttributes.LIFE_REGENERATION_WITH_SHIELD.get());
		return lifeRegenerationWithShield;
	}

	private static double getBonusChestplateArmor(ServerPlayer player) {
		var chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		if (chestplate.isEmpty()) {
			return 0D;
		}
		var chestplateArmor = new AtomicDouble();
		chestplate.getAttributeModifiers(EquipmentSlot.CHEST).forEach((attribute, modifier) -> {
			if (attribute == Attributes.ARMOR && modifier.getOperation() == Operation.ADDITION) {
				chestplateArmor.addAndGet(modifier.getAmount());
			}
		});
		if (chestplateArmor.get() == 0) {
			return 0;
		}
		var bonusChestplateArmor = player.getAttributeValue(PSTAttributes.CHESTPLATE_ARMOR.get()) - 1;
		return bonusChestplateArmor * chestplateArmor.get();
	}

	private static double getLifeOnBlockPerShieldEnchantment(ServerPlayer player) {
		var offhandItem = player.getOffhandItem();
		if (!ItemHelper.isShield(offhandItem)) {
			return 0D;
		}
		var enchantmentsCount = offhandItem.getAllEnchantments().size();
		if (enchantmentsCount == 0) {
			return 0D;
		}
		var lifeOnBlockPerShieldEnchantment = player.getAttributeValue(PSTAttributes.LIFE_ON_BLOCK_PER_SHIELD_ENCHANTMENT.get());
		return lifeOnBlockPerShieldEnchantment * enchantmentsCount;
	}

	private static double getBlockChancePerShieldEnchantment(ServerPlayer player) {
		var offhandItem = player.getOffhandItem();
		if (!ItemHelper.isShield(offhandItem)) return 0D;
		int enchantments = offhandItem.getAllEnchantments().size();
		if (enchantments == 0) return 0D;
		var blockChancePerShieldEnchantment = player.getAttributeValue(PSTAttributes.BLOCK_CHANCE_PER_SHIELD_ENCHANTMENT.get()) - 1;
		return blockChancePerShieldEnchantment * enchantments;
	}

	private static double getBlockChanceIfNotHungry(ServerPlayer player) {
		var isHungry = player.getFoodData().getFoodLevel() < 10;
		if (isHungry) return 0D;
		var blockChanceIfNotHungry = player.getAttributeValue(PSTAttributes.BLOCK_CHANCE_IF_NOT_HUNGRY.get()) - 1;
		return blockChanceIfNotHungry;
	}

	private static double getLifeOnBlockIfNotHungry(ServerPlayer player) {
		var isHungry = player.getFoodData().getFoodLevel() < 10;
		if (isHungry) return 0D;
		var lifeOnBlockIfNotHungry = player.getAttributeValue(PSTAttributes.LIFE_ON_BLOCK_IF_NOT_HUNGRY.get());
		return lifeOnBlockIfNotHungry;
	}

	private static double getMaximumLifeIfNotHungry(ServerPlayer player) {
		var isHungry = player.getFoodData().getFoodLevel() < 10;
		if (isHungry) return 0D;
		var maximumLifeIfNotHungry = player.getAttributeValue(PSTAttributes.MAXIMUM_LIFE_IF_NOT_HUNGRY.get());
		return maximumLifeIfNotHungry;
	}

	private static double getBlockChancePerSatisfiedHunger(ServerPlayer player) {
		var satisfiedHunger = player.getFoodData().getFoodLevel();
		if (satisfiedHunger == 0) return 0D;
		var blockChancePerSatisfiedHunger = player.getAttributeValue(PSTAttributes.BLOCK_CHANCE_PER_SATISFIED_HUNGER.get()) - 1;
		return satisfiedHunger * blockChancePerSatisfiedHunger;
	}

	private static double getMaximumLifePerEquippedJewelry(ServerPlayer player) {
		int jewelry = CuriosApi.getCuriosHelper().findCurios(player, "ring", "necklace").size();
		if (jewelry == 0) return 0D;
		return jewelry * player.getAttributeValue(PSTAttributes.MAXIMUM_LIFE_PER_EQUIPPED_JEWELRY.get());
	}

	private static double getAttackSpeedIfWounded(ServerPlayer player) {
		if (player.getHealth() >= player.getMaxHealth() / 2) return 0D;
		return player.getAttributeValue(PSTAttributes.ATTACK_SPEED_IF_WOUNDED.get()) - 1;
	}

	private static double getCritChanceIfWounded(ServerPlayer player) {
		if (player.getHealth() >= player.getMaxHealth() / 2) return 0D;
		return player.getAttributeValue(PSTAttributes.CRIT_CHANCE_IF_WOUNDED.get()) - 1;
	}

	private static double getLifePerHitIfWounded(ServerPlayer player) {
		if (player.getHealth() >= player.getMaxHealth() / 2) return 0D;
		return player.getAttributeValue(PSTAttributes.LIFE_PER_HIT_IF_WOUNDED.get());
	}

	private static double getMaximumLifePerArrowInQuiver(ServerPlayer player) {
		Optional<SlotResult> quiverCurio = CuriosApi.getCuriosHelper().findFirstCurio(player, ItemHelper::isQuiver);
		if (!quiverCurio.isPresent()) return 0D;
		double lifeBonus = player.getAttributeValue(PSTAttributes.MAXIMUM_LIFE_PER_ARROW_IN_QUIVER.get());
		if (lifeBonus == 0) return 0D;
		ItemStack quiver = quiverCurio.map(SlotResult::stack).orElse(ItemStack.EMPTY);
		return QuiverItem.getArrowsCount(quiver) * lifeBonus;
	}

	private static double getLuckWhileFishing(ServerPlayer player) {
		if (player.fishing == null) return 0D;
		return player.getAttributeValue(PSTAttributes.LUCK_WHILE_FISHING.get());
	}

	@SubscribeEvent
	public static void setCraftedArmorBonuses(ItemCraftedEvent event) {
		ItemStack stack = event.getCrafting();
		if (!ItemHelper.isArmor(stack)) return;
		double defenceBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_ARMOR_DEFENCE.get()) - 1;
		if (defenceBonus > 0) {
			ItemHelper.setBonus(stack, ItemHelper.DEFENCE, defenceBonus);
		}
		double lifeBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_ARMOR_MAXIMUM_LIFE.get());
		if (lifeBonus > 0) {
			ItemHelper.setBonus(stack, ItemHelper.MAXIMUM_LIFE, lifeBonus);
		}
		double toughnessBonus = event.getEntity().getAttributeValue(PSTAttributes.CHANCE_TO_CRAFT_TOUGHER_ARMOR.get()) - 1;
		if (toughnessBonus > 0) {
			double toughness = Math.floor(toughnessBonus);
			toughnessBonus -= toughness;
			if (event.getEntity().getRandom().nextFloat() < toughnessBonus) toughness++;
			ItemHelper.setBonus(stack, ItemHelper.TOUGHNESS, toughness);
		}
		double evasionBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_ARMOR_EVASION.get()) - 1;
		if (evasionBonus > 0) {
			ItemHelper.setBonus(stack, ItemHelper.EVASION, evasionBonus);
		}
		double stealthBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_ARMOR_STEALTH.get()) - 1;
		if (ItemHelper.isBoots(stack)) {
			stealthBonus += event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_BOOTS_STEALTH.get()) - 1;
		}
		if (stealthBonus > 0) {
			ItemHelper.setBonus(stack, ItemHelper.STEALTH, stealthBonus);
		}
		if (ItemHelper.isHelmet(stack)) {
			double helmetSockets = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_HELMETS_SOCKETS.get());
			if (helmetSockets > 0) {
				ItemHelper.setBonus(stack, ItemHelper.ADDITIONAL_SOCKETS, 1);
			}
		}
		if (ItemHelper.isBoots(stack)) {
			double bootsSockets = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_BOOTS_SOCKETS.get());
			if (bootsSockets > 0) {
				ItemHelper.setBonus(stack, ItemHelper.ADDITIONAL_SOCKETS, 1);
			}
			double bootsMovementSpeed = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_BOOTS_MOVEMENT_SPEED.get()) - 1;
			if (bootsMovementSpeed > 0) {
				ItemHelper.setBonus(stack, ItemHelper.MOVEMENT_SPEED, bootsMovementSpeed);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void applyArmorBonuses(ItemAttributeModifierEvent event) {
		ItemStack stack = event.getItemStack();
		if (!ItemHelper.isArmor(stack)) return;
		if (ItemHelper.hasBonus(stack, ItemHelper.DEFENCE)) {
			double bonus = ItemHelper.getBonus(stack, ItemHelper.DEFENCE);
			applyBaseModifierBonus(event, Attributes.ARMOR, d -> bonus * d);
		}
		applyArmorBonus(event, ItemHelper.TOUGHNESS, Attributes.ARMOR_TOUGHNESS, Operation.ADDITION);
		applyArmorBonus(event, ItemHelper.MOVEMENT_SPEED, Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_BASE);
		applyArmorBonus(event, ItemHelper.MAXIMUM_LIFE, Attributes.MAX_HEALTH, Operation.ADDITION);
		applyArmorBonus(event, ItemHelper.STEALTH, PSTAttributes.STEALTH.get(), Operation.MULTIPLY_BASE);
		applyArmorBonus(event, ItemHelper.EVASION, PSTAttributes.EVASION.get(), Operation.MULTIPLY_BASE);
	}

	private static void applyBaseModifierBonus(ItemAttributeModifierEvent event, Attribute attribute, Function<Double, Double> func) {
		double baseValue = 0D;
		for (AttributeModifier modifier : event.getOriginalModifiers().get(attribute)) {
			if (modifier.getOperation() == Operation.ADDITION) {
				baseValue += modifier.getAmount();
			}
		}
		double value = func.apply(baseValue);
		if (value != 0) {
			UUID id = UUID.fromString("d57e6ff6-4665-417b-8bdb-1b0f58261814");
			AttributeModifier modifier = new AttributeModifier(id, "CraftedItemBonus", value, Operation.ADDITION);
			event.addModifier(attribute, modifier);
		}
	}

	private static void applyArmorBonus(ItemAttributeModifierEvent event, String type, Attribute attribute, Operation operation) {
		// formatter:off
		String[] modifierIds = new String[] {
			"2493d298-ea9f-46b9-8769-4362dbada80e",
			"e78c1b0b-9109-413d-9351-da90388496e6",
			"58562319-48ee-43f4-8cc4-9e7b011d6773",
			"4a9913ec-0bc5-42d3-9922-fc35684bd67f"
		};
		// formatter:on
		EquipmentSlot slot = Player.getEquipmentSlotForItem(event.getItemStack());
		if (ItemHelper.hasBonus(event.getItemStack(), type) && slot == event.getSlotType()) {
			double bonus = ItemHelper.getBonus(event.getItemStack(), type);
			UUID modifierId = UUID.fromString(modifierIds[event.getSlotType().getIndex()]);
			AttributeModifier modifier = new AttributeModifier(modifierId, "CraftedArmorBonus", bonus, operation);
			event.addModifier(attribute, modifier);
		}
	}

	@SubscribeEvent
	public static void setCraftedRingsBonuses(ItemCraftedEvent event) {
		ItemStack stack = event.getCrafting();
		if (!ItemHelper.isRing(stack)) return;
		double critDamageBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_RINGS_CRITICAL_DAMAGE.get()) - 1;
		if (critDamageBonus > 0) ItemHelper.setBonus(stack, ItemHelper.CRIT_DAMAGE, critDamageBonus);
	}

	@SubscribeEvent
	public static void setCraftedNecklacesBonuses(ItemCraftedEvent event) {
		ItemStack stack = event.getCrafting();
		if (!ItemHelper.isNecklace(stack)) return;
		double lifeBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_NECKLACES_MAXIMUM_LIFE.get());
		if (lifeBonus > 0) ItemHelper.setBonus(stack, ItemHelper.MAXIMUM_LIFE, lifeBonus);
	}

	@SubscribeEvent
	public static void setCraftedQuiverBonuses(ItemCraftedEvent event) {
		ItemStack stack = event.getCrafting();
		if (!ItemHelper.isQuiver(stack)) return;
		double lifeBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_QUIVERS_MAXIMUM_LIFE.get());
		if (lifeBonus > 0) ItemHelper.setBonus(stack, ItemHelper.MAXIMUM_LIFE, lifeBonus);
		double capacityBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_QUIVERS_CAPACITY.get()) - 1;
		if (capacityBonus > 0) ItemHelper.setBonus(stack, ItemHelper.CAPACITY, capacityBonus);
		double igniteChanceBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_QUIVERS_CHANCE_TO_IGNITE.get()) - 1;
		if (igniteChanceBonus > 0) ItemHelper.setBonus(stack, ItemHelper.IGNITE_CHANCE, igniteChanceBonus);
		double damageAgainstBurningBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_QUIVERS_DAMAGE_AGAINST_BURNING.get()) - 1;
		if (damageAgainstBurningBonus > 0) ItemHelper.setBonus(stack, ItemHelper.DAMAGE_AGAINST_BURNING, damageAgainstBurningBonus);
		double chanceToRetrieveArrows = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_QUIVERS_CHANCE_TO_RETRIEVE_ARROWS.get()) - 1;
		if (chanceToRetrieveArrows > 0) ItemHelper.setBonus(stack, ItemHelper.CHANCE_TO_RETRIEVE_ARROWS, chanceToRetrieveArrows);
	}

	@SubscribeEvent
	public static void applyCraftedCuriosBonuses(CurioAttributeModifierEvent event) {
		if (ItemHelper.hasBonus(event.getItemStack(), ItemHelper.CRIT_DAMAGE)) {
			double bonus = ItemHelper.getBonus(event.getItemStack(), ItemHelper.CRIT_DAMAGE);
			AttributeModifier modifier = new AttributeModifier(event.getUuid(), "CraftedCurioBonus", bonus, Operation.MULTIPLY_BASE);
			event.addModifier(PSTAttributes.CRIT_DAMAGE.get(), modifier);
		}
		if (ItemHelper.hasBonus(event.getItemStack(), ItemHelper.MAXIMUM_LIFE)) {
			double bonus = ItemHelper.getBonus(event.getItemStack(), ItemHelper.MAXIMUM_LIFE);
			AttributeModifier modifier = new AttributeModifier(event.getUuid(), "CraftedCurioBonus", bonus, Operation.ADDITION);
			event.addModifier(Attributes.MAX_HEALTH, modifier);
		}
		if (ItemHelper.hasBonus(event.getItemStack(), ItemHelper.IGNITE_CHANCE)) {
			double bonus = ItemHelper.getBonus(event.getItemStack(), ItemHelper.IGNITE_CHANCE);
			AttributeModifier modifier = new AttributeModifier(event.getUuid(), "CraftedCurioBonus", bonus, Operation.MULTIPLY_BASE);
			event.addModifier(PSTAttributes.CHANCE_TO_IGNITE.get(), modifier);
		}
		if (ItemHelper.hasBonus(event.getItemStack(), ItemHelper.DAMAGE_AGAINST_BURNING)) {
			double bonus = ItemHelper.getBonus(event.getItemStack(), ItemHelper.DAMAGE_AGAINST_BURNING);
			AttributeModifier modifier = new AttributeModifier(event.getUuid(), "CraftedCurioBonus", bonus, Operation.MULTIPLY_BASE);
			event.addModifier(PSTAttributes.DAMAGE_AGAINST_BURNING.get(), modifier);
		}
		if (ItemHelper.hasBonus(event.getItemStack(), ItemHelper.CHANCE_TO_RETRIEVE_ARROWS)) {
			double bonus = ItemHelper.getBonus(event.getItemStack(), ItemHelper.CHANCE_TO_RETRIEVE_ARROWS);
			AttributeModifier modifier = new AttributeModifier(event.getUuid(), "CraftedCurioBonus", bonus, Operation.MULTIPLY_BASE);
			event.addModifier(PSTAttributes.CHANCE_TO_RETRIEVE_ARROWS.get(), modifier);
		}
	}

	@SubscribeEvent
	public static void setCraftedShieldBonuses(ItemCraftedEvent event) {
		ItemStack stack = event.getCrafting();
		if (!ItemHelper.isShield(stack)) return;
		double armorBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_SHIELDS_ARMOR.get());
		if (armorBonus > 0) ItemHelper.setBonus(stack, ItemHelper.DEFENCE, armorBonus);
		double lifeBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_SHIELDS_MAXIMUM_LIFE.get());
		if (lifeBonus > 0) ItemHelper.setBonus(stack, ItemHelper.MAXIMUM_LIFE, lifeBonus);
		double blockBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_SHIELDS_BLOCK_CHANCE.get()) - 1;
		if (blockBonus > 0) ItemHelper.setBonus(stack, ItemHelper.BLOCK_CHANCE, blockBonus);
	}

	@SubscribeEvent
	public static void applyShieldAttributeBonuses(ItemAttributeModifierEvent event) {
		if (!ItemHelper.isShield(event.getItemStack())) return;
		if (event.getSlotType() != EquipmentSlot.OFFHAND) return;
		if (ItemHelper.hasBonus(event.getItemStack(), ItemHelper.DEFENCE)) {
			double armorBonus = ItemHelper.getBonus(event.getItemStack(), ItemHelper.DEFENCE);
			UUID modifierId = UUID.fromString("7174b3ef-e310-4b60-9535-eeddc741fcf5");
			AttributeModifier modifier = new AttributeModifier(modifierId, "CraftedShieldBonus", armorBonus, Operation.ADDITION);
			event.addModifier(Attributes.ARMOR, modifier);
		}
		if (ItemHelper.hasBonus(event.getItemStack(), ItemHelper.MAXIMUM_LIFE)) {
			double lifeBonus = ItemHelper.getBonus(event.getItemStack(), ItemHelper.MAXIMUM_LIFE);
			UUID modifierId = UUID.fromString("9f696045-500c-437d-8c20-0560271e0a45");
			AttributeModifier modifier = new AttributeModifier(modifierId, "CraftedShieldBonus", lifeBonus, Operation.ADDITION);
			event.addModifier(Attributes.MAX_HEALTH, modifier);
		}
		if (ItemHelper.hasBonus(event.getItemStack(), ItemHelper.BLOCK_CHANCE)) {
			double blockBonus = ItemHelper.getBonus(event.getItemStack(), ItemHelper.BLOCK_CHANCE);
			UUID modifierId = UUID.fromString("dd4374cc-3783-4953-9413-080d0b0d9e87");
			AttributeModifier modifier = new AttributeModifier(modifierId, "CraftedShieldBonus", blockBonus, Operation.MULTIPLY_BASE);
			event.addModifier(PSTAttributes.BLOCK_CHANCE.get(), modifier);
		}
	}

	@SubscribeEvent
	public static void setCraftedWeaponBonuses(ItemCraftedEvent event) {
		ItemStack stack = event.getCrafting();
		if (!ItemHelper.isWeapon(stack)) return;
		double igniteChanceBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_WEAPON_CHANCE_TO_IGNITE.get()) - 1;
		if (igniteChanceBonus > 0) ItemHelper.setBonus(stack, ItemHelper.IGNITE_CHANCE, igniteChanceBonus);
		double damageAgainstBurningBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_WEAPON_DAMAGE_AGAINST_BURNING.get()) - 1;
		if (damageAgainstBurningBonus > 0) ItemHelper.setBonus(stack, ItemHelper.DAMAGE_AGAINST_BURNING, damageAgainstBurningBonus);
		double lifePerHitBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_WEAPON_LIFE_PER_HIT.get());
		if (lifePerHitBonus > 0) ItemHelper.setBonus(stack, ItemHelper.LIFE_PER_HIT, lifePerHitBonus);
		double doubleLootBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_WEAPON_DOUBLE_LOOT_CHANCE.get()) - 1;
		if (doubleLootBonus > 0) ItemHelper.setBonus(stack, ItemHelper.DOUBLE_LOOT, doubleLootBonus);
		if (ItemHelper.isRangedWeapon(stack)) {
			double attackSpeedBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_RANGED_WEAPON_ATTACK_SPEED.get()) - 1;
			attackSpeedBonus += event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_WEAPON_ATTACK_SPEED.get()) - 1;
			if (attackSpeedBonus > 0) ItemHelper.setBonus(stack, ItemHelper.ATTACK_SPEED, attackSpeedBonus);
			int additionalSockets = (int) event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_RANGED_WEAPON_SOCKETS.get());
			if (additionalSockets > 0) ItemHelper.setBonus(stack, ItemHelper.ADDITIONAL_SOCKETS, 1);
		}
		if (ItemHelper.isMeleeWeapon(stack)) {
			double damageBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_MELEE_WEAPON_DAMAGE_BONUS.get());
			if (damageBonus > 0) ItemHelper.setBonus(stack, ItemHelper.DAMAGE, damageBonus);
			double attackSpeedBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_MELEE_WEAPON_ATTACK_SPEED.get()) - 1;
			attackSpeedBonus += event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_WEAPON_ATTACK_SPEED.get()) - 1;
			if (attackSpeedBonus > 0) ItemHelper.setBonus(stack, ItemHelper.ATTACK_SPEED, attackSpeedBonus);
			double critChanceBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_MELEE_WEAPON_CRIT_CHANCE.get()) - 1;
			if (ItemHelper.isAxe(stack)) critChanceBonus += event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_AXES_CRIT_CHANCE.get()) - 1;
			if (critChanceBonus > 0) ItemHelper.setBonus(stack, ItemHelper.CRIT_CHANCE, critChanceBonus);
		}
	}

	@SubscribeEvent
	public static void applyCraftedWeaponAttributeBonuses(ItemAttributeModifierEvent event) {
		if (event.getSlotType() != EquipmentSlot.MAINHAND) return;
		ItemStack stack = event.getItemStack();
		if (!ItemHelper.isWeapon(stack)) return;
		if (ItemHelper.isMeleeWeapon(stack)) {
			if (ItemHelper.hasBonus(stack, ItemHelper.DAMAGE)) {
				double damageBonus = ItemHelper.getBonus(stack, ItemHelper.DAMAGE);
				applyBaseModifierBonus(event, Attributes.ATTACK_DAMAGE, d -> damageBonus);
			}
			if (ItemHelper.hasBonus(stack, ItemHelper.ATTACK_SPEED)) {
				double attackSpeedBonus = ItemHelper.getBonus(stack, ItemHelper.ATTACK_SPEED);
				applyBaseModifierBonus(event, Attributes.ATTACK_SPEED, d -> {
					double basePlayerAttackSpeed = 4;
					double oldAttackSpeed = basePlayerAttackSpeed + d;
					return oldAttackSpeed * attackSpeedBonus;
				});
			}
		}
		if (ItemHelper.isRangedWeapon(stack)) {
			// Item.BASE_ATTACK_SPEED_UUID
			UUID modifierId = (UUID) ObfuscationReflectionHelper.getPrivateValue(Item.class, null, "f_41375_");
			// Base attack speed is 4 attacks per second, we need to reduce it to 1
			float modifierAmount = -3F;
			if (ItemHelper.hasBonus(stack, ItemHelper.ATTACK_SPEED)) {
				double attackSpeedBonus = ItemHelper.getBonus(stack, ItemHelper.ATTACK_SPEED);
				modifierAmount += attackSpeedBonus;
			}
			AttributeModifier modifier = new AttributeModifier(modifierId, "BaseAttackSpeed", modifierAmount, Operation.ADDITION);
			event.addModifier(Attributes.ATTACK_SPEED, modifier);
		}
		applyCraftedWeaponBonus(event, ItemHelper.IGNITE_CHANCE, PSTAttributes.CHANCE_TO_IGNITE.get(), Operation.MULTIPLY_BASE);
		applyCraftedWeaponBonus(event, ItemHelper.CRIT_CHANCE, PSTAttributes.CRIT_CHANCE.get(), Operation.MULTIPLY_BASE);
		applyCraftedWeaponBonus(event, ItemHelper.DAMAGE_AGAINST_BURNING, PSTAttributes.DAMAGE_AGAINST_BURNING.get(), Operation.MULTIPLY_BASE);
		applyCraftedWeaponBonus(event, ItemHelper.LIFE_PER_HIT, PSTAttributes.LIFE_PER_HIT.get(), Operation.ADDITION);
		applyCraftedWeaponBonus(event, ItemHelper.DOUBLE_LOOT, PSTAttributes.DOUBLE_LOOT_CHANCE.get(), Operation.MULTIPLY_BASE);
	}

	private static void applyCraftedWeaponBonus(ItemAttributeModifierEvent event, String type, Attribute attribute, Operation operation) {
		if (ItemHelper.hasBonus(event.getItemStack(), type)) {
			double bonus = ItemHelper.getBonus(event.getItemStack(), type);
			UUID modifierId = UUID.fromString("ad214d33-db04-4a52-8d66-06b3ad38d76b");
			AttributeModifier modifier = new AttributeModifier(modifierId, "CraftedWeaponBonus", bonus, operation);
			event.addModifier(attribute, modifier);
		}
	}

	@SubscribeEvent
	public static void applyBreakSpeedBonus(PlayerEvent.BreakSpeed event) {
		if (ItemHelper.isPickaxe(event.getEntity().getMainHandItem())) {
			float miningSpeedMultiplier = (float) event.getEntity().getAttributeValue(PSTAttributes.MINING_SPEED.get());
			event.setNewSpeed(event.getOriginalSpeed() * miningSpeedMultiplier);
		}
	}

	@SubscribeEvent
	public static void applyLifeRegenerationBonus(PlayerTickEvent event) {
		if (event.phase == Phase.END || event.player.level.isClientSide) return;
		if (event.player.getFoodData().getFoodLevel() == 0) return;
		float lifeRegeneration = (float) event.player.getAttributeValue(PSTAttributes.LIFE_REGENERATION.get());
		if (event.player.getHealth() != event.player.getMaxHealth() && event.player.tickCount % 20 == 0) {
			event.player.heal(lifeRegeneration);
			event.player.getFoodData().addExhaustion(lifeRegeneration / 5);
		}
	}

	@SubscribeEvent
	public static void applyEvasionBonus(LivingAttackEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		float evasion = (float) player.getAttributeValue(PSTAttributes.EVASION.get()) - 1;
		if (evasion == 0) return;
		boolean canEvade = PlayerHelper.canEvadeDamage(event.getSource());
		if (canEvade && player.getRandom().nextFloat() < evasion) {
			player.level.playSound(null, player, SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.5F, 1.5F);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void applyLootDuplicationChanceBonus(LivingDropsEvent event) {
		// shoudln't multiply player's loot
		if (event.getEntity() instanceof Player) return;
		if (!(event.getSource().getEntity() instanceof Player player)) return;
		double doubleLootChance = player.getAttributeValue(PSTAttributes.DOUBLE_LOOT_CHANCE.get()) - 1;
		double tripleLootChance = player.getAttributeValue(PSTAttributes.TRIPLE_LOOT_CHANCE.get()) - 1;
		if (player.getRandom().nextFloat() < doubleLootChance) {
			List<ItemEntity> drops = getDrops(event);
			event.getDrops().addAll(drops);
		}
		if (player.getRandom().nextFloat() < tripleLootChance) {
			List<ItemEntity> drops = getDrops(event);
			event.getDrops().addAll(drops);
			event.getDrops().addAll(drops);
		}
	}

	protected static List<ItemEntity> getDrops(LivingDropsEvent event) {
		List<ItemEntity> drops = new ArrayList<ItemEntity>();
		event.getDrops().stream().map(ItemEntity::copy).forEach(drops::add);
		if (event.getEntity() instanceof EquipmentContainer entity) drops.removeIf(entity::equipped);
		return drops;
	}

	@SubscribeEvent
	public static void setCraftedFoodBonuses(ItemCraftedEvent event) {
		if (!ItemHelper.isFood(event.getCrafting())) return;
		FoodHelper.setCraftedFoodBonuses(event.getCrafting(), event.getEntity());
	}

	@SubscribeEvent
	public static void setCraftedFoodBonuses(ItemSmeltedEvent event) {
		ItemStack craftedItem = event.getSmelting();
		if (!ItemHelper.isFood(craftedItem)) return;
		Player player = event.getEntity();
		FoodHelper.setCraftedFoodBonuses(craftedItem, player);
	}

	@SubscribeEvent
	public static void applyCraftedFoodBonuses(LivingEntityUseItemEvent.Finish event) {
		if (!ItemHelper.isFood(event.getItem())) return;
		if (!(event.getEntity() instanceof Player)) return;
		var player = (Player) event.getEntity();
		var restoration = event.getItem().getFoodProperties(player).getNutrition();
		if (FoodHelper.hasHealingBonus(event.getItem())) {
			var healingBonus = FoodHelper.getHealingBonus(event.getItem());
			player.heal(healingBonus * restoration);
		}
		if (FoodHelper.hasDamageBonus(event.getItem())) {
			var damageBonus = FoodHelper.getDamageBonus(event.getItem());
			var effectAmplifier = (int) (damageBonus * restoration * 100);
			var damageEffect = new MobEffectInstance(PSTEffects.DAMAGE_BONUS.get(), 20 * 60, effectAmplifier);
			player.addEffect(damageEffect);
		}
		if (FoodHelper.hasLifeRegenerationBonus(event.getItem())) {
			var lifeRegenerationBonus = FoodHelper.getLifeRegenerationBonus(event.getItem());
			var effectAmplifier = (int) (lifeRegenerationBonus * 100);
			var lifeRegenerationEffect = new MobEffectInstance(PSTEffects.LIFE_REGENERATION_BONUS.get(), 20 * 60, effectAmplifier);
			player.addEffect(lifeRegenerationEffect);
		}
		if (FoodHelper.hasCritDamageBonus(event.getItem())) {
			var critDamageBonus = FoodHelper.getCritDamageBonus(event.getItem());
			var effectAmplifier = (int) (critDamageBonus * restoration * 100);
			var critDamageEffect = new MobEffectInstance(PSTEffects.CRIT_DAMAGE_BONUS.get(), 20 * 60, effectAmplifier);
			player.addEffect(critDamageEffect);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void addFoodBonusesTooltips(ItemTooltipEvent event) {
		if (!ItemHelper.isFood(event.getItemStack())) return;
		float restoration = event.getItemStack().getFoodProperties(event.getEntity()).getNutrition();
		if (FoodHelper.hasRestorationBonus(event.getItemStack())) {
			float restorationBonus = FoodHelper.getRestorationBonus(event.getItemStack());
			restoration += restoration * restorationBonus;
		}
		if (FoodHelper.hasHealingBonus(event.getItemStack())) {
			float healing = FoodHelper.getHealingBonus(event.getItemStack()) * restoration;
			String formatted = String.format("%.1f", healing);
			formatted = formatted.replace(".0", "");
			Component component = Component.literal(formatted).withStyle(ChatFormatting.BLUE);
			event.getToolTip().add(Component.translatable("food.bonus.healing", component).withStyle(ChatFormatting.YELLOW));
		}
		if (FoodHelper.hasCritDamageBonus(event.getItemStack())) {
			int critDamage = (int) (FoodHelper.getCritDamageBonus(event.getItemStack()) * restoration * 100);
			Component component = Component.literal("+" + critDamage + "%").withStyle(ChatFormatting.BLUE);
			event.getToolTip().add(Component.translatable("food.bonus.crit_damage", component).withStyle(ChatFormatting.YELLOW));
		}
		if (FoodHelper.hasDamageBonus(event.getItemStack())) {
			int damage = (int) (FoodHelper.getDamageBonus(event.getItemStack()) * restoration * 100);
			Component component = Component.literal("+" + damage + "%").withStyle(ChatFormatting.BLUE);
			event.getToolTip().add(Component.translatable("food.bonus.damage", component).withStyle(ChatFormatting.YELLOW));
		}
		if (FoodHelper.hasLifeRegenerationBonus(event.getItemStack())) {
			float lifeRegeneration = FoodHelper.getLifeRegenerationBonus(event.getItemStack());
			String formatted = String.format("%.1f", lifeRegeneration);
			formatted = formatted.replace(".0", "");
			Component component = Component.literal("+" + formatted).withStyle(ChatFormatting.BLUE);
			event.getToolTip().add(Component.translatable("food.bonus.life_regeneration", component).withStyle(ChatFormatting.YELLOW));
		}
	}

	@SubscribeEvent
	public static void applyBlockChanceBonus(LivingAttackEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		if (event.getAmount() <= 0) return;
		if (event.getSource().isBypassArmor()) return;
		if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow && arrow.getPierceLevel() > 0) return;
		var player = (Player) event.getEntity();
		var offhandItem = player.getOffhandItem();
		if (!ItemHelper.isShield(offhandItem)) return;
		var blockChance = player.getAttributeValue(PSTAttributes.BLOCK_CHANCE.get()) - 1;
		if (player.getRandom().nextFloat() >= blockChance) return;
		var shieldBlockEvent = ForgeHooks.onShieldBlock(player, event.getSource(), event.getAmount());
		if (shieldBlockEvent.isCanceled()) return;
		event.setCanceled(true);
		player.level.broadcastEntityEvent(player, (byte) 29);
		if (shieldBlockEvent.shieldTakesDamage()) PlayerHelper.hurtShield(player, offhandItem, event.getAmount());
		if (event.getSource().isProjectile()) return;
		var attacker = event.getSource().getDirectEntity();
		if (attacker instanceof LivingEntity livingAttacker) {
			var blockUsingShieldMethod = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "m_6728_", LivingEntity.class);
			try {
				blockUsingShieldMethod.invoke(player, livingAttacker);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public static void applyLifeOnBlockBonus(ShieldBlockEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (player.getFoodData().getFoodLevel() == 0) return;
		float lifeOnBlock = (float) player.getAttributeValue(PSTAttributes.LIFE_ON_BLOCK.get());
		if (lifeOnBlock == 0) return;
		player.getFoodData().addExhaustion(lifeOnBlock / 5F);
		player.heal(lifeOnBlock);
	}

	@SubscribeEvent
	public static void applyPoisonedWeaponEffects(LivingHurtEvent event) {
		var damageSource = event.getSource();
		if (!(damageSource.getDirectEntity() instanceof Player)) return;
		var player = (Player) damageSource.getDirectEntity();
		var weapon = player.getMainHandItem();
		if (!ItemHelper.hasPoisons(weapon)) return;
		var poisons = ItemHelper.getPoisons(weapon);
		var target = event.getEntity();
		poisons.stream().map(MobEffectInstance::new).forEach(target::addEffect);
	}

	@SubscribeEvent
	public static void applyPoisonedThrownTridentEffects(LivingHurtEvent event) {
		var damageSource = event.getSource();
		if (!(damageSource.getDirectEntity() instanceof ThrownTrident)) return;
		var trident = (ThrownTrident) damageSource.getDirectEntity();
		var getPickupItemMethod = ObfuscationReflectionHelper.findMethod(AbstractArrow.class, "m_7941_");
		var weapon = (ItemStack) null;
		try {
			weapon = (ItemStack) getPickupItemMethod.invoke(trident);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		if (!ItemHelper.hasPoisons(weapon)) return;
		var poisons = ItemHelper.getPoisons(weapon);
		var target = event.getEntity();
		poisons.stream().map(MobEffectInstance::new).forEach(target::addEffect);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void addPoisonedWeaponTooltips(ItemTooltipEvent event) {
		ItemStack weapon = event.getItemStack();
		if (!ItemHelper.hasPoisons(weapon)) return;
		event.getToolTip().add(Component.empty());
		event.getToolTip().add(Component.translatable("weapon.poisoned").withStyle(ChatFormatting.DARK_PURPLE));
		ItemHelper.getPoisons(weapon).stream().map(TooltipHelper::getEffectTooltip).forEach(event.getToolTip()::add);
	}

	@SubscribeEvent
	public static void applyLifePerHitBonus(LivingHurtEvent event) {
		Entity directAttacker = event.getSource().getDirectEntity();
		Entity indirectAttacker = event.getSource().getEntity();
		Player player = (Player) null;
		if (directAttacker instanceof Player) {
			player = (Player) directAttacker;
		} else if (directAttacker instanceof AbstractArrow && indirectAttacker instanceof Player) {
			player = (Player) indirectAttacker;
		}
		if (player == null) return;
		if (player.getFoodData().getFoodLevel() == 0) return;
		double lifePerHit = player.getAttributeValue(PSTAttributes.LIFE_PER_HIT.get());
		if (directAttacker instanceof AbstractArrow) {
			lifePerHit += player.getAttributeValue(PSTAttributes.LIFE_PER_PROJECTILE_HIT.get());
		}
		player.getFoodData().addExhaustion((float) (lifePerHit / 5));
		player.heal((float) lifePerHit);
	}

	@SubscribeEvent
	public static void applyChanceToRetrieveArrowsBonus(LivingHurtEvent event) {
		if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) return;
		if (!(event.getSource().getEntity() instanceof Player player)) return;
		double chance = player.getAttributeValue(PSTAttributes.CHANCE_TO_RETRIEVE_ARROWS.get()) - 1;
		if (player.getRandom().nextFloat() >= chance) return;
		LivingEntity target = event.getEntity();
		CompoundTag targetData = target.getPersistentData();
		ListTag stuckArrowsTag = targetData.getList("StuckArrows", new CompoundTag().getId());
		stuckArrowsTag.add(getArrowStack(arrow).save(new CompoundTag()));
		targetData.put("StuckArrows", stuckArrowsTag);
	}

	private static ItemStack getArrowStack(AbstractArrow arrow) {
		try {
			// AbstractArrow.getPickupItem
			return (ItemStack) ObfuscationReflectionHelper.findMethod(AbstractArrow.class, "m_7941_").invoke(arrow);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return ItemStack.EMPTY;
	}

	@SubscribeEvent
	public static void retrieveArrows(LivingDeathEvent event) {
		LivingEntity entity = event.getEntity();
		ListTag stuckArrowsTag = entity.getPersistentData().getList("StuckArrows", new CompoundTag().getId());
		if (stuckArrowsTag.isEmpty()) return;
		stuckArrowsTag.stream().map(CompoundTag.class::cast).map(ItemStack::of).forEach(entity::spawnAtLocation);
	}

	@SubscribeEvent
	public static void setCraftedEquipmentBonuses(ItemCraftedEvent event) {
		ItemStack craftedStack = event.getCrafting();
		if (!ItemHelper.isEquipment(craftedStack)) return;
		double durabilityBonus = event.getEntity().getAttributeValue(PSTAttributes.CRAFTED_EQUIPMENT_DURABILITY.get()) - 1;
		if (durabilityBonus == 0) return;
		ItemHelper.setBonus(craftedStack, ItemHelper.DURABILITY, durabilityBonus);
	}

	@SubscribeEvent
	public static void addAdditionalSocketTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		int sockets = 0;
		if (stack.getItem() instanceof HasAdditionalSockets item) {
			sockets += item.getAdditionalSockets();
		}
		if (ItemHelper.hasBonus(stack, ItemHelper.ADDITIONAL_SOCKETS)) {
			sockets += ItemHelper.getBonus(stack, ItemHelper.ADDITIONAL_SOCKETS);
		}
		if (sockets > 0) {
			Component additionalSocketTooltip = Component.translatable("gem.additional_socket_" + sockets).withStyle(ChatFormatting.YELLOW);
			event.getToolTip().add(1, additionalSocketTooltip);
		}
	}

	@SubscribeEvent
	public static void applyIncomingHealingBonus(LivingHealEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		float multiplier = (float) (player.getAttributeValue(PSTAttributes.INCOMING_HEALING.get()));
		event.setAmount(event.getAmount() * multiplier);
	}

	@SubscribeEvent
	public static void applyExperiencePerHourBonus(PlayerTickEvent event) {
		if (event.player.level.isClientSide) return;
		float bonus = (float) (event.player.getAttributeValue(PSTAttributes.EXPERIENCE_PER_HOUR.get()));
		int frequency = Math.max((int) (1000 / bonus), 1);
		if (event.player.tickCount % frequency == 0) {
			event.player.giveExperiencePoints(1);
		}
	}

	@SubscribeEvent
	public static void applyExperienceFromMobsBonus(LivingExperienceDropEvent event) {
		if (event.getAttackingPlayer() == null) return;
		float bonus = (float) (event.getAttackingPlayer().getAttributeValue(PSTAttributes.EXPERIENCE_FROM_MOBS.get()));
		event.setDroppedExperience((int) (event.getDroppedExperience() * bonus));
	}

	@SubscribeEvent
	public static void applyExperienceFromOreBonus(BreakEvent event) {
		if (!event.getState().is(Tags.Blocks.ORES)) return;
		float bonus = (float) (event.getPlayer().getAttributeValue(PSTAttributes.EXPERIENCE_FROM_ORE.get()));
		event.setExpToDrop((int) (event.getExpToDrop() * bonus));
	}

	@SubscribeEvent
	public static void applyStealthBonus(LivingChangeTargetEvent event) {
		if (!(event.getNewTarget() instanceof Player player)) return;
		double stealth = player.getAttributeValue(PSTAttributes.STEALTH.get()) - 1;
		if (stealth == 0) return;
		if (event.getEntity().distanceTo(player) > event.getEntity().getAttributeValue(Attributes.FOLLOW_RANGE) * (1 - stealth)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void applyFishingExperienceBonus(ItemFishedEvent event) {
		Player player = event.getEntity();
		double expBonus = player.getAttributeValue(PSTAttributes.EXPERIENCE_FROM_FISHING.get()) - 1;
		if (expBonus == 0) return;
		int exp = (int) ((player.getRandom().nextInt(6) + 1) * expBonus);
		if (exp == 0) return;
		ExperienceOrb expOrb = new ExperienceOrb(player.level, player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, exp);
		player.level.addFreshEntity(expOrb);
	}

	@SubscribeEvent
	public static void applyChanceToIgnite(LivingHurtEvent event) {
		if (!(event.getSource().getEntity() instanceof Player player)) return;
		double chance = player.getAttributeValue(PSTAttributes.CHANCE_TO_IGNITE.get()) - 1;
		if (player.getRandom().nextFloat() >= chance) return;
		event.getEntity().setSecondsOnFire(5);
	}

	@SubscribeEvent
	public static void applyChanceToExplodeEnemy(LivingHurtEvent event) {
		if (!(event.getSource().getEntity() instanceof Player player)) return;
		double chance = player.getAttributeValue(PSTAttributes.CHANCE_TO_EXPLODE_ENEMY.get()) - 1;
		if (player.getRandom().nextFloat() >= chance) return;
		LivingEntity target = event.getEntity();
		target.level.explode(player, target.getX(), target.getEyeY(), target.getZ(), 2F, BlockInteraction.NONE);
	}

	@SubscribeEvent
	public static void applyGrindstoneExpPenalty(GrindstoneEvent.OnTakeItem event) {
		event.setXp((int) (event.getXp() * Config.grindstone_exp_multiplier));
	}

	@SubscribeEvent
	public static void applyRepairEfficiencyBonus(AnvilUpdateEvent event) {
		double repairEfficiency = event.getPlayer().getAttributeValue(PSTAttributes.EQUIPMENT_REPAIR_EFFICIENCY.get()) - 1;
		if (repairEfficiency == 0) return;
		ItemStack stack = event.getLeft();
		if (!stack.isDamageableItem() || !stack.isDamaged()) return;
		ItemStack material = event.getRight();
		if (!stack.getItem().isValidRepairItem(stack, material)) return;
		ItemStack result = stack.copy();
		int durabilityPerMaterial = (int) (result.getMaxDamage() * 12 * (1 + repairEfficiency) / 100);
		int durabilityRestored = durabilityPerMaterial;
		int materialsUsed;
		int cost = 0;
		for (materialsUsed = 0; durabilityRestored > 0 && materialsUsed < material.getCount(); materialsUsed++) {
			result.setDamageValue(result.getDamageValue() - durabilityRestored);
			cost++;
			durabilityRestored = Math.min(result.getDamageValue(), durabilityPerMaterial);
		}
		if (event.getName() != null && !StringUtils.isBlank(event.getName())) {
			if (!event.getName().equals(stack.getHoverName().getString())) {
				cost++;
				result.setHoverName(Component.literal(event.getName()));
			}
		} else if (stack.hasCustomHoverName()) {
			cost++;
			result.resetHoverName();
		}
		event.setMaterialCost(materialsUsed);
		event.setCost(cost);
		event.setOutput(result);
	}
}
