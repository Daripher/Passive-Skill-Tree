package daripher.skilltree.item.necklace;

import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.IncomingHealingBonus;
import java.util.function.Consumer;

public class HealerNecklace extends NecklaceItem {
  @Override
  public void getItemBonuses(Consumer<ItemBonus<?>> consumer) {
    consumer.accept(new ItemSkillBonus(new IncomingHealingBonus(0.05f)));
  }
}
