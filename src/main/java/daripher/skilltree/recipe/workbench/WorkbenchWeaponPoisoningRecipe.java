package daripher.skilltree.recipe.workbench;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.event.PoisonedWeaponEvents;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import daripher.skilltree.util.ForgeRegistries;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public class WorkbenchWeaponPoisoningRecipe extends AbstractWorkbenchRecipe {
    private final int maxUses;

    public WorkbenchWeaponPoisoningRecipe(
            ResourceLocation id, boolean requiresPassiveSkill, int maxUses) {
        super(id, requiresPassiveSkill);
        this.maxUses = maxUses;
    }

    @Override
    public @NotNull ItemStack assemble(
            @NotNull WorkbenchContainer container,
            @NotNull HolderLookup.Provider registryAccess) {
        return getResult(container);
    }

    @Override
    public boolean isValidBaseItem(ItemStack itemStack) {
        return EquipmentPredicate.isMeleeWeapon(itemStack);
    }

    @Override
    public boolean isValidIngredient(ItemStack itemStack) {
        return isValidPoison(itemStack);
    }

    private boolean isValidPoison(ItemStack itemStack) {
        PotionContents contents =
                itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return StreamSupport.stream(contents.getAllEffects().spliterator(), false)
                .anyMatch(WorkbenchWeaponPoisoningRecipe::isHarmfulEffect);
    }

    @Override
    public Pair<Ingredient, Integer> getBaseIngredient() {
        return Pair.of(getMeleeWeaponIngredient(), 1);
    }

    private Ingredient getMeleeWeaponIngredient() {
        Collection<Item> items = ForgeRegistries.ITEMS.getValues();
        Stream<ItemStack> meleeWeapons =
                items.stream().map(ItemStack::new).filter(EquipmentPredicate::isMeleeWeapon);
        return Ingredient.of(meleeWeapons.toArray(ItemStack[]::new));
    }

    @Override
    public Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseIngredient) {
        return Map.of(getPoisonIngredient(), 1);
    }

    private Ingredient getPoisonIngredient() {
        Collection<Potion> availablePotions = ForgeRegistries.POTIONS.getValues();
        Stream<ItemStack> suitablePotionStacks = availablePotions.stream()
                .filter(WorkbenchWeaponPoisoningRecipe::isHarmfulPotion)
                .map(potion -> PotionContents.createItemStack(
                        Items.POTION, ForgeRegistries.POTIONS.wrapAsHolder(potion)));
        return Ingredient.of(suitablePotionStacks.toArray(ItemStack[]::new));
    }

    private static boolean isHarmfulPotion(Potion potion) {
        return potion.getEffects().stream().anyMatch(WorkbenchWeaponPoisoningRecipe::isHarmfulEffect);
    }

    private static boolean isHarmfulEffect(MobEffectInstance effect) {
        return effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL;
    }

    @Override
    public Component getShortDescription() {
        return Component.translatable(getDescriptionId());
    }

    @Override
    public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
        ItemStack weaponStack = workbenchContainer.getBaseItem();
        ItemStack potionStack = workbenchContainer.getItem(1);
        ItemStack result = weaponStack.copyWithCount(1);
        PoisonedWeaponEvents.setPoisonedWeaponEffects(result, potionStack, maxUses);
        return result;
    }

    @Override
    public int requiredBaseItemAmount() {
        return 1;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return PSTRecipeSerializers.WORKBENCH_WEAPON_POISONING.get();
    }

    public static class Serializer implements RecipeSerializer<WorkbenchWeaponPoisoningRecipe> {
        private static final ResourceLocation UNKNOWN_ID =
                ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "unknown");
        private static final MapCodec<WorkbenchWeaponPoisoningRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                                ResourceLocation.CODEC.optionalFieldOf("id", UNKNOWN_ID)
                                        .forGetter(AbstractWorkbenchRecipe::getId),
                                Codec.BOOL.optionalFieldOf("requires_passive_skill", false)
                                        .forGetter(AbstractWorkbenchRecipe::hasPassiveSkillRequirement),
                                Codec.INT.fieldOf("max_uses")
                                        .forGetter(recipe -> recipe.maxUses))
                        .apply(instance, WorkbenchWeaponPoisoningRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchWeaponPoisoningRecipe>
                STREAM_CODEC = StreamCodec.of(Serializer::encode, Serializer::decode);

        @Override
        public @NotNull MapCodec<WorkbenchWeaponPoisoningRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, WorkbenchWeaponPoisoningRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static WorkbenchWeaponPoisoningRecipe decode(RegistryFriendlyByteBuf buf) {
            return new WorkbenchWeaponPoisoningRecipe(
                    buf.readResourceLocation(), buf.readBoolean(), buf.readVarInt());
        }

        private static void encode(
                RegistryFriendlyByteBuf buf, WorkbenchWeaponPoisoningRecipe recipe) {
            buf.writeResourceLocation(recipe.getId());
            buf.writeBoolean(recipe.hasPassiveSkillRequirement());
            buf.writeVarInt(recipe.maxUses);
        }
    }
}
