package daripher.skilltree.recipe.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.EquipmentBonus;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WorkbenchItemBonusRecipeBuilder {
    private final ResourceLocation id;
    private ItemStackPredicate baseItemStackPredicate;
    private final Map<Ingredient, Integer> ingredients = new HashMap<>();
    private boolean requiresPassiveSkill;
    private ItemBonus<?> itemBonus;

    private WorkbenchItemBonusRecipeBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static WorkbenchItemBonusRecipeBuilder create(ResourceLocation id) {
        return new WorkbenchItemBonusRecipeBuilder(id);
    }

    public WorkbenchItemBonusRecipeBuilder setBaseItemCondition(ItemStackPredicate baseItemStackPredicate) {
        this.baseItemStackPredicate = baseItemStackPredicate;
        return this;
    }

    public WorkbenchItemBonusRecipeBuilder addIngredients(Ingredient ingredient, int requiredAmount) {
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

    public void save(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        validate();
        finishedRecipeConsumer.accept(new Result(id, baseItemStackPredicate, ingredients, requiresPassiveSkill, itemBonus));
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

    private record Result(ResourceLocation id, ItemStackPredicate baseItemStackPredicate, Map<Ingredient, Integer> ingredients,
                          boolean requiresPassiveSkill, ItemBonus<?> itemBonus) implements FinishedRecipe {
        @Override
        public void serializeRecipeData(@NotNull JsonObject jsonObject) {
            JsonArray ingredientsJson = new JsonArray();
            ingredients.forEach(((ingredient, requiredAmount) -> {
                JsonObject ingredientJson = new JsonObject();
                ingredientJson.add("ingredient", ingredient.toJson());
                ingredientJson.addProperty("required_amount", requiredAmount);
                ingredientsJson.add(ingredientJson);
            }));
            SerializationHelper.serializeItemPredicate(jsonObject, baseItemStackPredicate, "base_item_condition");
            SerializationHelper.serializeItemBonus(jsonObject, itemBonus);
            jsonObject.addProperty("requires_passive_skill", requiresPassiveSkill);
            jsonObject.add("ingredients", ingredientsJson);
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return id;
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return PSTRecipeSerializers.WORKBENCH_ITEM_BONUS.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
