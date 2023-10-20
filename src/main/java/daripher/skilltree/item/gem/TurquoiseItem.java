package daripher.skilltree.item.gem;

import daripher.skilltree.init.PSTAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class TurquoiseItem extends SimpleGemItem {
  public TurquoiseItem() {
    super();
    setBonuses(
        PSTAttributes.ENCHANTMENT_LEVEL_REQUIREMENT.get(),
        -0.02F,
        Operation.MULTIPLY_BASE,
        "necklace");
    setBonuses(PSTAttributes.ENCHANTMENT_POWER.get(), 0.02F, Operation.MULTIPLY_BASE, "ring");
  }
}
