package daripher.skilltree.gem;

import java.util.HashSet;
import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.GemItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class GemHelper {
	private static final Logger LOGGER = LogUtils.getLogger();
	protected static final String GEMS_TAG = "GEMSTONES";
	protected static final String GEM_TAG = "GEMSTONE";
	protected static final String ATTRIBUTE_TAG = "ATTRIBUTE";
	protected static final String AMOUNT_TAG = "AMOUNT";
	protected static final String OPERATION_TAG = "OPERATION";
	protected static final String ADDITIONAL_GEMS_TAG = "ADDITIONAL_GEMSTONES";

	public static boolean hasGem(ItemStack itemStack, int gemSlot) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) return false;
		}
		if (!itemStack.hasTag()) return false;
		if (!itemStack.getTag().contains(GEMS_TAG)) return false;
		var gemsTagList = getGemsListTag(itemStack);
		if (gemsTagList.size() <= gemSlot) return false;
		return gemsTagList.get(gemSlot) != null;
	}

	public static void insertGem(Player player, ItemStack itemStack, GemItem gem, int gemSlot, double gemPower) {
		CompoundTag gemTag = new CompoundTag();
		ListTag gemsTagList = getGemsListTag(itemStack);
		if (gemsTagList.size() > gemSlot) gemTag = gemsTagList.getCompound(gemSlot);
		Optional<Pair<Attribute, AttributeModifier>> optionalBonus = gem.getGemBonus(player, itemStack);
		if (!optionalBonus.isPresent()) {
			LOGGER.error("Cannot insert gem into {}", itemStack.getItem());
			LOGGER.error("Slot: {}", Player.getEquipmentSlotForItem(itemStack));
			return;
		}
		Pair<Attribute, AttributeModifier> bonus = optionalBonus.get();
		gemTag.putString(GEM_TAG, ForgeRegistries.ITEMS.getKey(gem).toString());
		gemTag.putString(ATTRIBUTE_TAG, ForgeRegistries.ATTRIBUTES.getKey(bonus.getLeft()).toString());
		gemTag.putDouble(AMOUNT_TAG, bonus.getRight().getAmount() * (1 + gemPower));
		gemTag.putString(OPERATION_TAG, bonus.getRight().getOperation().toString());
		gemsTagList.add(gemSlot, gemTag);
		itemStack.getTag().put(GEMS_TAG, gemsTagList);
	}

	public static void removeGems(ItemStack itemStack) {
		itemStack.getTag().remove(GEMS_TAG);
	}

	public static Optional<Pair<Attribute, AttributeModifier>> getAttributeBonus(ItemStack itemStack, int socket) {
		if (!GemHelper.hasGem(itemStack, socket)) return Optional.empty();
		CompoundTag gemsTag = (CompoundTag) getGemsListTag(itemStack).get(socket);
		if (gemsTag == null) return Optional.empty();
		String attributeId = gemsTag.getString(ATTRIBUTE_TAG);
		if (attributeId.isEmpty()) return Optional.empty();
		Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeId));
		if (attribute == null) return Optional.empty();
		Operation operation = Operation.valueOf(gemsTag.getString(OPERATION_TAG));
		if (operation == null) return Optional.empty();
		double amount = gemsTag.getDouble(AMOUNT_TAG);
		if (amount == 0) return Optional.empty();
		return Optional.of(Pair.of(attribute, new AttributeModifier("Gem Bonus", amount, operation)));
	}

	public static Optional<GemItem> getGem(ItemStack itemStack, int socket) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) return Optional.empty();
		}
		if (!hasGem(itemStack, socket)) return null;
		CompoundTag gemTag = (CompoundTag) getGemsListTag(itemStack).get(socket);
		String gemId = gemTag.getString(GEM_TAG);
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(gemId));
		if (!(item instanceof GemItem gem)) return Optional.empty();
		return Optional.of(gem);
	}

	public static void setAdditionalSocket(ItemStack itemStack) {
		itemStack.getOrCreateTag().putBoolean(ADDITIONAL_GEMS_TAG, true);
	}

	public static boolean hasAdditionalSocket(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(ADDITIONAL_GEMS_TAG);
	}

	public static int getGemsCount(ItemStack itemStack) {
		if (ModList.get().isLoaded("apotheosis")) {
			return ApotheosisCompatibility.ISNTANCE.getGemsCount(itemStack);
		}
		if (itemStack.isEmpty()) return 0;
		var gemsCount = 0;
		var socket = 0;
		while (hasGem(itemStack, socket)) {
			gemsCount++;
			socket++;
		}
		return gemsCount;
	}

	public static int getGemsCount(ItemStack itemStack, Item gem) {
		if (ModList.get().isLoaded("apotheosis")) {
			return 0; // TODO: implement
		}
		if (itemStack.isEmpty()) return 0;
		var gemsCount = 0;
		var socket = 0;
		while (hasGem(itemStack, socket)) {
			if (getGem(itemStack, socket).filter(gem::equals).isPresent()) gemsCount++;
			socket++;
		}
		return gemsCount;
	}

	public static boolean hasOnlyDifferentGems(ItemStack itemStack) {
		if (ModList.get().isLoaded("apotheosis")) {
			return false; // TODO: implement
		}
		if (!hasGem(itemStack, 0)) return false;
		var gems = new HashSet<>();
		var socket = 0;
		while (hasGem(itemStack, socket)) {
			var gem = getGem(itemStack, socket);
			if (gems.contains(gem)) return false;
			socket++;
		}
		return true;
	}

	public static boolean hasOnlySameGems(ItemStack itemStack) {
		if (ModList.get().isLoaded("apotheosis")) {
			return false; // TODO: implement
		}
		if (!hasGem(itemStack, 0)) return false;
		var socket = 1;
		while (hasGem(itemStack, socket)) {
			if (getGem(itemStack, socket) != getGem(itemStack, 0)) return false;
			socket++;
		}
		return true;
	}

	public static int getEmptySockets(@NotNull ItemStack itemStack, @Nullable Player player) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) return 0;
		}
		var sockets = getMaximumSockets(itemStack, player);
		var emptySockets = sockets;
		for (var socket = 0; socket < sockets; socket++) {
			if (hasGem(itemStack, socket)) emptySockets--;
		}
		return emptySockets;
	}

	public static int getMaximumSockets(@NotNull ItemStack itemStack, @Nullable Player player) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) return 0;
		}
		var sockets = 1;
		if (hasAdditionalSocket(itemStack)) sockets++;
		if (player == null) return sockets;
		sockets += (int) player.getAttributeValue(SkillTreeAttributes.MAXIMUM_SOCKETS.get());
		if (ItemHelper.isChestplate(itemStack)) {
			sockets += (int) player.getAttributeValue(SkillTreeAttributes.MAXIMUM_CHESTPLATE_SOCKETS.get());
		}
		if (ItemHelper.isWeapon(itemStack)) {
			sockets += (int) player.getAttributeValue(SkillTreeAttributes.MAXIMUM_WEAPON_SOCKETS.get());
		}
		return sockets;
	}

	protected static ListTag getGemsListTag(ItemStack itemStack) {
		return itemStack.getOrCreateTag().getList(GEMS_TAG, new CompoundTag().getId());
	}
}
