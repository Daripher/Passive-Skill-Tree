package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTLivingConditions;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record HealthPercentageCondition(float min, float max) implements LivingCondition {
  @Override
  public boolean met(LivingEntity living) {
    float percentage = living.getHealth() / living.getMaxHealth();
    if (min == -1) {
      return percentage <= max;
    }
    if (max == -1) {
      return percentage >= min;
    }
    return percentage <= max && percentage >= min;
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, String target) {
    String key = getDescriptionId();
    MutableComponent targetDescription =
        Component.translatable("%s.target.%s".formatted(key, target));
    String min = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.min * 100);
    String max = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.max * 100);
    if (this.min == -1) {
      return Component.translatable(key + ".max", bonusTooltip, targetDescription, max);
    }
    if (this.max == -1) {
      return Component.translatable(key + ".min", bonusTooltip, targetDescription, min);
    }
    return Component.translatable(key + ".range", bonusTooltip, targetDescription, min, max);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.HEALTH_PERCENTAGE.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HealthPercentageCondition that = (HealthPercentageCondition) o;
    return Float.compare(min, that.min) == 0 && Float.compare(max, that.max) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, max);
  }

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      float min = json.has("min") ? json.get("min").getAsFloat() : -1;
      float max = json.has("max") ? json.get("max").getAsFloat() : -1;
      return new HealthPercentageCondition(min, max);
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof HealthPercentageCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      if (aCondition.min != -1) {
        json.addProperty("min", aCondition.min);
      }
      if (aCondition.max != -1) {
        json.addProperty("max", aCondition.max);
      }
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      float min = tag.contains("Min") ? tag.getFloat("Min") : -1;
      float max = tag.contains("Max") ? tag.getFloat("Max") : -1;
      return new HealthPercentageCondition(min, max);
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof HealthPercentageCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      if (aCondition.min != -1) {
        tag.putFloat("Min", aCondition.min);
      }
      if (aCondition.max != -1) {
        tag.putFloat("Max", aCondition.max);
      }
      return tag;
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new HealthPercentageCondition(buf.readFloat(), buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof HealthPercentageCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aCondition.min);
      buf.writeFloat(aCondition.max);
    }
  }
}
