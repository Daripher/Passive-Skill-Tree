package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.network.NetworkHelper;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public record ArmorCondition(@Nullable EquipmentSlot slot) implements ItemCondition {
  @Override
  public boolean met(ItemStack stack) {
    if (!ItemHelper.isArmor(stack) && !ItemHelper.isShield(stack)) return false;
    return slot == null || Player.getEquipmentSlotForItem(stack) == slot;
  }

  @Override
  public String getDescriptionId() {
    return "%s.%s"
        .formatted(ItemCondition.super.getDescriptionId(), slot == null ? "any" : slot.getName());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ArmorCondition that = (ArmorCondition) o;
    return slot == that.slot;
  }

  @Override
  public int hashCode() {
    return Objects.hash(slot);
  }

  @Override
  public ItemCondition.Serializer getSerializer() {
    return PSTItemConditions.ARMOR.get();
  }

  public static class Serializer implements ItemCondition.Serializer {
    @Override
    public ItemCondition deserialize(JsonObject json) throws JsonParseException {
      return new ArmorCondition(SerializationHelper.deserializeSlot(json));
    }

    @Override
    public void serialize(JsonObject json, ItemCondition condition) {
      if (!(condition instanceof ArmorCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeSlot(json, aCondition.slot);
    }

    @Override
    public ItemCondition deserialize(CompoundTag tag) {
      return new ArmorCondition(SerializationHelper.deserializeSlot(tag));
    }

    @Override
    public CompoundTag serialize(ItemCondition condition) {
      if (!(condition instanceof ArmorCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeSlot(tag, aCondition.slot);
      return tag;
    }

    @Override
    public ItemCondition deserialize(FriendlyByteBuf buf) {
      return new ArmorCondition(NetworkHelper.readNullableEnum(buf, EquipmentSlot.class));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
      if (!(condition instanceof ArmorCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeNullableEnum(buf, aCondition.slot);
    }
  }
}
