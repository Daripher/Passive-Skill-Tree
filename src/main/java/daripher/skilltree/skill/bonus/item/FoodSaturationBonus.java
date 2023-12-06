package daripher.skilltree.skill.bonus.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.init.PSTItemBonuses;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public final class FoodSaturationBonus implements ItemBonus<FoodSaturationBonus> {
  private float multiplier;

  public FoodSaturationBonus(float multiplier) {
    this.multiplier = multiplier;
  }

  @Override
  public boolean canMerge(ItemBonus<?> other) {
    return other instanceof FoodSaturationBonus;
  }

  @Override
  public FoodSaturationBonus merge(ItemBonus<?> other) {
    if (!(other instanceof FoodSaturationBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new FoodSaturationBonus(this.multiplier + otherBonus.multiplier);
  }

  @Override
  public FoodSaturationBonus copy() {
    return new FoodSaturationBonus(multiplier);
  }

  @Override
  public FoodSaturationBonus multiply(double multiplier) {
    return new FoodSaturationBonus((float) (multiplier * multiplier));
  }

  @Override
  public ItemBonus.Serializer getSerializer() {
    return PSTItemBonuses.FOOD_SATURATION.get();
  }

  @Override
  public MutableComponent getTooltip() {
    double visibleAmount = multiplier * 100;
    if (multiplier < 0) visibleAmount *= -1;
    String operationDescription = multiplier > 0 ? "plus" : "take";
    MutableComponent bonusDescription = Component.translatable(getDescriptionId());
    String amountDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleAmount);
    operationDescription = "attribute.modifier." + operationDescription + ".1";
    return Component.translatable(operationDescription, amountDescription, bonusDescription);
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int index, Consumer<ItemBonus<?>> consumer) {
    editor.addLabel(0, 0, "Multiplier", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 90, 14, getMultiplier())
        .setNumericResponder(
            v -> {
              setMultiplier(v.floatValue());
              consumer.accept(this);
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
    FoodSaturationBonus that = (FoodSaturationBonus) obj;
    return Float.floatToIntBits(this.multiplier) == Float.floatToIntBits(that.multiplier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(multiplier);
  }

  public static class Serializer implements ItemBonus.Serializer {
    @Override
    public ItemBonus<?> deserialize(JsonObject json) throws JsonParseException {
      float multiplier = json.get("multiplier").getAsFloat();
      return new FoodSaturationBonus(multiplier);
    }

    @Override
    public void serialize(JsonObject json, ItemBonus<?> bonus) {
      if (!(bonus instanceof FoodSaturationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("multiplier", aBonus.multiplier);
    }

    @Override
    public ItemBonus<?> deserialize(CompoundTag tag) {
      float multiplier = tag.getFloat("multiplier");
      return new FoodSaturationBonus(multiplier);
    }

    @Override
    public CompoundTag serialize(ItemBonus<?> bonus) {
      if (!(bonus instanceof FoodSaturationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("multiplier", aBonus.multiplier);
      return tag;
    }

    @Override
    public ItemBonus<?> deserialize(FriendlyByteBuf buf) {
      return new FoodSaturationBonus(buf.readFloat());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemBonus<?> bonus) {
      if (!(bonus instanceof FoodSaturationBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.multiplier);
    }

    @Override
    public ItemBonus<?> createDefaultInstance() {
      return new FoodSaturationBonus(0.1f);
    }
  }
}
