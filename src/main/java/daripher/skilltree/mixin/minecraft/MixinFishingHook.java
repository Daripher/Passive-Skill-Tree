package daripher.skilltree.mixin.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import daripher.skilltree.init.PSTAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;

@Mixin(FishingHook.class)
public class MixinFishingHook {
	@Redirect(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
	private boolean doubleFishingLoot(Level level, Entity entity) {
		Player player = getPlayerOwner();
		double chance = player.getAttributeValue(PSTAttributes.DOUBLE_FISHING_LOOT_CHANCE.get()) - 1;
		if (entity instanceof ItemEntity item && player.getRandom().nextFloat() < chance) {
			ItemEntity copy = item.copy();
			copy.setDeltaMovement(item.getDeltaMovement());
			level.addFreshEntity(copy);
		}
		return level.addFreshEntity(entity);
	}

	public @Shadow Player getPlayerOwner() {
		return null;
	}
}
