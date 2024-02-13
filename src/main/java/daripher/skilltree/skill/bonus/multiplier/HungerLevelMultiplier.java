package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTLivingMultipliers;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class HungerLevelMultiplier implements LivingMultiplier {
  @Override
  public float getValue(LivingEntity entity) {
    if (!(entity instanceof Player player)) return 1f;
    return player.getFoodData().getFoodLevel();
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
    return Component.translatable(getDescriptionId(target), bonusTooltip);
  }

  @Override
  public LivingMultiplier.Serializer getSerializer() {
    return PSTLivingMultipliers.FOOD_LEVEL.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    return o != null && getClass() == o.getClass();
  }

  public static class Serializer implements LivingMultiplier.Serializer {
    @Override
    public LivingMultiplier deserialize(JsonObject json) throws JsonParseException {
      return new HungerLevelMultiplier();
    }

    @Override
    public void serialize(JsonObject json, LivingMultiplier object) {}

    @Override
    public LivingMultiplier deserialize(CompoundTag tag) {
      return new HungerLevelMultiplier();
    }

    @Override
    public CompoundTag serialize(LivingMultiplier object) {
      return new CompoundTag();
    }

    @Override
    public LivingMultiplier deserialize(FriendlyByteBuf buf) {
      return new HungerLevelMultiplier();
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingMultiplier object) {}

    @Override
    public LivingMultiplier createDefaultInstance() {
      return new HungerLevelMultiplier();
    }
  }
}
