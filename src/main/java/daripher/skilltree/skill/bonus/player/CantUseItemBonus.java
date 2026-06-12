package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class CantUseItemBonus implements SkillBonus<CantUseItemBonus> {
    private @Nonnull ItemStackPredicate itemStackPredicate;

    public CantUseItemBonus(@Nonnull ItemStackPredicate itemStackPredicate) {
        this.itemStackPredicate = itemStackPredicate;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.CANT_USE_ITEM.get();
    }

    @Override
    public CantUseItemBonus copy() {
        return new CantUseItemBonus(itemStackPredicate);
    }

    @Override
    public CantUseItemBonus multiply(double multiplier) {
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof CantUseItemBonus otherBonus)) {
            return false;
        }
        return Objects.equals(otherBonus.itemStackPredicate, this.itemStackPredicate);
    }

    @Override
    public SkillBonus<CantUseItemBonus> merge(SkillBonus<?> other) {
        return this;
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        Component itemDescription = itemStackPredicate.getTooltip("plural");
        return Component.translatable(getDescriptionId(), itemDescription).withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<CantUseItemBonus> consumer) {
        editor.addLabel(0, 0, "Item Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, itemStackPredicate).setResponder(condition -> selectItemCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<CantUseItemBonus> consumer, ItemStackPredicate condition) {
        setItemCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<CantUseItemBonus> consumer) {
        itemStackPredicate.addEditorWidgets(editor, c -> {
            setItemCondition(c);
            consumer.accept(this.copy());
        });
    }

    public void setItemCondition(@Nonnull ItemStackPredicate itemStackPredicate) {
        this.itemStackPredicate = itemStackPredicate;
    }

    @Nonnull
    public ItemStackPredicate getItemCondition() {
        return itemStackPredicate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        CantUseItemBonus that = (CantUseItemBonus) obj;
        return Objects.equals(this.itemStackPredicate, that.itemStackPredicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStackPredicate);
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public CantUseItemBonus deserialize(JsonObject json) throws JsonParseException {
            ItemStackPredicate condition = SerializationHelper.deserializeItemPredicate(json);
            return new CantUseItemBonus(condition);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof CantUseItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            SerializationHelper.serializeItemPredicate(json, aBonus.itemStackPredicate);
        }

        @Override
        public CantUseItemBonus deserialize(CompoundTag tag) {
            ItemStackPredicate condition = SerializationHelper.deserializeItemPredicate(tag);
            return new CantUseItemBonus(condition);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof CantUseItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemPredicate(tag, aBonus.itemStackPredicate);
            return tag;
        }

        @Override
        public CantUseItemBonus deserialize(FriendlyByteBuf buf) {
            return new CantUseItemBonus(NetworkHelper.readItemPredicate(buf));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof CantUseItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            NetworkHelper.writeItemPredicate(buf, aBonus.itemStackPredicate);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new CantUseItemBonus(new EquipmentPredicate(EquipmentPredicate.Type.BOW));
        }
    }
}
