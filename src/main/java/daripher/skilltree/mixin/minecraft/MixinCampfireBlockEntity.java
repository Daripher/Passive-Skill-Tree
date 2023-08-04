package daripher.skilltree.mixin.minecraft;

import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.util.FoodHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;

@Mixin(CampfireBlockEntity.class)
public class MixinCampfireBlockEntity implements PlayerContainer {
	private @Shadow @Final NonNullList<ItemStack> items;
	private @Nullable Player player;

	@Inject(method = "placeFood", at = @At("HEAD"))
	private void setCookingPlayer(@Nullable Entity entity, ItemStack itemStack, int cookingTime, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (entity instanceof Player || entity == null) player = (Player) entity;
	}

	@Redirect(method = "cookTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"))
	private static void setCookedFoodBonuses(Level level, double x, double y, double z, ItemStack itemStack) {
		var pos = new BlockPos((int)x, (int)y, (int)z);
		BlockEntity blockEntity = level.getBlockEntity(pos);
		var player = (Player) null;
		if (blockEntity instanceof PlayerContainer playerContainer) {
			player = playerContainer.getPlayer().orElse(null);
		}
		if (player == null) return;
		FoodHelper.setCraftedFoodBonuses(itemStack, player);
		Containers.dropItemStack(level, x, y, z, itemStack);
	}

	@Override
	public Optional<Player> getPlayer() {
		return Optional.ofNullable(player);
	}

	@Override
	public void setPlayer(Player player) {
		this.player = player;
	}
}
