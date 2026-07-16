package daripher.skilltree.recipe.builder;

import daripher.skilltree.recipe.workbench.WorkbenchUpgradeBonusRecipe;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.item.EquipmentBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class WorkbenchItemBonusRecipeBuilder {
    private final ResourceLocation id;
    private final Map<Ingredient, Integer> ingredients = new HashMap<>();
    private ItemStackPredicate baseItemStackPredicate;
    private boolean requiresPassiveSkill;
    private ItemBonus<?> itemBonus;

    private WorkbenchItemBonusRecipeBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static WorkbenchItemBonusRecipeBuilder create(ResourceLocation id) {
        return new WorkbenchItemBonusRecipeBuilder(id);
    }

    public WorkbenchItemBonusRecipeBuilder setBaseItemCondition(
            ItemStackPredicate baseItemStackPredicate) {
        this.baseItemStackPredicate = baseItemStackPredicate;
        return this;
    }

    public WorkbenchItemBonusRecipeBuilder addIngredients(
            Ingredient ingredient, int requiredAmount) {
        this.ingredients.put(ingredient, requiredAmount);
        return this;
    }

    public WorkbenchItemBonusRecipeBuilder setRequiresPassiveSkill() {
        this.requiresPassiveSkill = true;
        return this;
    }

    public WorkbenchItemBonusRecipeBuilder setItemBonus(ItemBonus<?> itemBonus) {
        this.itemBonus = itemBonus;
        return this;
    }

    public WorkbenchItemBonusRecipeBuilder setItemBonus(SkillBonus<?> skillBonus) {
        this.itemBonus = new EquipmentBonus(skillBonus);
        return this;
    }

    public void save(RecipeOutput recipeOutput) {
        validate();
        recipeOutput.accept(
                id,
                new WorkbenchUpgradeBonusRecipe(
                        id,
                        baseItemStackPredicate,
                        Map.copyOf(ingredients),
                        requiresPassiveSkill,
                        itemBonus),
                null);
    }

    private void validate() {
        if (baseItemStackPredicate == null) {
            throw new IllegalStateException("No base item condition set for recipe " + id);
        }
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("No ingredients set for recipe " + id);
        }
        if (ingredients.size() > 9) {
            throw new IllegalStateException("Too many ingredients set for recipe " + id);
        }
        if (itemBonus == null) {
            throw new IllegalStateException("No item bonus set for recipe " + id);
        }
    }
}
