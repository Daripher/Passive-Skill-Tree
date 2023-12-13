package daripher.skilltree.mixin.farmersdelight;

import daripher.skilltree.container.ContainerHelper;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(value = CookingPotBlockEntity.class, remap = false)
public class CookingPotBlockEntityMixin {
  @Redirect(
      method = "processCooking",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lvectorwing/farmersdelight/common/crafting/CookingPotRecipe;getResultItem(Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;",
              remap = true))
  private ItemStack setCookedFoodBonuses(CookingPotRecipe recipe, RegistryAccess access) {
    ItemStack result = recipe.getResultItem(access);
    @SuppressWarnings("DataFlowIssue")
    CookingPotBlockEntity cookingPot = (CookingPotBlockEntity) (Object) this;
    ContainerHelper.getViewingPlayer(cookingPot)
        .ifPresent(player -> SkillBonusHandler.itemCrafted(player, result));
    return result;
  }
}
