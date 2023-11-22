package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTSkillBonusMultipliers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class HungerLevelMultiplier implements SkillBonusMultiplier {
  @Override
  public float getValue(Player player) {
    return player.getFoodData().getFoodLevel();
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip) {
    return Component.translatable(getDescriptionId(), bonusTooltip);
  }

  @Override
  public SkillBonusMultiplier.Serializer getSerializer() {
    return PSTSkillBonusMultipliers.FOOD_LEVEL.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    return o != null && getClass() == o.getClass();
  }

  public static class Serializer implements SkillBonusMultiplier.Serializer {
    @Override
    public SkillBonusMultiplier deserialize(JsonObject json) throws JsonParseException {
      return new HungerLevelMultiplier();
    }

    @Override
    public void serialize(JsonObject json, SkillBonusMultiplier object) {}

    @Override
    public SkillBonusMultiplier deserialize(CompoundTag tag) {
      return new HungerLevelMultiplier();
    }

    @Override
    public CompoundTag serialize(SkillBonusMultiplier object) {
      return new CompoundTag();
    }

    @Override
    public SkillBonusMultiplier deserialize(FriendlyByteBuf buf) {
      return new HungerLevelMultiplier();
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonusMultiplier object) {}
  }
}
