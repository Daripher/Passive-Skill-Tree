package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTLivingConditions;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public record NoneLivingCondition() implements LivingCondition {
  @Override
  public boolean met(LivingEntity living) {
    return true;
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, String target) {
    return bonusTooltip;
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.NONE.get();
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
      return new NoneLivingCondition();
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof NoneLivingCondition)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      return new NoneLivingCondition();
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof NoneLivingCondition)) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new NoneLivingCondition();
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof NoneLivingCondition)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public LivingCondition createDefaultInstance() {
      return new NoneLivingCondition();
    }
  }
}
