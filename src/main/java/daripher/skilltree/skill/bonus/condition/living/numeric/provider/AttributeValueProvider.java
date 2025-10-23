package daripher.skilltree.skill.bonus.condition.living.numeric.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTNumericValueProviders;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Consumer;

public class AttributeValueProvider implements NumericValueProvider<AttributeValueProvider> {
  private Attribute attribute;

  public AttributeValueProvider(Attribute attribute) {
    this.attribute = attribute;
  }

  @Override
  public float getValue(LivingEntity entity) {
    AttributeMap attributes = entity.getAttributes();
    return attributes.hasAttribute(attribute) ? (float) attributes.getValue(attribute) : 0f;
  }

  @Override
  public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
    String key = "%s.multiplier.%s".formatted(getDescriptionId(), target.getName());
    MutableComponent attributeDescription = Component.translatable(attribute.getDescriptionId());
    if (divisor != 1) {
      key += ".plural";
      return Component.translatable(key, bonusTooltip, formatNumber(divisor), attributeDescription);
    } else {
      return Component.translatable(key, bonusTooltip, attributeDescription);
    }
  }

  @Override
  public MutableComponent getConditionTooltip(SkillBonus.Target target, NumericValueCondition.Logic logic, Component bonusTooltip,
                                              float requiredValue) {
    String key = "%s.condition.%s".formatted(getDescriptionId(), target.getName());
    Component attributeDescription = Component.translatable(attribute.getDescriptionId());
    String valueDescription = formatNumber(requiredValue);
    Component logicDescription = logic.getTooltip("attribute_value", valueDescription);
    return Component.translatable(key, bonusTooltip, attributeDescription, logicDescription);
  }

  @Override
  public MutableComponent getRequirementTooltip(NumericValueCondition.Logic logic, float requiredValue) {
    String key = "%s.requirement".formatted(getDescriptionId());
    Component attributeDescription = Component.translatable(attribute.getDescriptionId());
    String valueDescription = formatNumber(requiredValue);
    Component logicDescription = logic.getTooltip("attribute_value", valueDescription);
    return Component.translatable(key, logicDescription, attributeDescription);
  }

  @Override
  public NumericValueProvider.Serializer getSerializer() {
    return PSTNumericValueProviders.ATTRIBUTE_VALUE.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer) {
    editor.addLabel(0, 0, "Attribute", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, attribute).setResponder(attribute -> selectAttribute(consumer, attribute));
    editor.increaseHeight(19);
  }

  private void selectAttribute(Consumer<NumericValueProvider<?>> consumer, Attribute attribute) {
    setAttribute(attribute);
    consumer.accept(this);
  }

  public void setAttribute(Attribute attribute) {
    this.attribute = attribute;
  }

  public static class Serializer implements NumericValueProvider.Serializer {
    @Override
    public NumericValueProvider<?> deserialize(JsonObject json) throws JsonParseException {
      Attribute attribute = SerializationHelper.deserializeAttribute(json);
      return new AttributeValueProvider(attribute);
    }

    @Override
    public void serialize(JsonObject json, NumericValueProvider<?> provider) {
      if (!(provider instanceof AttributeValueProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeAttribute(json, aProvider.attribute);
    }

    @Override
    public NumericValueProvider<?> deserialize(CompoundTag tag) {
      Attribute attribute = SerializationHelper.deserializeAttribute(tag);
      return new AttributeValueProvider(attribute);
    }

    @Override
    public CompoundTag serialize(NumericValueProvider<?> provider) {
      if (!(provider instanceof AttributeValueProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeAttribute(tag, aProvider.attribute);
      return tag;
    }

    @Override
    public NumericValueProvider<?> deserialize(FriendlyByteBuf buf) {
      Attribute attribute = NetworkHelper.readAttribute(buf);
      return new AttributeValueProvider(attribute);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, NumericValueProvider<?> provider) {
      if (!(provider instanceof AttributeValueProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeAttribute(buf, aProvider.attribute);
    }

    @Override
    public NumericValueProvider<?> createDefaultInstance() {
      return new AttributeValueProvider(Attributes.MAX_HEALTH);
    }
  }
}
