package daripher.skilltree.item.gem;

import daripher.skilltree.skill.bonus.condition.item.PotionCondition;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.item.PotionAmplificationBonus;
import daripher.skilltree.skill.bonus.item.PotionDurationBonus;
import daripher.skilltree.skill.bonus.player.CraftedItemBonus;

public class AdamiteItem extends SimpleGemItem {
  public AdamiteItem() {
    super();
    setBonuses(
        new ItemSkillBonus(
            new CraftedItemBonus(
                new PotionCondition(PotionCondition.Type.ANY), new PotionAmplificationBonus(0.02f))),
        "necklace");
    setBonuses(
        new ItemSkillBonus(
            new CraftedItemBonus(
                new PotionCondition(PotionCondition.Type.ANY), new PotionDurationBonus(0.02f))),
        "ring");
  }
}
