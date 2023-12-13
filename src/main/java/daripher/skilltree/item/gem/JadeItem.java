package daripher.skilltree.item.gem;

import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;

public class JadeItem extends SimpleGemItem {
  public JadeItem() {
    super();
    setBonuses(
        new ItemSkillBonus(new LootDuplicationBonus(0.025f, 2f, LootDuplicationBonus.LootType.MOBS)),
        "necklace");
    setBonuses(
        new ItemSkillBonus(new LootDuplicationBonus(0.025f, 1f, LootDuplicationBonus.LootType.MOBS)),
        "ring");
  }
}
