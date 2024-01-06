package daripher.skilltree.item.gem.bonus;

import daripher.skilltree.item.gem.GemItem;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface GemBonusProvider {
  @Nullable
  ItemBonus<?> getBonus(Player player, ItemStack stack);

  boolean canApply(Player player, ItemStack itemStack);

  default void addGemBonus(Player player, ItemStack itemStack, ItemStack gemStack) {
    ItemBonus<?> bonus = getBonus(player, itemStack);
    if (bonus != null) {
      GemItem.addGemBonus(player, itemStack, gemStack, bonus);
    }
  }

  MutableComponent getTooltip();

  Serializer getSerializer();

  interface Serializer extends daripher.skilltree.data.serializers.Serializer<GemBonusProvider> {}
}
