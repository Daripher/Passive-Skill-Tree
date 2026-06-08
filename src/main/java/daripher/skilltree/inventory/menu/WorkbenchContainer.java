package daripher.skilltree.inventory.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.Map;

public class WorkbenchContainer extends TransientCraftingContainer {
    public final WorkbenchMenu menu;

    public WorkbenchContainer(WorkbenchMenu menu) {
        super(menu, 5, 2);
        this.menu = menu;
    }

    public Player getPlayer() {
        return menu.getPlayer();
    }

    public ItemStack getBaseItem() {
        return getItem(0);
    }

    public boolean hasIngredients(Map<Ingredient, Integer> ingredients) {
        Map<Ingredient, Integer> remaining = new HashMap<>(ingredients);
        for (int i = 1; i < getContainerSize(); i++) {
            ItemStack item = getItem(i);
            if (item.isEmpty()) {
                continue;
            }
            for (Map.Entry<Ingredient, Integer> entry : new HashMap<>(remaining).entrySet()) {
                Ingredient ingredient = entry.getKey();
                int needed = entry.getValue();
                if (needed <= 0) {
                    continue;
                }
                if (ingredient.test(item)) {
                    int available = item.getCount();
                    if (available >= needed) {
                        remaining.put(ingredient, 0);
                    } else {
                        remaining.put(ingredient, needed - available);
                    }
                }
            }
            if (remaining.values().stream().allMatch(count -> count <= 0)) {
                return true;
            }
        }
        return remaining.values().stream().allMatch(count -> count <= 0);
    }
}
