package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTLivingMultipliers;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public record NoneMultiplier() implements LivingMultiplier {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    return o != null && getClass() == o.getClass();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSerializer());
  }

  public static class Serializer implements LivingMultiplier.Serializer {
    @Override
    public LivingMultiplier deserialize(JsonObject json) throws JsonParseException {
      return new NoneMultiplier();
    }

    @Override
    public void serialize(JsonObject json, LivingMultiplier multiplier) {
      if (!(multiplier instanceof NoneMultiplier)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public LivingMultiplier deserialize(CompoundTag tag) {
      return new NoneMultiplier();
    }

    @Override
    public CompoundTag serialize(LivingMultiplier multiplier) {
      if (!(multiplier instanceof NoneMultiplier)) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public LivingMultiplier deserialize(FriendlyByteBuf buf) {
      return new NoneMultiplier();
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingMultiplier multiplier) {
      if (!(multiplier instanceof NoneMultiplier)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public LivingMultiplier createDefaultInstance() {
      return new NoneMultiplier();
    }
  }
}
