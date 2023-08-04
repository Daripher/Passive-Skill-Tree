package daripher.skilltree.util;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.item.gem.GemItem;
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
	private static final MutableComponent ERROR = Component.literal("ERROR").withStyle(ChatFormatting.RED);

	public static MutableComponent getAttributeBonusTooltip(Optional<Pair<Attribute, AttributeModifier>> bonus) {
		return bonus.isPresent() ? getAttributeBonusTooltip(bonus.get()) : ERROR;
	}

	public static MutableComponent getAttributeBonusTooltip(Pair<Attribute, AttributeModifier> bonus) {
		return getAttributeBonusTooltip(bonus, true);
	}

	public static MutableComponent getAttributeBonusTooltip(Pair<Attribute, AttributeModifier> bonus, boolean format) {
		AttributeModifier modifier = bonus.getRight();
		if (modifier == null) return ERROR;
		Attribute attribute = bonus.getLeft();
		Operation operation = modifier.getOperation();
		if (operation == null || attribute == null) return ERROR;
		double amount = modifier.getAmount();
		double visibleAmount = amount;
		if (operation == AttributeModifier.Operation.ADDITION) {
			if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) visibleAmount *= 10D;
		} else {
			visibleAmount *= 100D;
		}
		if (amount < 0D) visibleAmount *= -1D;
		String textOperation = amount > 0 ? "plus" : "take";
		ChatFormatting style = getModifierColor(attribute, amount, format);
		MutableComponent attributeDescription = Component.translatable(attribute.getDescriptionId());
		if (!isNumeric(attribute)) return attributeDescription.withStyle(style);
		String textAmount = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(visibleAmount);
		textOperation = "attribute.modifier." + textOperation + "." + operation.toValue();
		return Component.translatable(textOperation, textAmount, attributeDescription).withStyle(style);
	}

	public static ChatFormatting getModifierColor(Attribute attribute, double amount, boolean format) {
		if (format && isNegative(attribute)) {
			return amount > 0 ? ChatFormatting.RED : ChatFormatting.BLUE;
		} else {
			return amount > 0 ? ChatFormatting.BLUE : ChatFormatting.RED;
		}
	}

	private static boolean isNumeric(Attribute attribute) {
		return attribute != PSTAttributes.CAN_MIX_POTIONS.get() && attribute != PSTAttributes.CAN_POISON_WEAPONS.get();
	}

	private static boolean isNegative(Attribute attribute) {
		return attribute == PSTAttributes.ENCHANTMENT_LEVEL_REQUIREMENT.get();
	}

	public static MutableComponent getAttributeBonusTooltip(String slot, Optional<Pair<Attribute, AttributeModifier>> bonus) {
		return bonus.isPresent() ? getAttributeBonusTooltip(slot, bonus.get()) : ERROR;
	}

	public static MutableComponent getAttributeBonusTooltip(String slot, Pair<Attribute, AttributeModifier> bonus) {
		Component slotTooltip = GemItem.formatGemClass(slot).withStyle(ChatFormatting.GRAY);
		Component bonusTooltip = getAttributeBonusTooltip(bonus);
		return Component.empty().append(slotTooltip).append(bonusTooltip);
	}

	public static MutableComponent getEffectTooltip(MobEffectInstance effectInstance) {
		MutableComponent tooltip = Component.translatable(effectInstance.getDescriptionId());
		MobEffect effect = effectInstance.getEffect();
		if (effectInstance.getAmplifier() > 0) {
			MutableComponent amplifier = Component.translatable("potion.potency." + effectInstance.getAmplifier());
			tooltip = Component.translatable("potion.withAmplifier", tooltip, amplifier);
		}
		if (effectInstance.getDuration() > 20) {
			Component duration = MobEffectUtil.formatDuration(effectInstance, 1F);
			tooltip = Component.translatable("potion.withDuration", tooltip, duration);
		}
		tooltip = tooltip.withStyle(effect.getCategory().getTooltipFormatting());
		return Component.literal(" ").append(tooltip);
	}
}
