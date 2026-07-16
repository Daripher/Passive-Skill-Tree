package daripher.skilltree.skill.bonus.function;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTFloatFunctions;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.living.FloatFunctionEntityPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.function.Consumer;

public class HealthLevelFunction implements FloatFunction<HealthLevelFunction> {
    private boolean percentage;
    private boolean missing;

    public HealthLevelFunction(boolean percentage, boolean missing) {
        this.percentage = percentage;
        this.missing = missing;
    }

    @Override
    public float apply(LivingEntity entity) {
        float value = entity.getHealth();
        if (missing) {
            value = entity.getMaxHealth() - value;
        }
        if (percentage) {
            value = value / entity.getMaxHealth();
        }
        return value;
    }

    @Override
    public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
        String key = "%s.multiplier.%s".formatted(getDescriptionId(), target.getName());
        String pointsKey = getDescriptionId();
        pointsKey += percentage ? ".percentage" : ".point";
        if (divisor != 1) {
            key += ".plural";
            pointsKey += ".plural";
        }
        Component pointsDescription = Component.translatable(pointsKey);
        if (missing) {
            key += ".missing";
        }
        if (divisor != 1) {
            return Component.translatable(key, bonusTooltip, formatNumber(divisor), pointsDescription);
        } else {
            return Component.translatable(key, bonusTooltip, pointsDescription);
        }
    }

    @Override
    public MutableComponent getPredicateTooltip(SkillBonus.Target target, FloatFunctionEntityPredicate.Logic logic, Component bonusTooltip, float requiredValue) {
        String key = "%s.condition.%s".formatted(getDescriptionId(), target.getName());
        String pointsKey = getDescriptionId();
        pointsKey += percentage ? ".percentage" : ".point";
        if (requiredValue != 1) {
            pointsKey += ".plural";
        }
        Component pointsDescription = Component.translatable(pointsKey);
        if (logic == FloatFunctionEntityPredicate.Logic.EQUAL && percentage && requiredValue == 1) {
            return Component.translatable(key + ".full", bonusTooltip);
        }
        if (logic == FloatFunctionEntityPredicate.Logic.LESS && percentage && requiredValue == 1) {
            return Component.translatable(key + ".not_full", bonusTooltip);
        }
        if (missing) {
            key += ".missing";
        }
        String valueDescription = formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("health_level", valueDescription);
        return Component.translatable(key, bonusTooltip, logicDescription, pointsDescription);
    }

    @Override
    public MutableComponent getRequirementTooltip(FloatFunctionEntityPredicate.Logic logic, float requiredValue) {
        String key = "%s.requirement".formatted(getDescriptionId());
        String pointsKey = getDescriptionId() + ".point";
        if (requiredValue != 1) {
            pointsKey += ".plural";
        }
        Component pointsDescription = Component.translatable(pointsKey);
        if (logic == FloatFunctionEntityPredicate.Logic.EQUAL && percentage && requiredValue == 1) {
            return Component.translatable(key + ".full");
        }
        if (logic == FloatFunctionEntityPredicate.Logic.LESS && percentage && requiredValue == 1) {
            return Component.translatable(key + ".not_full");
        }
        if (missing) {
            key += ".missing";
        }
        String valueDescription = formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("health_level", valueDescription);
        return Component.translatable(key, logicDescription, pointsDescription);
    }

    @Override
    public String formatNumber(float number) {
        if (percentage) {
            return FloatFunction.super.formatNumber(number * 100) + "%";
        }
        return FloatFunction.super.formatNumber(number);
    }

    @Override
    public FloatFunction.Serializer getSerializer() {
        return PSTFloatFunctions.HEALTH_LEVEL.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<FloatFunction<?>> consumer) {
        editor.addLabel(0, 0, "Missing", ChatFormatting.GREEN);
        editor.addLabel(55, 0, "Percentage", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addCheckBox(0, 0, missing).setResponder(v -> selectMissingMode(consumer, v));
        editor.addCheckBox(55, 0, percentage).setResponder(v -> selectPercentageMode(consumer, v));
        editor.increaseHeight(19);
    }

    private void selectMissingMode(Consumer<FloatFunction<?>> consumer, boolean missing) {
        setMissing(missing);
        consumer.accept(this);
    }

    private void selectPercentageMode(Consumer<FloatFunction<?>> consumer, boolean percentage) {
        setPercentage(percentage);
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
        HealthLevelFunction that = (HealthLevelFunction) o;
        return percentage == that.percentage && missing == that.missing;
    }

    @Override
    public int hashCode() {
        return Objects.hash(percentage, missing);
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    public void setPercentage(boolean percentage) {
        this.percentage = percentage;
    }

    public static class Serializer implements FloatFunction.Serializer {
        @Override
        public FloatFunction<?> deserialize(JsonObject json) throws JsonParseException {
            boolean percentage = json.get("percentage").getAsBoolean();
            boolean missing = json.get("missing").getAsBoolean();
            return new HealthLevelFunction(percentage, missing);
        }

        @Override
        public void serialize(JsonObject json, FloatFunction<?> provider) {
            if (!(provider instanceof HealthLevelFunction aProvider)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("percentage", aProvider.percentage);
            json.addProperty("missing", aProvider.missing);
        }

        @Override
        public FloatFunction<?> deserialize(CompoundTag tag) {
            boolean percentage = tag.getBoolean("percentage");
            boolean missing = tag.getBoolean("missing");
            return new HealthLevelFunction(percentage, missing);
        }

        @Override
        public CompoundTag serialize(FloatFunction<?> provider) {
            if (!(provider instanceof HealthLevelFunction aProvider)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("percentage", aProvider.percentage);
            tag.putBoolean("missing", aProvider.missing);
            return tag;
        }

        @Override
        public FloatFunction<?> deserialize(FriendlyByteBuf buf) {
            boolean percentage = buf.readBoolean();
            boolean missing = buf.readBoolean();
            return new HealthLevelFunction(percentage, missing);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, FloatFunction<?> provider) {
            if (!(provider instanceof HealthLevelFunction aProvider)) {
                throw new IllegalArgumentException();
            }
            buf.writeBoolean(aProvider.percentage);
            buf.writeBoolean(aProvider.missing);
        }

        @Override
        public FloatFunction<?> createDefaultInstance() {
            return new HealthLevelFunction(false, false);
        }
    }
}
