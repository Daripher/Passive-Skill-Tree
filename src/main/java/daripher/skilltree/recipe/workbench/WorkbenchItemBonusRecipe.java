package daripher.skilltree.recipe.workbench;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemBonusHandler;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class WorkbenchItemBonusRecipe extends AbstractWorkbenchRecipe {
  private final ItemStackPredicate baseItemStackPredicate;
  private final ItemBonus<?> itemBonus;

  public WorkbenchItemBonusRecipe(
      ResourceLocation id,
      ItemStackPredicate baseItemStackPredicate,
      Map<Ingredient, Integer> ingredients,
      boolean requiresPassiveSkill,
      ItemBonus<?> itemBonus) {
    super(id, ingredients, requiresPassiveSkill);
    this.baseItemStackPredicate = baseItemStackPredicate;
    this.itemBonus = itemBonus;
  }

  @Override
  public @NotNull ItemStack assemble(
      @NotNull WorkbenchContainer container, @NotNull HolderLookup.Provider registryAccess) {
    return getResult(container);
  }

  @Override
  public boolean isValidBaseItem(ItemStack itemStack) {
    return baseItemStackPredicate.test(itemStack);
  }

  @Override
  public Component getShortDescription() {
    List<MutableComponent> bonusTooltip = new ArrayList<>();
    itemBonus.addTooltip(bonusTooltip::add);
    Component itemTooltip = baseItemStackPredicate.getTooltip("plural");
    return Component.translatable(getDescriptionId(), bonusTooltip.get(0), itemTooltip);
  }

  @Override
  public List<Component> getFullDescription() {
    List<Component> fullDescription = new ArrayList<>();
    Style style = TooltipHelper.getItemBonusStyle();
    itemBonus.addTooltip(tooltip -> fullDescription.add(tooltip.withStyle(style)));
    Component itemTooltip = baseItemStackPredicate.getTooltip("plural");
    itemTooltip = Component.literal("[").append(itemTooltip).append("]");
    fullDescription.add(itemTooltip);
    return fullDescription;
  }

  @Override
  public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
    ItemStack baseItem = workbenchContainer.getBaseItem().copy();
    Player player = workbenchContainer.getPlayer();
    int craftedBonusLimit = ItemBonusHandler.getCraftedBonusLimit(baseItem, player);
    if (craftedBonusLimit <= 0) {
      return baseItem;
    }
    List<ItemBonus<?>> originalBonuses = new ArrayList<>(ItemBonusHandler.getItemBonuses(baseItem));
    while (craftedBonusLimit <= originalBonuses.size()) {
      originalBonuses.remove(0);
    }
    originalBonuses.add(itemBonus.copy());
    ItemBonusHandler.setItemBonuses(baseItem, originalBonuses);
    return baseItem;
  }

  @Override
  public int requiredBaseItemAmount() {
    return 1;
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.WORKBENCH_ITEM_BONUS.get();
  }

  public static class Serializer implements RecipeSerializer<WorkbenchItemBonusRecipe> {
    private static final ResourceLocation UNKNOWN_ID =
        ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "unknown");

    private static final Codec<ItemStackPredicate> ITEM_CONDITION_CODEC =
        WorkbenchRecipeCodecs.JSON_OBJECT.xmap(
            Serializer::deserializeItemCondition, Serializer::serializeItemCondition);

    private static final Codec<ItemBonus<?>> ITEM_BONUS_CODEC =
        WorkbenchRecipeCodecs.JSON_OBJECT.xmap(
            Serializer::deserializeItemBonus, Serializer::serializeItemBonus);

    private static final MapCodec<WorkbenchItemBonusRecipe> CODEC =
        RecordCodecBuilder.mapCodec(
            instance ->
                instance
                    .group(
                        ResourceLocation.CODEC
                            .optionalFieldOf("id", UNKNOWN_ID)
                            .forGetter(AbstractWorkbenchRecipe::getId),
                        ITEM_CONDITION_CODEC
                            .fieldOf("base_item_condition")
                            .forGetter(recipe -> recipe.baseItemStackPredicate),
                        ITEM_BONUS_CODEC.fieldOf("item_bonus").forGetter(recipe -> recipe.itemBonus),
                        WorkbenchRecipeCodecs.IngredientEntry.CODEC
                            .listOf()
                            .fieldOf("ingredients")
                            .xmap(WorkbenchRecipeCodecs::toMap, WorkbenchRecipeCodecs::toEntries)
                            .forGetter(AbstractWorkbenchRecipe::getAdditionalIngredients),
                        Codec.BOOL
                            .optionalFieldOf("requires_passive_skill", false)
                            .forGetter(AbstractWorkbenchRecipe::requiresPassiveSkill))
                    .apply(
                        instance,
                        (id, baseItemStackPredicate, itemBonus, ingredients, requiresPassiveSkill) ->
                            new WorkbenchItemBonusRecipe(
                                id,
                                baseItemStackPredicate,
                                ingredients,
                                requiresPassiveSkill,
                                itemBonus)));

    private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchItemBonusRecipe>
        STREAM_CODEC = StreamCodec.of(Serializer::encode, Serializer::decode);

    @Override
    public @NotNull MapCodec<WorkbenchItemBonusRecipe> codec() {
      return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, WorkbenchItemBonusRecipe> streamCodec() {
      return STREAM_CODEC;
    }

    private static WorkbenchItemBonusRecipe decode(RegistryFriendlyByteBuf buf) {
      ResourceLocation id = buf.readResourceLocation();
      ItemStackPredicate baseItemStackPredicate = NetworkHelper.readItemCondition(buf);
      ItemBonus<?> itemBonus = NetworkHelper.readItemBonus(buf);
      boolean requiresPassiveSkill = buf.readBoolean();
      Map<Ingredient, Integer> ingredients = WorkbenchRecipeCodecs.readIngredientMap(buf);
      return new WorkbenchItemBonusRecipe(
          id, baseItemStackPredicate, ingredients, requiresPassiveSkill, itemBonus);
    }

    private static void encode(RegistryFriendlyByteBuf buf, WorkbenchItemBonusRecipe recipe) {
      buf.writeResourceLocation(recipe.getId());
      NetworkHelper.writeItemCondition(buf, recipe.baseItemStackPredicate);
      NetworkHelper.writeItemBonus(buf, recipe.itemBonus);
      buf.writeBoolean(recipe.requiresPassiveSkill());
      WorkbenchRecipeCodecs.writeIngredientMap(buf, recipe.getAdditionalIngredients());
    }

    private static ItemStackPredicate deserializeItemCondition(JsonObject conditionJson) {
      JsonObject recipeJson = new JsonObject();
      recipeJson.add("base_item_condition", conditionJson);
      return SerializationHelper.deserializeItemCondition(recipeJson, "base_item_condition");
    }

    private static JsonObject serializeItemCondition(ItemStackPredicate itemStackPredicate) {
      JsonObject recipeJson = new JsonObject();
      SerializationHelper.serializeItemCondition(
          recipeJson, itemStackPredicate, "base_item_condition");
      return recipeJson.getAsJsonObject("base_item_condition");
    }

    private static ItemBonus<?> deserializeItemBonus(JsonObject itemBonusJson) {
      JsonObject recipeJson = new JsonObject();
      recipeJson.add("item_bonus", itemBonusJson);
      return SerializationHelper.deserializeItemBonus(recipeJson);
    }

    private static JsonObject serializeItemBonus(ItemBonus<?> itemBonus) {
      JsonObject recipeJson = new JsonObject();
      SerializationHelper.serializeItemBonus(recipeJson, itemBonus);
      return recipeJson.getAsJsonObject("item_bonus");
    }
  }
}
