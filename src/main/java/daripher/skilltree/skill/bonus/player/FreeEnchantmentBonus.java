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

public record FreeEnchantmentBonus(float chance) implements SkillBonus<FreeEnchantmentBonus> {
  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.FREE_ENCHANTMENT.get();
  }

  @Override
  public SkillBonus<FreeEnchantmentBonus> copy() {
    return new FreeEnchantmentBonus(chance);
  }

  @Override
  public FreeEnchantmentBonus multiply(double multiplier) {
    return new FreeEnchantmentBonus((float) (chance() * multiplier));
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
    double visibleChance = chance * 100;
    if (chance < 0) visibleChance *= -1;
    String operationDescription = chance > 0 ? "plus" : "take";
    operationDescription = "attribute.modifier." + operationDescription + ".1";
    String multiplierDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleChance);
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
  }
}
