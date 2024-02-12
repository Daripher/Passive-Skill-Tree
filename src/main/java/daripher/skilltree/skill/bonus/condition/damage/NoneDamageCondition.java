package daripher.skilltree.skill.bonus.condition.damage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTDamageConditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.damagesource.DamageSource;

public enum NoneDamageCondition implements DamageCondition {
  INSTANCE;

  @Override
  public boolean met(DamageSource source) {
    return true;
  }

  @Override
  public DamageCondition.Serializer getSerializer() {
    return PSTDamageConditions.NONE.get();
  }

  public static class Serializer implements DamageCondition.Serializer {
    @Override
    public DamageCondition deserialize(JsonObject json) throws JsonParseException {
      return NoneDamageCondition.INSTANCE;
    }

    @Override
    public void serialize(JsonObject json, DamageCondition condition) {
      if (condition != NoneDamageCondition.INSTANCE) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public DamageCondition deserialize(CompoundTag tag) {
      return NoneDamageCondition.INSTANCE;
    }

    @Override
    public CompoundTag serialize(DamageCondition condition) {
      if (condition != NoneDamageCondition.INSTANCE) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public DamageCondition deserialize(FriendlyByteBuf buf) {
      return NoneDamageCondition.INSTANCE;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, DamageCondition condition) {
      if (condition != NoneDamageCondition.INSTANCE) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public DamageCondition createDefaultInstance() {
      return NoneDamageCondition.INSTANCE;
    }
  }
}
