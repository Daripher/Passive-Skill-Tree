package daripher.skilltree.recipe;

import com.google.gson.JsonObject;

import daripher.skilltree.api.PlayerContainer;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.init.SkillTreeRecipeSerializers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;

public class PSTShapelessRecipe extends ShapelessRecipe {
	private final ResourceLocation requiredSkillId;

	public PSTShapelessRecipe(ShapelessRecipe recipe, ResourceLocation requiredSkillId) {
		super(recipe.getId(), recipe.getGroup(), recipe.getResultItem(), recipe.getIngredients());
		this.requiredSkillId = requiredSkillId;
	}

	@Override
	public boolean matches(CraftingContainer container, Level level) {
		if (!meetsSkillRequirement(container)) return false;
		return super.matches(container, level);
	}
	
	@Override
	public ItemStack assemble(CraftingContainer container) {
		if (!meetsSkillRequirement(container)) return ItemStack.EMPTY;
		return super.assemble(container);
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SkillTreeRecipeSerializers.SHAPELESS_CRAFTING.get();
	}

	protected boolean meetsSkillRequirement(Container container) {
		if (!(container instanceof PlayerContainer)) return false;
		Player player = ((PlayerContainer) container).getPlayer().orElse(null);
		if (player == null) return false;
		return PlayerSkillsProvider.get(player).hasSkill(requiredSkillId);
	}

	public static class Serializer implements RecipeSerializer<PSTShapelessRecipe> {
		public PSTShapelessRecipe fromJson(ResourceLocation id, JsonObject json) {
			ShapelessRecipe recipe = SHAPELESS_RECIPE.fromJson(id, json);
			var requiredSkillId = new ResourceLocation(GsonHelper.getAsString(json, "required_skill"));
			return new PSTShapelessRecipe(recipe, requiredSkillId);
		}

		public PSTShapelessRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf byteBuf) {
			ShapelessRecipe recipe = SHAPELESS_RECIPE.fromNetwork(id, byteBuf);
			var requiredSkillId = new ResourceLocation(byteBuf.readUtf());
			return new PSTShapelessRecipe(recipe, requiredSkillId);
		}

		public void toNetwork(FriendlyByteBuf byteBuf, PSTShapelessRecipe recipe) {
			SHAPELESS_RECIPE.toNetwork(byteBuf, recipe);
			byteBuf.writeUtf(recipe.requiredSkillId.toString());
		}
	}
}
