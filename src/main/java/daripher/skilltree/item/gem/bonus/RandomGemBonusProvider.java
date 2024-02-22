package daripher.skilltree.item.gem.bonus;

import com.google.gson.*;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.entity.player.PlayerExtension;
import daripher.skilltree.init.PSTGemBonuses;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.GemItem;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import java.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class RandomGemBonusProvider implements GemBonusProvider {
  private final List<GemBonusProvider> bonuses;

  public RandomGemBonusProvider(List<GemBonusProvider> bonuses) {
    this.bonuses = bonuses;
  }

  @Nullable
  @Override
  public ItemBonus<?> getBonus(Player player, ItemStack stack) {
    int randomSeed = ((PlayerExtension) player).getGemsRandomSeed();
    Random random = new Random(randomSeed);
    GemBonusProvider provider = bonuses.get(random.nextInt(bonuses.size()));
    return provider.getBonus(player, stack);
  }

  @Override
  public boolean canApply(Player player, ItemStack itemStack) {
    int socket = ItemHelper.getFirstEmptySocket(itemStack, player);
    if (GemItem.hasGem(itemStack, socket)) return false;
    return getBonus(player, itemStack) != null;
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent tooltip = Component.translatable("gem_bonus.random");
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(true));
  }

  @Override
  public GemBonusProvider.Serializer getSerializer() {
    return PSTGemBonuses.RANDOM.get();
  }

  public static class Serializer implements GemBonusProvider.Serializer {
    @Override
    public GemBonusProvider deserialize(JsonObject json) throws JsonParseException {
      List<GemBonusProvider> bonuses =
          SerializationHelper.deserializeObjects(
              json, "bonuses", SerializationHelper::deserializeGemBonusProvider);
      return new RandomGemBonusProvider(bonuses);
    }

    @Override
    public void serialize(JsonObject json, GemBonusProvider provider) {
      if (!(provider instanceof RandomGemBonusProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      SerializationHelper.serializeObjects(
          json, "bonuses", aProvider.bonuses, SerializationHelper::serializeGemBonusProvider);
    }

    @Override
    public GemBonusProvider deserialize(CompoundTag tag) {
      List<GemBonusProvider> bonuses =
          SerializationHelper.deserializeObjects(
              tag, "bonuses", SerializationHelper::deserializeGemBonusProvider);
      return new RandomGemBonusProvider(bonuses);
    }

    @Override
    public CompoundTag serialize(GemBonusProvider provider) {
      if (!(provider instanceof RandomGemBonusProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      SerializationHelper.serializeObjects(
          tag, "bonuses", aProvider.bonuses, SerializationHelper::serializeGemBonusProvider);
      return tag;
    }

    @Override
    public GemBonusProvider deserialize(FriendlyByteBuf buf) {
      List<GemBonusProvider> bonuses = new ArrayList<>();
      int size = buf.readInt();
      for (int i = 0; i < size; i++) {
        bonuses.add(NetworkHelper.readGemBonusProvider(buf));
      }
      return new RandomGemBonusProvider(bonuses);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, GemBonusProvider provider) {
      if (!(provider instanceof RandomGemBonusProvider aProvider)) {
        throw new IllegalArgumentException();
      }
      buf.writeInt(aProvider.bonuses.size());
      aProvider.bonuses.forEach(b -> NetworkHelper.writeGemBonusProvider(buf, b));
    }
  }
}
