package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public record UnarmedCondition() implements LivingCondition {
  @Override
  public boolean isConditionMet(LivingEntity living) {
    return !EquipmentCondition.isWeapon(living.getOffhandItem())
        && !EquipmentCondition.isWeapon(living.getMainHandItem());
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
    String key = getDescriptionId();
    MutableComponent targetDescription =
        Component.translatable("%s.target.%s".formatted(key, target.getName()));
    return Component.translatable(key, bonusTooltip, targetDescription);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.UNARMED.get();
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
      return new UnarmedCondition();
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof UnarmedCondition)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      return new UnarmedCondition();
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof UnarmedCondition)) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new UnarmedCondition();
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof UnarmedCondition)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public LivingCondition createDefaultInstance() {
      return new UnarmedCondition();
    }
  }
}
