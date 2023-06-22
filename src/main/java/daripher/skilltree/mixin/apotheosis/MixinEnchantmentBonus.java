package daripher.skilltree.mixin.apotheosis;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import shadows.apotheosis.adventure.affix.socket.gem.bonus.EnchantmentBonus;
import shadows.apotheosis.adventure.loot.LootRarity;

@Mixin(value = EnchantmentBonus.class, remap = false)
public class MixinEnchantmentBonus {
	@Redirect(method = "getSocketBonusTooltip", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object amplifyGemPowerVisually(Map<?, ?> values, Object rarity, ItemStack gemStack, LootRarity rarity_, int facets) {
		int level = (int) values.get(rarity);
		if (!gemStack.hasTag()) return level;
		if (!gemStack.getTag().contains("gem_power")) return level;
		float gemPower = gemStack.getTag().getFloat("gem_power");
		return (int) (level * gemPower);
	}
	
	@Redirect(method = "getEnchantmentLevels", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object amplifyGemPower(Map<?, ?> values, Object rarity, ItemStack gemStack, LootRarity rarity_, int facets, Map<Enchantment, Integer> enchantments) {
		int level = (int) values.get(rarity);
		if (!gemStack.hasTag()) return level;
		if (!gemStack.getTag().contains("gem_power")) return level;
		float gemPower = gemStack.getTag().getFloat("gem_power");
		return (int) (level * gemPower);
	}
}
