package daripher.skilltree.item.necklace;

import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.CritChanceBonus;
import daripher.skilltree.skill.bonus.player.CritDamageBonus;
import java.util.function.Consumer;

public class AssassinNecklace extends NecklaceItem {
  @Override
  public void getItemBonuses(Consumer<ItemBonus<?>> consumer) {
    consumer.accept(new ItemSkillBonus(new CritChanceBonus(0.05f)));
    consumer.accept(new ItemSkillBonus(new CritDamageBonus(0.05f)));
  }
}
