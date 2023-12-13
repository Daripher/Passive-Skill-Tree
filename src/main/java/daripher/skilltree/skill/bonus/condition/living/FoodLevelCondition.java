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
import net.minecraft.world.entity.player.Player;

public final class FoodLevelCondition implements LivingCondition {
  private int min;
  private int max;

  public FoodLevelCondition(int min, int max) {
    this.min = min;
    this.max = max;
  }

  @Override
  public boolean met(LivingEntity living) {
    if (!(living instanceof Player player)) return false;
    int hunger = player.getFoodData().getFoodLevel();
    if (min == -1) return hunger <= max;
    if (max == -1) return hunger >= min;
    return hunger <= max && hunger >= min;
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, String target) {
    String key = getDescriptionId();
    MutableComponent targetDescription =
        Component.translatable("%s.target.%s".formatted(key, target));
    if (min == -1) {
      return Component.translatable(key + ".max", bonusTooltip, targetDescription, max);
    }
    if (max == -1) {
      if (min == 1)
        return Component.translatable(key + ".min.1", bonusTooltip, targetDescription, min);
      return Component.translatable(key + ".min", bonusTooltip, targetDescription, min);
    }
    return Component.translatable(key + ".range", bonusTooltip, targetDescription, min, max);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.FOOD_LEVEL.get();
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
              setMin(a.intValue());
              consumer.accept(this);
            });
    editor
        .addNumericTextField(55, 0, 50, 14, max)
        .setNumericResponder(
            a -> {
              setMax(a.intValue());
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FoodLevelCondition that = (FoodLevelCondition) o;
    return min == that.min && max == that.max;
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, max);
  }

  public void setMin(int min) {
    this.min = min;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      int min = json.has("min") ? json.get("min").getAsInt() : -1;
      int max = json.has("max") ? json.get("max").getAsInt() : -1;
      return new FoodLevelCondition(min, max);
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof FoodLevelCondition aCondition)) {
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
      int min = tag.contains("min") ? tag.getInt("min") : -1;
      int max = tag.contains("max") ? tag.getInt("max") : -1;
      return new FoodLevelCondition(min, max);
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof FoodLevelCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      if (aCondition.min != -1) {
        tag.putInt("min", aCondition.min);
      }
      if (aCondition.max != -1) {
        tag.putInt("max", aCondition.max);
      }
      return tag;
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new FoodLevelCondition(buf.readInt(), buf.readInt());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof FoodLevelCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      buf.writeInt(aCondition.min);
      buf.writeInt(aCondition.max);
    }

    @Override
    public LivingCondition createDefaultInstance() {
      return new FoodLevelCondition(15, -1);
    }
  }
}
