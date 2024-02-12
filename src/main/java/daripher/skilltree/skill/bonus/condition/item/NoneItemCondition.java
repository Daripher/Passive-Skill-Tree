package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTItemConditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public enum NoneItemCondition implements ItemCondition {
  INSTANCE;

  @Override
  public boolean met(ItemStack stack) {
    return true;
  }

  @Override
  public ItemCondition.Serializer getSerializer() {
    return PSTItemConditions.NONE.get();
  }

  public static class Serializer implements ItemCondition.Serializer {
    @Override
    public ItemCondition deserialize(JsonObject json) throws JsonParseException {
      return NoneItemCondition.INSTANCE;
    }

    @Override
    public void serialize(JsonObject json, ItemCondition condition) {
      if (condition != NoneItemCondition.INSTANCE) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public ItemCondition deserialize(CompoundTag tag) {
      return NoneItemCondition.INSTANCE;
    }

    @Override
    public CompoundTag serialize(ItemCondition condition) {
      if (condition != NoneItemCondition.INSTANCE) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public ItemCondition deserialize(FriendlyByteBuf buf) {
      return NoneItemCondition.INSTANCE;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
      if (condition != NoneItemCondition.INSTANCE) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public ItemCondition createDefaultInstance() {
      return NoneItemCondition.INSTANCE;
    }
  }
}
