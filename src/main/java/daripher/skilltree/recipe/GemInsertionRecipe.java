package daripher.skilltree.recipe;

import com.google.gson.JsonObject;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.gem.GemHelper;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeRecipeSerializers;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.item.gem.GemItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;

public class GemInsertionRecipe extends UpgradeRecipe {
	public GemInsertionRecipe() {
		super(new ResourceLocation(SkillTreeMod.MOD_ID, "gem_insertion"), Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
	}

	@Override
	public boolean matches(Container container, Level level) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) return false;
		}
		if (!isBaseIngredient(container.getItem(0))) return false;
		if (!isAdditionIngredient(container.getItem(1))) return false;
		var gem = (GemItem) container.getItem(1).getItem();
		var playerContainer = (PlayerContainer) container;
		var player = playerContainer.getPlayer().orElseThrow(NullPointerException::new);
		var gemBonus = gem.getGemBonus(player, container.getItem(0));
		return gemBonus != null;
	}

	@Override
	public ItemStack assemble(Container container) {
		if (ModList.get().isLoaded("apotheosis")) {
			if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) return ItemStack.EMPTY;
		}
		var playerContainer = (PlayerContainer) container;
		if (!playerContainer.getPlayer().isPresent()) return ItemStack.EMPTY;
		var player = playerContainer.getPlayer().get();
		var baseItem = container.getItem(0);
		var socket = getEmptySocket(baseItem, player);
		var gem = (GemItem) container.getItem(1).getItem();
		if (!gem.canInsertInto(player, baseItem, socket)) return ItemStack.EMPTY;
		var resultItemStack = baseItem.copy();
		if (baseItem.getTag() != null) resultItemStack.setTag(baseItem.getTag().copy());
		var gemPower = getPlayerGemPower(player, baseItem);
		gem.insertInto(player, resultItemStack, socket, gemPower);
		return resultItemStack;
	}

	public double getPlayerGemPower(Player player, ItemStack itemStack) {
		var gemPower = player.getAttributeValue(SkillTreeAttributes.GEM_POWER.get()) - 1;
		var isArmor = ItemHelper.isArmor(itemStack) || ItemHelper.isShield(itemStack);
		var isWeapon = ItemHelper.isWeapon(itemStack);
		if (isArmor) {
			var armorGemPower = player.getAttributeValue(SkillTreeAttributes.GEM_POWER_IN_ARMOR.get()) - 1;
			gemPower += armorGemPower;
		} else if (isWeapon) {
			var weaponGemPower = player.getAttributeValue(SkillTreeAttributes.GEM_POWER_IN_WEAPON.get()) - 1;
			gemPower += weaponGemPower;
		}
		return gemPower;
	}

	public int getEmptySocket(ItemStack baseItem, Player player) {
		var sockets = GemHelper.getMaximumSockets(baseItem, player);
		var socket = 0;
		for (int i = 0; i < sockets; i++) {
			socket = i;
			if (!GemHelper.hasGem(baseItem, socket)) break;
		}
		return socket;
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
		return ItemHelper.canInsertGem(itemStack);
	}

	public boolean isAdditionIngredient(ItemStack itemStack) {
		return itemStack.getItem() instanceof GemItem;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SkillTreeRecipeSerializers.GEMSTONE_INSERTION.get();
	}

	@Override
	public boolean isIncomplete() {
		return false;
	}

	public static class Serializer implements RecipeSerializer<GemInsertionRecipe> {
		@Override
		public GemInsertionRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
			return new GemInsertionRecipe();
		}

		@Override
		public GemInsertionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			return new GemInsertionRecipe();
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, GemInsertionRecipe recipe) {
		}
	}
}