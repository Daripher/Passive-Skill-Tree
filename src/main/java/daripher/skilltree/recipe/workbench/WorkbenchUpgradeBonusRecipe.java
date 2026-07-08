package daripher.skilltree.recipe.workbench;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemBonusHandler;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkbenchUpgradeBonusRecipe extends AbstractWorkbenchRecipe {
    private final ItemStackPredicate baseItemStackPredicate;
    private final Map<Ingredient, Integer> additionalIngredients;
    private final ItemBonus<?> itemBonus;

    public WorkbenchUpgradeBonusRecipe(ResourceLocation id, ItemStackPredicate baseItemStackPredicate, Map<Ingredient, Integer> additionalIngredients, boolean requiresPassiveSkill, ItemBonus<?> itemBonus) {
        super(id, requiresPassiveSkill);
        this.baseItemStackPredicate = baseItemStackPredicate;
        this.itemBonus = itemBonus;
        this.additionalIngredients = additionalIngredients;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull WorkbenchContainer container, @NotNull RegistryAccess registryAccess) {
        return getResult(container);
    }

    @Override
    public boolean isValidBaseItem(ItemStack itemStack) {
        return baseItemStackPredicate.test(itemStack);
    }

    @Override
    public Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseIngredient) {
        return additionalIngredients;
    }

    public Map<Ingredient, Integer> getAdditionalIngredients() {
        return additionalIngredients;
    }

    @Override
    public Component getShortDescription() {
        Component itemTooltip = baseItemStackPredicate.getTooltip("plural");
        return Component.translatable(getDescriptionId(), itemBonus.getFullTooltip().get(0), itemTooltip);
    }

    @Override
    public List<Component> getFullDescription() {
        List<Component> fullDescription = new ArrayList<>();
        Style style = TooltipHelper.getItemUpgradeStyle();
        for (MutableComponent mutableComponent : itemBonus.getFullTooltip()) {
            fullDescription.add(mutableComponent.withStyle(style));
        }
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
        ItemBonusHandler.setUpgradeBonuses(baseItem, originalBonuses);
        return baseItem;
    }

    @Override
    public int requiredBaseItemAmount() {
        return 1;
    }

    @Override
    public Pair<Ingredient, Integer> getBaseIngredient() {
        return null;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return PSTRecipeSerializers.WORKBENCH_ITEM_BONUS.get();
    }

    public static class Serializer implements RecipeSerializer<WorkbenchUpgradeBonusRecipe> {
        @Override
        public @NotNull WorkbenchUpgradeBonusRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject jsonObject) {
            ItemStackPredicate baseItemStackPredicate = SerializationHelper.deserializeItemPredicate(jsonObject, "base_item_condition");
            ItemBonus<?> itemBonus = SerializationHelper.deserializeItemBonus(jsonObject);
            boolean requiresPassiveSkill = jsonObject.get("requires_passive_skill").getAsBoolean();
            Map<Ingredient, Integer> additionalIngredients = new HashMap<>();
            JsonArray ingredientsJson = jsonObject.getAsJsonArray("additionalIngredients");
            for (JsonElement jsonElement : ingredientsJson) {
                Ingredient ingredient = Ingredient.fromJson(jsonElement.getAsJsonObject().get("ingredient"));
                int requiredAmount = jsonElement.getAsJsonObject().get("required_amount").getAsInt();
                additionalIngredients.put(ingredient, requiredAmount);
            }
            return new WorkbenchUpgradeBonusRecipe(id, baseItemStackPredicate, additionalIngredients, requiresPassiveSkill, itemBonus);
        }

        @Override
        public @Nullable WorkbenchUpgradeBonusRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            ItemStackPredicate baseItemStackPredicate = NetworkHelper.readItemPredicate(buf);
            ItemBonus<?> itemBonus = NetworkHelper.readItemBonus(buf);
            boolean requiresPassiveSkill = buf.readBoolean();
            Map<Ingredient, Integer> additionalIngredients = new HashMap<>();
            int ingredientsCount = buf.readInt();
            for (int i = 0; i < ingredientsCount; i++) {
                additionalIngredients.put(Ingredient.fromNetwork(buf), buf.readInt());
            }
            return new WorkbenchUpgradeBonusRecipe(id, baseItemStackPredicate, additionalIngredients, requiresPassiveSkill, itemBonus);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull WorkbenchUpgradeBonusRecipe recipe) {
            NetworkHelper.writeItemPredicate(buf, recipe.baseItemStackPredicate);
            NetworkHelper.writeItemBonus(buf, recipe.itemBonus);
            buf.writeBoolean(recipe.hasPassiveSkillRequirement());
            int ingredientsCount = recipe.getAdditionalIngredients().size();
            buf.writeInt(ingredientsCount);
            recipe.getAdditionalIngredients().forEach((ingredient, requiredAmount) -> {
                ingredient.toNetwork(buf);
                buf.writeInt(requiredAmount);
            });
        }
    }
}
