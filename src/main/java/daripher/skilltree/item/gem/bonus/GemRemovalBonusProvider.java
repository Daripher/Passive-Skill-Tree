package daripher.skilltree.item.gem.bonus;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTGemBonuses;
import daripher.skilltree.item.gem.GemItem;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class GemRemovalBonusProvider implements GemBonusProvider {
  public GemRemovalBonusProvider() {}

  @Nullable
  @Override
  public ItemBonus<?> getBonus(Player player, ItemStack stack) {
    return null;
  }

  @Override
  public boolean canApply(Player player, ItemStack itemStack) {
    return GemItem.hasGem(itemStack, 0);
  }

  @Override
  public void addGemBonus(Player player, ItemStack itemStack, ItemStack gemStack) {
    GemItem.removeGemBonuses(itemStack);
  }

  @Override
  public MutableComponent getTooltip() {
    MutableComponent tooltip = Component.translatable("gem_bonus.removal");
    return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(true));
  }

  @Override
  public GemBonusProvider.Serializer getSerializer() {
    return PSTGemBonuses.GEM_REMOVAL.get();
  }

  public static class Serializer implements GemBonusProvider.Serializer {
    @Override
    public GemBonusProvider deserialize(JsonObject json) throws JsonParseException {
      return new GemRemovalBonusProvider();
    }

    @Override
    public void serialize(JsonObject json, GemBonusProvider provider) {
      if (!(provider instanceof GemRemovalBonusProvider)) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public GemBonusProvider deserialize(CompoundTag tag) {
      return new GemRemovalBonusProvider();
    }

    @Override
    public CompoundTag serialize(GemBonusProvider provider) {
      if (!(provider instanceof GemRemovalBonusProvider)) {
        throw new IllegalArgumentException();
      }
      return new CompoundTag();
    }

    @Override
    public GemBonusProvider deserialize(FriendlyByteBuf buf) {
      return new GemRemovalBonusProvider();
    }

    @Override
    public void serialize(FriendlyByteBuf buf, GemBonusProvider provider) {
      if (!(provider instanceof GemRemovalBonusProvider)) {
        throw new IllegalArgumentException();
      }
    }
  }
}
