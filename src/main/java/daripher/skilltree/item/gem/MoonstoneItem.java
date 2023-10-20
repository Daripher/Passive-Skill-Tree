package daripher.skilltree.item.gem;

import daripher.skilltree.init.PSTAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MoonstoneItem extends SimpleGemItem {
  public MoonstoneItem() {
    super();
    setBonuses(PSTAttributes.MINING_SPEED.get(), 0.02F, Operation.MULTIPLY_BASE, "pickaxe");
    setBonuses(Attributes.MOVEMENT_SPEED, 0.02F, Operation.MULTIPLY_BASE, "boots");
    setBonuses(
        Attributes.ATTACK_SPEED, 0.02F, Operation.MULTIPLY_BASE, "melee_weapon", "ranged_weapon");
    setBonuses(PSTAttributes.BLOCK_CHANCE.get(), 0.02F, Operation.MULTIPLY_BASE, "shield");
    setBonuses(PSTAttributes.EVASION.get(), 0.02F, Operation.MULTIPLY_BASE, "chestplate");
    setBonuses(PSTAttributes.EVASION.get(), 0.01F, Operation.MULTIPLY_BASE, "helmet");
  }
}
