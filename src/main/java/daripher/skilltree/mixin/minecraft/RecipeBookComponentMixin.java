package daripher.skilltree.mixin.minecraft;

import com.google.common.collect.Lists;
import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.api.SkillRequiringRecipe;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
  protected @Shadow RecipeBookMenu<?> menu;

  @Redirect(
      method = "updateCollections",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;"))
  private ArrayList<RecipeCollection> removeUncraftableRecipes(
      Iterable<RecipeCollection> elements) {
    ArrayList<RecipeCollection> collections = Lists.newArrayList(elements);
    collections.forEach(this::removeUncraftableRecipes);
    return collections;
  }

  protected void removeUncraftableRecipes(RecipeCollection recipeCollection) {
    Set<Recipe<?>> craftableRecipes =
        ObfuscationReflectionHelper.getPrivateValue(
            RecipeCollection.class, recipeCollection, "f_100493_");
    assert craftableRecipes != null;
    if (craftableRecipes.isEmpty()) return;
    craftableRecipes.removeIf(this::cantCraft);
  }

  private boolean cantCraft(Recipe<?> recipe) {
    if (!(recipe instanceof SkillRequiringRecipe)) return false;
    if (!(menu instanceof PlayerContainer)) return true;
    return !((SkillRequiringRecipe) recipe).canCraftIn((PlayerContainer) menu);
  }
}
