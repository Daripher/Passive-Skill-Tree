package daripher.skilltree.recipe;

import java.util.Optional;

import com.google.gson.JsonObject;

import daripher.skilltree.container.ContainerHelper;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.init.PSTRecipeSerializers;
import daripher.skilltree.item.ItemHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class WeaponPoisoningRecipe extends CustomRecipe {
	public WeaponPoisoningRecipe(ResourceLocation id) {
		super(id, CraftingBookCategory.MISC);
	}

	@Override
	public boolean matches(CraftingContainer container, Level level) {
		Optional<Player> player = ContainerHelper.getViewingPlayer(container);
		if (!player.isPresent()) return false;
		boolean canPoison = player.get().getAttributeValue(PSTAttributes.CAN_POISON_WEAPONS.get()) >= 1;
		if (!canPoison) return false;
		int weaponsCount = 0;
		int poisonsCount = 0;
		for (int slot = 0; slot < container.getContainerSize(); slot++) {
			ItemStack stackInSlot = container.getItem(slot);
			if (stackInSlot.isEmpty()) continue;
			if (ItemHelper.isMeleeWeapon(stackInSlot)) {
				weaponsCount++;
				continue;
			}
			if (ItemHelper.isPoison(stackInSlot)) poisonsCount++;
		}
		return weaponsCount == 1 && poisonsCount == 1;
	}

	@Override
	public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
		ItemStack weaponStack = ItemStack.EMPTY;
		ItemStack poisonStack = ItemStack.EMPTY;
		for (int slot = 0; slot < container.getContainerSize(); slot++) {
			ItemStack stackInSlot = container.getItem(slot);
			if (stackInSlot.isEmpty()) continue;
			if (ItemHelper.isMeleeWeapon(stackInSlot)) weaponStack = stackInSlot;
			if (ItemHelper.isPoison(stackInSlot)) poisonStack = stackInSlot;
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
	public RecipeSerializer<?> getSerializer() {
		return PSTRecipeSerializers.WEAPON_POISONING.get();
	}

	public static class Serializer implements RecipeSerializer<WeaponPoisoningRecipe> {
		@Override
		public WeaponPoisoningRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
			return new WeaponPoisoningRecipe(id);
		}

		@Override
		public WeaponPoisoningRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			return new WeaponPoisoningRecipe(id);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, WeaponPoisoningRecipe recipe) {
		}
	}
}
