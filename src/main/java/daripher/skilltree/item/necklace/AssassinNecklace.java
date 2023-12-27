package daripher.skilltree.item.necklace;

import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.CritChanceBonus;
import daripher.skilltree.skill.bonus.player.CritDamageBonus;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class AssassinNecklace extends NecklaceItem {
  @Override
  public @NotNull List<ItemBonus<?>> getItemBonuses() {
    return List.of(
        new ItemSkillBonus(new CritChanceBonus(0.05f)),
        new ItemSkillBonus(new CritDamageBonus(0.05f)));
  }
}
