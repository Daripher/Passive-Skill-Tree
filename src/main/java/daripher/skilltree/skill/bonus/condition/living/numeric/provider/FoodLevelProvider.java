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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.function.Consumer;

public class FoodLevelProvider implements NumericValueProvider<FoodLevelProvider> {
  private boolean percentage;
  private boolean missing;

  public FoodLevelProvider(boolean percentage, boolean missing) {
    this.percentage = percentage;
    this.missing = missing;
  }

  @Override
  public float getValue(LivingEntity entity) {
    if (!(entity instanceof Player player)) {
      return 0f;
    }
    float value = player.getFoodData().getFoodLevel();
    if (missing) {
      value = 20 - value;
    }
    if (percentage) {
      value = value / 20;
    }
    return value;
  }

  @Override
  public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
    String key = "%s.multiplier.%s".formatted(getDescriptionId(), target.getName());
    String pointsKey = getDescriptionId() + ".point";
    if (divisor != 1) {
      key += ".plural";
      pointsKey += ".plural";
    }
    Component pointsDescription = Component.translatable(pointsKey);
    if (missing) {
      key += ".missing";
    }
    if (divisor != 1) {
      return Component.translatable(key, bonusTooltip, formatNumber(divisor), pointsDescription);
    } else {
      return Component.translatable(key, bonusTooltip, pointsDescription);
    }
  }

  @Override
  public MutableComponent getConditionTooltip(SkillBonus.Target target, NumericValueCondition.Logic logic, Component bonusTooltip,
                                              float requiredValue) {
    String key = "%s.condition.%s".formatted(getDescriptionId(), target.getName());
    String pointsKey = getDescriptionId() + ".point";
    if (requiredValue != 1) {
      pointsKey += ".plural";
    }
    Component pointsDescription = Component.translatable(pointsKey);
    if (logic == NumericValueCondition.Logic.EQUAL && percentage && requiredValue == 1) {
      return Component.translatable(key + ".full", bonusTooltip);
    }
    if (logic == NumericValueCondition.Logic.LESS && percentage && requiredValue == 1) {
      return Component.translatable(key + ".not_full", bonusTooltip);
    }
    if (missing) {
      key += ".missing";
    }
    String valueDescription = formatNumber(requiredValue);
    Component logicDescription = logic.getTooltip("food_level", valueDescription);
    return Component.translatable(key, bonusTooltip, logicDescription, pointsDescription);
  }

  @Override
  public MutableComponent getRequirementTooltip(NumericValueCondition.Logic logic, float requiredValue) {
    String key = "%s.requirement".formatted(getDescriptionId());
    String pointsKey = getDescriptionId() + ".point";
    if (requiredValue != 1) {
      pointsKey += ".plural";
    }
    Component pointsDescription = Component.translatable(pointsKey);
    if (logic == NumericValueCondition.Logic.EQUAL && percentage && requiredValue == 1) {
      return Component.translatable(key + ".full");
    }
    if (logic == NumericValueCondition.Logic.LESS && percentage && requiredValue == 1) {
      return Component.translatable(key + ".not_full");
    }
    if (missing) {
      key += ".missing";
    }
    String valueDescription = formatNumber(requiredValue);
    Component logicDescription = logic.getTooltip("food_level", valueDescription);
    return Component.translatable(key, logicDescription, pointsDescription);
  }

  @Override
  public String formatNumber(float number) {
    if (percentage) {
      return NumericValueProvider.super.formatNumber(number * 100) + "%";
    }
    return NumericValueProvider.super.formatNumber(number);
  }

  @Override
  public NumericValueProvider.Serializer getSerializer() {
    return PSTNumericValueProviders.FOOD_LEVEL.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer) {
    editor.addLabel(0, 0, "Missing", ChatFormatting.GREEN);
    editor.addLabel(55, 0, "Percentage", ChatFormatting.GREEN);
    editor.increaseHeight(19);
    editor.addCheckBox(0, 0, missing).setResponder(v -> selectMissingMode(consumer, v));
    editor.addCheckBox(55, 0, percentage).setResponder(v -> selectPercentageMode(consumer, v));
    editor.increaseHeight(19);
  }

  private void selectMissingMode(Consumer<NumericValueProvider<?>> consumer, boolean missing) {
    setMissing(missing);
    consumer.accept(this);
  }

  private void selectPercentageMode(Consumer<NumericValueProvider<?>> consumer, boolean percentage) {
    setPercentage(percentage);
    consumer.accept(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FoodLevelProvider that = (FoodLevelProvider) o;
    return percentage == that.percentage && missing == that.missing;
  }

  @Override
  public int hashCode() {
    return Objects.hash(percentage, missing);
  }

  public void setMissing(boolean missing) {
    this.missing = missing;
  }

  public void setPercentage(boolean percentage) {
    this.percentage = percentage;
  }

  public static class Serializer implements NumericValueProvider.Serializer {
    @Override
    public NumericValueProvider<?> deserialize(JsonObject json) throws JsonParseException {
      boolean percentage = json.get("percentage").getAsBoolean();
      boolean missing = json.get("missing").getAsBoolean();
      return new FoodLevelProvider(percentage, missing);
    }

    @Override
    public void serialize(JsonObject json, NumericValueProvider<?> provider) {
      if (!(provider instanceof FoodLevelProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("percentage", aProvider.percentage);
      json.addProperty("missing", aProvider.missing);
    }

    @Override
    public NumericValueProvider<?> deserialize(CompoundTag tag) {
      boolean percentage = tag.getBoolean("percentage");
      boolean missing = tag.getBoolean("missing");
      return new FoodLevelProvider(percentage, missing);
    }

    @Override
    public CompoundTag serialize(NumericValueProvider<?> provider) {
      if (!(provider instanceof FoodLevelProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("percentage", aProvider.percentage);
      tag.putBoolean("missing", aProvider.missing);
      return tag;
    }

    @Override
    public NumericValueProvider<?> deserialize(FriendlyByteBuf buf) {
      boolean percentage = buf.readBoolean();
      boolean missing = buf.readBoolean();
      return new FoodLevelProvider(percentage, missing);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, NumericValueProvider<?> provider) {
      if (!(provider instanceof FoodLevelProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      buf.writeBoolean(aProvider.percentage);
      buf.writeBoolean(aProvider.missing);
    }

    @Override
    public NumericValueProvider<?> createDefaultInstance() {
      return new FoodLevelProvider(false, false);
    }
  }
}
