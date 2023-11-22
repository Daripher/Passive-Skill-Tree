package daripher.skilltree.skill.bonus.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.init.PSTItemBonuses;
import daripher.skilltree.item.ItemHelper;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public record PotionAmplificationBonus(float chance)
    implements ItemBonus<PotionAmplificationBonus> {
  @Override
  public void itemCrafted(ItemStack stack) {
    if (chance % 1 != 0) {
      ItemHelper.removeItemBonus(stack, this);
      Random random = new Random();
      if (random.nextFloat() < chance % 1) {
        ItemHelper.addItemBonus(stack, new PotionAmplificationBonus(1 + (int) (chance)));
      } else {
        ItemHelper.addItemBonus(stack, new PotionAmplificationBonus((int) (chance)));
      }
    }
  }

  @Override
  public boolean canMerge(ItemBonus<?> other) {
    if (!(other instanceof PotionAmplificationBonus otherBonus)) return false;
    return chance == otherBonus.chance;
  }

  @Override
  public PotionAmplificationBonus merge(ItemBonus<?> other) {
    if (!(other instanceof PotionAmplificationBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new PotionAmplificationBonus(this.chance + otherBonus.chance);
  }

  @Override
  public PotionAmplificationBonus copy() {
    return new PotionAmplificationBonus(chance);
  }

  @Override
  public PotionAmplificationBonus multiply(double multiplier) {
    return new PotionAmplificationBonus((float) (multiplier * multiplier));
  }

  @Override
  public ItemBonus.Serializer getSerializer() {
    return PSTItemBonuses.POTION_AMPLIFICATION.get();
  }

  @Override
  public MutableComponent getTooltip() {
    double visibleAmount = chance * 100;
    if (chance < 0D) visibleAmount *= -1D;
    String operationDescription = chance > 0 ? "plus" : "take";
    MutableComponent bonusDescription = Component.translatable(getDescriptionId());
    String amountDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleAmount);
    operationDescription = "attribute.modifier." + operationDescription + ".1";
    return Component.translatable(operationDescription, amountDescription, bonusDescription);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    // TODO
  }

  public static class Serializer implements ItemBonus.Serializer {
    @Override
    public ItemBonus<?> deserialize(JsonObject json) throws JsonParseException {
      float chance = json.get("chance").getAsFloat();
      return new PotionAmplificationBonus(chance);
    }

    @Override
    public void serialize(JsonObject json, ItemBonus<?> bonus) {
      if (!(bonus instanceof PotionAmplificationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.chance);
    }

    @Override
    public ItemBonus<?> deserialize(CompoundTag tag) {
      float chance = tag.getFloat("chance");
      return new PotionAmplificationBonus(chance);
    }

    @Override
    public CompoundTag serialize(ItemBonus<?> bonus) {
      if (!(bonus instanceof PotionAmplificationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.chance);
      return tag;
    }

    @Override
    public ItemBonus<?> deserialize(FriendlyByteBuf buf) {
      return new PotionAmplificationBonus(buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemBonus<?> bonus) {
      if (!(bonus instanceof PotionAmplificationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.chance);
    }
  }
}
