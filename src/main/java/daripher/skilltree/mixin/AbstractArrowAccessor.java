package daripher.skilltree.mixin;

import javax.annotation.Nullable;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface AbstractArrowAccessor {
  // Should never be null. Some mods still return null.
  @Nullable
  @Invoker("getPickupItem")
  ItemStack invokeGetPickupItem();
}
