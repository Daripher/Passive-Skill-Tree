package daripher.skilltree.item.gem;

import daripher.skilltree.skill.bonus.condition.item.FoodCondition;
import daripher.skilltree.skill.bonus.item.FoodHealingBonus;
import daripher.skilltree.skill.bonus.item.FoodSaturationBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.CraftedItemBonus;

public class TourmalineItem extends SimpleGemItem {
  public TourmalineItem() {
    super();
    setBonuses(
        new ItemSkillBonus(new CraftedItemBonus(new FoodCondition(), new FoodHealingBonus(0.25f))),
        "necklace");
    setBonuses(
        new ItemSkillBonus(
            new CraftedItemBonus(new FoodCondition(), new FoodSaturationBonus(0.1f))),
        "ring");
  }
}
