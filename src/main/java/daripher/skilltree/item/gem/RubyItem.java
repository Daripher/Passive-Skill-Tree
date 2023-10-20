package daripher.skilltree.item.gem;

import daripher.skilltree.init.PSTAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class RubyItem extends SimpleGemItem {
  public RubyItem() {
    super();
    setBonuses(Attributes.MAX_HEALTH, 2F, Operation.ADDITION, "shield");
    setBonuses(Attributes.MAX_HEALTH, 1F, Operation.ADDITION, "chestplate");
    setBonuses(Attributes.MAX_HEALTH, 0.5F, Operation.ADDITION, "helmet");
    setBonuses(
        PSTAttributes.LIFE_PER_HIT.get(),
        0.1F,
        Operation.ADDITION,
        "melee_weapon",
        "ranged_weapon");
    setBonuses(PSTAttributes.LIFE_ON_BLOCK.get(), 0.1F, Operation.ADDITION, "shield");
    setBonuses(PSTAttributes.LIFE_REGENERATION.get(), 0.1F, Operation.ADDITION, "ring", "necklace");
  }
}
