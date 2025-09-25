package daripher.skilltree.skill.bonus.condition.living.numeric;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface NumericValueProvider<T> {
  float getValue(LivingEntity entity);

  default String getDescriptionId() {
    ResourceLocation id = PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getKey(getSerializer());
    assert id != null;
    return "value_provider.%s.%s".formatted(id.getNamespace(), id.getPath());
  }

  default String formatNumber(float number) {
    return TooltipHelper.formatNumber(number);
  }

  MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip);

  MutableComponent getConditionTooltip(SkillBonus.Target target, NumericValueCondition.Logic logic, Component bonusTooltip, float requiredValue);

  MutableComponent getRequirementTooltip(NumericValueCondition.Logic logic, float requiredValue);

  Serializer getSerializer();

  default T createDefaultInstance() {
    return (T) getSerializer().createDefaultInstance();
  }

  void addEditorWidgets(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer);

  interface Serializer extends daripher.skilltree.data.serializers.Serializer<NumericValueProvider<?>> {
    NumericValueProvider<?> createDefaultInstance();
  }
}
