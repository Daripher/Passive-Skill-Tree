package daripher.skilltree.mixin.easyvillagers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.util.PotionHelper;
import de.maxhenkel.easyvillagers.blocks.tileentity.ConverterTileentity;
import net.minecraft.world.item.ItemStack;

@Mixin(value = ConverterTileentity.class, remap = false)
public class MixinConverterTileentity {
	@Inject(method = "isWeakness", at = @At("HEAD"), cancellable = true)
	private static void isSuperiorWeakness(ItemStack stack, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (!PotionHelper.isSuperiorPotion(stack)) return;
		ItemStack potionStack = PotionHelper.getActualPotionStack(stack);
		callbackInfo.setReturnValue(isWeakness(potionStack));
	}

	private static @Shadow boolean isWeakness(ItemStack potion) {
		return false;
	}
}
