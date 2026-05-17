package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.recipe.builder.WorkbenchCraftingRecipeBuilder;
import daripher.skilltree.recipe.builder.WorkbenchItemBonusRecipeBuilder;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import daripher.skilltree.skill.bonus.player.DamageBonus;
import daripher.skilltree.skill.bonus.predicate.damage.ThornsDamageCondition;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class PSTRecipesProvider extends RecipeProvider {
  public PSTRecipesProvider(
      PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(packOutput, lookupProvider);
  }

  @Override
  protected void buildRecipes(
      @NotNull RecipeOutput consumer, @NotNull HolderLookup.Provider lookupProvider) {
    addCraftingTableRecipes(consumer);
    addUpgradeRecipes(consumer);
    addWorkbenchCraftingRecipes(consumer);
  }

  private static void addCraftingTableRecipes(@NotNull RecipeOutput consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PSTItems.WORKBENCH.get())
        .define('I', Tags.Items.INGOTS_IRON)
        .define('G', Tags.Items.INGOTS_GOLD)
        .define('C', Tags.Items.INGOTS_COPPER)
        .define('#', Items.SMITHING_TABLE)
        .pattern("III")
        .pattern("G#G")
        .pattern("CCC")
        .unlockedBy(getHasName(Items.SMITHING_TABLE), has(Items.SMITHING_TABLE))
        .save(consumer);
  }

  private static void addUpgradeRecipes(@NotNull RecipeOutput consumer) {
    WorkbenchItemBonusRecipeBuilder.create(modRecipeId("shields_thorns_bonus"))
        .setBaseItemCondition(new EquipmentPredicate(EquipmentPredicate.Type.SHIELD))
        .addIngredients(Ingredient.of(Tags.Items.INGOTS_IRON), 5)
        .addIngredients(Ingredient.of(Tags.Items.NUGGETS_IRON), 9)
        .setItemBonus(
            new DamageBonus(0.25f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                .setDamageCondition(new ThornsDamageCondition()))
        .setRequiresPassiveSkill()
        .save(consumer);
    WorkbenchItemBonusRecipeBuilder.create(modRecipeId("armor_defence_bonus"))
        .setBaseItemCondition(new EquipmentPredicate(EquipmentPredicate.Type.ARMOR))
        .addIngredients(Ingredient.of(Tags.Items.INGOTS_COPPER), 2)
        .setItemBonus(
            new AttributeBonus(
                Attributes.ARMOR.value(),
                new AttributeModifier(
                    modRecipeId("armor_defence_bonus"), 1, AttributeModifier.Operation.ADD_VALUE)))
        .setRequiresPassiveSkill()
        .save(consumer);
    WorkbenchItemBonusRecipeBuilder.create(modRecipeId("shields_defence_bonus"))
        .setBaseItemCondition(new EquipmentPredicate(EquipmentPredicate.Type.SHIELD))
        .addIngredients(Ingredient.of(Tags.Items.INGOTS_COPPER), 2)
        .setItemBonus(
            new AttributeBonus(
                Attributes.ARMOR.value(),
                new AttributeModifier(
                    modRecipeId("shields_defence_bonus"), 2, AttributeModifier.Operation.ADD_VALUE)))
        .setRequiresPassiveSkill()
        .save(consumer);
    WorkbenchItemBonusRecipeBuilder.create(modRecipeId("melee_weapon_attack_speed_bonus"))
        .setBaseItemCondition(new EquipmentPredicate(EquipmentPredicate.Type.MELEE_WEAPON))
        .addIngredients(Ingredient.of(Tags.Items.LEATHERS), 3)
        .addIngredients(Ingredient.of(Tags.Items.NUGGETS_IRON), 5)
        .setItemBonus(
            new AttributeBonus(
                Attributes.ATTACK_SPEED.value(),
                new AttributeModifier(
                    modRecipeId("melee_weapon_attack_speed_bonus"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))
        .setRequiresPassiveSkill()
        .save(consumer);
    WorkbenchItemBonusRecipeBuilder.create(modRecipeId("boots_movement_speed_bonus"))
        .setBaseItemCondition(new EquipmentPredicate(EquipmentPredicate.Type.BOOTS))
        .addIngredients(Ingredient.of(Tags.Items.LEATHERS), 4)
        .addIngredients(Ingredient.of(Tags.Items.NUGGETS_IRON), 3)
        .setItemBonus(
            new AttributeBonus(
                Attributes.MOVEMENT_SPEED.value(),
                new AttributeModifier(
                    modRecipeId("boots_movement_speed_bonus"), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))
        .setRequiresPassiveSkill()
        .save(consumer);
    WorkbenchItemBonusRecipeBuilder.create(modRecipeId("melee_weapon_attack_damage_bonus"))
        .setBaseItemCondition(new EquipmentPredicate(EquipmentPredicate.Type.MELEE_WEAPON))
        .addIngredients(Ingredient.of(Tags.Items.GEMS_DIAMOND), 1)
        .setItemBonus(
            new AttributeBonus(
                Attributes.ATTACK_DAMAGE.value(),
                new AttributeModifier(
                    modRecipeId("melee_weapon_attack_damage_bonus"), 1, AttributeModifier.Operation.ADD_VALUE)))
        .setRequiresPassiveSkill()
        .save(consumer);
    WorkbenchItemBonusRecipeBuilder.create(modRecipeId("chestplates_toughness_bonus"))
        .setBaseItemCondition(new EquipmentPredicate(EquipmentPredicate.Type.CHESTPLATE))
        .addIngredients(Ingredient.of(Tags.Items.ORES_NETHERITE_SCRAP), 2)
        .addIngredients(Ingredient.of(Tags.Items.GEMS_DIAMOND), 3)
        .setItemBonus(
            new AttributeBonus(
                Attributes.ARMOR_TOUGHNESS.value(),
                new AttributeModifier(
                    modRecipeId("chestplates_toughness_bonus"), 1, AttributeModifier.Operation.ADD_VALUE)))
        .setRequiresPassiveSkill()
        .save(consumer);
  }

  private static void addWorkbenchCraftingRecipes(@NotNull RecipeOutput consumer) {
    WorkbenchCraftingRecipeBuilder.create(modRecipeId("saddle_crafting"))
        .addIngredients(Ingredient.of(Tags.Items.LEATHERS), 8)
        .addIngredients(Ingredient.of(Tags.Items.NUGGETS_IRON), 4)
        .addIngredients(Ingredient.of(Tags.Items.INGOTS_IRON), 2)
        .setResult(new ItemStack(Items.SADDLE))
        .setRequiresPassiveSkill()
        .save(consumer);
    WorkbenchCraftingRecipeBuilder.create(modRecipeId("name_tag_crafting"))
        .addIngredients(Ingredient.of(Items.PAPER), 8)
        .addIngredients(Ingredient.of(Tags.Items.NUGGETS_IRON), 2)
        .addIngredients(Ingredient.of(Tags.Items.INGOTS_IRON), 1)
        .setResult(new ItemStack(Items.NAME_TAG))
        .setRequiresPassiveSkill()
        .save(consumer);
  }

  private static ResourceLocation modRecipeId(String path) {
    return ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, path);
  }
}
