package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTLivingMultipliers;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public final class MissingHealthPointsMultiplier implements LivingMultiplier {
  private float divisor;

  public MissingHealthPointsMultiplier(float divisor) {
    this.divisor = divisor;
  }

  @Override
  public float getValue(LivingEntity entity) {
    return (int) ((entity.getMaxHealth() - entity.getHealth()) / divisor);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
    String multiplierDescription = getDescriptionId(target);
    String divisorDescription = TooltipHelper.formatNumber(divisor);
    return Component.translatable(multiplierDescription, bonusTooltip, divisorDescription);
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
    return PSTLivingMultipliers.MISSING_HEALTH_POINTS.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MissingHealthPointsMultiplier that = (MissingHealthPointsMultiplier) o;
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
      return new MissingHealthPointsMultiplier(json.get("divisor").getAsFloat());
    }

    @Override
    public void serialize(JsonObject json, LivingMultiplier multiplier) {
      if (!(multiplier instanceof MissingHealthPointsMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("divisor", aMultiplier.divisor);
    }

    @Override
    public LivingMultiplier deserialize(CompoundTag tag) {
      float divisor = tag.getFloat("divisor");
      return new MissingHealthPointsMultiplier(divisor);
    }

    @Override
    public CompoundTag serialize(LivingMultiplier multiplier) {
      if (!(multiplier instanceof MissingHealthPointsMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("divisor", aMultiplier.divisor);
      return tag;
    }

    @Override
    public LivingMultiplier deserialize(FriendlyByteBuf buf) {
      return new MissingHealthPointsMultiplier(buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingMultiplier multiplier) {
      if (!(multiplier instanceof MissingHealthPointsMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aMultiplier.divisor);
    }

    @Override
    public LivingMultiplier createDefaultInstance() {
      return new MissingHealthPointsMultiplier(2f);
    }
  }
}
