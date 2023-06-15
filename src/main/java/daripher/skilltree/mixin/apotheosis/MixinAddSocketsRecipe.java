package daripher.skilltree.mixin.apotheosis;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import daripher.skilltree.gem.GemHelper;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.socket.AddSocketsRecipe;
import shadows.apotheosis.adventure.affix.socket.SocketHelper;

@Mixin(value = AddSocketsRecipe.class, remap = false)
public class MixinAddSocketsRecipe {
	@Redirect(method = { "matches", "m_5818_" }, at = @At(value = "INVOKE", target = "Lshadows/apotheosis/adventure/affix/socket/SocketHelper;getSockets(Lnet/minecraft/world/item/ItemStack;)I"))
	private int getSocketsWithoutAdditional(ItemStack itemStack) {
		var sockets = SocketHelper.getSockets(itemStack);
		if (GemHelper.hasAdditionalSocket(itemStack)) sockets--;
		return sockets;
	}
}
