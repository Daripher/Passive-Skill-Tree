package daripher.skilltree.item;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public abstract class GemstoneItem extends Item {
	private final int gemstoneColor;

	public GemstoneItem(int gemstoneColor) {
		super(new Properties().tab(CreativeModeTab.TAB_MATERIALS));
		this.gemstoneColor = gemstoneColor;
	}

	@Override
	public MutableComponent getName(ItemStack itemStack) {
		return Component.literal("").append(super.getName(itemStack)).withStyle(Style.EMPTY.withColor(gemstoneColor));
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level level, List<Component> components, TooltipFlag tooltipFlag) {
		components.add(Component.empty());
		addModifierDescription(components, "helmet", getHelmetBonus());
		addModifierDescription(components, "chestplate", getChestplateBonus());
		addModifierDescription(components, "leggings", getLeggingsBonus());
		addModifierDescription(components, "boots", getBootsBonus());
		addModifierDescription(components, "weapon", getWeaponBonus());
		addModifierDescription(components, "shield", getShieldBonus());
		addModifierDescription(components, "bow", getBowBonus());
	}

	private void addModifierDescription(List<Component> components, String itemType, Triple<Attribute, Double, Operation> attributeBonus) {
		if (attributeBonus.getMiddle() == 0) {
			return;
		}

		components.add(Component.translatable("gemstone.modifier." + itemType).withStyle(ChatFormatting.GRAY));
		var attributeBonusComponent = TooltipHelper.getAttributeBonusTooltip(attributeBonus, 0);
		components.add(attributeBonusComponent);
	}

	public abstract Triple<Attribute, Double, Operation> getHelmetBonus();

	public abstract Triple<Attribute, Double, Operation> getChestplateBonus();

	public abstract Triple<Attribute, Double, Operation> getLeggingsBonus();

	public abstract Triple<Attribute, Double, Operation> getBootsBonus();

	public abstract Triple<Attribute, Double, Operation> getWeaponBonus();

	public abstract Triple<Attribute, Double, Operation> getShieldBonus();

	public abstract Triple<Attribute, Double, Operation> getBowBonus();
}
