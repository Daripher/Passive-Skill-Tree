package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.recipe.workbench.WorkbenchCraftingRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchItemBonusRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import daripher.skilltree.util.ForgeRegistries;

public class PSTRecipeSerializers {
  public static final DeferredRegister<RecipeSerializer<?>> REGISTRY =
      DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SkillTreeMod.MOD_ID);

  public static final DeferredHolder<RecipeSerializer<?>, ? extends WorkbenchItemBonusRecipe.Serializer>
      WORKBENCH_ITEM_BONUS =
      REGISTRY.register("workbench_item_bonus", WorkbenchItemBonusRecipe.Serializer::new);
  public static final DeferredHolder<RecipeSerializer<?>, ? extends WorkbenchCraftingRecipe.Serializer>
      WORKBENCH_CRAFTING =
      REGISTRY.register("workbench_crafting", WorkbenchCraftingRecipe.Serializer::new);
}
