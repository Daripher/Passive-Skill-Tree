package daripher.skilltree.mixin.minecraft;

import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import daripher.skilltree.container.ContainerHelper;
import daripher.skilltree.util.FoodHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;

@Mixin(CampfireBlockEntity.class)
public class MixinCampfireBlockEntity {
	private @Shadow @Final NonNullList<ItemStack> items;

	@Redirect(method = "cookTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"))
	private static void setCookedFoodBonuses(Level level, double x, double y, double z, ItemStack itemStack) {
		BlockPos pos = new BlockPos((int) x, (int) y, (int) z);
		BlockEntity blockEntity = level.getBlockEntity(pos);
		Optional<Player> player = ContainerHelper.getViewingPlayer(blockEntity);
		if (!player.isPresent()) return;
		FoodHelper.setCraftedFoodBonuses(itemStack, player.get());
		Containers.dropItemStack(level, x, y, z, itemStack);
	}
}
