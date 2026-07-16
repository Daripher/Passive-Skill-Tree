package daripher.skilltree.skill.bonus.function;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.living.FloatFunctionEntityPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public interface FloatFunction<T> {
    float apply(LivingEntity entity);

    default String getDescriptionId() {
        ResourceLocation id = PSTRegistries.FLOAT_FUNCTIONS.get().getKey(getSerializer());
        assert id != null;
        return "value_provider.%s.%s".formatted(id.getNamespace(), id.getPath());
    }

    default String formatNumber(float number) {
        return TooltipHelper.formatNumber(number);
    }

    MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip);

    MutableComponent getPredicateTooltip(SkillBonus.Target target, FloatFunctionEntityPredicate.Logic logic, Component bonusTooltip, float requiredValue);

    MutableComponent getRequirementTooltip(FloatFunctionEntityPredicate.Logic logic, float requiredValue);

    Serializer getSerializer();

    void addEditorWidgets(SkillTreeEditor editor, Consumer<FloatFunction<?>> consumer);

    interface Serializer extends daripher.skilltree.data.serializers.Serializer<FloatFunction<?>> {
        FloatFunction<?> createDefaultInstance();
    }
}
