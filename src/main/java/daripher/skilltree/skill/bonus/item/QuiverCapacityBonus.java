package daripher.skilltree.skill.bonus.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTItemBonuses;
import daripher.skilltree.network.NetworkHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public record QuiverCapacityBonus(float amount, AttributeModifier.Operation operation)
    implements ItemBonus<QuiverCapacityBonus> {
  @Override
  public boolean canMerge(ItemBonus<?> other) {
    if (!(other instanceof QuiverCapacityBonus otherBonus)) return false;
    return operation == otherBonus.operation;
  }

  @Override
  public QuiverCapacityBonus merge(ItemBonus<?> other) {
    if (!(other instanceof QuiverCapacityBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new QuiverCapacityBonus(this.amount + otherBonus.amount, operation);
  }

  @Override
  public QuiverCapacityBonus copy() {
    return new QuiverCapacityBonus(amount, operation);
  }

  @Override
  public QuiverCapacityBonus multiply(double multiplier) {
    return new QuiverCapacityBonus((float) (amount * multiplier), operation);
  }

  @Override
  public ItemBonus.Serializer getSerializer() {
    return PSTItemBonuses.QUIVER_CAPACITY.get();
  }

  @Override
  public MutableComponent getTooltip() {
    double visibleAmount = amount;
    if (operation != AttributeModifier.Operation.ADDITION) {
      visibleAmount *= 100D;
    }
    if (amount < 0D) visibleAmount *= -1D;
    String operationDescription = amount > 0 ? "plus" : "take";
    MutableComponent bonusDescription = Component.translatable(getDescriptionId());
    String amountDescription = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleAmount);
    operationDescription = "attribute.modifier." + operationDescription + "." + operation.toValue();
    return Component.translatable(operationDescription, amountDescription, bonusDescription);
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {
    // TODO
  }

  public static class Serializer implements ItemBonus.Serializer {
    @Override
    public ItemBonus<?> deserialize(JsonObject json) throws JsonParseException {
      float amount = json.get("chance").getAsFloat();
      AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
      return new QuiverCapacityBonus(amount, operation);
    }

    @Override
    public void serialize(JsonObject json, ItemBonus<?> bonus) {
      if (!(bonus instanceof QuiverCapacityBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("chance", aBonus.amount);
      SerializationHelper.serializeOperation(json, aBonus.operation);
    }

    @Override
    public ItemBonus<?> deserialize(CompoundTag tag) {
      float amount = tag.getFloat("chance");
      AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
      return new QuiverCapacityBonus(amount, operation);
    }

    @Override
    public CompoundTag serialize(ItemBonus<?> bonus) {
      if (!(bonus instanceof QuiverCapacityBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putFloat("chance", aBonus.amount);
      SerializationHelper.serializeOperation(tag, aBonus.operation);
      return tag;
    }

    @Override
    public ItemBonus<?> deserialize(FriendlyByteBuf buf) {
      return new QuiverCapacityBonus(buf.readFloat(), NetworkHelper.readOperation(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemBonus<?> bonus) {
      if (!(bonus instanceof QuiverCapacityBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeFloat(aBonus.amount);
      NetworkHelper.writeOperation(buf, aBonus.operation);
    }
  }
}
