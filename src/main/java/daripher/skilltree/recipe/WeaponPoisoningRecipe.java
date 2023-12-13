package daripher.skilltree.recipe;

import com.google.gson.JsonObject;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.item.ItemHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WeaponPoisoningRecipe extends CustomRecipe implements SkillRequiringRecipe {
  public WeaponPoisoningRecipe(ResourceLocation id) {
    super(id, CraftingBookCategory.MISC);
  }

  @Override
  public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {
    if (isUncraftable(container, this)) return false;
    int weaponsCount = 0;
    int poisonsCount = 0;
    for (int slot = 0; slot < container.getContainerSize(); slot++) {
      ItemStack stackInSlot = container.getItem(slot);
      if (stackInSlot.isEmpty()) continue;
      if (ItemHelper.isMeleeWeapon(stackInSlot)) {
        weaponsCount++;
        continue;
      }
      if (isPoison(stackInSlot)) poisonsCount++;
    }
    return weaponsCount == 1 && poisonsCount == 1;
  }

  @Override
  public @NotNull ItemStack assemble(@NotNull CraftingContainer container, @NotNull RegistryAccess access) {
    if (isUncraftable(container, this)) return ItemStack.EMPTY;
    ItemStack weaponStack = ItemStack.EMPTY;
    ItemStack poisonStack = ItemStack.EMPTY;
    for (int slot = 0; slot < container.getContainerSize(); slot++) {
      ItemStack stackInSlot = container.getItem(slot);
      if (stackInSlot.isEmpty()) continue;
      if (ItemHelper.isMeleeWeapon(stackInSlot)) weaponStack = stackInSlot;
      if (isPoison(stackInSlot)) poisonStack = stackInSlot;
    }
    ItemStack result = weaponStack.copy();
    ItemHelper.setPoisons(result, poisonStack);
    return result;
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.WEAPON_POISONING.get();
  }

  private boolean isPoison(ItemStack stack) {
    return PotionUtils.getAllEffects(stack.getTag()).stream()
        .anyMatch(effect -> effect.getEffect().getCategory() == MobEffectCategory.HARMFUL);
  }

  public static class Serializer implements RecipeSerializer<WeaponPoisoningRecipe> {
    @Override
    public @NotNull WeaponPoisoningRecipe fromJson(
        @NotNull ResourceLocation id, @NotNull JsonObject jsonObject) {
      return new WeaponPoisoningRecipe(id);
    }

    @Override
    public WeaponPoisoningRecipe fromNetwork(
        @NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
      return new WeaponPoisoningRecipe(id);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull WeaponPoisoningRecipe recipe) {}
  }
}
