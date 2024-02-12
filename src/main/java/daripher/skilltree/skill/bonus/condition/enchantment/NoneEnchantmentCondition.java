package daripher.skilltree.skill.bonus.condition.enchantment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTEnchantmentConditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public enum NoneEnchantmentCondition implements EnchantmentCondition {
  INSTANCE;

  @Override
  public boolean met(EnchantmentCategory category) {
    return true;
  }

  @Override
  public EnchantmentCondition.Serializer getSerializer() {
    return PSTEnchantmentConditions.NONE.get();
  }

  public static class Serializer implements EnchantmentCondition.Serializer {
    @Override
    public EnchantmentCondition deserialize(JsonObject json) throws JsonParseException {
      return NoneEnchantmentCondition.INSTANCE;
    }

    @Override
    public void serialize(JsonObject json, EnchantmentCondition condition) {
      if (condition != NoneEnchantmentCondition.INSTANCE) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public EnchantmentCondition deserialize(CompoundTag tag) {
      return NoneEnchantmentCondition.INSTANCE;
    }

    @Override
    public CompoundTag serialize(EnchantmentCondition condition) {
      if (condition != NoneEnchantmentCondition.INSTANCE) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public EnchantmentCondition deserialize(FriendlyByteBuf buf) {
      return NoneEnchantmentCondition.INSTANCE;
    }

    @Override
    public void serialize(FriendlyByteBuf buf, EnchantmentCondition condition) {
      if (condition != NoneEnchantmentCondition.INSTANCE) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public EnchantmentCondition createDefaultInstance() {
      return NoneEnchantmentCondition.INSTANCE;
    }
  }
}
