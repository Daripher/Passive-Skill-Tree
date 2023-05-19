package daripher.skilltree.mixin.minecraft;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import daripher.skilltree.api.PlayerContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;

@Mixin(CraftingMenu.class)
public abstract class MixinCraftingMenu extends RecipeBookMenu<CraftingContainer> {
	private @Shadow @Final CraftingContainer craftSlots;
	private @Shadow @Final Player player;
	private @Shadow @Final ResultContainer resultSlots;

	public MixinCraftingMenu() {
		super(null, 0);
	}

	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
	private void setPlayerIntoContainer(int windowId, Inventory inventory, ContainerLevelAccess levelAccess, CallbackInfo callbackInfo) {
		((PlayerContainer) craftSlots).setPlayer(player);
	}

	@Override
	protected boolean moveItemStackTo(ItemStack itemStack, int fromSlot, int toSlot, boolean beginFromEnd) {
		if (itemStack == resultSlots.getItem(0)) {
			fireItemCraftedEvent(itemStack);
		}
		return super.moveItemStackTo(itemStack, fromSlot, toSlot, beginFromEnd);
	}

	private void fireItemCraftedEvent(ItemStack itemStack) {
		net.minecraftforge.event.ForgeEventFactory.firePlayerCraftingEvent(player, itemStack, craftSlots);
	}
}
