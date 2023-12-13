package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.container.InteractiveContainer;
import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractFurnaceMenu.class)
public abstract class AbstractFurnaceMenuMixin extends RecipeBookMenu<Container> {
  @SuppressWarnings("DataFlowIssue")
  private AbstractFurnaceMenuMixin() {
    super(null, 0);
  }

  @Override
  protected boolean moveItemStackTo(
      @NotNull ItemStack itemStack, int fromSlot, int toSlot, boolean beginFromEnd) {
    Slot resultSlot = slots.get(AbstractFurnaceMenu.RESULT_SLOT);
    if (itemStack == resultSlot.getItem()) {
      Optional<Player> viewingPlayer = ((InteractiveContainer) this).getUser();
      viewingPlayer.ifPresent(
          player -> ForgeEventFactory.firePlayerSmeltedEvent(player, itemStack));
    }
    return super.moveItemStackTo(itemStack, fromSlot, toSlot, beginFromEnd);
  }
}
