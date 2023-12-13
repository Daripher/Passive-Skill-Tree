package daripher.skilltree.skill.bonus.condition.enchantment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTEnchantmentConditions;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class NoneEnchantmentCondition implements EnchantmentCondition {
  @Override
  public boolean met(EnchantmentCategory category) {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    return o != null && getClass() == o.getClass();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSerializer());
  }

  @Override
  public EnchantmentCondition.Serializer getSerializer() {
    return PSTEnchantmentConditions.NONE.get();
  }

  public static class Serializer implements EnchantmentCondition.Serializer {
    @Override
    public EnchantmentCondition deserialize(JsonObject json) throws JsonParseException {
      return new NoneEnchantmentCondition();
    }

    @Override
    public void serialize(JsonObject json, EnchantmentCondition condition) {
      if (!(condition instanceof NoneEnchantmentCondition)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public EnchantmentCondition deserialize(CompoundTag tag) {
      return new NoneEnchantmentCondition();
    }

    @Override
    public CompoundTag serialize(EnchantmentCondition condition) {
      if (!(condition instanceof NoneEnchantmentCondition)) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public EnchantmentCondition deserialize(FriendlyByteBuf buf) {
      return new NoneEnchantmentCondition();
    }

    @Override
    public void serialize(FriendlyByteBuf buf, EnchantmentCondition condition) {
      if (!(condition instanceof NoneEnchantmentCondition)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public EnchantmentCondition createDefaultInstance() {
      return new NoneEnchantmentCondition();
    }
  }
}
