package daripher.skilltree.skill.bonus.item;

import com.google.common.collect.ImmutableList;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import daripher.skilltree.skill.bonus.player.ItemUpgradeLimitBonusesBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemBonusHandler {
    public static final String UPGRADE_BONUSES_TAG_NAME = "UpgradeBonuses";
    public static final String CRAFTING_BONUSES_TAG_NAME = "CraftingBonuses";

    @SubscribeEvent
    public static void addItemBonusTooltips(ItemTooltipEvent event) {
        List<Component> toolTip = event.getToolTip();
        List<ItemBonus<?>> itemBonuses = getItemBonuses(event.getItemStack());
        if (!itemBonuses.isEmpty()) {
            toolTip.add(Component.empty());
        }
        for (ItemBonus<?> itemBonus : itemBonuses) {
            Style style = TooltipHelper.getItemUpgradeStyle();
            itemBonus.addTooltip(tooltip -> toolTip.add(tooltip.withStyle(style)));
        }
    }

    @SubscribeEvent
    public static void addEquipmentAttributeBonuses(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        for (ItemBonus<?> itemBonus : getItemBonuses(event.getFrom(), EquipmentBonus.class)) {
            EquipmentBonus bonus = (EquipmentBonus) itemBonus;
            if (!(bonus.getSkillBonus() instanceof AttributeBonus attributeBonus)) {
                continue;
            }
            AttributeInstance attributeInstance = entity.getAttribute(attributeBonus.getAttribute());
            if (attributeInstance == null) {
                continue;
            }
            attributeInstance.removeModifier(attributeBonus.getModifier().getId());
        }
        for (ItemBonus<?> itemBonus : getItemBonuses(event.getTo(), EquipmentBonus.class)) {
            EquipmentBonus bonus = (EquipmentBonus) itemBonus;
            if (!(bonus.getSkillBonus() instanceof AttributeBonus attributeBonus)) {
                continue;
            }
            if (attributeBonus.isDynamic()) {
                continue;
            }
            AttributeInstance attributeInstance = entity.getAttribute(attributeBonus.getAttribute());
            if (attributeInstance == null) {
                continue;
            }
            if (attributeInstance.hasModifier(attributeBonus.getModifier())) {
                continue;
            }
            attributeInstance.addTransientModifier(attributeBonus.getModifier());
        }
    }

    public static void addCraftingBonuses(ItemStack itemStack, GroupedItemBonus itemBonuses) {
        List<ItemBonus<?>> craftingBonuses = getBonusesFromTag(itemStack, CRAFTING_BONUSES_TAG_NAME);
        craftingBonuses.add(itemBonuses);
        setCraftingBonuses(itemStack, craftingBonuses);
    }

    public static List<ItemBonus<?>> getItemBonuses(ItemStack itemStack) {
        if (!itemStack.hasTag()) {
            return ImmutableList.of();
        }
        List<ItemBonus<?>> list = new ArrayList<>();
        list.addAll(getUpgradeBonuses(itemStack));
        list.addAll(getCraftingBonuses(itemStack));
        return list;
    }

    private static List<ItemBonus<?>> getCraftingBonuses(ItemStack itemStack) {
        return getBonusesFromTag(itemStack, CRAFTING_BONUSES_TAG_NAME);
    }

    private static List<ItemBonus<?>> getUpgradeBonuses(ItemStack itemStack) {
        return getBonusesFromTag(itemStack, UPGRADE_BONUSES_TAG_NAME);
    }

    private static List<ItemBonus<?>> getBonusesFromTag(ItemStack itemStack, String subTagName) {
        CompoundTag stackTag = itemStack.getOrCreateTag();
        List<ItemBonus<?>> itemBonuses = new ArrayList<>();
        if (!stackTag.contains(subTagName, Tag.TAG_LIST)) {
            return new ArrayList<>();
        }
        ListTag bonusesTagList = stackTag.getList(subTagName, Tag.TAG_COMPOUND);
        bonusesTagList.stream().map(CompoundTag.class::cast).forEach(bonusTag -> itemBonuses.add(deserializeBonus(bonusTag)));
        return itemBonuses;
    }

    public static List<ItemBonus<?>> getItemBonuses(ItemStack stack, Class<?> type) {
        List<ItemBonus<?>> bonuses = new ArrayList<>();
        for (ItemBonus<?> bonus : getItemBonuses(stack)) {
            if (bonus instanceof GroupedItemBonus listBonus) {
                bonuses.addAll(listBonus.getInnerBonuses());
            } else {
                bonuses.add(bonus);
            }
        }
        return bonuses.stream().filter(type::isInstance).toList();
    }

    public static void setUpgradeBonuses(ItemStack stack, List<ItemBonus<?>> bonuses) {
        setTagBonuses(stack, bonuses, UPGRADE_BONUSES_TAG_NAME);
    }

    public static void setCraftingBonuses(ItemStack stack, List<ItemBonus<?>> bonuses) {
        setTagBonuses(stack, bonuses, CRAFTING_BONUSES_TAG_NAME);
    }

    private static void setTagBonuses(ItemStack stack, List<ItemBonus<?>> bonuses, String tagName) {
        ListTag bonusesTagList = new ListTag();
        for (ItemBonus<?> itemBonus : bonuses) {
            bonusesTagList.add(serializeBonus(itemBonus));
        }
        stack.getOrCreateTag().put(tagName, bonusesTagList);
    }

    private static CompoundTag serializeBonus(ItemBonus<? extends ItemBonus<?>> bonus) {
        ItemBonus.Serializer serializer = bonus.getSerializer();
        CompoundTag bonusTag = serializer.serialize(bonus);
        ResourceLocation id = PSTRegistries.ITEM_BONUSES.get().getKey(serializer);
        bonusTag.putString("type", Objects.requireNonNull(id).toString());
        return bonusTag;
    }

    private static ItemBonus<?> deserializeBonus(CompoundTag tag) {
        if (!tag.contains("type")) {
            return null;
        }
        ResourceLocation id = ResourceLocation.parse(tag.getString("type"));
        ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(id);
        if (serializer == null) {
            return null;
        }
        try {
            return serializer.deserialize(tag);
        } catch (Exception exception) {
            String errorMessage = "Couldn't deserialize item bonus from " + tag;
            SkillTreeMod.LOGGER.error(errorMessage, exception);
            return null;
        }
    }

    public static int getCraftedBonusLimit(ItemStack itemStack, @Nullable Player player) {
        int limit = 1;
        if (player != null) {
            limit += SkillBonusHandler.getSkillBonuses(player, ItemUpgradeLimitBonusesBonus.class).stream()
                    .filter(bonus -> bonus.getItemCondition().test(itemStack)).map(ItemUpgradeLimitBonusesBonus::getAmount)
                    .reduce(Integer::sum).orElse(0);
        }
        return limit;
    }

    @NotNull
    @SuppressWarnings({"rawtypes", "unchecked", "SuspiciousMethodCalls"})
    public static <T> List<T> mergeItemBonuses(List<T> bonuses) {
        List<T> mergedBonuses = new ArrayList<>();
        for (T bonus : bonuses) {
            ItemBonus itemBonus = (ItemBonus) bonus;
            Optional<ItemBonus> mergeTarget = mergedBonuses.stream().map(ItemBonus.class::cast).filter(itemBonus::canMerge).findAny();
            if (mergeTarget.isPresent()) {
                mergedBonuses.remove(mergeTarget.get());
                mergedBonuses.add((T) mergeTarget.get().copy().merge(itemBonus));
            } else {
                mergedBonuses.add((T) itemBonus);
            }
        }
        return mergedBonuses;
    }

    public static GroupedItemBonus mergeGroupedItemBonuses(GroupedItemBonus itemBonus1, GroupedItemBonus itemBonus2) {
        ArrayList<ItemBonus<?>> innerBonuses = new ArrayList<>();
        innerBonuses.addAll(itemBonus1.getInnerBonuses());
        innerBonuses.addAll(itemBonus2.getInnerBonuses());
        ItemBonusHandler.mergeItemBonuses(innerBonuses);
        return new GroupedItemBonus(innerBonuses);
    }
}
