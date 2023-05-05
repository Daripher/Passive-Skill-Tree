package daripher.skilltree.attribute;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.function.Function;

import com.google.common.util.concurrent.AtomicDouble;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeEffects;
import daripher.skilltree.init.SkillTreeItems;
import daripher.skilltree.item.GemstoneItem;
import daripher.skilltree.util.FoodHelper;
import daripher.skilltree.util.ItemHelper;
import daripher.skilltree.util.PlayerHelper;
import net.minecraft.ChatFormatting;
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
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
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
			event.setAmount((event.getAmount() + damageBonus) * damageMultiplier);
		}
	}

	@SubscribeEvent
	public static void applyCritBonus(CriticalHitEvent event) {
		if (event.isVanillaCritical()) {
			return;
		}
		if (event.getTarget() instanceof LivingEntity target) {
			var critChance = PlayerHelper.getCritChance(event.getEntity(), target);
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
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "d1f7e78b-3368-409c-aa89-90f0f89a5524", AttributeBonusHandler::getMaxLifePerEvasion);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "b68181bd-fbc4-4a63-95d4-df386fe3f71f", AttributeBonusHandler::getMaxLifePerGemstoneInArmor);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "7cb71ee5-8715-40ae-a877-72ec3b49b33e", AttributeBonusHandler::getArmorPerEvasion);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "66eae15c-53eb-4a4a-b511-2ab94f81324b", AttributeBonusHandler::getArmorPerGemstoneInChestplate);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "1080308c-bdd4-4693-876c-a36390b66b73", AttributeBonusHandler::getArmorPerGemstoneInHelmet);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "ddf82a22-6ae5-46f8-8acb-452949cbd603", AttributeBonusHandler::getArmorPerChestplateArmor);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_DAMAGE, Operation.ADDITION, "d1079882-dd8c-42b7-9a43-3928553193c8", AttributeBonusHandler::getDamagePerArmor);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "9199d7cf-c7e4-4123-b636-6f6591e1137d", AttributeBonusHandler::getMaximumLifePerArmor);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "8810227f-9798-4890-8400-91c0941a3fc0", AttributeBonusHandler::getMaximumLifePerBootsArmor);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "579a74e0-a2fd-4074-bb7b-cbaa646cf847", AttributeBonusHandler::getMaximumLifeIfAteRecently);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "98a17cd0-68c8-4808-8981-1796c33295e7", AttributeBonusHandler::getMaximumLifePerSatisfiedHunger);
		applyDynamicAttributeBonus(player, SkillTreeAttributes.EVASION_CHANCE_MULTIPLIER.get(), Operation.ADDITION, "4aa87d74-b729-4e1d-9c76-893495050416", AttributeBonusHandler::getEvasionUnderPotionEffect);
		applyDynamicAttributeBonus(player, Attributes.ATTACK_SPEED, Operation.ADDITION, "a4daf7f8-29e3-404d-8277-9215a16ef4c8", AttributeBonusHandler::getAttackSpeedUnderPotionEffect);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "de712f9d-9f47-475c-8b86-188bca70d1df", AttributeBonusHandler::getMaximumLifeUnderPotionEffect);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "282c4f81-7b6d-48e0-82c9-c4ebd58265cb", AttributeBonusHandler::getMaximumLifePerPotionEffect);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "16c35c5c-56da-4d21-ad56-bd6618fee711", AttributeBonusHandler::getMaximumLifeWithEnchantedItem);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "8b836bea-4c28-4430-8184-7330530239f6", AttributeBonusHandler::getArmorWithEnchantedShield);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "27b4644b-96a0-4443-89e5-1700af61d602", AttributeBonusHandler::getMaximumLifePerEnchantment);
		applyDynamicAttributeBonus(player, Attributes.ARMOR, Operation.ADDITION, "55c3cb58-c09e-465a-a812-6a18ae587ec0", AttributeBonusHandler::getArmorPerChestplateEnchantment);
		applyDynamicAttributeBonus(player, Attributes.MAX_HEALTH, Operation.ADDITION, "9b1e9aac-fa58-4343-ba88-7541eca2836f", AttributeBonusHandler::getMaximumLifePerArmorEnchantment);
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

	private static double getMaxLifePerEvasion(Player player) {
		var lifePerEvasion = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_PER_EVASION.get());
		var evasion = player.getAttributeValue(SkillTreeAttributes.EVASION_CHANCE_MULTIPLIER.get()) - 1;
		return evasion * lifePerEvasion * 100;
	}

	private static double getMaxLifePerGemstoneInArmor(Player player) {
		var lifePerGemstoneInArmor = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_PER_GEMSTONE_IN_ARMOR.get());
		var gemstonesInArmor = 0;
		for (var slot = 0; slot < 4; slot++) {
			var itemInSlot = player.getItemBySlot(EquipmentSlot.byTypeAndIndex(Type.ARMOR, slot));
			gemstonesInArmor += GemstoneItem.getGemstonesCount(itemInSlot);
		}
		return lifePerGemstoneInArmor * gemstonesInArmor;
	}

	private static double getArmorPerEvasion(Player player) {
		var armorPerEvasion = player.getAttributeValue(SkillTreeAttributes.ARMOR_PER_EVASION.get());
		var evasion = player.getAttributeValue(SkillTreeAttributes.EVASION_CHANCE_MULTIPLIER.get()) - 1;
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

	private static double getArmorPerChestplateArmor(Player player) {
		var chestplateArmorBonus = player.getAttributeValue(SkillTreeAttributes.CHESTPLATE_ARMOR_MULTIPLIER.get()) - 1;
		var chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		var chestplateArmorModifiers = chestplate.getAttributeModifiers(EquipmentSlot.CHEST).get(Attributes.ARMOR).stream().filter(modifier -> modifier.getOperation() == Operation.ADDITION);
		var chestplateArmor = new AtomicDouble();
		chestplateArmorModifiers.forEach(modifier -> chestplateArmor.addAndGet(modifier.getAmount()));
		return chestplateArmor.doubleValue() * chestplateArmorBonus;
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
		var bootsArmorModifiers = boots.getAttributeModifiers(EquipmentSlot.CHEST).get(Attributes.ARMOR).stream().filter(modifier -> modifier.getOperation() == Operation.ADDITION);
		var bootsArmor = new AtomicDouble();
		bootsArmorModifiers.forEach(modifier -> bootsArmor.addAndGet(modifier.getAmount()));
		return bootsArmor.doubleValue() * maximumLifePerBootsArmor;
	}

	private static double getMaximumLifeIfAteRecently(Player player) {
		var maximumLifeIfAteRecently = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_IF_ATE_RECENTLY.get());
		return player.hasEffect(SkillTreeEffects.DELICACY.get()) ? maximumLifeIfAteRecently : 0;
	}

	private static double getMaximumLifePerSatisfiedHunger(Player player) {
		var maximumLifePerSatisfiedHunger = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_PER_SATISFIED_HUNGER.get());
		var satisfiedHunger = player.getFoodData().getFoodLevel();
		return satisfiedHunger * maximumLifePerSatisfiedHunger;
	}

	private static double getEvasionUnderPotionEffect(Player player) {
		var evasionUnderPotionEffect = player.getAttributeValue(SkillTreeAttributes.EVASION_UNDER_POTION_EFFECT_MULTIPLIER.get()) - 1;
		var hasPotionEffect = !player.getActiveEffects().isEmpty();
		return hasPotionEffect ? evasionUnderPotionEffect : 0;
	}

	private static double getAttackSpeedUnderPotionEffect(Player player) {
		var attackSpeedUnderPotionEffect = player.getAttributeValue(SkillTreeAttributes.ATTACK_SPEED_UNDER_POTION_EFFECT_MULTIPLIER.get()) - 1;
		var hasPotionEffect = !player.getActiveEffects().isEmpty();
		return hasPotionEffect ? attackSpeedUnderPotionEffect : 0;
	}

	private static double getMaximumLifeUnderPotionEffect(Player player) {
		var maximumLifeUnderPotionEffect = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_UNDER_POTION_EFFECT.get());
		var hasPotionEffect = !player.getActiveEffects().isEmpty();
		return hasPotionEffect ? maximumLifeUnderPotionEffect : 0;
	}

	private static double getMaximumLifePerPotionEffect(Player player) {
		var maximumLifePerPotionEffect = player.getAttributeValue(SkillTreeAttributes.MAXIMUM_LIFE_PER_POTION_EFFECT.get());
		var potionEffectCount = player.getActiveEffects().size();
		return potionEffectCount * maximumLifePerPotionEffect;
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

	@SubscribeEvent
	public static void applyAttackSpeedBonus(LivingEquipmentChangeEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		var player = (Player) event.getEntity();
		var attackSpeedBonus = PlayerHelper.getAttackSpeedBonus(player);
		var modifierId = UUID.fromString("030cf99a-09f8-4e40-a858-56b9d588b445");
		if (attackSpeedBonus > 0) {
			var attackSpeedAttribute = player.getAttribute(Attributes.ATTACK_SPEED);
			var oldModifier = attackSpeedAttribute.getModifier(modifierId);
			if (oldModifier != null) {
				if (oldModifier.getAmount() == attackSpeedBonus) {
					return;
				}
				attackSpeedAttribute.removeModifier(modifierId);
			}
			attackSpeedAttribute.addTransientModifier(new AttributeModifier(modifierId, "Skill Tree Bonus", attackSpeedBonus, Operation.MULTIPLY_TOTAL));
		}
	}

	@SubscribeEvent
	public static void setCraftedArmorBonuses(ItemCraftedEvent event) {
		if (!ItemHelper.isArmor(event.getCrafting())) {
			return;
		}
		var armorDefenceBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_ARMOR_DEFENCE_MULTIPLIER.get()) - 1;
		if (armorDefenceBonus > 0) {
			ItemHelper.setDefenceBonus(event.getCrafting(), armorDefenceBonus);
		}
		var tougherArmorCraftingChance = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_ARMOR_BONUS_TOUGHNESS_CHANCE_MULTIPLIER.get()) - 1;
		if (tougherArmorCraftingChance > 0) {
			var bonusToughness = Math.floor(tougherArmorCraftingChance);
			tougherArmorCraftingChance -= bonusToughness;
			if (event.getEntity().getRandom().nextFloat() < tougherArmorCraftingChance) {
				bonusToughness++;
			}
			ItemHelper.setToughnessBonus(event.getCrafting(), bonusToughness);
		}
		var armorEvasionBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_ARMOR_EVASION_BONUS.get());
		if (armorEvasionBonus > 0) {
			ItemHelper.setEvasionBonus(event.getCrafting(), armorEvasionBonus);
		}
		var helmetAdditionalGemstoneSlots = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_HELMETS_ADDITIONAL_GEMSTONE_SLOTS.get());
		if (ItemHelper.isHelmet(event.getCrafting()) && helmetAdditionalGemstoneSlots > 0) {
			GemstoneItem.setAdditionalGemstoneSlot(event.getCrafting());
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
				event.addModifier(SkillTreeAttributes.EVASION_CHANCE_MULTIPLIER.get(), new AttributeModifier(modifierId, "Crafted Armor Bonus", evasionBonus, Operation.MULTIPLY_BASE));
			}
		}
	}

	@SubscribeEvent
	public static void setCraftedWeaponAttributeBonuses(ItemCraftedEvent event) {
		if (!ItemHelper.isWeapon(event.getCrafting())) {
			return;
		}
		var weaponDamageBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_WEAPON_DAMAGE_BONUS.get());
		if (weaponDamageBonus > 0) {
			ItemHelper.setDamageBonus(event.getCrafting(), weaponDamageBonus);
		}
		var weaponAttackSpeedBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_WEAPON_ATTACK_SPEED_MULTIPLIER.get()) - 1;
		if (weaponAttackSpeedBonus > 0) {
			ItemHelper.setAttackSpeedBonus(event.getCrafting(), weaponAttackSpeedBonus);
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
			var miningSpeedMultiplier = (float) event.getEntity().getAttributeValue(SkillTreeAttributes.MINING_SPEED_MULTIPLIER.get());
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
		var gemstoneFindingChance = player.getAttributeValue(SkillTreeAttributes.GEMSTONE_FINDING_CHANCE.get());
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
		if (event.phase == Phase.END || event.player.level.isClientSide)
			return;
		if (event.player.getFoodData().getFoodLevel() == 0)
			return;
		var lifeRegeneration = (float) event.player.getAttributeValue(SkillTreeAttributes.LIFE_REGENERATION_BONUS.get());
		if (event.player.getHealth() != event.player.getMaxHealth() && event.player.tickCount % 20 == 0) {
			event.player.heal(lifeRegeneration);
			event.player.getFoodData().addExhaustion(lifeRegeneration / 5);
		}
	}

	@SubscribeEvent
	public static void applyEvasionBonus(LivingAttackEvent event) {
		if (event.getEntity() instanceof Player player) {
			var evasionChance = (float) player.getAttributeValue(SkillTreeAttributes.EVASION_CHANCE_MULTIPLIER.get()) - 1;
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
		var doubleLootChance = player.getAttributeValue(SkillTreeAttributes.DOUBLE_LOOT_CHANCE.get());
		var tripleLootChance = player.getAttributeValue(SkillTreeAttributes.TRIPLE_LOOT_CHANCE.get());
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
	public static void setCraftedBowsAttributeBonuses(ItemCraftedEvent event) {
		if (!ItemHelper.isBow(event.getCrafting())) {
			return;
		}
		var bowChargeSpeedBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_BOWS_CHARGE_SPEED_BONUS.get());
		if (bowChargeSpeedBonus > 0) {
			ItemHelper.setAttackSpeedBonus(event.getCrafting(), bowChargeSpeedBonus);
		}
	}

	@SubscribeEvent
	public static void applyBowAttributeBonuses(ItemAttributeModifierEvent event) {
		if (!ItemHelper.isBow(event.getItemStack()) || event.getSlotType() != EquipmentSlot.MAINHAND) {
			return;
		}
		if (ItemHelper.hasAttackSpeedBonus(event.getItemStack())) {
			var attackSpeedBonus = ItemHelper.getAttackSpeedBonus(event.getItemStack());
			var modifierId = UUID.fromString("0cf2b159-b953-4c96-a84e-d380f6a74031");
			var attackSpeedModifier = new AttributeModifier(modifierId, "Crafted Bow Bonus", attackSpeedBonus, Operation.MULTIPLY_BASE);
			event.addModifier(SkillTreeAttributes.BOW_CHARGE_SPEED_MULTIPLIER.get(), attackSpeedModifier);
		}
	}

	@SubscribeEvent
	public static void applyArrowDamageBonuses(LivingHurtEvent event) {
		if (!(event.getSource().getDirectEntity() instanceof AbstractArrow))
			return;
		if (!(event.getSource().getEntity() instanceof Player))
			return;
		var player = (Player) event.getSource().getEntity();
		var arrow = (AbstractArrow) event.getSource().getDirectEntity();
		var damageBonus = PlayerHelper.getFlatArrowDamageBonus(player);
		var damageMultiplier = PlayerHelper.getArrowDamageMultiplier(player, event.getEntity(), arrow);
		event.setAmount(event.getAmount() + damageBonus);
		event.setAmount(event.getAmount() * damageMultiplier);
	}

	@SubscribeEvent
	public static void setCraftedFoodBonuses(ItemCraftedEvent event) {
		if (!ItemHelper.isFood(event.getCrafting())) {
			return;
		}
		var restorationBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.COOKED_FOOD_RESTORATION_MULTIPLIER.get()) - 1;
		if (restorationBonus > 0) {
			FoodHelper.setRestorationBonus(event.getCrafting(), (float) restorationBonus);
		}
		var lifeRegenerationBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.COOKED_FOOD_LIFE_REGENERATION_PER_RESTORATION.get());
		if (lifeRegenerationBonus > 0) {
			FoodHelper.setLifeRegenerationBonus(event.getCrafting(), (float) lifeRegenerationBonus);
		}
		var damagePerRestorationBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.COOKED_FOOD_DAMAGE_PER_RESTORATION_MULTIPLIER.get()) - 1;
		if (damagePerRestorationBonus > 0) {
			FoodHelper.setDamageBonus(event.getCrafting(), (float) damagePerRestorationBonus);
		}
		var critDamagePerRestorationBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.COOKED_FOOD_CRITICAL_DAMAGE_PER_RESTORATION_MULTIPLIER.get()) - 1;
		if (critDamagePerRestorationBonus > 0) {
			FoodHelper.setCritDamageBonus(event.getCrafting(), (float) critDamagePerRestorationBonus);
		}
		var healingPerRestorationBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.COOKED_FOOD_HEALING_PER_RESTORATION_MULTIPLIER.get()) - 1;
		if (healingPerRestorationBonus > 0) {
			FoodHelper.setHealingBonus(event.getCrafting(), (float) healingPerRestorationBonus);
		}
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
			var effectAmplifier = (int) (lifeRegenerationBonus * finalRestoration * 100);
			var lifeRegenerationEffect = new MobEffectInstance(SkillTreeEffects.LIFE_REGENERATION_BONUS.get(), 20 * 60, effectAmplifier);
			player.addEffect(lifeRegenerationEffect);
		}
		if (FoodHelper.hasCritDamageBonus(event.getItem())) {
			var critDamageBonus = FoodHelper.getCritDamageBonus(event.getItem());
			var effectAmplifier = (int) (critDamageBonus * finalRestoration * 100);
			var critDamageEffect = new MobEffectInstance(SkillTreeEffects.CRIT_DAMAGE_BONUS.get(), 20 * 60, effectAmplifier);
			player.addEffect(critDamageEffect);
		}
		var delicacyEffect = new MobEffectInstance(SkillTreeEffects.DELICACY.get(), 20 * 60, 0, false, false, false);
		player.addEffect(delicacyEffect);
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
			event.getToolTip().add(Component.empty());
			event.getToolTip().add(Component.translatable("food.bonus").withStyle(ChatFormatting.GRAY));
			var damagePerRestorationBonus = FoodHelper.getDamageBonus(event.getItemStack());
			event.getToolTip().add(Component.translatable("food.bonus.damage", (int) (damagePerRestorationBonus * finalRestoration * 100)).withStyle(ChatFormatting.BLUE));
		}
		if (FoodHelper.hasCritDamageBonus(event.getItemStack())) {
			var critDamageBonus = FoodHelper.getCritDamageBonus(event.getItemStack());
			event.getToolTip().add(Component.translatable("food.bonus.crit_damage", (int) (critDamageBonus * finalRestoration * 100)).withStyle(ChatFormatting.BLUE));
		}
		if (FoodHelper.hasLifeRegenerationBonus(event.getItemStack())) {
			var lifeRegenerationBonus = FoodHelper.getLifeRegenerationBonus(event.getItemStack());
			var formattedBonus = String.format("%.1f", lifeRegenerationBonus * finalRestoration);
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
		var blockChance = player.getAttributeValue(SkillTreeAttributes.BLOCK_CHANCE_MULTIPLIER.get()) - 1;
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
}
