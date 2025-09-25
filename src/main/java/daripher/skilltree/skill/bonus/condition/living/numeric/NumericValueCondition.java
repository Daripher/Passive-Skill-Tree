package daripher.skilltree.skill.bonus.condition.living.numeric;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.HealthLevelProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.function.Consumer;

public class NumericValueCondition implements LivingCondition {
  private NumericValueProvider<?> valueProvider;
  private float requiredValue;
  private Logic logic;

  public NumericValueCondition(NumericValueProvider<?> valueProvider, float requiredValue, Logic logic) {
    this.valueProvider = valueProvider;
    this.requiredValue = requiredValue;
    this.logic = logic;
  }

  @Override
  public boolean isConditionMet(LivingEntity living) {
    float value = valueProvider.getValue(living);
    return switch (logic) {
      case EQUAL -> value == requiredValue;
      case MORE -> value > requiredValue;
      case LESS -> value < requiredValue;
      case AT_LEAST -> value >= requiredValue;
      case AT_MOST -> value <= requiredValue;
    };
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
    return valueProvider.getConditionTooltip(target, logic, bonusTooltip, requiredValue);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.NUMERIC_VALUE.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingCondition> consumer) {
    editor.addLabel(0, 0, "Value Type", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, valueProvider).setResponder(provider -> selectValueProvider(editor, consumer, provider)).setMenuInitFunc(() -> addValueProviderWidgets(editor, consumer));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Logic", ChatFormatting.GREEN);
    editor.addLabel(100, 0, "Required Value", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 90, logic).setElementNameGetter(logic -> Component.literal(logic.name())).setResponder(logic -> selectLogic(consumer, logic));
    editor.addNumericTextField(100, 0, 50, 14, requiredValue).setNumericResponder(value -> selectRequiredValue(consumer, value));
    editor.increaseHeight(19);
  }

  private void addValueProviderWidgets(SkillTreeEditor editor, Consumer<LivingCondition> consumer) {
    valueProvider.addEditorWidgets(editor, provider -> selectValueProvider(editor, consumer, provider));
  }

  private void selectRequiredValue(Consumer<LivingCondition> consumer, Double value) {
    setRequiredValue(value.floatValue());
    consumer.accept(this);
  }

  private void selectValueProvider(SkillTreeEditor editor, Consumer<LivingCondition> consumer, NumericValueProvider<?> provider) {
    setValueProvider(provider);
    consumer.accept(this);
    editor.rebuildWidgets();
  }

  private void selectLogic(Consumer<LivingCondition> consumer, Logic logic) {
    setLogic(logic);
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
    NumericValueCondition that = (NumericValueCondition) o;
    if (Float.compare(requiredValue, that.requiredValue) != 0) {
      return false;
    }
    if (!Objects.equals(valueProvider, that.valueProvider)) {
      return false;
    }
    return logic == that.logic;
  }

  @Override
  public int hashCode() {
    return Objects.hash(valueProvider, requiredValue, logic);
  }

  public void setValueProvider(NumericValueProvider<?> provider) {
    this.valueProvider = provider;
  }

  public void setRequiredValue(float requiredValue) {
    this.requiredValue = requiredValue;
  }

  public void setLogic(Logic logic) {
    this.logic = logic;
  }

  public NumericValueProvider<?> getValueProvider() {
    return valueProvider;
  }

  public Logic getLogic() {
    return logic;
  }

  public float getRequiredValue() {
    return requiredValue;
  }

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      NumericValueProvider<?> valueProvider = SerializationHelper.deserializeValueProvider(json);
      float requiredValue = json.get("required_value").getAsFloat();
      Logic logic = Logic.valueOf(json.get("logic").getAsString());
      return new NumericValueCondition(valueProvider, requiredValue, logic);
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof NumericValueCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeValueProvider(json, aCondition.valueProvider);
      json.addProperty("required_value", aCondition.requiredValue);
      json.addProperty("logic", aCondition.logic.name());
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      NumericValueProvider<?> valueProvider = SerializationHelper.deserializeValueProvider(tag);
      float requiredValue = tag.getFloat("required_value");
      Logic logic = Logic.valueOf(tag.getString("logic"));
      return new NumericValueCondition(valueProvider, requiredValue, logic);
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof NumericValueCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeValueProvider(tag, aCondition.valueProvider);
      tag.putFloat("required_value", aCondition.requiredValue);
      tag.putString("logic", aCondition.logic.name());
      return tag;
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      NumericValueProvider<?> valueProvider = NetworkHelper.readValueProvider(buf);
      float requiredValue = buf.readFloat();
      Logic logic = Logic.values()[buf.readInt()];
      return new NumericValueCondition(valueProvider, requiredValue, logic);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof NumericValueCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeValueProvider(buf, aCondition.valueProvider);
      buf.writeFloat(aCondition.requiredValue);
      buf.writeInt(aCondition.logic.ordinal());
    }

    @Override
    public LivingCondition createDefaultInstance() {
      return new NumericValueCondition(new HealthLevelProvider(true, false), 1, Logic.EQUAL);
    }
  }

  public enum Logic {
    MORE, LESS, EQUAL, AT_LEAST, AT_MOST;

    public String getName() {
      return name().toLowerCase();
    }

    public Component getTooltip(String subtype, Object... args) {
      String conditionDescriptionId = PSTLivingConditions.NUMERIC_VALUE.get().createDefaultInstance().getDescriptionId();
      String key = conditionDescriptionId + "." + getName();
      return TooltipHelper.getOptionalTooltip(key, subtype, args);
    }
  }
}
