package daripher.skilltree.recipe.workbench;

import daripher.skilltree.init.PSTRecipeTypes;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.recipe.SkillRequiringRecipe;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.RecipeUnlockBonus;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import daripher.skilltree.util.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractWorkbenchRecipe implements Recipe<WorkbenchContainer>, SkillRequiringRecipe {
    private final ResourceLocation id;
    private final boolean requiresPassiveSkill;

    public AbstractWorkbenchRecipe(ResourceLocation id, boolean requiresPassiveSkill) {
        this.requiresPassiveSkill = requiresPassiveSkill;
        this.id = id;
    }

    @Override
    public boolean matches(@NotNull WorkbenchContainer container, @NotNull Level level) {
        ItemStack baseItem = container.getBaseItem();
        if (!isValidBaseItem(baseItem)) {
            return false;
        }
        if (isLockedFor(container.getPlayer())) {
            return false;
        }
        return hasIngredients(container, getAdditionalIngredients(baseItem));
    }

    public String getDescriptionId() {
        ResourceLocation id = ForgeRegistries.RECIPE_SERIALIZERS.getKey(getSerializer());
        Objects.requireNonNull(id);
        return "recipe.%s.%s".formatted(id.getNamespace(), id.getPath());
    }

    public boolean isLockedFor(@NotNull Player player) {
        return requiresPassiveSkill && !hasRecipeLearned(player);
    }

    public abstract boolean isValidBaseItem(ItemStack itemStack);

    public boolean isValidIngredient(ItemStack itemStack) {
        return true;
    }

    public abstract Component getShortDescription();

    public List<Component> getFullDescription() {
        return List.of(getShortDescription());
    }

    public abstract @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer);

    public abstract int requiredBaseItemAmount();

    public abstract @Nullable Pair<Ingredient, Integer> getBaseIngredient();

    public abstract Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseIngredient);

    protected final boolean hasRecipeLearned(@NotNull Player player) {
        return SkillBonusProvider.getSkillBonuses(player, RecipeUnlockBonus.class).stream().map(RecipeUnlockBonus::getRecipeId)
                .anyMatch(getId()::equals);
    }

    protected boolean hasIngredients(@NotNull WorkbenchContainer container, Map<Ingredient, Integer> ingredients) {
        return container.hasIngredients(ingredients);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width == 5 && height == 2;
    }

    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Deprecated
    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return PSTRecipeTypes.WORKBENCH.get();
    }

    @Override
    public boolean hasPassiveSkillRequirement() {
        return requiresPassiveSkill;
    }
}
