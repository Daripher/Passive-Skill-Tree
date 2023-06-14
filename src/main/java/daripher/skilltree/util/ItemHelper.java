package daripher.skilltree.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.config.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemHelper {
	private static final String DEFENCE_BONUS_TAG = "DefenceBonus";
	private static final String EVASION_BONUS_TAG = "EvasionBonus";
	private static final String DAMAGE_BONUS_TAG = "DamageBonus";
	private static final String ATTACK_SPEED_BONUS_TAG = "AttackSpeedBonus";
	private static final String TOUGHNESS_BONUS_TAG = "ToughnessBonus";
	private static final String DURABILITY_BONUS_TAG = "DurabilityBonus";
	private static final String POISONS_TAG = "Poisons";

	public static void setDefenceBonus(ItemStack itemStack, double bonus) {
		itemStack.getOrCreateTag().putDouble(DEFENCE_BONUS_TAG, bonus);
	}

	public static boolean hasDefenceBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(DEFENCE_BONUS_TAG);
	}

	public static double getDefenceBonus(ItemStack itemStack) {
		return itemStack.getTag().getDouble(DEFENCE_BONUS_TAG);
	}

	public static void setEvasionBonus(ItemStack itemStack, double bonus) {
		itemStack.getOrCreateTag().putDouble(EVASION_BONUS_TAG, bonus);
	}

	public static boolean hasEvasionBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(EVASION_BONUS_TAG);
	}

	public static double getEvasionBonus(ItemStack itemStack) {
		return itemStack.getTag().getDouble(EVASION_BONUS_TAG);
	}

	public static void setToughnessBonus(ItemStack itemStack, double bonus) {
		itemStack.getOrCreateTag().putDouble(TOUGHNESS_BONUS_TAG, bonus);
	}

	public static boolean hasToughnessBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(TOUGHNESS_BONUS_TAG);
	}

	public static double getToughnessBonus(ItemStack itemStack) {
		return itemStack.getTag().getDouble(TOUGHNESS_BONUS_TAG);
	}

	public static void setDamageBonus(ItemStack itemStack, double bonus) {
		itemStack.getOrCreateTag().putDouble(DAMAGE_BONUS_TAG, bonus);
	}

	public static boolean hasDamageBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(DAMAGE_BONUS_TAG);
	}

	public static double getDamageBonus(ItemStack itemStack) {
		return itemStack.getTag().getDouble(DAMAGE_BONUS_TAG);
	}

	public static void setAttackSpeedBonus(ItemStack itemStack, double bonus) {
		itemStack.getOrCreateTag().putDouble(ATTACK_SPEED_BONUS_TAG, bonus);
	}

	public static boolean hasAttackSpeedBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(ATTACK_SPEED_BONUS_TAG);
	}

	public static double getAttackSpeedBonus(ItemStack itemStack) {
		return itemStack.getTag().getDouble(ATTACK_SPEED_BONUS_TAG);
	}

	public static void setDurabilityBonus(ItemStack itemStack, double bonus) {
		itemStack.getOrCreateTag().putDouble(DURABILITY_BONUS_TAG, bonus);
	}

	public static boolean hasDurabilityBonus(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(DURABILITY_BONUS_TAG);
	}

	public static double getDurabilityBonus(ItemStack itemStack) {
		return itemStack.getTag().getDouble(DURABILITY_BONUS_TAG);
	}

	public static void applyBaseModifierBonus(ItemAttributeModifierEvent event, Attribute attribute, Function<Double, Double> amountModifier) {
		var modifiers = event.getOriginalModifiers().get(attribute);

		modifiers.forEach(modifier -> {
			event.removeModifier(attribute, modifier);
			modifier = new AttributeModifier(modifier.getId(), modifier.getName(), amountModifier.apply(modifier.getAmount()), modifier.getOperation());
			event.addModifier(attribute, modifier);
		});
	}

	public static boolean canInsertGem(ItemStack itemStack) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) {
				return false;
			}
		}
		var blacklist = Config.COMMON_CONFIG.getBlacklistedGemstoneContainers();
		if (blacklist.contains("*:*")) {
			return false;
		}
		var itemId = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
		if (blacklist.contains(itemId.toString())) {
			return false;
		}
		var itemNamespace = itemId.getNamespace();
		if (blacklist.contains(itemNamespace + ":*")) {
			return false;
		}
		return isArmor(itemStack) || isShield(itemStack) || isWeapon(itemStack) || isBow(itemStack);
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
		result.getOrCreateTag().put(POISONS_TAG, poisonsTag);
	}

	public static boolean hasPoisons(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(POISONS_TAG);
	}

	public static List<MobEffectInstance> getPoisons(ItemStack itemStack) {
		var poisonsTag = itemStack.getTag().getList(POISONS_TAG, 10);
		var effects = new ArrayList<MobEffectInstance>();
		for (var tag : poisonsTag) {
			var effect = MobEffectInstance.load((CompoundTag) tag);
			effects.add(effect);
		}
		return effects;
	}

	public static EquipmentSlot getSlotForItem(ItemStack itemStack) {
		var slot = Player.getEquipmentSlotForItem(itemStack);
		if (ItemHelper.isWeapon(itemStack) && slot == EquipmentSlot.OFFHAND) {
			slot = EquipmentSlot.MAINHAND;
		}
		return slot;
	}

	public static boolean isArmor(ItemStack itemStack) {
		return itemStack.is(Tags.Items.ARMORS);
	}

	public static boolean isShield(ItemStack itemStack) {
		return itemStack.is(Tags.Items.TOOLS_SHIELDS);
	}

	public static boolean isWeapon(ItemStack itemStack) {
		return itemStack.is(Tags.Items.TOOLS_AXES) || itemStack.is(Tags.Items.TOOLS_SWORDS);
	}

	public static boolean isBow(ItemStack itemStack) {
		return itemStack.is(Tags.Items.TOOLS_BOWS);
	}

	public static boolean isWeaponOrBow(ItemStack itemStack) {
		return isWeapon(itemStack) || isBow(itemStack);
	}

	public static boolean isHelmet(ItemStack itemStack) {
		return itemStack.is(Tags.Items.ARMORS_HELMETS);
	}

	public static boolean isChestplate(ItemStack itemStack) {
		return itemStack.is(Tags.Items.ARMORS_CHESTPLATES);
	}

	public static boolean isLeggings(ItemStack itemStack) {
		return itemStack.is(Tags.Items.ARMORS_LEGGINGS);
	}

	public static boolean isBoots(ItemStack itemStack) {
		return itemStack.is(Tags.Items.ARMORS_BOOTS);
	}

	public static boolean isPickaxe(ItemStack itemStack) {
		return itemStack.is(Tags.Items.TOOLS_PICKAXES);
	}

	public static boolean isFood(ItemStack itemStack) {
		return itemStack.getFoodProperties(null) != null;
	}

	public static boolean isAxe(ItemStack itemStack) {
		return itemStack.getItem() instanceof AxeItem;
	}

	public static boolean isEquipment(ItemStack stack) {
		return isWeaponOrBow(stack) || isArmor(stack) || isShield(stack);
	}
}
