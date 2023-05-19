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
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;

@Mixin(InventoryMenu.class)
public abstract class MixinInventoryMenu extends RecipeBookMenu<CraftingContainer> {
	private @Shadow @Final CraftingContainer craftSlots;
	private @Shadow @Final Player owner;
	private @Shadow @Final ResultContainer resultSlots;

	public MixinInventoryMenu() {
		super(null, 0);
	}

	@Inject(method = "<init>(Lnet/minecraft/world/entity/player/Inventory;ZLnet/minecraft/world/entity/player/Player;)V", at = @At("TAIL"))
	private void setPlayerIntoContainer(Inventory inventory, boolean isServerSide, Player player, CallbackInfo callbackInfo) {
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
		net.minecraftforge.event.ForgeEventFactory.firePlayerCraftingEvent(owner, itemStack, craftSlots);
	}
}
