package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public record EnchantmentRequirementBonus(float multiplier)
    implements SkillBonus<EnchantmentRequirementBonus> {
  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.ENCHANTMENT_REQUIREMENT.get();
  }

  @Override
  public SkillBonus<EnchantmentRequirementBonus> copy() {
    return new EnchantmentRequirementBonus(multiplier);
  }

  @Override
  public EnchantmentRequirementBonus multiply(double multiplier) {
    return new EnchantmentRequirementBonus((float) (multiplier() * multiplier));
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
    double visibleMultiplier = multiplier * 100;
    if (multiplier < 0) visibleMultiplier *= -1;
    String operationDescription = multiplier > 0 ? "plus" : "take";
    operationDescription = "attribute.modifier." + operationDescription + ".1";
    String multiplierDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleMultiplier);
    MutableComponent bonusDescription = Component.translatable(getDescriptionId() + ".bonus");
    bonusDescription =
        Component.translatable(operationDescription, multiplierDescription, bonusDescription)
            .withStyle(Style.EMPTY.withColor(0x7AB3E2));
    return Component.translatable(getDescriptionId(), bonusDescription)
        .withStyle(Style.EMPTY.withColor(0x7B7BE5));
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    // TODO: add widgets
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
  }
}
