package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.recipe.GemstoneInsertionRecipe;
import daripher.skilltree.recipe.PotionMixingRecipe;
import daripher.skilltree.recipe.WeaponPoisoningRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SkillTreeRecipeSerializers {
	public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SkillTreeMod.MOD_ID);

	public static final RegistryObject<RecipeSerializer<?>> GEMSTONE_INSERTION = REGISTRY.register("gemstone_insertion", GemstoneInsertionRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<?>> WEAPON_POISONING = REGISTRY.register("weapon_poisoning", WeaponPoisoningRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<?>> POTION_MIXING = REGISTRY.register("potion_mixing", PotionMixingRecipe.Serializer::new);
}
