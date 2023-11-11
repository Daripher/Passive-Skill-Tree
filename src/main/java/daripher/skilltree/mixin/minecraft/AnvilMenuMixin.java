package daripher.skilltree.mixin.minecraft;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {
  @Redirect(
      method = "createResult",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"),
      require = 0)
  private int uncapEnchantmentLevel(Enchantment enchantment) {
    return Integer.MAX_VALUE;
  }
}
