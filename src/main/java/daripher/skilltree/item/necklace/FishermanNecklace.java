package daripher.skilltree.item.necklace;

import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import java.util.function.Consumer;

public class FishermanNecklace extends NecklaceItem {
  @Override
  public void getItemBonuses(Consumer<ItemBonus<?>> consumer) {
    consumer.accept(
        new ItemSkillBonus(
            new LootDuplicationBonus(0.05f, 1f, LootDuplicationBonus.LootType.FISHING)));
  }
}
