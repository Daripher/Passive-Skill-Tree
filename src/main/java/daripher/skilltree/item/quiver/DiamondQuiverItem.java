package daripher.skilltree.item.quiver;

import daripher.skilltree.item.ItemBonusProvider;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.CritChanceBonus;

import java.util.function.Consumer;

public class DiamondQuiverItem extends QuiverItem implements ItemBonusProvider {
  public DiamondQuiverItem() {
    super(150);
  }
  @Override
  public void getItemBonuses(Consumer<ItemBonus<?>> consumer) {
    consumer.accept(new ItemSkillBonus(new CritChanceBonus(0.05f)));
  }
}
