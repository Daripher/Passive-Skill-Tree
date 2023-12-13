package daripher.skilltree.skill.bonus.multiplier;

import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.init.PSTRegistries;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface LivingMultiplier {
  float getValue(LivingEntity entity);

  Serializer getSerializer();

  default String getDescriptionId() {
    ResourceLocation id = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(getSerializer());
    Objects.requireNonNull(id);
    return "skill_bonus_multiplier.%s.%s".formatted(id.getNamespace(), id.getPath());
  }

  MutableComponent getTooltip(MutableComponent bonusTooltip);

  default void addEditorWidgets(
      SkillTreeEditorScreen editor, Consumer<LivingMultiplier> consumer) {}

  interface Serializer extends daripher.skilltree.data.serializers.Serializer<LivingMultiplier> {
    LivingMultiplier createDefaultInstance();
  }
}
