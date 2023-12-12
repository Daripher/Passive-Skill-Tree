package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.network.NetworkHelper;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;

public final class PotionCondition implements ItemCondition {
  private Type type;

  public PotionCondition(Type type) {
    this.type = type;
  }

  @Override
  public boolean met(ItemStack stack) {
    if (!(stack.getItem() instanceof PotionItem)) return false;
    return switch (type) {
      case ANY -> true;
      case NEUTRAL -> hasEffects(stack, MobEffectCategory.NEUTRAL);
      case HARMFUL -> hasEffects(stack, MobEffectCategory.HARMFUL);
      case BENEFICIAL -> hasEffects(stack, MobEffectCategory.BENEFICIAL);
    };
  }

  private boolean hasEffects(ItemStack stack, MobEffectCategory category) {
    return PotionUtils.getAllEffects(stack.getOrCreateTag()).stream()
        .map(MobEffectInstance::getEffect)
        .anyMatch(effect -> effect.getCategory() == category);
  }

  @Override
  public String getDescriptionId() {
    return "%s.%s".formatted(ItemCondition.super.getDescriptionId(), type.getName());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PotionCondition that = (PotionCondition) o;
    return type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  public enum Type {
    HARMFUL("harmful"),
    NEUTRAL("neutral"),
    BENEFICIAL("beneficial"),
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

  @Override
  public ItemCondition.Serializer getSerializer() {
    return PSTItemConditions.POTIONS.get();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<ItemCondition> consumer) {
    editor.addLabel(0, 0, "Type", ChatFormatting.GREEN);
    editor.shiftWidgets(0, 19);
    editor
        .addDropDownList(0, 0, 200, 14, 4, type)
        .setToNameFunc(PotionCondition.Type::getFormattedName)
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

  public static class Serializer implements ItemCondition.Serializer {
    @Override
    public ItemCondition deserialize(JsonObject json) throws JsonParseException {
      return new PotionCondition(SerializationHelper.deserializePotionType(json));
    }

    @Override
    public void serialize(JsonObject json, ItemCondition condition) {
      if (!(condition instanceof PotionCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializePotionType(json, aCondition.type);
    }

    @Override
    public ItemCondition deserialize(CompoundTag tag) {
      return new PotionCondition(SerializationHelper.deserializePotionType(tag));
    }

    @Override
    public CompoundTag serialize(ItemCondition condition) {
      if (!(condition instanceof PotionCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializePotionType(tag, aCondition.type);
      return tag;
    }

    @Override
    public ItemCondition deserialize(FriendlyByteBuf buf) {
      return new PotionCondition(NetworkHelper.readEnum(buf, Type.class));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
      if (!(condition instanceof PotionCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeEnum(buf, aCondition.type);
    }

    @Override
    public ItemCondition createDefaultInstance() {
      return new PotionCondition(Type.ANY);
    }
  }
}
