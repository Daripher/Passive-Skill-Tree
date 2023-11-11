package daripher.skilltree.mixin.apotheosis;

import daripher.skilltree.api.EnchantmentMenuExtention;
import daripher.skilltree.container.ContainerHelper;
import daripher.skilltree.enchantment.EnchantmentHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadows.apotheosis.ench.table.ApothEnchantmentMenu;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(value = ApothEnchantmentMenu.class, remap = false)
public class ApothEnchantContainerMixin {
  @Redirect(
      method = "lambda$slotsChanged$1",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraftforge/event/ForgeEventFactory;onEnchantmentLevelSet(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;IILnet/minecraft/world/item/ItemStack;I)I"))
  private int reduceLevelRequirements(
      Level level, BlockPos pos, int slot, int power, ItemStack itemStack, int enchantmentLevel) {
    @SuppressWarnings("DataFlowIssue")
    ApothEnchantmentMenu menu = (ApothEnchantmentMenu) (Object) this;
    int[] costs = menu.costs;
    int cost =
        ForgeEventFactory.onEnchantmentLevelSet(level, pos, slot, power, itemStack, costs[slot]);
    int[] costsBeforeReduction = ((EnchantmentMenuExtention) this).getCostsBeforeReduction();
    costsBeforeReduction[slot] = cost;
    return ContainerHelper.getViewingPlayer(menu)
        .map(player -> EnchantmentHelper.adjustEnchantmentCost(cost, player))
        .orElse(cost);
  }

  @Redirect(
      method = "lambda$slotsChanged$1",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lshadows/apotheosis/ench/table/ApothEnchantmentMenu;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;",
              remap = true))
  private List<EnchantmentInstance> amplifyEnchantmentsVisually(
      ApothEnchantmentMenu menu, ItemStack itemStack, int slot, int cost) {
    return amplifyEnchantments(itemStack, slot);
  }

  @Redirect(
      method = "lambda$clickMenuButton$0",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lshadows/apotheosis/ench/table/ApothEnchantmentMenu;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;",
              remap = true))
  private List<EnchantmentInstance> amplifyEnchantmentsOnButtonClick(
      ApothEnchantmentMenu menu, ItemStack itemStack, int slot, int cost) {
    return amplifyEnchantments(itemStack, slot);
  }

  protected List<EnchantmentInstance> amplifyEnchantments(ItemStack itemStack, int slot) {
    EnchantmentMenuExtention enchantmentMenu = (EnchantmentMenuExtention) this;
    int[] costsBeforeReduction = enchantmentMenu.getCostsBeforeReduction();
    List<EnchantmentInstance> enchantments =
        getEnchantmentList(itemStack, slot, costsBeforeReduction[slot]);
    int enchantmentSeed = enchantmentMenu.getEnchantmentSeed();
    RandomSource random = RandomSource.create(enchantmentSeed);
    @SuppressWarnings("DataFlowIssue")
    ApothEnchantmentMenu menu = (ApothEnchantmentMenu) (Object) this;
    Optional<Player> player = ContainerHelper.getViewingPlayer(menu);
    if (player.isEmpty()) return enchantments;
    assert enchantments != null;
    EnchantmentHelper.amplifyEnchantments(enchantments, random, player.get());
    return enchantments;
  }

  @SuppressWarnings({"DefaultAnnotationParam", "unused"})
  @Shadow(remap = true)
  private List<EnchantmentInstance> getEnchantmentList(
      ItemStack stack, int enchantSlot, int level) {
    return null;
  }
}
