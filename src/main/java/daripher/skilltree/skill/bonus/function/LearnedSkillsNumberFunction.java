package daripher.skilltree.skill.bonus.function;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTFloatFunctions;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.living.FloatFunctionEntityPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class LearnedSkillsNumberFunction implements FloatFunction<LearnedSkillsNumberFunction> {
    @Override
    public float apply(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return 0;
        }
        if (!PlayerSkillsProvider.hasSkills(player)) {
            return 0;
        }
        return PlayerSkillsProvider.get(player).getPlayerSkills().size();
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
        String pointsKey = getDescriptionId() + ".point";
        if (requiredValue != 1) {
            pointsKey += ".plural";
        }
        Component pointsDescription = Component.translatable(pointsKey);
        String valueDescription = formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("learned_skills_amount", valueDescription);
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
        String valueDescription = formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("learned_skills_amount", valueDescription);
        return Component.translatable(key, logicDescription, pointsDescription);
    }

    @Override
    public FloatFunction.Serializer getSerializer() {
        return PSTFloatFunctions.LEARNED_SKILLS_AMOUNT.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<FloatFunction<?>> consumer) {
    }

    public static class Serializer implements FloatFunction.Serializer {
        @Override
        public FloatFunction<?> deserialize(JsonObject json) throws JsonParseException {
            return new LearnedSkillsNumberFunction();
        }

        @Override
        public void serialize(JsonObject json, FloatFunction<?> provider) {
            if (!(provider instanceof LearnedSkillsNumberFunction)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public FloatFunction<?> deserialize(CompoundTag tag) {
            return new LearnedSkillsNumberFunction();
        }

        @Override
        public CompoundTag serialize(FloatFunction<?> provider) {
            if (!(provider instanceof LearnedSkillsNumberFunction)) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public FloatFunction<?> deserialize(FriendlyByteBuf buf) {
            return new LearnedSkillsNumberFunction();
        }

        @Override
        public void serialize(FriendlyByteBuf buf, FloatFunction<?> provider) {
            if (!(provider instanceof LearnedSkillsNumberFunction)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public FloatFunction<?> createDefaultInstance() {
            return new LearnedSkillsNumberFunction();
        }
    }
}
