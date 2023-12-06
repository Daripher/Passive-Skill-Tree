package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTLivingMultipliers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public class EffectAmountMultiplier implements LivingMultiplier {
  @Override
  public float getValue(LivingEntity entity) {
    return entity.getActiveEffects().size();
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip) {
    return Component.translatable(getDescriptionId(), bonusTooltip);
  }

  @Override
  public LivingMultiplier.Serializer getSerializer() {
    return PSTLivingMultipliers.EFFECT_AMOUNT.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    return o != null && getClass() == o.getClass();
  }

  public static class Serializer implements LivingMultiplier.Serializer {
    @Override
    public LivingMultiplier deserialize(JsonObject json) throws JsonParseException {
      return new EffectAmountMultiplier();
    }

    @Override
    public void serialize(JsonObject json, LivingMultiplier object) {}

    @Override
    public LivingMultiplier deserialize(CompoundTag tag) {
      return new EffectAmountMultiplier();
    }

    @Override
    public CompoundTag serialize(LivingMultiplier object) {
      return new CompoundTag();
    }

    @Override
    public LivingMultiplier deserialize(FriendlyByteBuf buf) {
      return new EffectAmountMultiplier();
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingMultiplier object) {}

    @Override
    public LivingMultiplier createDefaultInstance() {
      return new EffectAmountMultiplier();
    }
  }
}
