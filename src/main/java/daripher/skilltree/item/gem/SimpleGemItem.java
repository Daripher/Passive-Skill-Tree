package daripher.skilltree.item.gem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import daripher.skilltree.item.ItemHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class SimpleGemItem extends GemItem {
	private Optional<Pair<Attribute, AttributeModifier>> helmetBonus = Optional.empty();
	private Optional<Pair<Attribute, AttributeModifier>> chestplateBonus = Optional.empty();
	private Optional<Pair<Attribute, AttributeModifier>> leggingsBonus = Optional.empty();
	private Optional<Pair<Attribute, AttributeModifier>> bootsBonus = Optional.empty();
	private Optional<Pair<Attribute, AttributeModifier>> weaponBonus = Optional.empty();
	private Optional<Pair<Attribute, AttributeModifier>> shieldBonus = Optional.empty();
	private Optional<Pair<Attribute, AttributeModifier>> bowBonus = Optional.empty();
	private Optional<Pair<Attribute, AttributeModifier>> pickaxeBonus = Optional.empty();

	public SimpleGemItem(int color) {
		super(color);
	}

	@Override
	public Optional<Pair<Attribute, AttributeModifier>> getGemBonus(Player player, ItemStack itemStack) {
		EquipmentSlot slot = ItemHelper.getSlotForItem(itemStack);
		if (ItemHelper.isPickaxe(itemStack) && slot == EquipmentSlot.MAINHAND) return getPickaxeBonus();
		else if (ItemHelper.isMeleeWeapon(itemStack) && slot == EquipmentSlot.MAINHAND) return getWeaponBonus();
		else if (ItemHelper.isShield(itemStack) && slot == EquipmentSlot.OFFHAND) return getShieldBonus();
		else if (ItemHelper.isHelmet(itemStack) && slot == EquipmentSlot.HEAD) return getHelmetBonus();
		else if (ItemHelper.isChestplate(itemStack) && slot == EquipmentSlot.CHEST) return getChestplateBonus();
		else if (ItemHelper.isLeggings(itemStack) && slot == EquipmentSlot.LEGS) return getLeggingsBonus();
		else if (ItemHelper.isBoots(itemStack) && slot == EquipmentSlot.FEET) return getBootsBonus();
		else if (ItemHelper.isRangedWeapon(itemStack) && slot == EquipmentSlot.MAINHAND) return getBowBonus();
		return Optional.empty();
	}

	public Optional<Pair<Attribute, AttributeModifier>> getHelmetBonus() {
		return helmetBonus;
	}

	public Optional<Pair<Attribute, AttributeModifier>> getChestplateBonus() {
		return chestplateBonus;
	}

	public Optional<Pair<Attribute, AttributeModifier>> getLeggingsBonus() {
		return leggingsBonus;
	}

	public Optional<Pair<Attribute, AttributeModifier>> getBootsBonus() {
		return bootsBonus;
	}

	public Optional<Pair<Attribute, AttributeModifier>> getWeaponBonus() {
		return weaponBonus;
	}

	public Optional<Pair<Attribute, AttributeModifier>> getShieldBonus() {
		return shieldBonus;
	}

	public Optional<Pair<Attribute, AttributeModifier>> getBowBonus() {
		return bowBonus;
	}

	public Optional<Pair<Attribute, AttributeModifier>> getPickaxeBonus() {
		return pickaxeBonus;
	}

	public List<Pair<Attribute, AttributeModifier>> getBonuses() {
		// formatter:off
		return Arrays.asList(helmetBonus, chestplateBonus, leggingsBonus, bootsBonus, weaponBonus, shieldBonus, bowBonus, pickaxeBonus)
				.stream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
		// formatter:on
	}

	protected void setHelmetBonus(Attribute attribute, double value, Operation operation) {
		helmetBonus = Optional.of(Pair.of(attribute, new AttributeModifier("Gem Bonus", value, operation)));
	}

	protected void setChestplateBonus(Attribute attribute, double value, Operation operation) {
		chestplateBonus = Optional.of(Pair.of(attribute, new AttributeModifier("Gem Bonus", value, operation)));
	}

	protected void setLeggingsBonus(Attribute attribute, double value, Operation operation) {
		leggingsBonus = Optional.of(Pair.of(attribute, new AttributeModifier("Gem Bonus", value, operation)));
	}

	protected void setBootsBonus(Attribute attribute, double value, Operation operation) {
		bootsBonus = Optional.of(Pair.of(attribute, new AttributeModifier("Gem Bonus", value, operation)));
	}

	protected void setWeaponBonus(Attribute attribute, double value, Operation operation) {
		weaponBonus = Optional.of(Pair.of(attribute, new AttributeModifier("Gem Bonus", value, operation)));
	}

	protected void setShieldBonus(Attribute attribute, double value, Operation operation) {
		shieldBonus = Optional.of(Pair.of(attribute, new AttributeModifier("Gem Bonus", value, operation)));
	}

	protected void setBowBonus(Attribute attribute, double value, Operation operation) {
		bowBonus = Optional.of(Pair.of(attribute, new AttributeModifier("Gem Bonus", value, operation)));
	}

	protected void setPickaxeBonus(Attribute attribute, double value, Operation operation) {
		pickaxeBonus = Optional.of(Pair.of(attribute, new AttributeModifier("Gem Bonus", value, operation)));
	}

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
}
