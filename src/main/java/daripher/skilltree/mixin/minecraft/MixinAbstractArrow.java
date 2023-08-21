package daripher.skilltree.mixin.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;

@Mixin(AbstractArrow.class)
public abstract class MixinAbstractArrow {
	protected @Shadow boolean inGround;

	@Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
	private void pickupAsItem(Player player, CallbackInfo callback) {
		AbstractArrow arrow = (AbstractArrow) (Object) this;
		if (!arrow.level().isClientSide && (inGround || arrow.isNoPhysics()) && arrow.shakeTime <= 0) {
			if (arrow.pickup == Pickup.ALLOWED) {
				ItemEntity item = new ItemEntity(arrow.level(), player.getX(), player.getY(), player.getZ(), getPickupItem().copy());
				arrow.level().addFreshEntity(item);
				item.setPickUpDelay(0);
				arrow.discard();
				callback.cancel();
			}
		}
	}

	protected abstract @Shadow ItemStack getPickupItem();
}
