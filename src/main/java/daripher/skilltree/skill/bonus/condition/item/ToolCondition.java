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
import net.minecraft.world.item.ItemStack;

public class ToolCondition implements ItemCondition {
  private Type type;

  public ToolCondition(Type type) {
    this.type = type;
  }

  @Override
  public boolean met(ItemStack stack) {
    return switch (type) {
      case AXE -> ItemHelper.isAxe(stack);
      case HOE -> ItemHelper.isHoe(stack);
      case PICKAXE -> ItemHelper.isPickaxe(stack);
      case SHOVEL -> ItemHelper.isShovel(stack);
      case ANY -> ItemHelper.isTool(stack);
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
    ToolCondition that = (ToolCondition) o;
    return type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  @Override
  public ItemCondition.Serializer getSerializer() {
    return PSTItemConditions.TOOL.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<ItemCondition> consumer) {
    editor.addLabel(0, 0, "Tool Type", ChatFormatting.GREEN);
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

  public enum Type {
    AXE("axe"),
    SHOVEL("shovel"),
    HOE("hoe"),
    PICKAXE("pickaxe"),
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
      return new ToolCondition(SerializationHelper.deserializeToolType(json));
    }

    @Override
    public void serialize(JsonObject json, ItemCondition condition) {
      if (!(condition instanceof ToolCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeToolType(json, aCondition.type);
    }

    @Override
    public ItemCondition deserialize(CompoundTag tag) {
      return new ToolCondition(SerializationHelper.deserializeToolType(tag));
    }

    @Override
    public CompoundTag serialize(ItemCondition condition) {
      if (!(condition instanceof ToolCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeToolType(tag, aCondition.type);
      return tag;
    }

    @Override
    public ItemCondition deserialize(FriendlyByteBuf buf) {
      return new ToolCondition(NetworkHelper.readEnum(buf, Type.class));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
      if (!(condition instanceof ToolCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeEnum(buf, aCondition.type);
    }

    @Override
    public ItemCondition createDefaultInstance() {
      return new ToolCondition(Type.ANY);
    }
  }
}
