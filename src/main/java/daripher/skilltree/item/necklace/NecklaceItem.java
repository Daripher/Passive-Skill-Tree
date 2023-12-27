package daripher.skilltree.item.necklace;

import daripher.skilltree.init.PSTCreativeTabs;
import daripher.skilltree.item.ItemBonusProvider;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class NecklaceItem extends Item implements ICurioItem, ItemBonusProvider {
  public NecklaceItem() {
    super(new Properties().tab(PSTCreativeTabs.SKILLTREE).stacksTo(1));
  }

  @Override
  public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
    getItemBonuses().stream().map(ItemBonus::getTooltip).forEach(tooltips::add);
    return tooltips;
  }

  @Override
  public @NotNull List<ItemBonus<?>> getItemBonuses() {
    return List.of();
  }
}
