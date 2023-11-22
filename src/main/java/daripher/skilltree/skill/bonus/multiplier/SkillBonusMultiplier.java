package daripher.skilltree.skill.bonus.multiplier;

import daripher.skilltree.init.PSTRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface SkillBonusMultiplier {
  float getValue(Player player);

  Serializer getSerializer();

  default String getDescriptionId() {
    ResourceLocation id = PSTRegistries.BONUS_MULTIPLIERS.get().getKey(getSerializer());
    assert id != null;
    return "skill_bonus_multiplier.%s.%s".formatted(id.getNamespace(), id.getPath());
  }

  MutableComponent getTooltip(MutableComponent bonusTooltip);

  interface Serializer
      extends daripher.skilltree.data.serializers.Serializer<SkillBonusMultiplier> {}
}
