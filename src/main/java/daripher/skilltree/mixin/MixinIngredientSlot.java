package daripher.skilltree.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.api.recipe.SkillRequiringBrewingRecipes;
import net.minecraft.world.item.ItemStack;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$IngredientsSlot")
public class MixinIngredientSlot {
	@Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
	public void mayPlace(ItemStack itemStack, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (SkillRequiringBrewingRecipes.isValidIngredient(itemStack)) {
			callbackInfo.setReturnValue(true);
		}
	}
}
