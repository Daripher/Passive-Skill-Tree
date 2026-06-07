package daripher.skilltree.skill.bonus.predicate.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTLivingEntityPredicates;
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

public final class HasItemEquippedEntityPredicate implements LivingEntityPredicate {
    private @Nonnull ItemStackPredicate itemStackPredicate;

    public HasItemEquippedEntityPredicate(@Nonnull ItemStackPredicate itemStackPredicate) {
        this.itemStackPredicate = itemStackPredicate;
    }

    @Override
    public boolean test(LivingEntity living) {
        return PlayerHelper.getAllEquipment(living).anyMatch(itemStackPredicate);
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
        return PSTLivingEntityPredicates.HAS_ITEM_EQUIPPED.get();
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
        HasItemEquippedEntityPredicate that = (HasItemEquippedEntityPredicate) o;
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
            return new HasItemEquippedEntityPredicate(SerializationHelper.deserializeItemPredicate(json));
        }

        @Override
        public void serialize(JsonObject json, LivingEntityPredicate predicate) {
            HasItemEquippedEntityPredicate validPredicate = validatePredicate(predicate);
            SerializationHelper.serializeItemPredicate(json, validPredicate.itemStackPredicate);
        }

        @Override
        public LivingEntityPredicate deserialize(CompoundTag tag) {
            return new HasItemEquippedEntityPredicate(SerializationHelper.deserializeItemPredicate(tag));
        }

        @Override
        public CompoundTag serialize(LivingEntityPredicate predicate) {
            HasItemEquippedEntityPredicate validPredicate = validatePredicate(predicate);
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemPredicate(tag, validPredicate.itemStackPredicate);
            return tag;
        }

        @Override
        public LivingEntityPredicate deserialize(FriendlyByteBuf buf) {
            return new HasItemEquippedEntityPredicate(NetworkHelper.readItemPredicate(buf));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingEntityPredicate predicate) {
            HasItemEquippedEntityPredicate validPredicate = validatePredicate(predicate);
            NetworkHelper.writeItemPredicate(buf, validPredicate.itemStackPredicate);
        }

        private static @NotNull HasItemEquippedEntityPredicate validatePredicate(LivingEntityPredicate predicate) {
            if (!(predicate instanceof HasItemEquippedEntityPredicate validPredicate)) {
                throw new IllegalArgumentException("Expected HasItemEquippedEntityPredicate, got: " + predicate);
            }
            return validPredicate;
        }

        @Override
        public LivingEntityPredicate createDefaultInstance() {
            return new HasItemEquippedEntityPredicate(NoneItemStackPredicate.INSTANCE);
        }
    }
}
