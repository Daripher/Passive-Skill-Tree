package daripher.skilltree.item.gem;

import daripher.skilltree.skill.bonus.condition.item.ArmorCondition;
import daripher.skilltree.skill.bonus.item.ItemDurabilityBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import daripher.skilltree.skill.bonus.player.CraftedItemBonus;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SapphireItem extends SimpleGemItem {
  public SapphireItem() {
    super();
    setBonuses(
        new ItemSkillBonus(
            new CraftedItemBonus(
                new ArmorCondition(ArmorCondition.Type.ANY),
                new ItemSkillBonus(
                    new AttributeBonus(
                        Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(
                            UUID.fromString("c9fd21b4-5f42-4b94-994f-760bb39a9e4e"),
                            "Gem",
                            0.05f,
                            Operation.MULTIPLY_BASE))))),
        "necklace");
    setBonuses(
        new ItemSkillBonus(
            new CraftedItemBonus(
                new ArmorCondition(ArmorCondition.Type.ANY),
                new ItemDurabilityBonus(0.1f, Operation.MULTIPLY_BASE))),
        "ring");
  }
}
