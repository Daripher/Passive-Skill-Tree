package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.network.NetworkHelper;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.min;

public record AttributeValueCondition(Attribute attribute, float min, float max)
    implements LivingCondition {
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
    MutableComponent attribteDescription = Component.translatable(attribute.getDescriptionId());
    MutableComponent targetDescription =
        Component.translatable("%s.target.%s".formatted(key, target));
    String min = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.min);
    String max = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.max);
    if (this.min == -1) {
      return Component.translatable(
          key + ".max", bonusTooltip, targetDescription, max, attribteDescription);
    }
    if (this.max == -1) {
      return Component.translatable(
          key + ".min", bonusTooltip, targetDescription, min, attribteDescription);
    }
    return Component.translatable(
        key + ".range", bonusTooltip, targetDescription, min, max, attribteDescription);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.ATTRIBUTE_VALUE.get();
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
  }
}
