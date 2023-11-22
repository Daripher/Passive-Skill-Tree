package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonusMultipliers;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.util.PlayerHelper;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public record EnchantLevelsAmountMultiplier(ItemCondition itemCondition)
    implements SkillBonusMultiplier {
  @Override
  public float getValue(Player player) {
    return getEnchantLevels(PlayerHelper.getAllEquipment(player).filter(itemCondition::met));
  }

  private int getEnchantLevels(Stream<ItemStack> items) {
    return items
        .map(EnchantmentHelper::getEnchantments)
        .mapToInt(m -> m.values().stream().reduce(Integer::sum).orElse(0))
        .reduce(Integer::sum)
        .orElse(0);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip) {
    MutableComponent itemDescription =
        TooltipHelper.getOptionalTooltip(itemCondition.getDescriptionId(), "contains");
    return Component.translatable(getDescriptionId(), bonusTooltip, itemDescription);
  }

  @Override
  public SkillBonusMultiplier.Serializer getSerializer() {
    return PSTSkillBonusMultipliers.ENCHANTS_LEVELS.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EnchantLevelsAmountMultiplier that = (EnchantLevelsAmountMultiplier) o;
    return Objects.equals(itemCondition, that.itemCondition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemCondition);
  }

  public static class Serializer implements SkillBonusMultiplier.Serializer {
    @Override
    public SkillBonusMultiplier deserialize(JsonObject json) throws JsonParseException {
      ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(json);
      return new EnchantLevelsAmountMultiplier(itemCondition);
    }

    @Override
    public void serialize(JsonObject json, SkillBonusMultiplier multiplier) {
      if (!(multiplier instanceof EnchantLevelsAmountMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemCondition(json, aMultiplier.itemCondition);
    }

    @Override
    public SkillBonusMultiplier deserialize(CompoundTag tag) {
      ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(tag);
      return new EnchantLevelsAmountMultiplier(itemCondition);
    }

    @Override
    public CompoundTag serialize(SkillBonusMultiplier multiplier) {
      if (!(multiplier instanceof EnchantLevelsAmountMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemCondition(tag, aMultiplier.itemCondition);
      return tag;
    }

    @Override
    public SkillBonusMultiplier deserialize(FriendlyByteBuf buf) {
      ItemCondition itemCondition = NetworkHelper.readItemCondition(buf);
      return new EnchantLevelsAmountMultiplier(itemCondition);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonusMultiplier multiplier) {
      if (!(multiplier instanceof EnchantLevelsAmountMultiplier aMultiplier)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemCondition(buf, aMultiplier.itemCondition);
    }
  }
}
