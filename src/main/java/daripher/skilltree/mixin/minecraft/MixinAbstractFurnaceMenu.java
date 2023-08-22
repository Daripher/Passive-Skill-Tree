package daripher.skilltree.mixin.minecraft;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;

import daripher.skilltree.api.PlayerContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;

@Mixin(AbstractFurnaceMenu.class)
public abstract class MixinAbstractFurnaceMenu extends RecipeBookMenu<Container> {
	private MixinAbstractFurnaceMenu() {
		super(null, 0);
	}

	@Override
	protected boolean moveItemStackTo(ItemStack itemStack, int fromSlot, int toSlot, boolean beginFromEnd) {
		Slot resultSlot = slots.get(AbstractFurnaceMenu.RESULT_SLOT);
		if (itemStack == resultSlot.getItem()) {
			Optional<Player> viewingPlayer = ((PlayerContainer) (Object) this).getViewingPlayer();
			if (viewingPlayer.isPresent()) {
				ForgeEventFactory.firePlayerSmeltedEvent(viewingPlayer.get(), itemStack);
			}
		}
		return super.moveItemStackTo(itemStack, fromSlot, toSlot, beginFromEnd);
	}
}
