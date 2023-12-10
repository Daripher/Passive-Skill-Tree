package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.init.PSTLivingConditions;
import java.util.Objects;
import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class HealthPercentageCondition implements LivingCondition {
  private float min;
  private float max;

  public HealthPercentageCondition(float min, float max) {
    this.min = min;
    this.max = max;
  }

  @Override
  public boolean met(LivingEntity living) {
    float percentage = living.getHealth() / living.getMaxHealth();
    if (min == -1) {
      return percentage <= max;
    }
    if (max == -1) {
      return percentage >= min;
    }
    return percentage <= max && percentage >= min;
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, String target) {
    String key = getDescriptionId();
    MutableComponent targetDescription =
        Component.translatable("%s.target.%s".formatted(key, target));
    String min = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.min * 100);
    String max = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.max * 100);
    if (this.min == -1) {
      return Component.translatable(key + ".max", bonusTooltip, targetDescription, max);
    }
    if (this.max == -1) {
      return Component.translatable(key + ".min", bonusTooltip, targetDescription, min);
    }
    return Component.translatable(key + ".range", bonusTooltip, targetDescription, min, max);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.HEALTH_PERCENTAGE.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<LivingCondition> consumer) {
    editor.addLabel(0, 0, "Min", ChatFormatting.GREEN);
    editor.addLabel(55, 0, "Max", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, min)
        .setNumericResponder(
            a -> {
              setMin(a.floatValue());
              consumer.accept(this);
            });
    editor
        .addNumericTextField(55, 0, 50, 14, max)
        .setNumericResponder(
            a -> {
              setMax(a.floatValue());
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HealthPercentageCondition that = (HealthPercentageCondition) o;
    return Float.compare(min, that.min) == 0 && Float.compare(max, that.max) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, max);
  }

  public void setMin(float min) {
    this.min = min;
  }

  public void setMax(float max) {
    this.max = max;
  }

  public float getMin() {
    return min;
  }

  public float getMax() {
    return max;
  }

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      float min = json.has("min") ? json.get("min").getAsFloat() : -1;
      float max = json.has("max") ? json.get("max").getAsFloat() : -1;
      return new HealthPercentageCondition(min, max);
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof HealthPercentageCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      if (aCondition.min != -1) {
        json.addProperty("min", aCondition.min);
      }
      if (aCondition.max != -1) {
        json.addProperty("max", aCondition.max);
      }
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      float min = tag.contains("Min") ? tag.getFloat("Min") : -1;
      float max = tag.contains("Max") ? tag.getFloat("Max") : -1;
      return new HealthPercentageCondition(min, max);
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof HealthPercentageCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      if (aCondition.min != -1) {
        tag.putFloat("Min", aCondition.min);
      }
      if (aCondition.max != -1) {
        tag.putFloat("Max", aCondition.max);
      }
      return tag;
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new HealthPercentageCondition(buf.readFloat(), buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof HealthPercentageCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aCondition.min);
      buf.writeFloat(aCondition.max);
    }

    @Override
    public LivingCondition createDefaultInstance() {
      return new HealthPercentageCondition(-1, 0.5f);
    }
  }
}
