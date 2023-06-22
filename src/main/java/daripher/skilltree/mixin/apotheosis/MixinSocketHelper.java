package daripher.skilltree.mixin.apotheosis;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.gem.GemHelper;
import daripher.skilltree.item.ItemHelper;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.Apoth.Affixes;
import shadows.apotheosis.adventure.affix.AffixHelper;
import shadows.apotheosis.adventure.affix.AffixInstance;
import shadows.apotheosis.adventure.affix.socket.SocketHelper;

@Mixin(value = SocketHelper.class, remap = false)
public class MixinSocketHelper {
	private static final String ADDITIONAL_SOCKET_TAG = "ADDITIONAL_SOCKET";
	private static final String ADDITIONAL_GEMS_TAG = "ADDITIONAL_GEMS";

	@ModifyVariable(method = "getGems(Lnet/minecraft/world/item/ItemStack;I)Ljava/util/List;", at = @At("HEAD"), ordinal = 0)
	private static int applyAdditionalGems(int sockets, ItemStack stack, int sockets_) {
		if (!stack.hasTag()) return sockets;
		if (!stack.getTag().contains(ADDITIONAL_GEMS_TAG)) return sockets;
		int additionalGems = stack.getTag().getInt(ADDITIONAL_GEMS_TAG);
		return sockets + additionalGems;
	}

	@Inject(method = "setGems", at = @At("HEAD"), cancellable = true)
	private static void setAdditionalGems(ItemStack stack, List<ItemStack> gems, CallbackInfo callbackInfo) {
		int sockets = getSockets(stack);
		if (gems.size() <= sockets) {
			if (!stack.hasTag()) return;
			if (!stack.getTag().contains(ADDITIONAL_GEMS_TAG)) return;
			stack.getOrCreateTag().remove(ADDITIONAL_GEMS_TAG);
			return;
		}
		int additionalGems = gems.size() - sockets;
		stack.getOrCreateTag().putInt(ADDITIONAL_GEMS_TAG, additionalGems);
	}

	@Inject(method = "getSockets", at = @At("HEAD"))
	private static void addAdditionalSockets(ItemStack stack, CallbackInfoReturnable<Integer> callbackInfo) {
		if (!ItemHelper.isEquipment(stack)) return;
		AffixInstance socketAffix = AffixHelper.getAffixes(stack).get(Affixes.SOCKET.get());
		if (socketAffix == null) SocketHelper.setSockets(stack, 1);
		if (GemHelper.hasAdditionalSocket(stack) && !hasAdditionalSocket(stack)) {
			setHasAdditionalSocket(stack);
			int sockets = (int) AffixHelper.getAffixes(stack).get(Affixes.SOCKET.get()).level();
			SocketHelper.setSockets(stack, sockets + 1);
		}
	}

	private static boolean hasAdditionalSocket(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains(ADDITIONAL_SOCKET_TAG);
	}

	private static void setHasAdditionalSocket(ItemStack stack) {
		stack.getOrCreateTag().putBoolean(ADDITIONAL_SOCKET_TAG, true);
	}

	private static @Shadow int getSockets(ItemStack stack) {
		return 0;
	}

	private static @Shadow List<ItemStack> getGems(ItemStack stack, int size) {
		return null;
	}
}
