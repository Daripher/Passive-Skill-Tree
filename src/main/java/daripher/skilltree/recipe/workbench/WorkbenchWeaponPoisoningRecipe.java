package daripher.skilltree.recipe.workbench;

import com.google.gson.JsonObject;
import daripher.skilltree.event.PoisonedWeaponEvents;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class WorkbenchWeaponPoisoningRecipe extends AbstractWorkbenchRecipe {
    private final int maxUses;

    public WorkbenchWeaponPoisoningRecipe(ResourceLocation id, boolean requiresPassiveSkill, int maxUses) {
        super(id, requiresPassiveSkill);
        this.maxUses = maxUses;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull WorkbenchContainer container, @NotNull RegistryAccess registryAccess) {
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
        Stream<MobEffectInstance> effectsStream = PotionUtils.getMobEffects(itemStack).stream();
        return effectsStream.anyMatch(mobEffectInstance -> mobEffectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL);
    }

    @Override
    public Pair<Ingredient, Integer> getBaseIngredient() {
        return Pair.of(getMeleeWeaponIngredient(), 1);
    }

    private Ingredient getMeleeWeaponIngredient() {
        Collection<Item> items = ForgeRegistries.ITEMS.getValues();
        Stream<ItemStack> meleeWeapons = items.stream().map(ItemStack::new).filter(EquipmentPredicate::isMeleeWeapon);
        return Ingredient.of(meleeWeapons.toList().toArray(new ItemStack[0]));
    }

    @Override
    public Map<Ingredient, Integer> getAdditionalIngredients(ItemStack baseIngredient) {
        return Map.of(getPoisonIngredient(), 1);
    }

    private Ingredient getPoisonIngredient() {
        Item baseItem = Items.POTION;
        Collection<Potion> availablePotions = ForgeRegistries.POTIONS.getValues();
        Stream<Potion> harmfulPotions = availablePotions.stream().filter(WorkbenchWeaponPoisoningRecipe::isHarmfulPotion);
        Stream<ItemStack> suitablePotionStacks = harmfulPotions.map(potion -> getPotionStack(baseItem, potion));
        return Ingredient.of(suitablePotionStacks.toList().toArray(new ItemStack[0]));
    }

    private static boolean isHarmfulPotion(Potion potion) {
        List<MobEffectInstance> effects = potion.getEffects();
        for (MobEffectInstance mobEffectInstance : effects) {
            if (mobEffectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                return true;
            }
        }
        return false;
    }

    private static @NotNull ItemStack getPotionStack(Item baseItem, Potion potion) {
        ItemStack itemStack = new ItemStack(baseItem);
        PotionUtils.setPotion(itemStack, potion);
        return itemStack;
    }

    @Override
    public Component getShortDescription() {
        return Component.translatable(getDescriptionId());
    }

    @Override
    public @NotNull ItemStack getResult(WorkbenchContainer workbenchContainer) {
        ItemStack weaponStack = workbenchContainer.getBaseItem();
        ItemStack potionStack = workbenchContainer.getItem(1);
        ItemStack resultItemStack = new ItemStack(weaponStack.getItem());
        PoisonedWeaponEvents.setPoisonedWeaponEffects(resultItemStack, potionStack, maxUses);
        return resultItemStack;
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
        @Override
        public @NotNull WorkbenchWeaponPoisoningRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject jsonObject) {
            boolean requiresPassiveSkill = jsonObject.get("requires_passive_skill").getAsBoolean();
            int maxUses = jsonObject.get("max_uses").getAsInt();
            return new WorkbenchWeaponPoisoningRecipe(id, requiresPassiveSkill, maxUses);
        }

        @Override
        public @Nullable WorkbenchWeaponPoisoningRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            boolean requiresPassiveSkill = buf.readBoolean();
            int maxUses = buf.readInt();
            return new WorkbenchWeaponPoisoningRecipe(id, requiresPassiveSkill, maxUses);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull WorkbenchWeaponPoisoningRecipe recipe) {
            buf.writeBoolean(recipe.hasPassiveSkillRequirement());
            buf.writeInt(recipe.maxUses);
        }
    }
}
