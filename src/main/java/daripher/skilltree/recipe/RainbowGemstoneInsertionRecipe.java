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

public class RainbowGemstoneInsertionRecipe extends GemstoneInsertionRecipe {
	public RainbowGemstoneInsertionRecipe(ResourceLocation recipeId) {
		super(recipeId);
	}

	@Override
	public ItemStack assemble(Container smithingContainer) {
		var baseItem = smithingContainer.getItem(0);
		var gemstoneSlot = getEmptyGemstoneSlot(baseItem);
		if (GemstoneHelper.hasGemstone(baseItem, gemstoneSlot)) {
			return ItemStack.EMPTY;
		}
		var resultItemStack = baseItem.copy();
		if (baseItem.getTag() != null) {
			resultItemStack.setTag(baseItem.getTag().copy());
		}
		GemstoneHelper.applyRainbowGemstone(resultItemStack, gemstoneSlot);
		var gemstoneStrength = getGemstoneStrength(baseItem);
		GemstoneHelper.setGemstoneStrength(resultItemStack, gemstoneStrength, gemstoneSlot);
		return resultItemStack;
	}

	public boolean isAdditionIngredient(ItemStack itemStack) {
		return itemStack.getItem() == SkillTreeItems.RAINBOW_GEMSTONE.get();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SkillTreeRecipeSerializers.RAINBOW_GEMSTONE_INSERTION.get();
	}

	@Override
	public boolean isIncomplete() {
		return false;
	}

	public static class Serializer implements RecipeSerializer<RainbowGemstoneInsertionRecipe> {
		@Override
		public RainbowGemstoneInsertionRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
			return new RainbowGemstoneInsertionRecipe(id);
		}

		@Override
		public RainbowGemstoneInsertionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			return new RainbowGemstoneInsertionRecipe(id);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, RainbowGemstoneInsertionRecipe recipe) {
		}
	}
}