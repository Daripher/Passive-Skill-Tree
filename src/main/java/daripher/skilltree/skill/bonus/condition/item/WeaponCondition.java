package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.network.NetworkHelper;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class WeaponCondition implements ItemCondition {
  private Type type;

  public WeaponCondition(Type type) {
    this.type = type;
  }

  @Override
  public boolean met(ItemStack stack) {
    return switch (type) {
      case MELEE -> ItemHelper.isMeleeWeapon(stack);
      case AXE -> ItemHelper.isAxe(stack);
      case SWORD -> ItemHelper.isSword(stack);
      case TRIDENT -> ItemHelper.isTrident(stack);
      case RANGED -> ItemHelper.isRangedWeapon(stack);
      case BOW -> ItemHelper.isBow(stack);
      case CROSSBOW -> ItemHelper.isCrossbow(stack);
      case ANY -> ItemHelper.isWeapon(stack);
    };
  }

  @Override
  public String getDescriptionId() {
    return "%s.%s".formatted(ItemCondition.super.getDescriptionId(), type.getName());
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

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<ItemCondition> consumer) {
    editor.addLabel(0, 0, "Weapon Type", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 8, type)
        .setToNameFunc(Type::getFormattedName)
        .setResponder(
            t -> {
              setType(t);
              consumer.accept(this);
            });
    editor.shiftWidgets(0, 19);
  }

  public void setType(Type type) {
    this.type = type;
  }

  @Nullable
  public Type getType() {
    return type;
  }

  public enum Type {
    MELEE("melee"),
    SWORD("sword"),
    AXE("axe"),
    TRIDENT("trident"),
    RANGED("ranged"),
    BOW("bow"),
    CROSSBOW("crossbow"),
    ANY("any");

    final String name;

    Type(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public Component getFormattedName() {
      return Component.literal(getName().substring(0, 1).toUpperCase() + getName().substring(1));
    }

    public static Type byName(String name) {
      for (Type type : values()) {
        if (type.name.equals(name)) return type;
      }
      return ANY;
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
      return new WeaponCondition(NetworkHelper.readEnum(buf, Type.class));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
      if (!(condition instanceof WeaponCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeEnum(buf, aCondition.type);
    }

    @Override
    public ItemCondition createDefaultInstance() {
      return new WeaponCondition(Type.ANY);
    }
  }
}
