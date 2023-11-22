package daripher.skilltree.skill.bonus.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.init.PSTItemBonuses;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public record FoodHealingBonus(float amount) implements ItemBonus<FoodHealingBonus> {
  @Override
  public boolean canMerge(ItemBonus<?> other) {
    return other instanceof FoodHealingBonus;
  }

  @Override
  public FoodHealingBonus merge(ItemBonus<?> other) {
    if (!(other instanceof FoodHealingBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new FoodHealingBonus(this.amount + otherBonus.amount);
  }

  @Override
  public FoodHealingBonus copy() {
    return new FoodHealingBonus(amount);
  }

  @Override
  public FoodHealingBonus multiply(double multiplier) {
    return new FoodHealingBonus((float) (multiplier * multiplier));
  }

  @Override
  public ItemBonus.Serializer getSerializer() {
    return PSTItemBonuses.FOOD_HEALING.get();
  }

  @Override
  public MutableComponent getTooltip() {
    double visibleAmount = amount;
    if (amount < 0) visibleAmount *= -1;
    String amountDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleAmount);
    return Component.translatable(getDescriptionId(), amountDescription);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    // TODO
  }

  public static class Serializer implements ItemBonus.Serializer {
    @Override
    public ItemBonus<?> deserialize(JsonObject json) throws JsonParseException {
      float multiplier = json.get("amount").getAsFloat();
      return new FoodHealingBonus(multiplier);
    }

    @Override
    public void serialize(JsonObject json, ItemBonus<?> bonus) {
      if (!(bonus instanceof FoodHealingBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("amount", aBonus.amount);
    }

    @Override
    public ItemBonus<?> deserialize(CompoundTag tag) {
      float multiplier = tag.getFloat("amount");
      return new FoodHealingBonus(multiplier);
    }

    @Override
    public CompoundTag serialize(ItemBonus<?> bonus) {
      if (!(bonus instanceof FoodHealingBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("amount", aBonus.amount);
      return tag;
    }

    @Override
    public ItemBonus<?> deserialize(FriendlyByteBuf buf) {
      return new FoodHealingBonus(buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemBonus<?> bonus) {
      if (!(bonus instanceof FoodHealingBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.amount);
    }
  }
}
