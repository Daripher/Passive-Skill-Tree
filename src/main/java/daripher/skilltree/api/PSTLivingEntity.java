package daripher.skilltree.api;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface PSTLivingEntity {
	boolean hadEquipped(ItemStack stack);
	
	default boolean hadEquipped(ItemEntity entity) {
		return hadEquipped(entity.getItem());
	}
}
