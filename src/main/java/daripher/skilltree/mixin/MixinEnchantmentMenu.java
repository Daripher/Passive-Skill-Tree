package daripher.skilltree.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.EnchantmentTableBlock;

@Mixin(EnchantmentMenu.class)
public abstract class MixinEnchantmentMenu extends AbstractContainerMenu {
	private @Shadow @Final ContainerLevelAccess access;
	private @Shadow @Final Container enchantSlots;
	private @Shadow @Final DataSlot enchantmentSeed;
	private @Shadow @Final RandomSource random;
	public @Shadow @Final int[] costs;
	public @Shadow @Final int[] enchantClue;
	public @Shadow @Final int[] levelClue;
	private Player player;
	private int[] costsBeforeReduction;

	private MixinEnchantmentMenu() {
		super(null, 0);
	}

	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
	private void setPlayer(int windowId, Inventory inventory, ContainerLevelAccess levelAccess, CallbackInfo callbackInfo) {
		player = inventory.player;
	}

	@SuppressWarnings("deprecation")
	@Inject(method = "slotsChanged", at = @At("HEAD"), cancellable = true)
	private void extendedSlotsChanged(Container container, CallbackInfo callbackInfo) {
		if (container == enchantSlots) {
			ItemStack enchantingStack = container.getItem(0);

			if (!enchantingStack.isEmpty() && enchantingStack.isEnchantable()) {
				access.execute((level, pos) -> {
					var enchantPower = 0;

					for (BlockPos blockpos : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
						if (EnchantmentTableBlock.isValidBookShelf(level, pos, blockpos)) {
							enchantPower += level.getBlockState(pos.offset(blockpos)).getEnchantPowerBonus(level, pos.offset(blockpos));
						}
					}

					random.setSeed(enchantmentSeed.get());

					for (var slot = 0; slot < 3; ++slot) {
						costs[slot] = EnchantmentHelper.getEnchantmentCost(random, slot, enchantPower, enchantingStack);
						enchantClue[slot] = -1;
						levelClue[slot] = -1;

						if (costs[slot] < slot + 1) {
							costs[slot] = 0;
						}

						costs[slot] = net.minecraftforge.event.ForgeEventFactory.onEnchantmentLevelSet(level, pos, slot, enchantPower, enchantingStack, costs[slot]);
					}

					decreaseLevelRequirement();

					for (var slot = 0; slot < 3; ++slot) {
						if (costs[slot] > 0) {
							List<EnchantmentInstance> list = getEnchantmentList(enchantingStack, slot, costsBeforeReduction[slot]);

							if (list != null && !list.isEmpty()) {
								EnchantmentInstance enchantmentinstance = list.get(this.random.nextInt(list.size()));
								enchantClue[slot] = Registry.ENCHANTMENT.getId(enchantmentinstance.enchantment);
								levelClue[slot] = enchantmentinstance.level;
							}
						}
					}

					broadcastChanges();
				});
			} else {
				for (var slot = 0; slot < 3; ++slot) {
					costs[slot] = 0;
					enchantClue[slot] = -1;
					levelClue[slot] = -1;
				}
			}
		}

		callbackInfo.cancel();
	}

	private void decreaseLevelRequirement() {
		costsBeforeReduction = costs;
		var levelRequirementDecrease = player.getAttributeValue(SkillTreeAttributes.ENCHANTMENT_LEVEL_REQUIREMENT_MULTIPLIER.get());
		if (levelRequirementDecrease == 0) {
			return;
		}
		for (var i = 0; i < costs.length; i++) {
			if (costs[i] == 0) {
				continue;
			}
			costs[i] *= (1 - levelRequirementDecrease);
			if (costs[i] < 1) {
				costs[i] = 1;
			}
		}
	}

	private void amplifyEnchantmentsLevels(List<EnchantmentInstance> enchantments) {
		var random = RandomSource.create(enchantmentSeed.get());
		for (var i = 0; i < enchantments.size(); i++) {
			var enchantment = enchantments.get(i);
			var amplificationChance = player.getAttributeValue(SkillTreeAttributes.ENCHANTMENTS_AMPLIFICATION_CHANCE.get());
			var enchantmentCategory = enchantment.enchantment.category;
			var isArmorEnchtantment = enchantmentCategory == EnchantmentCategory.ARMOR || enchantmentCategory == EnchantmentCategory.ARMOR_FEET || enchantmentCategory == EnchantmentCategory.ARMOR_LEGS
					|| enchantmentCategory == EnchantmentCategory.ARMOR_HEAD;
			if (isArmorEnchtantment) {
				amplificationChance += player.getAttributeValue(SkillTreeAttributes.ARMOR_ENCHANTMENTS_AMPLIFICATION_CHANCE.get());
			}
			var isWeaponEnchtantment = enchantmentCategory == EnchantmentCategory.WEAPON || enchantmentCategory == EnchantmentCategory.BOW || enchantmentCategory == EnchantmentCategory.CROSSBOW;
			if (isWeaponEnchtantment) {
				amplificationChance += player.getAttributeValue(SkillTreeAttributes.WEAPON_ENCHANTMENTS_AMPLIFICATION_CHANCE.get());
			}
			if (amplificationChance == 0) {
				return;
			}
			var levelBonus = (int) amplificationChance;
			amplificationChance -= levelBonus;
			var enchantmentLevel = enchantment.level + levelBonus;
			if (random.nextFloat() < amplificationChance) {
				enchantmentLevel++;
			}
			enchantments.set(i, new EnchantmentInstance(enchantment.enchantment, enchantmentLevel));
		}
	}

	@Inject(method = "getEnchantmentList", at = @At("HEAD"), cancellable = true)
	private void extendedGetEnchantmentList(ItemStack enchantingStack, int slot, int cost, CallbackInfoReturnable<List<EnchantmentInstance>> callbackInfo) {
		random.setSeed(enchantmentSeed.get() + slot);
		var enchantments = EnchantmentHelper.selectEnchantment(random, enchantingStack, costsBeforeReduction[slot], false);
		if (enchantingStack.is(Items.BOOK) && enchantments.size() > 1) {
			enchantments.remove(random.nextInt(enchantments.size()));
		}
		amplifyEnchantmentsLevels(enchantments);
		callbackInfo.setReturnValue(enchantments);
	}

	private @Shadow List<EnchantmentInstance> getEnchantmentList(ItemStack itemStack, int slot, int cost) {
		return null;
	}
}
