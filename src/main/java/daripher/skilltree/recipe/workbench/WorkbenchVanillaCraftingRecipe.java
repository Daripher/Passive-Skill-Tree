package daripher.skilltree.recipe.workbench;

import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.VanillaRecipeUnlockBonus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorkbenchVanillaCraftingRecipe extends AbstractWorkbenchRecipe {
    private @Nullable Pair<Ingredient, Integer> baseIngredient;
    private Map<Ingredient, Integer> additionalIngredients;
    private final ItemStack result;
    private final RecipeSerializer<?> serializer;

    public WorkbenchVanillaCraftingRecipe(
            RecipeHolder<CraftingRecipe> vanillaRecipe,
            HolderLookup.Provider registryAccess) {
        super(vanillaRecipe.id(), true);
        CraftingRecipe recipe = vanillaRecipe.value();
        this.result = recipe.getResultItem(registryAccess);
        this.serializer = recipe.getSerializer();
        additionalIngredients = getIngredientsFromCraftingRecipe(recipe);
        List<Pair<Ingredient, Integer>> ingredients =
                new ArrayList<>(additionalIngredients.entrySet().stream().map(Pair::of).toList());
        if (!ingredients.isEmpty()) {
            this.baseIngredient = ingredients.remove(0);
            additionalIngredients = ingredients.stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    private static Map<Ingredient, Integer> getIngredientsFromCraftingRecipe(
            CraftingRecipe vanillaRecipe) {
        record IngredientKey(Set<Item> items) {
        }
        Map<IngredientKey, Ingredient> uniqueIngredients = new HashMap<>();
        Map<IngredientKey, Integer> ingredientCounts = new HashMap<>();
        NonNullList<Ingredient> vanillaIngredients = vanillaRecipe.getIngredients();
        for (Ingredient ingredient : vanillaIngredients) {
            ItemStack[] matchingStacks = ingredient.getItems();
            if (matchingStacks.length == 0) {
                continue;
            }
            Set<Item> itemSet = new HashSet<>(matchingStacks.length);
            for (ItemStack matchingStack : matchingStacks) {
                itemSet.add(matchingStack.getItem());
            }
            IngredientKey key = new IngredientKey(itemSet);
            uniqueIngredients.putIfAbsent(key, ingredient);
            ingredientCounts.put(key, ingredientCounts.getOrDefault(key, 0) + 1);
        }
        Map<Ingredient, Integer> result = new HashMap<>(ingredientCounts.size());
        ingredientCounts.forEach(
                (key, count) -> result.put(uniqueIngredients.get(key), count));
        return result;
    }

    @Override
    public @NotNull ItemStack assemble(
            @NotNull WorkbenchContainer container,
            @NotNull HolderLookup.Provider registryAccess) {
        return getResult(container);
    }

    @Override
    public boolean isValidBaseItem(ItemStack itemStack) {
        return baseIngredient != null
                && baseIngredient.getLeft().test(itemStack)
                && itemStack.getCount() >= baseIngredient.getRight();
    }

    @Override
    public Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseIngredient) {
        return additionalIngredients;
    }

    public Map<Ingredient, Integer> getAdditionalIngredients() {
        return additionalIngredients;
    }

    @Override
    public boolean isLockedFor(@NotNull Player player) {
        List<VanillaRecipeUnlockBonus> recipeUnlockBonuses =
                SkillBonusProvider.getSkillBonuses(player, VanillaRecipeUnlockBonus.class);
        return recipeUnlockBonuses.stream().noneMatch(bonus -> bonus.canUnlockRecipe(this));
    }

    @Override
    public Component getShortDescription() {
        return result.getHoverName();
    }

    @Override
    public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
        return result.copy();
    }

    public @NotNull ItemStack getResult() {
        return result.copy();
    }

    @Override
    public int requiredBaseItemAmount() {
        return baseIngredient == null ? 0 : baseIngredient.getRight();
    }

    @Override
    public @Nullable Pair<Ingredient, Integer> getBaseIngredient() {
        return baseIngredient;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return serializer;
    }
}
