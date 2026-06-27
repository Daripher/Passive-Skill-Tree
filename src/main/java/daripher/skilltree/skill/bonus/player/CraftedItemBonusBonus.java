package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.item.EquipmentBonus;
import daripher.skilltree.skill.bonus.item.GroupedItemBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemBonusHandler;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class CraftedItemBonusBonus implements SkillBonus<CraftedItemBonusBonus> {
    private @Nonnull ItemStackPredicate itemStackPredicate;
    private @Nonnull GroupedItemBonus itemBonuses;

    public CraftedItemBonusBonus(@Nonnull ItemStackPredicate itemStackPredicate, @Nonnull GroupedItemBonus itemBonuses) {
        this.itemStackPredicate = itemStackPredicate;
        this.itemBonuses = itemBonuses;
    }

    public void itemCrafted(ItemStack craftResult) {
        if (!itemStackPredicate.test(craftResult)) {
            return;
        }
        ItemBonusHandler.addCraftingBonuses(craftResult, itemBonuses);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.CRAFTED_ITEM_BONUS.get();
    }

    @Override
    public CraftedItemBonusBonus copy() {
        return new CraftedItemBonusBonus(itemStackPredicate, itemBonuses);
    }

    @Override
    public CraftedItemBonusBonus multiply(double multiplier) {
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof CraftedItemBonusBonus otherBonus)) {
            return false;
        }
        return Objects.equals(otherBonus.itemStackPredicate, this.itemStackPredicate);
    }

    @Override
    public SkillBonus<CraftedItemBonusBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof CraftedItemBonusBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        GroupedItemBonus mergedItemBonuses = ItemBonusHandler.mergeGroupedItemBonuses(itemBonuses, otherBonus.itemBonuses);
        return new CraftedItemBonusBonus(itemStackPredicate, mergedItemBonuses);
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        Style tooltipStyle = TooltipHelper.getSkillBonusStyle(isPositive());
        Component itemDescription = itemStackPredicate.getTooltip("plural");
        return Component.translatable(getDescriptionId(), itemDescription).withStyle(tooltipStyle);
    }

    @Override
    public List<MutableComponent> getFullTooltip() {
        List<MutableComponent> fullTooltip = new ArrayList<>();
        fullTooltip.add(getSimpleTooltip());
        itemBonuses.addTooltip(component -> {
            MutableComponent listItemPrefix = Component.translatable(getDescriptionId() + ".list_item_prefix");
            fullTooltip.add(listItemPrefix.append(component).withStyle(component.getStyle()));
        });
        return fullTooltip;
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<CraftedItemBonusBonus> consumer) {
        editor.addLabel(0, 0, "Item Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, itemStackPredicate).setResponder(condition -> selectItemCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Item Bonuses", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        addItemBonusWidgets(editor, consumer);
    }

    private void addItemBonusWidgets(SkillTreeEditor editor, Consumer<CraftedItemBonusBonus> consumer) {
        itemBonuses.addEditorWidgets(editor, itemBonuses -> {
            selectItemBonuses(consumer, itemBonuses);
            consumer.accept(this.copy());
        });
    }

    private void selectItemBonuses(Consumer<CraftedItemBonusBonus> consumer, GroupedItemBonus itemBonuses) {
        setItemBonuses(itemBonuses);
        consumer.accept(this.copy());
    }

    private void setItemBonuses(@Nonnull GroupedItemBonus itemBonuses) {
        this.itemBonuses = itemBonuses;
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<CraftedItemBonusBonus> consumer, ItemStackPredicate condition) {
        setItemCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<CraftedItemBonusBonus> consumer) {
        itemStackPredicate.addEditorWidgets(editor, c -> {
            setItemCondition(c);
            consumer.accept(this.copy());
        });
    }

    public void setItemCondition(@Nonnull ItemStackPredicate itemStackPredicate) {
        this.itemStackPredicate = itemStackPredicate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        CraftedItemBonusBonus that = (CraftedItemBonusBonus) obj;
        return Objects.equals(this.itemStackPredicate, that.itemStackPredicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStackPredicate);
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public CraftedItemBonusBonus deserialize(JsonObject json) throws JsonParseException {
            ItemStackPredicate condition = SerializationHelper.deserializeItemPredicate(json);
            ItemBonus<?> itemBonus = SerializationHelper.deserializeItemBonus(json);
            if (!(itemBonus instanceof GroupedItemBonus itemBonuses)) {
                throw new IllegalArgumentException();
            }
            return new CraftedItemBonusBonus(condition, itemBonuses);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof CraftedItemBonusBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            SerializationHelper.serializeItemPredicate(json, aBonus.itemStackPredicate);
            SerializationHelper.serializeItemBonus(json, aBonus.itemBonuses);
        }

        @Override
        public CraftedItemBonusBonus deserialize(CompoundTag tag) {
            ItemStackPredicate condition = SerializationHelper.deserializeItemPredicate(tag);
            ItemBonus<?> itemBonus = SerializationHelper.deserializeItemBonus(tag);
            if (!(itemBonus instanceof GroupedItemBonus itemBonuses)) {
                throw new IllegalArgumentException();
            }
            return new CraftedItemBonusBonus(condition, itemBonuses);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof CraftedItemBonusBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemPredicate(tag, aBonus.itemStackPredicate);
            SerializationHelper.serializeItemBonus(tag, aBonus.itemBonuses);
            return tag;
        }

        @Override
        public CraftedItemBonusBonus deserialize(FriendlyByteBuf buf) {
            ItemStackPredicate itemStackPredicate = NetworkHelper.readItemPredicate(buf);
            ItemBonus<?> itemBonus = NetworkHelper.readItemBonus(buf);
            if (!(itemBonus instanceof GroupedItemBonus itemBonuses)) {
                throw new IllegalArgumentException();
            }
            return new CraftedItemBonusBonus(itemStackPredicate, itemBonuses);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof CraftedItemBonusBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            NetworkHelper.writeItemPredicate(buf, aBonus.itemStackPredicate);
            NetworkHelper.writeItemBonus(buf, aBonus.itemBonuses);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            EquipmentPredicate itemStackPredicate = new EquipmentPredicate(EquipmentPredicate.Type.BOW);
            ItemBonus<?> itemBonus = new EquipmentBonus(new CritChanceBonus(0.05f));
            ArrayList<ItemBonus<?>> itemBonusList = new ArrayList<>();
            itemBonusList.add(itemBonus);
            GroupedItemBonus itemBonuses = new GroupedItemBonus(itemBonusList);
            return new CraftedItemBonusBonus(itemStackPredicate, itemBonuses);
        }
    }
}
