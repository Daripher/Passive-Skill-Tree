package daripher.skilltree.recipe.workbench;

import com.google.gson.JsonObject;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class WorkbenchPotionMixingRecipe extends AbstractWorkbenchRecipe {
    public static final String IS_MIXTURE_TAG_NAME = "isMixture";

    public WorkbenchPotionMixingRecipe(ResourceLocation id, boolean requiresPassiveSkill) {
        super(id, requiresPassiveSkill);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull WorkbenchContainer container, @NotNull RegistryAccess registryAccess) {
        return getResult(container);
    }

    @Override
    public boolean isValidBaseItem(ItemStack itemStack) {
        return isValidPotion(itemStack);
    }

    @Override
    public boolean isValidIngredient(ItemStack itemStack) {
        return isValidPotion(itemStack);
    }

    private boolean isValidPotion(ItemStack itemStack) {
        return itemStack.getItem() instanceof PotionItem && canMixPotion(itemStack);
    }

    @Override
    public Pair<Ingredient, Integer> getBaseIngredient() {
        return Pair.of(getAllPotionsIngredient(), 1);
    }

    private void setIsMixtureTag(ItemStack itemStack) {
        itemStack.getOrCreateTag().putBoolean(IS_MIXTURE_TAG_NAME, true);
    }

    private boolean canMixPotion(ItemStack itemStack) {
        if (!itemStack.hasTag()) {
            return true;
        }
        return !itemStack.getOrCreateTag().getBoolean(IS_MIXTURE_TAG_NAME);
    }

    private Ingredient getPotionItemIngredient(PotionItem baseItem) {
        Collection<Potion> availablePotions = ForgeRegistries.POTIONS.getValues();
        Stream<Potion> potionsWithEffects = availablePotions.stream().filter(potion -> !potion.getEffects().isEmpty());
        Stream<ItemStack> suitablePotionStacks = potionsWithEffects.map(potion -> getPotionStack(baseItem, potion));
        return Ingredient.of(suitablePotionStacks.toList().toArray(new ItemStack[0]));
    }

    private Ingredient getAllPotionsIngredient() {
        Collection<Potion> availablePotions = ForgeRegistries.POTIONS.getValues();
        Stream<Potion> potions = availablePotions.stream().filter(potion -> !potion.getEffects().isEmpty());
        List<ItemStack> suitablePotionStacks = new ArrayList<>();
        potions.forEach(potion -> {
            List<PotionItem> potionItems = ForgeRegistries.ITEMS.getValues().stream().filter(PotionItem.class::isInstance)
                    .map(PotionItem.class::cast).toList();
            potionItems.forEach(potionItem -> suitablePotionStacks.add(getPotionStack(potionItem, potion)));
        });
        return Ingredient.of(suitablePotionStacks.toArray(new ItemStack[0]));
    }

    private static @NotNull ItemStack getPotionStack(PotionItem baseItem, Potion potion) {
        ItemStack itemStack = new ItemStack(baseItem);
        PotionUtils.setPotion(itemStack, potion);
        return itemStack;
    }

    @Override
    public Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseItem) {
        Map<Ingredient, Integer> allPotionsIngredient = Map.of(getAllPotionsIngredient(), 1);
        if (baseItem.isEmpty()) {
            return allPotionsIngredient;
        }
        Item item = baseItem.getItem();
        if (!(item instanceof PotionItem potionItem)) {
            return allPotionsIngredient;
        }
        return Map.of(getPotionItemIngredient(potionItem), 1);
    }

    @Override
    public Component getShortDescription() {
        return Component.translatable(getDescriptionId());
    }

    @Override
    public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
        ItemStack potionStack1 = workbenchContainer.getBaseItem();
        ItemStack potionStack2 = workbenchContainer.getItem(1);
        ItemStack resultItemStack = new ItemStack(potionStack1.getItem());
        setMixtureEffects(potionStack1, potionStack2, resultItemStack);
        setMixtureColor(potionStack1, potionStack2, resultItemStack);
        setMixtureName(potionStack1, resultItemStack);
        setIsMixtureTag(resultItemStack);
        return resultItemStack;
    }

    private void setMixtureEffects(ItemStack potionStack1, ItemStack potionStack2, ItemStack resultItemStack) {
        List<MobEffectInstance> mobEffectInstances = new ArrayList<>();
        mobEffectInstances.addAll(PotionUtils.getMobEffects(potionStack1));
        mobEffectInstances.addAll(PotionUtils.getMobEffects(potionStack2));
        PotionUtils.setCustomEffects(resultItemStack, mobEffectInstances);
    }

    private void setMixtureColor(ItemStack potionStack1, ItemStack potionStack2, ItemStack resultItemStack) {
        int potionColor = mixHexColors(PotionUtils.getColor(potionStack1), PotionUtils.getColor(potionStack2));
        resultItemStack.getOrCreateTag().putInt(PotionUtils.TAG_CUSTOM_POTION_COLOR, potionColor);
    }

    private void setMixtureName(ItemStack potionStack1, ItemStack resultItemStack) {
        String descriptionId = potionStack1.getItem().getDescriptionId() + ".mixture";
        MutableComponent itemStackName = Component.translatable(descriptionId);
        resultItemStack.setHoverName(itemStackName);
    }

    private int mixHexColors(int color1, int color2) {
        return ((color1 ^ color2) & 0xFEFEFE) >> 1 + (color1 & color2);
    }

    @Override
    public int requiredBaseItemAmount() {
        return 1;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return PSTRecipeSerializers.WORKBENCH_POTION_MIXING.get();
    }

    public static class Serializer implements RecipeSerializer<WorkbenchPotionMixingRecipe> {
        @Override
        public @NotNull WorkbenchPotionMixingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject jsonObject) {
            boolean requiresPassiveSkill = jsonObject.get("requires_passive_skill").getAsBoolean();
            return new WorkbenchPotionMixingRecipe(id, requiresPassiveSkill);
        }

        @Override
        public @Nullable WorkbenchPotionMixingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            boolean requiresPassiveSkill = buf.readBoolean();
            return new WorkbenchPotionMixingRecipe(id, requiresPassiveSkill);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull WorkbenchPotionMixingRecipe recipe) {
            buf.writeBoolean(recipe.hasPassiveSkillRequirement());
        }
    }
}
