package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.network.NetworkHelper;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;

public record PotionCondition(@Nullable MobEffectCategory category)
    implements ItemCondition {
  @Override
  public boolean met(ItemStack stack) {
    if (!(stack.getItem() instanceof PotionItem)) return false;
    return category == null || hasEffects(stack, category);
  }

  private boolean hasEffects(ItemStack stack, MobEffectCategory category) {
    return PotionUtils.getAllEffects(stack.getOrCreateTag()).stream()
        .map(MobEffectInstance::getEffect)
        .anyMatch(effect -> effect.getCategory() == category);
  }

  @Override
  public String getDescriptionId() {
    return "%s.%s"
        .formatted(ItemCondition.super.getDescriptionId(), SerializationHelper.getName(category));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PotionCondition that = (PotionCondition) o;
    return category == that.category;
  }

  @Override
  public int hashCode() {
    return Objects.hash(category);
  }

  @Override
  public ItemCondition.Serializer getSerializer() {
    return PSTItemConditions.POTION.get();
  }

  public static class Serializer implements ItemCondition.Serializer {
    @Override
    public ItemCondition deserialize(JsonObject json) throws JsonParseException {
      return new PotionCondition(SerializationHelper.deserializePotionCategory(json));
    }

    @Override
    public void serialize(JsonObject json, ItemCondition condition) {
      if (!(condition instanceof PotionCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializePotionCategory(json, aCondition.category);
    }

    @Override
    public ItemCondition deserialize(CompoundTag tag) {
      return new PotionCondition(SerializationHelper.deserializePotionCategory(tag));
    }

    @Override
    public CompoundTag serialize(ItemCondition condition) {
      if (!(condition instanceof PotionCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializePotionCategory(tag, aCondition.category);
      return tag;
    }

    @Override
    public ItemCondition deserialize(FriendlyByteBuf buf) {
      return new PotionCondition(
          NetworkHelper.readNullableEnum(buf, MobEffectCategory.class));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
      if (!(condition instanceof PotionCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeNullableEnum(buf, aCondition.category);
    }
  }
}
