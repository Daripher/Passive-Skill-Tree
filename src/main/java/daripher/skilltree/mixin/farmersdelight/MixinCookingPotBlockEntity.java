package daripher.skilltree.mixin.farmersdelight;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.util.FoodHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@Mixin(value = CookingPotBlockEntity.class, remap = false)
public class MixinCookingPotBlockEntity {
	private @Nullable Player player;

	@Inject(method = { "createMenu", "m_7208_" }, at = @At("HEAD"))
	private void setCookingPlayer(int id, Inventory inventory, Player player, CallbackInfoReturnable<AbstractContainerMenu> callbackInfo) {
		this.player = player;
	}

	@Redirect(method = "processCooking", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/crafting/CookingPotRecipe;getResultItem()Lnet/minecraft/world/item/ItemStack;", remap = true))
	private ItemStack setCookedFoodBonuses(CookingPotRecipe recipe) {
		ItemStack result = recipe.getResultItem();
		if (player == null) return result;
		FoodHelper.setCraftedFoodBonuses(result, player);
		return result;
	}
}
