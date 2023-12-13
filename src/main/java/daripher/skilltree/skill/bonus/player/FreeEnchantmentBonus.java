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

public final class FreeEnchantmentBonus implements SkillBonus<FreeEnchantmentBonus> {
  private float chance;

  public FreeEnchantmentBonus(float chance) {
    this.chance = chance;
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.FREE_ENCHANTMENT.get();
  }

  @Override
  public FreeEnchantmentBonus copy() {
    return new FreeEnchantmentBonus(chance);
  }

  @Override
  public FreeEnchantmentBonus multiply(double multiplier) {
    return new FreeEnchantmentBonus((float) (getChance() * multiplier));
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    return other instanceof FreeEnchantmentBonus;
  }

  @Override
  public SkillBonus<FreeEnchantmentBonus> merge(SkillBonus<?> other) {
    if (!(other instanceof FreeEnchantmentBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new FreeEnchantmentBonus(otherBonus.chance + this.chance);
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent bonusDescription =
        TooltipHelper.getSkillBonusTooltip(
                getDescriptionId() + ".bonus", chance, AttributeModifier.Operation.MULTIPLY_BASE)
            .withStyle(TooltipHelper.getItemBonusStyle(isPositive()));
    return Component.translatable(getDescriptionId(), bonusDescription)
        .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
  }

  @Override
  public boolean isPositive() {
    return chance > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int row, Consumer<FreeEnchantmentBonus> consumer) {
    editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, chance)
        .setNumericResponder(
            v -> {
              setChance(v.floatValue());
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
  }

  public void setChance(float chance) {
    this.chance = chance;
  }

  public float getChance() {
    return chance;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    FreeEnchantmentBonus that = (FreeEnchantmentBonus) obj;
    return Float.floatToIntBits(this.chance) == Float.floatToIntBits(that.chance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(chance);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public FreeEnchantmentBonus deserialize(JsonObject json) throws JsonParseException {
      float multiplier = json.get("chance").getAsFloat();
      return new FreeEnchantmentBonus(multiplier);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof FreeEnchantmentBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.chance);
    }

    @Override
    public FreeEnchantmentBonus deserialize(CompoundTag tag) {
      float multiplier = tag.getFloat("chance");
      return new FreeEnchantmentBonus(multiplier);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof FreeEnchantmentBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.chance);
      return tag;
    }

    @Override
    public FreeEnchantmentBonus deserialize(FriendlyByteBuf buf) {
      return new FreeEnchantmentBonus(buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof FreeEnchantmentBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.chance);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new FreeEnchantmentBonus(0.05f);
    }
  }
}
