package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.recipe.GemstoneInsertionRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SkillTreeRecipeSerializers {
	public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SkillTreeMod.MOD_ID);

	public static final RegistryObject<RecipeSerializer<?>> GEMSTONE_INSERTION = REGISTRY.register("gemstone_insertion", GemstoneInsertionRecipe.Serializer::new);
}
