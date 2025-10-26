package daripher.skilltree.recipe.workbench;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemBonusHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorkbenchItemBonusRecipe extends AbstractWorkbenchRecipe {
  private final ItemCondition baseItemCondition;
  private final ItemBonus<?> itemBonus;

  public WorkbenchItemBonusRecipe(
      ResourceLocation id,
      ItemCondition baseItemCondition,
      Map<Ingredient, Integer> ingredients,
      boolean requiresPassiveSkill,
      ItemBonus<?> itemBonus) {
    super(id, ingredients, requiresPassiveSkill);
    this.baseItemCondition = baseItemCondition;
    this.itemBonus = itemBonus;
  }

  @Override
  public @NotNull ItemStack assemble(
      @NotNull WorkbenchContainer container, @NotNull RegistryAccess registryAccess) {
    return getResult(container);
  }

  @Override
  public boolean isValidBaseItem(ItemStack itemStack) {
    return baseItemCondition.met(itemStack);
  }

  @Override
  public Component getTooltip() {
    return itemBonus.getTooltip();
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
    @Override
    public @NotNull WorkbenchItemBonusRecipe fromJson(
        @NotNull ResourceLocation id, @NotNull JsonObject jsonObject) {
      ItemCondition baseItemCondition =
          SerializationHelper.deserializeItemCondition(jsonObject, "base_item_condition");
      ItemBonus<?> itemBonus = SerializationHelper.deserializeItemBonus(jsonObject);
      boolean requiresPassiveSkill = jsonObject.get("requires_passive_skill").getAsBoolean();
      Map<Ingredient, Integer> ingredients = new HashMap<>();
      JsonArray ingredientsJson = jsonObject.getAsJsonArray("ingredients");
      for (JsonElement jsonElement : ingredientsJson) {
        Ingredient ingredient =
            Ingredient.fromJson(jsonElement.getAsJsonObject().get("ingredient"));
        int requiredAmount = jsonElement.getAsJsonObject().get("required_amount").getAsInt();
        ingredients.put(ingredient, requiredAmount);
      }
      return new WorkbenchItemBonusRecipe(
          id, baseItemCondition, ingredients, requiresPassiveSkill, itemBonus);
    }

    @Override
    public @Nullable WorkbenchItemBonusRecipe fromNetwork(
        @NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
      ItemCondition baseItemCondition = NetworkHelper.readItemCondition(buf);
      ItemBonus<?> itemBonus = NetworkHelper.readItemBonus(buf);
      boolean requiresPassiveSkill = buf.readBoolean();
      Map<Ingredient, Integer> ingredients = new HashMap<>();
      int ingredientsCount = buf.readInt();
      for (int i = 0; i < ingredientsCount; i++) {
        ingredients.put(Ingredient.fromNetwork(buf), buf.readInt());
      }
      return new WorkbenchItemBonusRecipe(
          id, baseItemCondition, ingredients, requiresPassiveSkill, itemBonus);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull WorkbenchItemBonusRecipe recipe) {
      NetworkHelper.writeItemCondition(buf, recipe.baseItemCondition);
      NetworkHelper.writeItemBonus(buf, recipe.itemBonus);
      buf.writeBoolean(recipe.requiresPassiveSkill());
      int ingredientsCount = recipe.getAdditionalIngredients().size();
      buf.writeInt(ingredientsCount);
      recipe
          .getAdditionalIngredients()
          .forEach(
              (ingredient, requiredAmount) -> {
                ingredient.toNetwork(buf);
                buf.writeInt(requiredAmount);
              });
    }
  }
}
