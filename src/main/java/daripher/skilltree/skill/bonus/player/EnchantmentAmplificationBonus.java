package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.enchantment.EnchantmentCondition;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public record EnchantmentAmplificationBonus(EnchantmentCondition condition, float chance)
    implements SkillBonus<EnchantmentAmplificationBonus> {
  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.ENCHANTMENT_AMPLIFICATION.get();
  }

  @Override
  public SkillBonus<EnchantmentAmplificationBonus> copy() {
    return new EnchantmentAmplificationBonus(condition, chance);
  }

  @Override
  public EnchantmentAmplificationBonus multiply(double multiplier) {
    return new EnchantmentAmplificationBonus(condition, (float) (chance() * multiplier));
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
    double visibleChance = chance * 100;
    if (chance < 0) visibleChance *= -1;
    String operationDescription = chance > 0 ? "plus" : "take";
    operationDescription = "attribute.modifier." + operationDescription + ".1";
    String multiplierDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleChance);
    MutableComponent bonusDescription = Component.translatable(getDescriptionId() + ".bonus");
    bonusDescription =
        Component.translatable(operationDescription, multiplierDescription, bonusDescription)
            .withStyle(Style.EMPTY.withColor(0x7AB3E2));
    return Component.translatable(getDescriptionId(), enchantmentDescription, bonusDescription)
        .withStyle(Style.EMPTY.withColor(0x7B7BE5));
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    // TODO: add widgets
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
  }
}
