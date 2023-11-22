package daripher.skilltree.mixin.easymagic;

import daripher.skilltree.api.EnchantmentMenuExtention;
import daripher.skilltree.container.ContainerHelper;
import fuzs.easymagic.mixin.accessor.EnchantmentMenuAccessor;
import fuzs.easymagic.world.inventory.ModEnchantmentMenu;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModEnchantmentMenu.class)
public class ModEnchantmentMenuMixin {
  private @Shadow @Final DataSlot enchantmentSeed;

  private static int getReducedEnchantmentCost(Player value, int cost) {
    return daripher.skilltree.enchantment.EnchantmentHelper.adjustEnchantmentCost(cost, value);
  }

  @Redirect(
      method = "updateLevels",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentCost(Lnet/minecraft/util/RandomSource;IILnet/minecraft/world/item/ItemStack;)I"))
  private int reduceLevelRequirements(
      RandomSource random, int slot, int power, ItemStack stack) {
    int levelRequirement = EnchantmentHelper.getEnchantmentCost(random, slot, power, stack);
    int[] costsBeforeReduction = ((EnchantmentMenuExtention) this).getCostsBeforeReduction();
    costsBeforeReduction[slot] = levelRequirement;
    @SuppressWarnings("DataFlowIssue")
    ModEnchantmentMenu menu = (ModEnchantmentMenu) (Object) this;
    return ContainerHelper.getViewingPlayer(menu)
        .map(player -> getReducedEnchantmentCost(player, levelRequirement))
        .orElse(levelRequirement);
  }

  @Inject(
      method = "createEnchantmentInstance",
      at = @At("RETURN"),
      cancellable = true,
      remap = false)
  private void amplifyEnchantments(
      ItemStack itemStack,
      int slot,
      CallbackInfoReturnable<List<EnchantmentInstance>> callbackInfo) {
    int[] costsBeforeReduction = ((EnchantmentMenuExtention) this).getCostsBeforeReduction();
    EnchantmentMenuAccessor accessor = (EnchantmentMenuAccessor) this;
    List<EnchantmentInstance> enchantments =
        accessor.callGetEnchantmentList(itemStack, slot, costsBeforeReduction[slot]);
    RandomSource random = RandomSource.create(enchantmentSeed.get());
    @SuppressWarnings("DataFlowIssue")
    ModEnchantmentMenu menu = (ModEnchantmentMenu) (Object) this;
    Optional<Player> player = ContainerHelper.getViewingPlayer(menu);
    if (player.isEmpty()) return;
    daripher.skilltree.enchantment.EnchantmentHelper.amplifyEnchantments(
        enchantments, random, player.get());
    callbackInfo.setReturnValue(enchantments);
  }
}
