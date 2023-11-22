package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.network.NetworkHelper;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public record HasEffectCondition(MobEffect effect) implements LivingCondition {
  @Override
  public boolean met(LivingEntity living) {
    return living.hasEffect(this.effect);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, String target) {
    String key = getDescriptionId();
    MutableComponent targetDescription =
        Component.translatable("%s.target.%s".formatted(key, target));
    return Component.translatable(key, bonusTooltip, targetDescription, effect.getDisplayName());
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.HAS_EFFECT.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HasEffectCondition that = (HasEffectCondition) o;
    return Objects.equals(effect, that.effect);
  }

  @Override
  public int hashCode() {
    return Objects.hash(effect);
  }

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      MobEffect effect = SerializationHelper.deserializeEffect(json);
      return new HasEffectCondition(effect);
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof HasEffectCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeEffect(json, aCondition.effect);
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      MobEffect effect = SerializationHelper.deserializeEffect(tag);
      return new HasEffectCondition(effect);
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof HasEffectCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeEffect(tag, aCondition.effect);
      return tag;
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new HasEffectCondition(NetworkHelper.readEffect(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof HasEffectCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeEffect(buf, aCondition.effect);
    }
  }
}
