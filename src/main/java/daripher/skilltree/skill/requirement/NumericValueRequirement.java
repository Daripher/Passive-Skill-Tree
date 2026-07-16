package daripher.skilltree.skill.requirement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.predicate.PSTLivingEntityPredicates;
import daripher.skilltree.init.PSTSkillRequirements;
import daripher.skilltree.skill.bonus.function.EffectAmountFunction;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectType;
import daripher.skilltree.skill.bonus.predicate.living.FloatFunctionEntityPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public final class NumericValueRequirement implements SkillRequirement<NumericValueRequirement> {
    private FloatFunctionEntityPredicate condition;

    public NumericValueRequirement(FloatFunctionEntityPredicate condition) {
        this.condition = condition;
    }

    @Override
    public boolean test(Player player) {
        return condition.test(player);
    }

    @Override
    public MutableComponent getTooltip() {
        return condition.getValueProvider().getRequirementTooltip(condition.getLogic(), condition.getRequiredValue());
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<NumericValueRequirement> consumer) {
        condition.addEditorWidgets(editor, condition -> setCondition((FloatFunctionEntityPredicate) condition, consumer));
    }

    public void setCondition(FloatFunctionEntityPredicate condition, Consumer<NumericValueRequirement> consumer) {
        this.condition = condition;
        consumer.accept(this);
    }

    @Override
    public NumericValueRequirement copy() {
        return new NumericValueRequirement(condition);
    }

    @Override
    public SkillRequirement.Serializer getSerializer() {
        return PSTSkillRequirements.NUMERIC_VALUE.get();
    }

    public static class Serializer implements SkillRequirement.Serializer {
        @Override
        public SkillRequirement<?> deserialize(JsonObject json) throws JsonParseException {
            FloatFunctionEntityPredicate condition = (FloatFunctionEntityPredicate) PSTLivingEntityPredicates.NUMERIC_VALUE.get()
                    .deserialize(json);
            return new NumericValueRequirement(condition);
        }

        @Override
        public void serialize(JsonObject json, SkillRequirement<?> requirement) {
            if (requirement instanceof NumericValueRequirement aRequirement) {
                aRequirement.condition.getSerializer().serialize(json, aRequirement.condition);
            }
        }

        @Override
        public SkillRequirement<?> deserialize(CompoundTag tag) {
            FloatFunctionEntityPredicate condition = (FloatFunctionEntityPredicate) PSTLivingEntityPredicates.NUMERIC_VALUE.get()
                    .deserialize(tag);
            return new NumericValueRequirement(condition);
        }

        @Override
        public CompoundTag serialize(SkillRequirement<?> requirement) {
            CompoundTag tag = new CompoundTag();
            if (requirement instanceof NumericValueRequirement aRequirement) {
                return aRequirement.condition.getSerializer().serialize(aRequirement.condition);
            }
            return tag;
        }

        @Override
        public SkillRequirement<?> deserialize(FriendlyByteBuf buf) {
            FloatFunctionEntityPredicate condition = (FloatFunctionEntityPredicate) PSTLivingEntityPredicates.NUMERIC_VALUE.get()
                    .deserialize(buf);
            return new NumericValueRequirement(condition);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillRequirement<?> requirement) {
            if (requirement instanceof NumericValueRequirement aRequirement) {
                aRequirement.condition.getSerializer().serialize(buf, aRequirement.condition);
            }
        }

        @Override
        public SkillRequirement<?> createDefaultInstance() {
            return new NumericValueRequirement(new FloatFunctionEntityPredicate(new EffectAmountFunction(MobEffectType.BENEFICIAL), 5, FloatFunctionEntityPredicate.Logic.MORE));
        }
    }
}
