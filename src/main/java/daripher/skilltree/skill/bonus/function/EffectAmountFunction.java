package daripher.skilltree.skill.bonus.function;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTFloatFunctions;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectType;
import daripher.skilltree.skill.bonus.predicate.living.FloatFunctionEntityPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Consumer;

public class EffectAmountFunction implements FloatFunction<EffectAmountFunction> {
    private MobEffectType effectType;

    public EffectAmountFunction(MobEffectType effectType) {
        this.effectType = effectType;
    }

    @Override
    public float apply(LivingEntity entity) {
        List<MobEffect> effects = entity.getActiveEffects().stream().map(MobEffectInstance::getEffect).toList();
        return switch (effectType) {
            case ANY -> effects.size();
            case NEUTRAL -> effects.stream().filter(e -> e.getCategory() == MobEffectCategory.NEUTRAL).count();
            case HARMFUL -> effects.stream().filter(e -> e.getCategory() == MobEffectCategory.HARMFUL).count();
            case BENEFICIAL -> effects.stream().filter(e -> e.getCategory() == MobEffectCategory.BENEFICIAL).count();
        };
    }

    @Override
    public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
        String key = "%s.multiplier.%s".formatted(getDescriptionId(), target.getName());
        String effectTypeKey = effectType.getDescriptionId();
        if (divisor != 1) {
            effectTypeKey += ".plural";
            key += ".plural";
            Component effectDescription = Component.translatable(effectTypeKey);
            return Component.translatable(key, bonusTooltip, formatNumber(divisor), effectDescription);
        } else {
            Component effectDescription = Component.translatable(effectTypeKey);
            return Component.translatable(key, bonusTooltip, effectDescription);
        }
    }

    @Override
    public MutableComponent getPredicateTooltip(SkillBonus.Target target, FloatFunctionEntityPredicate.Logic logic, Component bonusTooltip, float requiredValue) {
        String key = "%s.condition.%s".formatted(getDescriptionId(), target.getName());
        String effectTypeKey = effectType.getDescriptionId();
        if (!(requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.MORE)) {
            if (requiredValue != 1) {
                effectTypeKey += ".plural";
            }
        }
        Component effectDescription = Component.translatable(effectTypeKey);
        if (requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.EQUAL) {
            return Component.translatable(key + ".none", bonusTooltip, effectDescription);
        }
        if (requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.MORE) {
            return Component.translatable(key + ".any", bonusTooltip, effectDescription);
        }
        String valueDescription = formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("effect_amount", valueDescription);
        return Component.translatable(key, bonusTooltip, logicDescription, effectDescription);
    }

    @Override
    public MutableComponent getRequirementTooltip(FloatFunctionEntityPredicate.Logic logic, float requiredValue) {
        String key = "%s.requirement".formatted(getDescriptionId());
        String effectTypeKey = effectType.getDescriptionId();
        if (!(requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.MORE)) {
            if (requiredValue != 1) {
                effectTypeKey += ".plural";
            }
        }
        Component effectDescription = Component.translatable(effectTypeKey);
        if (requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.EQUAL) {
            return Component.translatable(key + ".none", effectDescription);
        }
        if (requiredValue == 0 && logic == FloatFunctionEntityPredicate.Logic.MORE) {
            return Component.translatable(key + ".any", effectDescription);
        }
        String valueDescription = formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("effect_amount", valueDescription);
        return Component.translatable(key, logicDescription, effectDescription);
    }

    @Override
    public FloatFunction.Serializer getSerializer() {
        return PSTFloatFunctions.EFFECT_AMOUNT.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<FloatFunction<?>> consumer) {
        editor.addLabel(0, 0, "Effect Type", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, effectType).setElementNameGetter(effectType -> Component.literal(effectType.name()))
                .setResponder(effectType -> selectEffectType(consumer, effectType));
        editor.increaseHeight(19);
    }

    private void selectEffectType(Consumer<FloatFunction<?>> consumer, MobEffectType type) {
        setEffectType(type);
        consumer.accept(this);
    }

    public void setEffectType(MobEffectType type) {
        this.effectType = type;
    }

    public static class Serializer implements FloatFunction.Serializer {
        @Override
        public FloatFunction<?> deserialize(JsonObject json) throws JsonParseException {
            MobEffectType type = MobEffectType.fromName(json.get("effect_type").getAsString());
            return new EffectAmountFunction(type);
        }

        @Override
        public void serialize(JsonObject json, FloatFunction<?> provider) {
            if (!(provider instanceof EffectAmountFunction aProvider)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("effect_type", aProvider.effectType.getName());
        }

        @Override
        public FloatFunction<?> deserialize(CompoundTag tag) {
            MobEffectType type = MobEffectType.fromName(tag.getString("effect_type"));
            return new EffectAmountFunction(type);
        }

        @Override
        public CompoundTag serialize(FloatFunction<?> provider) {
            if (!(provider instanceof EffectAmountFunction aProvider)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putString("effect_type", aProvider.effectType.getName());
            return tag;
        }

        @Override
        public FloatFunction<?> deserialize(FriendlyByteBuf buf) {
            MobEffectType type = MobEffectType.values()[buf.readInt()];
            return new EffectAmountFunction(type);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, FloatFunction<?> provider) {
            if (!(provider instanceof EffectAmountFunction aProvider)) {
                throw new IllegalArgumentException();
            }
            buf.writeInt(aProvider.effectType.ordinal());
        }

        @Override
        public FloatFunction<?> createDefaultInstance() {
            return new EffectAmountFunction(MobEffectType.ANY);
        }
    }
}
