package daripher.skilltree.mixin.minecraft;

import java.util.Optional;

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
import net.minecraftforge.event.ForgeEventFactory;

@Mixin(CraftingMenu.class)
public abstract class MixinCraftingMenu extends RecipeBookMenu<CraftingContainer> implements PlayerContainer {
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
			ForgeEventFactory.firePlayerCraftingEvent(player, itemStack, craftSlots);
		}
		return super.moveItemStackTo(itemStack, fromSlot, toSlot, beginFromEnd);
	}

	@Override
	public Optional<Player> getPlayer() {
		return Optional.ofNullable(player);
	}

	@Override
	public void setPlayer(Player player) {
		this.player = player;
	}
}
