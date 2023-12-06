package daripher.skilltree.skill.bonus.condition.damage;

import daripher.skilltree.init.PSTRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;

public interface DamageCondition {
  boolean met(DamageSource source);

  default String getDescriptionId() {
    ResourceLocation id = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(getSerializer());
    assert id != null;
    return "damage_condition.%s.%s".formatted(id.getNamespace(), id.getPath());
  }

  MutableComponent getTooltip(MutableComponent bonusTooltip);

  Serializer getSerializer();

  interface Serializer extends daripher.skilltree.data.serializers.Serializer<DamageCondition> {
    DamageCondition createDefaultInstance();
  }
}
