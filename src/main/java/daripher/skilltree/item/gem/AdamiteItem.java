package daripher.skilltree.item.gem;

import daripher.skilltree.init.PSTAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class AdamiteItem extends SimpleGemItem {
  public AdamiteItem() {
    super();
    setAttributeBonuses(
        PSTAttributes.BREWED_POTIONS_STRENGTH.get(), 0.02F, Operation.MULTIPLY_BASE, "necklace");
    setAttributeBonuses(
        PSTAttributes.BREWED_POTIONS_DURATION.get(), 0.02F, Operation.MULTIPLY_BASE, "ring");
  }
}
