package daripher.skilltree.recipe.workbench;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.util.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public class WorkbenchPotionMixingRecipe extends AbstractWorkbenchRecipe {
    public static final String IS_MIXTURE_TAG_NAME = "isMixture";

    public WorkbenchPotionMixingRecipe(ResourceLocation id, boolean requiresPassiveSkill) {
        super(id, requiresPassiveSkill);
    }

    @Override
    public @NotNull ItemStack assemble(
            @NotNull WorkbenchContainer container,
            @NotNull HolderLookup.Provider registryAccess) {
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
        CustomData.update(
                DataComponents.CUSTOM_DATA,
                itemStack,
                tag -> tag.putBoolean(IS_MIXTURE_TAG_NAME, true));
    }

    private boolean canMixPotion(ItemStack itemStack) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return !customData.copyTag().getBoolean(IS_MIXTURE_TAG_NAME);
    }

    private Ingredient getPotionItemIngredient(PotionItem baseItem) {
        Collection<Potion> availablePotions = ForgeRegistries.POTIONS.getValues();
        Stream<Potion> potionsWithEffects =
                availablePotions.stream().filter(potion -> !potion.getEffects().isEmpty());
        Stream<ItemStack> suitablePotionStacks =
                potionsWithEffects.map(potion -> getPotionStack(baseItem, potion));
        return Ingredient.of(suitablePotionStacks.toArray(ItemStack[]::new));
    }

    private Ingredient getAllPotionsIngredient() {
        Collection<Potion> availablePotions = ForgeRegistries.POTIONS.getValues();
        Stream<Potion> potions =
                availablePotions.stream().filter(potion -> !potion.getEffects().isEmpty());
        List<ItemStack> suitablePotionStacks = new ArrayList<>();
        List<PotionItem> potionItems = ForgeRegistries.ITEMS.getValues().stream()
                .filter(PotionItem.class::isInstance)
                .map(PotionItem.class::cast)
                .toList();
        potions.forEach(potion -> potionItems.forEach(
                potionItem -> suitablePotionStacks.add(getPotionStack(potionItem, potion))));
        return Ingredient.of(suitablePotionStacks.toArray(ItemStack[]::new));
    }

    private static @NotNull ItemStack getPotionStack(Item baseItem, Potion potion) {
        return PotionContents.createItemStack(baseItem, ForgeRegistries.POTIONS.wrapAsHolder(potion));
    }

    @Override
    public Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseItem) {
        Map<Ingredient, Integer> allPotionsIngredient = Map.of(getAllPotionsIngredient(), 1);
        if (baseItem.getItem() instanceof PotionItem potionItem) {
            return Map.of(getPotionItemIngredient(potionItem), 1);
        }
        return allPotionsIngredient;
    }

    @Override
    public Component getShortDescription() {
        return Component.translatable(getDescriptionId());
    }

    @Override
    public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
        ItemStack firstPotion = workbenchContainer.getBaseItem();
        ItemStack secondPotion = workbenchContainer.getItem(1);
        ItemStack result = new ItemStack(firstPotion.getItem());
        List<MobEffectInstance> effects = new ArrayList<>();
        getPotionContents(firstPotion).getAllEffects().forEach(effects::add);
        getPotionContents(secondPotion).getAllEffects().forEach(effects::add);
        int color = mixHexColors(
                getPotionContents(firstPotion).getColor(),
                getPotionContents(secondPotion).getColor());
        result.set(
                DataComponents.POTION_CONTENTS,
                new PotionContents(Optional.empty(), Optional.of(color), effects));
        result.set(
                DataComponents.CUSTOM_NAME,
                Component.translatable(firstPotion.getItem().getDescriptionId() + ".mixture"));
        setIsMixtureTag(result);
        return result;
    }

    private static PotionContents getPotionContents(ItemStack stack) {
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    }

    private int mixHexColors(int color1, int color2) {
        return (((color1 ^ color2) & 0xFEFEFE) >> 1) + (color1 & color2);
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
        private static final ResourceLocation UNKNOWN_ID =
                ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "unknown");
        private static final MapCodec<WorkbenchPotionMixingRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                                ResourceLocation.CODEC.optionalFieldOf("id", UNKNOWN_ID)
                                        .forGetter(AbstractWorkbenchRecipe::getId),
                                Codec.BOOL.optionalFieldOf("requires_passive_skill", false)
                                        .forGetter(AbstractWorkbenchRecipe::hasPassiveSkillRequirement))
                        .apply(instance, WorkbenchPotionMixingRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchPotionMixingRecipe>
                STREAM_CODEC = StreamCodec.of(Serializer::encode, Serializer::decode);

        @Override
        public @NotNull MapCodec<WorkbenchPotionMixingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, WorkbenchPotionMixingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static WorkbenchPotionMixingRecipe decode(RegistryFriendlyByteBuf buf) {
            return new WorkbenchPotionMixingRecipe(
                    buf.readResourceLocation(), buf.readBoolean());
        }

        private static void encode(RegistryFriendlyByteBuf buf, WorkbenchPotionMixingRecipe recipe) {
            buf.writeResourceLocation(recipe.getId());
            buf.writeBoolean(recipe.hasPassiveSkillRequirement());
        }
    }
}
