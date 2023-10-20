package daripher.skilltree.api;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface EquipmentContainer {
  boolean equipped(ItemStack stack);

  default boolean equipped(ItemEntity entity) {
    return equipped(entity.getItem());
  }
}
