package daripher.skilltree.item.quiver;

import daripher.skilltree.item.ItemBonusProvider;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.CritChanceBonus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DiamondQuiverItem extends QuiverItem implements ItemBonusProvider {
  public DiamondQuiverItem() {
    super(150);
  }

  @Override
  public @NotNull List<ItemBonus<?>> getItemBonuses() {
    return List.of(new ItemSkillBonus(new CritChanceBonus(0.05f)));
  }
}
