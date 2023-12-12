package daripher.skilltree.recipe;

import com.google.gson.JsonObject;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.potion.PotionHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MixtureRecipe extends CustomRecipe implements SkillRequiringRecipe {
  public MixtureRecipe(ResourceLocation id) {
    super(id);
  }

  @Override
  public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {
    if (isUncraftable(container, this)) return false;
    ItemStack potionStack1 = ItemStack.EMPTY;
    ItemStack potionStack2 = ItemStack.EMPTY;
    int potionsCount = 0;
    for (int slot = 0; slot < container.getContainerSize(); slot++) {
      ItemStack stackInSlot = container.getItem(slot);
      if (stackInSlot.isEmpty()) continue;
      if (PotionHelper.isPotion(stackInSlot) && !PotionHelper.isMixture(stackInSlot)) {
        potionsCount++;
        if (potionStack1.isEmpty()) {
          potionStack1 = stackInSlot;
        } else {
          potionStack2 = stackInSlot;
        }
      }
    }
    if (PotionUtils.getMobEffects(potionStack1).isEmpty()
        || PotionUtils.getMobEffects(potionStack2).isEmpty()) {
      return false;
    }
    return potionsCount == 2 && potionStack1.getItem() == potionStack2.getItem();
  }

  @Override
  public @NotNull ItemStack assemble(@NotNull CraftingContainer container) {
    if (isUncraftable(container, this)) return ItemStack.EMPTY;
    ItemStack potionStack1 = ItemStack.EMPTY;
    ItemStack potionStack2 = ItemStack.EMPTY;
    for (int slot = 0; slot < container.getContainerSize(); slot++) {
      ItemStack stackInSlot = container.getItem(slot);
      if (stackInSlot.isEmpty()) continue;
      if (PotionHelper.isPotion(stackInSlot) && !PotionHelper.isMixture(stackInSlot)) {
        if (potionStack1.isEmpty()) {
          potionStack1 = stackInSlot;
        } else {
          potionStack2 = stackInSlot;
        }
      }
    }
    return PotionHelper.mixPotions(potionStack1, potionStack2);
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return PSTRecipeSerializers.POTION_MIXING.get();
  }

  public static class Serializer implements RecipeSerializer<MixtureRecipe> {
    @Override
    public @NotNull MixtureRecipe fromJson(
        @NotNull ResourceLocation id, @NotNull JsonObject jsonObject) {
      return new MixtureRecipe(id);
    }

    @Override
    public MixtureRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
      return new MixtureRecipe(id);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull MixtureRecipe recipe) {}
  }
}
