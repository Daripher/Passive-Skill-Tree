package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class PSTRecipeTypes {
  public static final DeferredRegister<RecipeType<?>> REGISTRY =
      DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, SkillTreeMod.MOD_ID);

  public static final RecipeType<AbstractWorkbenchRecipe> WORKBENCH = register("workbench");

  private static <T extends Recipe<?>> RecipeType<T> register(final String identifier) {
    RecipeType<T> recipeType =
        new RecipeType<>() {
          public String toString() {
            return identifier;
          }
        };
    ForgeRegistries.RECIPE_TYPES.register(identifier, recipeType);
    return recipeType;
  }
}
