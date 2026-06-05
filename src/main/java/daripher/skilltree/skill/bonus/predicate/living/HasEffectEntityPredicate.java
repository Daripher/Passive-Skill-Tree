package daripher.skilltree.skill.bonus.predicate.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class HasEffectEntityPredicate implements LivingEntityPredicate {
    private MobEffect effect;
    private int amplifier;

    public HasEffectEntityPredicate(@Nonnull MobEffect effect) {
        this(effect, 0);
    }

    public HasEffectEntityPredicate(@Nonnull MobEffect effect, int amplifier) {
        this.effect = effect;
        this.amplifier = amplifier;
    }

    @Override
    public boolean test(LivingEntity living) {
        if (amplifier == 0) {
            return living.hasEffect(this.effect);
        }
        MobEffectInstance effect = living.getEffect(this.effect);
        return effect != null && effect.getAmplifier() >= this.amplifier;
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        String key = getDescriptionId();
        Component targetDescription = Component.translatable("%s.target.%s".formatted(key, target.getName()));
        Component effectDescription = effect.getDisplayName();
        if (amplifier == 0) {
            return Component.translatable(key, bonusTooltip, targetDescription, effectDescription);
        }
        Component amplifierDescription = Component.translatable("potion.potency." + amplifier);
        effectDescription = Component.translatable("potion.withAmplifier", effectDescription, amplifierDescription);
        return Component.translatable(key + ".amplifier", bonusTooltip, targetDescription, effectDescription);
    }

    @Override
    public LivingEntityPredicate.Serializer getSerializer() {
        return PSTLivingConditions.HAS_EFFECT.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer) {
        editor.addLabel(0, 0, "Effect", ChatFormatting.GREEN);
        editor.addLabel(150, 0, "Level", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 145, effect).setResponder(effect -> selectEffect(consumer, effect));
        editor.addNumericTextField(150, 0, 50, 14, amplifier).setNumericFilter(value -> value >= 0 && value == value.intValue())
                .setNumericResponder(value -> selectAmplifier(consumer, value));
        editor.increaseHeight(19);
    }

    private void selectAmplifier(Consumer<LivingEntityPredicate> consumer, Double value) {
        setAmplifier(value.intValue());
        consumer.accept(this);
    }

    private void selectEffect(Consumer<LivingEntityPredicate> consumer, MobEffect effect) {
        setEffect(effect);
        consumer.accept(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HasEffectEntityPredicate that = (HasEffectEntityPredicate) o;
        return amplifier == that.amplifier && Objects.equals(effect, that.effect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(effect, amplifier);
    }

    public void setEffect(MobEffect effect) {
        this.effect = effect;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    public static class Serializer implements LivingEntityPredicate.Serializer {
        @Override
        public LivingEntityPredicate deserialize(JsonObject json) throws JsonParseException {
            MobEffect effect = SerializationHelper.deserializeEffect(json);
            int amplifier = !json.has("amplifier") ? 0 : json.get("amplifier").getAsInt();
            Objects.requireNonNull(effect);
            return new HasEffectEntityPredicate(effect, amplifier);
        }

        @Override
        public void serialize(JsonObject json, LivingEntityPredicate condition) {
            if (!(condition instanceof HasEffectEntityPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            SerializationHelper.serializeEffect(json, aCondition.effect);
            json.addProperty("amplifier", aCondition.amplifier);
        }

        @Override
        public LivingEntityPredicate deserialize(CompoundTag tag) {
            MobEffect effect = SerializationHelper.deserializeEffect(tag);
            int amplifier = !tag.contains("amplifier") ? 0 : tag.getInt("amplifier");
            Objects.requireNonNull(effect);
            return new HasEffectEntityPredicate(effect, amplifier);
        }

        @Override
        public CompoundTag serialize(LivingEntityPredicate condition) {
            if (!(condition instanceof HasEffectEntityPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeEffect(tag, aCondition.effect);
            tag.putInt("amplifier", aCondition.amplifier);
            return tag;
        }

        @Override
        public LivingEntityPredicate deserialize(FriendlyByteBuf buf) {
            MobEffect effect = NetworkHelper.readEffect(buf);
            Objects.requireNonNull(effect);
            return new HasEffectEntityPredicate(effect, buf.readInt());
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingEntityPredicate condition) {
            if (!(condition instanceof HasEffectEntityPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            NetworkHelper.writeEffect(buf, aCondition.effect);
            buf.writeInt(aCondition.amplifier);
        }

        @Override
        public LivingEntityPredicate createDefaultInstance() {
            return new HasEffectEntityPredicate(MobEffects.POISON);
        }
    }
}
