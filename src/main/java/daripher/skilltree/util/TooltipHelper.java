package daripher.skilltree.util;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class TooltipHelper {
	public static MutableComponent getAttributeBonusTooltip(Triple<Attribute, Double, Operation> attributeBonus, double gemstoneStrengthBonus) {
		var modifierValue = attributeBonus.getMiddle() * (1 + gemstoneStrengthBonus);
		var modifierOperation = attributeBonus.getRight();
		var modifiedAttribute = attributeBonus.getLeft();
		var visibleBonusValue = modifierValue;

		if (modifierOperation == AttributeModifier.Operation.ADDITION) {
			if (modifiedAttribute.equals(Attributes.KNOCKBACK_RESISTANCE)) {
				visibleBonusValue *= 10D;
			}
		} else {
			visibleBonusValue *= 100D;
		}

		if (modifierValue < 0D) {
			visibleBonusValue *= -1D;
		}

		var textOperation = modifierValue > 0 ? "plus" : "take";
		var style = modifierValue > 0 ? ChatFormatting.BLUE : ChatFormatting.RED;
		var attributeComponent = Component.translatable(modifiedAttribute.getDescriptionId());
		var formattedVisibleBonus = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleBonusValue);
		var formattedOperation = "attribute.modifier." + textOperation + "." + modifierOperation.toValue();
		return Component.translatable(formattedOperation, formattedVisibleBonus, attributeComponent).withStyle(style);
	}
}
