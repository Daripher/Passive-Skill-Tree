package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.network.NetworkHelper;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record WeaponCondition(@Nullable Type type) implements ItemCondition {
  @Override
  public boolean met(ItemStack stack) {
    if (!ItemHelper.isWeapon(stack)) return false;
    if (type == null) return true;
    return switch (type) {
      case MELEE -> ItemHelper.isMeleeWeapon(stack);
      case RANGED -> ItemHelper.isRangedWeapon(stack);
    };
  }

  @Override
  public String getDescriptionId() {
    return "%s.%s"
        .formatted(ItemCondition.super.getDescriptionId(), type == null ? "any" : type.getName());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WeaponCondition that = (WeaponCondition) o;
    return type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  @Override
  public ItemCondition.Serializer getSerializer() {
    return PSTItemConditions.WEAPON.get();
  }

  public enum Type {
    RANGED("ranged"),
    MELEE("melee");

    final String name;

    Type(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public static Type byName(String name) {
      for (Type type : values()) {
        if (type.name.equals(name)) return type;
      }
      return null;
    }
  }

  public static class Serializer implements ItemCondition.Serializer {
    @Override
    public ItemCondition deserialize(JsonObject json) throws JsonParseException {
      return new WeaponCondition(SerializationHelper.deserializeWeaponType(json));
    }

    @Override
    public void serialize(JsonObject json, ItemCondition condition) {
      if (!(condition instanceof WeaponCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeWeaponType(json, aCondition.type);
    }

    @Override
    public ItemCondition deserialize(CompoundTag tag) {
      return new WeaponCondition(SerializationHelper.deserializeWeaponType(tag));
    }

    @Override
    public CompoundTag serialize(ItemCondition condition) {
      if (!(condition instanceof WeaponCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeWeaponType(tag, aCondition.type);
      return tag;
    }

    @Override
    public ItemCondition deserialize(FriendlyByteBuf buf) {
      return new WeaponCondition(NetworkHelper.readNullableEnum(buf, Type.class));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
      if (!(condition instanceof WeaponCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeNullableEnum(buf, aCondition.type);
    }
  }
}
