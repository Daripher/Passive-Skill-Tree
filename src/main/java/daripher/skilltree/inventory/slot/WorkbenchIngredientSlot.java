package daripher.skilltree.inventory.slot;

import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class WorkbenchIngredientSlot extends Slot {
    private final WorkbenchContainer container;
    private final int ingredientIndex;

    public WorkbenchIngredientSlot(WorkbenchContainer container, int index, int x, int y, int ingredientIndex) {
        super(container, index, x, y);
        this.container = container;
        this.ingredientIndex = ingredientIndex;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        AbstractWorkbenchRecipe selectedRecipe = container.menu.getSelectedRecipe();
        if (selectedRecipe == null) {
            return false;
        }
        if (!selectedRecipe.isValidIngredient(itemStack)) {
            return false;
        }
        ItemStack baseItemStack = container.getBaseItem();
        Map<Ingredient, Integer> additionalIngredients = selectedRecipe.getAdditionalIngredients(baseItemStack);
        List<Map.Entry<Ingredient, Integer>> requiredIngredients = additionalIngredients.entrySet().stream().toList();
        if (ingredientIndex >= requiredIngredients.size()) {
            return false;
        }
        Ingredient requiredIngredient = requiredIngredients.get(ingredientIndex).getKey();
        return requiredIngredient.test(itemStack);
    }
}
