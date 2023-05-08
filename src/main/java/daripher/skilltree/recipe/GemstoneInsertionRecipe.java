package daripher.skilltree.recipe;

import com.google.gson.JsonObject;

import daripher.skilltree.api.player.PlayerContainer;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeRecipeSerializers;
import daripher.skilltree.item.GemstoneItem;
import daripher.skilltree.util.ItemHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;

public class GemstoneInsertionRecipe extends UpgradeRecipe {
	public GemstoneInsertionRecipe(ResourceLocation recipeId) {
		super(recipeId, null, null, null);
	}

	@Override
	public boolean matches(Container smithingContainer, Level level) {
		return isBaseIngredient(smithingContainer.getItem(0)) && isAdditionIngredient(smithingContainer.getItem(1));
	}

	@Override
	public ItemStack assemble(Container smithingContainer) {
		var playerContainer = (PlayerContainer) smithingContainer;
		if (!playerContainer.getPlayer().isPresent()) {
			return ItemStack.EMPTY;
		}
		var player = playerContainer.getPlayer().get();
		var baseItem = smithingContainer.getItem(0);
		var gemstoneSlot = getFirstEmptyGemstoneSlot(baseItem, player);
		var gemstoneItem = (GemstoneItem) smithingContainer.getItem(1).getItem();
		if (!gemstoneItem.canInsertInto(player, baseItem, gemstoneSlot)) {
			return ItemStack.EMPTY;
		}
		var resultItemStack = baseItem.copy();
		if (baseItem.getTag() != null) {
			resultItemStack.setTag(baseItem.getTag().copy());
		}
		var gemstoneStrength = getPlayerGemstoneStrength(player, baseItem);
		gemstoneItem.insertInto(player, resultItemStack, gemstoneSlot, gemstoneStrength);
		return resultItemStack;
	}

	public double getPlayerGemstoneStrength(Player player, ItemStack craftingItem) {
		var gemstoneStrength = player.getAttributeValue(SkillTreeAttributes.GEMSTONES_STRENGTH_MULTIPLIER.get()) - 1;
		var craftingArmor = ItemHelper.isArmor(craftingItem) || ItemHelper.isShield(craftingItem);
		var craftingWeapon = ItemHelper.isWeapon(craftingItem) || ItemHelper.isBow(craftingItem);
		if (craftingArmor) {
			var gemstoneStrengthInArmor = player.getAttributeValue(SkillTreeAttributes.GEMSTONES_STRENGTH_IN_ARMOR_MULTIPLIER.get()) - 1;
			gemstoneStrength += gemstoneStrengthInArmor;
		} else if (craftingWeapon) {
			var gemstoneStrengthInWeapon = player.getAttributeValue(SkillTreeAttributes.GEMSTONES_STRENGTH_IN_WEAPON_MULTIPLIER.get()) - 1;
			gemstoneStrength += gemstoneStrengthInWeapon;
		}
		return gemstoneStrength;
	}

	public int getFirstEmptyGemstoneSlot(ItemStack baseItem, Player player) {
		var maximumSlots = GemstoneItem.getMaximumGemstoneSlots(baseItem, player);
		var gemstoneSlot = 0;
		for (int i = 0; i < maximumSlots; i++) {
			gemstoneSlot = i;
			if (!GemstoneItem.hasGemstone(baseItem, gemstoneSlot)) {
				break;
			}
		}
		return gemstoneSlot;
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