package daripher.skilltree.skill.bonus.predicate.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.predicate.PSTItemPredicates;
import daripher.skilltree.network.NetworkHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Objects;
import java.util.function.Consumer;

public final class EnchantedStackPredicate implements ItemStackPredicate {
    private ItemStackPredicate itemStackPredicate;

    public EnchantedStackPredicate(ItemStackPredicate itemStackPredicate) {
        this.itemStackPredicate = itemStackPredicate;
    }

    @Override
    public boolean test(ItemStack stack) {
        return !EnchantmentHelper.getEnchantments(stack).isEmpty() && itemStackPredicate.test(stack);
    }

    @Override
    public Component getTooltip() {
        return Component.translatable(getDescriptionId(), itemStackPredicate.getTooltip("type"));
    }

    @Override
    public Component getTooltip(String type) {
        return Component.translatable(getDescriptionId(), itemStackPredicate.getTooltip(type + ".type"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EnchantedStackPredicate that = (EnchantedStackPredicate) o;
        return itemStackPredicate.equals(that.itemStackPredicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStackPredicate);
    }

    @Override
    public ItemStackPredicate.Serializer getSerializer() {
        return PSTItemPredicates.ENCHANTED.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemStackPredicate> consumer) {
        editor.addLabel(0, 0, "Inner Item Condition", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, itemStackPredicate).setResponder(condition -> selectItemCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addItemConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<ItemStackPredicate> consumer) {
        itemStackPredicate.addEditorWidgets(editor, condition -> {
            setItemCondition(condition);
            consumer.accept(this);
        });
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<ItemStackPredicate> consumer, ItemStackPredicate condition) {
        setItemCondition(condition);
        consumer.accept(this);
        editor.rebuildWidgets();
    }

    public void setItemCondition(ItemStackPredicate itemStackPredicate) {
        this.itemStackPredicate = itemStackPredicate;
    }

    public static class Serializer implements ItemStackPredicate.Serializer {
        @Override
        public ItemStackPredicate deserialize(JsonObject json) throws JsonParseException {
            return new EnchantedStackPredicate(SerializationHelper.deserializeItemPredicate(json));
        }

        @Override
        public void serialize(JsonObject json, ItemStackPredicate condition) {
            if (!(condition instanceof EnchantedStackPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            SerializationHelper.serializeItemPredicate(json, aCondition.itemStackPredicate);
        }

        @Override
        public ItemStackPredicate deserialize(CompoundTag tag) {
            return new EnchantedStackPredicate(SerializationHelper.deserializeItemPredicate(tag));
        }

        @Override
        public CompoundTag serialize(ItemStackPredicate condition) {
            if (!(condition instanceof EnchantedStackPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemPredicate(tag, aCondition.itemStackPredicate);
            return tag;
        }

        @Override
        public ItemStackPredicate deserialize(FriendlyByteBuf buf) {
            return new EnchantedStackPredicate(NetworkHelper.readItemPredicate(buf));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemStackPredicate condition) {
            if (!(condition instanceof EnchantedStackPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            NetworkHelper.writeItemPredicate(buf, aCondition.itemStackPredicate);
        }

        @Override
        public ItemStackPredicate createDefaultInstance() {
            return new EnchantedStackPredicate(new ItemTagPredicate(ItemTags.SWORDS.location()));
        }
    }
}
