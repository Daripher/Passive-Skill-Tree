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

public record EffectAmountCondition(int min, int max) implements LivingCondition {
  @Override
  public boolean met(LivingEntity living) {
    int effects = living.getActiveEffects().size();
    if (min == -1) return effects <= max;
    if (max == -1) return effects >= min;
    return effects <= max && effects >= min;
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, String target) {
    String key = getDescriptionId();
    MutableComponent targetDescription =
        Component.translatable("%s.target.%s".formatted(key, target));
    if (min == -1) {
      return Component.translatable(key + ".max", bonusTooltip, targetDescription, max);
    }
    if (max == -1) {
      if (min == 1)
        return Component.translatable(key + ".min.1", bonusTooltip, targetDescription, min);
      return Component.translatable(key + ".min", bonusTooltip, targetDescription, min);
    }
    return Component.translatable(key + ".range", bonusTooltip, targetDescription, min, max);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.EFFECT_AMOUNT.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EffectAmountCondition that = (EffectAmountCondition) o;
    return min == that.min && max == that.max;
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, max);
  }

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      int min = json.has("min") ? json.get("min").getAsInt() : -1;
      int max = json.has("max") ? json.get("max").getAsInt() : -1;
      return new EffectAmountCondition(min, max);
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof EffectAmountCondition aCondition)) {
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
      int min = tag.contains("Min") ? tag.getInt("Min") : -1;
      int max = tag.contains("Max") ? tag.getInt("Max") : -1;
      return new EffectAmountCondition(min, max);
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof EffectAmountCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      if (aCondition.min != -1) {
        tag.putInt("Min", aCondition.min);
      }
      if (aCondition.max != -1) {
        tag.putInt("Max", aCondition.max);
      }
      return tag;
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new EffectAmountCondition(buf.readInt(), buf.readInt());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof EffectAmountCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      buf.writeInt(aCondition.min);
      buf.writeInt(aCondition.max);
    }
  }
}
