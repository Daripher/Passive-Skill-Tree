package daripher.skilltree.item.quiver;

import daripher.skilltree.item.ItemBonusProvider;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BoneQuiverItem extends QuiverItem implements ItemBonusProvider {
  @Override
  public @NotNull List<ItemBonus<?>> getItemBonuses() {
    return List.of(
        new ItemSkillBonus(
            new LootDuplicationBonus(0.05f, 2f, LootDuplicationBonus.LootType.MOBS)));
  }
}
