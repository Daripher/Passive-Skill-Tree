package daripher.skilltree.recipe;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.util.PotionHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.RegisterEvent;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
public class SuperiorPotionRecipe implements IBrewingRecipe {
	@SubscribeEvent
	public static void registerRecipe(RegisterEvent event) {
		BrewingRecipeRegistry.addRecipe(new SuperiorPotionRecipe());
	}

	@Override
	public boolean isInput(ItemStack input) {
		return PotionHelper.isSuperiorPotion(input) && BrewingRecipeRegistry.isValidInput(PotionHelper.getActualPotionStack(input));
	}

	@Override
	public boolean isIngredient(ItemStack ingredient) {
		return PotionBrewing.isIngredient(ingredient);
	}

	@Override
	public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
		var actualPotionStack = PotionHelper.getActualPotionStack(input);

		if (!BrewingRecipeRegistry.isValidInput(actualPotionStack)) {
			return ItemStack.EMPTY;
		}

		for (IBrewingRecipe recipe : BrewingRecipeRegistry.getRecipes()) {
			if (recipe instanceof SuperiorPotionRecipe) {
				continue;
			}

			ItemStack output = recipe.getOutput(actualPotionStack, ingredient);

			if (!output.isEmpty()) {
				return output;
			}
		}

		return ItemStack.EMPTY;
	}
}
