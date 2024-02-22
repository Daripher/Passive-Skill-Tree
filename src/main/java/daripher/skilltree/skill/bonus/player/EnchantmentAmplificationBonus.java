package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTEnchantmentConditions;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.NoneEnchantmentCondition;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class EnchantmentAmplificationBonus
    implements SkillBonus<EnchantmentAmplificationBonus> {
  private @Nonnull EnchantmentCondition condition;
  private float chance;

  public EnchantmentAmplificationBonus(@Nonnull EnchantmentCondition condition, float chance) {
    this.condition = condition;
    this.chance = chance;
  }

  public EnchantmentAmplificationBonus(float chance) {
    this(NoneEnchantmentCondition.INSTANCE, chance);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.ENCHANTMENT_AMPLIFICATION.get();
  }

  @Override
  public EnchantmentAmplificationBonus copy() {
    return new EnchantmentAmplificationBonus(condition, chance);
  }

  @Override
  public EnchantmentAmplificationBonus multiply(double multiplier) {
    return new EnchantmentAmplificationBonus(condition, (float) (getChance() * multiplier));
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    if (!(other instanceof EnchantmentAmplificationBonus otherBonus)) return false;
    return Objects.equals(otherBonus.condition, this.condition);
  }

  @Override
  public SkillBonus<EnchantmentAmplificationBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof EnchantmentAmplificationBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new EnchantmentAmplificationBonus(condition, otherBonus.chance + this.chance);
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent enchantmentDescription = Component.translatable(condition.getDescriptionId());
    MutableComponent bonusDescription =
        TooltipHelper.getSkillBonusTooltip(
                getDescriptionId() + ".bonus", chance, AttributeModifier.Operation.MULTIPLY_BASE)
            .withStyle(TooltipHelper.getItemBonusStyle(isPositive()));
    return Component.translatable(getDescriptionId(), enchantmentDescription, bonusDescription)
        .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return chance > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int row, Consumer<EnchantmentAmplificationBonus> consumer) {
    editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
    editor.addLabel(55, 0, "Enchantment Condition", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, chance)
        .setNumericResponder(
            v -> {
              setChance(v.floatValue());
              consumer.accept(this.copy());
            });
    editor
        .addDropDownList(55, 0, 145, 14, 10, condition, PSTEnchantmentConditions.conditionsList())
        .setToNameFunc(c -> Component.literal(PSTEnchantmentConditions.getName(c)))
        .setResponder(
            c -> {
              setCondition(c);
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
  }

  public void setCondition(@Nonnull EnchantmentCondition condition) {
    this.condition = condition;
  }

  public void setChance(float chance) {
    this.chance = chance;
  }

  @Nonnull
  public EnchantmentCondition getCondition() {
    return condition;
  }

  public float getChance() {
    return chance;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    EnchantmentAmplificationBonus that = (EnchantmentAmplificationBonus) obj;
    if (!Objects.equals(this.condition, that.condition)) return false;
    return this.chance == that.chance;
  }

  @Override
  public int hashCode() {
    return Objects.hash(condition, chance);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public EnchantmentAmplificationBonus deserialize(JsonObject json) throws JsonParseException {
      EnchantmentCondition condition = SerializationHelper.deserializeEnchantmentCondition(json);
      float multiplier = json.get("chance").getAsFloat();
      return new EnchantmentAmplificationBonus(condition, multiplier);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof EnchantmentAmplificationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeEnchantmentCondition(json, aBonus.condition);
      json.addProperty("chance", aBonus.chance);
    }

    @Override
    public EnchantmentAmplificationBonus deserialize(CompoundTag tag) {
      EnchantmentCondition condition = SerializationHelper.deserializeEnchantmentCondition(tag);
      float multiplier = tag.getFloat("chance");
      return new EnchantmentAmplificationBonus(condition, multiplier);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof EnchantmentAmplificationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeEnchantmentCondition(tag, aBonus.condition);
      tag.putFloat("chance", aBonus.chance);
      return tag;
    }

    @Override
    public EnchantmentAmplificationBonus deserialize(FriendlyByteBuf buf) {
      return new EnchantmentAmplificationBonus(
          NetworkHelper.readEnchantmentCondition(buf), buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof EnchantmentAmplificationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeEnchantmentCondition(buf, aBonus.condition);
      buf.writeFloat(aBonus.chance);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new EnchantmentAmplificationBonus(0.1f);
    }
  }
}
