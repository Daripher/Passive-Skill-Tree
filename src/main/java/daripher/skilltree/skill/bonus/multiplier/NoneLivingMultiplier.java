package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTLivingMultipliers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public enum NoneLivingMultiplier implements LivingMultiplier {
  INSTANCE;

  @Override
  public float getValue(LivingEntity entity) {
    return 1f;
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip) {
    return bonusTooltip;
  }

  @Override
  public LivingMultiplier.Serializer getSerializer() {
    return PSTLivingMultipliers.NONE.get();
  }

  public static class Serializer implements LivingMultiplier.Serializer {
    @Override
    public LivingMultiplier deserialize(JsonObject json) throws JsonParseException {
      return INSTANCE;
    }

    @Override
    public void serialize(JsonObject json, LivingMultiplier multiplier) {
      if (multiplier != NoneLivingMultiplier.INSTANCE) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public LivingMultiplier deserialize(CompoundTag tag) {
      return INSTANCE;
    }

    @Override
    public CompoundTag serialize(LivingMultiplier multiplier) {
      if (multiplier != NoneLivingMultiplier.INSTANCE) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public LivingMultiplier deserialize(FriendlyByteBuf buf) {
      return INSTANCE;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingMultiplier multiplier) {
      if (multiplier != NoneLivingMultiplier.INSTANCE) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public LivingMultiplier createDefaultInstance() {
      return INSTANCE;
    }
  }
}
