package daripher.skilltree.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;

import daripher.skilltree.api.player.PlayerContainer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;

@Mixin(SimpleContainer.class)
public class MixinSimpleContainer implements PlayerContainer {
	private Optional<Player> player = Optional.empty();

	@Override
	public Optional<Player> getPlayer() {
		return player;
	}

	@Override
	public void setPlayer(Player player) {
		this.player = Optional.of(player);
	}
}
