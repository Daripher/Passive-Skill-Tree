package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.GAME)
public class PSTBrewingRecipes {
  @SubscribeEvent
  public static void addRecipes(RegisterBrewingRecipesEvent event) {
    addRecipe(event, Potions.FIRE_RESISTANCE.value(), Items.FERMENTED_SPIDER_EYE, PSTPotions.LIQUID_FIRE_1.get());
    addSplashRecipe(event, PSTPotions.LIQUID_FIRE_1.get());
    addLingeringRecipe(event, PSTPotions.LIQUID_FIRE_1.get());
    addRecipe(event, PSTPotions.LIQUID_FIRE_1.get(), Items.GLOWSTONE_DUST, PSTPotions.LIQUID_FIRE_2.get());
    addSplashRecipe(event, PSTPotions.LIQUID_FIRE_2.get());
    addLingeringRecipe(event, PSTPotions.LIQUID_FIRE_2.get());
  }

  private static void addRecipe(
      RegisterBrewingRecipesEvent event, Potion inputPotion, Item ingredient, @NotNull Potion outputPotion) {
    Item[] potionItems = new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION};
    for (Item potionItem : potionItems) {
      addRecipe(event, inputPotion, potionItem, ingredient, outputPotion, potionItem);
    }
  }

  private static void addLingeringRecipe(RegisterBrewingRecipesEvent event, @NotNull Potion potion) {
    addRecipe(event, potion, Items.SPLASH_POTION, Items.DRAGON_BREATH, potion, Items.LINGERING_POTION);
  }

  private static void addSplashRecipe(RegisterBrewingRecipesEvent event, @NotNull Potion potion) {
    addRecipe(event, potion, Items.POTION, Items.GUNPOWDER, potion, Items.SPLASH_POTION);
  }

  private static void addRecipe(
      RegisterBrewingRecipesEvent event,
      Potion inputPotion,
      Item inputItem,
      Item ingredient,
      @NotNull Potion outputPotion,
      Item outputItem) {
    ItemStack input = getPotionStack(inputItem, inputPotion);
    ItemStack output = getPotionStack(outputItem, outputPotion);
    event.getBuilder().addRecipe(DataComponentIngredient.of(true, input), Ingredient.of(ingredient), output);
  }

  @NotNull
  private static ItemStack getPotionStack(Item potionItem, @NotNull Potion outputPotion) {
    return PotionContents.createItemStack(
        potionItem, BuiltInRegistries.POTION.wrapAsHolder(outputPotion));
  }
}
