package daripher.skilltree.skill.bonus.condition.living.numeric.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTNumericValueProviders;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class DistanceToTargetProvider implements NumericValueProvider<DistanceToTargetProvider> {
  @Override
  public float getValue(LivingEntity entity) {
    if (!(entity instanceof Player player)) {
      return 0f;
    }
    int lastTargetId = player.getPersistentData().getInt("LastAttackTarget");
    Entity target = entity.level().getEntity(lastTargetId);
    if (target == null) {
      return 0f;
    }
    return target.distanceTo(entity);
  }

  @Override
  public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
    String key = "%s.multiplier.%s".formatted(getDescriptionId(), target.getName());
    if (divisor != 1) {
      key += ".plural";
      return Component.translatable(key, bonusTooltip, formatNumber(divisor));
    } else {
      return Component.translatable(key, bonusTooltip);
    }
  }

  @Override
  public MutableComponent getConditionTooltip(SkillBonus.Target target, NumericValueCondition.Logic logic, Component bonusTooltip,
                                              float requiredValue) {
    String key = "%s.condition.%s".formatted(getDescriptionId(), target.getName());
    String valueDescription = formatNumber(requiredValue);
    Component logicDescription = logic.getTooltip("distance_to_target", valueDescription);
    return Component.translatable(key, bonusTooltip, logicDescription);
  }

  @Override
  public MutableComponent getRequirementTooltip(NumericValueCondition.Logic logic, float requiredValue) {
    return Component.literal("Unsupported").withStyle(ChatFormatting.RED);
  }

  @Override
  public NumericValueProvider.Serializer getSerializer() {
    return PSTNumericValueProviders.DISTANCE_TO_TARGET.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer) {
  }

  public static class Serializer implements NumericValueProvider.Serializer {
    @Override
    public NumericValueProvider<?> deserialize(JsonObject json) throws JsonParseException {
      return new DistanceToTargetProvider();
    }

    @Override
    public void serialize(JsonObject json, NumericValueProvider<?> provider) {
      if (!(provider instanceof DistanceToTargetProvider)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public NumericValueProvider<?> deserialize(CompoundTag tag) {
      return new DistanceToTargetProvider();
    }

    @Override
    public CompoundTag serialize(NumericValueProvider<?> provider) {
      if (!(provider instanceof DistanceToTargetProvider)) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public NumericValueProvider<?> deserialize(FriendlyByteBuf buf) {
      return new DistanceToTargetProvider();
    }

    @Override
    public void serialize(FriendlyByteBuf buf, NumericValueProvider<?> provider) {
      if (!(provider instanceof DistanceToTargetProvider)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public NumericValueProvider<?> createDefaultInstance() {
      return new DistanceToTargetProvider();
    }
  }
}
