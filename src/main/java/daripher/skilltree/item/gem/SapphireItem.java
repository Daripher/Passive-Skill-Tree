package daripher.skilltree.item.gem;

import daripher.skilltree.init.PSTAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class SapphireItem extends SimpleGemItem {
  public SapphireItem() {
    super();
    setBonuses(
        PSTAttributes.CHANCE_TO_CRAFT_TOUGHER_ARMOR.get(),
        0.02F,
        Operation.MULTIPLY_BASE,
        "necklace");
    setBonuses(
        PSTAttributes.CRAFTED_EQUIPMENT_DURABILITY.get(), 0.02F, Operation.MULTIPLY_BASE, "ring");
  }
}
