package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import daripher.itemproduction.block.entity.Interactive;
import daripher.skilltree.mixin.RecipeCollectionAccessor;
import daripher.skilltree.recipe.SkillRequiringRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Set;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
  protected @Shadow RecipeBookMenu<?> menu;

  @ModifyExpressionValue(method = "updateCollections", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList" +
      "(Ljava/lang/Iterable;)Ljava/util/ArrayList;"))
  private ArrayList<RecipeCollection> removeUnusableRecipes(ArrayList<RecipeCollection> original) {
    original.forEach(this::removeUnusableRecipes);
    return original;
  }

  protected void removeUnusableRecipes(RecipeCollection recipeCollection) {
    RecipeCollectionAccessor accessor = (RecipeCollectionAccessor) recipeCollection;
    Set<Recipe<?>> usableRecipes = accessor.getCraftable();
    usableRecipes.removeIf(recipe -> !canUseRecipe(recipe));
  }

  private boolean canUseRecipe(Recipe<?> recipe) {
    if (!(recipe instanceof SkillRequiringRecipe aRecipe)) return false;
    if (!(menu instanceof Interactive interactive)) return true;
    return aRecipe.canUseRecipe(interactive, recipe);
  }
}
