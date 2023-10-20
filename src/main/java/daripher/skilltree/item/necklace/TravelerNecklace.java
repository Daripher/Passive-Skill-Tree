package daripher.skilltree.item.necklace;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class TravelerNecklace extends NecklaceItem {
  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
      SlotContext slotContext, UUID uuid, ItemStack stack) {
    Multimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create();
    modifiers.put(
        Attributes.MOVEMENT_SPEED,
        new AttributeModifier(uuid, "Necklace Bonus", 0.05, Operation.MULTIPLY_BASE));
    return modifiers;
  }
}
