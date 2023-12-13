package daripher.skilltree.item.gem;

import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.BlockBreakSpeedBonus;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MoonstoneItem extends SimpleGemItem {
  public MoonstoneItem() {
    super();
    setBonuses(
        new ItemSkillBonus(new BlockBreakSpeedBonus(new NoneLivingCondition(), 0.02f)), "pickaxe");
    setAttributeBonuses(Attributes.MOVEMENT_SPEED, 0.02F, Operation.MULTIPLY_BASE, "boots");
    setAttributeBonuses(
        Attributes.ATTACK_SPEED, 0.02F, Operation.MULTIPLY_BASE, "melee_weapon", "ranged_weapon");
    setAttributeBonuses(PSTAttributes.BLOCKING.get(), 0.02F, Operation.MULTIPLY_BASE, "shield");
    setAttributeBonuses(PSTAttributes.EVASION.get(), 0.02F, Operation.MULTIPLY_BASE, "chestplate");
    setAttributeBonuses(PSTAttributes.EVASION.get(), 0.01F, Operation.MULTIPLY_BASE, "helmet");
  }
}
