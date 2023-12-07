package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
  @SuppressWarnings("DataFlowIssue")
  public AnvilMenuMixin() {
    super(null, 0, null, null);
  }

  @ModifyExpressionValue(
      method = "createResult",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
  private int uncapEnchantmentLevel(int original) {
    ItemStack base = inputSlots.getItem(0);
    ItemStack addition = inputSlots.getItem(1);
    if (base.getAllEnchantments().isEmpty() && addition.getItem() == Items.ENCHANTED_BOOK) {
      // no limitations - just applying level from book
      return Integer.MAX_VALUE;
    }
    return original;
  }
}
