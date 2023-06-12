package daripher.skilltree.mixin.apotheosis;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.util.ItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.socket.SocketHelper;
import shadows.apotheosis.adventure.client.AdventureModuleClient;

@Mixin(AdventureModuleClient.class)
public class MixinAdventureModuleClient {
	private static final String ADDITIONAL_GEMS_TAG = "ADDITIONAL_GEMS";

	@Redirect(method = "comps", at = @At(value = "INVOKE", target = "Lshadows/apotheosis/adventure/affix/socket/SocketHelper;getGems(Lnet/minecraft/world/item/ItemStack;I)Ljava/util/List;"))
	private static List<ItemStack> showAdditionalSockets(ItemStack itemStack, int size) {
		var minecraft = Minecraft.getInstance();
		var player = minecraft.player;
		var additionalSockets = getAdditionalSockets(itemStack, player);
		return SocketHelper.getGems(itemStack, size + additionalSockets);
	}

	private static int getAdditionalSockets(ItemStack itemStack, Player player) {
		var playerMaxSockets = getMaxSockets(player, itemStack);
		var additionalGems = getAdditionalGems(itemStack);
		var additionalSockets = Math.max(0, playerMaxSockets - additionalGems);
		return additionalSockets;
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
}
