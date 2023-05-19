package daripher.skilltree.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

@Mixin(EnchantmentMenu.class)
public abstract class MixinEnchantmentMenu extends AbstractContainerMenu {
	private @Shadow @Final DataSlot enchantmentSeed;
	public @Shadow @Final int[] costs;
	private Player player;
	private int[] costsBeforeReduction = new int[3];

	private MixinEnchantmentMenu() {
		super(null, 0);
	}

	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
	private void setPlayer(int windowId, Inventory inventory, ContainerLevelAccess levelAccess, CallbackInfo callbackInfo) {
		player = inventory.player;
	}

	@Redirect(method = { "lambda$slotsChanged$0", "m_39483_" },
			at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onEnchantmentLevelSet(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;IILnet/minecraft/world/item/ItemStack;I)I"))
	private int decreaseLevelRequirements(Level level, BlockPos pos, int slot, int power, ItemStack itemStack, int enchantmentLevel) {
		var cost = ForgeEventFactory.onEnchantmentLevelSet(level, pos, slot, power, itemStack, costs[slot]);
		costsBeforeReduction[slot] = cost;
		var requirementDecrease = player.getAttributeValue(SkillTreeAttributes.ENCHANTMENT_LEVEL_REQUIREMENT_DECREASE.get()) - 1;
		if (requirementDecrease == 0) {
			return cost;
		}
		return reduceCost(cost, requirementDecrease);
	}

	protected int reduceCost(int cost, double reduction) {
		if (cost == 0) {
			return cost;
		}
		cost *= (1 - reduction);
		if (cost < 1) {
			cost = 1;
		}
		return cost;
	}

	@Redirect(method = { "lambda$slotsChanged$0", "m_39483_" },
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/EnchantmentMenu;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
	private List<EnchantmentInstance> amplifyEnchantmentsVisually(EnchantmentMenu menu, ItemStack itemStack, int slot, int cost) {
		var enchantments = getEnchantmentList(itemStack, slot, costsBeforeReduction[slot]);
		amplifyEnchantmentsLevels(enchantments);
		return enchantments;
	}

	@Redirect(method = { "lambda$clickMenuButton$1", "m_39475_" },
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/EnchantmentMenu;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
	private List<EnchantmentInstance> amplifyEnchantmentsOnButtonClick(EnchantmentMenu menu, ItemStack itemStack, int slot, int cost) {
		var enchantments = getEnchantmentList(itemStack, slot, costsBeforeReduction[slot]);
		amplifyEnchantmentsLevels(enchantments);
		return enchantments;
	}

	private void amplifyEnchantmentsLevels(List<EnchantmentInstance> enchantments) {
		var random = RandomSource.create(enchantmentSeed.get());
		for (var i = 0; i < enchantments.size(); i++) {
			var enchantment = enchantments.get(i);
			var amplifiedEnchantment = amplifyEnchantmentLevel(enchantment, random);
			enchantments.set(i, amplifiedEnchantment);
		}
	}

	protected EnchantmentInstance amplifyEnchantmentLevel(EnchantmentInstance enchantment, RandomSource random) {
		var amplificationChance = player.getAttributeValue(SkillTreeAttributes.ENCHANTMENTS_AMPLIFICATION_CHANCE.get());
		var category = enchantment.enchantment.category;
		if (isArmorEnchantment(category)) {
			amplificationChance += player.getAttributeValue(SkillTreeAttributes.ARMOR_ENCHANTMENTS_AMPLIFICATION_CHANCE.get());
		}
		if (isWeaponEnchantment(category)) {
			amplificationChance += player.getAttributeValue(SkillTreeAttributes.WEAPON_ENCHANTMENTS_AMPLIFICATION_CHANCE.get());
		}
		if (amplificationChance == 0) {
			return enchantment;
		}
		var levelBonus = (int) amplificationChance;
		amplificationChance -= levelBonus;
		var enchantmentLevel = enchantment.level + levelBonus;
		if (random.nextFloat() < amplificationChance) {
			enchantmentLevel++;
		}
		return new EnchantmentInstance(enchantment.enchantment, enchantmentLevel);
	}

	protected boolean isWeaponEnchantment(EnchantmentCategory enchantmentCategory) {
		return enchantmentCategory == EnchantmentCategory.WEAPON || enchantmentCategory == EnchantmentCategory.BOW || enchantmentCategory == EnchantmentCategory.CROSSBOW;
	}

	protected boolean isArmorEnchantment(EnchantmentCategory enchantmentCategory) {
		return enchantmentCategory == EnchantmentCategory.ARMOR || enchantmentCategory == EnchantmentCategory.ARMOR_FEET || enchantmentCategory == EnchantmentCategory.ARMOR_LEGS
				|| enchantmentCategory == EnchantmentCategory.ARMOR_HEAD;
	}

	@Shadow
	private List<EnchantmentInstance> getEnchantmentList(ItemStack stack, int slot, int cost) {
		return null;
	}
}
