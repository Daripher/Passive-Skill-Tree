package daripher.skilltree.mixin.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(Item.class)
public class MixinItem {
	@Redirect(method = "getBarWidth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getMaxDamage(Lnet/minecraft/world/item/ItemStack;)I"))
	private int applyBonusDurability(Item item, ItemStack itemStack) {
		return itemStack.getMaxDamage();
	}
}
