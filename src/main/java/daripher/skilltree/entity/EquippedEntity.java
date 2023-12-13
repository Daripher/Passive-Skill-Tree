package daripher.skilltree.entity;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface EquippedEntity {
  boolean hasItemEquipped(ItemStack stack);

  default boolean hasItemEquipped(ItemEntity entity) {
    return hasItemEquipped(entity.getItem());
  }
}
