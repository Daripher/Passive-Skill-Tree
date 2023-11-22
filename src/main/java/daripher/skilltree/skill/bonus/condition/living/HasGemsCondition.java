package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.item.gem.GemHelper;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.util.PlayerHelper;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record HasGemsCondition(int min, int max, ItemCondition itemCondition)
    implements LivingCondition {
  @Override
  public boolean met(LivingEntity living) {
    int gems = getGems(PlayerHelper.getAllEquipment(living).filter(itemCondition::met));
    if (min == -1) {
      return gems <= max;
    }
    if (max == -1) {
      return gems >= min;
    }
    return gems <= max && gems >= min;
  }

  private int getGems(Stream<ItemStack> items) {
    return items.map(GemHelper::getGemsCount).reduce(Integer::sum).orElse(0);
  }

  @Override
  public MutableComponent getTooltip(MutableComponent bonusTooltip, String target) {
    String key = getDescriptionId();
    MutableComponent targetDescription =
        Component.translatable("%s.target.%s".formatted(key, target));
    MutableComponent itemDescription =
        TooltipHelper.getOptionalTooltip(itemCondition.getDescriptionId(), "contains");
    if (min == -1) {
      return Component.translatable(
          key + ".max", bonusTooltip, targetDescription, max, itemDescription);
    }
    if (max == -1) {
      if (min == 1)
        return Component.translatable(
            key + ".min.1", bonusTooltip, targetDescription, itemDescription);
      return Component.translatable(
          key + ".min", bonusTooltip, targetDescription, min, itemDescription);
    }
    return Component.translatable(
        key + ".range", bonusTooltip, targetDescription, min, max, itemDescription);
  }

  @Override
  public LivingCondition.Serializer getSerializer() {
    return PSTLivingConditions.HAS_GEMS.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HasGemsCondition that = (HasGemsCondition) o;
    return min == that.min && max == that.max && Objects.equals(itemCondition, that.itemCondition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, max, itemCondition);
  }

  public static class Serializer implements LivingCondition.Serializer {
    @Override
    public LivingCondition deserialize(JsonObject json) throws JsonParseException {
      int min = json.has("min") ? json.get("min").getAsInt() : -1;
      int max = json.has("max") ? json.get("max").getAsInt() : -1;
      ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(json);
      return new HasGemsCondition(min, max, itemCondition);
    }

    @Override
    public void serialize(JsonObject json, LivingCondition condition) {
      if (!(condition instanceof HasGemsCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      if (aCondition.min != -1) {
        json.addProperty("min", aCondition.min);
      }
      if (aCondition.max != -1) {
        json.addProperty("max", aCondition.max);
      }
      SerializationHelper.serializeItemCondition(json, aCondition.itemCondition);
    }

    @Override
    public LivingCondition deserialize(CompoundTag tag) {
      int min = tag.contains("Min") ? tag.getInt("Min") : -1;
      int max = tag.contains("Max") ? tag.getInt("Max") : -1;
      ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(tag);
      return new HasGemsCondition(min, max, itemCondition);
    }

    @Override
    public CompoundTag serialize(LivingCondition condition) {
      if (!(condition instanceof HasGemsCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      if (aCondition.min != -1) {
        tag.putInt("Min", aCondition.min);
      }
      if (aCondition.max != -1) {
        tag.putInt("Max", aCondition.max);
      }
      SerializationHelper.serializeItemCondition(tag, aCondition.itemCondition);
      return tag;
    }

    @Override
    public LivingCondition deserialize(FriendlyByteBuf buf) {
      return new HasGemsCondition(
          buf.readInt(), buf.readInt(), NetworkHelper.readItemCondition(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
      if (!(condition instanceof HasGemsCondition aCondition)) {
        throw new IllegalArgumentException();
      }
      buf.writeInt(aCondition.min);
      buf.writeInt(aCondition.max);
      NetworkHelper.writeItemCondition(buf, aCondition.itemCondition);
    }
  }
}
