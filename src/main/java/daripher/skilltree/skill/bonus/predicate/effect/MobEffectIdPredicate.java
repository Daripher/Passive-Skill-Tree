package daripher.skilltree.skill.bonus.predicate.effect;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.predicate.PSTMobEffectPredicates;
import daripher.skilltree.network.NetworkHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;

import java.util.Objects;
import java.util.function.Consumer;

public final class MobEffectIdPredicate implements MobEffectPredicate {
    private MobEffect mobEffect;

    public MobEffectIdPredicate(MobEffect mobEffect) {
        this.mobEffect = mobEffect;
    }

    @Override
    public boolean test(MobEffect mobEffect) {
        return mobEffect == this.mobEffect;
    }

    @Override
    public boolean testsForHarmfulEffects() {
        return mobEffect.getCategory() == MobEffectCategory.HARMFUL;
    }

    @Override
    public Component getTooltip() {
        return mobEffect.getDisplayName();
    }

    @Override
    public Component getTooltip(String type) {
        return TooltipHelper.getOptionalTooltip(mobEffect.getDescriptionId(), type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MobEffectIdPredicate that = (MobEffectIdPredicate) o;
        return mobEffect.equals(that.mobEffect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mobEffect);
    }

    @Override
    public MobEffectPredicate.Serializer getSerializer() {
        return PSTMobEffectPredicates.EFFECT_ID.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<MobEffectPredicate> consumer) {
        editor.addLabel(0, 0, "Effect Type", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, mobEffect).setResponder(mobEffect -> selectEffect(consumer, mobEffect));
        editor.increaseHeight(19);
    }

    private void selectEffect(Consumer<MobEffectPredicate> consumer, MobEffect mobEffect) {
        setEffectType(mobEffect);
        consumer.accept(this);
    }

    public void setEffectType(MobEffect mobEffect) {
        this.mobEffect = mobEffect;
    }

    public static class Serializer implements MobEffectPredicate.Serializer {
        @Override
        public MobEffectPredicate deserialize(JsonObject json) throws JsonParseException {
            MobEffect mobEffect = SerializationHelper.deserializeMobEffect(json);
            return new MobEffectIdPredicate(mobEffect);
        }

        @Override
        public void serialize(JsonObject json, MobEffectPredicate predicate) {
            MobEffectIdPredicate validPredicate = validatePredicate(predicate);
            SerializationHelper.serializeMobEffect(json, validPredicate.mobEffect);
        }

        @Override
        public MobEffectPredicate deserialize(CompoundTag tag) {
            MobEffect mobEffect = SerializationHelper.deserializeMobEffect(tag);
            return new MobEffectIdPredicate(mobEffect);
        }

        @Override
        public CompoundTag serialize(MobEffectPredicate predicate) {
            MobEffectIdPredicate validPredicate = validatePredicate(predicate);
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeMobEffect(tag, validPredicate.mobEffect);
            return tag;
        }

        @Override
        public MobEffectPredicate deserialize(FriendlyByteBuf buf) {
            MobEffect mobEffect = NetworkHelper.readMobEffect(buf);
            return new MobEffectIdPredicate(mobEffect);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, MobEffectPredicate predicate) {
            MobEffectIdPredicate validPredicate = validatePredicate(predicate);
            NetworkHelper.writeMobEffect(buf, validPredicate.mobEffect);
        }

        private MobEffectIdPredicate validatePredicate(MobEffectPredicate predicate) {
            if (!(predicate instanceof MobEffectIdPredicate validPredicate)) {
                throw new IllegalArgumentException();
            }
            return validPredicate;
        }

        @Override
        public MobEffectPredicate createDefaultInstance() {
            return new MobEffectIdPredicate(MobEffects.POISON);
        }
    }
}
