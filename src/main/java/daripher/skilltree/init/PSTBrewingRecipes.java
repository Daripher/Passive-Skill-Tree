package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PSTBrewingRecipes {
    @SubscribeEvent
    public static void addRecipes(FMLCommonSetupEvent event) {
        addRecipe(Potions.FIRE_RESISTANCE, Items.FERMENTED_SPIDER_EYE, PSTPotions.LIQUID_FIRE_1.get());
        addSplashRecipe(PSTPotions.LIQUID_FIRE_1.get());
        addLingeringRecipe(PSTPotions.LIQUID_FIRE_1.get());
        addRecipe(PSTPotions.LIQUID_FIRE_1.get(), Items.GLOWSTONE_DUST, PSTPotions.LIQUID_FIRE_2.get());
        addSplashRecipe(PSTPotions.LIQUID_FIRE_2.get());
        addLingeringRecipe(PSTPotions.LIQUID_FIRE_2.get());
    }

    private static void addRecipe(Potion inputPotion, Item ingredient, @NotNull Potion outputPotion) {
        Item[] potionItems = new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION};
        for (Item potionItem : potionItems) {
            addRecipe(inputPotion, potionItem, ingredient, outputPotion, potionItem);
        }
    }

    private static void addLingeringRecipe(@NotNull Potion potion) {
        addRecipe(potion, Items.SPLASH_POTION, Items.DRAGON_BREATH, potion, Items.LINGERING_POTION);
    }

    private static void addSplashRecipe(@NotNull Potion potion) {
        addRecipe(potion, Items.POTION, Items.GUNPOWDER, potion, Items.SPLASH_POTION);
    }

    private static void addRecipe(Potion inputPotion, Item inputItem, Item ingredient, @NotNull Potion outputPotion, Item outputItem) {
        ItemStack input = getPotionStack(inputItem, inputPotion);
        ItemStack output = getPotionStack(outputItem, outputPotion);
        BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(input), Ingredient.of(ingredient), output);
    }

    @NotNull
    private static ItemStack getPotionStack(Item potionItem, @NotNull Potion outputPotion) {
        ItemStack output = new ItemStack(potionItem);
        PotionUtils.setPotion(output, outputPotion);
        return output;
    }
}
