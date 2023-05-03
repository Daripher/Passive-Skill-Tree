package daripher.skilltree.recipe;

import com.google.gson.JsonObject;

import daripher.skilltree.api.recipe.PlayerRequiringRecipe;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeRecipeSerializers;
import daripher.skilltree.item.GemstoneItem;
import daripher.skilltree.util.GemstoneHelper;
import daripher.skilltree.util.ItemHelper;
import daripher.skilltree.util.PlayerHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;

public class GemstoneInsertionRecipe extends UpgradeRecipe implements PlayerRequiringRecipe {
	private Player currentPlayer;

	public GemstoneInsertionRecipe(ResourceLocation recipeId) {
		super(recipeId, null, null, null);
	}

	@Override
	public boolean matches(Container smithingContainer, Level level) {
		return isBaseIngredient(smithingContainer.getItem(0)) && isAdditionIngredient(smithingContainer.getItem(1));
	}

	@Override
	public ItemStack assemble(Container smithingContainer) {
		var baseItem = smithingContainer.getItem(0);
		var gemstoneSlot = getEmptyGemstoneSlot(baseItem);
		if (GemstoneHelper.hasGemstone(baseItem, gemstoneSlot)) {
			return ItemStack.EMPTY;
		}
		var gemstoneItem = (GemstoneItem) smithingContainer.getItem(1).getItem();
		var resultItemStack = baseItem.copy();
		if (baseItem.getTag() != null) {
			resultItemStack.setTag(baseItem.getTag().copy());
		}
		GemstoneHelper.applyGemstone(resultItemStack, gemstoneItem, gemstoneSlot);
		var gemstoneStrength = getGemstoneStrength(baseItem);
		GemstoneHelper.setGemstoneStrength(resultItemStack, gemstoneStrength, gemstoneSlot);
		return resultItemStack;
	}

	public double getGemstoneStrength(ItemStack craftingItem) {
		var gemstoneStrength = currentPlayer.getAttributeValue(SkillTreeAttributes.GEMSTONES_STRENGTH_MULTIPLIER.get()) - 1;
		var craftingArmor = ItemHelper.isArmor(craftingItem) || ItemHelper.isShield(craftingItem);
		var craftingWeapon = ItemHelper.isWeapon(craftingItem) || ItemHelper.isBow(craftingItem);
		if (craftingArmor) {
			var gemstoneStrengthInArmor = currentPlayer.getAttributeValue(SkillTreeAttributes.GEMSTONES_STRENGTH_IN_ARMOR_MULTIPLIER.get()) - 1;
			gemstoneStrength += gemstoneStrengthInArmor;
		} else if (craftingWeapon) {
			var gemstoneStrengthInWeapon = currentPlayer.getAttributeValue(SkillTreeAttributes.GEMSTONES_STRENGTH_IN_WEAPON_MULTIPLIER.get()) - 1;
			gemstoneStrength += gemstoneStrengthInWeapon;
		}
		return gemstoneStrength;
	}

	public int getEmptyGemstoneSlot(ItemStack baseItem) {
		var maxGemSlots = getMaxGemSlots(baseItem);
		var gemstoneSlot = 0;
		for (int i = 0; i < maxGemSlots; i++) {
			gemstoneSlot = i;
			if (!GemstoneHelper.hasGemstone(baseItem, gemstoneSlot)) {
				break;
			}
		}
		return gemstoneSlot;
	}

	public int getMaxGemSlots(ItemStack baseItem) {
		var slots = 1;
		if (GemstoneHelper.hasAdditionalGemstoneSlot(baseItem)) {
			slots++;
		}
		slots += PlayerHelper.getAdditionalGemstoneSlots(currentPlayer);
		return slots;
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
	public void setCurrentPlayer(Player player) {
		this.currentPlayer = player;
	}

	public boolean isBaseIngredient(ItemStack itemStack) {
		return ItemHelper.canApplyGemstone(itemStack);
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
			return new GemstoneInsertionRecipe(id);
		}

		@Override
		public GemstoneInsertionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			return new GemstoneInsertionRecipe(id);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, GemstoneInsertionRecipe recipe) {
		}
	}
}