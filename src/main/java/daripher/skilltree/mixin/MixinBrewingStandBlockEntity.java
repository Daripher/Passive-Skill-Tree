package daripher.skilltree.mixin;

import java.util.Arrays;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.api.block.ExtendedBrewingStand;
import daripher.skilltree.api.recipe.SkillRequiringBrewingRecipes;
import daripher.skilltree.util.PotionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity extends BaseContainerBlockEntity implements ExtendedBrewingStand {
	private @Shadow boolean[] lastPotionCount;
	private @Shadow Item ingredient;
	private @Shadow @Final static int[] SLOTS_FOR_SIDES;
	private @Shadow NonNullList<ItemStack> items;
	@Shadow int fuel;
	@Shadow int brewTime;
	private Player lastUser;

	protected MixinBrewingStandBlockEntity() {
		super(null, null, null);
	}

	@Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
	private static void extendedServerTick(Level level, BlockPos blockPos, BlockState blockState, BrewingStandBlockEntity brewingStand, CallbackInfo callbackInfo) {
		if (!(brewingStand instanceof ExtendedBrewingStand)) {
			return;
		}

		var extendedBrewingStand = (ExtendedBrewingStand) brewingStand;
		var fuelStack = extendedBrewingStand.getInventory().get(4);

		if (extendedBrewingStand.getFuel() <= 0 && fuelStack.is(Items.BLAZE_POWDER)) {
			extendedBrewingStand.setFuel(20);
			fuelStack.shrink(1);
			setChanged(level, blockPos, blockState);
		}

		var canBrew = isBrewable(extendedBrewingStand);

		if (!canBrew) {
			return;
		}

		var isBrewing = extendedBrewingStand.getBrewTime() > 0;
		var ingredientStack = extendedBrewingStand.getInventory().get(3);

		if (isBrewing) {
			extendedBrewingStand.setBrewTime(extendedBrewingStand.getBrewTime() - 1);
			var finishedBrewing = extendedBrewingStand.getBrewTime() == 0;

			if (finishedBrewing && canBrew) {
				doBrew(level, blockPos, extendedBrewingStand);
				setChanged(level, blockPos, blockState);
			} else if (!canBrew || !ingredientStack.is(extendedBrewingStand.getIngredient())) {
				extendedBrewingStand.setBrewTime(0);
				setChanged(level, blockPos, blockState);
			}
		} else if (canBrew && extendedBrewingStand.getFuel() > 0) {
			extendedBrewingStand.setFuel(extendedBrewingStand.getFuel() - 1);
			extendedBrewingStand.setBrewTime(400);
			extendedBrewingStand.setIngredient(ingredientStack.getItem());
			setChanged(level, blockPos, blockState);
		}

		var potionCount = extendedBrewingStand.getPotionCount();

		if (!Arrays.equals(potionCount, extendedBrewingStand.getLastPotionCount())) {
			extendedBrewingStand.setLastPotionCount(potionCount);

			if (!(blockState.getBlock() instanceof BrewingStandBlock)) {
				return;
			}

			for (int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; ++i) {
				blockState = blockState.setValue(BrewingStandBlock.HAS_BOTTLE[i], potionCount[i]);
			}

			level.setBlock(blockPos, blockState, 2);
		}

		callbackInfo.cancel();
	}

	@Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
	private void injectCanPlaceItem(int slot, ItemStack itemStack, CallbackInfoReturnable<Boolean> callbackInfo) {
		var validIngredient = slot == 3 && SkillRequiringBrewingRecipes.isValidIngredient(itemStack);
		var validBase = slot < 3 && SkillRequiringBrewingRecipes.isValidInput(itemStack) && getItem(slot).isEmpty();

		if (validIngredient || validBase) {
			callbackInfo.setReturnValue(true);
		}
	}

	private static boolean isBrewable(ExtendedBrewingStand brewingStand) {
		var inventory = brewingStand.getInventory();
		return SkillRequiringBrewingRecipes.canBrew(inventory, inventory.get(3), SLOTS_FOR_SIDES, brewingStand.getLastUser());
	}

	private static void doBrew(Level level, BlockPos blockPos, ExtendedBrewingStand brewingStand) {
		var inventory = brewingStand.getInventory();
		var ingredient = inventory.get(3);
		SkillRequiringBrewingRecipes.brewPotions(inventory, ingredient, SLOTS_FOR_SIDES, brewingStand.getLastUser());

		if (ingredient.hasCraftingRemainingItem()) {
			var remainingItem = ingredient.getCraftingRemainingItem();
			ingredient.shrink(1);

			if (ingredient.isEmpty()) {
				ingredient = remainingItem;
			} else {
				Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), remainingItem);
			}
		} else {
			ingredient.shrink(1);
		}

		inventory.set(3, ingredient);
		level.levelEvent(1035, blockPos, 0);
	}

	@Inject(method = "doBrew", at = @At("TAIL"))
	private static void enhanceBrewedPotions(Level level, BlockPos blockPos, NonNullList<ItemStack> itemStacks, CallbackInfo callbackInfo) {
		var blockEntity = level.getBlockEntity(blockPos);

		if (blockEntity instanceof ExtendedBrewingStand brewingStand) {
			var player = brewingStand.getLastUser();
			var amplificationChance = PotionHelper.getPlayerPotionAmplificationChance(player);
			var durationBonus = PotionHelper.getPlayerPotionDurationBonus(player);

			for (var slot = 0; slot < 3; slot++) {
				PotionHelper.enhancePotion(itemStacks.get(slot), amplificationChance, durationBonus);
			}
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
		lastUser = player;
		return super.createMenu(window, inventory, player);
	}

	@Override
	public Player getLastUser() {
		return lastUser;
	}

	@Override
	public NonNullList<ItemStack> getInventory() {
		return items;
	}

	@Override
	public int getFuel() {
		return fuel;
	}

	@Override
	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	@Override
	public int getBrewTime() {
		return brewTime;
	}

	@Override
	public void setBrewTime(int brewTime) {
		this.brewTime = brewTime;
	}

	@Override
	public Item getIngredient() {
		return ingredient;
	}

	@Override
	public void setIngredient(Item ingredient) {
		this.ingredient = ingredient;
	}

	@Override
	public boolean[] getPotionCount() {
		var counts = new boolean[3];

		for (int i = 0; i < 3; ++i) {
			if (!items.get(i).isEmpty()) {
				counts[i] = true;
			}
		}

		return counts;
	}

	@Override
	public boolean[] getLastPotionCount() {
		return lastPotionCount;
	}

	@Override
	public void setLastPotionCount(boolean[] lastPotionCount) {
		this.lastPotionCount = lastPotionCount;
	}
}
