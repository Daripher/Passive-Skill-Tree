package daripher.skilltree.item.gem;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class LightGemstoneItem extends SimpleGemstoneItem {
	public LightGemstoneItem() {
		super(0xD0D0BC);
	}

	@Override
	public Triple<Attribute, Double, Operation> getHelmetBonus() {
		return Triple.of(SkillTreeAttributes.EVASION_CHANCE.get(), 0.01D, Operation.MULTIPLY_BASE);
	}

	@Override
	public Triple<Attribute, Double, Operation> getChestplateBonus() {
		return Triple.of(SkillTreeAttributes.EVASION_CHANCE.get(), 0.02D, Operation.MULTIPLY_BASE);
	}

	@Override
	public Triple<Attribute, Double, Operation> getLeggingsBonus() {
		return Triple.of(SkillTreeAttributes.EVASION_CHANCE.get(), 0.01D, Operation.MULTIPLY_BASE);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBootsBonus() {
		return Triple.of(Attributes.MOVEMENT_SPEED, 0.025D, Operation.MULTIPLY_TOTAL);
	}

	@Override
	public Triple<Attribute, Double, Operation> getWeaponBonus() {
		return Triple.of(Attributes.ATTACK_SPEED, 0.025D, Operation.MULTIPLY_TOTAL);
	}

	@Override
	public Triple<Attribute, Double, Operation> getShieldBonus() {
		return Triple.of(SkillTreeAttributes.BLOCK_CHANCE.get(), 0.01D, Operation.MULTIPLY_BASE);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBowBonus() {
		return Triple.of(Attributes.ATTACK_SPEED, 0.025D, Operation.MULTIPLY_BASE);
	}

	@Override
	protected void appenBonusesTooltip(List<Component> components) {
		components.add(Component.translatable("gemstone.slot.chestplate").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getChestplateBonus())));
		components.add(Component.translatable("gemstone.slot.boots").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getBootsBonus())));
		components.add(Component.translatable("gemstone.slot.other_armor").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getHelmetBonus())));
		components.add(Component.translatable("gemstone.slot.weapon").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getWeaponBonus())));
		components.add(Component.translatable("gemstone.slot.shield").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getShieldBonus())));
	}
}
