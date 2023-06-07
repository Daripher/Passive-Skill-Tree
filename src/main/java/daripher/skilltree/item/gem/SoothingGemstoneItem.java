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

public class SoothingGemstoneItem extends SimpleGemstoneItem {
	public SoothingGemstoneItem() {
		super(0xB4E58B);
	}

	@Override
	public Triple<Attribute, Double, Operation> getHelmetBonus() {
		return Triple.of(SkillTreeAttributes.LIFE_REGENERATION.get(), 0.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getChestplateBonus() {
		return Triple.of(Attributes.MAX_HEALTH, 2D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getLeggingsBonus() {
		return Triple.of(Attributes.MAX_HEALTH, 1D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBootsBonus() {
		return Triple.of(Attributes.MAX_HEALTH, 1D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getWeaponBonus() {
		return Triple.of(SkillTreeAttributes.LIFE_PER_HIT.get(), 0.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getShieldBonus() {
		return Triple.of(SkillTreeAttributes.LIFE_ON_BLOCK.get(), 0.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBowBonus() {
		return Triple.of(SkillTreeAttributes.LIFE_PER_HIT.get(), 0.5D, Operation.ADDITION);
	}

	@Override
	protected void appenBonusesTooltip(List<Component> components) {
		components.add(Component.translatable("gemstone.slot.helmet").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getHelmetBonus())));
		components.add(Component.translatable("gemstone.slot.chestplate").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getChestplateBonus())));
		components.add(Component.translatable("gemstone.slot.other_armor").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getLeggingsBonus())));
		components.add(Component.translatable("gemstone.slot.weapon").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getWeaponBonus())));
		components.add(Component.translatable("gemstone.slot.shield").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getShieldBonus())));
	}
}
