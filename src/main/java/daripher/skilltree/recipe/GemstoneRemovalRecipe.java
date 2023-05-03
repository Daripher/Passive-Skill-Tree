package daripher.skilltree.recipe;

import com.google.gson.JsonObject;

import daripher.skilltree.init.SkillTreeItems;
import daripher.skilltree.init.SkillTreeRecipeSerializers;
import daripher.skilltree.util.GemstoneHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;

public class GemstoneRemovalRecipe extends UpgradeRecipe {
	public GemstoneRemovalRecipe(ResourceLocation recipeId) {
		super(recipeId, null, null, null);
	}

	@Override
	public boolean matches(Container smithingContainer, Level level) {
		return isBaseIngredient(smithingContainer.getItem(0)) && isAdditionIngredient(smithingContainer.getItem(1));
	}

	@Override
	public ItemStack assemble(Container smithingContainer) {
		var baseItem = smithingContainer.getItem(0);
		var resultItemStack = baseItem.copy();
		for (var slot = 0; slot < 3; slot++) {
			GemstoneHelper.removeGemstone(resultItemStack, slot);
		}
		return resultItemStack;
	}

	@Override
	public ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	public boolean isBaseIngredient(ItemStack itemStack) {
		return GemstoneHelper.hasGemstone(itemStack, 0);
	}

	public boolean isAdditionIngredient(ItemStack itemStack) {
		return itemStack.getItem() == SkillTreeItems.VOID_GEMSTONE.get();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SkillTreeRecipeSerializers.GEMSTONE_REMOVAL.get();
	}

	@Override
	public boolean isIncomplete() {
		return false;
	}

	public static class Serializer implements RecipeSerializer<GemstoneRemovalRecipe> {
		@Override
		public GemstoneRemovalRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
			return new GemstoneRemovalRecipe(id);
		}

		@Override
		public GemstoneRemovalRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			return new GemstoneRemovalRecipe(id);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, GemstoneRemovalRecipe recipe) {
		}
	}
}