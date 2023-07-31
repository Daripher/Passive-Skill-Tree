package daripher.skilltree.mixin.apotheosis;

import java.util.List;
import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.util.PlayerHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import shadows.apotheosis.adventure.affix.socket.SocketHelper;
import shadows.apotheosis.adventure.affix.socket.SocketingRecipe;
import shadows.apotheosis.adventure.affix.socket.gem.Gem;
import shadows.apotheosis.adventure.affix.socket.gem.GemItem;

@Mixin(value = SocketingRecipe.class, remap = false)
public class MixinSocketingRecipe {
	private static final String ADDITIONAL_GEMS_TAG = "ADDITIONAL_GEMS";

	@Redirect(method = {
		"matches",
		"m_5818_"
	}, at = @At(value = "INVOKE", target = "Lshadows/apotheosis/adventure/affix/socket/SocketHelper;hasEmptySockets(Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean bypassMaximumSockets(ItemStack itemStack, Container container, Level level) {
		if (!(container instanceof PlayerContainer)) return SocketHelper.hasEmptySockets(itemStack);
		PlayerContainer playerContainer = (PlayerContainer) container;
		if (playerContainer.getPlayer().isEmpty()) return SocketHelper.hasEmptySockets(itemStack);
		Player player = playerContainer.getPlayer().orElseThrow(NullPointerException::new);
		return hasEmptySockets(player, itemStack);
	}

	@Inject(method = {
		"assemble",
		"m_5874_"
	}, at = @At(value = "HEAD"), cancellable = true)
	private void bypassMaximumSocketsAndApplyGemStrength(Container container, CallbackInfoReturnable<ItemStack> callbackInfo) {
		ItemStack result = container.getItem(0).copy();
		if (result.isEmpty()) return;
		result.setCount(1);
		int baseSockets = SocketHelper.getSockets(result);
		int additionalSockets = 0;
		if (container instanceof PlayerContainer playerContainer && playerContainer.getPlayer().isPresent()) {
			Player player = ((PlayerContainer) container).getPlayer().orElseThrow(NullPointerException::new);
			additionalSockets = getAdditionalSockets(result, player);
		}
		int sockets = baseSockets + additionalSockets;
		List<ItemStack> gems = SocketHelper.getGems(result, sockets);
		for (int socket = 0; socket < gems.size(); socket++) {
			Gem gem = GemItem.getGem(gems.get(socket));
			if (gem == null) {
				ItemStack gemStack = container.getItem(1).copy();
				gemStack = increaseGemPower(gemStack, container);
				gems.set(socket, gemStack);
				break;
			}
		}
		SocketHelper.setGems(result, gems);
		callbackInfo.setReturnValue(result);
	}

	private static int getAdditionalSockets(ItemStack itemStack, Player player) {
		int playerMaxSockets = getMaxSockets(player, itemStack);
		int additionalGems = getAdditionalGems(itemStack);
		int additionalSockets = Math.max(0, playerMaxSockets - additionalGems);
		return additionalSockets;
	}

	private ItemStack increaseGemPower(ItemStack gem, Container container) {
		if (!(container instanceof PlayerContainer)) return gem;
		PlayerContainer playerContainer = (PlayerContainer) container;
		if (playerContainer.getPlayer().isEmpty()) return gem;
		Player player = playerContainer.getPlayer().orElseThrow(NullPointerException::new);
		ItemStack result = container.getItem(0);
		float power = PlayerHelper.getGemPower(player, result);
		if (power == 1) return gem;
		gem.getOrCreateTag().putFloat("gem_power", (float) power);
		return gem;
	}

	private static int getMaxSockets(Player player, ItemStack itemStack) {
		int maxSockets = 0;
		maxSockets += (int) player.getAttributeValue(PSTAttributes.MAXIMUM_EQUIPMENT_SOCKETS.get());
		if (ItemHelper.isChestplate(itemStack)) {
			Attribute chestplateSockets = PSTAttributes.MAXIMUM_CHESTPLATE_SOCKETS.get();
			maxSockets += (int) player.getAttributeValue(chestplateSockets);
		}
		if (ItemHelper.isWeapon(itemStack)) {
			Attribute weaponSockets = PSTAttributes.MAXIMUM_WEAPON_SOCKETS.get();
			maxSockets += (int) player.getAttributeValue(weaponSockets);
		}
		return maxSockets;
	}

	private boolean hasEmptySockets(Player player, ItemStack itemStack) {
		int playerSockets = getMaxSockets(player, itemStack);
		int additionalGems = getAdditionalGems(itemStack);
		int sockets = SocketHelper.getSockets(itemStack) + playerSockets - additionalGems;
		return SocketHelper.getGems(itemStack, sockets).stream().map(GemItem::getGem).anyMatch(Objects::isNull);
	}

	private static int getAdditionalGems(ItemStack itemStack) {
		if (!itemStack.hasTag()) return 0;
		if (!itemStack.getTag().contains(ADDITIONAL_GEMS_TAG)) return 0;
		int additionalGems = itemStack.getTag().getInt(ADDITIONAL_GEMS_TAG);
		return additionalGems;
	}
}
