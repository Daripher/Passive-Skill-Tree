package daripher.skilltree.skill.bonus.function;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTFloatFunctions;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.handler.SkillBonusHandlerUtils;
import daripher.skilltree.skill.bonus.predicate.living.FloatFunctionEntityPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class DistanceToTargetFunction implements FloatFunction<DistanceToTargetFunction> {
    @Override
    public float apply(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return 0f;
        }
        Entity target = SkillBonusHandlerUtils.getLastPlayerAttackTarget(player);
        if (target == null) {
            return 0f;
        }
        return target.distanceTo(entity);
    }

    @Override
    public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
        String key = "%s.multiplier.%s".formatted(getDescriptionId(), target.getName());
        if (divisor != 1) {
            key += ".plural";
            return Component.translatable(key, bonusTooltip, formatNumber(divisor));
        } else {
            return Component.translatable(key, bonusTooltip);
        }
    }

    @Override
    public MutableComponent getPredicateTooltip(SkillBonus.Target target, FloatFunctionEntityPredicate.Logic logic, Component bonusTooltip, float requiredValue) {
        String key = "%s.condition.%s".formatted(getDescriptionId(), target.getName());
        String valueDescription = formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("distance_to_target", valueDescription);
        return Component.translatable(key, bonusTooltip, logicDescription);
    }

    @Override
    public MutableComponent getRequirementTooltip(FloatFunctionEntityPredicate.Logic logic, float requiredValue) {
        return Component.literal("Unsupported").withStyle(ChatFormatting.RED);
    }

    @Override
    public FloatFunction.Serializer getSerializer() {
        return PSTFloatFunctions.DISTANCE_TO_TARGET.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<FloatFunction<?>> consumer) {
    }

    public static class Serializer implements FloatFunction.Serializer {
        @Override
        public FloatFunction<?> deserialize(JsonObject json) throws JsonParseException {
            return new DistanceToTargetFunction();
        }

        @Override
        public void serialize(JsonObject json, FloatFunction<?> provider) {
            if (!(provider instanceof DistanceToTargetFunction)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public FloatFunction<?> deserialize(CompoundTag tag) {
            return new DistanceToTargetFunction();
        }

        @Override
        public CompoundTag serialize(FloatFunction<?> provider) {
            if (!(provider instanceof DistanceToTargetFunction)) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public FloatFunction<?> deserialize(FriendlyByteBuf buf) {
            return new DistanceToTargetFunction();
        }

        @Override
        public void serialize(FriendlyByteBuf buf, FloatFunction<?> provider) {
            if (!(provider instanceof DistanceToTargetFunction)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public FloatFunction<?> createDefaultInstance() {
            return new DistanceToTargetFunction();
        }
    }
}
