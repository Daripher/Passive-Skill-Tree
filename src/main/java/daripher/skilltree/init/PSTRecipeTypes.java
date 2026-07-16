package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import daripher.skilltree.util.ForgeRegistries;

public class PSTRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, SkillTreeMod.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AbstractWorkbenchRecipe>> WORKBENCH =
            REGISTRY.register("workbench", () -> new RecipeType<>() {
                public String toString() {
                    return SkillTreeMod.MOD_ID + ":workbench";
                }
            });
}
