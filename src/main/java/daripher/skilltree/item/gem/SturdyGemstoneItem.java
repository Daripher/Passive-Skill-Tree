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

public class SturdyGemstoneItem extends SimpleGemstoneItem {
	public SturdyGemstoneItem() {
		super(0xECB573);
	}

	@Override
	public Triple<Attribute, Double, Operation> getHelmetBonus() {
		return Triple.of(Attributes.ARMOR, 0.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getChestplateBonus() {
		return Triple.of(Attributes.ARMOR, 1D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getLeggingsBonus() {
		return Triple.of(Attributes.ARMOR, 0.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBootsBonus() {
		return Triple.of(Attributes.ARMOR, 0.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getWeaponBonus() {
		return Triple.of(Attributes.ATTACK_DAMAGE, 0.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getShieldBonus() {
		return Triple.of(Attributes.ARMOR, 0.5D, Operation.ADDITION);
	}

	@Override
	public Triple<Attribute, Double, Operation> getBowBonus() {
		return Triple.of(SkillTreeAttributes.ARROW_DAMAGE_BONUS.get(), 0.5D, Operation.ADDITION);
	}

	@Override
	protected void appenBonusesTooltip(List<Component> components) {
		components.add(Component.translatable("gemstone.slot.chestplate").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getChestplateBonus())));
		components.add(Component.translatable("gemstone.slot.other_armor").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getHelmetBonus())));
		components.add(Component.translatable("gemstone.slot.melee_weapon").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getWeaponBonus())));
		components.add(Component.translatable("gemstone.slot.shield").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getShieldBonus())));
		components.add(Component.translatable("gemstone.slot.bow").withStyle(ChatFormatting.GOLD).append(TooltipHelper.getAttributeBonusTooltip(getBowBonus())));
	}
}
