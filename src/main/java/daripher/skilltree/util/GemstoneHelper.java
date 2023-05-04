package daripher.skilltree.util;

import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import daripher.skilltree.init.SkillTreeItems;
import daripher.skilltree.item.GemstoneItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class GemstoneHelper {
	private static final String GEMSTONE_TAG = "Gemstone";
	private static final String GEMSTONE_STRENGTH_TAG = "GemstoneStrength";
	private static final String ADDITIONAL_GEMSTONES_TAG = "AdditionalGemstones";
	private static final String RAINBOW_GEMSTONE_TAG = "RainbowGemstone";
	private static final String RAINBOW_GEMSTONE_BONUS_TAG = "RainbowGemstoneBonus";

	public static void applyGemstone(ItemStack itemStack, GemstoneItem gemstoneItem, int gemstoneSlot) {
		var gemstoneId = ForgeRegistries.ITEMS.getKey(gemstoneItem).toString();
		itemStack.getOrCreateTag().putString(GEMSTONE_TAG + gemstoneSlot, gemstoneId);
	}

	public static boolean hasGemstone(ItemStack itemStack, int gemstoneSlot) {
		var hasGemstoneTag = itemStack.hasTag() && itemStack.getTag().contains(GEMSTONE_TAG + gemstoneSlot);
		return hasGemstoneTag;
	}

	public static void removeGemstone(ItemStack itemStack, int gemstoneSlot) {
		itemStack.getTag().remove(GEMSTONE_TAG + gemstoneSlot);
		itemStack.getTag().remove(GEMSTONE_STRENGTH_TAG + gemstoneSlot);
		itemStack.getTag().remove(RAINBOW_GEMSTONE_BONUS_TAG + gemstoneSlot);
	}

	public static void applyRainbowGemstone(ItemStack itemStack, int gemstoneSlot) {
		itemStack.getOrCreateTag().putString(GEMSTONE_TAG + gemstoneSlot, RAINBOW_GEMSTONE_TAG);
	}

	public static boolean hasRainbowGemstone(ItemStack itemStack, int gemstoneSlot) {
		var itemTag = itemStack.getTag();
		if (itemTag == null) {
			return false;
		}
		return itemTag.getString(GEMSTONE_TAG + gemstoneSlot).equals(RAINBOW_GEMSTONE_TAG);
	}

	public static boolean isRainbowGemstoneBonusSet(ItemStack itemStack, int gemstoneSlot) {
		return itemStack.hasTag() && itemStack.getTag().contains(RAINBOW_GEMSTONE_BONUS_TAG + gemstoneSlot);
	}

	public static void setRainbowGemstoneBonus(ItemStack itemStack, int gemstoneSlot) {
		var random = new Random();
		var bonusTag = new CompoundTag();
		var gemstones = new Item[] { SkillTreeItems.LIGHT_GEMSTONE.get(), SkillTreeItems.SOOTHING_GEMSTONE.get(), SkillTreeItems.STURDY_GEMSTONE.get() };
		var gemstone = gemstones[random.nextInt(gemstones.length)];
		var gemstoneId = ForgeRegistries.ITEMS.getKey(gemstone).toString();
		bonusTag.putInt("BonusSlot", random.nextInt(7));
		bonusTag.putString("Gemstone", gemstoneId);
		itemStack.getOrCreateTag().put(RAINBOW_GEMSTONE_BONUS_TAG + gemstoneSlot, bonusTag);
	}

	@Nullable
	public static Triple<Attribute, Double, Operation> getRainbowGemstoneBonus(ItemStack itemStack, int gemstoneSlot, EquipmentSlot slotType) {
		if (ItemHelper.isWeapon(itemStack) && slotType != EquipmentSlot.MAINHAND) {
			return null;
		} else if (ItemHelper.isShield(itemStack) && slotType != EquipmentSlot.OFFHAND) {
			return null;
		} else if (ItemHelper.isHelmet(itemStack) && slotType != EquipmentSlot.HEAD) {
			return null;
		} else if (ItemHelper.isChestplate(itemStack) && slotType != EquipmentSlot.CHEST) {
			return null;
		} else if (ItemHelper.isLeggings(itemStack) && slotType != EquipmentSlot.LEGS) {
			return null;
		} else if (ItemHelper.isBoots(itemStack) && slotType != EquipmentSlot.FEET) {
			return null;
		} else if (ItemHelper.isBow(itemStack) && slotType != EquipmentSlot.MAINHAND) {
			return null;
		}
		var bonusTag = itemStack.getOrCreateTag().getCompound(RAINBOW_GEMSTONE_BONUS_TAG + gemstoneSlot);
		var gemstoneId = new ResourceLocation(bonusTag.getString("Gemstone"));
		var gemstoneItem = (GemstoneItem) ForgeRegistries.ITEMS.getValue(gemstoneId);
		var bonusSlot = bonusTag.getInt("BonusSlot");
		switch (bonusSlot) {
			case 0:
				return gemstoneItem.getHelmetBonus();
			case 1:
				return gemstoneItem.getChestplateBonus();
			case 2:
				return gemstoneItem.getLeggingsBonus();
			case 3:
				return gemstoneItem.getBootsBonus();
			case 4:
				return gemstoneItem.getWeaponBonus();
			case 5:
				return gemstoneItem.getShieldBonus();
			case 6:
				return gemstoneItem.getBowBonus();
			default:
				return null;
		}
	}

	public static Triple<Attribute, Double, Operation> getRainbowGemstoneBonus(ItemStack itemStack, int gemstoneSlot) {
		return getRainbowGemstoneBonus(itemStack, gemstoneSlot, null);
	}

	public static void setAdditionalGemstoneSlot(ItemStack itemStack) {
		itemStack.getOrCreateTag().putBoolean(ADDITIONAL_GEMSTONES_TAG, true);
	}

	public static boolean hasAdditionalGemstoneSlot(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(ADDITIONAL_GEMSTONES_TAG);
	}

	public static GemstoneItem getGemstone(ItemStack itemStack, int gemstoneSlot) {
		var gemstoneId = new ResourceLocation(itemStack.getTag().getString(GEMSTONE_TAG + gemstoneSlot));
		return (GemstoneItem) ForgeRegistries.ITEMS.getValue(gemstoneId);
	}

	public static void setGemstoneStrength(ItemStack itemStack, double gemstoneStrengthBonus, int gemstoneSlot) {
		itemStack.getOrCreateTag().putDouble(GEMSTONE_STRENGTH_TAG + gemstoneSlot, gemstoneStrengthBonus);
	}

	public static double getGemstoneStrength(ItemStack itemStack, int gemstoneSlot) {
		return itemStack.hasTag() ? itemStack.getTag().getDouble(GEMSTONE_STRENGTH_TAG + gemstoneSlot) : 0;
	}

	public static int getGemstonesCount(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return 0;
		}
		var gemstones = 0;
		for (var slot = 0; slot < 3; slot++) {
			if (!hasGemstone(itemStack, slot)) {
				break;
			}
			gemstones++;
		}
		return gemstones;
	}

	public static int getEmptyGemstoneSlots(@NotNull ItemStack itemStack, @Nullable Player player) {
		var maximumSlots = 1;
		if (hasAdditionalGemstoneSlot(itemStack)) {
			maximumSlots++;
		}
		if (player != null) {
			maximumSlots += PlayerHelper.getAdditionalGemstoneSlots(player);
		}
		var emptySlots = maximumSlots;
		for (var slot = 0; slot < maximumSlots; slot++) {
			if (hasGemstone(itemStack, slot)) {
				emptySlots--;
			}
		}
		return emptySlots;
	}

	@Nullable
	public static Triple<Attribute, Double, Operation> getAttributeBonus(ItemStack itemStack, GemstoneItem gemstoneItem, @Nullable EquipmentSlot slotType) {
		if (ItemHelper.isWeapon(itemStack) && (slotType == EquipmentSlot.MAINHAND || slotType == null)) {
			return gemstoneItem.getWeaponBonus();
		} else if (ItemHelper.isShield(itemStack) && (slotType == EquipmentSlot.OFFHAND || slotType == null)) {
			return gemstoneItem.getShieldBonus();
		} else if (ItemHelper.isHelmet(itemStack) && (slotType == EquipmentSlot.HEAD || slotType == null)) {
			return gemstoneItem.getHelmetBonus();
		} else if (ItemHelper.isChestplate(itemStack) && (slotType == EquipmentSlot.CHEST || slotType == null)) {
			return gemstoneItem.getChestplateBonus();
		} else if (ItemHelper.isLeggings(itemStack) && (slotType == EquipmentSlot.LEGS || slotType == null)) {
			return gemstoneItem.getLeggingsBonus();
		} else if (ItemHelper.isBoots(itemStack) && (slotType == EquipmentSlot.FEET || slotType == null)) {
			return gemstoneItem.getBootsBonus();
		} else if (ItemHelper.isBow(itemStack) && (slotType == EquipmentSlot.MAINHAND || slotType == null)) {
			return gemstoneItem.getBowBonus();
		}

		return null;
	}

	@Nullable
	public static Triple<Attribute, Double, Operation> getAttributeBonus(ItemStack itemStack, GemstoneItem gemstoneItem) {
		return getAttributeBonus(itemStack, gemstoneItem, null);
	}
}
