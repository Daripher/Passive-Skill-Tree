package daripher.skilltree.mixin.apotheosis;

import java.util.Map;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadows.apotheosis.adventure.affix.socket.gem.bonus.EnchantmentBonus;

@Mixin(value = EnchantmentBonus.class, remap = false)
public class MixinEnchantmentBonus {
  @Redirect(
      method = "getSocketBonusTooltip",
      at =
          @At(
              value = "INVOKE",
              target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
  private Object amplifyGemPowerVisually(Map<?, ?> values, Object rarity, ItemStack gem) {
    return getGemPower(values, rarity, gem);
  }

  @Redirect(
      method = "getEnchantmentLevels",
      at =
          @At(
              value = "INVOKE",
              target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
  private Object amplifyGemPower(Map<?, ?> values, Object rarity, ItemStack gem) {
    return getGemPower(values, rarity, gem);
  }

  private int getGemPower(Map<?, ?> values, Object rarity, ItemStack gem) {
    int level = (int) values.get(rarity);
    if (!gem.hasTag()) return level;
    if (!gem.getTag().contains("gem_power")) return level;
    float power = gem.getTag().getFloat("gem_power") + 1;
    return (int) (level * power);
  }
}
