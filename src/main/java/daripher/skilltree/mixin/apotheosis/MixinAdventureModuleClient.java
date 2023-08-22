package daripher.skilltree.mixin.apotheosis;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.client.AdventureModuleClient;

@Mixin(value = AdventureModuleClient.class, remap = false)
public class MixinAdventureModuleClient {
	@Redirect(method = "comps", at = @At(value = "INVOKE", target = "Lshadows/apotheosis/adventure/affix/socket/SocketHelper;getGems(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
	private static List<ItemStack> showPlayerSockets(ItemStack stack) {
		Minecraft minecraft = Minecraft.getInstance();
		int sockets = ApotheosisCompatibility.ISNTANCE.getSockets(stack, minecraft.player);
		return ApotheosisCompatibility.ISNTANCE.getGems(stack, sockets);
	}

	@Redirect(method = "comps", at = @At(value = "INVOKE", target = "Lshadows/apotheosis/adventure/affix/socket/SocketHelper;getSockets(Lnet/minecraft/world/item/ItemStack;)I"))
	private static int addPlayerSockets(ItemStack stack) {
		return ApotheosisCompatibility.ISNTANCE.getSockets(stack, Minecraft.getInstance().player);
	}

	@Redirect(method = "tooltips", at = @At(value = "INVOKE", target = "Lshadows/apotheosis/adventure/affix/socket/SocketHelper;getSockets(Lnet/minecraft/world/item/ItemStack;)I"))
	private static int addPlayerSockets2(ItemStack stack) {
		return ApotheosisCompatibility.ISNTANCE.getSockets(stack, Minecraft.getInstance().player);
	}
}
