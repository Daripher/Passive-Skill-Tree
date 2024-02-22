package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.network.NetworkHelper;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public final class AttributeValueCondition implements LivingCondition {
  private Attribute attribute;
  private float min;
  private float max;

  public AttributeValueCondition(Attribute attribute, float min, float max) {
    this.attribute = attribute;
    this.min = min;
    this.max = max;
  }

  @Override
  public boolean met(LivingEntity living) {
    float value = (float) living.getAttributeValue(attribute);
    if (min == -1) return value <= max;
    if (max == -1) return value >= min;
    return value <= max && value >= min;
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, String target) {
    String key = getDescriptionId();
    MutableComponent attributeDescription = Component.translatable(attribute.getDescriptionId());
    MutableComponent targetDescription =
        Component.translatable("%s.target.%s".formatted(key, target));
    String min = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.min);
    String max = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.max);
    if (this.min == -1) {
      return Component.translatable(
          key + ".max", bonusTooltip, targetDescription, max, attributeDescription);
    }
    if (this.max == -1) {
      return Component.translatable(
          key + ".min", bonusTooltip, targetDescription, min, attributeDescription);
    }
    return Component.translatable(
        key + ".range", bonusTooltip, targetDescription, min, max, attributeDescription);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.ATTRIBUTE_VALUE.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<LivingCondition> consumer) {
    editor.addLabel(0, 0, "Attribute", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addAttributePicker(0, 0, 200, 14, 10, attribute)
        .setResponder(
            a -> {
              setAttribute(a);
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
    editor.addLabel(0, 0, "Min", ChatFormatting.GREEN);
    editor.addLabel(55, 0, "Max", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, min)
        .setNumericResponder(
            a -> {
              setMin(a.floatValue());
              consumer.accept(this);
            });
    editor
        .addNumericTextField(55, 0, 50, 14, max)
        .setNumericResponder(
            a -> {
              setMax(a.floatValue());
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AttributeValueCondition that = (AttributeValueCondition) o;
    return min == that.min && max == that.max;
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, max);
  }

  public void setAttribute(Attribute attribute) {
    this.attribute = attribute;
  }

  public void setMin(float min) {
    this.min = min;
  }

  public void setMax(float max) {
    this.max = max;
  }

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      Attribute attribute = SerializationHelper.deserializeAttribute(json);
      float min = json.has("min") ? json.get("min").getAsFloat() : -1;
      float max = json.has("max") ? json.get("max").getAsFloat() : -1;
      return new AttributeValueCondition(attribute, min, max);
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof AttributeValueCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeAttribute(json, aCondition.attribute);
      if (aCondition.min != -1) {
        json.addProperty("min", aCondition.min);
      }
      if (aCondition.max != -1) {
        json.addProperty("max", aCondition.max);
      }
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      Attribute attribute = SerializationHelper.deserializeAttribute(tag);
      float min = tag.contains("min") ? tag.getFloat("min") : -1;
      float max = tag.contains("max") ? tag.getFloat("max") : -1;
      return new AttributeValueCondition(attribute, min, max);
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof AttributeValueCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeAttribute(tag, aCondition.attribute);
      if (aCondition.min != -1) {
        tag.putFloat("min", aCondition.min);
      }
      if (aCondition.max != -1) {
        tag.putFloat("max", aCondition.max);
      }
      return tag;
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new AttributeValueCondition(
          NetworkHelper.readAttribute(buf), buf.readFloat(), buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof AttributeValueCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeAttribute(buf, aCondition.attribute);
      buf.writeFloat(aCondition.min);
      buf.writeFloat(aCondition.max);
    }

    @Override
    public LivingCondition createDefaultInstance() {
      return new AttributeValueCondition(Attributes.ARMOR, 5, -1);
    }
  }
}
