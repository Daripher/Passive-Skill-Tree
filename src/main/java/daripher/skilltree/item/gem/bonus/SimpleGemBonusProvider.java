package daripher.skilltree.item.gem.bonus;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.SerializationHelper;
import daripher.skilltree.init.PSTGemBonuses;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.GemItem;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SimpleGemBonusProvider implements GemBonusProvider {
  private final ItemBonus<?> bonus;

  public SimpleGemBonusProvider(ItemBonus<?> bonus) {
    this.bonus = bonus;
  }

  @Nullable
  @Override
  public ItemBonus<?> getBonus(Player player, ItemStack stack) {
    return bonus;
  }

  @Override
  public boolean canApply(Player player, ItemStack itemStack) {
    int socket = ItemHelper.getFirstEmptySocket(itemStack, player);
    if (GemItem.hasGem(itemStack, socket)) return false;
    return getBonus(player, itemStack) != null;
  }

  @Override
  public MutableComponent getTooltip() {
    return bonus.getTooltip().withStyle(TooltipHelper.getSkillBonusStyle(bonus.isPositive()));
  }

  @Override
  public GemBonusProvider.Serializer getSerializer() {
    return PSTGemBonuses.SIMPLE.get();
  }

  public static class Serializer implements GemBonusProvider.Serializer {
    @Override
    public GemBonusProvider deserialize(JsonObject json) throws JsonParseException {
      return new SimpleGemBonusProvider(SerializationHelper.deserializeItemBonus(json));
    }

    @Override
    public void serialize(JsonObject json, GemBonusProvider provider) {
      if (!(provider instanceof SimpleGemBonusProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeItemBonus(json, aProvider.bonus);
    }

    @Override
    public GemBonusProvider deserialize(CompoundTag tag) {
      return new SimpleGemBonusProvider(SerializationHelper.deserializeItemBonus(tag));
    }

    @Override
    public CompoundTag serialize(GemBonusProvider provider) {
      if (!(provider instanceof SimpleGemBonusProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeItemBonus(tag, aProvider.bonus);
      return tag;
    }

    @Override
    public GemBonusProvider deserialize(FriendlyByteBuf buf) {
      return new SimpleGemBonusProvider(NetworkHelper.readItemBonus(buf));
    }

    @Override
    public void serialize(FriendlyByteBuf buf, GemBonusProvider provider) {
      if (!(provider instanceof SimpleGemBonusProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      NetworkHelper.writeItemBonus(buf, aProvider.bonus);
    }
  }
}
