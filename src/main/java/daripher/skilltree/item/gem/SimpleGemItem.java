package daripher.skilltree.item.gem;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.util.ItemHelper;
import daripher.skilltree.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class SimpleGemItem extends GemItem {
	private Triple<Attribute, Double, Operation> helmetBonus;
	private Triple<Attribute, Double, Operation> chestplateBonus;
	private Triple<Attribute, Double, Operation> leggingsBonus;
	private Triple<Attribute, Double, Operation> bootsBonus;
	private Triple<Attribute, Double, Operation> weaponBonus;
	private Triple<Attribute, Double, Operation> shieldBonus;
	private Triple<Attribute, Double, Operation> bowBonus;
	private Triple<Attribute, Double, Operation> pickaxeBonus;

	public SimpleGemItem(int color) {
		super(color);
	}

	@Override
	public Triple<Attribute, Double, Operation> getGemBonus(Player player, ItemStack itemStack) {
		var itemSlot = ItemHelper.getSlotForItem(itemStack);
		if (ItemHelper.isPickaxe(itemStack) && itemSlot == EquipmentSlot.MAINHAND) return getPickaxeBonus();
		else if (ItemHelper.isWeapon(itemStack) && itemSlot == EquipmentSlot.MAINHAND) return getWeaponBonus();
		else if (ItemHelper.isShield(itemStack) && itemSlot == EquipmentSlot.OFFHAND) return getShieldBonus();
		else if (ItemHelper.isHelmet(itemStack) && itemSlot == EquipmentSlot.HEAD) return getHelmetBonus();
		else if (ItemHelper.isChestplate(itemStack) && itemSlot == EquipmentSlot.CHEST) return getChestplateBonus();
		else if (ItemHelper.isLeggings(itemStack) && itemSlot == EquipmentSlot.LEGS) return getLeggingsBonus();
		else if (ItemHelper.isBoots(itemStack) && itemSlot == EquipmentSlot.FEET) return getBootsBonus();
		else if (ItemHelper.isBow(itemStack) && itemSlot == EquipmentSlot.MAINHAND) return getBowBonus();
		return null;
	}

	public Triple<Attribute, Double, Operation> getHelmetBonus() { return helmetBonus; }

	public Triple<Attribute, Double, Operation> getChestplateBonus() { return chestplateBonus; }

	public Triple<Attribute, Double, Operation> getLeggingsBonus() { return leggingsBonus; }

	public Triple<Attribute, Double, Operation> getBootsBonus() { return bootsBonus; }

	public Triple<Attribute, Double, Operation> getWeaponBonus() { return weaponBonus; }

	public Triple<Attribute, Double, Operation> getShieldBonus() { return shieldBonus; }

	public Triple<Attribute, Double, Operation> getBowBonus() { return bowBonus; }

	public Triple<Attribute, Double, Operation> getPickaxeBonus() { return pickaxeBonus; }

	public List<Triple<Attribute, Double, Operation>> getBonuses() {
		return Arrays.asList(helmetBonus, chestplateBonus, leggingsBonus, bootsBonus, weaponBonus, shieldBonus, bowBonus, pickaxeBonus)
				.stream()
				.filter(Objects::nonNull)
				.toList();
	}
	
	protected void setHelmetBonus(Attribute attribute, double value, Operation operation) { helmetBonus = Triple.of(attribute, value, operation); }

	protected void setChestplateBonus(Attribute attribute, double value, Operation operation) { chestplateBonus = Triple.of(attribute, value, operation); }

	protected void setLeggingsBonus(Attribute attribute, double value, Operation operation) { leggingsBonus = Triple.of(attribute, value, operation); }

	protected void setBootsBonus(Attribute attribute, double value, Operation operation) { bootsBonus = Triple.of(attribute, value, operation); }

	protected void setWeaponBonus(Attribute attribute, double value, Operation operation) { weaponBonus = Triple.of(attribute, value, operation); }

	protected void setShieldBonus(Attribute attribute, double value, Operation operation) { shieldBonus = Triple.of(attribute, value, operation); }

	protected void setBowBonus(Attribute attribute, double value, Operation operation) { bowBonus = Triple.of(attribute, value, operation); }

	protected void setPickaxeBonus(Attribute attribute, double value, Operation operation) { pickaxeBonus = Triple.of(attribute, value, operation); }

	protected void setArmorBonus(Attribute attribute, float value, Operation operation) {
		setHelmetBonus(attribute, value, operation);
		setChestplateBonus(attribute, value, operation);
		setLeggingsBonus(attribute, value, operation);
		setBootsBonus(attribute, value, operation);
	}

	protected void setArmorAndShieldBonus(Attribute attribute, float value, Operation operation) {
		setArmorBonus(attribute, value, operation);
		setShieldBonus(attribute, value, operation);
	}

	protected void setAllBonuses(Attribute attribute, float value, Operation operation) {
		setArmorAndShieldBonus(attribute, value, operation);
		setWeaponBonus(attribute, value, operation);
		setBowBonus(attribute, value, operation);
		setPickaxeBonus(attribute, value, operation);
	}

	protected Component getBonusTooltip(String slot, Triple<Attribute, Double, Operation> bonus) {
		var slotTooltip = Component.translatable("gem.slot." + slot).withStyle(ChatFormatting.GOLD);
		var bonusTooltip = TooltipHelper.getAttributeBonusTooltip(bonus);
		return Component.empty().append(slotTooltip).append(bonusTooltip);
	}
}
