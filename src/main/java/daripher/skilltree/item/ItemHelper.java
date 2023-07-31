package daripher.skilltree.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.PSTTags;
import daripher.skilltree.util.PotionHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.fml.ModList;
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

	public static void setBonus(ItemStack itemStack, String type, double bonus) {
		itemStack.getOrCreateTag().putDouble(type, bonus);
	}

	public static boolean hasBonus(ItemStack itemStack, String type) {
		return itemStack.hasTag() && itemStack.getTag().contains(type);
	}

	public static double getBonus(ItemStack itemStack, String type) {
		return itemStack.getTag().getDouble(type);
	}

	public static void applyBaseModifierBonus(ItemAttributeModifierEvent event, Attribute attribute, Function<Double, Double> func) {
		Collection<AttributeModifier> modifiers = event.getOriginalModifiers().get(attribute);
		modifiers.forEach(modifier -> {
			event.removeModifier(attribute, modifier);
			modifier = new AttributeModifier(modifier.getId(), modifier.getName(), func.apply(modifier.getAmount()), modifier.getOperation());
			event.addModifier(attribute, modifier);
		});
	}

	public static boolean canInsertGem(ItemStack itemStack) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) return false;
		}
		List<? extends String> blacklist = Config.COMMON_CONFIG.getBlacklistedGemstoneContainers();
		if (blacklist.contains("*:*")) return false;
		ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
		if (itemId == null) return false;
		if (blacklist.contains(itemId.toString())) return false;
		String itemNamespace = itemId.getNamespace();
		if (blacklist.contains(itemNamespace + ":*")) return false;
		return isEquipment(itemStack) && !isLeggings(itemStack) || isJewelry(itemStack);
	}

	public static boolean isPoison(ItemStack itemStack) {
		return itemStack.getItem() instanceof PotionItem potion && PotionHelper.isHarmfulPotion(itemStack);
	}

	public static boolean isPotion(ItemStack itemStack) {
		return itemStack.getItem() instanceof PotionItem;
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

	public static boolean hasPoisons(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(POISONS);
	}

	public static List<MobEffectInstance> getPoisons(ItemStack itemStack) {
		var poisonsTag = itemStack.getTag().getList(POISONS, 10);
		var effects = new ArrayList<MobEffectInstance>();
		for (var tag : poisonsTag) {
			var effect = MobEffectInstance.load((CompoundTag) tag);
			effects.add(effect);
		}
		return effects;
	}

	public static EquipmentSlot getSlotForItem(ItemStack itemStack) {
		var slot = Player.getEquipmentSlotForItem(itemStack);
		if (ItemHelper.isMeleeWeapon(itemStack) && slot == EquipmentSlot.OFFHAND) {
			slot = EquipmentSlot.MAINHAND;
		}
		return slot;
	}

	public static boolean isArmor(ItemStack itemStack) {
		return itemStack.getItem() instanceof ArmorItem || itemStack.is(Tags.Items.ARMORS);
	}

	public static boolean isShield(ItemStack itemStack) {
		if (itemStack.canPerformAction(ToolActions.SHIELD_BLOCK)) return true;
		return itemStack.getItem() instanceof ShieldItem || itemStack.is(Tags.Items.TOOLS_SHIELDS);
	}

	public static boolean isMeleeWeapon(ItemStack itemStack) {
		return isSword(itemStack) || isAxe(itemStack) || isTrident(itemStack);
	}

	public static boolean isRangedWeapon(ItemStack itemStack) {
		return isCrossbow(itemStack) || isBow(itemStack) || isTrident(itemStack);
	}

	public static boolean isCrossbow(ItemStack itemStack) {
		if (ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString().equals("tetra:modular_crossbow")) return true;
		return itemStack.getItem() instanceof CrossbowItem || itemStack.is(Tags.Items.TOOLS_CROSSBOWS);
	}

	public static boolean isBow(ItemStack itemStack) {
		if (ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString().equals("tetra:modular_bow")) return true;
		return itemStack.getItem() instanceof BowItem || itemStack.is(Tags.Items.TOOLS_BOWS);
	}

	public static boolean isTrident(ItemStack itemStack) {
		if (ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString().equals("tetra:modular_single")) return true;
		return itemStack.getItem() instanceof TridentItem || itemStack.is(Tags.Items.TOOLS_TRIDENTS);
	}

	public static boolean isAxe(ItemStack itemStack) {
		if (itemStack.canPerformAction(ToolActions.AXE_DIG)) return true;
		return itemStack.getItem() instanceof AxeItem || itemStack.is(Tags.Items.TOOLS_AXES);
	}

	public static boolean isSword(ItemStack itemStack) {
		if (ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString().equals("tetra:modular_sword")) return true;
		if (itemStack.canPerformAction(ToolActions.SWORD_DIG)) return true;
		if (itemStack.canPerformAction(ToolActions.SWORD_SWEEP)) return true;
		return itemStack.getItem() instanceof SwordItem || itemStack.is(Tags.Items.TOOLS_SWORDS);
	}

	public static boolean isWeapon(ItemStack itemStack) {
		return isMeleeWeapon(itemStack) || isRangedWeapon(itemStack);
	}

	public static boolean isHelmet(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ArmorItem armor && armor.getSlot() == EquipmentSlot.HEAD) return true;
		return itemStack.is(Tags.Items.ARMORS_HELMETS);
	}

	public static boolean isChestplate(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ArmorItem armor && armor.getSlot() == EquipmentSlot.CHEST) return true;
		return itemStack.is(Tags.Items.ARMORS_CHESTPLATES);
	}

	public static boolean isLeggings(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ArmorItem armor && armor.getSlot() == EquipmentSlot.LEGS) return true;
		return itemStack.is(Tags.Items.ARMORS_LEGGINGS);
	}

	public static boolean isBoots(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ArmorItem armor && armor.getSlot() == EquipmentSlot.FEET) return true;
		return itemStack.is(Tags.Items.ARMORS_BOOTS);
	}

	public static boolean isPickaxe(ItemStack itemStack) {
		if (itemStack.canPerformAction(ToolActions.PICKAXE_DIG)) return true;
		return itemStack.getItem() instanceof PickaxeItem || itemStack.is(Tags.Items.TOOLS_PICKAXES);
	}

	public static boolean isFood(ItemStack itemStack) {
		return itemStack.getFoodProperties(null) != null;
	}

	public static boolean isEquipment(ItemStack stack) {
		return isWeapon(stack) || isArmor(stack) || isShield(stack) || isPickaxe(stack);
	}

	public static boolean isJewelry(ItemStack stack) {
		return isRing(stack) || isNecklace(stack);
	}

	public static boolean isRing(ItemStack stack) {
		return stack.is(PSTTags.RINGS);
	}

	public static boolean isNecklace(ItemStack stack) {
		return stack.is(PSTTags.NECKLACES);
	}

	public static boolean isQuiver(ItemStack stack) {
		return stack.is(PSTTags.QUIVERS);
	}
}
