package daripher.skilltree.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import daripher.skilltree.config.Config;
import daripher.skilltree.init.PSTTags;
import daripher.skilltree.potion.PotionHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemHelper {
	private static final String POISONS = "Poisons";
	public static final String IGNITE_CHANCE = "IgniteChanceBonus";
	public static final String DAMAGE_AGAINST_BURNING = "DamageVsBurningBonus";
	public static final String LIFE_PER_HIT = "LifePerHitBonus";
	public static final String ATTACK_SPEED = "AttackSpeedBonus";
	public static final String DAMAGE = "DamageBonus";
	public static final String CRIT_CHANCE = "CritChanceBonus";
	public static final String DURABILITY = "DurabilityBonus";
	public static final String MAXIMUM_LIFE = "MaximumLifeBonus";
	public static final String CAPACITY = "CapacityBonus";
	public static final String TOUGHNESS = "ToghnessBonus";
	public static final String DEFENCE = "DefenceBonus";
	public static final String MOVEMENT_SPEED = "MovementSpeedBonus";
	public static final String STEALTH = "StealthBonus";
	public static final String EVASION = "EvasionBonus";
	public static final String CRIT_DAMAGE = "CritDamageBonus";
	public static final String BLOCK_CHANCE = "BlockChanceBonus";
	public static final String DOUBLE_LOOT = "DoubleLootBonus";
	public static final String ADDITIONAL_SOCKETS = "AdditionalSocksetsBonus";

	public static void setBonus(ItemStack stack, String type, double bonus) {
		stack.getOrCreateTag().putDouble(type, bonus);
	}

	public static boolean hasBonus(ItemStack stack, String type) {
		return stack.hasTag() && stack.getTag().contains(type);
	}

	public static double getBonus(ItemStack stack, String type) {
		return stack.getTag().getDouble(type);
	}

	public static void applyBaseModifierBonus(ItemAttributeModifierEvent event, Attribute attribute, Function<Double, Double> func) {
		Collection<AttributeModifier> modifiers = event.getOriginalModifiers().get(attribute);
		modifiers.forEach(modifier -> {
			event.removeModifier(attribute, modifier);
			modifier = new AttributeModifier(modifier.getId(), modifier.getName(), func.apply(modifier.getAmount()), modifier.getOperation());
			event.addModifier(attribute, modifier);
		});
	}

	public static boolean canInsertGem(ItemStack stack) {
		return hasSockets(stack);
	}

	public static boolean hasSockets(ItemStack stack) {
		List<? extends String> blacklist = Config.socket_blacklist;
		if (blacklist.contains("*:*")) return false;
		ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
		if (itemId == null) return false;
		if (blacklist.contains(itemId.toString())) return false;
		String namespace = itemId.getNamespace();
		if (blacklist.contains(namespace + ":*")) return false;
		return isEquipment(stack) && !isLeggings(stack) || isJewelry(stack);
	}

	public static boolean isPoison(ItemStack stack) {
		return stack.getItem() instanceof PotionItem potion && PotionHelper.isHarmfulPotion(stack);
	}

	public static boolean isPotion(ItemStack stack) {
		return stack.getItem() instanceof PotionItem;
	}

	public static void setPoisons(ItemStack result, ItemStack poisonStack) {
		var effects = PotionUtils.getMobEffects(poisonStack);
		var poisonsTag = new ListTag();
		for (var effect : effects) {
			var effectTag = effect.save(new CompoundTag());
			poisonsTag.add(effectTag);
		}
		result.getOrCreateTag().put(POISONS, poisonsTag);
	}

	public static boolean hasPoisons(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains(POISONS);
	}

	public static List<MobEffectInstance> getPoisons(ItemStack stack) {
		var poisonsTag = stack.getTag().getList(POISONS, 10);
		var effects = new ArrayList<MobEffectInstance>();
		for (var tag : poisonsTag) {
			var effect = MobEffectInstance.load((CompoundTag) tag);
			effects.add(effect);
		}
		return effects;
	}

	public static EquipmentSlot getSlotForItem(ItemStack stack) {
		var slot = Player.getEquipmentSlotForItem(stack);
		if (ItemHelper.isMeleeWeapon(stack) && slot == EquipmentSlot.OFFHAND) {
			slot = EquipmentSlot.MAINHAND;
		}
		return slot;
	}

	public static int getDefaultSockets(ItemStack stack) {
		if (isHelmet(stack)) return Config.default_helmet_sockets;
		if (isChestplate(stack)) return Config.default_chestplate_sockets;
		if (isLeggings(stack)) return Config.default_leggings_sockets;
		if (isBoots(stack)) return Config.default_boots_sockets;
		if (isWeapon(stack)) return Config.default_weapon_sockets;
		if (isShield(stack)) return Config.default_shield_sockets;
		if (isRing(stack)) return Config.default_ring_sockets;
		if (isNecklace(stack)) return Config.default_necklace_sockets;
		return 0;
	}

	public static boolean isArmor(ItemStack stack) {
		return isHelmet(stack) || isChestplate(stack) || isLeggings(stack) || isBoots(stack) || stack.is(Tags.Items.ARMORS);
	}

	public static boolean isShield(ItemStack stack) {
		if (stack.canPerformAction(ToolActions.SHIELD_BLOCK)) return true;
		if (Config.forced_shields.contains(stack.getItem())) return true;
		return stack.getItem() instanceof ShieldItem || stack.is(Tags.Items.TOOLS_SHIELDS);
	}

	public static boolean isMeleeWeapon(ItemStack stack) {
		if (Config.forced_melee_weapon.contains(stack.getItem())) return true;
		return isSword(stack) || isAxe(stack) || isTrident(stack);
	}

	public static boolean isRangedWeapon(ItemStack stack) {
		if (Config.forced_ranged_weapon.contains(stack.getItem())) return true;
		return isCrossbow(stack) || isBow(stack) || isTrident(stack);
	}

	public static boolean isCrossbow(ItemStack stack) {
		if (ForgeRegistries.ITEMS.getKey(stack.getItem()).toString().equals("tetra:modular_crossbow")) return true;
		return stack.getItem() instanceof CrossbowItem || stack.is(Tags.Items.TOOLS_CROSSBOWS);
	}

	public static boolean isBow(ItemStack stack) {
		if (ForgeRegistries.ITEMS.getKey(stack.getItem()).toString().equals("tetra:modular_bow")) return true;
		return stack.getItem() instanceof BowItem || stack.is(Tags.Items.TOOLS_BOWS);
	}

	public static boolean isTrident(ItemStack stack) {
		if (ForgeRegistries.ITEMS.getKey(stack.getItem()).toString().equals("tetra:modular_single")) return true;
		return stack.getItem() instanceof TridentItem || stack.is(Tags.Items.TOOLS_TRIDENTS);
	}

	public static boolean isAxe(ItemStack stack) {
		if (stack.canPerformAction(ToolActions.AXE_DIG)) return true;
		return stack.getItem() instanceof AxeItem || stack.is(ItemTags.AXES);
	}

	public static boolean isSword(ItemStack stack) {
		if (ForgeRegistries.ITEMS.getKey(stack.getItem()).toString().equals("tetra:modular_sword")) return true;
		if (stack.canPerformAction(ToolActions.SWORD_DIG)) return true;
		if (stack.canPerformAction(ToolActions.SWORD_SWEEP)) return true;
		return stack.getItem() instanceof SwordItem || stack.is(ItemTags.SWORDS);
	}

	public static boolean isWeapon(ItemStack stack) {
		return isMeleeWeapon(stack) || isRangedWeapon(stack);
	}

	public static boolean isHelmet(ItemStack stack) {
		if (Config.forced_helmets.contains(stack.getItem())) return true;
		if (stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.HEAD) return true;
		return stack.is(Tags.Items.ARMORS_HELMETS);
	}

	public static boolean isChestplate(ItemStack stack) {
		if (Config.forced_chestplates.contains(stack.getItem())) return true;
		if (stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.CHEST) return true;
		return stack.is(Tags.Items.ARMORS_CHESTPLATES);
	}

	public static boolean isLeggings(ItemStack stack) {
		if (Config.forced_leggings.contains(stack.getItem())) return true;
		if (stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.LEGS) return true;
		return stack.is(Tags.Items.ARMORS_LEGGINGS);
	}

	public static boolean isBoots(ItemStack stack) {
		if (Config.forced_boots.contains(stack.getItem())) return true;
		if (stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.FEET) return true;
		return stack.is(Tags.Items.ARMORS_BOOTS);
	}

	public static boolean isPickaxe(ItemStack stack) {
		if (stack.canPerformAction(ToolActions.PICKAXE_DIG)) return true;
		return stack.getItem() instanceof PickaxeItem || stack.is(ItemTags.PICKAXES);
	}

	public static boolean isFood(ItemStack stack) {
		return stack.getFoodProperties(null) != null;
	}

	public static boolean isEquipment(ItemStack stack) {
		return isWeapon(stack) || isArmor(stack) || isShield(stack) || isPickaxe(stack);
	}

	public static boolean isJewelry(ItemStack stack) {
		return isRing(stack) || isNecklace(stack);
	}

	public static boolean isRing(ItemStack stack) {
		return !stack.isEmpty() && stack.is(PSTTags.RINGS);
	}

	public static boolean isNecklace(ItemStack stack) {
		return !stack.isEmpty() && stack.is(PSTTags.NECKLACES);
	}

	public static boolean isQuiver(ItemStack stack) {
		return !stack.isEmpty() && stack.is(PSTTags.QUIVERS);
	}

	public static boolean isArrow(ItemStack stack) {
		return stack.is(ItemTags.ARROWS);
	}

	public static List<String> getBonuses() {
		return List.of(POISONS, IGNITE_CHANCE, DAMAGE_AGAINST_BURNING, LIFE_PER_HIT, ATTACK_SPEED, DAMAGE, CRIT_CHANCE, DURABILITY, MAXIMUM_LIFE,
				CAPACITY, TOUGHNESS, DEFENCE, MOVEMENT_SPEED, STEALTH, EVASION, CRIT_DAMAGE, BLOCK_CHANCE, DOUBLE_LOOT, ADDITIONAL_SOCKETS);
	}
}
