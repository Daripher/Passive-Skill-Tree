package daripher.skilltree.item.gem;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import daripher.skilltree.api.HasAdditionalSockets;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.item.ItemHelper;
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
	protected static final String UUID_TAG = "ID";

	public static boolean hasGem(ItemStack stack, int socket) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled())
				return false;
		}
		if (!stack.hasTag())
			return false;
		if (!stack.getTag().contains(GEMS_TAG))
			return false;
		ListTag gems = getGemsListTag(stack);
		if (gems.size() <= socket)
			return false;
		return gems.get(socket) != null;
	}

	public static void insertGem(Player player, ItemStack stack, GemItem gem, int socket, double power) {
		CompoundTag gemTag = new CompoundTag();
		ListTag gemsTagList = getGemsListTag(stack);
		if (gemsTagList.size() > socket)
			gemTag = gemsTagList.getCompound(socket);
		Optional<Pair<Attribute, AttributeModifier>> optionalBonus = gem.getGemBonus(player, stack);
		if (!optionalBonus.isPresent()) {
			LOGGER.error("Cannot insert gem into {}", stack.getItem());
			LOGGER.error("Slot: {}", Player.getEquipmentSlotForItem(stack));
			return;
		}
		Pair<Attribute, AttributeModifier> bonus = optionalBonus.get();
		gemTag.putString(GEM_TAG, ForgeRegistries.ITEMS.getKey(gem).toString());
		gemTag.putString(ATTRIBUTE_TAG, ForgeRegistries.ATTRIBUTES.getKey(bonus.getLeft()).toString());
		gemTag.putDouble(AMOUNT_TAG, bonus.getRight().getAmount() * (1 + power));
		gemTag.putString(OPERATION_TAG, bonus.getRight().getOperation().toString());
		gemTag.putString(UUID_TAG, UUID.randomUUID().toString());
		gemsTagList.add(socket, gemTag);
		stack.getTag().put(GEMS_TAG, gemsTagList);
	}

	public static void removeGems(ItemStack itemStack) {
		itemStack.getTag().remove(GEMS_TAG);
	}

	public static Optional<Pair<Attribute, AttributeModifier>> getAttributeBonus(ItemStack itemStack, int socket) {
		if (!GemHelper.hasGem(itemStack, socket))
			return Optional.empty();
		CompoundTag gemTag = (CompoundTag) getGemsListTag(itemStack).get(socket);
		if (gemTag == null)
			return Optional.empty();
		String attributeId = gemTag.getString(ATTRIBUTE_TAG);
		if (attributeId.isEmpty())
			return Optional.empty();
		Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeId));
		if (attribute == null)
			return Optional.empty();
		Operation operation = Operation.valueOf(gemTag.getString(OPERATION_TAG));
		if (operation == null)
			return Optional.empty();
		double amount = gemTag.getDouble(AMOUNT_TAG);
		if (amount == 0)
			return Optional.empty();
		if (!gemTag.contains(UUID_TAG))
			gemTag.putString(UUID_TAG, UUID.randomUUID().toString());
		UUID id = UUID.fromString(gemTag.getString(UUID_TAG));
		return Optional.of(Pair.of(attribute, new AttributeModifier(id, "Gem Bonus", amount, operation)));
	}

	public static Optional<GemItem> getGem(ItemStack itemStack, int socket) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled())
				return Optional.empty();
		}
		if (!hasGem(itemStack, socket))
			return Optional.empty();
		CompoundTag gemTag = (CompoundTag) getGemsListTag(itemStack).get(socket);
		String gemId = gemTag.getString(GEM_TAG);
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(gemId));
		if (!(item instanceof GemItem gem))
			return Optional.empty();
		return Optional.of(gem);
	}

	public static int getGemsCount(ItemStack itemStack) {
		if (ModList.get().isLoaded("apotheosis")) {
			return ApotheosisCompatibility.ISNTANCE.getGemsCount(itemStack);
		}
		if (itemStack.isEmpty())
			return 0;
		int gemsCount = 0;
		int socket = 0;
		while (hasGem(itemStack, socket)) {
			gemsCount++;
			socket++;
		}
		return gemsCount;
	}

	public static int getEmptySockets(@NotNull ItemStack itemStack, @Nullable Player player) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled())
				return 0;
		}
		int sockets = getMaximumSockets(itemStack, player);
		int emptySockets = sockets;
		for (int socket = 0; socket < sockets; socket++) {
			if (hasGem(itemStack, socket))
				emptySockets--;
		}
		return emptySockets;
	}

	public static int getMaximumSockets(ItemStack stack, @Nullable Player player) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled())
				return 0;
		}
		int sockets = ItemHelper.getDefaultSockets(stack);
		if (ItemHelper.hasBonus(stack, ItemHelper.ADDITIONAL_SOCKETS)) {
			sockets += ItemHelper.getBonus(stack, ItemHelper.ADDITIONAL_SOCKETS);
		}
		if (stack.getItem() instanceof HasAdditionalSockets) {
			sockets += ((HasAdditionalSockets) stack.getItem()).getAdditionalSockets();
		}
		if (player != null)
			sockets += getPlayerSockets(stack, player);
		return sockets;
	}

	public static int getPlayerSockets(ItemStack stack, @Nullable Player player) {
		if (player == null)
			return 0;
		int sockets = 0;
		if (ItemHelper.isEquipment(stack)) {
			sockets += (int) player.getAttributeValue(PSTAttributes.MAXIMUM_EQUIPMENT_SOCKETS.get());
		}
		if (ItemHelper.isChestplate(stack)) {
			sockets += (int) player.getAttributeValue(PSTAttributes.MAXIMUM_CHESTPLATE_SOCKETS.get());
		}
		if (ItemHelper.isWeapon(stack)) {
			sockets += (int) player.getAttributeValue(PSTAttributes.MAXIMUM_WEAPON_SOCKETS.get());
		}
		if (ItemHelper.isRing(stack)) {
			sockets += (int) player.getAttributeValue(PSTAttributes.MAXIMUM_RING_SOCKETS.get());
		}
		return sockets;
	}

	protected static ListTag getGemsListTag(ItemStack itemStack) {
		return itemStack.getOrCreateTag().getList(GEMS_TAG, new CompoundTag().getId());
	}
}
