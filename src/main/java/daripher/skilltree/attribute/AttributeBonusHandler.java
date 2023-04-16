package daripher.skilltree.attribute;

import java.util.Optional;
import java.util.UUID;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeEffects;
import daripher.skilltree.init.SkillTreeItems;
import daripher.skilltree.init.SkillTreeTags;
import daripher.skilltree.util.FoodHelper;
import daripher.skilltree.util.ItemHelper;
import daripher.skilltree.util.PlayerHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
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
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class AttributeBonusHandler {
	@SubscribeEvent
	public static void applyDamageBonus(LivingHurtEvent event) {
		if (event.getSource().getEntity() instanceof Player player) {
			var damageMultiplier = PlayerHelper.getDamageMultiplier(player, event.getEntity());
			event.setAmount(event.getAmount() * damageMultiplier);
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
	public static void applyAttackSpeedBonus(PlayerTickEvent event) {
		if (event.player.level.isClientSide) {
			return;
		}

		var attackSpeedBonus = PlayerHelper.getAttackSpeedBonus(event.player);
		var modifierId = UUID.fromString("030cf99a-09f8-4e40-a858-56b9d588b445");

		if (attackSpeedBonus > 0) {
			var attackSpeedAttribute = event.player.getAttribute(Attributes.ATTACK_SPEED);

			var oldModifier = attackSpeedAttribute.getModifier(modifierId);

			if (oldModifier != null) {
				if (oldModifier.getAmount() == attackSpeedBonus) {
					return;
				}

				attackSpeedAttribute.removeModifier(modifierId);
			}

			attackSpeedAttribute.addPermanentModifier(new AttributeModifier(modifierId, "Skill Tree Bonus", attackSpeedBonus, Operation.MULTIPLY_TOTAL));
		}
	}

	@SubscribeEvent
	public static void setCraftedArmorDefenceBonuses(ItemCraftedEvent event) {
		if (!ItemHelper.isArmor(event.getCrafting())) {
			return;
		}

		var armorDefenceBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_ARMOR_DEFENCE_BONUS.get());

		if (armorDefenceBonus > 0) {
			ItemHelper.setDefenceBonus(event.getCrafting(), armorDefenceBonus);
		}

		var tougherArmorCraftingChance = event.getEntity().getAttributeValue(SkillTreeAttributes.TOUGHER_ARMOR_CRAFTING_CHANCE.get());

		if (tougherArmorCraftingChance > 0) {
			if (event.getEntity().getRandom().nextFloat() < tougherArmorCraftingChance) {
				ItemHelper.setToughnessBonus(event.getCrafting(), 1);
			}
		}
	}

	@SubscribeEvent
	public static void applyArmorDefenceBonuses(ItemAttributeModifierEvent event) {
		if (ItemHelper.hasDefenceBonus(event.getItemStack())) {
			var defenceBonus = ItemHelper.getDefenceBonus(event.getItemStack());
			ItemHelper.applyBaseModifierBonus(event, Attributes.ARMOR, d -> d * (1 + defenceBonus));
		}

		if (ItemHelper.hasToughnessBonus(event.getItemStack())) {
			var toughnessBonus = ItemHelper.getToughnessBonus(event.getItemStack());
			ItemHelper.applyBaseModifierBonus(event, Attributes.ARMOR_TOUGHNESS, d -> d + toughnessBonus);
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

		var weaponAttackSpeedBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_WEAPON_ATTACK_SPEED_BONUS.get());

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
			ItemHelper.applyBaseModifierBonus(event, Attributes.ATTACK_DAMAGE, d -> d * (1 + damageBonus));
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
		var miningSpeedBonus = (float) event.getEntity().getAttributeValue(SkillTreeAttributes.MINING_SPEED_BONUS.get());
		event.setNewSpeed((event.getOriginalSpeed() * (1 + miningSpeedBonus)));
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void applyMineralsDuplicationChanceBonus(BlockEvent.BreakEvent event) {
		var mineralsDuplicationChance = event.getPlayer().getAttributeValue(SkillTreeAttributes.MINERALS_DUPLICATION_CHANCE.get());

		if (mineralsDuplicationChance == 0) {
			return;
		}

		if (event.getPlayer().getRandom().nextFloat() >= mineralsDuplicationChance) {
			return;
		}

		if (!ForgeHooks.isCorrectToolForDrops(event.getState(), event.getPlayer())) {
			return;
		}

		var level = event.getPlayer().getLevel();

		if (level.isClientSide) {
			return;
		}

		var blockPos = event.getPos();
		var drops = Block.getDrops(event.getState(), (ServerLevel) level, blockPos, level.getBlockEntity(blockPos), event.getPlayer(), ItemStack.EMPTY);

		drops.forEach(itemStack -> {
			if (itemStack.is(SkillTreeTags.MINERALS)) {
				Block.popResource(level, blockPos, itemStack);
			}
		});
	}

	@SubscribeEvent
	public static void applyGemstoneFindingChanceBonus(BlockEvent.BreakEvent event) {
		var level = event.getPlayer().getLevel();

		if (level.isClientSide) {
			return;
		}

		var gemstoneFindingChance = event.getPlayer().getAttributeValue(SkillTreeAttributes.GEMSTONE_FINDING_CHANCE.get());

		if (gemstoneFindingChance == 0) {
			return;
		}

		var blockPos = event.getPos();

		if (!level.getBlockState(blockPos).is(Tags.Blocks.ORES)) {
			return;
		}

		if (event.getPlayer().getRandom().nextFloat() >= gemstoneFindingChance) {
			return;
		}

		if (!ForgeHooks.isCorrectToolForDrops(event.getState(), event.getPlayer())) {
			return;
		}

		var gemstones = new Item[] { SkillTreeItems.LIGHT_GEMSTONE.get(), SkillTreeItems.SOOTHING_GEMSTONE.get(), SkillTreeItems.STURDY_GEMSTONE.get() };
		Block.popResource(level, blockPos, new ItemStack(gemstones[event.getPlayer().getRandom().nextInt(gemstones.length)]));
	}

	@SubscribeEvent
	public static void applyLifeRegenerationBonus(PlayerTickEvent event) {
		if (event.phase == Phase.END || event.player.level.isClientSide) {
			return;
		}

		if (event.player.getFoodData().getFoodLevel() == 0) {
			return;
		}

		var lifeRegeneration = (float) event.player.getAttributeValue(SkillTreeAttributes.HEALTH_REGENERATION_BONUS.get());

		if (event.player.getHealth() != event.player.getMaxHealth() && event.player.tickCount % 20 == 0) {
			event.player.heal(lifeRegeneration);
			event.player.getFoodData().addExhaustion(lifeRegeneration * 2);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void applyLootDuplicationChanceBonus(LivingDropsEvent event) {
		if (!(event.getSource().getEntity() instanceof Player)) {
			return;
		}

		var player = (Player) event.getSource().getEntity();
		var lootDuplicationChance = player.getAttributeValue(SkillTreeAttributes.LOOT_DUPLICATION_CHANCE.get());

		if (lootDuplicationChance == 0) {
			return;
		}

		if (player.getRandom().nextFloat() < lootDuplicationChance) {
			var additionalDrops = event.getDrops().stream().map(itemEntity -> new ItemEntity(itemEntity.level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), itemEntity.getItem().copy())).toList();
			event.getDrops().addAll(additionalDrops);
		}
	}

	@SubscribeEvent
	public static void setCraftedBowsAttributeBonuses(ItemCraftedEvent event) {
		if (!ItemHelper.isBow(event.getCrafting())) {
			return;
		}

		var bowDamageBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_BOWS_DAMAGE_BONUS.get());

		if (bowDamageBonus > 0) {
			ItemHelper.setDamageBonus(event.getCrafting(), bowDamageBonus);
		}

		var bowChargeSpeedBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_BOWS_CHARGE_SPEED_BONUS.get());

		if (bowChargeSpeedBonus > 0) {
			ItemHelper.setAttackSpeedBonus(event.getCrafting(), bowChargeSpeedBonus);
		}

		var bowDoubleDamageChance = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_BOWS_DOUBLE_DAMAGE_CHANCE.get());

		if (bowDoubleDamageChance > 0) {
			ItemHelper.setDoubleDamageChanceBonus(event.getCrafting(), bowDoubleDamageChance);
		}

		var bowArmorReduction = event.getEntity().getAttributeValue(SkillTreeAttributes.CRAFTED_BOWS_ARMOR_REDUCTION.get());

		if (bowArmorReduction > 0) {
			ItemHelper.setArmorReductionBonus(event.getCrafting(), bowArmorReduction);
		}
	}

	@SubscribeEvent
	public static void applyBowAttributeBonuses(ItemAttributeModifierEvent event) {
		if (!ItemHelper.isBow(event.getItemStack()) || event.getSlotType() != EquipmentSlot.MAINHAND) {
			return;
		}

		if (ItemHelper.hasDamageBonus(event.getItemStack())) {
			var damageBonus = ItemHelper.getDamageBonus(event.getItemStack());
			var modifierId = UUID.fromString("3c135366-4dfd-4f81-a644-7b0dd9cd564c");
			var damageModifier = new AttributeModifier(modifierId, "Crafted Bow Bonus", damageBonus, Operation.MULTIPLY_BASE);
			event.addModifier(SkillTreeAttributes.ARROW_DAMAGE_MULTIPLIER.get(), damageModifier);
		}

		if (ItemHelper.hasAttackSpeedBonus(event.getItemStack())) {
			var attackSpeedBonus = ItemHelper.getAttackSpeedBonus(event.getItemStack());
			var modifierId = UUID.fromString("0cf2b159-b953-4c96-a84e-d380f6a74031");
			var attackSpeedModifier = new AttributeModifier(modifierId, "Crafted Bow Bonus", attackSpeedBonus, Operation.MULTIPLY_BASE);
			event.addModifier(SkillTreeAttributes.BOW_CHARGE_SPEED_MULTIPLIER.get(), attackSpeedModifier);
		}

		if (ItemHelper.hasDoubleDamageChanceBonus(event.getItemStack())) {
			var doubleDamageChanceBonus = ItemHelper.getDoubleDamageChanceBonus(event.getItemStack());
			var modifierId = UUID.fromString("499c871d-a9f0-46c4-a958-76b7a2d915a4");
			var doubleDamageChanceModifier = new AttributeModifier(modifierId, "Crafted Bow Bonus", doubleDamageChanceBonus, Operation.MULTIPLY_BASE);
			event.addModifier(SkillTreeAttributes.ARROW_DOUBLE_DAMAGE_CHANCE_MULTIPLIER.get(), doubleDamageChanceModifier);
		}

		if (ItemHelper.hasArmorReductionBonus(event.getItemStack())) {
			var armorReductionBonus = ItemHelper.getArmorReductionBonus(event.getItemStack());
			var modifierId = UUID.fromString("a9a87d0c-6f65-46f3-be7d-fbfd91704286");
			var armorReductionModifier = new AttributeModifier(modifierId, "Crafted Bow Bonus", armorReductionBonus, Operation.MULTIPLY_BASE);
			event.addModifier(SkillTreeAttributes.ARROW_ARMOR_REDUCTION_MULTIPLIER.get(), armorReductionModifier);
		}
	}

	@SubscribeEvent
	public static void applyArrowArmorReductionBonus(LivingAttackEvent event) {
		if (!(event.getSource().getDirectEntity() instanceof AbstractArrow)) {
			return;
		}

		if (!(event.getSource().getEntity() instanceof Player)) {
			return;
		}

		var player = (Player) event.getSource().getEntity();
		var armorReduction = player.getAttributeValue(SkillTreeAttributes.ARROW_ARMOR_REDUCTION_MULTIPLIER.get()) - 1;

		if (armorReduction > 0) {
			var armorReductionModifierId = UUID.fromString("40d38fc0-e00e-43f7-b16c-f35cc3ea80df");
			var armorModifier = new AttributeModifier(armorReductionModifierId, "Arrow Armor Reduction", 1 - armorReduction, Operation.MULTIPLY_TOTAL);
			var armorAttribute = event.getEntity().getAttribute(Attributes.ARMOR);

			if (armorAttribute.getModifier(armorReductionModifierId) != null) {
				armorAttribute.removeModifier(armorReductionModifierId);
			}

			armorAttribute.addTransientModifier(armorModifier);
		}
	}

	@SubscribeEvent
	public static void applyArrowDamageBonuses(LivingHurtEvent event) {
		if (!(event.getSource().getDirectEntity() instanceof AbstractArrow)) {
			return;
		}

		if (!(event.getSource().getEntity() instanceof Player)) {
			return;
		}

		var armorReductionModifierId = UUID.fromString("40d38fc0-e00e-43f7-b16c-f35cc3ea80df");
		var armorAttribute = event.getEntity().getAttribute(Attributes.ARMOR);
		var armorReductionModifier = armorAttribute.getModifier(armorReductionModifierId);

		if (armorReductionModifier != null) {
			armorAttribute.removeModifier(armorReductionModifierId);
		}

		var player = (Player) event.getSource().getEntity();
		var arrow = (AbstractArrow) event.getSource().getDirectEntity();
		var damageMultiplier = PlayerHelper.getArrowDamageMultiplier(player, event.getEntity(), arrow);
		event.setAmount((float) (event.getAmount() * damageMultiplier));
	}

	@SubscribeEvent
	public static void setCraftedFoodBonuses(ItemCraftedEvent event) {
		if (!ItemHelper.isFood(event.getCrafting())) {
			return;
		}

		var restorationBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.COOKED_FOOD_RESTORATION_BONUS.get());

		if (restorationBonus > 0) {
			FoodHelper.setRestorationBonus(event.getCrafting(), (float) restorationBonus);
		}

		var healingBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.COOKED_FOOD_HEALING_PER_RESTORATION.get());

		if (healingBonus > 0) {
			FoodHelper.setHealingBonus(event.getCrafting(), (float) healingBonus);
		}

		var attackSpeedBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.COOKED_FOOD_ATTACK_SPEED_BONUS.get());

		if (attackSpeedBonus > 0) {
			FoodHelper.setAttackSpeedBonus(event.getCrafting(), (float) attackSpeedBonus);
		}

		var damagePerRestorationBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.COOKED_FOOD_DAMAGE_PER_RESTORATION_BONUS.get());

		if (damagePerRestorationBonus > 0) {
			FoodHelper.setDamagePerRestorationBonus(event.getCrafting(), (float) damagePerRestorationBonus);
		}

		var critChanceBonus = event.getEntity().getAttributeValue(SkillTreeAttributes.COOKED_FOOD_CRIT_CHANCE_BONUS.get());

		if (critChanceBonus > 0) {
			FoodHelper.setCritChanceBonus(event.getCrafting(), (float) critChanceBonus);
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
			player.heal(finalRestoration * healingBonus);
		}

		var hiddenEffect = (MobEffectInstance) null;

		if (FoodHelper.hasAttackSpeedBonus(event.getItem())) {
			var attackSpeedBonus = FoodHelper.getAttackSpeedBonus(event.getItem());
			hiddenEffect = new MobEffectInstance(SkillTreeEffects.ATTACK_SPEED_BONUS.get(), 20 * 60, (int) (attackSpeedBonus * 100), false, true, true, hiddenEffect, Optional.ofNullable(null));
		}

		if (FoodHelper.hasDamagePerRestorationBonus(event.getItem())) {
			var damagePerRestorationBonus = FoodHelper.getDamagePerRestorationBonus(event.getItem());
			hiddenEffect = new MobEffectInstance(SkillTreeEffects.DAMAGE_BONUS.get(), 20 * 60, (int) (damagePerRestorationBonus * finalRestoration * 100), false, true, true, hiddenEffect, Optional.ofNullable(null));
		}

		if (FoodHelper.hasCritChanceBonus(event.getItem())) {
			var critChanceBonus = FoodHelper.getCritChanceBonus(event.getItem());
			hiddenEffect = new MobEffectInstance(SkillTreeEffects.CRIT_CHANCE_BONUS.get(), 20 * 60, (int) (critChanceBonus * 100), false, true, true, hiddenEffect, Optional.ofNullable(null));
		}

		var foodBonusEffect = new MobEffectInstance(SkillTreeEffects.DELICACY.get(), 20 * 60, 0, false, true, true, hiddenEffect, Optional.ofNullable(null));
		player.addEffect(foodBonusEffect);

		var critDamageBonusIfAteRecently = player.getAttributeValue(SkillTreeAttributes.CRIT_DAMAGE_BONUS_IF_ATE_RECENTLY.get());

		if (critDamageBonusIfAteRecently > 0) {
			player.addEffect(new MobEffectInstance(SkillTreeEffects.GOURMET.get(), 20 * 60));
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

		if (FoodHelper.hasDamagePerRestorationBonus(event.getItemStack())) {
			event.getToolTip().add(Component.empty());
			event.getToolTip().add(Component.translatable("food.bonus").withStyle(ChatFormatting.GRAY));
			var damagePerRestorationBonus = FoodHelper.getDamagePerRestorationBonus(event.getItemStack());
			event.getToolTip().add(Component.translatable("food.bonus.damage", (int) (damagePerRestorationBonus * finalRestoration * 100)).withStyle(ChatFormatting.BLUE));
		}

		if (FoodHelper.hasCritChanceBonus(event.getItemStack())) {
			var critChanceBonus = FoodHelper.getCritChanceBonus(event.getItemStack());
			event.getToolTip().add(Component.translatable("food.bonus.crit_chance", (int) (critChanceBonus * 100)).withStyle(ChatFormatting.BLUE));
		}

		if (FoodHelper.hasAttackSpeedBonus(event.getItemStack())) {
			var attackSpeedBonus = FoodHelper.getAttackSpeedBonus(event.getItemStack());
			event.getToolTip().add(Component.translatable("food.bonus.attack_speed", (int) (attackSpeedBonus * 100)).withStyle(ChatFormatting.BLUE));
		}
	}
}
