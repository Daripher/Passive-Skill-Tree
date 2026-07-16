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
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorkbenchUpgradeBonusRecipe extends AbstractWorkbenchRecipe {
    private final ItemStackPredicate baseItemStackPredicate;
    private final Map<Ingredient, Integer> additionalIngredients;
    private final ItemBonus<?> itemBonus;

    public WorkbenchUpgradeBonusRecipe(
            ResourceLocation id,
            ItemStackPredicate baseItemStackPredicate,
            Map<Ingredient, Integer> additionalIngredients,
            boolean requiresPassiveSkill,
            ItemBonus<?> itemBonus) {
        super(id, requiresPassiveSkill);
        this.baseItemStackPredicate = baseItemStackPredicate;
        this.itemBonus = itemBonus;
        this.additionalIngredients = additionalIngredients;
    }

    @Override
    public @NotNull ItemStack assemble(
            @NotNull WorkbenchContainer container,
            @NotNull HolderLookup.Provider registryAccess) {
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
        fullDescription.add(Component.literal("[").append(itemTooltip).append("]"));
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
        List<ItemBonus<?>> originalBonuses =
                new ArrayList<>(ItemBonusHandler.getItemBonuses(baseItem));
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
    public @Nullable Pair<Ingredient, Integer> getBaseIngredient() {
        return null;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return PSTRecipeSerializers.WORKBENCH_ITEM_BONUS.get();
    }

    public static class Serializer implements RecipeSerializer<WorkbenchUpgradeBonusRecipe> {
        private static final ResourceLocation UNKNOWN_ID =
                ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "unknown");

        private static final Codec<ItemStackPredicate> ITEM_PREDICATE_CODEC =
                WorkbenchRecipeCodecs.JSON_OBJECT.xmap(
                        Serializer::deserializeItemPredicate, Serializer::serializeItemPredicate);
        private static final Codec<ItemBonus<?>> ITEM_BONUS_CODEC =
                WorkbenchRecipeCodecs.JSON_OBJECT.xmap(
                        Serializer::deserializeItemBonus, Serializer::serializeItemBonus);

        private static final MapCodec<WorkbenchUpgradeBonusRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                                ResourceLocation.CODEC.optionalFieldOf("id", UNKNOWN_ID)
                                        .forGetter(AbstractWorkbenchRecipe::getId),
                                ITEM_PREDICATE_CODEC.fieldOf("base_item_condition")
                                        .forGetter(recipe -> recipe.baseItemStackPredicate),
                                ITEM_BONUS_CODEC.fieldOf("item_bonus")
                                        .forGetter(recipe -> recipe.itemBonus),
                                WorkbenchRecipeCodecs.IngredientEntry.CODEC.listOf()
                                        .fieldOf("ingredients")
                                        .xmap(WorkbenchRecipeCodecs::toMap, WorkbenchRecipeCodecs::toEntries)
                                        .forGetter(WorkbenchUpgradeBonusRecipe::getAdditionalIngredients),
                                Codec.BOOL.optionalFieldOf("requires_passive_skill", false)
                                        .forGetter(AbstractWorkbenchRecipe::hasPassiveSkillRequirement))
                        .apply(instance, (id, predicate, itemBonus, ingredients, requiresPassiveSkill) ->
                                new WorkbenchUpgradeBonusRecipe(
                                        id, predicate, ingredients, requiresPassiveSkill, itemBonus)));

        private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchUpgradeBonusRecipe>
                STREAM_CODEC = StreamCodec.of(Serializer::encode, Serializer::decode);

        @Override
        public @NotNull MapCodec<WorkbenchUpgradeBonusRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, WorkbenchUpgradeBonusRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static WorkbenchUpgradeBonusRecipe decode(RegistryFriendlyByteBuf buf) {
            ResourceLocation id = buf.readResourceLocation();
            ItemStackPredicate baseItemStackPredicate = NetworkHelper.readItemPredicate(buf);
            ItemBonus<?> itemBonus = NetworkHelper.readItemBonus(buf);
            boolean requiresPassiveSkill = buf.readBoolean();
            Map<Ingredient, Integer> ingredients = WorkbenchRecipeCodecs.readIngredientMap(buf);
            return new WorkbenchUpgradeBonusRecipe(
                    id, baseItemStackPredicate, ingredients, requiresPassiveSkill, itemBonus);
        }

        private static void encode(RegistryFriendlyByteBuf buf, WorkbenchUpgradeBonusRecipe recipe) {
            buf.writeResourceLocation(recipe.getId());
            NetworkHelper.writeItemPredicate(buf, recipe.baseItemStackPredicate);
            NetworkHelper.writeItemBonus(buf, recipe.itemBonus);
            buf.writeBoolean(recipe.hasPassiveSkillRequirement());
            WorkbenchRecipeCodecs.writeIngredientMap(buf, recipe.additionalIngredients);
        }

        private static ItemStackPredicate deserializeItemPredicate(JsonObject predicateJson) {
            JsonObject wrapper = new JsonObject();
            wrapper.add("base_item_condition", predicateJson);
            return SerializationHelper.deserializeItemPredicate(wrapper, "base_item_condition");
        }

        private static JsonObject serializeItemPredicate(ItemStackPredicate predicate) {
            JsonObject wrapper = new JsonObject();
            SerializationHelper.serializeItemPredicate(wrapper, predicate, "base_item_condition");
            return wrapper.getAsJsonObject("base_item_condition");
        }

        private static ItemBonus<?> deserializeItemBonus(JsonObject itemBonusJson) {
            JsonObject wrapper = new JsonObject();
            wrapper.add("item_bonus", itemBonusJson);
            return SerializationHelper.deserializeItemBonus(wrapper);
        }

        private static JsonObject serializeItemBonus(ItemBonus<?> itemBonus) {
            JsonObject wrapper = new JsonObject();
            SerializationHelper.serializeItemBonus(wrapper, itemBonus);
            return wrapper.getAsJsonObject("item_bonus");
        }
    }
}
