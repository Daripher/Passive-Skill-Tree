package daripher.skilltree.recipe.builder;

import com.google.gson.JsonObject;
import daripher.skilltree.init.PSTRecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class StackResultSmithingRecipeBuilder {
  private final Ingredient template;
  private final Ingredient base;
  private final Ingredient addition;
  private final ItemStack result;
  private final RecipeSerializer<?> type = PSTRecipeSerializers.SMITHING_TRANSFORM.get();

  private StackResultSmithingRecipeBuilder(Ingredient pTemplate, Ingredient pBase, Ingredient pAddition, ItemStack pResult) {
    this.template = pTemplate;
    this.base = pBase;
    this.addition = pAddition;
    this.result = pResult;
  }

  public static StackResultSmithingRecipeBuilder create(Ingredient pTemplate, Ingredient pBase, Ingredient pAddition, ItemStack pResult) {
    return new StackResultSmithingRecipeBuilder(pTemplate, pBase, pAddition, pResult);
  }

  public void save(Consumer<FinishedRecipe> pRecipeConsumer, ResourceLocation pLocation) {
    pRecipeConsumer.accept(new StackResultSmithingRecipeBuilder.Result(pLocation, this.type, this.template, this.base, this.addition, this.result));
  }

  public static final class Result implements FinishedRecipe {
    private final ResourceLocation id;
    private final RecipeSerializer<?> type;
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final ItemStack result;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

    public Result(ResourceLocation id, RecipeSerializer<?> type, Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
      this.id = id;
      this.type = type;
      this.template = template;
      this.base = base;
      this.addition = addition;
      this.result = result;
    }

    public void serializeRecipeData(JsonObject json) {
      json.add("template", this.template.toJson());
      json.add("base", this.base.toJson());
      json.add("addition", this.addition.toJson());
      JsonObject itemJson = new JsonObject();
      Item resultItem = this.result.getItem();
      ResourceLocation itemId = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(resultItem));
      itemJson.addProperty("item", itemId.toString());
      CompoundTag resultTag = this.result.getTag();
      if (resultTag != null) {
        itemJson.addProperty("nbt", resultTag.toString());
      }
      json.add("result", itemJson);
      json.addProperty("show_notification", false);
    }

    public @NotNull ResourceLocation getId() {
      return this.id;
    }

    public @NotNull RecipeSerializer<?> getType() {
      return this.type;
    }

    public @NotNull JsonObject serializeAdvancement() {
      return this.advancement.serializeToJson();
    }

    @Nullable
    public ResourceLocation getAdvancementId() {
      return getId().withPrefix("recipes/");
    }

    public ResourceLocation id() {
      return this.id;
    }

    public RecipeSerializer<?> type() {
      return this.type;
    }

    public ItemStack result() {
      return this.result;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) return true;
      if (obj == null || obj.getClass() != this.getClass()) return false;
      Result that = (Result) obj;
      return Objects.equals(this.id, that.id) &&
             Objects.equals(this.type, that.type) &&
             Objects.equals(this.template, that.template) &&
             Objects.equals(this.base, that.base) &&
             Objects.equals(this.addition, that.addition) &&
             Objects.equals(this.result, that.result);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, type, template, base, addition, result);
    }

    @Override
    public String toString() {
      return "Result[id=%s, type=%s, template=%s, base=%s, addition=%s, result=%s]".formatted(id, type, template, base, addition, result);
    }
  }
}
