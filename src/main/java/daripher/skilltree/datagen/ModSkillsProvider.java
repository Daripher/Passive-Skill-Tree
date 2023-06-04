package daripher.skilltree.datagen;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.*;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;
import static daripher.skilltree.init.SkillTreeAttributes.*;

public class ModSkillsProvider implements DataProvider {
	private Map<ResourceLocation, PassiveSkill> data = new HashMap<>();
	private String[] playerClasses = new String[] { "alchemist", "hunter", "enchanter", "cook", "blacksmith", "miner" };
	private DataGenerator dataGenerator;

	public ModSkillsProvider(DataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	private void addSkills() {
		addSkill("void", "void", 24);
		setSkillPosition(0, null, -12, 0, "void");
		Arrays.stream(playerClasses).forEach(this::addClassSkills);
	}

	private void addClassSkills(String playerClass) {
		// class skills
		addSkill(playerClass, "class", "class", 24);
		// lesser skills
		addSkillBranch(playerClass, "defensive", "defensive_1", 16, 1, 7);
		addSkillBranch(playerClass, "offensive", "offensive_1", 16, 1, 7);
		addSkillBranch(playerClass, "defensive_crafting", "defensive_crafting_1", 16, 1, 7);
		addSkillBranch(playerClass, "offensive_crafting", "offensive_crafting_1", 16, 1, 7);
		addSkillBranch(playerClass, "crafting", "crafting_1", 16, 1, 3);
		addSkillBranch(playerClass, "life", "life_1", 16, 1, 2);
		addSkillBranch(playerClass, "speed", "speed_1", 16, 1, 2);
		addSkillBranch(playerClass, "healing", "healing_1", 16, 1, 2);
		addSkillBranch(playerClass, "lesser", "lesser_1", 16, 1, 6);
		addSkillBranch(playerClass, "crit", "crit_1", 16, 1, 4);
		// notable skills
		addSkill(playerClass, "defensive_notable_1", "defensive_notable_1", 20);
		addSkill(playerClass, "offensive_notable_1", "offensive_notable_1", 20);
		addSkill(playerClass, "crafting_notable_1", "crafting_notable_1", 20);
		addSkill(playerClass, "life_notable_1", "life_notable_1", 20);
		addSkill(playerClass, "speed_notable_1", "speed_notable_1", 20);
		addSkill(playerClass, "healing_notable_1", "healing_notable_1", 20);
		// keystone skills
		addSkill(playerClass, "defensive_keystone_1", "defensive_keystone_1", 24);
		addSkill(playerClass, "offensive_keystone_1", "offensive_keystone_1", 24);
		addSkill(playerClass, "defensive_crafting_keystone_1", "defensive_crafting_keystone_1", 24);
		addSkill(playerClass, "offensive_crafting_keystone_1", "offensive_crafting_keystone_1", 24);
		addSkill(playerClass, "mastery", "mastery", 24);
		addSkill(playerClass, "crit_keystone_1", "crit_keystone_1", 24);
	}

	private void shapeSkillTree() {
		Arrays.stream(playerClasses).forEach(this::shapeClassTree);
		connectClassTrees();
	}

	private void shapeClassTree(String playerClass) {
		setSkillPosition(playerClass, null, 100, 0, "class");
		setSkillBranchPosition(playerClass, "class", 10, "defensive", 30, 0, 1, 4);
		setSkillBranchPosition(playerClass, "class", 10, "offensive", -30, 0, 1, 4);
		setSkillPosition(playerClass, "class", 10, 120, "defensive_crafting_1");
		setSkillPosition(playerClass, "class", 10, -120, "offensive_crafting_1");
		setSkillBranchPosition(playerClass, "defensive_crafting_1", 10, "defensive_crafting", 30, 0, 2, 4);
		setSkillBranchPosition(playerClass, "offensive_crafting_1", 10, "offensive_crafting", -30, 0, 2, 4);
		setSkillPosition(playerClass, "defensive_crafting_4", 12, 30, "defensive_notable_1");
		setSkillPosition(playerClass, "offensive_crafting_4", 12, -30, "offensive_notable_1");
		connectSkills(playerClass, "defensive_notable_1", "defensive_4");
		connectSkills(playerClass, "offensive_notable_1", "offensive_4");
		setSkillBranchPosition(playerClass, "class", 12, "crafting", 180, 0, 1, 2);
		setSkillPosition(playerClass, "crafting_2", 14, 150, "crafting_3");
		setSkillPosition(playerClass, "crafting_3", 12, 30, "crafting_notable_1");
		connectSkills(playerClass + "_crafting_3", "void");
		setSkillPosition(playerClass, "defensive_4", 10, -90, "life_1");
		setSkillPosition(playerClass, "offensive_4", 10, 90, "life_2");
		setSkillPosition(playerClass, "life_2", 10, 90, "life_notable_1");
		connectSkills(playerClass, "life_1", "life_notable_1");
		setSkillBranchPosition(playerClass, "defensive_4", 10, "defensive", 0, 0, 5, 7);
		setSkillBranchPosition(playerClass, "offensive_4", 10, "offensive", 0, 0, 5, 7);
		setSkillPosition(playerClass, "defensive_5", 10, -90, "speed_1");
		setSkillPosition(playerClass, "offensive_5", 10, 90, "speed_2");
		setSkillPosition(playerClass, "speed_2", 10, 90, "speed_notable_1");
		connectSkills(playerClass, "speed_1", "speed_notable_1");
		setSkillPosition(playerClass, "defensive_7", 10, 0, "defensive_keystone_1");
		setSkillPosition(playerClass, "offensive_7", 10, 0, "offensive_keystone_1");
		setSkillBranchPosition(playerClass, "defensive_notable_1", 10, "defensive_crafting", 0, 0, 5, 6);
		setSkillBranchPosition(playerClass, "offensive_notable_1", 10, "offensive_crafting", 0, 0, 5, 6);
		setSkillPosition(playerClass, "defensive_crafting_6", 10, 30, "defensive_crafting_7");
		setSkillPosition(playerClass, "offensive_crafting_6", 10, -30, "offensive_crafting_7");
		setSkillPosition(playerClass, "defensive_crafting_7", 10, 30, "defensive_crafting_keystone_1");
		setSkillPosition(playerClass, "offensive_crafting_7", 10, -30, "offensive_crafting_keystone_1");
		setSkillPosition(playerClass, "defensive_crafting_4", 10, 120, "healing_1");
		setSkillPosition(playerClass, "healing_1", 8, -150, "healing_notable_1");
		setSkillPosition(playerClass, "healing_notable_1", 8, -150, "healing_2");
		setSkillPosition(playerClass, "defensive_6", 10, -90, "lesser_1");
		setSkillPosition(playerClass, "offensive_6", 10, 90, "lesser_2");
		setSkillPosition(playerClass, "lesser_1", 10, 0, "lesser_3");
		setSkillPosition(playerClass, "lesser_2", 10, 0, "lesser_4");
		setSkillPosition(playerClass, "lesser_4", 12, 90, "lesser_5");
		connectSkills(playerClass, "lesser_5", "lesser_3");
		setSkillPosition(playerClass, "lesser_5", 10, 0, "lesser_6");
		setSkillPosition(playerClass, "lesser_6", 10, 0, "mastery");
		setSkillBranchPosition(playerClass, "offensive_crafting_5", 10, "crit", -120, 0, 1, 2);
		setSkillPosition(playerClass, "crit_2", 10, 0, "crit_3");
		connectSkills(playerClass, "crit_3", "crit_1");
		setSkillPosition(playerClass, "crit_3", 10, -30, "crit_4");
		setSkillPosition(playerClass, "crit_4", 10, -30, "crit_keystone_1");
	}

	protected void connectClassTrees() {
		connectSkillsBetweenClasses("healing_2", "offensive_crafting_2");
		connectSkillsBetweenClasses("crafting_3", "crafting_2");
		connectSkillsBetweenClasses("defensive_crafting_5", "crit_2");
	}

	private void setSkillsAttributeModifiers() {
		// alchemist skills
		addSkillAttributeModifier("alchemist_class", BREWED_POTIONS_DURATION.get(), 0.4, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("alchemist_defensive_crafting", CHANCE_TO_BREW_STRONGER_BENEFICIAL_POTION.get(), 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("alchemist_offensive_crafting", CHANCE_TO_BREW_STRONGER_HARMFUL_POTION.get(), 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("alchemist_defensive", EVASION_CHANCE.get(), 0.01, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("alchemist_offensive", DAMAGE_AGAINST_POISONED.get(), 0.1, MULTIPLY_BASE, 1, 7);
		addSkillAttributeModifier("alchemist_defensive_notable_1", EVASION_UNDER_POTION_EFFECT.get(), 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_offensive_notable_1", DAMAGE_AGAINST_POISONED.get(), 0.4, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("alchemist_life", MAXIMUM_LIFE_UNDER_POTION_EFFECT.get(), 2, ADDITION, 1, 2);
		addSkillAttributeModifier("alchemist_life_notable_1", MAXIMUM_LIFE_UNDER_POTION_EFFECT.get(), 6, ADDITION);
		addSkillBranchAttributeModifier("alchemist_speed", ATTACK_SPEED_UNDER_POTION_EFFECT.get(), 0.05, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("alchemist_speed_notable_1", ATTACK_SPEED_UNDER_POTION_EFFECT.get(), 0.15, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("alchemist_lesser", BREWED_POTIONS_DURATION.get(), 0.05, MULTIPLY_BASE, 1, 6);
		addSkillAttributeModifier("alchemist_mastery", CHANCE_TO_BREW_STRONGER_POTION.get(), 1, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("alchemist_crit", CRIT_CHANCE_AGAINST_POISONED.get(), 0.05, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("alchemist_crit_keystone_1", CRIT_DAMAGE_AGAINST_POISONED.get(), 0.2, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("alchemist_crafting", CHANCE_TO_BREW_STRONGER_POTION.get(), 0.05, MULTIPLY_BASE, 1, 3);
		addSkillAttributeModifier("alchemist_defensive_crafting_keystone_1", CHANCE_TO_BREW_STRONGER_BENEFICIAL_POTION.get(), 1.3, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_offensive_crafting_keystone_1", CHANCE_TO_BREW_STRONGER_HARMFUL_POTION.get(), 0.3, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_offensive_crafting_keystone_1", CAN_POISON_WEAPONS.get(), 1, ADDITION);
		addSkillBranchAttributeModifier("alchemist_healing", LIFE_PER_HIT.get(), 0.5, ADDITION, 1, 2);
		addSkillAttributeModifier("alchemist_healing_notable_1", LIFE_PER_HIT_UNDER_POTION_EFFECT.get(), 1, ADDITION);
		addSkillAttributeModifier("alchemist_crafting_notable_1", CAN_MIX_POTIONS.get(), 1, ADDITION);
		addSkillAttributeModifier("alchemist_defensive_keystone_1", EVASION_PER_POTION_EFFECT.get(), 0.02, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_offensive_keystone_1", DAMAGE_PER_POTION_EFFECT.get(), 0.1, MULTIPLY_BASE);
		// hunter skills
		addSkillAttributeModifier("hunter_class", DOUBLE_LOOT_CHANCE.get(), 0.15, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("hunter_defensive_crafting", CRAFTED_ARMOR_EVASION.get(), 0.01, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("hunter_offensive_crafting", CRAFTED_BOWS_ATTACK_SPEED.get(), 0.04, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("hunter_defensive", EVASION_CHANCE.get(), 0.01, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("hunter_offensive", ARROW_DAMAGE_BONUS.get(), 1, ADDITION, 1, 7);
		addSkillAttributeModifier("hunter_defensive_notable_1", EVASION_CHANCE_WHEN_WOUNDED.get(), 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_offensive_notable_1", ARROW_DAMAGE.get(), 0.2, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("hunter_life", MAXIMUM_LIFE_PER_EVASION.get(), 0.05, ADDITION, 1, 2);
		addSkillAttributeModifier("hunter_life_notable_1", MAXIMUM_LIFE_PER_EVASION.get(), 0.1, ADDITION);
		addSkillBranchAttributeModifier("hunter_speed", ATTACK_SPEED_WITH_BOW.get(), 0.05, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("hunter_speed_notable_1", ATTACK_SPEED_WITH_BOW.get(), 0.10, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("hunter_lesser", DOUBLE_LOOT_CHANCE.get(), 0.05, MULTIPLY_BASE, 1, 6);
		addSkillAttributeModifier("hunter_mastery", TRIPLE_LOOT_CHANCE.get(), 0.05, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("hunter_crit", CRIT_CHANCE_WITH_BOW.get(), 0.05, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("hunter_crit_keystone_1", ARROW_CRIT_DAMAGE.get(), 0.2, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("hunter_crafting", CHANCE_TO_RETRIEVE_ARROWS.get(), 0.05, MULTIPLY_BASE, 1, 3);
		addSkillAttributeModifier("hunter_defensive_crafting_keystone_1", CRAFTED_HELMETS_ADDITIONAL_GEMSTONE_SLOTS.get(), 1, ADDITION);
		addSkillAttributeModifier("hunter_offensive_crafting_keystone_1", CRAFTED_BOWS_ADDITIONAL_GEMSTONE_SLOTS.get(), 1, ADDITION);
		addSkillBranchAttributeModifier("hunter_healing", LIFE_PER_HIT.get(), 0.5, ADDITION, 1, 2);
		addSkillAttributeModifier("hunter_healing_notable_1", LIFE_PER_ARROW_HIT.get(), 1, ADDITION);
		addSkillAttributeModifier("hunter_crafting_notable_1", CHANCE_TO_RETRIEVE_ARROWS.get(), 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_defensive_keystone_1", ARMOR_PER_EVASION.get(), 0.25, ADDITION);
		addSkillAttributeModifier("hunter_offensive_keystone_1", ARROW_DAMAGE_PER_DISTANCE.get(), 0.05, MULTIPLY_BASE);
		// miner skills
		addSkillAttributeModifier("miner_class", MINING_SPEED.get(), 0.15, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("miner_defensive_crafting", GEMSTONES_STRENGTH_IN_ARMOR.get(), 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("miner_offensive_crafting", GEMSTONES_STRENGTH_IN_WEAPON.get(), 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("miner_defensive", ARMOR, 1, ADDITION, 1, 7);
		addSkillBranchAttributeModifier("miner_offensive", PICKAXE_DAMAGE_BONUS.get(), 1, ADDITION, 1, 7);
		addSkillAttributeModifier("miner_defensive_notable_1", ARMOR_PER_GEMSTONE_IN_HELMET.get(), 2, ADDITION);
		addSkillAttributeModifier("miner_offensive_notable_1", PICKAXE_DAMAGE.get(), 0.25, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("miner_life", MAXIMUM_LIFE_PER_GEMSTONE_IN_HELMET.get(), 1, ADDITION, 1, 2);
		addSkillAttributeModifier("miner_life_notable_1", MAXIMUM_LIFE_PER_GEMSTONE_IN_ARMOR.get(), 1, ADDITION);
		addSkillBranchAttributeModifier("miner_speed", ATTACK_SPEED_WITH_PICKAXE.get(), 0.05, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("miner_speed_notable_1", ATTACK_SPEED_WITH_PICKAXE.get(), 0.15, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("miner_lesser", MINING_SPEED.get(), 0.05, MULTIPLY_BASE, 1, 6);
		addSkillAttributeModifier("miner_mastery", MAXIMUM_GEMSTONE_SLOTS_BONUS.get(), 1, ADDITION);
		addSkillBranchAttributeModifier("miner_crit", CRIT_CHANCE_PER_GEMSTONE_IN_WEAPON.get(), 0.02, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("miner_crit_keystone_1", CRIT_DAMAGE_PER_GEMSTONE_IN_WEAPON.get(), 0.1, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("miner_crafting", CHANCE_TO_FIND_GEMSTONE.get(), 0.01, MULTIPLY_BASE, 1, 3);
		addSkillAttributeModifier("miner_defensive_crafting_keystone_1", GEMSTONES_STRENGTH_IN_ARMOR.get(), 0.3, MULTIPLY_BASE);
		addSkillAttributeModifier("miner_defensive_crafting_keystone_1", MAXIMUM_CHESTPLATE_GEMSTONE_SLOTS_BONUS.get(), 1, ADDITION);
		addSkillAttributeModifier("miner_offensive_crafting_keystone_1", GEMSTONES_STRENGTH_IN_WEAPON.get(), 0.3, MULTIPLY_BASE);
		addSkillAttributeModifier("miner_offensive_crafting_keystone_1", MAXIMUM_WEAPON_GEMSTONE_SLOTS_BONUS.get(), 1, ADDITION);
		addSkillBranchAttributeModifier("miner_healing", LIFE_REGENERATION.get(), 0.5, ADDITION, 1, 2);
		addSkillAttributeModifier("miner_healing_notable_1", LIFE_REGENERATION_PER_GEMSTONE_IN_HELMET.get(), 0.5, ADDITION);
		addSkillAttributeModifier("miner_crafting_notable_1", CHANCE_TO_FIND_GEMSTONE.get(), 0.02, MULTIPLY_BASE);
		addSkillAttributeModifier("miner_defensive_keystone_1", ARMOR_PER_GEMSTONE_IN_CHESTPLATE.get(), 5, ADDITION);
		addSkillAttributeModifier("miner_offensive_keystone_1", PICKAXE_DAMAGE.get(), 0.75, MULTIPLY_BASE);
		// blacksmith skills
		addSkillAttributeModifier("blacksmith_class", CRAFTED_ARMOR_DEFENCE.get(), 0.4, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_defensive_crafting", CRAFTED_ARMOR_DEFENCE.get(), 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("blacksmith_offensive_crafting", CRAFTED_WEAPON_DAMAGE_BONUS.get(), 1, ADDITION, 1, 7);
		addSkillBranchAttributeModifier("blacksmith_defensive", ARMOR, 1, ADDITION, 1, 7);
		addSkillBranchAttributeModifier("blacksmith_offensive", DAMAGE_WITH_SHIELD.get(), 0.1, MULTIPLY_BASE, 1, 7);
		addSkillAttributeModifier("blacksmith_defensive_notable_1", ARMOR, 10, ADDITION);
		addSkillAttributeModifier("blacksmith_offensive_notable_1", DAMAGE_WITH_SHIELD.get(), 0.25, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_life", MAXIMUM_LIFE_PER_BOOTS_ARMOR.get(), 0.5, ADDITION, 1, 2);
		addSkillAttributeModifier("blacksmith_life_notable_1", MAXIMUM_LIFE_PER_BOOTS_ARMOR.get(), 1, ADDITION);
		addSkillBranchAttributeModifier("blacksmith_speed", ATTACK_SPEED_WITH_SHIELD.get(), 0.05, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("blacksmith_speed_notable_1", ATTACK_SPEED_WITH_SHIELD.get(), 0.10, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_lesser", CHANCE_TO_CRAFT_TOUGHER_ARMOR.get(), 0.1, MULTIPLY_BASE, 1, 6);
		addSkillAttributeModifier("blacksmith_mastery", CHANCE_TO_CRAFT_TOUGHER_ARMOR.get(), 1, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_crit", CRIT_CHANCE_WITH_SHIELD.get(), 0.05, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("blacksmith_crit_keystone_1", CRIT_DAMAGE_WITH_SHIELD.get(), 0.25, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_crafting", CRAFTED_SHIELDS_ARMOR_BONUS.get(), 1, ADDITION, 1, 3);
		addSkillAttributeModifier("blacksmith_defensive_crafting_keystone_1", CRAFTED_ARMOR_DEFENCE.get(), 0.4, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_offensive_crafting_keystone_1", CRAFTED_WEAPON_ATTACK_SPEED.get(), 0.25, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_healing", LIFE_REGENERATION.get(), 0.5, ADDITION, 1, 2);
		addSkillAttributeModifier("blacksmith_healing_notable_1", LIFE_REGENERATION_WITH_SHIELD.get(), 1, ADDITION);
		addSkillAttributeModifier("blacksmith_crafting_notable_1", CRAFTED_SHIELDS_ARMOR_BONUS.get(), 2, ADDITION);
		addSkillAttributeModifier("blacksmith_defensive_keystone_1", CHESTPLATE_ARMOR.get(), 1, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_offensive_keystone_1", ATTACK_DAMAGE_PER_ARMOR.get(), 0.15, ADDITION);
		// enchanter skills
		addSkillAttributeModifier("enchanter_class", ENCHANTMENT_LEVEL_REQUIREMENT_REDUCTION.get(), 0.3, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_defensive_crafting", CHANCE_TO_APPLY_BETTER_ARMOR_ENCHANTMENT.get(), 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("enchanter_offensive_crafting", CHANCE_TO_APPLY_BETTER_WEAPON_ENCHANTMENT.get(), 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("enchanter_defensive", BLOCK_CHANCE.get(), 0.01, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("enchanter_offensive", DAMAGE_WITH_ENCHANTED_WEAPON.get(), 0.1, MULTIPLY_BASE, 1, 7);
		addSkillAttributeModifier("enchanter_defensive_notable_1", BLOCK_CHANCE_WITH_ENCHANTED_SHIELD.get(), 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_offensive_notable_1", DAMAGE_WITH_ENCHANTED_WEAPON.get(), 0.20, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_life", MAXIMUM_LIFE_WITH_ENCHANTED_ITEM.get(), 2, ADDITION, 1, 2);
		addSkillAttributeModifier("enchanter_life_notable_1", MAXIMUM_LIFE_PER_ARMOR_ENCHANTMENT.get(), 1, ADDITION);
		addSkillBranchAttributeModifier("enchanter_speed", ATTACK_SPEED_WITH_ENCHANTED_WEAPON.get(), 0.05, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("enchanter_speed_notable_1", ATTACK_SPEED_WITH_ENCHANTED_WEAPON.get(), 0.10, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_lesser", ENCHANTMENT_LEVEL_REQUIREMENT_REDUCTION.get(), 0.05, MULTIPLY_BASE, 1, 6);
		addSkillAttributeModifier("enchanter_mastery", CHANCE_TO_APPLY_BETTER_ENCHANTMENT.get(), 1, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_crit", CRIT_CHANCE_WITH_ENCHANTED_WEAPON.get(), 0.05, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("enchanter_crit_keystone_1", CRIT_DAMAGE_PER_WEAPON_ENCHANTMENT.get(), 0.05, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_crafting", FREE_ENCHANTMENT_CHANCE.get(), 0.05, MULTIPLY_BASE, 1, 3);
		addSkillAttributeModifier("enchanter_defensive_crafting_keystone_1", CHANCE_TO_APPLY_BETTER_ARMOR_ENCHANTMENT.get(), 0.4, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_offensive_crafting_keystone_1", CHANCE_TO_APPLY_BETTER_WEAPON_ENCHANTMENT.get(), 0.4, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_healing", LIFE_ON_BLOCK.get(), 0.5, ADDITION, 1, 2);
		addSkillAttributeModifier("enchanter_healing_notable_1", LIFE_ON_BLOCK_PER_SHIELD_ENCHANTMENT.get(), 0.5, ADDITION);
		addSkillAttributeModifier("enchanter_crafting_notable_1", FREE_ENCHANTMENT_CHANCE.get(), 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_defensive_keystone_1", BLOCK_CHANCE_PER_SHIELD_ENCHANTMENT.get(), 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_offensive_keystone_1", DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL.get(), 0.05, MULTIPLY_BASE);
		// cook skills
		addSkillAttributeModifier("cook_class", COOKED_FOOD_SATURATION.get(), 0.2, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_defensive_crafting", COOKED_FOOD_HEALING_PER_SATURATION.get(), 0.05, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("cook_offensive_crafting", COOKED_FOOD_DAMAGE_PER_SATURATION.get(), 0.05, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("cook_defensive", BLOCK_CHANCE.get(), 0.01, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("cook_offensive", DAMAGE_IF_NOT_HUNGRY.get(), 0.1, MULTIPLY_BASE, 1, 7);
		addSkillAttributeModifier("cook_defensive_notable_1", BLOCK_CHANCE_IF_NOT_HUNGRY.get(), 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("cook_offensive_notable_1", DAMAGE_IF_NOT_HUNGRY.get(), 0.25, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_life", MAXIMUM_LIFE_IF_NOT_HUNGRY.get(), 2, ADDITION, 1, 2);
		addSkillAttributeModifier("cook_life_notable_1", MAXIMUM_LIFE_PER_SATISFIED_HUNGER.get(), 0.5, ADDITION);
		addSkillBranchAttributeModifier("cook_speed", ATTACK_SPEED_IF_NOT_HUNGRY.get(), 0.05, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("cook_speed_notable_1", ATTACK_SPEED_IF_NOT_HUNGRY.get(), 0.15, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_lesser", COOKED_FOOD_SATURATION.get(), 0.05, MULTIPLY_BASE, 1, 6);
		addSkillAttributeModifier("cook_mastery", COOKED_FOOD_SATURATION.get(), 0.5, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_crit", CRIT_CHANCE_IF_NOT_HUNGRY.get(), 0.05, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("cook_crit_keystone_1", CRIT_DAMAGE_PER_SATISFIED_HUNGER.get(), 0.025, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_crafting", COOKED_FOOD_LIFE_REGENERATION.get(), 0.5, ADDITION, 1, 3);
		addSkillAttributeModifier("cook_defensive_crafting_keystone_1", COOKED_FOOD_HEALING_PER_SATURATION.get(), 0.25, MULTIPLY_BASE);
		addSkillAttributeModifier("cook_offensive_crafting_keystone_1", COOKED_FOOD_CRITICAL_DAMAGE_PER_SATURATION.get(), 0.025, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_healing", LIFE_ON_BLOCK.get(), 0.5, ADDITION, 1, 2);
		addSkillAttributeModifier("cook_healing_notable_1", LIFE_ON_BLOCK_IF_NOT_HUNGRY.get(), 1, ADDITION);
		addSkillAttributeModifier("cook_crafting_notable_1", COOKED_FOOD_LIFE_REGENERATION.get(), 1.5, ADDITION);
		addSkillAttributeModifier("cook_defensive_keystone_1", BLOCK_CHANCE_PER_SATISFIED_HUNGER.get(), 0.01, MULTIPLY_BASE);
		addSkillAttributeModifier("cook_offensive_keystone_1", DAMAGE_PER_SATISFIED_HUNGER.get(), 0.025, MULTIPLY_BASE);
	}

	private void addSkillBranchAttributeModifier(String branchName, Attribute attribute, double amount, Operation operation, int from, int to) {
		for (var node = from; node <= to; node++) {
			addSkillAttributeModifier(branchName + "_" + node, attribute, amount, operation);
		}
	}

	private void addSkillAttributeModifier(String skillName, Attribute attribute, double amount, Operation operation) {
		getSkill(skillName).addAttributeBonus(attribute, new AttributeModifier(UUID.randomUUID(), "Passive Skill Bonus", amount, operation));
	}

	public void addSkillBranch(String playerClass, String branchName, String iconName, int nodeSize, int from, int to) {
		for (var node = from; node <= to; node++) {
			addSkill(playerClass, branchName + "_" + node, iconName, nodeSize);
		}
	}

	private void connectSkillsBetweenClasses(String from, String to) {
		for (var classId = 0; classId < playerClasses.length - 1; classId++) {
			connectSkills(playerClasses[classId] + "_" + from, playerClasses[classId + 1] + "_" + to);
		}

		connectSkills(playerClasses[5] + "_" + from, playerClasses[0] + "_" + to);
	}

	private void setSkillBranchPosition(String playerClass, String initialNodeName, int distanceBetweenNodes, String branchName, float initialRotation, float rotationPerNode, int from, int to) {
		var branchNode = initialNodeName;
		for (var node = from; node <= to; node++) {
			setSkillPosition(playerClass, branchNode, distanceBetweenNodes, initialRotation + (node - from) * rotationPerNode, branchName + "_" + node);
			branchNode = branchName + "_" + node;
		}
	}

	private void setSkillPosition(String playerClass, @Nullable String previousSkillName, float distance, float angle, String skillName) {
		setSkillPosition(getClassId(playerClass), playerClass + "_" + previousSkillName, distance, angle, playerClass + "_" + skillName);
	}

	private void setSkillPosition(int classId, @Nullable String previousSkillName, float distance, float angle, String skillName) {
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

	protected int getClassId(String playerClass) {
		return Arrays.asList(playerClasses).indexOf(playerClass);
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

	private void connectSkills(String playerClass, String skillName1, String skillName2) {
		getSkill(playerClass + "_" + skillName1).connect(getSkill(playerClass + "_" + skillName2));
	}

	private ResourceLocation getSkillId(String skillName) {
		return new ResourceLocation(SkillTreeMod.MOD_ID, skillName);
	}

	private void addSkill(String playerClass, String skillName, String iconName, int buttonSize) {
		addSkill(playerClass + "_" + skillName, playerClass + "_" + iconName, buttonSize);
	}

	private void addSkill(String skillName, String iconName, int buttonSize) {
		var skillId = new ResourceLocation(SkillTreeMod.MOD_ID, skillName);
		var treeId = new ResourceLocation(SkillTreeMod.MOD_ID, "tree");
		var backgroundName = buttonSize == 24 ? "keystone" : buttonSize == 20 ? "notable" : "lesser";
		var backgroundTexture = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/background/" + backgroundName + ".png");
		var iconTexture = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/" + iconName + ".png");
		data.put(skillId, new PassiveSkill(skillId, treeId, buttonSize, backgroundTexture, iconTexture, skillName.endsWith("class")));
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
