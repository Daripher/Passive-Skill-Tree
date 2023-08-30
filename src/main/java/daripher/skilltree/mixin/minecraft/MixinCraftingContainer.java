package daripher.skilltree.mixin.minecraft;

import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import daripher.skilltree.api.PlayerContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;

@Mixin(TransientCraftingContainer.class)
public class MixinCraftingContainer implements PlayerContainer {
	private @Shadow @Final AbstractContainerMenu menu;

	@Override
	public Optional<Player> getViewingPlayer() {
		return ((PlayerContainer) menu).getViewingPlayer();
	}

	@Override
	public void setViewingPlayer(Optional<Player> player) {
	}
}
