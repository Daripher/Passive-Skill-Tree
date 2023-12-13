package daripher.skilltree.item;

import daripher.skilltree.skill.bonus.item.ItemBonus;
import java.util.function.Consumer;

public interface ItemBonusProvider {
  void getItemBonuses(Consumer<ItemBonus<?>> consumer);
}
