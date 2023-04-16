package daripher.skilltree.api.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class SkillRequiringBrewingRecipes {
	private static final List<SkillRequiringBrewingRecipe> RECIPES = new ArrayList<>();

	public static boolean addRecipe(SkillRequiringBrewingRecipe recipe) {
		return RECIPES.add(recipe);
	}

	public static List<SkillRequiringBrewingRecipe> getRecipes() {
		return Collections.unmodifiableList(RECIPES);
	}

	public static boolean isValidIngredient(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		for (IBrewingRecipe recipe : RECIPES) {
			if (recipe.isIngredient(stack)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isValidInput(ItemStack stack) {
		for (IBrewingRecipe recipe : RECIPES) {
			if (recipe.isInput(stack)) {
				return true;
			}
		}
		
		return false;
	}

	public static void brewPotions(NonNullList<ItemStack> inputs, ItemStack ingredient, int[] inputIndexes, Player player) {
		for (int i : inputIndexes) {
			ItemStack output = getOutput(inputs.get(i), ingredient, player);

			if (!output.isEmpty()) {
				inputs.set(i, output);
			}
		}
	}

	public static boolean canBrew(NonNullList<ItemStack> inputs, ItemStack ingredient, int[] inputIndexes, Player player) {
		if (ingredient.isEmpty()) {
			return false;
		}

		for (int i : inputIndexes) {
			if (hasOutput(inputs.get(i), ingredient, player)) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasOutput(ItemStack input, ItemStack ingredient, Player player) {
		return !getOutput(input, ingredient, player).isEmpty();
	}

	public static ItemStack getOutput(ItemStack input, ItemStack ingredient, Player player) {
		if (input.isEmpty() || input.getCount() != 1 || ingredient.isEmpty()) {
			return ItemStack.EMPTY;
		}

		for (SkillRequiringBrewingRecipe recipe : RECIPES) {
			if (!PlayerSkillsProvider.get(player).hasSkill(recipe.getRequiredSkillId())) {
				continue;
			}

			ItemStack output = recipe.getOutput(input, ingredient);

			if (!output.isEmpty()) {
				return output;
			}
		}

		return ItemStack.EMPTY;
	}
}
