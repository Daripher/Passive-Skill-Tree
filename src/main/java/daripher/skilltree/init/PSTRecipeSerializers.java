package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.recipe.GemInsertionRecipe;
import daripher.skilltree.recipe.MixtureRecipe;
import daripher.skilltree.recipe.PSTShapedRecipe;
import daripher.skilltree.recipe.PSTShapelessRecipe;
import daripher.skilltree.recipe.QuiverFillingRecipe;
import daripher.skilltree.recipe.WeaponPoisoningRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PSTRecipeSerializers {
  public static final DeferredRegister<RecipeSerializer<?>> REGISTRY =
      DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SkillTreeMod.MOD_ID);

  public static final RegistryObject<RecipeSerializer<?>> GEM_INSERTION =
      REGISTRY.register("gem_insertion", GemInsertionRecipe.Serializer::new);
  public static final RegistryObject<RecipeSerializer<?>> WEAPON_POISONING =
      REGISTRY.register("weapon_poisoning", WeaponPoisoningRecipe.Serializer::new);
  public static final RegistryObject<RecipeSerializer<?>> POTION_MIXING =
      REGISTRY.register("potion_mixing", MixtureRecipe.Serializer::new);
  public static final RegistryObject<RecipeSerializer<?>> SHAPED_CRAFTING =
      REGISTRY.register("crafting_shaped", PSTShapedRecipe.Serializer::new);
  public static final RegistryObject<RecipeSerializer<?>> SHAPELESS_CRAFTING =
      REGISTRY.register("crafting_shapeless", PSTShapelessRecipe.Serializer::new);
  public static final RegistryObject<RecipeSerializer<?>> QUIVER_FILLING =
      REGISTRY.register("quiver_filling", QuiverFillingRecipe.Serializer::new);
}
