package daripher.skilltree.api.block;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface ExtendedBrewingStand {
	Player getLastUser();
	
	NonNullList<ItemStack> getInventory();

	int getFuel();

	void setFuel(int fuel);

	int getBrewTime();

	void setBrewTime(int brewTime);

	Item getIngredient();

	void setIngredient(Item ingredient);

	boolean[] getPotionCount();

	boolean[] getLastPotionCount();

	void setLastPotionCount(boolean[] lastPotionCount);
}
