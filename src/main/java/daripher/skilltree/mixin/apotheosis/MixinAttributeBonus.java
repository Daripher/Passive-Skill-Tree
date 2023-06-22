package daripher.skilltree.mixin.apotheosis;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.socket.gem.bonus.AttributeBonus;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.util.StepFunction;

@Mixin(value = AttributeBonus.class, remap = false)
public class MixinAttributeBonus {
	@Redirect(method = "read", at = @At(value = "INVOKE", target = "Lshadows/placebo/util/StepFunction;getForStep(I)F"))
	private float amplifyGemPower(StepFunction function, int step, ItemStack gemStack, LootRarity rarity, int facets) {
		float bonus = function.getForStep(step);
		if (!gemStack.hasTag()) return bonus;
		if (!gemStack.getTag().contains("gem_power")) return bonus;
		float gemPower = gemStack.getTag().getFloat("gem_power");
		return bonus * gemPower;
	}
}
