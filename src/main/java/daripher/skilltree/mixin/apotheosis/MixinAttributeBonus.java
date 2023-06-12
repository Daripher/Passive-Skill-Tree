package daripher.skilltree.mixin.apotheosis;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.socket.gem.GemItem;
import shadows.apotheosis.adventure.affix.socket.gem.bonus.AttributeBonus;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.util.StepFunction;

@Mixin(value = AttributeBonus.class, remap = false)
public class MixinAttributeBonus {
	protected @Shadow @Final Operation operation;
	protected @Shadow @Final Map<LootRarity, StepFunction> values;

	@Inject(method = "read", at = @At("HEAD"), cancellable = true)
	private void applyGemPower(ItemStack gem, LootRarity rarity, int facets, CallbackInfoReturnable<AttributeModifier> callbackInfo) {
		if (!gem.hasTag()) {
			return;
		}
		if (!gem.getTag().contains("gem_power")) {
			return;
		}
		var gemPower = gem.getTag().getFloat("gem_power");
		var bonus = values.get(rarity).getForStep(facets) * gemPower;
		var modifier = new AttributeModifier(GemItem.getUUIDs(gem).get(0), "apoth.gem_modifier", bonus, operation);
		callbackInfo.setReturnValue(modifier);
	}
}
