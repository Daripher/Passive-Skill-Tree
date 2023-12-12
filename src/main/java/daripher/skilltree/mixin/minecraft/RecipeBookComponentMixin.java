package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import daripher.skilltree.container.InteractiveContainer;
import daripher.skilltree.mixin.RecipeCollectionAccessor;
import daripher.skilltree.recipe.SkillRequiringRecipe;
import java.util.ArrayList;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
  protected @Shadow RecipeBookMenu<?> menu;

  @ModifyExpressionValue(
      method = "updateCollections",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;"))
  private ArrayList<RecipeCollection> removeUncraftableRecipes(
      ArrayList<RecipeCollection> original) {
    original.forEach(this::removeUncraftableRecipes);
    return original;
  }

  protected void removeUncraftableRecipes(RecipeCollection recipeCollection) {
    RecipeCollectionAccessor accessor = (RecipeCollectionAccessor) recipeCollection;
    accessor.getCraftable().removeIf(this::isUncraftable);
  }

  private boolean isUncraftable(Recipe<?> recipe) {
    if (!(recipe instanceof SkillRequiringRecipe aRecipe)) return false;
    if (!(menu instanceof InteractiveContainer container)) return true;
    return aRecipe.isUncraftable(container, recipe);
  }
}
