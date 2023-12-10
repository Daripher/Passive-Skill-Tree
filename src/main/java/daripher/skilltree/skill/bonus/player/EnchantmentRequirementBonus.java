package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class EnchantmentRequirementBonus implements SkillBonus<EnchantmentRequirementBonus> {
  private float multiplier;

  public EnchantmentRequirementBonus(float multiplier) {
    this.multiplier = multiplier;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.ENCHANTMENT_REQUIREMENT.get();
  }

  @Override
  public EnchantmentRequirementBonus copy() {
    return new EnchantmentRequirementBonus(multiplier);
  }

  @Override
  public EnchantmentRequirementBonus multiply(double multiplier) {
    return new EnchantmentRequirementBonus((float) (getMultiplier() * multiplier));
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    return other instanceof EnchantmentRequirementBonus;
  }

  @Override
  public SkillBonus<EnchantmentRequirementBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof EnchantmentRequirementBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new EnchantmentRequirementBonus(otherBonus.multiplier + this.multiplier);
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent bonusDescription =
        TooltipHelper.getSkillBonusTooltip(
                getDescriptionId() + ".bonus",
                multiplier,
                AttributeModifier.Operation.MULTIPLY_BASE)
            .withStyle(TooltipHelper.getItemBonusStyle(isPositive()));
    return Component.translatable(getDescriptionId(), bonusDescription)
        .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return multiplier < 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int row, Consumer<EnchantmentRequirementBonus> consumer) {
    editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, multiplier)
        .setNumericResponder(
            v -> {
              setMultiplier(v.floatValue());
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
  }

  public void setMultiplier(float multiplier) {
    this.multiplier = multiplier;
  }

  public float getMultiplier() {
    return multiplier;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    EnchantmentRequirementBonus that = (EnchantmentRequirementBonus) obj;
    return Float.floatToIntBits(this.multiplier) == Float.floatToIntBits(that.multiplier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(multiplier);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public EnchantmentRequirementBonus deserialize(JsonObject json) throws JsonParseException {
      float multiplier = json.get("multiplier").getAsFloat();
      return new EnchantmentRequirementBonus(multiplier);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof EnchantmentRequirementBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("multiplier", aBonus.multiplier);
    }

    @Override
    public EnchantmentRequirementBonus deserialize(CompoundTag tag) {
      float multiplier = tag.getFloat("multiplier");
      return new EnchantmentRequirementBonus(multiplier);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof EnchantmentRequirementBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("multiplier", aBonus.multiplier);
      return tag;
    }

    @Override
    public EnchantmentRequirementBonus deserialize(FriendlyByteBuf buf) {
      return new EnchantmentRequirementBonus(buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof EnchantmentRequirementBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.multiplier);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new EnchantmentRequirementBonus(-0.05f);
    }
  }
}
