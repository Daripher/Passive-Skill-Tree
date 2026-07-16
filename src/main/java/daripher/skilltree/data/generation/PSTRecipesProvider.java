package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class PSTRecipesProvider extends RecipeProvider {
    public PSTRecipesProvider(
            PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(
            @NotNull RecipeOutput recipeOutput,
            @NotNull HolderLookup.Provider lookupProvider) {
        addCraftingTableRecipes(recipeOutput);
    }

    private static void addCraftingTableRecipes(@NotNull RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PSTItems.WORKBENCH.get()).define('I', Tags.Items.INGOTS_IRON)
                .define('G', Tags.Items.INGOTS_GOLD).define('C', Tags.Items.INGOTS_COPPER).define('#', Items.SMITHING_TABLE).pattern("III")
                .pattern("G#G").pattern("CCC").unlockedBy(getHasName(Items.SMITHING_TABLE), has(Items.SMITHING_TABLE)).save(recipeOutput);
    }

    private static ResourceLocation modRecipeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, path);
    }
}
