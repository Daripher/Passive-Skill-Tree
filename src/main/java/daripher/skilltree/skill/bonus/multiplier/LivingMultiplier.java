package daripher.skilltree.skill.bonus.multiplier;

import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface LivingMultiplier {
  float getValue(LivingEntity entity);

  Serializer getSerializer();

  default String getDescriptionId(SkillBonus.Target target) {
    ResourceLocation id = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(getSerializer());
    Objects.requireNonNull(id);
    String targetDescription = target.name().toLowerCase();
    return "skill_bonus_multiplier.%s.%s.%s"
        .formatted(id.getNamespace(), id.getPath(), targetDescription);
  }

  MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target);

  default void addEditorWidgets(
      SkillTreeEditorScreen editor, Consumer<LivingMultiplier> consumer) {}

  interface Serializer extends daripher.skilltree.data.serializers.Serializer<LivingMultiplier> {
    LivingMultiplier createDefaultInstance();
  }
}
