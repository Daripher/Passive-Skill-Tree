package daripher.skilltree.attribute;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.function.Function;

import com.google.common.util.concurrent.AtomicDouble;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeEffects;
import daripher.skilltree.init.SkillTreeItems;
import daripher.skilltree.item.GemstoneItem;
import daripher.skilltree.util.FoodHelper;
import daripher.skilltree.util.ItemHelper;
import daripher.skilltree.util.PlayerHelper;
import daripher.skilltree.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class AttributeBonusHandler {
	@SubscribeEvent
	public static void applyDamageBonus(LivingHurtEvent event) {
		if (event.getSource().getDirectEntity() instanceof Player player) {
			var damageBonus = PlayerHelper.getFlatDamageBonus(player);
			var damageMultiplier = PlayerHelper.getDamageMultiplier(player, event.getEntity());
			var damage = (event.getAmount() + damageBonus) * damageMultiplier;
			event.setAmount(damage);
		}
	}

	@SubscribeEvent
	public static void applyCritBonus(CriticalHitEvent event) {
		if (event.isVanillaCritical()) {
			return;
		}
		if (event.getTarget() instanceof LivingEntity target) {
			var critChance = PlayerHelper.getCritChance(event.getEntity(), target);
			critChance = Math.min(0.75F, critChance);
			if (event.getEntity().getRandom().nextFloat() < critChance) {
				var critDamage = PlayerHelper.getCritDamageMultiplier(event.getEntity(), target);
				event.setDamageModifier(critDamage);
				event.setResult(Result.ALLOW);
			}
		}
	}

	@SubscribeEvent
	public static void applyDynamicAttributeBonuses(PlayerTickEvent event) {
		if (event.player.level.isClientSide) {
			return;
		}
		var player = event.player;
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "d1f7e78b-3368-409c-aa89-90f0f89a5524", AttributeBonusHandler::getMaximumLifePerEvasion);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "b68181bd-fbc4-4a63-95d4-df386fe3f71f", AttributeBonusHandler::getMaximumLifePerGemstoneInArmor);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "7cb71ee5-8715-40ae-a877-72ec3b49b33e", AttributeBonusHandler::getArmorPerEvasion);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "66eae15c-53eb-4a4a-b511-2ab94f81324b", AttributeBonusHandler::getArmorPerGemstoneInChestplate);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "1080308c-bdd4-4693-876c-a36390b66b73", AttributeBonusHandler::getArmorPerGemstoneInHelmet);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_DAMAGE, Operation.ADDITION, "d1079882-dd8c-42b7-9a43-3928553193c8", AttributeBonusHandler::getDamagePerArmor);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "9199d7cf-c7e4-4123-b636-6f6591e1137d", AttributeBonusHandler::getMaximumLifePerArmor);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "8810227f-9798-4890-8400-91c0941a3fc0", AttributeBonusHandler::getMaximumLifePerBootsArmor);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "98a17cd0-68c8-4808-8981-1796c33295e7", AttributeBonusHandler::getMaximumLifePerSatisfiedHunger);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.EVASION_CHANCE.get(), Operation.MULTIPLY_BASE, "4aa87d74-b729-4e1d-9c76-893495050416", AttributeBonusHandler::getEvasionUnderPotionEffect);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_TOTAL, "a4daf7f8-29e3-404d-8277-9215a16ef4c8", AttributeBonusHandler::getAttackSpeedUnderPotionEffect);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "de712f9d-9f47-475c-8b86-188bca70d1df", AttributeBonusHandler::getMaximumLifeUnderPotionEffect);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.EVASION_CHANCE.get(), Operation.MULTIPLY_BASE, "282c4f81-7b6d-48e0-82c9-c4ebd58265cb", AttributeBonusHandler::getEvasionPerPotionEffect);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "16c35c5c-56da-4d21-ad56-bd6618fee711", AttributeBonusHandler::getMaximumLifeWithEnchantedItem);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "8b836bea-4c28-4430-8184-7330530239f6", AttributeBonusHandler::getArmorWithEnchantedShield);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "27b4644b-96a0-4443-89e5-1700af61d602", AttributeBonusHandler::getMaximumLifePerEnchantment);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "55c3cb58-c09e-465a-a812-6a18ae587ec0", AttributeBonusHandler::getArmorPerChestplateEnchantment);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "9b1e9aac-fa58-4343-ba88-7541eca2836f", AttributeBonusHandler::getMaximumLifePerArmorEnchantment);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_TOTAL, "5e2d6a95-bc70-4f3d-a348-307b49f5bc84", AttributeBonusHandler::getAttackSpeedWithPickaxe);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.BLOCK_CHANCE.get(), Operation.MULTIPLY_BASE, "bfbc3d6b-7c37-498b-888c-3b05c921f24a", AttributeBonusHandler::getBlockChanceWithEnchantedShield);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.EVASION_CHANCE.get(), Operation.MULTIPLY_BASE, "d2865c2c-d5cc-4de9-a793-752349d27da0", AttributeBonusHandler::getEvasionChanceWhenWounded);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_BASE, "f6dbc327-88c0-4704-b230-91fe1642dc7a", AttributeBonusHandler::getAttackSpeedIfNotHungry);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_BASE, "5d449ea8-12dd-4596-a6e1-e4837946acb6", AttributeBonusHandler::getAttackSpeedWithBow);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_BASE, "e37a2257-8511-4ffb-a5dd-913b591dd520", AttributeBonusHandler::getAttackSpeedPerGemstoneInWeapon);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.CRIT_CHANCE.get(), Operation.MULTIPLY_BASE, "fbc2d0b3-1453-4c49-8220-662e89ae1f45", AttributeBonusHandler::getCritChanceWithBow);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.CRIT_CHANCE.get(), Operation.MULTIPLY_BASE, "44984187-74c8-4927-be18-1e187ca9babe", AttributeBonusHandler::getCritChanceIfNotHungry);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_BASE, "7bd1d9fb-4a20-41f3-89df-7cb42e849c5f", AttributeBonusHandler::getAttackWithEnchantedWeapon);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.LIFE_PER_HIT.get(), Operation.ADDITION, "9c36d4dc-06e3-4f42-b8e6-abb0fb6b344c", AttributeBonusHandler::getLifePerHitUnderPotionEffect);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "77353761-61e2-4f3c-b0e4-2abef4b75d76", AttributeBonusHandler::getMaximumLifePerGemstoneInHelmet);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.CRIT_CHANCE.get(), Operation.MULTIPLY_BASE, "636b118d-478b-4c4e-9785-b6e7da876828", AttributeBonusHandler::getCritChancePerGemstoneInWeapon);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.CRIT_DAMAGE.get(), Operation.MULTIPLY_BASE, "3051c828-7281-458c-b6fc-df9d93b31d30", AttributeBonusHandler::getCritDamagePerGemstoneInWeapon);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.LIFE_REGENERATION.get(), Operation.ADDITION, "6732aed2-1948-4e86-a83c-aad617cd4387", AttributeBonusHandler::getLifeRegenerationPerGemstoneInHelmet);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.MULTIPLY_BASE, "b983bec3-a049-49d7-855e-3025b283c7d2", AttributeBonusHandler::getAttackSpeedWithShield);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.LIFE_REGENERATION.get(), Operation.ADDITION, "d86d8efb-4539-46f3-b157-672b2e1241d6", AttributeBonusHandler::getLifeRegenerationWithShield);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "f11460ca-56f9-4cff-98ea-791ed27f6639", AttributeBonusHandler::getBonusChestplateArmor);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.LIFE_ON_BLOCK.get(), Operation.ADDITION, "6dccce60-76e9-4ca0-8497-8352ba26620d", AttributeBonusHandler::getLifeOnBlockPerShieldEnchantment);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.BLOCK_CHANCE.get(), Operation.MULTIPLY_BASE, "7ea18323-13e4-43ee-bb30-decfdc1b1299", AttributeBonusHandler::getBlockChancePerShieldEnchantment);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.BLOCK_CHANCE.get(), Operation.MULTIPLY_BASE, "629ecea2-51b4-415a-80ff-4137a0f0dce1", AttributeBonusHandler::getBlockChanceIfNotHungry);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.BLOCK_CHANCE.get(), Operation.MULTIPLY_BASE, "fb0934db-cdbb-4fa6-bf4f-d3c0c12f50be", AttributeBonusHandler::getBlockChancePerSatisfiedHunger);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.LIFE_ON_BLOCK.get(), Operation.ADDITION, "793d56a0-7d0c-4bec-a328-65d6d681ec44", AttributeBonusHandler::getLifeOnBlockIfNotHungry);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "e529b2e9-2170-430c-b77d-cfe829f66c69", AttributeBonusHandler::getMaximumLifeIfNotHungry);
	}

	private static void applyDynamicAttributeBonus(Player player, Attribute modifiedAttribute, Operation operation, String id, Function<Player, Double> dynamicBonusFunction) {
		var dynamicBonus = dynamicBonusFunction.apply(player);
		var modifierId = UUID.fromString(id);
		var playerAttribute = player.getAttribute(modifiedAttribute);
		var oldModifier = playerAttribute.getModifier(modifierId);
		if (oldModifier != null) {
			if (oldModifier.getAmount() == dynamicBonus) {
				return;
			}
			playerAttribute.removeModifier(modifierId);
		}
		if (modifiedAttribute == Attributes.MAX_HEALTH) {
			playerAttribute.addPermanentModifier(new AttributeModifier(modifierId, "Skill Tree Bonus", dynamicBonus, operation));
			player.setHealth(player.getHealth());
		} else {
			playerAttribute.addTransientModifier(new AttributeModifier(modifierId, "Skill Tree Bonus", dynamicBonus, operation));
		}
	}

	private static double getMaximumLifePerEvasion(Player player) {
		var lifePerEvasion = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_PER_EVASION.get());
		var evasion = player.getAttributeValue(SkillTreeAttributes.EVASION_CHANCE.get()) - 1;
		return evasion * lifePerEvasion * 100;
	}

	private static double getMaximumLifePerGemstoneInArmor(Player player) {
		var lifePerGemstoneInArmor = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_PER_GEMSTONE_IN_ARMOR.get());
		var gemstonesInArmor = 0;
		for (var slot = 0; slot < 4; slot++) {
			var itemInSlot = player.getItemBySlot(EquipmentSlot.byTypeAndIndex(Type.ARMOR, slot));
			gemstonesInArmor += GemstoneItem.getGemstonesCount(itemInSlot);
		}
		return lifePerGemstoneInArmor * gemstonesInArmor;
	}

	private static double getMaximumLifePerGemstoneInHelmet(Player player) {
		var lifePerGemstoneInHelmet = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_PER_GEMSTONE_IN_HELMET.get());
		var helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		var gemstonesInHelmet = GemstoneItem.getGemstonesCount(helmet);
		return lifePerGemstoneInHelmet * gemstonesInHelmet;
	}

	private static double getArmorPerEvasion(Player player) {
		var armorPerEvasion = player.getAttributeValue(SkillTreeAttributes.ARMOR_PER_EVASION.get());
		var evasion = player.getAttributeValue(SkillTreeAttributes.EVASION_CHANCE.get()) - 1;
		return evasion * armorPerEvasion * 100;
	}

	private static double getArmorPerGemstoneInChestplate(Player player) {
		var armorPerGemstoneInChestplate = player.getAttributeValue(SkillTreeAttributes.ARMOR_PER_GEMSTONE_IN_CHESTPLATE.get());
		var getmstonesInChestplate = GemstoneItem.getGemstonesCount(player.getItemBySlot(EquipmentSlot.CHEST));
		return armorPerGemstoneInChestplate * getmstonesInChestplate;
	}

	private static double getArmorPerGemstoneInHelmet(Player player) {
		var armorPerGemstoneInHelmet = player.getAttributeValue(SkillTreeAttributes.ARMOR_PER_GEMSTONE_IN_HELMET.get());
		var getmstonesInHelmet = GemstoneItem.getGemstonesCount(player.getItemBySlot(EquipmentSlot.HEAD));
		return armorPerGemstoneInHelmet * getmstonesInHelmet;
	}

	private static double getDamagePerArmor(Player player) {
		var damagePerArmor = player.getAttributeValue(SkillTreeAttributes.ATTACK_DAMAGE_PER_ARMOR.get());
		var armor = player.getAttributeValue(Attributes.ARMOR);
		return damagePerArmor * armor;
	}

	private static double getMaximumLifePerArmor(Player player) {
		var maximumLifePerArmor = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_PER_ARMOR.get());
		var armor = player.getAttributeValue(Attributes.ARMOR);
		return maximumLifePerArmor * armor;
	}

	private static double getMaximumLifePerBootsArmor(Player player) {
		var maximumLifePerBootsArmor = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_PER_BOOTS_ARMOR.get());
		var boots = player.getItemBySlot(EquipmentSlot.FEET);
		var bootsArmor = new AtomicDouble();
		boots.getAttributeModifiers(EquipmentSlot.FEET).forEach((attribute, modifier) -> {
			if (attribute == Attributes.ARMOR && modifier.getOperation() == Operation.ADDITION) {
				bootsArmor.addAndGet(modifier.getAmount());
			}
		});
		if (bootsArmor.get() == 0) {
			return 0;
		}
		return bootsArmor.doubleValue() * maximumLifePerBootsArmor;
	}

	private static double getMaximumLifePerSatisfiedHunger(Player player) {
		var maximumLifePerSatisfiedHunger = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_PER_SATISFIED_HUNGER.get());
		var satisfiedHunger = player.getFoodData().getFoodLevel();
		return satisfiedHunger * maximumLifePerSatisfiedHunger;
	}

	private static double getEvasionUnderPotionEffect(Player player) {
		var evasionUnderPotionEffect = player.getAttributeValue(SkillTreeAttributes.EVASION_UNDER_POTION_EFFECT.get()) - 1;
		var hasPotionEffect = !player.getActiveEffects().isEmpty();
		return hasPotionEffect ? evasionUnderPotionEffect : 0;
	}

	private static double getAttackSpeedUnderPotionEffect(Player player) {
		var attackSpeedUnderPotionEffect = player.getAttributeValue(SkillTreeAttributes.ATTACK_SPEED_UNDER_POTION_EFFECT.get()) - 1;
		var hasPotionEffect = !player.getActiveEffects().isEmpty();
		return hasPotionEffect ? attackSpeedUnderPotionEffect : 0;
	}

	private static double getMaximumLifeUnderPotionEffect(Player player) {
		var maximumLifeUnderPotionEffect = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_UNDER_POTION_EFFECT.get());
		var hasPotionEffect = !player.getActiveEffects().isEmpty();
		return hasPotionEffect ? maximumLifeUnderPotionEffect : 0;
	}

	private static double getEvasionPerPotionEffect(Player player) {
		var evasionPerPotionEffect = player.getAttributeValue(SkillTreeAttributes.EVASION_PER_POTION_EFFECT.get()) - 1;
		var potionEffectCount = player.getActiveEffects().size();
		return potionEffectCount * evasionPerPotionEffect;
	}

	private static double getMaximumLifeWithEnchantedItem(Player player) {
		var maximumLifeWithEnchantedItem = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_WITH_ENCHANTED_ITEM.get());
		for (var slot = 0; slot < 6; slot++) {
			var isItemEnchanted = player.getItemBySlot(EquipmentSlot.values()[slot]).isEnchanted();
			if (isItemEnchanted) {
				return maximumLifeWithEnchantedItem;
			}
		}
		return 0D;
	}

	private static double getArmorWithEnchantedShield(Player player) {
		var offhandItem = player.getOffhandItem();
		if (!ItemHelper.isShield(offhandItem) || !offhandItem.isEnchanted()) {
			return 0D;
		}
		var armorWithEnchantedShield = player.getAttributeValue(SkillTreeAttributes.ARMOR_WITH_ENCHANTED_SHIELD.get());
		return armorWithEnchantedShield;
	}

	private static double getMaximumLifePerEnchantment(Player player) {
		var maximumLifePerEnchantment = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_PER_ENCHANTMENT.get());
		var enchantmentCount = 0;
		for (var slot : EquipmentSlot.values()) {
			var itemInSlot = player.getItemBySlot(slot);
			if (!itemInSlot.isEnchanted()) {
				continue;
			}
			enchantmentCount += itemInSlot.getAllEnchantments().size();
		}
		return maximumLifePerEnchantment * enchantmentCount;
	}

	private static double getArmorPerChestplateEnchantment(Player player) {
		var armorPerChestplateEnchantment = player.getAttributeValue(SkillTreeAttributes.ARMOR_PER_CHESTPLATE_ENCHANTMENT.get());
		var chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		var enchantmentCount = chestplate.getAllEnchantments().size();
		return armorPerChestplateEnchantment * enchantmentCount;
	}

	private static double getMaximumLifePerArmorEnchantment(Player player) {
		var maximumLifePerArmorEnchantment = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_PER_ARMOR_ENCHANTMENT.get());
		var enchantmentCount = 0;
		for (var slot = 0; slot < 4; slot++) {
			var itemInSlot = player.getItemBySlot(EquipmentSlot.byTypeAndIndex(Type.ARMOR, slot));
			if (!itemInSlot.isEnchanted()) {
				continue;
			}
			enchantmentCount += itemInSlot.getAllEnchantments().size();
		}
		return maximumLifePerArmorEnchantment * enchantmentCount;
	}

	private static double getAttackSpeedWithPickaxe(Player player) {
		if (!ItemHelper.isPickaxe(player.getMainHandItem())) {
			return 0;
		}
		var attackSpeedWithPickaxe = player.getAttributeValue(SkillTreeAttributes.ATTACK_SPEED_WITH_PICKAXE.get()) - 1;
		return attackSpeedWithPickaxe;
	}

	private static double getBlockChanceWithEnchantedShield(Player player) {
		var offhandItem = player.getOffhandItem();
		if (!ItemHelper.isShield(offhandItem) || !offhandItem.isEnchanted()) {
			return 0D;
		}
		var blockChanceWithEnchantedShield = player.getAttributeValue(SkillTreeAttributes.BLOCK_CHANCE_WITH_ENCHANTED_SHIELD.get()) - 1;
		return blockChanceWithEnchantedShield;
	}

	private static double getEvasionChanceWhenWounded(Player player) {
		var isWounded = player.getHealth() < player.getMaxHealth() / 2;
		if (!isWounded) {
			return 0D;
		}
		var evasionChanceWhenWounded = player.getAttributeValue(SkillTreeAttributes.EVASION_CHANCE_WHEN_WOUNDED.get()) - 1;
		return evasionChanceWhenWounded;
	}

	private static double getAttackSpeedIfNotHungry(Player player) {
		var isHungry = player.getFoodData().getFoodLevel() < 10;
		if (isHungry) {
			return 0D;
		}
		var attackSpeedIfNotHungry = player.getAttributeValue(SkillTreeAttributes.ATTACK_SPEED_IF_NOT_HUNGRY.get()) - 1;
		return attackSpeedIfNotHungry;
	}

	private static double getAttackSpeedWithBow(Player player) {
		var hasBow = ItemHelper.isBow(player.getMainHandItem());
		if (hasBow) {
			return 0D;
		}
		var attackSpeedWithBow = player.getAttributeValue(SkillTreeAttributes.ATTACK_SPEED_WITH_BOW.get()) - 1;
		return attackSpeedWithBow;
	}

	private static double getAttackSpeedPerGemstoneInWeapon(Player player) {
		var mainHandItem = player.getMainHandItem();
		if (!ItemHelper.isWeaponOrBow(mainHandItem)) {
			return 0D;
		}
		var gemstonesInWeapon = GemstoneItem.getGemstonesCount(mainHandItem);
		if (gemstonesInWeapon == 0) {
			return 0D;
		}
		var attackSpeedPerGemstoneInWeapon = player.getAttributeValue(SkillTreeAttributes.ATTACK_SPEED_PER_GEMSTONE_IN_WEAPON.get()) - 1;
		return attackSpeedPerGemstoneInWeapon * gemstonesInWeapon;
	}

	private static double getCritChancePerGemstoneInWeapon(Player player) {
		var mainHandItem = player.getMainHandItem();
		if (!ItemHelper.isWeaponOrBow(mainHandItem)) {
			return 0D;
		}
		var gemstonesInWeapon = GemstoneItem.getGemstonesCount(mainHandItem);
		if (gemstonesInWeapon == 0) {
			return 0D;
		}
		var critChancePerGemstoneInWeapon = player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_PER_GEMSTONE_IN_WEAPON.get()) - 1;
		return critChancePerGemstoneInWeapon * gemstonesInWeapon;
	}

	private static double getCritChanceWithBow(Player player) {
		var hasBow = ItemHelper.isBow(player.getMainHandItem());
		if (hasBow) {
			return 0D;
		}
		var critChanceWithBow = player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_WITH_BOW.get()) - 1;
		return critChanceWithBow;
	}

	private static double getCritChanceIfNotHungry(Player player) {
		var isHungry = player.getFoodData().getFoodLevel() < 10;
		if (isHungry) {
			return 0D;
		}
		var critChanceIfNotHungry = player.getAttributeValue(SkillTreeAttributes.CRIT_CHANCE_IF_NOT_HUNGRY.get()) - 1;
		return critChanceIfNotHungry;
	}

	private static double getAttackWithEnchantedWeapon(Player player) {
		var mainHandItem = player.getMainHandItem();
		if (!ItemHelper.isWeaponOrBow(mainHandItem) || !mainHandItem.isEnchanted()) {
			return 0D;
		}
		var attackWithEnchantedWeapon = player.getAttributeValue(SkillTreeAttributes.ATTACK_SPEED_WITH_ENCHANTED_WEAPON.get()) - 1;
		return attackWithEnchantedWeapon;
	}

	private static double getLifePerHitUnderPotionEffect(Player player) {
		var lifePerHitUnderPotionEffect = player.getAttributeValue(SkillTreeAttributes.LIFE_PER_HIT_UNDER_POTION_EFFECT.get());
		var hasPotionEffect = !player.getActiveEffects().isEmpty();
		return hasPotionEffect ? lifePerHitUnderPotionEffect : 0;
	}

	private static double getCritDamagePerGemstoneInWeapon(Player player) {
		var mainHandItem = player.getMainHandItem();
		if (!ItemHelper.isWeaponOrBow(mainHandItem)) {
			return 0D;
		}
		var gemstonesInWeapon = GemstoneItem.getGemstonesCount(mainHandItem);
		if (gemstonesInWeapon == 0) {
			return 0D;
		}
		var critDamagePerGemstoneInWeapon = player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_PER_GEMSTONE_IN_WEAPON.get()) - 1;
		return critDamagePerGemstoneInWeapon * gemstonesInWeapon;
	}

	private static double getLifeRegenerationPerGemstoneInHelmet(Player player) {
		var lifeRegenerationPerGemstoneInHelmet = player.getAttributeValue(SkillTreeAttributes.LIFE_REGENERATION_PER_GEMSTONE_IN_HELMET.get());
		var getmstonesInHelmet = GemstoneItem.getGemstonesCount(player.getItemBySlot(EquipmentSlot.HEAD));
		return lifeRegenerationPerGemstoneInHelmet * getmstonesInHelmet;
	}

	private static double getAttackSpeedWithShield(Player player) {
		var offhandItem = player.getOffhandItem();
		if (!ItemHelper.isShield(offhandItem)) {
			return 0D;
		}
		var attackSpeedWithShield = player.getAttributeValue(SkillTreeAttributes.ATTACK_SPEED_WITH_SHIELD.get()) - 1;
		return attackSpeedWithShield;
	}

	private static double getLifeRegenerationWithShield(Player player) {
		var offhandItem = player.getOffhandItem();
		if (!ItemHelper.isShield(offhandItem)) {
			return 0D;
		}
		var lifeRegenerationWithShield = player.getAttributeValue(SkillTreeAttributes.LIFE_REGENERATION_WITH_SHIELD.get());
		return lifeRegenerationWithShield;
	}

	private static double getBonusChestplateArmor(Player player) {
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
		var bonusChestplateArmor = player.getAttributeValue(SkillTreeAttributes.CHESTPLATE_ARMOR.get()) - 1;
		return bonusChestplateArmor * chestplateArmor.get();
	}

	private static double getLifeOnBlockPerShieldEnchantment(Player player) {
		var offhandItem = player.getOffhandItem();
		if (!ItemHelper.isShield(offhandItem)) {
			return 0D;
		}
		var enchantmentsCount = offhandItem.getAllEnchantments().size();
		if (enchantmentsCount == 0) {
			return 0D;
		}
		var lifeOnBlockPerShieldEnchantment = player.getAttributeValue(SkillTreeAttributes.LIFE_ON_BLOCK_PER_SHIELD_ENCHANTMENT.get());
		return lifeOnBlockPerShieldEnchantment * enchantmentsCount;
	}

	private static double getBlockChancePerShieldEnchantment(Player player) {
		var offhandItem = player.getOffhandItem();
		if (!ItemHelper.isShield(offhandItem)) {
			return 0D;
		}
		var enchantmentsCount = offhandItem.getAllEnchantments().size();
		if (enchantmentsCount == 0) {
			return 0D;
		}
		var blockChancePerShieldEnchantment = player.getAttributeValue(SkillTreeAttributes.BLOCK_CHANCE_PER_SHIELD_ENCHANTMENT.get()) - 1;
		return blockChancePerShieldEnchantment * enchantmentsCount;
	}

	private static double getBlockChanceIfNotHungry(Player player) {
		var isHungry = player.getFoodData().getFoodLevel() < 10;
		if (isHungry) {
			return 0D;
		}
		var blockChanceIfNotHungry = player.getAttributeValue(SkillTreeAttributes.BLOCK_CHANCE_IF_NOT_HUNGRY.get()) - 1;
		return blockChanceIfNotHungry;
	}

	private static double getLifeOnBlockIfNotHungry(Player player) {
		var isHungry = player.getFoodData().getFoodLevel() < 10;
		if (isHungry) {
			return 0D;
		}
		var lifeOnBlockIfNotHungry = player.getAttributeValue(SkillTreeAttributes.LIFE_ON_BLOCK_IF_NOT_HUNGRY.get());
		return lifeOnBlockIfNotHungry;
	}

	private static double getMaximumLifeIfNotHungry(Player player) {
		var isHungry = player.getFoodData().getFoodLevel() < 10;
		if (isHungry) {
			return 0D;
		}
		var maximumLifeIfNotHungry = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_IF_NOT_HUNGRY.get());
		return maximumLifeIfNotHungry;
	}

	private static double getBlockChancePerSatisfiedHunger(Player player) {
		var satisfiedHunger = player.getFoodData().getFoodLevel();
		if (satisfiedHunger == 0) {
			return 0D;
		}
		var blockChancePerSatisfiedHunger = player.getAttributeValue(SkillTreeAttributes.BLOCK_CHANCE_PER_SATISFIED_HUNGER.get()) - 1;
		return satisfiedHunger * blockChancePerSatisfiedHunger;
	}

	@SubscribeEvent
	public static void setCraftedArmorBonuses(ItemCraftedEvent event) {
		var itemStack = event.getCrafting();
		if (!ItemHelper.isArmor(itemStack)) {
			return;
		}
		var armorDefenceBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_ARMOR_DEFENCE.get()) - 1;
		if (armorDefenceBonus > 0) {
			ItemHelper.setDefenceBonus(itemStack, armorDefenceBonus);
		}
		var tougherArmorCraftingChance = event.getEntity().getAttributeValue(SkillTreeAttributes.CHANCE_TO_CRAFT_TOUGHER_ARMOR.get()) - 1;
		if (tougherArmorCraftingChance > 0) {
			var bonusToughness = Math.floor(tougherArmorCraftingChance);
			tougherArmorCraftingChance -= bonusToughness;
			if (event.getEntity().getRandom().nextFloat() < tougherArmorCraftingChance) {
				bonusToughness++;
			}
			ItemHelper.setToughnessBonus(itemStack, bonusToughness);
		}
		var armorEvasionBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_ARMOR_EVASION.get()) - 1;
		if (armorEvasionBonus > 0) {
			ItemHelper.setEvasionBonus(itemStack, armorEvasionBonus);
		}
		var helmetAdditionalGemstoneSlots = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_HELMETS_ADDITIONAL_GEMSTONE_SLOTS.get());
		if (ItemHelper.isHelmet(itemStack) && helmetAdditionalGemstoneSlots > 0) {
			GemstoneItem.setAdditionalGemstoneSlot(itemStack);
		}
	}

	@SubscribeEvent
	public static void applyArmorBonuses(ItemAttributeModifierEvent event) {
		if (ItemHelper.hasDefenceBonus(event.getItemStack())) {
			var defenceBonus = ItemHelper.getDefenceBonus(event.getItemStack());
			ItemHelper.applyBaseModifierBonus(event, Attributes.ARMOR, d -> d * (1 + defenceBonus));
		}
		if (ItemHelper.hasToughnessBonus(event.getItemStack())) {
			var toughnessBonus = ItemHelper.getToughnessBonus(event.getItemStack());
			ItemHelper.applyBaseModifierBonus(event, Attributes.ARMOR_TOUGHNESS, d -> d + toughnessBonus);
		}
		if (event.getItemStack().getItem() instanceof ArmorItem armorItem) {
			if (ItemHelper.hasEvasionBonus(event.getItemStack()) && event.getSlotType() == armorItem.getSlot()) {
				var modifierIds = new String[] { "845DB27C-C624-495F-8C9F-6020A9A58B6B", "D8499B04-0E66-4726-AB29-64469D734E0D", "9F3D476D-C118-4544-8365-64846904B48E", "2AD3F246-FEE1-4E67-B886-69FD380BB150" };
				var evasionBonus = ItemHelper.getEvasionBonus(event.getItemStack());
				var modifierId = UUID.fromString(modifierIds[event.getSlotType().getIndex()]);
				event.addModifier(SkillTreeAttributes.EVASION_CHANCE.get(), new AttributeModifier(modifierId, "Crafted Armor Bonus", evasionBonus, Operation.MULTIPLY_BASE));
			}
		}
	}

	@SubscribeEvent
	public static void setCraftedShieldBonuses(ItemCraftedEvent event) {
		var itemStack = event.getCrafting();
		if (!ItemHelper.isShield(itemStack)) {
			return;
		}
		var shieldArmorBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_SHIELDS_ARMOR_BONUS.get());
		if (shieldArmorBonus > 0) {
			ItemHelper.setDefenceBonus(itemStack, shieldArmorBonus);
		}
	}

	@SubscribeEvent
	public static void applyShieldAttributeBonuses(ItemAttributeModifierEvent event) {
		if (!ItemHelper.isShield(event.getItemStack())) {
			return;
		}
		if (event.getSlotType() != EquipmentSlot.OFFHAND) {
			return;
		}
		if (ItemHelper.hasDefenceBonus(event.getItemStack())) {
			var armorBonus = ItemHelper.getDefenceBonus(event.getItemStack());
			var modifierId = UUID.fromString("7174b3ef-e310-4b60-9535-eeddc741fcf5");
			var modifier = new AttributeModifier(modifierId, "Skill Tree Bonus", armorBonus, Operation.ADDITION);
			event.addModifier(Attributes.ARMOR, modifier);
		}
	}

	@SubscribeEvent
	public static void setCraftedWeaponBonuses(ItemCraftedEvent event) {
		var itemStack = event.getCrafting();
		if (!ItemHelper.isWeapon(itemStack)) {
			return;
		}
		var weaponDamageBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_WEAPON_DAMAGE_BONUS.get());
		if (weaponDamageBonus > 0) {
			ItemHelper.setDamageBonus(itemStack, weaponDamageBonus);
		}
		var weaponAttackSpeedBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_WEAPON_ATTACK_SPEED.get()) - 1;
		if (weaponAttackSpeedBonus > 0) {
			ItemHelper.setAttackSpeedBonus(itemStack, weaponAttackSpeedBonus);
		}
	}

	@SubscribeEvent
	public static void applyWeaponAttributeBonuses(ItemAttributeModifierEvent event) {
		if (!ItemHelper.isWeapon(event.getItemStack())) {
			return;
		}
		if (ItemHelper.hasDamageBonus(event.getItemStack())) {
			var damageBonus = ItemHelper.getDamageBonus(event.getItemStack());
			ItemHelper.applyBaseModifierBonus(event, Attributes.ATTACK_DAMAGE, d -> d + damageBonus);
		}
		if (ItemHelper.hasAttackSpeedBonus(event.getItemStack())) {
			var attackSpeedBonus = ItemHelper.getAttackSpeedBonus(event.getItemStack());
			ItemHelper.applyBaseModifierBonus(event, Attributes.ATTACK_SPEED, d -> {
				var basePlayerAttackSpeed = 4;
				var oldAttackSpeed = basePlayerAttackSpeed + d;
				var newAttackSpeed = oldAttackSpeed * (1 + attackSpeedBonus);
				return newAttackSpeed - basePlayerAttackSpeed;
			});
		}
	}

	@SubscribeEvent
	public static void applyBreakSpeedBonus(PlayerEvent.BreakSpeed event) {
		if (ItemHelper.isPickaxe(event.getEntity().getMainHandItem())) {
			var miningSpeedMultiplier = (float) event.getEntity().getAttributeValue(SkillTreeAttributes.MINING_SPEED.get());
			event.setNewSpeed(event.getOriginalSpeed() * miningSpeedMultiplier);
		}
	}

	@SubscribeEvent
	public static void applyGemstoneFindingChanceBonus(BlockEvent.BreakEvent event) {
		var player = event.getPlayer();
		var level = player.getLevel();
		if (level.isClientSide) {
			return;
		}
		var gemstoneFindingChance = Config.COMMON_CONFIG.getGemstoneDropChance();
		gemstoneFindingChance += player.getAttributeValue(SkillTreeAttributes.CHANCE_TO_FIND_GEMSTONE.get()) - 1;
		if (gemstoneFindingChance == 0) {
			return;
		}
		var blockPos = event.getPos();
		if (!level.getBlockState(blockPos).is(Tags.Blocks.ORES)) {
			return;
		}
		if (player.getRandom().nextFloat() >= gemstoneFindingChance) {
			return;
		}
		if (!ForgeHooks.isCorrectToolForDrops(event.getState(), player)) {
			return;
		}
		var gemstones = new Item[] { SkillTreeItems.LIGHT_GEMSTONE.get(), SkillTreeItems.SOOTHING_GEMSTONE.get(), SkillTreeItems.STURDY_GEMSTONE.get(), SkillTreeItems.RAINBOW_GEMSTONE.get() };
		var foundGemstone = gemstones[player.getRandom().nextInt(gemstones.length)];
		if (player.getRandom().nextFloat() < 0.1) {
			foundGemstone = SkillTreeItems.VOID_GEMSTONE.get();
		}
		Block.popResource(level, blockPos, new ItemStack(foundGemstone));
	}

	@SubscribeEvent
	public static void applyLifeRegenerationBonus(PlayerTickEvent event) {
		if (event.phase == Phase.END || event.player.level.isClientSide) {
			return;
		}
		if (event.player.getFoodData().getFoodLevel() == 0) {
			return;
		}
		var lifeRegeneration = (float) event.player.getAttributeValue(SkillTreeAttributes.LIFE_REGENERATION.get());
		if (event.player.getHealth() != event.player.getMaxHealth() && event.player.tickCount % 20 == 0) {
			event.player.heal(lifeRegeneration);
			event.player.getFoodData().addExhaustion(lifeRegeneration / 5);
		}
	}

	@SubscribeEvent
	public static void applyEvasionBonus(LivingAttackEvent event) {
		if (event.getEntity() instanceof Player player) {
			var evasionChance = (float) player.getAttributeValue(SkillTreeAttributes.EVASION_CHANCE.get()) - 1;
			if (evasionChance == 0)
				return;
			var canEvade = PlayerHelper.canEvadeDamage(event.getSource());
			if (canEvade && player.getRandom().nextFloat() < evasionChance)
				event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void applyLootDuplicationChanceBonus(LivingDropsEvent event) {
		if (!(event.getSource().getEntity() instanceof Player)) {
			return;
		}
		var player = (Player) event.getSource().getEntity();
		var doubleLootChance = player.getAttributeValue(SkillTreeAttributes.DOUBLE_LOOT_CHANCE.get()) - 1;
		var tripleLootChance = player.getAttributeValue(SkillTreeAttributes.TRIPLE_LOOT_CHANCE.get()) - 1;
		if (doubleLootChance == 0 && tripleLootChance == 0) {
			return;
		}
		var lootTripled = player.getRandom().nextFloat() < tripleLootChance;
		var lootDoubled = player.getRandom().nextFloat() < doubleLootChance;
		if (!lootTripled && !lootDoubled) {
			return;
		}
		var additionalDrops = event.getDrops().stream().map(itemEntity -> new ItemEntity(itemEntity.level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), itemEntity.getItem().copy())).toList();
		event.getDrops().addAll(additionalDrops);
		if (!lootTripled) {
			return;
		}
		additionalDrops = event.getDrops().stream().map(itemEntity -> new ItemEntity(itemEntity.level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), itemEntity.getItem().copy())).toList();
		event.getDrops().addAll(additionalDrops);
	}

	@SubscribeEvent
	public static void setCraftedBowsBonuses(ItemCraftedEvent event) {
		var itemStack = event.getCrafting();
		if (!ItemHelper.isBow(itemStack)) {
			return;
		}
		var bowChargeSpeedBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_BOWS_ATTACK_SPEED.get()) - 1;
		if (bowChargeSpeedBonus > 0) {
			ItemHelper.setAttackSpeedBonus(itemStack, bowChargeSpeedBonus);
		}
		var bowAdditionalGemstoneSlots = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_BOWS_ADDITIONAL_GEMSTONE_SLOTS.get());
		if (bowAdditionalGemstoneSlots > 0) {
			GemstoneItem.setAdditionalGemstoneSlot(itemStack);
		}
	}

	@SubscribeEvent
	public static void applyBowAttributeBonuses(ItemAttributeModifierEvent event) {
		if (!ItemHelper.isBow(event.getItemStack()) || event.getSlotType() != EquipmentSlot.MAINHAND) {
			return;
		}
		// Item.BASE_ATTACK_SPEED_UUID
		var modifierId = (UUID) ObfuscationReflectionHelper.getPrivateValue(Item.class, null, "f_41375_");
		var modifierAmount = -3F;
		if (ItemHelper.hasAttackSpeedBonus(event.getItemStack())) {
			var attackSpeedBonus = ItemHelper.getAttackSpeedBonus(event.getItemStack());
			modifierAmount += attackSpeedBonus;
		}
		var modifier = new AttributeModifier(modifierId, "Base Attack Speed", modifierAmount, Operation.ADDITION);
		event.addModifier(Attributes.ATTACK_SPEED, modifier);
	}

	@SubscribeEvent
	public static void applyArrowDamageBonus(LivingHurtEvent event) {
		if (!(event.getSource().getDirectEntity() instanceof AbstractArrow))
			return;
		if (!(event.getSource().getEntity() instanceof Player))
			return;
		var player = (Player) event.getSource().getEntity();
		var arrow = (AbstractArrow) event.getSource().getDirectEntity();
		arrow.setCritArrow(false);
		var damageBonus = PlayerHelper.getFlatArrowDamageBonus(player);
		var damageMultiplier = PlayerHelper.getArrowDamageMultiplier(player, event.getEntity(), arrow);
		var damage = (event.getAmount() + damageBonus) * damageMultiplier;
		event.setAmount(damage);
	}

	@SubscribeEvent
	public static void setCraftedFoodBonuses(ItemCraftedEvent event) {
		var craftedItem = event.getCrafting();
		if (!ItemHelper.isFood(craftedItem)) {
			return;
		}
		var player = event.getEntity();
		FoodHelper.setCraftedFoodBonuses(craftedItem, player);
	}

	@SubscribeEvent
	public static void setCraftedFoodBonuses(ItemSmeltedEvent event) {
		var craftedItem = event.getSmelting();
		if (!ItemHelper.isFood(craftedItem)) {
			return;
		}
		var player = event.getEntity();
		FoodHelper.setCraftedFoodBonuses(craftedItem, player);
	}

	@SubscribeEvent
	public static void applyCraftedFoodBonuses(LivingEntityUseItemEvent.Finish event) {
		if (!ItemHelper.isFood(event.getItem())) {
			return;
		}
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		var player = (Player) event.getEntity();
		var restoration = event.getItem().getFoodProperties(player).getNutrition();
		var finalRestoration = restoration;
		if (FoodHelper.hasRestorationBonus(event.getItem())) {
			var restorationBonus = FoodHelper.getRestorationBonus(event.getItem());
			finalRestoration += restoration * restorationBonus;
			player.getFoodData().eat((int) (restoration * restorationBonus), 0F);
		}
		if (FoodHelper.hasHealingBonus(event.getItem())) {
			var healingBonus = FoodHelper.getHealingBonus(event.getItem());
			player.heal(healingBonus * finalRestoration);
		}
		if (FoodHelper.hasDamageBonus(event.getItem())) {
			var damageBonus = FoodHelper.getDamageBonus(event.getItem());
			var effectAmplifier = (int) (damageBonus * finalRestoration * 100);
			var damageEffect = new MobEffectInstance(SkillTreeEffects.DAMAGE_BONUS.get(), 20 * 60, effectAmplifier);
			player.addEffect(damageEffect);
		}
		if (FoodHelper.hasLifeRegenerationBonus(event.getItem())) {
			var lifeRegenerationBonus = FoodHelper.getLifeRegenerationBonus(event.getItem());
			var effectAmplifier = (int) (lifeRegenerationBonus * 100);
			var lifeRegenerationEffect = new MobEffectInstance(SkillTreeEffects.LIFE_REGENERATION_BONUS.get(), 20 * 60, effectAmplifier);
			player.addEffect(lifeRegenerationEffect);
		}
		if (FoodHelper.hasCritDamageBonus(event.getItem())) {
			var critDamageBonus = FoodHelper.getCritDamageBonus(event.getItem());
			var effectAmplifier = (int) (critDamageBonus * finalRestoration * 100);
			var critDamageEffect = new MobEffectInstance(SkillTreeEffects.CRIT_DAMAGE_BONUS.get(), 20 * 60, effectAmplifier);
			player.addEffect(critDamageEffect);
		}
	}

	@SubscribeEvent
	public static void addFoodBonusesTooltips(ItemTooltipEvent event) {
		if (!ItemHelper.isFood(event.getItemStack())) {
			return;
		}
		var restoration = event.getItemStack().getFoodProperties(event.getEntity()).getNutrition();
		var finalRestoration = restoration;
		if (FoodHelper.hasRestorationBonus(event.getItemStack())) {
			var restorationBonus = FoodHelper.getRestorationBonus(event.getItemStack());
			finalRestoration += restoration * restorationBonus;
		}
		if (FoodHelper.hasDamageBonus(event.getItemStack())) {
			var damagePerRestorationBonus = FoodHelper.getDamageBonus(event.getItemStack());
			event.getToolTip().add(Component.translatable("food.bonus.damage", (int) (damagePerRestorationBonus * finalRestoration * 100)).withStyle(ChatFormatting.BLUE));
		}
		if (FoodHelper.hasCritDamageBonus(event.getItemStack())) {
			var critDamageBonus = FoodHelper.getCritDamageBonus(event.getItemStack());
			event.getToolTip().add(Component.translatable("food.bonus.crit_damage", (int) (critDamageBonus * finalRestoration * 100)).withStyle(ChatFormatting.BLUE));
		}
		if (FoodHelper.hasLifeRegenerationBonus(event.getItemStack())) {
			var lifeRegenerationBonus = FoodHelper.getLifeRegenerationBonus(event.getItemStack());
			var formattedBonus = String.format("%.1f", lifeRegenerationBonus);
			formattedBonus = formattedBonus.replace(".0", "");
			event.getToolTip().add(Component.translatable("food.bonus.life_regeneration", formattedBonus).withStyle(ChatFormatting.BLUE));
		}
	}

	@SubscribeEvent
	public static void applyBlockChanceBonus(LivingAttackEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		if (event.getAmount() <= 0) {
			return;
		}
		if (event.getSource().isBypassArmor()) {
			return;
		}
		if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow && arrow.getPierceLevel() > 0) {
			return;
		}
		var player = (Player) event.getEntity();
		if (!ItemHelper.isShield(player.getOffhandItem())) {
			return;
		}
		var blockChance = player.getAttributeValue(SkillTreeAttributes.BLOCK_CHANCE.get()) - 1;
		if (player.getRandom().nextFloat() >= blockChance) {
			return;
		}
		var shieldBlockEvent = ForgeHooks.onShieldBlock(player, event.getSource(), event.getAmount());
		if (shieldBlockEvent.isCanceled()) {
			return;
		}
		event.setCanceled(true);
		player.level.broadcastEntityEvent(player, (byte) 29);
		if (shieldBlockEvent.shieldTakesDamage()) {
			var hurtCurrentlyUsedShieldMethod = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "m_7909_", float.class);
			try {
				hurtCurrentlyUsedShieldMethod.invoke(player, event.getAmount());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		if (event.getSource().isProjectile()) {
			return;
		}
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
	public static void applyHealingOnBlockBonus(ShieldBlockEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		var player = (Player) event.getEntity();
		var healingOnBlock = player.getAttributeValue(SkillTreeAttributes.LIFE_ON_BLOCK.get());
		if (healingOnBlock == 0) {
			return;
		}
		player.heal((float) healingOnBlock);
	}

	@SubscribeEvent
	public static void applyPoisonedWeaponEffects(LivingHurtEvent event) {
		if (!(event.getSource().getDirectEntity() instanceof Player)) {
			return;
		}
		var player = (Player) event.getSource().getDirectEntity();
		var weapon = player.getMainHandItem();
		if (!ItemHelper.hasPoisons(weapon)) {
			return;
		}
		var poisons = ItemHelper.getPoisons(weapon);
		var target = event.getEntity();
		poisons.stream().map(MobEffectInstance::new).forEach(target::addEffect);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void addPoisonedWeaponTooltips(ItemTooltipEvent event) {
		var weapon = event.getItemStack();
		if (!ItemHelper.hasPoisons(weapon)) {
			return;
		}
		event.getToolTip().add(Component.empty());
		event.getToolTip().add(Component.translatable("weapon.poisoned").withStyle(ChatFormatting.DARK_PURPLE));
		var poisons = ItemHelper.getPoisons(weapon);
		poisons.stream().map(TooltipHelper::getEffectTooltip).forEach(event.getToolTip()::add);
	}

	@SubscribeEvent
	public static void applyLifePerHitBonus(LivingHurtEvent event) {
		var directAttacker = event.getSource().getDirectEntity();
		var indirectAttacker = event.getSource().getEntity();
		var player = (Player) null;
		if (directAttacker instanceof Player) {
			player = (Player) directAttacker;
		} else if (directAttacker instanceof AbstractArrow && indirectAttacker instanceof Player) {
			player = (Player) indirectAttacker;
		}
		if (player == null) {
			return;
		}
		var lifePerHit = (float) player.getAttributeValue(SkillTreeAttributes.LIFE_PER_HIT.get());
		if (directAttacker instanceof AbstractArrow) {
			lifePerHit += player.getAttributeValue(SkillTreeAttributes.LIFE_PER_ARROW_HIT.get());
		}
		player.heal(lifePerHit);
	}

	@SubscribeEvent
	public static void applyChanceToRetrieveArrowsBonus(LivingHurtEvent event) {
		var directAttacker = event.getSource().getDirectEntity();
		var indirectAttacker = event.getSource().getEntity();
		var player = (Player) null;
		var arrow = (AbstractArrow) null;
		if (directAttacker instanceof AbstractArrow && indirectAttacker instanceof Player) {
			player = (Player) indirectAttacker;
			arrow = (AbstractArrow) directAttacker;
		}
		if (player == null) {
			return;
		}
		var chanceToRetrieveArrows = (float) player.getAttributeValue(SkillTreeAttributes.CHANCE_TO_RETRIEVE_ARROWS.get()) - 1;
		if (player.getRandom().nextFloat() >= chanceToRetrieveArrows) {
			return;
		}
		var target = event.getEntity();
		var targetData = target.getPersistentData();
		var stuckArrowsData = targetData.getList("StuckArrows", new CompoundTag().getId());
		ItemStack arrowStack;
		try {
			// AbstractArrow.getPickupItem
			arrowStack = (ItemStack) ObfuscationReflectionHelper.findMethod(AbstractArrow.class, "m_7941_").invoke(arrow);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return;
		}
		stuckArrowsData.add(arrowStack.save(new CompoundTag()));
		targetData.put("StuckArrows", stuckArrowsData);
	}

	@SubscribeEvent
	public static void retrieveArrows(LivingDeathEvent event) {
		var entity = event.getEntity();
		var entityData = entity.getPersistentData();
		var stuckArrowsData = entityData.getList("StuckArrows", new CompoundTag().getId());
		if (stuckArrowsData.isEmpty()) {
			return;
		}
		stuckArrowsData.stream().map(CompoundTag.class::cast).map(ItemStack::of).forEach(entity::spawnAtLocation);
	}
}
