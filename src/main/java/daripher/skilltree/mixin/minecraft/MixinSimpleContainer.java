package daripher.skilltree.mixin.minecraft;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;

import daripher.skilltree.api.PlayerContainer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;

@Mixin(SimpleContainer.class)
public class MixinSimpleContainer implements PlayerContainer {
	private Optional<Player> player = Optional.empty();

	@Override
	public Optional<Player> getViewingPlayer() {
		return player;
	}

	@Override
	public void setViewingPlayer(Optional<Player> player) {
		this.player = player;
	}
}
