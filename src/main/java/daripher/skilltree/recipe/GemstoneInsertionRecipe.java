package daripher.skilltree.recipe;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import daripher.skilltree.api.recipe.SkillRequiringRecipe;
import daripher.skilltree.init.SkillTreeRecipeSerializers;
import daripher.skilltree.item.GemstoneItem;
import daripher.skilltree.util.GemstoneHelper;
import daripher.skilltree.util.ItemHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;

public class GemstoneInsertionRecipe extends UpgradeRecipe implements SkillRequiringRecipe {
	public final @Nullable ResourceLocation requiredSkillId;
	public final int gemstoneSlot;

	public GemstoneInsertionRecipe(ResourceLocation recipeId, int gemstoneSlot, @Nullable ResourceLocation requiredSkillId) {
		super(recipeId, null, null, null);
		this.gemstoneSlot = gemstoneSlot;
		this.requiredSkillId = requiredSkillId;
	}

	@Override
	public boolean matches(Container smithingContainer, Level level) {
		return isBaseIngredient(smithingContainer.getItem(0)) && isAdditionIngredient(smithingContainer.getItem(1));
	}

	@Override
	public ItemStack assemble(Container smithingContainer) {
		var baseItem = smithingContainer.getItem(0);
		var gemstoneSlot = this.gemstoneSlot;

		while (gemstoneSlot > 0 && !GemstoneHelper.hasGemstone(baseItem, gemstoneSlot - 1)) {
			gemstoneSlot--;
		}

		var gemstoneItem = smithingContainer.getItem(1);
		var resultItemStack = baseItem.copy();
		var baseItemTag = baseItem.getTag();

		if (baseItemTag != null) {
			resultItemStack.setTag(baseItemTag.copy());
		}

		GemstoneHelper.applyGemstone(resultItemStack, (GemstoneItem) gemstoneItem.getItem(), gemstoneSlot);
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

	@Override
	public ResourceLocation getRequiredSkillId() {
		return requiredSkillId;
	}

	public boolean isBaseIngredient(ItemStack itemStack) {
		return ItemHelper.canApplyGemstone(itemStack) && !GemstoneHelper.hasGemstone(itemStack, gemstoneSlot);
	}

	public boolean isAdditionIngredient(ItemStack itemStack) {
		return itemStack.getItem() instanceof GemstoneItem;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SkillTreeRecipeSerializers.GEMSTONE_INSERTION.get();
	}

	@Override
	public boolean isIncomplete() {
		return false;
	}

	public static class Serializer implements RecipeSerializer<GemstoneInsertionRecipe> {
		@Override
		public GemstoneInsertionRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
			var gemstoneSlot = jsonObject.get("gemstone_slot").getAsInt();
			var requiredSkill = jsonObject.has("required_skill") ? new ResourceLocation(jsonObject.get("required_skill").getAsString()) : null;
			return new GemstoneInsertionRecipe(id, gemstoneSlot, requiredSkill);
		}

		@Override
		public GemstoneInsertionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			var gemstoneSlot = buf.readInt();
			var requiredSkill = buf.readBoolean() ? new ResourceLocation(buf.readUtf()) : null;
			return new GemstoneInsertionRecipe(id, gemstoneSlot, requiredSkill);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, GemstoneInsertionRecipe recipe) {
			buf.writeInt(recipe.gemstoneSlot);
			var requiresSkill = recipe.requiredSkillId != null;
			buf.writeBoolean(requiresSkill);

			if (requiresSkill) {
				buf.writeUtf(recipe.requiredSkillId.toString());
			}
		}
	}
}