package daripher.skilltree.mixin.minecraft;

import java.util.ArrayList;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.api.SkillRequiringRecipe;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@Mixin(RecipeBookComponent.class)
public class MixinRecipeBookComponent {
	private @Shadow @Final StackedContents stackedContents;
	private @Shadow ClientRecipeBook book;
	protected @Shadow RecipeBookMenu<?> menu;

	@Redirect(method = "updateCollections", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;"))
	private ArrayList<RecipeCollection> removeUncraftableRecipes(Iterable<RecipeCollection> elements) {
		ArrayList<RecipeCollection> collections = Lists.newArrayList(elements);
		collections.forEach(this::removeUncraftableRecipes);
		return collections;
	}

	protected void removeUncraftableRecipes(RecipeCollection recipeCollection) {
		Set<Recipe<?>> craftableRecipes = ObfuscationReflectionHelper.getPrivateValue(RecipeCollection.class, recipeCollection, "f_100493_");
		if (craftableRecipes.isEmpty()) return;
		craftableRecipes.removeIf(Predicates.not(this::canCraft));
	}
	
	private boolean canCraft(Recipe<?> recipe) {
		if (!(recipe instanceof SkillRequiringRecipe)) return true;
		if (!(menu instanceof PlayerContainer)) return false;
		return ((SkillRequiringRecipe) recipe).canCraftIn((PlayerContainer) menu);
	}
}
