package daripher.skilltree.item.gem;

import daripher.skilltree.skill.bonus.condition.enchantment.AnyEnchantmentCondition;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.EnchantmentAmplificationBonus;
import daripher.skilltree.skill.bonus.player.EnchantmentRequirementBonus;

public class TurquoiseItem extends SimpleGemItem {
  public TurquoiseItem() {
    super();
    setBonuses(new ItemSkillBonus(new EnchantmentRequirementBonus(-0.02f)), "necklace");
    setBonuses(
        new ItemSkillBonus(new EnchantmentAmplificationBonus(new AnyEnchantmentCondition(), 0.02f)),
        "ring");
  }
}
