package daripher.skilltree.item.gem;

import daripher.skilltree.skill.bonus.item.FoodHealingBonus;
import daripher.skilltree.skill.bonus.item.FoodSaturationBonus;

public class TourmalineItem extends SimpleGemItem {
  public TourmalineItem() {
    super();
    setBonuses(new FoodHealingBonus(0.25f), "necklace");
    setBonuses(new FoodSaturationBonus(0.1f), "ring");
  }
}
