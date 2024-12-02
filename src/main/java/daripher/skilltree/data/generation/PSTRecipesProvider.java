package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTEnchantments;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.init.PSTTags;
import daripher.skilltree.recipe.builder.ItemUpgradeRecipeBuilder;
import daripher.skilltree.recipe.builder.StackResultShapelessRecipeBuilder;
import daripher.skilltree.skill.bonus.condition.damage.MagicDamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.PoisonDamageCondition;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import daripher.skilltree.skill.bonus.item.ItemDurabilityBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.item.ItemSocketsBonus;
import daripher.skilltree.skill.bonus.player.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class PSTRecipesProvider extends RecipeProvider {
  public PSTRecipesProvider(DataGenerator dataGenerator) {
    super(dataGenerator.getPackOutput());
  }

  @Override
  protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
    // rings
    ring(PSTItems.GOLDEN_RING, Tags.Items.NUGGETS_GOLD, consumer);
    ring(PSTItems.COPPER_RING, PSTTags.Items.NUGGETS_COPPER, consumer);
    ring(PSTItems.IRON_RING, Tags.Items.NUGGETS_IRON, consumer);
    // resources
    packing(Items.COPPER_INGOT, PSTTags.Items.NUGGETS_COPPER, consumer);
    unpacking(PSTItems.COPPER_NUGGET, Tags.Items.INGOTS_COPPER, consumer);
    // necklaces
    necklace(PSTItems.ASSASSIN_NECKLACE, Items.BONE, consumer);
    necklace(PSTItems.TRAVELER_NECKLACE, Items.FEATHER, consumer);
    necklace(PSTItems.HEALER_NECKLACE, Items.GHAST_TEAR, consumer);
    necklace(PSTItems.SIMPLE_NECKLACE, consumer);
    necklace(PSTItems.SCHOLAR_NECKLACE, Items.ENDER_PEARL, consumer);
    necklace(PSTItems.ARSONIST_NECKLACE, Items.FIRE_CHARGE, consumer);
    necklace(PSTItems.FISHERMAN_NECKLACE, Items.TROPICAL_FISH, consumer);
    // books
    enchantmentBooks(PSTItems.ANCIENT_ALLOY_ENCHANTED, PSTEnchantments.MAGIC_FLOW, 3, consumer);
    enchantmentBooks(PSTItems.ANCIENT_ALLOY_CURATIVE, PSTEnchantments.DRAGON_BLOOD, 3, consumer);
    enchantmentBooks(PSTItems.ANCIENT_ALLOY_DURABLE, PSTEnchantments.STEEL_MIND, 3, consumer);
    enchantmentBooks(PSTItems.ANCIENT_ALLOY_HOT, PSTEnchantments.FIRE_WALL, 3, consumer);
    enchantmentBooks(PSTItems.ANCIENT_ALLOY_SPATIAL, PSTEnchantments.BOTTOMLESS_FLASK, consumer);
    // upgrades
    ItemUpgradeRecipeBuilder.create()
        .baseCondition(new EquipmentCondition(EquipmentCondition.Type.WEAPON))
        .additionalItem(Ingredient.of(PSTItems.ANCIENT_ALLOY_GILDED.get()))
        .itemBonus(new ItemSkillBonus(new LootDuplicationBonus(0.02f, 1f, LootDuplicationBonus.LootType.MOBS)))
        .save(consumer, getRecipeId("upgrades/weapons_double_loot"));
    ItemUpgradeRecipeBuilder.create()
        .baseCondition(new EquipmentCondition(EquipmentCondition.Type.BOOTS))
        .additionalItem(Ingredient.of(PSTItems.ANCIENT_ALLOY_LIGHTWEIGHT.get()))
        .itemBonus(new ItemSkillBonus(new AttributeBonus(Attributes.MOVEMENT_SPEED, "Upgrade", 0.01f, AttributeModifier.Operation.MULTIPLY_BASE)))
        .save(consumer, getRecipeId("upgrades/boots_movespeed"));
    ItemUpgradeRecipeBuilder.create()
        .baseCondition(new EquipmentCondition(EquipmentCondition.Type.CHESTPLATE))
        .additionalItem(Ingredient.of(PSTItems.ANCIENT_ALLOY_CURATIVE.get()))
        .itemBonus(new ItemSkillBonus(new IncomingHealingBonus(0.02f)))
        .save(consumer, getRecipeId("upgrades/chestplates_incoming_healing"));
    ItemUpgradeRecipeBuilder.create()
        .baseCondition(new EquipmentCondition(EquipmentCondition.Type.WEAPON))
        .additionalItem(Ingredient.of(PSTItems.ANCIENT_ALLOY_TOXIC.get()))
        .itemBonus(new ItemSkillBonus(new DamageBonus(0.02f,
                                                      AttributeModifier.Operation.MULTIPLY_BASE).setDamageCondition(new PoisonDamageCondition())))
        .save(consumer, getRecipeId("upgrades/weapons_poison_damage"));
    ItemUpgradeRecipeBuilder.create()
        .baseCondition(new EquipmentCondition(EquipmentCondition.Type.WEAPON))
        .additionalItem(Ingredient.of(PSTItems.ANCIENT_ALLOY_ENCHANTED.get()))
        .itemBonus(new ItemSkillBonus(new DamageBonus(0.02f,
                                                      AttributeModifier.Operation.MULTIPLY_BASE).setDamageCondition(new MagicDamageCondition())))
        .save(consumer, getRecipeId("upgrades/weapons_magic_damage"));
    ItemUpgradeRecipeBuilder.create()
        .baseCondition(new EquipmentCondition(EquipmentCondition.Type.PICKAXE))
        .additionalItem(Ingredient.of(PSTItems.ANCIENT_ALLOY_GILDED.get()))
        .itemBonus(new ItemSkillBonus(new LootDuplicationBonus(0.02f, 1f, LootDuplicationBonus.LootType.GEMS)))
        .save(consumer, getRecipeId("upgrades/pickaxes_double_gems"));
    ItemUpgradeRecipeBuilder.create()
        .baseCondition(new EquipmentCondition(EquipmentCondition.Type.WEAPON))
        .additionalItem(Ingredient.of(PSTItems.ANCIENT_ALLOY_SPATIAL.get()))
        .itemBonus(new ItemSocketsBonus(1))
        .maxUpgrades(1)
        .upgradeChances(1f)
        .save(consumer, getRecipeId("upgrades/weapons_sockets"));
    ItemUpgradeRecipeBuilder.create()
        .baseCondition(new EquipmentCondition(EquipmentCondition.Type.CHESTPLATE))
        .additionalItem(Ingredient.of(PSTItems.ANCIENT_ALLOY_SPATIAL.get()))
        .itemBonus(new ItemSocketsBonus(1))
        .maxUpgrades(1)
        .upgradeChances(1f)
        .save(consumer, getRecipeId("upgrades/chestplates_sockets"));
    ItemUpgradeRecipeBuilder.create()
        .baseCondition(new EquipmentCondition(EquipmentCondition.Type.WEAPON))
        .additionalItem(Ingredient.of(PSTItems.ANCIENT_ALLOY_LIGHTWEIGHT.get()))
        .itemBonus(new ItemSkillBonus(new AttributeBonus(Attributes.ATTACK_SPEED, "Upgrade", 0.1f, AttributeModifier.Operation.MULTIPLY_BASE)))
        .save(consumer, getRecipeId("upgrades/weapons_attack_speed"));
    ItemUpgradeRecipeBuilder.create()
        .baseCondition(new EquipmentCondition(EquipmentCondition.Type.CHESTPLATE))
        .additionalItem(Ingredient.of(PSTItems.ANCIENT_ALLOY_LIGHTWEIGHT.get()))
        .itemBonus(new ItemSkillBonus(new DamageAvoidanceBonus(0.01f)))
        .save(consumer, getRecipeId("upgrades/chestplates_damage_avoidance"));
    ItemUpgradeRecipeBuilder.create()
        .baseCondition(new EquipmentCondition(EquipmentCondition.Type.WEAPON))
        .additionalItem(Ingredient.of(PSTItems.ANCIENT_ALLOY_GILDED.get()))
        .itemBonus(new ItemSkillBonus(new AttributeBonus(Attributes.LUCK, "Upgrade", 1f, AttributeModifier.Operation.ADDITION)))
        .upgradeChances(0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f)
        .save(consumer, getRecipeId("upgrades/weapons_luck"));
    ItemUpgradeRecipeBuilder.create()
        .baseCondition(new EquipmentCondition(EquipmentCondition.Type.SHIELD))
        .additionalItem(Ingredient.of(PSTItems.ANCIENT_ALLOY_SPATIAL.get()))
        .itemBonus(new ItemSocketsBonus(1))
        .maxUpgrades(1)
        .upgradeChances(1f)
        .save(consumer, getRecipeId("upgrades/shields_sockets"));
    ItemUpgradeRecipeBuilder.create()
        .baseCondition(new EquipmentCondition(EquipmentCondition.Type.ANY))
        .additionalItem(Ingredient.of(PSTItems.ANCIENT_ALLOY_DURABLE.get()))
        .itemBonus(new ItemDurabilityBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE))
        .save(consumer, getRecipeId("upgrades/equipment_durability"));
  }

  protected void enchantmentBooks(RegistryObject<Item> ingredient, RegistryObject<Enchantment> enchantment, Consumer<FinishedRecipe> consumer) {
    enchantmentBooks(ingredient, enchantment, 1, consumer);
  }

  protected void enchantmentBooks(RegistryObject<Item> ingredient,
                                  RegistryObject<Enchantment> enchantment,
                                  int maxLevel,
                                  Consumer<FinishedRecipe> consumer) {
    Ingredient base = Ingredient.of(PSTItems.ANCIENT_BOOK.get());
    Ingredient addition = Ingredient.of(ingredient.get());
    ItemStack result = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment.get(), 1));
    @Nullable ResourceLocation enchantmentId = ForgeRegistries.ENCHANTMENTS.getKey(enchantment.get());
    Objects.requireNonNull(enchantmentId);
    StackResultShapelessRecipeBuilder recipeBuilder = StackResultShapelessRecipeBuilder.create(result)
        .requires(base)
        .requires(addition);
    recipeBuilder.save(consumer, getRecipeId("enchanted_book_" + enchantmentId.getPath() + "_1"));
    for (int level = 2; level <= maxLevel; level++) {
      base = PartialNBTIngredient.of(Objects.requireNonNull(result.getTag()), Items.ENCHANTED_BOOK);
      result = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment.get(), level));
      recipeBuilder = StackResultShapelessRecipeBuilder.create(result)
          .requires(base)
          .requires(addition);
      recipeBuilder.save(consumer, getRecipeId("enchanted_book_" + enchantmentId.getPath() + "_" + level));
    }
  }

  protected void necklace(RegistryObject<Item> result, Item material, Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get())
        .define('#', material)
        .define('n', Tags.Items.NUGGETS_GOLD)
        .pattern("nnn")
        .pattern("n n")
        .pattern("n#n")
        .group(getItemName(result.get()))
        .unlockedBy(getHasName(material), has(material))
        .save(consumer, getRecipeId(result.get()));
  }

  protected void necklace(RegistryObject<Item> result, Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get())
        .define('n', Tags.Items.NUGGETS_GOLD)
        .pattern("nnn")
        .pattern("n n")
        .pattern("nnn")
        .group(getItemName(result.get()))
        .unlockedBy(getHasName(Tags.Items.NUGGETS_GOLD), has(Tags.Items.NUGGETS_GOLD))
        .save(consumer, getRecipeId(result.get()));
  }

  protected void ring(RegistryObject<Item> result, TagKey<Item> material, Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get())
        .define('#', material)
        .pattern(" # ")
        .pattern("# #")
        .pattern(" # ")
        .group(getItemName(result.get()))
        .unlockedBy(getHasName(material), has(material))
        .save(consumer, getRecipeId(result.get()));
  }

  protected void packing(Item result, TagKey<Item> material, Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
        .define('#', material)
        .pattern("###")
        .pattern("###")
        .pattern("###")
        .group(getItemName(result))
        .unlockedBy(getHasName(material), has(material))
        .save(consumer, getRecipeId(result));
  }

  protected void unpacking(RegistryObject<Item> result, TagKey<Item> material, Consumer<FinishedRecipe> consumer) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get(), 9)
        .requires(material)
        .group(getItemName(result.get()))
        .unlockedBy(getHasName(material), has(material))
        .save(consumer, getRecipeId(result.get()));
  }

  protected String getHasName(TagKey<Item> material) {
    return "has_" +
           material.location()
               .getPath()
               .replaceAll("/", "_");
  }

  private ResourceLocation getRecipeId(Item item) {
    ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
    return new ResourceLocation(SkillTreeMod.MOD_ID,
                                Objects.requireNonNull(id)
                                    .getPath());
  }

  private ResourceLocation getRecipeId(String path) {
    return new ResourceLocation(SkillTreeMod.MOD_ID, path);
  }
}
