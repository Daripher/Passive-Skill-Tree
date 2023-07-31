package daripher.skilltree.mixin.apotheosis;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.item.ItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.client.AdventureModuleClient;

@Mixin(value = AdventureModuleClient.class, remap = false)
public class MixinAdventureModuleClient {
	private static final String ADDITIONAL_GEMS_TAG = "ADDITIONAL_GEMS";

	@ModifyArg(method = "comps", at = @At(value = "INVOKE", target = "Lshadows/apotheosis/adventure/affix/socket/SocketHelper;getGems(Lnet/minecraft/world/item/ItemStack;I)Ljava/util/List;"), index = 1)
	private static int showAdditionalSockets(ItemStack itemStack, int sockets) {
		var minecraft = Minecraft.getInstance();
		if (minecraft == null || minecraft.player == null) return sockets;
		int additionalSockets = getAdditionalSockets(itemStack, minecraft.player);
		return sockets + additionalSockets;
	}

	private static int getAdditionalSockets(ItemStack itemStack, Player player) {
		int playerMaxSockets = getMaxSockets(player, itemStack);
		int additionalGems = getAdditionalGems(itemStack);
		int additionalSockets = Math.max(0, playerMaxSockets - additionalGems);
		return additionalSockets;
	}

	private static int getAdditionalGems(ItemStack itemStack) {
		if (!itemStack.hasTag()) return 0;
		return itemStack.getTag().getInt(ADDITIONAL_GEMS_TAG);
	}

	private static int getMaxSockets(Player player, ItemStack itemStack) {
		int maxSockets = (int) player.getAttributeValue(PSTAttributes.MAXIMUM_EQUIPMENT_SOCKETS.get());
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
}
