package daripher.skilltree.data.generation;

import java.util.function.Consumer;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.init.PSTTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PSTRecipesProvider extends RecipeProvider {
	public PSTRecipesProvider(DataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		// rings
		ring(PSTItems.GOLDEN_RING, Tags.Items.NUGGETS_GOLD, consumer);
		ring(PSTItems.COPPER_RING, PSTTags.NUGGETS_COPPER, consumer);
		ring(PSTItems.IRON_RING, Tags.Items.NUGGETS_IRON, consumer);
		// resources
		packing(Items.COPPER_INGOT, PSTTags.NUGGETS_COPPER, consumer);
		unpacking(PSTItems.COPPER_NUGGET, Tags.Items.INGOTS_COPPER, consumer);
		// necklaces
		necklace(PSTItems.ASSASSIN_NECKLACE, Items.BONE, consumer);
		necklace(PSTItems.TRAVELER_NECKLACE, Items.FEATHER, consumer);
		necklace(PSTItems.HEALER_NECKLACE, Items.GHAST_TEAR, consumer);
		necklace(PSTItems.SIMPLE_NECKLACE, consumer);
		necklace(PSTItems.SCHOLAR_NECKLACE, Items.ENDER_PEARL, consumer);
		necklace(PSTItems.ARSONIST_NECKLACE, Items.FIRE_CHARGE, consumer);
		necklace(PSTItems.FISHERMAN_NECKLACE, Items.TROPICAL_FISH, consumer);
		// quviers
		quiver(PSTItems.QUIVER, consumer);
		quiver(PSTItems.ARMORED_QUIVER, Tags.Items.INGOTS_IRON, consumer);
		quiver(PSTItems.DIAMOND_QUIVER, Tags.Items.GEMS_DIAMOND, consumer);
		quiver(PSTItems.FIERY_QUIVER, Items.BLAZE_POWDER, consumer);
		quiver(PSTItems.GILDED_QUIVER, Tags.Items.INGOTS_GOLD, consumer);
		quiver(PSTItems.HEALING_QUIVER, Items.GHAST_TEAR, consumer);
		quiver(PSTItems.TOXIC_QUIVER, Items.FERMENTED_SPIDER_EYE, consumer);
		quiver(PSTItems.EXPLOSIVE_QUIVER, Items.GUNPOWDER, consumer);
		quiver(PSTItems.SILENT_QUIVER, Items.FEATHER, consumer);
		quiver(PSTItems.BONE_QUIVER, Items.BONE, consumer);
	}

	protected void quiver(RegistryObject<Item> result, Item material, Consumer<FinishedRecipe> consumer) {
		// formatter:off
		ShapedRecipeBuilder.shaped(result.get())
			.define('#', material)
			.define('l', Items.LEATHER)
			.define('s', Items.STRING)
			.pattern("#ls")
			.pattern("#ls")
			.pattern("#ls")
			.group(getItemName(result.get()))
			.unlockedBy(getHasName(material), has(material))
			.save(consumer, getRecipeId(result.get()));
		// formatter:on
	}

	protected void quiver(RegistryObject<Item> result, TagKey<Item> material, Consumer<FinishedRecipe> consumer) {
		// formatter:off
		ShapedRecipeBuilder.shaped(result.get())
			.define('#', material)
			.define('l', Items.LEATHER)
			.define('s', Items.STRING)
			.pattern("#ls")
			.pattern("#ls")
			.pattern("#ls")
			.group(getItemName(result.get()))
			.unlockedBy(getHasName(material), has(material))
			.save(consumer, getRecipeId(result.get()));
		// formatter:on
	}

	protected void quiver(RegistryObject<Item> result, Consumer<FinishedRecipe> consumer) {
		// formatter:off
		ShapedRecipeBuilder.shaped(result.get())
			.define('l', Items.LEATHER)
			.define('s', Items.STRING)
			.pattern("ls")
			.pattern("ls")
			.pattern("ls")
			.group(getItemName(result.get()))
			.unlockedBy(getHasName(Items.LEATHER), has(Items.LEATHER))
			.save(consumer, getRecipeId(result.get()));
		// formatter:on
	}

	protected void necklace(RegistryObject<Item> result, Item material, Consumer<FinishedRecipe> consumer) {
		// formatter:off
		ShapedRecipeBuilder.shaped(result.get())
			.define('#', material)
			.define('n', Tags.Items.NUGGETS_GOLD)
			.pattern("nnn")
			.pattern("n n")
			.pattern("n#n")
			.group(getItemName(result.get()))
			.unlockedBy(getHasName(material), has(material))
			.save(consumer, getRecipeId(result.get()));
		// formatter:on
	}

	protected void necklace(RegistryObject<Item> result, Consumer<FinishedRecipe> consumer) {
		// formatter:off
		ShapedRecipeBuilder.shaped(result.get())
			.define('n', Tags.Items.NUGGETS_GOLD)
			.pattern("nnn")
			.pattern("n n")
			.pattern("nnn")
			.group(getItemName(result.get()))
			.unlockedBy(getHasName(Tags.Items.NUGGETS_GOLD), has(Tags.Items.NUGGETS_GOLD))
			.save(consumer, getRecipeId(result.get()));
		// formatter:on
	}

	protected void ring(RegistryObject<Item> result, TagKey<Item> material, Consumer<FinishedRecipe> consumer) {
		// formatter:off
		ShapedRecipeBuilder.shaped(result.get())
			.define('#', material)
			.pattern(" # ")
			.pattern("# #")
			.pattern(" # ")
			.group(getItemName(result.get()))
			.unlockedBy(getHasName(material), has(material))
			.save(consumer, getRecipeId(result.get()));
		// formatter:on
	}

	protected void packing(Item result, TagKey<Item> material, Consumer<FinishedRecipe> consumer) {
		// formatter:off
		ShapedRecipeBuilder.shaped(result)
			.define('#', material)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.group(getItemName(result))
			.unlockedBy(getHasName(material), has(material))
			.save(consumer, getRecipeId(result));
		// formatter:on
	}

	protected void unpacking(RegistryObject<Item> result, TagKey<Item> material, Consumer<FinishedRecipe> consumer) {
		// formatter:off
		ShapelessRecipeBuilder.shapeless(result.get(), 9)
			.requires(material)
			.group(getItemName(result.get()))
			.unlockedBy(getHasName(material), has(material))
			.save(consumer, getRecipeId(result.get()));
		// formatter:on
	}

	protected String getHasName(TagKey<Item> material) {
		return "has_" + material.location().getPath().replaceAll("/", "_");
	}

	private ResourceLocation getRecipeId(Item item) {
		return new ResourceLocation(SkillTreeMod.MOD_ID, ForgeRegistries.ITEMS.getKey(item).getPath());
	}
}
