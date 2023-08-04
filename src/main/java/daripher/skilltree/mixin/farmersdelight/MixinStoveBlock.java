package daripher.skilltree.mixin.farmersdelight;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.api.PlayerContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import vectorwing.farmersdelight.common.block.StoveBlock;

@Mixin(value = StoveBlock.class)
public class MixinStoveBlock {
	@Inject(method = "use", at = @At("HEAD"))
	private void setCookingPlayer(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit,
			CallbackInfoReturnable<InteractionResult> callback) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof PlayerContainer container) container.setPlayer(player);
	}
}
