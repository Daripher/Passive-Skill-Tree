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

public final class ItemSocketsBonus implements ItemBonus<ItemSocketsBonus> {
  private int amount;

  public ItemSocketsBonus(int amount) {
    this.amount = amount;
  }

  @Override
  public boolean canMerge(ItemBonus<?> other) {
    return other instanceof ItemSocketsBonus;
  }

  @Override
  public ItemSocketsBonus merge(ItemBonus<?> other) {
    if (!(other instanceof ItemSocketsBonus otherBonus)) {
      throw new IllegalArgumentException();
    }
    return new ItemSocketsBonus(this.amount + otherBonus.amount);
  }

  @Override
  public ItemSocketsBonus copy() {
    return new ItemSocketsBonus(amount);
  }

  @Override
  public ItemSocketsBonus multiply(double multiplier) {
    return new ItemSocketsBonus((int) (amount * multiplier));
  }

  @Override
  public ItemBonus.Serializer getSerializer() {
    return PSTItemBonuses.SOCKETS.get();
  }

  @Override
  public MutableComponent getTooltip() {
    return Component.translatable(getDescriptionId(), amount);
  }

  @Override
  public boolean isPositive() {
    return amount > 0;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int index, Consumer<ItemBonus<?>> consumer) {
    editor.addLabel(0, 0, "Amount", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addNumericTextField(0, 0, 50, 14, getAmount())
        .setNumericResponder(
            v -> {
              setAmount(v.intValue());
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public int getAmount() {
    return amount;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    ItemSocketsBonus that = (ItemSocketsBonus) obj;
    return this.amount == that.amount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount);
  }

  public static class Serializer implements ItemBonus.Serializer {
    @Override
    public ItemBonus<?> deserialize(JsonObject json) throws JsonParseException {
      return new ItemSocketsBonus(json.get("amount").getAsInt());
    }

    @Override
    public void serialize(JsonObject json, ItemBonus<?> bonus) {
      if (!(bonus instanceof ItemSocketsBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("amount", aBonus.amount);
    }

    @Override
    public ItemBonus<?> deserialize(CompoundTag tag) {
      return new ItemSocketsBonus(tag.getInt("amount"));
    }

    @Override
    public CompoundTag serialize(ItemBonus<?> bonus) {
      if (!(bonus instanceof ItemSocketsBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putInt("amount", aBonus.amount);
      return tag;
    }

    @Override
    public ItemBonus<?> deserialize(FriendlyByteBuf buf) {
      return new ItemSocketsBonus(buf.readInt());
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemBonus<?> bonus) {
      if (!(bonus instanceof ItemSocketsBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeInt(aBonus.amount);
    }

    @Override
    public ItemBonus<?> createDefaultInstance() {
      return new ItemSocketsBonus(1);
    }
  }
}
