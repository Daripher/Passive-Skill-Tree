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
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ArmorCondition implements ItemCondition {
  private Type type;

  public ArmorCondition(Type type) {
    this.type = type;
  }

  @Override
  public boolean met(ItemStack stack) {
    if (!ItemHelper.isArmor(stack) && !ItemHelper.isShield(stack)) return false;
    final EquipmentSlot slot = Player.getEquipmentSlotForItem(stack);
    return switch (type) {
      case ANY -> true;
      case BOOTS -> slot == EquipmentSlot.FEET;
      case HELMET -> slot == EquipmentSlot.HEAD;
      case SHIELD -> slot == EquipmentSlot.OFFHAND;
      case LEGGINGS -> slot == EquipmentSlot.LEGS;
      case CHESTPLATE -> slot == EquipmentSlot.CHEST;
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
    ArmorCondition that = (ArmorCondition) o;
    return type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  @Override
  public ItemCondition.Serializer getSerializer() {
    return PSTItemConditions.ARMOR.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<ItemCondition> consumer) {
    editor.addLabel(0, 0, "Armor Type", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 6, type)
        .setToNameFunc(ArmorCondition.Type::getFormattedName)
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

  public Type getType() {
    return type;
  }

  public enum Type {
    HELMET("helmet"),
    CHESTPLATE("chestplate"),
    LEGGINGS("leggings"),
    BOOTS("boots"),
    SHIELD("shield"),
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

    public static ArmorCondition.Type byName(String name) {
      for (ArmorCondition.Type type : values()) {
        if (type.name.equals(name)) return type;
      }
      return ANY;
    }
  }

  public static class Serializer implements ItemCondition.Serializer {
    @Override
    public ItemCondition deserialize(JsonObject json) throws JsonParseException {
      return new ArmorCondition(SerializationHelper.deserializeArmorType(json));
    }

    @Override
    public void serialize(JsonObject json, ItemCondition condition) {
      if (!(condition instanceof ArmorCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeArmorType(json, aCondition.type);
    }

    @Override
    public ItemCondition deserialize(CompoundTag tag) {
      return new ArmorCondition(SerializationHelper.deserializeArmorType(tag));
    }

    @Override
    public CompoundTag serialize(ItemCondition condition) {
      if (!(condition instanceof ArmorCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeArmorType(tag, aCondition.type);
      return tag;
    }

    @Override
    public ItemCondition deserialize(FriendlyByteBuf buf) {
      return new ArmorCondition(NetworkHelper.readEnum(buf, Type.class));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
      if (!(condition instanceof ArmorCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeEnum(buf, aCondition.type);
    }

    @Override
    public ItemCondition createDefaultInstance() {
      return new ArmorCondition(Type.ANY);
    }
  }
}
