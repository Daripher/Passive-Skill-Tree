package daripher.skilltree.mixin.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.util.PotionHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;

@Mixin(VanillaBrewingRecipe.class)
public class MixinVanillaBrewingRecipe {
	@Inject(method = "isInput", at = @At("HEAD"), cancellable = true)
	private void isInput(ItemStack stack, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (PotionHelper.isSuperiorPotion(stack)) {
			callbackInfo.setReturnValue(false);
		}
	}

	@Inject(method = "getOutput", at = @At("HEAD"), cancellable = true)
	private void getOutput(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<ItemStack> callbackInfo) {
		if (PotionHelper.isSuperiorPotion(input)) {
			callbackInfo.setReturnValue(ItemStack.EMPTY);
		}
	}
}
