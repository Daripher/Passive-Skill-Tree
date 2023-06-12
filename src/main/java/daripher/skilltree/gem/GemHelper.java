package daripher.skilltree.gem;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.item.gem.GemItem;
import daripher.skilltree.util.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class GemHelper {
	private static final Logger LOGGER = LogUtils.getLogger();
	protected static final String GEMSTONES_TAG = "GEMSTONES";
	protected static final String GEMSTONE_TAG = "GEMSTONE";
	protected static final String ATTRIBUTE_TAG = "ATTRIBUTE";
	protected static final String AMOUNT_TAG = "AMOUNT";
	protected static final String OPERATION_TAG = "OPERATION";
	protected static final String ADDITIONAL_GEMSTONES_TAG = "ADDITIONAL_GEMSTONES";

	public static boolean hasGem(ItemStack itemStack, int gemSlot) {
		if (ModList.get().isLoaded("apotheosis")) {
			return false;
		}
		if (!itemStack.hasTag()) {
			return false;
		}
		if (!itemStack.getTag().contains(GEMSTONES_TAG)) {
			return false;
		}
		var gemsTagList = getGemsListTag(itemStack);
		if (gemsTagList.size() <= gemSlot) {
			return false;
		}
		if (gemsTagList.get(gemSlot) == null) {
			return false;
		}
		return true;
	}

	public static void insertGem(Player player, ItemStack itemStack, GemItem gem, int gemSlot, double gemPower) {
		var gemTag = (CompoundTag) null;
		var gemsTagList = getGemsListTag(itemStack);
		if (gemsTagList.size() > gemSlot) {
			gemTag = gemsTagList.getCompound(gemSlot);
		}
		if (gemTag == null) {
			gemTag = new CompoundTag();
		}
		var gemBonus = gem.getGemBonus(player, itemStack);
		if (gemBonus == null) {
			LOGGER.error("Cannot insert gem into {}", itemStack.getItem());
			LOGGER.error("Slot: {}", Player.getEquipmentSlotForItem(itemStack));
			return;
		}
		var attribute = gemBonus.getLeft();
		var attributeId = ForgeRegistries.ATTRIBUTES.getKey(attribute).toString();
		var amount = gemBonus.getMiddle() * (1 + gemPower);
		var operation = gemBonus.getRight().toString();
		var gemId = ForgeRegistries.ITEMS.getKey(gem).toString();
		gemTag.putString(GEMSTONE_TAG, gemId);
		gemTag.putString(ATTRIBUTE_TAG, attributeId);
		gemTag.putDouble(AMOUNT_TAG, amount);
		gemTag.putString(OPERATION_TAG, operation);
		gemsTagList.add(gemSlot, gemTag);
		itemStack.getTag().put(GEMSTONES_TAG, gemsTagList);
	}

	public static void removeGems(ItemStack itemStack) {
		itemStack.getTag().remove(GEMSTONES_TAG);
	}

	public static Triple<Attribute, Double, Operation> getAttributeBonus(ItemStack itemStack, int gemstoneSlot) {
		var gemstoneTag = (CompoundTag) getGemsListTag(itemStack).get(gemstoneSlot);
		var attributeId = gemstoneTag.getString(ATTRIBUTE_TAG);
		var attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeId));
		var amount = gemstoneTag.getDouble(AMOUNT_TAG);
		var operation = Operation.valueOf(gemstoneTag.getString(OPERATION_TAG));
		var attributeBonus = Triple.of(attribute, amount, operation);
		return attributeBonus;
	}

	public static GemItem getGem(ItemStack itemStack, int gemstoneSlot) {
		if (ModList.get().isLoaded("apotheosis")) {
			return null;
		}
		if (!hasGem(itemStack, gemstoneSlot)) {
			return null;
		}
		var gemstoneTag = (CompoundTag) getGemsListTag(itemStack).get(gemstoneSlot);
		var gemstoneId = gemstoneTag.getString(GEMSTONE_TAG);
		var gemstoneItem = (GemItem) ForgeRegistries.ITEMS.getValue(new ResourceLocation(gemstoneId));
		return gemstoneItem;
	}

	public static void setAdditionalSocket(ItemStack itemStack) {
		itemStack.getOrCreateTag().putBoolean(ADDITIONAL_GEMSTONES_TAG, true);
	}

	public static boolean hasAdditionalSocket(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(ADDITIONAL_GEMSTONES_TAG);
	}

	public static int getGemsCount(ItemStack itemStack) {
		if (ModList.get().isLoaded("apotheosis")) {
			return ApotheosisCompatibility.ISNTANCE.getGemsCount(itemStack);
		}
		if (itemStack.isEmpty()) {
			return 0;
		}
		var gemstones = 0;
		for (var gemstoneSlot = 0; gemstoneSlot < 3; gemstoneSlot++) {
			if (!hasGem(itemStack, gemstoneSlot)) {
				break;
			}
			gemstones++;
		}
		return gemstones;
	}

	public static int getEmptyGemSlots(@NotNull ItemStack itemStack, @Nullable Player player) {
		if (ModList.get().isLoaded("apotheosis")) {
			return 0;
		}
		var maximumSlots = getMaximumGemstoneSlots(itemStack, player);
		var emptySlots = maximumSlots;
		for (var slot = 0; slot < maximumSlots; slot++) {
			if (hasGem(itemStack, slot)) {
				emptySlots--;
			}
		}
		return emptySlots;
	}

	public static int getMaximumGemstoneSlots(@NotNull ItemStack itemStack, @Nullable Player player) {
		if (ModList.get().isLoaded("apotheosis")) {
			return 0;
		}
		var maximumSlots = 1;
		if (hasAdditionalSocket(itemStack)) {
			maximumSlots++;
		}
		if (player == null) {
			return maximumSlots;
		}
		maximumSlots += (int) player.getAttributeValue(SkillTreeAttributes.MAXIMUM_SOCKETS.get());
		if (ItemHelper.isChestplate(itemStack)) {
			maximumSlots += (int) player.getAttributeValue(SkillTreeAttributes.MAXIMUM_CHESTPLATE_SOCKETS.get());
		}
		if (ItemHelper.isWeapon(itemStack) || ItemHelper.isBow(itemStack)) {
			maximumSlots += (int) player.getAttributeValue(SkillTreeAttributes.MAXIMUM_WEAPON_SOCKETS.get());
		}
		return maximumSlots;
	}

	protected static ListTag getGemsListTag(ItemStack itemStack) {
		return itemStack.getOrCreateTag().getList(GEMSTONES_TAG, new CompoundTag().getId());
	}
}
