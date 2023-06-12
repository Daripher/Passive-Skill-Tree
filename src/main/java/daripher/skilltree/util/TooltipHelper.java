package daripher.skilltree.util;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class TooltipHelper {
	public static MutableComponent getAttributeBonusTooltip(Triple<Attribute, Double, Operation> attributeBonus) {
		var modifierValue = attributeBonus.getMiddle();
		var modifierOperation = attributeBonus.getRight();
		var modifiedAttribute = attributeBonus.getLeft();
		if (modifierOperation == null || modifiedAttribute == null) {
			return Component.literal("ERROR").withStyle(ChatFormatting.RED);
		}
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
		if (modifiedAttribute.getDescriptionId().contains(".can_")) {
			return attributeComponent.withStyle(style);
		}
		var formattedVisibleBonus = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleBonusValue);
		var formattedOperation = "attribute.modifier." + textOperation + "." + modifierOperation.toValue();
		return Component.translatable(formattedOperation, formattedVisibleBonus, attributeComponent).withStyle(style);
	}

	public static MutableComponent getAttributeBonusTooltip(Pair<Attribute, AttributeModifier> attributeBonus) {
		var modifier = attributeBonus.getRight();
		return getAttributeBonusTooltip(Triple.of(attributeBonus.getLeft(), modifier.getAmount(), modifier.getOperation()));
	}

	public static MutableComponent getEffectTooltip(MobEffectInstance effect) {
		MutableComponent potionTooltip = Component.translatable(effect.getDescriptionId());
		MobEffect mobeffect = effect.getEffect();
		if (effect.getAmplifier() > 0) {
			potionTooltip = Component.translatable("potion.withAmplifier", potionTooltip, Component.translatable("potion.potency." + effect.getAmplifier()));
		}
		if (effect.getDuration() > 20) {
			potionTooltip = Component.translatable("potion.withDuration", potionTooltip, MobEffectUtil.formatDuration(effect, 1F));
		}
		potionTooltip = potionTooltip.withStyle(mobeffect.getCategory().getTooltipFormatting());
		potionTooltip = Component.literal(" ").append(potionTooltip);
		return potionTooltip;
	}
}
