package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTLivingConditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public enum NoneLivingCondition implements LivingCondition {
  INSTANCE;

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

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      return NoneLivingCondition.INSTANCE;
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (condition != NoneLivingCondition.INSTANCE) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      return NoneLivingCondition.INSTANCE;
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (condition != NoneLivingCondition.INSTANCE) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return NoneLivingCondition.INSTANCE;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (condition != NoneLivingCondition.INSTANCE) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public LivingCondition createDefaultInstance() {
      return NoneLivingCondition.INSTANCE;
    }
  }
}
