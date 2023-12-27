package daripher.skilltree.item.necklace;

import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class FishermanNecklace extends NecklaceItem {
  @Override
  public @NotNull List<ItemBonus<?>> getItemBonuses() {
    return List.of(
        new ItemSkillBonus(
            new LootDuplicationBonus(0.05f, 1f, LootDuplicationBonus.LootType.FISHING)));
  }
}
