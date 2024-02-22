package daripher.skilltree.skill.bonus.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTItemBonuses;
import daripher.skilltree.network.NetworkHelper;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class QuiverCapacityBonus implements ItemBonus<QuiverCapacityBonus> {
  private float amount;
  private AttributeModifier.Operation operation;

  public QuiverCapacityBonus(float amount, AttributeModifier.Operation operation) {
    this.amount = amount;
    this.operation = operation;
  }

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
    return TooltipHelper.getSkillBonusTooltip(getDescriptionId(), amount, operation);
  }

  @Override
  public boolean isPositive() {
    return amount > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int index, Consumer<ItemBonus<?>> consumer) {
    editor.addLabel(0, 0, "Amount", ChatFormatting.GREEN);
    editor.addLabel(55, 0, "Operation", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, getAmount())
        .setNumericResponder(
            v -> {
              setAmount(v.floatValue());
              consumer.accept(this);
            });
    editor
        .addDropDownList(55, 0, 145, 14, 3, getOperation())
        .setToNameFunc(TooltipHelper::getOperationName)
        .setResponder(
            o -> {
              setOperation(o);
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  public void setAmount(float amount) {
    this.amount = amount;
  }

  public void setOperation(AttributeModifier.Operation operation) {
    this.operation = operation;
  }

  public float getAmount() {
    return amount;
  }

  public AttributeModifier.Operation getOperation() {
    return operation;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    QuiverCapacityBonus that = (QuiverCapacityBonus) obj;
    if (!Objects.equals(this.operation, that.operation)) return false;
    return this.amount == that.amount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, operation);
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

    @Override
    public ItemBonus<?> createDefaultInstance() {
      return new QuiverCapacityBonus(100f, AttributeModifier.Operation.ADDITION);
    }
  }
}
