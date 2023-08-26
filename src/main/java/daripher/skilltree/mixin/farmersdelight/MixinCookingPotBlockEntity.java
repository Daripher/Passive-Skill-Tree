package daripher.skilltree.mixin.farmersdelight;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import daripher.skilltree.container.ContainerHelper;
import daripher.skilltree.util.FoodHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@Mixin(value = CookingPotBlockEntity.class, remap = false)
public class MixinCookingPotBlockEntity {
	@Redirect(method = "processCooking", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/crafting/CookingPotRecipe;getResultItem()Lnet/minecraft/world/item/ItemStack;", remap = true))
	private ItemStack setCookedFoodBonuses(CookingPotRecipe recipe) {
		ItemStack result = recipe.getResultItem();
		Optional<Player> player = ContainerHelper.getViewingPlayer((CookingPotBlockEntity) (Object) this);
		if (!player.isPresent()) return result;
		FoodHelper.setCraftedFoodBonuses(result, player.get());
		return result;
	}
}
