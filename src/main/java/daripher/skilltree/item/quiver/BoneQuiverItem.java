package daripher.skilltree.item.quiver;

import daripher.skilltree.item.ItemBonusProvider;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;

import java.util.function.Consumer;

public class BoneQuiverItem extends QuiverItem implements ItemBonusProvider {
  @Override
  public void getItemBonuses(Consumer<ItemBonus<?>> consumer) {
    consumer.accept(
        new ItemSkillBonus(
            new LootDuplicationBonus(0.05f, 2f, LootDuplicationBonus.LootType.MOBS)));
  }
}
