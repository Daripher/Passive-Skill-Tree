package daripher.skilltree.mixin.apotheosis;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.util.ItemHelper;
import daripher.skilltree.util.PlayerHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import shadows.apotheosis.adventure.affix.socket.SocketHelper;
import shadows.apotheosis.adventure.affix.socket.SocketingRecipe;
import shadows.apotheosis.adventure.affix.socket.gem.GemItem;

@Mixin(SocketingRecipe.class)
public class MixinSocketingRecipe {
	private static final String ADDITIONAL_GEMS_TAG = "ADDITIONAL_GEMS";

	@Redirect(method = "matches", at = @At(value = "INVOKE", target = "Lshadows/apotheosis/adventure/affix/socket/SocketHelper;hasEmptySockets(Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean bypassMaximumSockets(ItemStack itemStack, Container container, Level level) {
		if (!(container instanceof PlayerContainer)) {
			return SocketHelper.hasEmptySockets(itemStack);
		}
		var player = ((PlayerContainer) container).getPlayer().orElseThrow(NullPointerException::new);
		return hasEmptySockets(player, itemStack);
	}

	@Inject(method = "assemble", at = @At(value = "HEAD"), cancellable = true)
	private void bypassMaximumSocketsAndApplyGemStrength(Container container, CallbackInfoReturnable<ItemStack> callbackInfo) {
		var result = container.getItem(0).copy();
		if (result.isEmpty()) {
			return;
		}
		result.setCount(1);
		var baseSockets = SocketHelper.getSockets(result);
		var additionalSockets = 0;
		if (container instanceof PlayerContainer playerContainer) {
			var player = ((PlayerContainer) container).getPlayer().orElseThrow(NullPointerException::new);
			additionalSockets = getAdditionalSockets(result, player);
		}
		var sockets = baseSockets + additionalSockets;
		var gems = SocketHelper.getGems(result, sockets);
		for (var socket = 0; socket < gems.size(); socket++) {
			var gem = GemItem.getGem(gems.get(socket));
			if (gem == null) {
				var gemStack = container.getItem(1).copy();
				gemStack = increaseGemPower(gemStack, container);
				gems.set(socket, gemStack);
				break;
			}
		}
		SocketHelper.setGems(result, gems);
		callbackInfo.setReturnValue(result);
	}

	private static int getAdditionalSockets(ItemStack itemStack, Player player) {
		var playerMaxSockets = getMaxSockets(player, itemStack);
		var additionalGems = getAdditionalGems(itemStack);
		var additionalSockets = Math.max(0, playerMaxSockets - additionalGems);
		return additionalSockets;
	}

	private ItemStack increaseGemPower(ItemStack gemStack, Container container) {
		if (!(container instanceof PlayerContainer)) {
			return gemStack;
		}
		var player = ((PlayerContainer) container).getPlayer().orElseThrow(NullPointerException::new);
		var itemStack = container.getItem(0);
		var gemPower = PlayerHelper.getGemPower(player, itemStack);
		if (gemPower == 1) {
			return gemStack;
		}
		gemStack.getOrCreateTag().putFloat("gem_power", (float) gemPower);
		return gemStack;
	}

	private static int getMaxSockets(Player player, ItemStack itemStack) {
		var maxSockets = 0;
		maxSockets += (int) player.getAttributeValue(SkillTreeAttributes.MAXIMUM_SOCKETS.get());
		if (ItemHelper.isChestplate(itemStack)) {
			maxSockets += (int) player.getAttributeValue(SkillTreeAttributes.MAXIMUM_CHESTPLATE_SOCKETS.get());
		}
		if (ItemHelper.isWeapon(itemStack) || ItemHelper.isBow(itemStack)) {
			maxSockets += (int) player.getAttributeValue(SkillTreeAttributes.MAXIMUM_WEAPON_SOCKETS.get());
		}

		return maxSockets;
	}

	private boolean hasEmptySockets(Player player, ItemStack itemStack) {
		var playerSockets = getMaxSockets(player, itemStack);
		var additionalGems = getAdditionalGems(itemStack);
		var sockets = SocketHelper.getSockets(itemStack) + playerSockets - additionalGems;
		return SocketHelper.getGems(itemStack, sockets).stream().map(GemItem::getGem).anyMatch(Objects::isNull);
	}

	private static int getAdditionalGems(ItemStack itemStack) {
		if (!itemStack.hasTag()) {
			return 0;
		}
		if (!itemStack.getTag().contains(ADDITIONAL_GEMS_TAG)) {
			return 0;
		}
		var additionalGems = itemStack.getTag().getInt(ADDITIONAL_GEMS_TAG);
		return additionalGems;
	}
}
