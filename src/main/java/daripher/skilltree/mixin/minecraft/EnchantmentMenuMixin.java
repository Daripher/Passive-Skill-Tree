package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.api.EnchantmentMenuExtention;
import daripher.skilltree.container.ContainerHelper;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin implements EnchantmentMenuExtention {
  private final int[] costsBeforeReduction = new int[3];
  public @Shadow @Final int[] costs;
  private @Shadow @Final DataSlot enchantmentSeed;

  @Redirect(
      method = {"lambda$slotsChanged$0", "m_39483_"},
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraftforge/event/ForgeEventFactory;onEnchantmentLevelSet(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;IILnet/minecraft/world/item/ItemStack;I)I"))
  private int reduceLevelRequirements(
      Level level, BlockPos pos, int slot, int power, ItemStack itemStack, int enchantmentLevel) {
    int levelRequirement =
        ForgeEventFactory.onEnchantmentLevelSet(level, pos, slot, power, itemStack, costs[slot]);
    costsBeforeReduction[slot] = levelRequirement;
    @SuppressWarnings("DataFlowIssue")
    EnchantmentMenu menu = (EnchantmentMenu) (Object) this;
    return ContainerHelper.getViewingPlayer(menu)
        .map(player -> SkillBonusHandler.adjustEnchantmentCost(levelRequirement, player))
        .orElse(levelRequirement);
  }

  @Redirect(
      method = {"lambda$slotsChanged$0", "m_39483_"},
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/inventory/EnchantmentMenu;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
  private List<EnchantmentInstance> amplifyEnchantmentsVisually(
      EnchantmentMenu menu, ItemStack itemStack, int slot, int cost) {
    return amplifyEnchantments(itemStack, slot);
  }

  @Redirect(
      method = {"lambda$clickMenuButton$1", "m_39475_"},
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/inventory/EnchantmentMenu;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
  private List<EnchantmentInstance> amplifyEnchantmentsOnButtonClick(
      EnchantmentMenu menu, ItemStack itemStack, int slot, int cost) {
    return amplifyEnchantments(itemStack, slot);
  }

  private List<EnchantmentInstance> amplifyEnchantments(ItemStack itemStack, int slot) {
    List<EnchantmentInstance> enchantments =
        getEnchantmentList(itemStack, slot, costsBeforeReduction[slot]);
    RandomSource random = RandomSource.create(enchantmentSeed.get());
    @SuppressWarnings("DataFlowIssue")
    EnchantmentMenu menu = (EnchantmentMenu) (Object) this;
    Optional<Player> player = ContainerHelper.getViewingPlayer(menu);
    if (player.isEmpty()) return enchantments;
    SkillBonusHandler.amplifyEnchantments(enchantments, random, player.get());
    return enchantments;
  }

  @SuppressWarnings({"unused", "DataFlowIssue"})
  @Shadow
  private @Nonnull List<EnchantmentInstance> getEnchantmentList(
      ItemStack stack, int slot, int cost) {
    return null;
  }

  @Override
  public int[] getCostsBeforeReduction() {
    return costsBeforeReduction;
  }

  @Override
  public int getEnchantmentSeed() {
    return enchantmentSeed.get();
  }
}
