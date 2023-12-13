package daripher.skilltree.item.quiver;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class GildedQuiverItem extends QuiverItem {
  public GildedQuiverItem() {
    super(150);
  }

  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
      SlotContext slotContext, UUID uuid, ItemStack stack) {
    Multimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create();
    modifiers.put(
        Attributes.MOVEMENT_SPEED,
        new AttributeModifier(uuid, "Quiver", 0.05, Operation.MULTIPLY_BASE));
    return modifiers;
  }

  @Override
  public boolean makesPiglinsNeutral(SlotContext slotContext, ItemStack stack) {
    return true;
  }
}
