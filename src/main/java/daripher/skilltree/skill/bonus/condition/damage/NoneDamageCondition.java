package daripher.skilltree.skill.bonus.condition.damage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTDamageConditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.damagesource.DamageSource;

public record NoneDamageCondition() implements DamageCondition {
  @Override
  public boolean met(DamageSource source) {
    return true;
  }

  @Override
  public DamageCondition.Serializer getSerializer() {
    return PSTDamageConditions.NONE.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    return o != null && getClass() == o.getClass();
  }

  @Override
  public int hashCode() {
    return getSerializer().hashCode();
  }

  public static class Serializer implements DamageCondition.Serializer {
    @Override
    public DamageCondition deserialize(JsonObject json) throws JsonParseException {
      return new NoneDamageCondition();
    }

    @Override
    public void serialize(JsonObject json, DamageCondition condition) {
      if (!(condition instanceof NoneDamageCondition)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public DamageCondition deserialize(CompoundTag tag) {
      return new NoneDamageCondition();
    }

    @Override
    public CompoundTag serialize(DamageCondition condition) {
      if (!(condition instanceof NoneDamageCondition)) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public DamageCondition deserialize(FriendlyByteBuf buf) {
      return new NoneDamageCondition();
    }

    @Override
    public void serialize(FriendlyByteBuf buf, DamageCondition condition) {
      if (!(condition instanceof NoneDamageCondition)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public DamageCondition createDefaultInstance() {
      return new NoneDamageCondition();
    }
  }
}
