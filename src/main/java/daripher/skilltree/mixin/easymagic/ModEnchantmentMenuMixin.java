package daripher.skilltree.mixin.easymagic;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import daripher.skilltree.api.EnchantmentMenuExtension;
import daripher.skilltree.container.ContainerHelper;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
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
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModEnchantmentMenu.class)
public class ModEnchantmentMenuMixin {
  private @Shadow @Final DataSlot enchantmentSeed;

  @Redirect(
      method = "updateLevels",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentCost(Lnet/minecraft/util/RandomSource;IILnet/minecraft/world/item/ItemStack;)I"))
  private int reduceLevelRequirements(RandomSource random, int slot, int power, ItemStack stack) {
    int cost = EnchantmentHelper.getEnchantmentCost(random, slot, power, stack);
    EnchantmentMenuExtension extension = (EnchantmentMenuExtension) this;
    extension.getCostsBeforeReduction()[slot] = cost;
    @SuppressWarnings("DataFlowIssue")
    ModEnchantmentMenu menu = (ModEnchantmentMenu) (Object) this;
    Optional<Player> player = ContainerHelper.getViewingPlayer(menu);
    return player.map(p -> SkillBonusHandler.adjustEnchantmentCost(cost, p)).orElse(cost);
  }

  @ModifyReturnValue(method = "createEnchantmentInstance", at = @At("RETURN"), remap = false)
  private List<EnchantmentInstance> amplifyEnchantments(
      List<EnchantmentInstance> original, ItemStack itemStack, int slot) {
    @SuppressWarnings("DataFlowIssue")
    ModEnchantmentMenu menu = (ModEnchantmentMenu) (Object) this;
    Optional<Player> player = ContainerHelper.getViewingPlayer(menu);
    if (player.isEmpty()) return original;
    EnchantmentMenuExtension extension = (EnchantmentMenuExtension) this;
    EnchantmentMenuAccessor accessor = (EnchantmentMenuAccessor) this;
    int cost = extension.getCostsBeforeReduction()[slot];
    List<EnchantmentInstance> enchantments = accessor.callGetEnchantmentList(itemStack, slot, cost);
    RandomSource random = RandomSource.create(enchantmentSeed.get());
    SkillBonusHandler.amplifyEnchantments(enchantments, random, player.get());
    return enchantments;
  }
}
