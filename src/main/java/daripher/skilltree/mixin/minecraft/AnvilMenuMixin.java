package daripher.skilltree.mixin.minecraft;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
  @SuppressWarnings("DataFlowIssue")
  public AnvilMenuMixin() {
    super(null, 0, null, null);
  }

  @Redirect(
      method = "createResult",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"),
      require = 0)
  private int uncapEnchantmentLevel(Enchantment enchantment) {
    ItemStack base = inputSlots.getItem(0);
    ItemStack addition = inputSlots.getItem(1);
    if (base.getAllEnchantments().isEmpty() && addition.getItem() == Items.ENCHANTED_BOOK) {
      return Integer.MAX_VALUE;
    }
    return enchantment.getMaxLevel();
  }
}
