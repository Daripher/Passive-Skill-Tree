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

public final class ItemUpgradeLimitBonusesBonus implements SkillBonus<ItemUpgradeLimitBonusesBonus> {
    private @Nonnull ItemStackPredicate itemStackPredicate;
    private int amount;

    public ItemUpgradeLimitBonusesBonus(@Nonnull ItemStackPredicate itemStackPredicate, int amount) {
        this.itemStackPredicate = itemStackPredicate;
        this.amount = amount;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.MORE_ITEM_BONUSES.get();
    }

    @Override
    public ItemUpgradeLimitBonusesBonus copy() {
        return new ItemUpgradeLimitBonusesBonus(itemStackPredicate, amount);
    }

    @Override
    public ItemUpgradeLimitBonusesBonus multiply(double multiplier) {
        return new ItemUpgradeLimitBonusesBonus(itemStackPredicate, (int) (amount * multiplier));
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof ItemUpgradeLimitBonusesBonus otherBonus)) {
            return false;
        }
        return Objects.equals(otherBonus.itemStackPredicate, this.itemStackPredicate);
    }

    @Override
    public SkillBonus<ItemUpgradeLimitBonusesBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof ItemUpgradeLimitBonusesBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        return new ItemUpgradeLimitBonusesBonus(itemStackPredicate, otherBonus.amount + this.amount);
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        Component itemDescription = itemStackPredicate.getTooltip("plural");
        MutableComponent bonusDescription;
        if (amount == 1) {
            bonusDescription = Component.translatable(getDescriptionId() + ".one", itemDescription);
        } else {
            bonusDescription = Component.translatable(getDescriptionId(), itemDescription, amount);
        }
        return bonusDescription.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return amount > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemUpgradeLimitBonusesBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, amount).setNumericResponder(value -> selectAmount(consumer, value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Item Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, itemStackPredicate).setResponder(condition -> selectItemCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<ItemUpgradeLimitBonusesBonus> consumer) {
        itemStackPredicate.addEditorWidgets(editor, condition -> {
            setItemCondition(condition);
            consumer.accept(this.copy());
        });
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<ItemUpgradeLimitBonusesBonus> consumer, ItemStackPredicate condition) {
        setItemCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectAmount(Consumer<ItemUpgradeLimitBonusesBonus> consumer, Double value) {
        setAmount(value.intValue());
        consumer.accept(this.copy());
    }

    public void setItemCondition(@Nonnull ItemStackPredicate itemStackPredicate) {
        this.itemStackPredicate = itemStackPredicate;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Nonnull
    public ItemStackPredicate getItemCondition() {
        return itemStackPredicate;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ItemUpgradeLimitBonusesBonus that = (ItemUpgradeLimitBonusesBonus) obj;
        if (!Objects.equals(this.itemStackPredicate, that.itemStackPredicate)) {
            return false;
        }
        return this.amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStackPredicate, amount);
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public ItemUpgradeLimitBonusesBonus deserialize(JsonObject json) throws JsonParseException {
            ItemStackPredicate condition = SerializationHelper.deserializeItemPredicate(json);
            int amount = SerializationHelper.getElement(json, "amount").getAsInt();
            return new ItemUpgradeLimitBonusesBonus(condition, amount);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof ItemUpgradeLimitBonusesBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            SerializationHelper.serializeItemPredicate(json, aBonus.itemStackPredicate);
            json.addProperty("amount", aBonus.amount);
        }

        @Override
        public ItemUpgradeLimitBonusesBonus deserialize(CompoundTag tag) {
            ItemStackPredicate condition = SerializationHelper.deserializeItemPredicate(tag);
            int amount = tag.getInt("amount");
            return new ItemUpgradeLimitBonusesBonus(condition, amount);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof ItemUpgradeLimitBonusesBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemPredicate(tag, aBonus.itemStackPredicate);
            tag.putInt("amount", aBonus.amount);
            return tag;
        }

        @Override
        public ItemUpgradeLimitBonusesBonus deserialize(FriendlyByteBuf buf) {
            return new ItemUpgradeLimitBonusesBonus(NetworkHelper.readItemPredicate(buf), buf.readInt());
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof ItemUpgradeLimitBonusesBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            NetworkHelper.writeItemPredicate(buf, aBonus.itemStackPredicate);
            buf.writeInt(aBonus.amount);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new ItemUpgradeLimitBonusesBonus(new EquipmentPredicate(EquipmentPredicate.Type.SHIELD), 1);
        }
    }
}
