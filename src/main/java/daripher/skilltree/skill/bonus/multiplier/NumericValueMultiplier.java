package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTLivingMultipliers;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.AttributeValueProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Objects;
import java.util.function.Consumer;

public final class NumericValueMultiplier implements LivingMultiplier {
  private NumericValueProvider<?> valueProvider;
  private float divisor;

  public NumericValueMultiplier(NumericValueProvider<?> valueProvider, float divisor) {
    this.valueProvider = valueProvider;
    this.divisor = divisor;
  }

  @Override
  public float getValue(LivingEntity entity) {
    return (int) (valueProvider.getValue(entity) / divisor);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
    return valueProvider.getMultiplierTooltip(target, divisor, bonusTooltip);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingMultiplier> consumer) {
    editor.addLabel(0, 0, "Value Type", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, valueProvider).setResponder(provider -> {
      selectValueProvider(consumer, provider);
      addValueProviderWidgets(editor, consumer);
      editor.rebuildWidgets();
    }).setMenuInitFunc(() -> addValueProviderWidgets(editor, consumer));
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Divisor", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor.addNumericTextField(0, 0, 50, 14, divisor).setNumericFilter(value -> value > 0).setNumericResponder(value -> selectDivisor(consumer,
        value));
    editor.increaseHeight(19);
  }

  private void addValueProviderWidgets(SkillTreeEditor editor, Consumer<LivingMultiplier> consumer) {
    valueProvider.addEditorWidgets(editor, provider -> selectValueProvider(consumer, provider));
  }

  private void selectDivisor(Consumer<LivingMultiplier> consumer, Double value) {
    setDivisor(value.floatValue());
    consumer.accept(this);
  }

  private void selectValueProvider(Consumer<LivingMultiplier> consumer, NumericValueProvider<?> valueProvider) {
    setValueProvider(valueProvider);
    consumer.accept(this);
  }

  public float getDivisor() {
    return divisor;
  }

  @Override
  public LivingMultiplier.Serializer getSerializer() {
    return PSTLivingMultipliers.NUMERIC_VALUE.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NumericValueMultiplier that = (NumericValueMultiplier) o;
    if (Float.compare(divisor, that.divisor) != 0) {
      return false;
    }
    return Objects.equals(valueProvider, that.valueProvider);
  }

  @Override
  public int hashCode() {
    return Objects.hash(valueProvider, divisor);
  }

  public void setValueProvider(NumericValueProvider<?> valueProvider) {
    this.valueProvider = valueProvider;
  }

  public void setDivisor(float divisor) {
    this.divisor = divisor;
  }

  public NumericValueProvider<?> getValueProvider() {
    return valueProvider;
  }

  public static class Serializer implements LivingMultiplier.Serializer {
    @Override
    public LivingMultiplier deserialize(JsonObject json) throws JsonParseException {
      NumericValueProvider<?> valueProvider = SerializationHelper.deserializeValueProvider(json);
      float divisor = !json.has("divisor") ? 1f : json.get("divisor").getAsFloat();
      return new NumericValueMultiplier(valueProvider, divisor);
    }

    @Override
    public void serialize(JsonObject json, LivingMultiplier multiplier) {
      if (!(multiplier instanceof NumericValueMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeValueProvider(json, aMultiplier.valueProvider);
      json.addProperty("divisor", aMultiplier.divisor);
    }

    @Override
    public LivingMultiplier deserialize(CompoundTag tag) {
      NumericValueProvider<?> valueProvider = SerializationHelper.deserializeValueProvider(tag);
      float divisor = !tag.contains("divisor") ? 1f : tag.getFloat("divisor");
      return new NumericValueMultiplier(valueProvider, divisor);
    }

    @Override
    public CompoundTag serialize(LivingMultiplier multiplier) {
      if (!(multiplier instanceof NumericValueMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeValueProvider(tag, aMultiplier.valueProvider);
      tag.putFloat("divisor", aMultiplier.divisor);
      return tag;
    }

    @Override
    public LivingMultiplier deserialize(FriendlyByteBuf buf) {
      NumericValueProvider<?> valueProvider = NetworkHelper.readValueProvider(buf);
      float divisor = buf.readFloat();
      return new NumericValueMultiplier(valueProvider, divisor);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingMultiplier multiplier) {
      if (!(multiplier instanceof NumericValueMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeValueProvider(buf, aMultiplier.valueProvider);
      buf.writeFloat(aMultiplier.divisor);
    }

    @Override
    public LivingMultiplier createDefaultInstance() {
      return new NumericValueMultiplier(new AttributeValueProvider(Attributes.MAX_HEALTH), 5f);
    }
  }
}
