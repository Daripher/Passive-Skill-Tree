package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTLivingMultipliers;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public final class MissingHealthPercentageMultiplier implements LivingMultiplier {
  private float divisor;

  public MissingHealthPercentageMultiplier(float divisor) {
    this.divisor = divisor;
  }

  @Override
  public float getValue(LivingEntity entity) {
    return (int) (100 - entity.getHealth() / entity.getMaxHealth() * 100);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip) {
    if (divisor != 1) {
      String divisorDescription = TooltipHelper.formatNumber(divisor) + "%";
      return Component.translatable(
          getDescriptionId() + ".divisor", bonusTooltip, divisorDescription);
    }
    return Component.translatable(getDescriptionId(), bonusTooltip);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<LivingMultiplier> consumer) {
    editor.addLabel(0, 0, "Divisor", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, divisor)
        .setNumericFilter(d -> d > 0)
        .setNumericResponder(
            v -> {
              setDivisor(v.floatValue());
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  @Override
  public LivingMultiplier.Serializer getSerializer() {
    return PSTLivingMultipliers.MISSING_HEALTH_PERCENTAGE.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MissingHealthPercentageMultiplier that = (MissingHealthPercentageMultiplier) o;
    return Float.compare(divisor, that.divisor) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(divisor);
  }

  public void setDivisor(float divisor) {
    this.divisor = divisor;
  }

  public static class Serializer implements LivingMultiplier.Serializer {
    @Override
    public LivingMultiplier deserialize(JsonObject json) throws JsonParseException {
      return new MissingHealthPercentageMultiplier(json.get("divisor").getAsFloat());
    }

    @Override
    public void serialize(JsonObject json, LivingMultiplier multiplier) {
      if (!(multiplier instanceof MissingHealthPercentageMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("divisor", aMultiplier.divisor);
    }

    @Override
    public LivingMultiplier deserialize(CompoundTag tag) {
      float divisor = tag.getFloat("divisor");
      return new MissingHealthPercentageMultiplier(divisor);
    }

    @Override
    public CompoundTag serialize(LivingMultiplier multiplier) {
      if (!(multiplier instanceof MissingHealthPercentageMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("divisor", aMultiplier.divisor);
      return tag;
    }

    @Override
    public LivingMultiplier deserialize(FriendlyByteBuf buf) {
      return new MissingHealthPercentageMultiplier(buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingMultiplier multiplier) {
      if (!(multiplier instanceof MissingHealthPercentageMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aMultiplier.divisor);
    }

    @Override
    public LivingMultiplier createDefaultInstance() {
      return new MissingHealthPercentageMultiplier(10f);
    }
  }
}
