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
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public final class PotionStackPredicate implements ItemStackPredicate {
    private Type type;

    public PotionStackPredicate(Type type) {
        this.type = type;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (!(stack.getItem() instanceof PotionItem)) {
            return false;
        }
        return switch (type) {
            case ANY -> true;
            case NEUTRAL -> hasEffects(stack, MobEffectCategory.NEUTRAL);
            case HARMFUL -> hasEffects(stack, MobEffectCategory.HARMFUL);
            case BENEFICIAL -> hasEffects(stack, MobEffectCategory.BENEFICIAL);
        };
    }

    public static boolean hasEffects(ItemStack stack, MobEffectCategory category) {
        return PotionUtils.getAllEffects(stack.getOrCreateTag()).stream().map(MobEffectInstance::getEffect)
                .anyMatch(effect -> effect.getCategory() == category);
    }

    @Override
    public String getDescriptionId() {
        return "%s.%s".formatted(ItemStackPredicate.super.getDescriptionId(), type.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PotionStackPredicate that = (PotionStackPredicate) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    public enum Type {
        HARMFUL("harmful"), NEUTRAL("neutral"), BENEFICIAL("beneficial"), ANY("any");

        final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Component getFormattedName() {
            return Component.literal(getName().substring(0, 1).toUpperCase(Locale.ROOT) + getName().substring(1));
        }

        public static Type byName(String name) {
            for (Type type : values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
            return ANY;
        }
    }

    @Override
    public ItemStackPredicate.Serializer getSerializer() {
        return PSTItemPredicates.POTIONS.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemStackPredicate> consumer) {
        editor.addLabel(0, 0, "Type", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelection(0, 0, 190, 1, type).setNameGetter(PotionStackPredicate.Type::getFormattedName)
                .setResponder(type -> selectPotionType(consumer, type));
        editor.increaseHeight(29);
    }

    private void selectPotionType(Consumer<ItemStackPredicate> consumer, Type type) {
        setType(type);
        consumer.accept(this);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static class Serializer implements ItemStackPredicate.Serializer {
        @Override
        public ItemStackPredicate deserialize(JsonObject json) throws JsonParseException {
            return new PotionStackPredicate(SerializationHelper.deserializePotionType(json));
        }

        @Override
        public void serialize(JsonObject json, ItemStackPredicate condition) {
            if (!(condition instanceof PotionStackPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            SerializationHelper.serializePotionType(json, aCondition.type);
        }

        @Override
        public ItemStackPredicate deserialize(CompoundTag tag) {
            return new PotionStackPredicate(SerializationHelper.deserializePotionType(tag));
        }

        @Override
        public CompoundTag serialize(ItemStackPredicate condition) {
            if (!(condition instanceof PotionStackPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializePotionType(tag, aCondition.type);
            return tag;
        }

        @Override
        public ItemStackPredicate deserialize(FriendlyByteBuf buf) {
            return new PotionStackPredicate(NetworkHelper.readEnum(buf, Type.class));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemStackPredicate condition) {
            if (!(condition instanceof PotionStackPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            NetworkHelper.writeEnum(buf, aCondition.type);
        }

        @Override
        public ItemStackPredicate createDefaultInstance() {
            return new PotionStackPredicate(Type.ANY);
        }
    }
}
