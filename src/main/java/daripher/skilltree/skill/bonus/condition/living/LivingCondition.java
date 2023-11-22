package daripher.skilltree.skill.bonus.condition.living;

import daripher.skilltree.init.PSTRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface LivingCondition {
  boolean met(LivingEntity living);

  default String getDescriptionId() {
    ResourceLocation id = PSTRegistries.LIVING_CONDITIONS.get().getKey(getSerializer());
    assert id != null;
    return "living_condition.%s.%s".formatted(id.getNamespace(), id.getPath());
  }

  MutableComponent getTooltip(MutableComponent bonusTooltip, String target);

  Serializer getSerializer();

  interface Serializer extends daripher.skilltree.data.serializers.Serializer<LivingCondition> {}
}
