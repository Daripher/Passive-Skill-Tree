package daripher.skilltree.inventory.slot;

import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WorkbenchBaseSlot extends Slot {
    private final WorkbenchContainer container;

    public WorkbenchBaseSlot(WorkbenchContainer container, int index, int x, int y) {
        super(container, index, x, y);
        this.container = container;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        AbstractWorkbenchRecipe selectedRecipe = container.menu.getSelectedRecipe();
        if (selectedRecipe == null) {
            return true;
        }
        return selectedRecipe.isValidBaseItem(itemStack);
    }
}
