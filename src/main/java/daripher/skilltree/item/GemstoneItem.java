package daripher.skilltree.item;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.util.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class GemstoneItem extends Item {
	private static final Logger LOGGER = LogUtils.getLogger();
	protected static final String GEMSTONES_TAG = "GEMSTONES";
	protected static final String GEMSTONE_TAG = "GEMSTONE";
	protected static final String ATTRIBUTE_TAG = "ATTRIBUTE";
	protected static final String AMOUNT_TAG = "AMOUNT";
	protected static final String OPERATION_TAG = "OPERATION";
	protected static final String ADDITIONAL_GEMSTONES_TAG = "ADDITIONAL_GEMSTONES";
	private final int gemstoneColor;

	public GemstoneItem(int gemstoneColor) {
		super(new Properties().tab(CreativeModeTab.TAB_MATERIALS));
		this.gemstoneColor = gemstoneColor;
	}

	@Override
	public MutableComponent getName(ItemStack itemStack) {
		return applyGemstoneColorStyle(super.getName(itemStack));
	}

	public MutableComponent applyGemstoneColorStyle(Component name) {
		return Component.literal("").append(name).withStyle(Style.EMPTY.withColor(gemstoneColor));
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level level, List<Component> components, TooltipFlag tooltipFlag) {
		var tooltip = Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.DARK_PURPLE).withStyle(ChatFormatting.ITALIC);
		components.add(tooltip);
	}

	public boolean canInsertInto(Player currentPlayer, ItemStack baseItem, int gemstoneSlot) {
		return !GemstoneItem.hasGemstone(baseItem, gemstoneSlot);
	}

	public void insertInto(Player player, ItemStack itemStack, int gemstoneSlot, double gemstoneStrength) {
		var gemstoneTag = (CompoundTag) null;
		var gemstonesTagList = getGemstonesTagList(itemStack);
		if (gemstonesTagList.size() > gemstoneSlot) {
			gemstoneTag = gemstonesTagList.getCompound(gemstoneSlot);
		}
		if (gemstoneTag == null) {
			gemstoneTag = new CompoundTag();
		}
		var gemstoneBonus = getGemstoneBonus(player, itemStack);
		if (gemstoneBonus == null) {
			LOGGER.error("Cannot insert gemstone into {}", itemStack.getItem());
			LOGGER.error("Slot: {}", Player.getEquipmentSlotForItem(itemStack));
			return;
		}
		var attribute = gemstoneBonus.getLeft();
		var attributeId = ForgeRegistries.ATTRIBUTES.getKey(attribute).toString();
		var amount = gemstoneBonus.getMiddle() * (1 + gemstoneStrength);
		var operation = gemstoneBonus.getRight().toString();
		var gemstoneId = ForgeRegistries.ITEMS.getKey(this).toString();
		gemstoneTag.putString(GEMSTONE_TAG, gemstoneId);
		gemstoneTag.putString(ATTRIBUTE_TAG, attributeId);
		gemstoneTag.putDouble(AMOUNT_TAG, amount);
		gemstoneTag.putString(OPERATION_TAG, operation);
		gemstonesTagList.add(gemstoneSlot, gemstoneTag);
		itemStack.getTag().put(GEMSTONES_TAG, gemstonesTagList);
	}

	protected abstract Triple<Attribute, Double, Operation> getGemstoneBonus(Player player, ItemStack itemStack);

	protected static ListTag getGemstonesTagList(ItemStack itemStack) {
		return itemStack.getOrCreateTag().getList(GEMSTONES_TAG, new CompoundTag().getId());
	}

	public static boolean hasGemstone(ItemStack itemStack, int gemstoneSlot) {
		if (!itemStack.hasTag()) {
			return false;
		}
		if (!itemStack.getTag().contains(GEMSTONES_TAG)) {
			return false;
		}
		var gemstonesTagList = getGemstonesTagList(itemStack);
		if (gemstonesTagList.size() <= gemstoneSlot) {
			return false;
		}
		if (gemstonesTagList.get(gemstoneSlot) == null) {
			return false;
		}
		return true;
	}

	public static Triple<Attribute, Double, Operation> getAttributeBonus(ItemStack itemStack, int gemstoneSlot) {
		if (!hasGemstone(itemStack, gemstoneSlot)) {
			return null;
		}
		var gemstoneTag = (CompoundTag) getGemstonesTagList(itemStack).get(gemstoneSlot);
		var attributeId = gemstoneTag.getString(ATTRIBUTE_TAG);
		var attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeId));
		var amount = gemstoneTag.getDouble(AMOUNT_TAG);
		var operation = Operation.valueOf(gemstoneTag.getString(OPERATION_TAG));
		var attributeBonus = Triple.of(attribute, amount, operation);
		return attributeBonus;
	}

	public static GemstoneItem getGemstone(ItemStack itemStack, int gemstoneSlot) {
		if (!hasGemstone(itemStack, gemstoneSlot)) {
			return null;
		}
		var gemstoneTag = (CompoundTag) getGemstonesTagList(itemStack).get(gemstoneSlot);
		var gemstoneId = gemstoneTag.getString(GEMSTONE_TAG);
		var gemstoneItem = (GemstoneItem) ForgeRegistries.ITEMS.getValue(new ResourceLocation(gemstoneId));
		return gemstoneItem;
	}

	public static void setAdditionalGemstoneSlot(ItemStack itemStack) {
		itemStack.getOrCreateTag().putBoolean(ADDITIONAL_GEMSTONES_TAG, true);
	}

	public static boolean hasAdditionalGemstoneSlot(ItemStack itemStack) {
		return itemStack.hasTag() && itemStack.getTag().contains(ADDITIONAL_GEMSTONES_TAG);
	}

	public static int getGemstonesCount(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return 0;
		}
		var gemstones = 0;
		for (var gemstoneSlot = 0; gemstoneSlot < 3; gemstoneSlot++) {
			if (!hasGemstone(itemStack, gemstoneSlot)) {
				break;
			}
			gemstones++;
		}
		return gemstones;
	}

	public static int getEmptyGemstoneSlots(@NotNull ItemStack itemStack, @Nullable Player player) {
		var maximumSlots = getMaximumGemstoneSlots(itemStack, player);
		var emptySlots = maximumSlots;
		for (var slot = 0; slot < maximumSlots; slot++) {
			if (hasGemstone(itemStack, slot)) {
				emptySlots--;
			}
		}
		return emptySlots;
	}

	public static int getMaximumGemstoneSlots(@NotNull ItemStack itemStack, @Nullable Player player) {
		var maximumSlots = 1;
		if (GemstoneItem.hasAdditionalGemstoneSlot(itemStack)) {
			maximumSlots++;
		}
		if (player == null) {
			return maximumSlots;
		}
		maximumSlots += (int) player.getAttributeValue(SkillTreeAttributes.MAXIMUM_GEMSTONE_SLOTS_BONUS.get());
		if (ItemHelper.isChestplate(itemStack)) {
			maximumSlots += (int) player.getAttributeValue(SkillTreeAttributes.MAXIMUM_CHESTPLATE_GEMSTONE_SLOTS_BONUS.get());
		}
		if (ItemHelper.isWeapon(itemStack) || ItemHelper.isBow(itemStack)) {
			maximumSlots += (int) player.getAttributeValue(SkillTreeAttributes.MAXIMUM_WEAPON_GEMSTONE_SLOTS_BONUS.get());
		}
		return maximumSlots;
	}
}
