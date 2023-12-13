package daripher.skilltree.mixin.farmersdelight;

import daripher.skilltree.container.ContainerHelper;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vectorwing.farmersdelight.common.block.entity.StoveBlockEntity;

@Mixin(value = StoveBlockEntity.class)
public class StoveBlockEntityMixin {
  @SuppressWarnings("DefaultAnnotationParam")
  @Redirect(
      method = "cookAndOutputItems",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/item/crafting/CampfireCookingRecipe;getResultItem(Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;",
              remap = true),
      remap = false)
  private ItemStack setCookedFoodBonuses(CampfireCookingRecipe recipe, RegistryAccess access) {
    ItemStack result = recipe.getResultItem(access);
    @SuppressWarnings("DataFlowIssue")
    StoveBlockEntity stove = (StoveBlockEntity) (Object) this;
    ContainerHelper.getViewingPlayer(stove)
        .ifPresent(player -> SkillBonusHandler.itemCrafted(player, result));
    return result;
  }
}
