package daripher.skilltree.skill.bonus.predicate.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.predicate.PSTLivingEntityPredicates;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.item.NoneItemStackPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class HasItemInHandEntityPredicate implements LivingEntityPredicate {
    private @Nonnull ItemStackPredicate itemStackPredicate;

    public HasItemInHandEntityPredicate(@Nonnull ItemStackPredicate itemStackPredicate) {
        this.itemStackPredicate = itemStackPredicate;
    }

    @Override
    public boolean test(LivingEntity living) {
        return itemStackPredicate.test(living.getMainHandItem()) || itemStackPredicate.test(living.getOffhandItem());
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        String key = getDescriptionId();
        Component targetDescription = Component.translatable("%s.target.%s".formatted(key, target.getName()));
        Component itemDescription = itemStackPredicate.getTooltip();
        return Component.translatable(key, bonusTooltip, targetDescription, itemDescription);
    }

    @Override
    public LivingEntityPredicate.Serializer getSerializer() {
        return PSTLivingEntityPredicates.HAS_ITEM_IN_HAND.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer) {
        editor.addLabel(0, 0, "Item Predicate", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, itemStackPredicate).setResponder(predicate -> selectItemPredicate(editor, consumer, predicate))
                .setMenuInitFunc(() -> addItemPredicateWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addItemPredicateWidgets(SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer) {
        itemStackPredicate.addEditorWidgets(editor, predicate -> {
            setItemPredicate(predicate);
            consumer.accept(this);
        });
    }

    private void selectItemPredicate(SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer, ItemStackPredicate predicate) {
        setItemPredicate(predicate);
        consumer.accept(this);
        editor.rebuildWidgets();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HasItemInHandEntityPredicate that = (HasItemInHandEntityPredicate) o;
        return Objects.equals(itemStackPredicate, that.itemStackPredicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStackPredicate);
    }

    public void setItemPredicate(@Nonnull ItemStackPredicate itemStackPredicate) {
        this.itemStackPredicate = itemStackPredicate;
    }

    public static class Serializer implements LivingEntityPredicate.Serializer {
        @Override
        public LivingEntityPredicate deserialize(JsonObject json) throws JsonParseException {
            return new HasItemInHandEntityPredicate(SerializationHelper.deserializeItemPredicate(json));
        }

        @Override
        public void serialize(JsonObject json, LivingEntityPredicate predicate) {
            HasItemInHandEntityPredicate validPredicate = validatePredicate(predicate);
            SerializationHelper.serializeItemPredicate(json, validPredicate.itemStackPredicate);
        }

        @Override
        public LivingEntityPredicate deserialize(CompoundTag tag) {
            return new HasItemInHandEntityPredicate(SerializationHelper.deserializeItemPredicate(tag));
        }

        @Override
        public CompoundTag serialize(LivingEntityPredicate predicate) {
            HasItemInHandEntityPredicate validPredicate = validatePredicate(predicate);
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemPredicate(tag, validPredicate.itemStackPredicate);
            return tag;
        }

        @Override
        public LivingEntityPredicate deserialize(FriendlyByteBuf buf) {
            return new HasItemInHandEntityPredicate(NetworkHelper.readItemPredicate(buf));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingEntityPredicate predicate) {
            HasItemInHandEntityPredicate validPredicate = validatePredicate(predicate);
            NetworkHelper.writeItemPredicate(buf, validPredicate.itemStackPredicate);
        }

        private static @NotNull HasItemInHandEntityPredicate validatePredicate(LivingEntityPredicate predicate) {
            if (!(predicate instanceof HasItemInHandEntityPredicate validPredicate)) {
                throw new IllegalArgumentException("Expected HasItemInHandEntityPredicate, got: " + predicate);
            }
            return validPredicate;
        }

        @Override
        public LivingEntityPredicate createDefaultInstance() {
            return new HasItemInHandEntityPredicate(NoneItemStackPredicate.INSTANCE);
        }
    }
}
