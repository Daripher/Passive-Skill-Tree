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
import net.minecraft.world.level.material.Fluids;

public record UnderwaterCondition() implements LivingCondition {
  @Override
  public boolean met(LivingEntity living) {
    return living.getEyeInFluidType() == Fluids.WATER.getFluidType();
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, String target) {
    String key = getDescriptionId();
    MutableComponent targetDescription =
        Component.translatable("%s.target.%s".formatted(key, target));
    return Component.translatable(key, bonusTooltip, targetDescription);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.UNDERWATER.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    return o != null && getClass() == o.getClass();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSerializer());
  }

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      return new UnderwaterCondition();
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof UnderwaterCondition)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      return new UnderwaterCondition();
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof UnderwaterCondition)) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new UnderwaterCondition();
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof UnderwaterCondition)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public LivingCondition createDefaultInstance() {
      return new UnderwaterCondition();
    }
  }
}
