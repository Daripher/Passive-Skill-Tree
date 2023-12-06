package daripher.skilltree.item.quiver;

import java.util.function.Consumer;

import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.IncomingHealingBonus;

public class HealingQuiverItem extends QuiverItem {
  public HealingQuiverItem() {
    super(150);
  }
  @Override
  public void getItemBonuses(Consumer<ItemBonus<?>> consumer) {
    consumer.accept(new ItemSkillBonus(new IncomingHealingBonus(0.05f)));
  }
}
