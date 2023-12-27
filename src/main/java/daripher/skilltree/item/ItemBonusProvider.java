package daripher.skilltree.item;

import daripher.skilltree.skill.bonus.item.ItemBonus;
import java.util.List;
import javax.annotation.Nonnull;

public interface ItemBonusProvider {
  @Nonnull
  List<ItemBonus<?>> getItemBonuses();
}
