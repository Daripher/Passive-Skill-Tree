package daripher.skilltree.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import daripher.skilltree.api.player.JewelerPlayer;
import daripher.skilltree.api.recipe.PlayerRequiringRecipe;
import daripher.skilltree.init.SkillTreeItems;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.UpgradeRecipe;

@Mixin(SmithingMenu.class)
public abstract class MixinSmithingMenu extends ItemCombinerMenu {
	private Player currentPlayer;

	public MixinSmithingMenu() {
		super(null, 0, null, null);
	}

	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
	private void init(int windowId, Inventory inventory, ContainerLevelAccess levelAccess, CallbackInfo callbackInfo) {
		this.currentPlayer = inventory.player;
	}

	@ModifyVariable(method = "createResult", at = @At("STORE"), ordinal = 0)
	private List<UpgradeRecipe> setPlayerForRecipes(List<UpgradeRecipe> recipes) {
		recipes.forEach(this::setPlayerIfNeeded);
		return recipes;
	}

	@Inject(method = "onTake", at = @At("HEAD"))
	private void changeRainbowJewelInsertionSeed(Player player, ItemStack itemStack, CallbackInfo callbackInfo) {
		if (inputSlots.getItem(1).getItem() != SkillTreeItems.RAINBOW_GEMSTONE.get()) {
			return;
		}
		if (!(player instanceof JewelerPlayer)) {
			return;
		}
		var jewelerPlayer = (JewelerPlayer) player;
		jewelerPlayer.rainbowJewelInserted();
	}

	private void setPlayerIfNeeded(UpgradeRecipe upgradeRecipe) {
		if (upgradeRecipe instanceof PlayerRequiringRecipe recipe) {
			recipe.setCurrentPlayer(currentPlayer);
		}
	}
}
