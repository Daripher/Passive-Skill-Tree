package daripher.skilltree.mixin.apotheosis;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import shadows.apotheosis.adventure.affix.socket.gem.bonus.EnchantmentBonus;
import shadows.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import shadows.apotheosis.adventure.loot.LootRarity;

@Mixin(EnchantmentBonus.class)
public abstract class MixinEnchantmentBonus extends GemBonus {
	protected @Shadow @Final Enchantment ench;
	protected @Shadow @Final boolean mustExist;
	protected @Shadow @Final boolean global;
	protected @Shadow @Final Map<LootRarity, Integer> values;

	private MixinEnchantmentBonus() {
		super(null, null);
	}

	@Inject(method = "getSocketBonusTooltip", at = @At("HEAD"), cancellable = true)
	private void applyGemPowerVisually(ItemStack gem, LootRarity rarity, int facets, CallbackInfoReturnable<Component> callbackInfo) {
		if (!gem.hasTag()) {
			return;
		}
		if (!gem.getTag().contains("gem_power")) {
			return;
		}
		var gemPower = gem.getTag().getFloat("gem_power");
		var level = (int) (values.get(rarity) * gemPower);
		var descriptionId = "bonus." + getId() + ".desc";
		if (global) {
			descriptionId += ".global";
		} else if (mustExist) {
			descriptionId += ".mustExist";
		}
		var enchantmentName = Component.translatable(ench.getDescriptionId());
		var style = ench.getFullname(0).getStyle();
		if (style.getColor().getValue() != ChatFormatting.GRAY.getColor()) {
			enchantmentName.withStyle(style);
		}
		var levelTooltip = Component.translatable("misc.apotheosis.level" + (level > 1 ? ".many" : ""));
		var tooltip = Component.translatable(descriptionId, level, levelTooltip, enchantmentName).withStyle(ChatFormatting.GREEN);
		callbackInfo.setReturnValue(tooltip);
	}

	@Inject(method = "getEnchantmentLevels", at = @At("HEAD"), cancellable = true)
	private void applyGemPower(ItemStack gem, LootRarity rarity, int facets, Map<Enchantment, Integer> enchantments, CallbackInfo callbackInfo) {
		if (!gem.hasTag()) {
			return;
		}
		if (!gem.getTag().contains("gem_power")) {
			return;
		}
		var gemPower = gem.getTag().getFloat("gem_power");
		var level = (int) (values.get(rarity) * gemPower);
		if (global) {
			for (Enchantment e : enchantments.keySet()) {
				enchantments.computeIfPresent(e, (key, val) -> val > 0 ? val + level : 0);
			}
		} else if (mustExist) {
			enchantments.computeIfPresent(ench, (key, val) -> val > 0 ? val + level : 0);
		} else {
			enchantments.merge(ench, level, Integer::sum);
		}
		callbackInfo.cancel();
	}
}
