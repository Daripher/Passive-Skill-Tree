package daripher.skilltree.recipe;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.api.recipe.SkillRequiringBrewingRecipe;
import daripher.skilltree.api.recipe.SkillRequiringBrewingRecipes;
import daripher.skilltree.util.PotionHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.RegisterEvent;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
public enum CombinedPotionRecipe implements SkillRequiringBrewingRecipe {
	INSTANCE;

	@SubscribeEvent
	public static void registerRecipe(RegisterEvent event) {
		SkillRequiringBrewingRecipes.addRecipe(INSTANCE);
	}

	@Override
	public boolean isInput(ItemStack input) {
		return PotionHelper.canCombinePotion(input);
	}

	@Override
	public boolean isIngredient(ItemStack ingredient) {
		return PotionHelper.canCombinePotion(ingredient);
	}

	@Override
	public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
		if (!PotionHelper.canCombinePotions(input, ingredient)) {
			return ItemStack.EMPTY;
		}

		var outputStack = PotionHelper.combinePotions(input, ingredient);
		return outputStack;
	}

	@Override
	public ResourceLocation getRequiredSkillId() {
		return new ResourceLocation(SkillTreeMod.MOD_ID, "alchemist_notable_left");
	}
}
