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
	@Redirect(method = "read", at = @At(value = "INVOKE", target = "Lshadows/placebo/util/StepFunction;get(F)F"))
	private float amplifyGemPower(StepFunction function, float level, ItemStack gem, LootRarity rarity) {
		float bonus = function.get(level);
		if (!gem.hasTag()) return bonus;
		if (!gem.getTag().contains("gem_power")) return bonus;
		float power = gem.getTag().getFloat("gem_power") + 1;
		return bonus * power;
	}
}
