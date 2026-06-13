package daripher.skilltree.skill.bonus.predicate.effect;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.selection.SelectionList;
import daripher.skilltree.init.predicate.PSTMobEffectPredicates;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.Objects;
import java.util.function.Consumer;

public final class MobEffectTypePredicate implements MobEffectPredicate {
    private MobEffectType effectType;

    public MobEffectTypePredicate(MobEffectType effectType) {
        this.effectType = effectType;
    }

    @Override
    public boolean test(MobEffect mobEffect) {
        return switch (effectType) {
            case ANY -> true;
            case BENEFICIAL -> mobEffect.getCategory() == MobEffectCategory.BENEFICIAL;
            case HARMFUL -> mobEffect.getCategory() == MobEffectCategory.HARMFUL;
            case NEUTRAL -> mobEffect.getCategory() == MobEffectCategory.NEUTRAL;
        };
    }

    @Override
    public boolean testsForHarmfulEffects() {
        return effectType == MobEffectType.HARMFUL;
    }

    @Override
    public Component getTooltip() {
        return Component.translatable(effectType.getDescriptionId());
    }

    @Override
    public Component getTooltip(String type) {
        return Component.translatable("%s.%s".formatted(effectType.getDescriptionId(), type));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MobEffectTypePredicate that = (MobEffectTypePredicate) o;
        return effectType.equals(that.effectType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(effectType);
    }

    @Override
    public MobEffectPredicate.Serializer getSerializer() {
        return PSTMobEffectPredicates.EFFECT_CATEGORY.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<MobEffectPredicate> consumer) {
        editor.addLabel(0, 0, "Effect Category", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        SelectionList<MobEffectType> selectionList = editor.addSelection(0, 0, 190, 4, effectType)
                .setResponder(condition -> selectEffectType(consumer, condition));
        editor.increaseHeight(selectionList.getHeight() + 10);
    }

    private void selectEffectType(Consumer<MobEffectPredicate> consumer, MobEffectType effectType) {
        setEffectType(effectType);
        consumer.accept(this);
    }

    public void setEffectType(MobEffectType effectType) {
        this.effectType = effectType;
    }

    public static class Serializer implements MobEffectPredicate.Serializer {
        @Override
        public MobEffectPredicate deserialize(JsonObject json) throws JsonParseException {
            MobEffectType effectType = MobEffectType.fromName(json.get("effect_type").getAsString());
            return new MobEffectTypePredicate(effectType);
        }

        @Override
        public void serialize(JsonObject json, MobEffectPredicate predicate) {
            MobEffectTypePredicate validPredicate = validatePredicate(predicate);
            json.addProperty("effect_type", validPredicate.effectType.getName());
        }

        @Override
        public MobEffectPredicate deserialize(CompoundTag tag) {
            MobEffectType effectType = MobEffectType.fromName(tag.getString("effect_type"));
            return new MobEffectTypePredicate(effectType);
        }

        @Override
        public CompoundTag serialize(MobEffectPredicate predicate) {
            MobEffectTypePredicate validPredicate = validatePredicate(predicate);
            CompoundTag tag = new CompoundTag();
            tag.putString("effect_type", validPredicate.effectType.getName());
            return tag;
        }

        @Override
        public MobEffectPredicate deserialize(FriendlyByteBuf buf) {
            MobEffectType effectType = MobEffectType.values()[buf.readInt()];
            return new MobEffectTypePredicate(effectType);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, MobEffectPredicate predicate) {
            MobEffectTypePredicate validPredicate = validatePredicate(predicate);
            buf.writeInt(validPredicate.effectType.ordinal());
        }

        private MobEffectTypePredicate validatePredicate(MobEffectPredicate predicate) {
            if (!(predicate instanceof MobEffectTypePredicate validPredicate)) {
                throw new IllegalArgumentException();
            }
            return validPredicate;
        }

        @Override
        public MobEffectPredicate createDefaultInstance() {
            return new MobEffectTypePredicate(MobEffectType.BENEFICIAL);
        }
    }
}
