package daripher.skilltree.skill.bonus.condition.living.numeric.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTNumericValueProviders;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.effect.EffectType;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Consumer;

public class EffectAmountProvider implements NumericValueProvider<EffectAmountProvider> {
  private EffectType effectType;

  public EffectAmountProvider(EffectType effectType) {
    this.effectType = effectType;
  }

  @Override
  public float getValue(LivingEntity entity) {
    List<MobEffect> effects = entity.getActiveEffects().stream().map(MobEffectInstance::getEffect).toList();
    return switch (effectType) {
      case ANY -> effects.size();
      case NEUTRAL -> effects.stream().filter(e -> e.getCategory() == MobEffectCategory.NEUTRAL).count();
      case HARMFUL -> effects.stream().filter(e -> e.getCategory() == MobEffectCategory.HARMFUL).count();
      case BENEFICIAL -> effects.stream().filter(e -> e.getCategory() == MobEffectCategory.BENEFICIAL).count();
    };
  }

  @Override
  public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
    String key = "%s.multiplier.%s".formatted(getDescriptionId(), target.getName());
    String effectTypeKey = effectType.getDescriptionId();
    if (divisor != 1) {
      effectTypeKey += ".plural";
      key += ".plural";
      Component effectDescription = Component.translatable(effectTypeKey);
      return Component.translatable(key, bonusTooltip, formatNumber(divisor), effectDescription);
    } else {
      Component effectDescription = Component.translatable(effectTypeKey);
      return Component.translatable(key, bonusTooltip, effectDescription);
    }
  }

  @Override
  public MutableComponent getConditionTooltip(SkillBonus.Target target, NumericValueCondition.Logic logic, Component bonusTooltip,
                                              float requiredValue) {
    String key = "%s.condition.%s".formatted(getDescriptionId(), target.getName());
    String effectTypeKey = effectType.getDescriptionId();
    if (!(requiredValue == 0 && logic == NumericValueCondition.Logic.MORE)) {
      if (requiredValue != 1) {
        effectTypeKey += ".plural";
      }
    }
    Component effectDescription = Component.translatable(effectTypeKey);
    if (requiredValue == 0 && logic == NumericValueCondition.Logic.EQUAL) {
      return Component.translatable(key + ".none", bonusTooltip, effectDescription);
    }
    if (requiredValue == 0 && logic == NumericValueCondition.Logic.MORE) {
      return Component.translatable(key + ".any", bonusTooltip, effectDescription);
    }
    String valueDescription = formatNumber(requiredValue);
    Component logicDescription = logic.getTooltip("effect_amount", valueDescription);
    return Component.translatable(key, bonusTooltip, logicDescription, effectDescription);
  }

  @Override
  public MutableComponent getRequirementTooltip(NumericValueCondition.Logic logic, float requiredValue) {
    String key = "%s.requirement".formatted(getDescriptionId());
    String effectTypeKey = effectType.getDescriptionId();
    if (!(requiredValue == 0 && logic == NumericValueCondition.Logic.MORE)) {
      if (requiredValue != 1) {
        effectTypeKey += ".plural";
      }
    }
    Component effectDescription = Component.translatable(effectTypeKey);
    if (requiredValue == 0 && logic == NumericValueCondition.Logic.EQUAL) {
      return Component.translatable(key + ".none", effectDescription);
    }
    if (requiredValue == 0 && logic == NumericValueCondition.Logic.MORE) {
      return Component.translatable(key + ".any", effectDescription);
    }
    String valueDescription = formatNumber(requiredValue);
    Component logicDescription = logic.getTooltip("effect_amount", valueDescription);
    return Component.translatable(key, logicDescription, effectDescription);
  }

  @Override
  public NumericValueProvider.Serializer getSerializer() {
    return PSTNumericValueProviders.EFFECT_AMOUNT.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer) {
    editor.addLabel(0, 0, "Effect Type", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, effectType).setElementNameGetter(effectType -> Component.literal(effectType.name())).setResponder(effectType -> selectEffectType(consumer, effectType));
    editor.increaseHeight(19);
  }

  private void selectEffectType(Consumer<NumericValueProvider<?>> consumer, EffectType type) {
    setEffectType(type);
    consumer.accept(this);
  }

  public void setEffectType(EffectType type) {
    this.effectType = type;
  }

  public static class Serializer implements NumericValueProvider.Serializer {
    @Override
    public NumericValueProvider<?> deserialize(JsonObject json) throws JsonParseException {
      EffectType type = EffectType.fromName(json.get("effect_type").getAsString());
      return new EffectAmountProvider(type);
    }

    @Override
    public void serialize(JsonObject json, NumericValueProvider<?> provider) {
      if (!(provider instanceof EffectAmountProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("effect_type", aProvider.effectType.getName());
    }

    @Override
    public NumericValueProvider<?> deserialize(CompoundTag tag) {
      EffectType type = EffectType.fromName(tag.getString("effect_type"));
      return new EffectAmountProvider(type);
    }

    @Override
    public CompoundTag serialize(NumericValueProvider<?> provider) {
      if (!(provider instanceof EffectAmountProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putString("effect_type", aProvider.effectType.getName());
      return tag;
    }

    @Override
    public NumericValueProvider<?> deserialize(FriendlyByteBuf buf) {
      EffectType type = EffectType.values()[buf.readInt()];
      return new EffectAmountProvider(type);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, NumericValueProvider<?> provider) {
      if (!(provider instanceof EffectAmountProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      buf.writeInt(aProvider.effectType.ordinal());
    }

    @Override
    public NumericValueProvider<?> createDefaultInstance() {
      return new EffectAmountProvider(EffectType.ANY);
    }
  }
}
