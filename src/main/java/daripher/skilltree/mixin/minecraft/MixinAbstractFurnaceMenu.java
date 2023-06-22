package daripher.skilltree.mixin.minecraft;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.api.PlayerContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.ForgeEventFactory;

@Mixin(AbstractFurnaceMenu.class)
public abstract class MixinAbstractFurnaceMenu extends RecipeBookMenu<Container> implements PlayerContainer {
	private Optional<Player> player = Optional.empty();

	private MixinAbstractFurnaceMenu() {
		super(null, 0);
	}

	@Inject(method = "<init>(Lnet/minecraft/world/inventory/MenuType;Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/inventory/RecipeBookType;ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V", at = @At("TAIL"))
	private void setPlayer(MenuType<?> menuType, RecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookType recipeBookType, int windowId, Inventory inventory, Container container, ContainerData containerData,
			CallbackInfo callbackInfo) {
		this.player = Optional.of(inventory.player);
	}

	@Inject(method = "quickMoveStack", at = @At("HEAD"))
	private void setPlayer(Player player, int slotId, CallbackInfoReturnable<ItemStack> callbackInfo) {
		this.player = Optional.of(player);
	}

	@Override
	protected boolean moveItemStackTo(ItemStack itemStack, int fromSlot, int toSlot, boolean beginFromEnd) {
		Slot resultSlot = slots.get(AbstractFurnaceMenu.RESULT_SLOT);
		if (itemStack == resultSlot.getItem()) {
			ForgeEventFactory.firePlayerSmeltedEvent(player.get(), itemStack);
		}
		return super.moveItemStackTo(itemStack, fromSlot, toSlot, beginFromEnd);
	}
}
