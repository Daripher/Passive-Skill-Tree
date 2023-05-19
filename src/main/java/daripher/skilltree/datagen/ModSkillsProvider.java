package daripher.skilltree.datagen;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ModSkillsProvider implements DataProvider {
	private Map<ResourceLocation, PassiveSkill> data = new HashMap<>();
	private String[] playerClasses = new String[] { "alchemist", "hunter", "enchanter", "cook", "blacksmith", "miner" };
	private DataGenerator dataGenerator;

	public ModSkillsProvider(DataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	private void addSkills() {
		Arrays.stream(playerClasses).forEach(this::addClassSkills);
		addSkill("center_node", "center_node", 24);
	}

	private void addClassSkills(String playerClass) {
		addSkill(playerClass + "_class", playerClass + "_class", 24);
		addSkillBranch(playerClass, "_defensive_crafting_", playerClass + "_defensive_crafting", 16, 1, 8);
		addSkillBranch(playerClass, "_offensive_crafting_", playerClass + "_offensive_crafting", 16, 1, 8);
		addSkillBranch(playerClass, "_defensive_combat_", playerClass + "_defensive_combat_1", 16, 1, 6);
		addSkillBranch(playerClass, "_defensive_combat_", playerClass + "_defensive_combat_2", 16, 7, 11);
		addSkillBranch(playerClass, "_offensive_combat_", playerClass + "_offensive_combat_1", 16, 1, 6);
		addSkillBranch(playerClass, "_offensive_combat_", playerClass + "_offensive_combat_2", 16, 7, 11);
		addSkill(playerClass + "_offensive_notable_1", playerClass + "_offensive_notable", 20);
		addSkill(playerClass + "_defensive_notable_1", playerClass + "_defensive_notable", 20);
		addSkill(playerClass + "_offensive_keystone_1", playerClass + "_offensive_keystone", 24);
		addSkill(playerClass + "_defensive_keystone_1", playerClass + "_defensive_keystone", 24);
		addSkill(playerClass + "_mixed_notable_1", playerClass + "_mixed_notable", 20);
		addSkillBranch(playerClass, "_lesser_", playerClass + "_lesser", 16, 1, 7);
		addSkill(playerClass + "_life_notable_1", playerClass + "_life_notable", 20);
		addSkill(playerClass + "_offensive_crafting_keystone_1", playerClass + "_offensive_crafting_keystone", 24);
		addSkill(playerClass + "_defensive_crafting_keystone_1", playerClass + "_defensive_crafting_keystone", 24);
		addSkill(playerClass + "_mastery", playerClass + "_mastery", 24);
	}

	private void shapeSkillTree() {
		setSkillPosition(null, 0, -12, 0, "center_node");
		Arrays.stream(playerClasses).forEach(this::shapeClassTree);
		connectClassTrees();
	}

	private void shapeClassTree(String playerClass) {
		var classId = Arrays.asList(playerClasses).indexOf(playerClass);
		setSkillPosition(null, classId, 72, 0, playerClass + "_class");
		setSkillPosition(playerClass + "_class", classId, 8, -75, playerClass + "_defensive_crafting_1");
		setSkillPosition(playerClass + "_class", classId, 8, 75, playerClass + "_offensive_crafting_1");
		setSkillPosition(playerClass + "_defensive_crafting_1", classId, 8, -55, playerClass + "_defensive_crafting_2");
		setSkillPosition(playerClass + "_offensive_crafting_1", classId, 8, 55, playerClass + "_offensive_crafting_2");
		setSkillPosition(playerClass + "_defensive_crafting_1", classId, 8, 15, playerClass + "_defensive_combat_1");
		setSkillPosition(playerClass + "_offensive_crafting_1", classId, 8, -15, playerClass + "_offensive_combat_1");
		setSkillBranchPosition(playerClass, classId, playerClass + "_defensive_crafting_2", 8, "_defensive_crafting_", -30, 0, 3, 5);
		setSkillBranchPosition(playerClass, classId, playerClass + "_offensive_crafting_2", 8, "_offensive_crafting_", 30, 0, 3, 5);
		setSkillBranchPosition(playerClass, classId, playerClass + "_defensive_combat_1", 8, "_defensive_combat_", -30, 0, 2, 4);
		setSkillBranchPosition(playerClass, classId, playerClass + "_offensive_combat_1", 8, "_offensive_combat_", 30, 0, 2, 4);
		setSkillPosition(playerClass + "_defensive_crafting_5", classId, 6, 15, playerClass + "_defensive_notable_1");
		setSkillPosition(playerClass + "_offensive_crafting_5", classId, 6, -15, playerClass + "_offensive_notable_1");
		connectSkills(playerClass + "_defensive_combat_4", playerClass + "_defensive_notable_1");
		connectSkills(playerClass + "_offensive_combat_4", playerClass + "_offensive_notable_1");
		setSkillPosition(playerClass + "_defensive_combat_4", classId, 8, 15, playerClass + "_defensive_combat_5");
		setSkillPosition(playerClass + "_defensive_combat_5", classId, 8, 90, playerClass + "_defensive_combat_7");
		setSkillPosition(playerClass + "_offensive_combat_4", classId, 8, -15, playerClass + "_offensive_combat_5");
		setSkillPosition(playerClass + "_offensive_combat_5", classId, 8, -90, playerClass + "_offensive_combat_7");
		setSkillPosition(playerClass + "_defensive_combat_7", classId, 6, 180, playerClass + "_defensive_combat_8");
		setSkillPosition(playerClass + "_offensive_combat_7", classId, 6, 180, playerClass + "_offensive_combat_8");
		setSkillPosition(playerClass + "_defensive_combat_8", classId, 8, 150, playerClass + "_defensive_combat_9");
		setSkillPosition(playerClass + "_offensive_combat_8", classId, 8, 210, playerClass + "_offensive_combat_9");
		connectSkills(playerClass + "_defensive_combat_9", playerClass + "_offensive_keystone_1");
		setSkillPosition(playerClass + "_offensive_combat_9", classId, 9, 210, playerClass + "_offensive_keystone_1");
		setSkillPosition(playerClass + "_offensive_combat_7", classId, 8, -90, playerClass + "_mixed_notable_1");
		connectSkills(playerClass + "_defensive_combat_7", playerClass + "_mixed_notable_1");
		setSkillPosition(playerClass + "_defensive_combat_7", classId, 6, 0, playerClass + "_defensive_combat_10");
		setSkillPosition(playerClass + "_offensive_combat_7", classId, 6, 0, playerClass + "_offensive_combat_10");
		setSkillPosition(playerClass + "_defensive_combat_10", classId, 8, 30, playerClass + "_defensive_combat_11");
		setSkillPosition(playerClass + "_offensive_combat_10", classId, 8, -30, playerClass + "_offensive_combat_11");
		setSkillPosition(playerClass + "_offensive_combat_11", classId, 9, -30, playerClass + "_defensive_keystone_1");
		connectSkills(playerClass + "_defensive_combat_11", playerClass + "_defensive_keystone_1");
		setSkillBranchPosition(playerClass, classId, playerClass + "_offensive_notable_1", 8, "_lesser_", 52, 0, 1, 2);
		setSkillBranchPosition(playerClass, classId, playerClass + "_defensive_notable_1", 8, "_lesser_", -52, 0, 3, 4);
		setSkillPosition(playerClass + "_lesser_2", classId, 9, 52, playerClass + "_life_notable_1");
		setSkillPosition(playerClass + "_class", classId, 9, 180, playerClass + "_lesser_5");
		setSkillPosition(playerClass + "_lesser_5", classId, 9, 180, playerClass + "_lesser_6");
		setSkillPosition(playerClass + "_lesser_6", classId, 9, 65, playerClass + "_lesser_7");
		setSkillPosition(playerClass + "_lesser_7", classId, 7, 30, playerClass + "_mastery");
		connectSkills(playerClass + "_lesser_6", "center_node");
		setSkillPosition(playerClass + "_lesser_1", classId, 8, -52, playerClass + "_offensive_crafting_6");
		setSkillBranchPosition(playerClass, classId, playerClass + "_offensive_crafting_6", 8, "_offensive_crafting_", 52, 0, 7, 8);
		setSkillPosition(playerClass + "_offensive_crafting_8", classId, 8, 52, playerClass + "_offensive_crafting_keystone_1");
		setSkillPosition(playerClass + "_lesser_3", classId, 8, 52, playerClass + "_defensive_crafting_6");
		setSkillBranchPosition(playerClass, classId, playerClass + "_defensive_crafting_6", 8, "_defensive_crafting_", -52, 0, 7, 8);
		setSkillPosition(playerClass + "_defensive_crafting_8", classId, 8, -52, playerClass + "_defensive_crafting_keystone_1");
		setSkillPosition(playerClass + "_offensive_combat_5", classId, 6, 0, playerClass + "_offensive_combat_6");
		connectSkills(playerClass + "_offensive_combat_6", playerClass + "_offensive_combat_10");
		connectSkills(playerClass + "_offensive_combat_6", playerClass + "_offensive_crafting_6");
		setSkillPosition(playerClass + "_defensive_combat_5", classId, 6, 0, playerClass + "_defensive_combat_6");
		connectSkills(playerClass + "_defensive_combat_6", playerClass + "_defensive_combat_10");
		connectSkills(playerClass + "_defensive_combat_6", playerClass + "_defensive_crafting_6");
	}

	protected void connectClassTrees() {
		connectSkillsBetweenClasses("_offensive_crafting_3", "_defensive_crafting_3");
		connectSkillsBetweenClasses("_offensive_crafting_4", "_defensive_crafting_4");
		connectSkillsBetweenClasses("_life_notable_1", "_lesser_4");
		connectSkillsBetweenClasses("_lesser_7", "_lesser_6");
	}

	private void setSkillsAttributeModifiers() {
		// hunter skills
		addSkillAttributeModifier("hunter_class", SkillTreeAttributes.DOUBLE_LOOT_CHANCE.get(), 0.15, Operation.ADDITION);
		addSkillBranchAttributeModifier("hunter_offensive_crafting_", SkillTreeAttributes.CRAFTED_BOWS_CHARGE_SPEED_BONUS.get(), 0.04, Operation.ADDITION, 1, 8);
		addSkillBranchAttributeModifier("hunter_defensive_crafting_", SkillTreeAttributes.CRAFTED_ARMOR_EVASION_BONUS.get(), 0.01, Operation.ADDITION, 1, 8);
		addSkillBranchAttributeModifier("hunter_offensive_combat_", SkillTreeAttributes.ARROW_DAMAGE_BONUS.get(), 1, Operation.ADDITION, 1, 6);
		addSkillBranchAttributeModifier("hunter_defensive_combat_", SkillTreeAttributes.EVASION_CHANCE_MULTIPLIER.get(), 0.01, Operation.MULTIPLY_BASE, 1, 6);
		addSkillBranchAttributeModifier("hunter_offensive_combat_", SkillTreeAttributes.ARROW_CRIT_DAMAGE_MULTIPLIER.get(), 0.05, Operation.MULTIPLY_BASE, 7, 11);
		addSkillBranchAttributeModifier("hunter_defensive_combat_", Attributes.MOVEMENT_SPEED, 0.02, Operation.MULTIPLY_BASE, 7, 11);
		addSkillAttributeModifier("hunter_offensive_notable_1", SkillTreeAttributes.ARROW_DAMAGE_MULTIPLIER.get(), 0.2, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_defensive_notable_1", SkillTreeAttributes.EVASION_CHANCE_MULTIPLIER.get(), 0.05, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_mixed_notable_1", SkillTreeAttributes.BOW_CHARGE_SPEED_MULTIPLIER.get(), 0.1, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_mixed_notable_1", Attributes.MOVEMENT_SPEED, 0.05, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_offensive_keystone_1", SkillTreeAttributes.ARROW_DAMAGE_PER_DISTANCE_MULTIPLIER.get(), 0.02, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_defensive_keystone_1", SkillTreeAttributes.ARMOR_PER_EVASION.get(), 1, Operation.ADDITION);
		addSkillBranchAttributeModifier("hunter_lesser_", SkillTreeAttributes.DOUBLE_LOOT_CHANCE.get(), 0.05, Operation.MULTIPLY_BASE, 1, 7);
		addSkillAttributeModifier("hunter_life_notable_1", SkillTreeAttributes.MAXIMUM_LIFE_PER_EVASION.get(), 1 / 3D, Operation.ADDITION);
		addSkillAttributeModifier("hunter_mastery", SkillTreeAttributes.TRIPLE_LOOT_CHANCE.get(), 0.05, Operation.ADDITION);
		addSkillAttributeModifier("hunter_defensive_crafting_keystone_1", SkillTreeAttributes.CRAFTED_HELMETS_ADDITIONAL_GEMSTONE_SLOTS.get(), 1, Operation.ADDITION);
		addSkillAttributeModifier("hunter_offensive_crafting_keystone_1", SkillTreeAttributes.CRAFTED_BOWS_ADDITIONAL_GEMSTONE_SLOTS.get(), 1, Operation.ADDITION);
		// cook skills
		addSkillAttributeModifier("cook_class", SkillTreeAttributes.COOKED_FOOD_RESTORATION_MULTIPLIER.get(), 0.25, Operation.MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_offensive_crafting_", SkillTreeAttributes.COOKED_FOOD_DAMAGE_PER_RESTORATION_MULTIPLIER.get(), 0.01, Operation.MULTIPLY_BASE, 1, 8);
		addSkillBranchAttributeModifier("cook_defensive_crafting_", SkillTreeAttributes.COOKED_FOOD_LIFE_REGENERATION_PER_RESTORATION.get(), 0.1, Operation.ADDITION, 1, 8);
		addSkillBranchAttributeModifier("cook_offensive_combat_", SkillTreeAttributes.AXE_ATTACK_SPEED_MULTIPLIER.get(), 0.1, Operation.MULTIPLY_BASE, 1, 6);
		addSkillBranchAttributeModifier("cook_defensive_combat_", SkillTreeAttributes.BLOCK_CHANCE_MULTIPLIER.get(), 0.01, Operation.MULTIPLY_BASE, 1, 6);
		addSkillBranchAttributeModifier("cook_offensive_combat_", SkillTreeAttributes.AXE_CRIT_CHANCE_MULTIPLIER.get(), 0.05, Operation.MULTIPLY_BASE, 7, 11);
		addSkillBranchAttributeModifier("cook_defensive_combat_", SkillTreeAttributes.MAXIMUM_LIFE_IF_ATE_RECENTLY.get(), 1, Operation.ADDITION, 7, 11);
		addSkillAttributeModifier("cook_offensive_notable_1", SkillTreeAttributes.AXE_CRIT_CHANCE_MULTIPLIER.get(), 0.2, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("cook_defensive_notable_1", SkillTreeAttributes.BLOCK_CHANCE_MULTIPLIER.get(), 0.05, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("cook_mixed_notable_1", SkillTreeAttributes.MAXIMUM_LIFE_IF_ATE_RECENTLY.get(), 5, Operation.ADDITION);
		addSkillAttributeModifier("cook_mixed_notable_1", SkillTreeAttributes.DAMAGE_MULTIPLIER_IF_ATE_RECENTLY.get(), 0.5, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("cook_offensive_keystone_1", SkillTreeAttributes.CRIT_DAMAGE_MULTIPLIER_PER_SATISFIED_HUNGER.get(), 0.05, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("cook_defensive_keystone_1", SkillTreeAttributes.MAXIMUM_LIFE_PER_SATISFIED_HUNGER.get(), 1, Operation.ADDITION);
		addSkillBranchAttributeModifier("cook_lesser_", SkillTreeAttributes.COOKED_FOOD_RESTORATION_MULTIPLIER.get(), 0.05, Operation.MULTIPLY_BASE, 1, 7);
		addSkillAttributeModifier("cook_life_notable_1", SkillTreeAttributes.MAXIMUM_LIFE_IF_ATE_RECENTLY.get(), 10, Operation.ADDITION);
		addSkillAttributeModifier("cook_mastery", SkillTreeAttributes.COOKED_FOOD_HEALING_PER_RESTORATION_MULTIPLIER.get(), 0.5, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("cook_defensive_crafting_keystone_1", SkillTreeAttributes.COOKED_FOOD_RESTORATION_MULTIPLIER.get(), 0.6, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("cook_offensive_crafting_keystone_1", SkillTreeAttributes.COOKED_FOOD_CRITICAL_DAMAGE_PER_RESTORATION_MULTIPLIER.get(), 0.05, Operation.MULTIPLY_BASE);
		// alchemist skills
		addSkillAttributeModifier("alchemist_class", SkillTreeAttributes.BREWED_POTIONS_DURATION_MULTIPLIER.get(), 0.4, Operation.MULTIPLY_BASE);
		addSkillBranchAttributeModifier("alchemist_offensive_crafting_", SkillTreeAttributes.BREWED_HARMFUL_POTIONS_STRENGTH_MULTIPLIER.get(), 0.05, Operation.MULTIPLY_BASE, 1, 8);
		addSkillBranchAttributeModifier("alchemist_defensive_crafting_", SkillTreeAttributes.BREWED_BENEFICIAL_POTIONS_STRENGTH_MULTIPLIER.get(), 0.05, Operation.MULTIPLY_BASE, 1, 8);
		addSkillBranchAttributeModifier("alchemist_offensive_combat_", SkillTreeAttributes.DAMAGE_AGAINST_POISONED_MULTIPLIER.get(), 0.2, Operation.MULTIPLY_BASE, 1, 6);
		addSkillBranchAttributeModifier("alchemist_defensive_combat_", SkillTreeAttributes.EVASION_UNDER_POTION_EFFECT_MULTIPLIER.get(), 0.02, Operation.MULTIPLY_BASE, 1, 6);
		addSkillBranchAttributeModifier("alchemist_offensive_combat_", SkillTreeAttributes.ATTACK_SPEED_UNDER_POTION_EFFECT_MULTIPLIER.get(), 0.03, Operation.MULTIPLY_BASE, 7, 11);
		addSkillBranchAttributeModifier("alchemist_defensive_combat_", SkillTreeAttributes.MAXIMUM_LIFE_UNDER_POTION_EFFECT.get(), 2, Operation.ADDITION, 7, 11);
		addSkillAttributeModifier("alchemist_offensive_notable_1", SkillTreeAttributes.CRIT_CHANCE_AGAINST_POISONED_MULTIPLIER.get(), 0.3, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_defensive_notable_1", SkillTreeAttributes.EVASION_UNDER_POTION_EFFECT_MULTIPLIER.get(), 0.1, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_mixed_notable_1", SkillTreeAttributes.MAXIMUM_LIFE_UNDER_POTION_EFFECT.get(), 10, Operation.ADDITION);
		addSkillAttributeModifier("alchemist_mixed_notable_1", SkillTreeAttributes.DAMAGE_UNDER_POTION_EFFECT_MULTIPLIER.get(), 0.2, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_offensive_keystone_1", SkillTreeAttributes.DAMAGE_PER_POTION_EFFECT_MULTIPLIER.get(), 0.1, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_defensive_keystone_1", SkillTreeAttributes.MAXIMUM_LIFE_PER_POTION_EFFECT.get(), 2, Operation.ADDITION);
		addSkillBranchAttributeModifier("alchemist_lesser_", SkillTreeAttributes.BREWED_POTIONS_DURATION_MULTIPLIER.get(), 0.1, Operation.MULTIPLY_BASE, 1, 7);
		addSkillAttributeModifier("alchemist_life_notable_1", SkillTreeAttributes.MAXIMUM_LIFE_UNDER_POTION_EFFECT.get(), 10, Operation.ADDITION);
		addSkillAttributeModifier("alchemist_mastery", SkillTreeAttributes.BREWED_POTIONS_STRENGTH_MULTIPLIER.get(), 1, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_defensive_crafting_keystone_1", SkillTreeAttributes.BREWED_BENEFICIAL_POTIONS_STRENGTH_MULTIPLIER.get(), 1.75, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_offensive_crafting_keystone_1", SkillTreeAttributes.BREWED_HARMFUL_POTIONS_STRENGTH_MULTIPLIER.get(), 0.75, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_offensive_crafting_keystone_1", SkillTreeAttributes.MAXIMUM_WEAPON_POISONS_BONUS.get(), 1, Operation.ADDITION);
		// enchanter class
		addSkillAttributeModifier("enchanter_class", SkillTreeAttributes.ENCHANTMENT_LEVEL_REQUIREMENT_DECREASE.get(), 0.3, Operation.MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_offensive_crafting_", SkillTreeAttributes.WEAPON_ENCHANTMENTS_AMPLIFICATION_CHANCE.get(), 0.1, Operation.ADDITION, 1, 8);
		addSkillBranchAttributeModifier("enchanter_defensive_crafting_", SkillTreeAttributes.ARMOR_ENCHANTMENTS_AMPLIFICATION_CHANCE.get(), 0.1, Operation.ADDITION, 1, 8);
		addSkillBranchAttributeModifier("enchanter_offensive_combat_", SkillTreeAttributes.DAMAGE_PER_ENCHANTMENT_MULTIPLIER.get(), 0.01, Operation.MULTIPLY_BASE, 1, 6);
		addSkillBranchAttributeModifier("enchanter_defensive_combat_", Attributes.ARMOR, 1, Operation.ADDITION, 1, 6);
		addSkillBranchAttributeModifier("enchanter_offensive_combat_", SkillTreeAttributes.CRIT_CHANCE_PER_ENCHANTMENT_MULTIPLIER.get(), 0.01, Operation.MULTIPLY_BASE, 7, 11);
		addSkillBranchAttributeModifier("enchanter_defensive_combat_", SkillTreeAttributes.MAXIMUM_LIFE_WITH_ENCHANTED_ITEM.get(), 2, Operation.ADDITION, 7, 11);
		addSkillAttributeModifier("enchanter_offensive_notable_1", SkillTreeAttributes.CRIT_DAMAGE_PER_ENCHANTMENT_MULTIPLIER.get(), 0.05, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_defensive_notable_1", SkillTreeAttributes.ARMOR_WITH_ENCHANTED_SHIELD.get(), 10, Operation.ADDITION);
		addSkillAttributeModifier("enchanter_mixed_notable_1", SkillTreeAttributes.MAXIMUM_LIFE_PER_ENCHANTMENT.get(), 2, Operation.ADDITION);
		addSkillAttributeModifier("enchanter_mixed_notable_1", SkillTreeAttributes.DAMAGE_PER_ENCHANTMENT_MULTIPLIER.get(), 0.05, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_offensive_keystone_1", SkillTreeAttributes.DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL_MULTIPLIER.get(), 0.05, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_defensive_keystone_1", SkillTreeAttributes.ARMOR_PER_CHESTPLATE_ENCHANTMENT.get(), 2, Operation.ADDITION);
		addSkillBranchAttributeModifier("enchanter_lesser_", SkillTreeAttributes.ENCHANTMENT_LEVEL_REQUIREMENT_DECREASE.get(), 0.05, Operation.MULTIPLY_BASE, 1, 7);
		addSkillAttributeModifier("enchanter_life_notable_1", SkillTreeAttributes.MAXIMUM_LIFE_PER_ARMOR_ENCHANTMENT.get(), 1, Operation.ADDITION);
		addSkillAttributeModifier("enchanter_mastery", SkillTreeAttributes.ENCHANTMENTS_AMPLIFICATION_CHANCE.get(), 1, Operation.ADDITION);
		addSkillAttributeModifier("enchanter_defensive_crafting_keystone_1", SkillTreeAttributes.ARMOR_ENCHANTMENTS_AMPLIFICATION_CHANCE.get(), 0.5, Operation.ADDITION);
		addSkillAttributeModifier("enchanter_offensive_crafting_keystone_1", SkillTreeAttributes.WEAPON_ENCHANTMENTS_AMPLIFICATION_CHANCE.get(), 0.5, Operation.ADDITION);
		// blacksmith class
		addSkillAttributeModifier("blacksmith_class", SkillTreeAttributes.CRAFTED_ARMOR_DEFENCE_MULTIPLIER.get(), 0.4, Operation.MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_offensive_crafting_", SkillTreeAttributes.CRAFTED_WEAPON_DAMAGE_BONUS.get(), 1, Operation.ADDITION, 1, 8);
		addSkillBranchAttributeModifier("blacksmith_defensive_crafting_", SkillTreeAttributes.CRAFTED_ARMOR_DEFENCE_MULTIPLIER.get(), 0.1, Operation.MULTIPLY_BASE, 1, 8);
		addSkillBranchAttributeModifier("blacksmith_offensive_combat_", SkillTreeAttributes.DAMAGE_WITH_SHIELD_MULTIPLIER.get(), 0.1, Operation.ADDITION, 1, 6);
		addSkillBranchAttributeModifier("blacksmith_defensive_combat_", SkillTreeAttributes.BLOCK_CHANCE_MULTIPLIER.get(), 0.02, Operation.MULTIPLY_BASE, 1, 6);
		addSkillBranchAttributeModifier("blacksmith_offensive_combat_", SkillTreeAttributes.CRIT_CHANCE_WITH_SHIELD_MULTIPLIER.get(), 0.05, Operation.MULTIPLY_BASE, 7, 11);
		addSkillBranchAttributeModifier("blacksmith_defensive_combat_", SkillTreeAttributes.CHESTPLATE_ARMOR_MULTIPLIER.get(), 0.04, Operation.MULTIPLY_BASE, 7, 11);
		addSkillAttributeModifier("blacksmith_offensive_notable_1", SkillTreeAttributes.CRIT_DAMAGE_WITH_SHIELD_MULTIPLIER.get(), 0.25, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_defensive_notable_1", Attributes.ARMOR, 2, Operation.ADDITION);
		addSkillAttributeModifier("blacksmith_defensive_notable_1", Attributes.ARMOR, 0.1, Operation.MULTIPLY_TOTAL);
		addSkillAttributeModifier("blacksmith_mixed_notable_1", SkillTreeAttributes.DAMAGE_WITH_SHIELD_MULTIPLIER.get(), 0.2, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_mixed_notable_1", SkillTreeAttributes.ARMOR_WITH_SHIELD_MULTIPLIER.get(), 0.1, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_offensive_keystone_1", SkillTreeAttributes.ATTACK_DAMAGE_PER_ARMOR.get(), 0.1, Operation.ADDITION);
		addSkillAttributeModifier("blacksmith_defensive_keystone_1", SkillTreeAttributes.MAXIMUM_LIFE_PER_ARMOR.get(), 0.2, Operation.ADDITION);
		addSkillBranchAttributeModifier("blacksmith_lesser_", SkillTreeAttributes.CRAFTED_ARMOR_BONUS_TOUGHNESS_CHANCE_MULTIPLIER.get(), 0.1, Operation.MULTIPLY_BASE, 1, 7);
		addSkillAttributeModifier("blacksmith_life_notable_1", SkillTreeAttributes.MAXIMUM_LIFE_PER_BOOTS_ARMOR.get(), 1, Operation.ADDITION);
		addSkillAttributeModifier("blacksmith_mastery", SkillTreeAttributes.CRAFTED_ARMOR_BONUS_TOUGHNESS_CHANCE_MULTIPLIER.get(), 1, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_defensive_crafting_keystone_1", SkillTreeAttributes.CRAFTED_SHIELDS_ARMOR_BONUS.get(), 5, Operation.ADDITION);
		addSkillAttributeModifier("blacksmith_defensive_crafting_keystone_1", SkillTreeAttributes.CRAFTED_ARMOR_DEFENCE_MULTIPLIER.get(), 0.3, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_offensive_crafting_keystone_1", SkillTreeAttributes.CRAFTED_WEAPON_ATTACK_SPEED_MULTIPLIER.get(), 0.25, Operation.MULTIPLY_BASE);
		// miner skills
		addSkillAttributeModifier("miner_class", SkillTreeAttributes.MINING_SPEED_MULTIPLIER.get(), 0.15, Operation.MULTIPLY_BASE);
		addSkillBranchAttributeModifier("miner_offensive_crafting_", SkillTreeAttributes.GEMSTONES_STRENGTH_IN_WEAPON_MULTIPLIER.get(), 0.1, Operation.MULTIPLY_BASE, 1, 8);
		addSkillBranchAttributeModifier("miner_defensive_crafting_", SkillTreeAttributes.GEMSTONES_STRENGTH_IN_ARMOR_MULTIPLIER.get(), 0.1, Operation.MULTIPLY_BASE, 1, 8);
		addSkillBranchAttributeModifier("miner_offensive_combat_", SkillTreeAttributes.PICKAXE_DAMAGE_BONUS.get(), 0.01, Operation.ADDITION, 1, 6);
		addSkillBranchAttributeModifier("miner_defensive_combat_", Attributes.ARMOR, 1, Operation.ADDITION, 1, 6);
		addSkillBranchAttributeModifier("miner_offensive_combat_", SkillTreeAttributes.PICKAXE_ATTACK_SPEED_MULTIPLIER.get(), 0.1, Operation.MULTIPLY_BASE, 7, 11);
		addSkillBranchAttributeModifier("miner_defensive_combat_", Attributes.MOVEMENT_SPEED, 0.02, Operation.MULTIPLY_BASE, 7, 11);
		addSkillAttributeModifier("miner_offensive_notable_1", SkillTreeAttributes.DAMAGE_PER_GEMSTONE_IN_WEAPON_MULTIPLIER.get(), 0.25, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("miner_defensive_notable_1", SkillTreeAttributes.ARMOR_PER_GEMSTONE_IN_HELMET.get(), 2, Operation.ADDITION);
		addSkillAttributeModifier("miner_mixed_notable_1", SkillTreeAttributes.PICKAXE_DAMAGE_BONUS.get(), 5, Operation.ADDITION);
		addSkillAttributeModifier("miner_mixed_notable_1", Attributes.ARMOR, 5, Operation.ADDITION);
		addSkillAttributeModifier("miner_offensive_keystone_1", SkillTreeAttributes.PICKAXE_DAMAGE_MULTIPLIER.get(), 0.5, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("miner_defensive_keystone_1", SkillTreeAttributes.ARMOR_PER_GEMSTONE_IN_CHESTPLATE.get(), 5, Operation.ADDITION);
		addSkillBranchAttributeModifier("miner_lesser_", SkillTreeAttributes.MINING_SPEED_MULTIPLIER.get(), 0.05, Operation.MULTIPLY_BASE, 1, 7);
		addSkillAttributeModifier("miner_life_notable_1", SkillTreeAttributes.MAXIMUM_LIFE_PER_GEMSTONE_IN_ARMOR.get(), 1, Operation.ADDITION);
		addSkillAttributeModifier("miner_mastery", SkillTreeAttributes.MAXIMUM_GEMSTONE_SLOTS_BONUS.get(), 1, Operation.ADDITION);
		addSkillAttributeModifier("miner_defensive_crafting_keystone_1", SkillTreeAttributes.GEMSTONES_STRENGTH_IN_ARMOR_MULTIPLIER.get(), 0.2, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("miner_defensive_crafting_keystone_1", SkillTreeAttributes.MAXIMUM_CHESTPLATE_GEMSTONE_SLOTS_BONUS.get(), 1, Operation.ADDITION);
		addSkillAttributeModifier("miner_offensive_crafting_keystone_1", SkillTreeAttributes.GEMSTONES_STRENGTH_IN_WEAPON_MULTIPLIER.get(), 0.2, Operation.MULTIPLY_BASE);
		addSkillAttributeModifier("miner_offensive_crafting_keystone_1", SkillTreeAttributes.MAXIMUM_WEAPON_GEMSTONE_SLOTS_BONUS.get(), 1, Operation.ADDITION);
	}

	private void addSkillBranchAttributeModifier(String branchName, Attribute attribute, double amount, Operation operation, int from, int to) {
		for (var node = from; node <= to; node++) {
			addSkillAttributeModifier(branchName + node, attribute, amount, operation);
		}
	}

	private void addSkillAttributeModifier(String skillName, Attribute attribute, double amount, Operation operation) {
		getSkill(skillName).addAttributeBonus(attribute, new AttributeModifier(UUID.randomUUID(), "Passive Skill Bonus", amount, operation));
	}

	public void addSkillBranch(String playerClass, String branchName, String iconName, int nodeSize, int from, int to) {
		for (var node = from; node <= to; node++) {
			addSkill(playerClass + branchName + node, iconName, nodeSize);
		}
	}

	private void connectSkillsBetweenClasses(String from, String to) {
		for (var classId = 0; classId < playerClasses.length - 1; classId++) {
			connectSkills(playerClasses[classId] + from, playerClasses[classId + 1] + to);
		}

		connectSkills(playerClasses[5] + from, playerClasses[0] + to);
	}

	private void setSkillBranchPosition(String playerClass, int classId, String initialNodeName, int distanceBetweenNodes, String branchName, int initialRotation, int rotationPerNode, int from, int to) {
		var branchNode = initialNodeName;

		for (var node = from; node <= to; node++) {
			setSkillPosition(branchNode, classId, distanceBetweenNodes, initialRotation + (node - from) * rotationPerNode, playerClass + branchName + node);
			branchNode = playerClass + branchName + node;
		}
	}

	private void setSkillPosition(@Nullable String previousSkillName, int classId, float distance, float angle, String skillName) {
		angle *= Mth.PI / 180F;
		angle += getClassBranchRotation(classId);
		var previousSkill = previousSkillName == null ? null : getSkill(previousSkillName);
		var skill = getSkill(skillName);
		var centerX = 0F;
		var centerY = 0F;
		var buttonSize = skill.getButtonSize();
		distance += buttonSize / 2F;

		if (previousSkill != null) {
			var previousButtonRadius = previousSkill.getButtonSize() / 2;
			distance += previousButtonRadius;
			centerX = previousSkill.getPositionX() + previousButtonRadius;
			centerY = previousSkill.getPositionY() + previousButtonRadius;
		}

		var skillX = centerX + Mth.sin(angle) * distance - buttonSize / 2F;
		var skillY = centerY + Mth.cos(angle) * distance - buttonSize / 2F;
		skill.setPosition(Math.round(skillX), Math.round(skillY));

		if (previousSkill != null) {
			previousSkill.connect(skill);
		}
	}

	public float getClassBranchRotation(int classId) {
		return classId * Mth.PI * 2F / 6F;
	}

	private PassiveSkill getSkill(String skillName) {
		return getSkills().get(getSkillId(skillName));
	}

	private void connectSkills(String skillName1, String skillName2) {
		getSkill(skillName1).connect(getSkill(skillName2));
	}

	private ResourceLocation getSkillId(String skillName) {
		return new ResourceLocation(SkillTreeMod.MOD_ID, skillName);
	}

	private void addSkill(String skillName, String iconName, int buttonSize) {
		var skillId = new ResourceLocation(SkillTreeMod.MOD_ID, skillName);
		var treeId = new ResourceLocation(SkillTreeMod.MOD_ID, "tree");
		var backgroundName = buttonSize == 24 ? "keystone" : buttonSize == 20 ? "notable" : "lesser";
		var backgroundTexture = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/background/" + backgroundName + ".png");
		var iconTexture = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/" + iconName + ".png");
		data.put(skillId, new PassiveSkill(skillId, treeId, buttonSize, backgroundTexture, iconTexture, skillName.endsWith("_class")));
	}

	@Override
	public void run(CachedOutput cachedOutput) throws IOException {
		addSkills();
		shapeSkillTree();
		setSkillsAttributeModifiers();

		for (var skill : data.values()) {
			save(cachedOutput, skill);
		}
	}

	private void save(CachedOutput cachedOutput, PassiveSkill skill) throws IOException {
		var path = dataGenerator.getOutputFolder().resolve("data/" + skill.getId().getNamespace() + "/skills/" + skill.getId().getPath() + ".json");
		var json = skill.writeToJson();
		DataProvider.saveStable(cachedOutput, json, path);
	}

	public Map<ResourceLocation, PassiveSkill> getSkills() {
		return data;
	}

	@Override
	public String getName() {
		return "Skills Provider";
	}
}
