package daripher.skilltree.util;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.item.GemstoneItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class GemstoneHelper {
	private static final String GEMSTONE_TAG = "Gemstone";
	private static final String GEMSTONE_STRENGTH_TAG = "GemstoneStrength";

	public static void applyGemstone(ItemStack itemStack, GemstoneItem gemstoneItem, int gemstoneSlot) {
		var gemstoneId = ForgeRegistries.ITEMS.getKey(gemstoneItem).toString();
		itemStack.getOrCreateTag().putString(GEMSTONE_TAG + gemstoneSlot, gemstoneId);
	}

	public static boolean hasGemstone(ItemStack itemStack, int gemstoneSlot) {
		var hasGemstoneTag = itemStack.hasTag() && itemStack.getTag().contains(GEMSTONE_TAG + gemstoneSlot);
		return hasGemstoneTag;
	}

	public static GemstoneItem getGemstone(ItemStack itemStack, int gemstoneSlot) {
		var gemstoneId = new ResourceLocation(itemStack.getTag().getString(GEMSTONE_TAG + gemstoneSlot));
		return (GemstoneItem) ForgeRegistries.ITEMS.getValue(gemstoneId);
	}

	public static void setGemstoneStrength(ItemStack itemStack, double gemstoneStrengthBonus, int gemstoneSlot) {
		while (!hasGemstone(itemStack, gemstoneSlot)) {
			gemstoneSlot--;
		}

		itemStack.getOrCreateTag().putDouble(GEMSTONE_STRENGTH_TAG + gemstoneSlot, gemstoneStrengthBonus);
	}

	public static double getGemstoneStrength(ItemStack itemStack, int gemstoneSlot) {
		return itemStack.hasTag() ? itemStack.getTag().getDouble(GEMSTONE_STRENGTH_TAG + gemstoneSlot) : 0;
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
