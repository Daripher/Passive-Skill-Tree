package daripher.skilltree.recipe.workbench;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorkbenchCraftingRecipe extends AbstractWorkbenchRecipe {
  private final @Nullable Pair<Ingredient, Integer> baseIngredient;
  private final ItemStack result;

  public WorkbenchCraftingRecipe(
      ResourceLocation id,
      @Nullable Pair<Ingredient, Integer> baseIngredient,
      Map<Ingredient, Integer> ingredients,
      boolean requiresPassiveSkill,
      ItemStack result) {
    super(id, ingredients, requiresPassiveSkill);
    this.result = result;
    this.baseIngredient = baseIngredient;
  }

  @Override
  public @NotNull ItemStack assemble(
      @NotNull WorkbenchContainer container, @NotNull HolderLookup.Provider registryAccess) {
    return getResult(container);
  }

  @Override
  public boolean isValidBaseItem(ItemStack itemStack) {
    if (baseIngredient == null) {
      return itemStack.isEmpty();
    }
    return baseIngredient.getLeft().test(itemStack)
        && itemStack.getCount() >= baseIngredient.getRight();
  }

  @Override
  public Component getShortDescription() {
    return result.getHoverName();
  }

  @Override
  public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
    return result.copy();
  }

  @Override
  public int requiredBaseItemAmount() {
    return baseIngredient == null ? 0 : baseIngredient.getRight();
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.WORKBENCH_CRAFTING.get();
  }

  public static class Serializer implements RecipeSerializer<WorkbenchCraftingRecipe> {
    private static final ResourceLocation UNKNOWN_ID =
        ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "unknown");

    private static final MapCodec<WorkbenchCraftingRecipe> CODEC =
        RecordCodecBuilder.mapCodec(
            instance ->
                instance
                    .group(
                        ResourceLocation.CODEC
                            .optionalFieldOf("id", UNKNOWN_ID)
                            .forGetter(AbstractWorkbenchRecipe::getId),
                        WorkbenchRecipeCodecs.IngredientEntry.CODEC
                            .optionalFieldOf("base_ingredient")
                            .forGetter(recipe -> optionalBaseIngredient(recipe.baseIngredient)),
                        WorkbenchRecipeCodecs.IngredientEntry.CODEC
                            .listOf()
                            .fieldOf("ingredients")
                            .xmap(WorkbenchRecipeCodecs::toMap, WorkbenchRecipeCodecs::toEntries)
                            .forGetter(AbstractWorkbenchRecipe::getAdditionalIngredients),
                        Codec.BOOL
                            .optionalFieldOf("requires_passive_skill", false)
                            .forGetter(AbstractWorkbenchRecipe::requiresPassiveSkill),
                        ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result))
                    .apply(
                        instance,
                        (id, baseIngredient, ingredients, requiresPassiveSkill, result) ->
                            new WorkbenchCraftingRecipe(
                                id,
                                baseIngredient
                                    .map(
                                        entry ->
                                            Pair.of(entry.ingredient(), entry.requiredAmount()))
                                    .orElse(null),
                                ingredients,
                                requiresPassiveSkill,
                                result)));

    private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchCraftingRecipe>
        STREAM_CODEC = StreamCodec.of(Serializer::encode, Serializer::decode);

    @Override
    public @NotNull MapCodec<WorkbenchCraftingRecipe> codec() {
      return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, WorkbenchCraftingRecipe> streamCodec() {
      return STREAM_CODEC;
    }

    private static WorkbenchCraftingRecipe decode(RegistryFriendlyByteBuf buf) {
      ResourceLocation id = buf.readResourceLocation();
      boolean requiresPassiveSkill = buf.readBoolean();
      Map<Ingredient, Integer> ingredients = WorkbenchRecipeCodecs.readIngredientMap(buf);
      Pair<Ingredient, Integer> baseIngredient = null;
      boolean hasBaseIngredient = buf.readBoolean();
      if (hasBaseIngredient) {
        baseIngredient = Pair.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readInt());
      }
      ItemStack result = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
      return new WorkbenchCraftingRecipe(
          id, baseIngredient, ingredients, requiresPassiveSkill, result);
    }

    private static void encode(RegistryFriendlyByteBuf buf, WorkbenchCraftingRecipe recipe) {
      buf.writeResourceLocation(recipe.getId());
      buf.writeBoolean(recipe.requiresPassiveSkill());
      WorkbenchRecipeCodecs.writeIngredientMap(buf, recipe.getAdditionalIngredients());
      buf.writeBoolean(recipe.baseIngredient != null);
      if (recipe.baseIngredient != null) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.baseIngredient.getLeft());
        buf.writeInt(recipe.baseIngredient.getRight());
      }
      ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, recipe.result);
    }

    private static Optional<WorkbenchRecipeCodecs.IngredientEntry> optionalBaseIngredient(
        @Nullable Pair<Ingredient, Integer> baseIngredient) {
      if (baseIngredient == null) {
        return Optional.empty();
      }
      return Optional.of(
          new WorkbenchRecipeCodecs.IngredientEntry(
              baseIngredient.getLeft(), baseIngredient.getRight()));
    }
  }
}
