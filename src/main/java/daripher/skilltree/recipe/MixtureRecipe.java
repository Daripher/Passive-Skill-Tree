package daripher.skilltree.recipe;

import com.google.gson.JsonObject;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.util.PotionHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class MixtureRecipe extends CustomRecipe {
	public MixtureRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingContainer container, Level level) {
		var playerContainer = (PlayerContainer) container;
		if (!playerContainer.getPlayer().isPresent()) {
			return false;
		}
		var player = playerContainer.getPlayer().get();
		var canMixPotions = player.getAttributeValue(PSTAttributes.CAN_MIX_POTIONS.get()) >= 1;
		if (!canMixPotions) {
			return false;
		}
		var potionStack1 = ItemStack.EMPTY;
		var potionStack2 = ItemStack.EMPTY;
		var potionsCount = 0;
		for (int slot = 0; slot < container.getContainerSize(); slot++) {
			var stackInSlot = container.getItem(slot);
			if (stackInSlot.isEmpty()) {
				continue;
			}
			if (PotionHelper.isPotion(stackInSlot) && !PotionHelper.isMixture(stackInSlot)) {
				potionsCount++;
				if (potionStack1.isEmpty()) {
					potionStack1 = stackInSlot;
				} else {
					potionStack2 = stackInSlot;
				}
			}
		}
		if (PotionUtils.getMobEffects(potionStack1).isEmpty() || PotionUtils.getMobEffects(potionStack2).isEmpty()) {
			return false;
		}
		return potionsCount == 2 && potionStack1.getItem() == potionStack2.getItem();
	}

	@Override
	public ItemStack assemble(CraftingContainer container) {
		var potionStack1 = ItemStack.EMPTY;
		var potionStack2 = ItemStack.EMPTY;
		for (int slot = 0; slot < container.getContainerSize(); slot++) {
			var stackInSlot = container.getItem(slot);
			if (stackInSlot.isEmpty()) {
				continue;
			}
			if (PotionHelper.isPotion(stackInSlot) && !PotionHelper.isMixture(stackInSlot)) {
				if (potionStack1.isEmpty()) {
					potionStack1 = stackInSlot;
				} else {
					potionStack2 = stackInSlot;
				}
			}
		}
		var result = PotionHelper.mixPotions(potionStack1, potionStack2);
		return result;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return PSTRecipeSerializers.POTION_MIXING.get();
	}

	public static class Serializer implements RecipeSerializer<MixtureRecipe> {
		@Override
		public MixtureRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
			return new MixtureRecipe(id);
		}

		@Override
		public MixtureRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			return new MixtureRecipe(id);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, MixtureRecipe recipe) {
		}
	}
}
