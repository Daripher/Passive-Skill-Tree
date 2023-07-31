package daripher.skilltree.item.ring;

import java.util.UUID;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import daripher.skilltree.init.PSTAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class CopperRingItem extends RingItem {
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
		Multimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create();
		modifiers.put(PSTAttributes.BLOCK_CHANCE.get(), new AttributeModifier(uuid, "Ring Bonus", 0.01, Operation.MULTIPLY_BASE));
		return modifiers;
	}
}
