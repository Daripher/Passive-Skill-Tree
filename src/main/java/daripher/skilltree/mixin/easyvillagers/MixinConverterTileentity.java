package daripher.skilltree.mixin.easyvillagers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daripher.skilltree.util.PotionHelper;
import de.maxhenkel.easyvillagers.blocks.tileentity.ConverterTileentity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;

@Mixin(value = ConverterTileentity.class, remap = false)
public class MixinConverterTileentity {
	@Inject(method = "isWeakness", at = @At("HEAD"), cancellable = true)
	private static void isWeakness(ItemStack stack, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (!PotionHelper.isSuperiorPotion(stack)) {
			return;
		}
		var actualPotion = PotionHelper.getActualPotion(stack);
		var isWeaknessPotion = actualPotion == Potions.WEAKNESS || actualPotion == Potions.LONG_WEAKNESS;
		if (isWeaknessPotion) {
			callbackInfo.setReturnValue(true);
		}
	}
}
