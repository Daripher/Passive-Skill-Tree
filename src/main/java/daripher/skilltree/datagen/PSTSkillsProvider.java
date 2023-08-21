package daripher.skilltree.datagen;

import static daripher.skilltree.init.PSTAttributes.*;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_BASE;
import static net.minecraft.world.entity.ai.attributes.Attributes.ARMOR;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED;
import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;
import static net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.util.JsonHelper;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.common.CuriosHelper;

public class PSTSkillsProvider implements DataProvider {
	private Map<ResourceLocation, PassiveSkill> data = new HashMap<>();
	private DataGenerator dataGenerator;
	private String[] playerClasses = new String[] { "alchemist", "hunter", "enchanter", "cook", "blacksmith", "miner" };

	public PSTSkillsProvider(DataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	private void addSkills() {
		Arrays.stream(playerClasses).forEach(this::addClassSkills);
		addGateways();
	}

	private void addClassSkills(String playerClass) {
		// class skills
		addSkill(playerClass, "class", "class", 24);
		// lesser skills
		addSkillBranch(playerClass, "defensive", "defensive_1", 16, 1, 8);
		addSkillBranch(playerClass, "offensive", "offensive_1", 16, 1, 8);
		addSkillBranch(playerClass, "defensive_crafting", "defensive_crafting_1", 16, 1, 7);
		addSkillBranch(playerClass, "offensive_crafting", "offensive_crafting_1", 16, 1, 7);
		addSkillBranch(playerClass, "crafting", "crafting_1", 16, 1, 3);
		addSkillBranch(playerClass, "life", "life_1", 16, 1, 2);
		addSkillBranch(playerClass, "speed", "speed_1", 16, 1, 2);
		addSkillBranch(playerClass, "healing", "healing_1", 16, 1, 4);
		addSkillBranch(playerClass, "lesser", "lesser_1", 16, 1, 6);
		addSkillBranch(playerClass, "crit", "crit_1", 16, 1, 2);
		addSkillBranch(playerClass, "subclass_1_defensive", "subclass_1_defensive_1", 16, 1, 4);
		addSkillBranch(playerClass, "subclass_2_defensive", "subclass_2_defensive_1", 16, 1, 4);
		addSkillBranch(playerClass, "subclass_1_crafting", "subclass_1_crafting_1", 16, 1, 5);
		addSkillBranch(playerClass, "subclass_1_offensive", "subclass_1_offensive_1", 16, 1, 4);
		addSkillBranch(playerClass, "subclass_2_crafting", "subclass_2_crafting_1", 16, 1, 5);
		addSkillBranch(playerClass, "subclass_2_life", "subclass_2_life_1", 16, 1, 4);
		// notable skills
		addSkill(playerClass, "defensive_notable_1", "defensive_notable_1", 20);
		addSkill(playerClass, "offensive_notable_1", "offensive_notable_1", 20);
		addSkill(playerClass, "crafting_notable_1", "crafting_notable_1", 20);
		addSkill(playerClass, "life_notable_1", "life_notable_1", 20);
		addSkill(playerClass, "speed_notable_1", "speed_notable_1", 20);
		addSkill(playerClass, "healing_notable_1", "healing_notable_1", 20);
		addSkill(playerClass, "crit_notable_1", "crit_notable_1", 20);
		addSkill(playerClass, "subclass_special", "subclass_special_1", 20);
		addSkill(playerClass, "subclass_1_offensive_notable_1", "subclass_1_offensive_notable_1", 20);
		addSkill(playerClass, "subclass_1_crafting_notable_1", "subclass_1_crafting_notable_1", 20);
		addSkill(playerClass, "subclass_2_life_notable_1", "subclass_2_life_notable_1", 20);
		addSkill(playerClass, "subclass_2_crafting_notable_1", "subclass_2_crafting_notable_1", 20);
		// keystone skills
		addSkill(playerClass, "defensive_keystone_1", "defensive_keystone_1", 24);
		addSkill(playerClass, "offensive_keystone_1", "offensive_keystone_1", 24);
		addSkill(playerClass, "defensive_crafting_keystone_1", "defensive_crafting_keystone_1", 24);
		addSkill(playerClass, "offensive_crafting_keystone_1", "offensive_crafting_keystone_1", 24);
		addSkill(playerClass, "mastery", "mastery", 24);
		addSkill(playerClass, "subclass_1", "subclass_1", 24);
		addSkill(playerClass, "subclass_2", "subclass_2", 24);
		addSkill(playerClass, "subclass_1_mastery", "subclass_1_mastery", 24);
		addSkill(playerClass, "subclass_2_mastery", "subclass_2_mastery", 24);
	}

	protected void addGateways() {
		addGateway("alchemist", "etherial_gateway");
		addGateway("cook", "etherial_gateway");
		addGateway("hunter", "dimensional_gateway");
		addGateway("blacksmith", "dimensional_gateway");
		addGateway("enchanter", "astral_gateway");
		addGateway("miner", "astral_gateway");
	}

	private void shapeSkillTree() {
		Arrays.stream(playerClasses).forEach(this::shapeClassTree);
		connectClassTrees();
	}

	private void shapeClassTree(String playerClass) {
		setSkillPosition(playerClass, null, 140, 0, "class");
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
		setSkillBranchPosition(playerClass, "class", 11, "crafting", 180, 0, 1, 3);
		setSkillPosition(playerClass, "crafting_3", 12, 180, "crafting_notable_1");
		setSkillPosition(playerClass, "defensive_4", 10, -90, "life_1");
		setSkillPosition(playerClass, "offensive_4", 10, 90, "life_2");
		setSkillPosition(playerClass, "life_2", 10, 90, "life_notable_1");
		connectSkills(playerClass, "life_1", "life_notable_1");
		setSkillBranchPosition(playerClass, "defensive_4", 10, "defensive", 0, 0, 5, 8);
		setSkillBranchPosition(playerClass, "offensive_4", 10, "offensive", 0, 0, 5, 8);
		setSkillPosition(playerClass, "defensive_5", 10, -90, "speed_1");
		setSkillPosition(playerClass, "offensive_5", 10, 90, "speed_2");
		setSkillPosition(playerClass, "speed_2", 10, 90, "speed_notable_1");
		connectSkills(playerClass, "speed_1", "speed_notable_1");
		setSkillPosition(playerClass, "defensive_8", 10, 0, "defensive_keystone_1");
		setSkillPosition(playerClass, "offensive_8", 10, 0, "offensive_keystone_1");
		setSkillPosition(playerClass, "defensive_notable_1", 8, 30, "defensive_crafting_5");
		setSkillPosition(playerClass, "offensive_notable_1", 8, -30, "offensive_crafting_5");
		setSkillBranchPosition(playerClass, "defensive_crafting_5", 10, "defensive_crafting", 0, 0, 6, 7);
		setSkillBranchPosition(playerClass, "offensive_crafting_5", 10, "offensive_crafting", 0, 0, 6, 7);
		setSkillPosition(playerClass, "defensive_crafting_7", 10, 0, "defensive_crafting_keystone_1");
		setSkillPosition(playerClass, "offensive_crafting_7", 10, 0, "offensive_crafting_keystone_1");
		setSkillPosition(playerClass, "defensive_crafting_3", 12, 120, "healing_1");
		setSkillPosition(playerClass, "healing_1", 10, 165, "healing_3");
		setSkillPosition(playerClass, "healing_3", 10, 75, "healing_2");
		setSkillPosition(playerClass, "healing_3", 10, 210, "healing_4");
		setSkillPosition(playerClass, "healing_4", 12, 210, "healing_notable_1");
		setSkillPosition(playerClass, "defensive_7", 10, -90, "lesser_1");
		setSkillPosition(playerClass, "offensive_7", 10, 90, "lesser_2");
		setSkillPosition(playerClass, "lesser_1", 10, 0, "lesser_3");
		setSkillPosition(playerClass, "lesser_2", 10, 0, "lesser_4");
		setSkillPosition(playerClass, "lesser_4", 12, 90, "lesser_5");
		connectSkills(playerClass, "lesser_5", "lesser_3");
		setSkillPosition(playerClass, "lesser_5", 10, 0, "lesser_6");
		setSkillPosition(playerClass, "lesser_6", 10, 0, "mastery");
		setSkillPosition(playerClass, "defensive_6", 10, -90, "crit_1");
		setSkillPosition(playerClass, "offensive_6", 10, 90, "crit_2");
		setSkillPosition(playerClass, "crit_2", 10, 90, "crit_notable_1");
		connectSkills(playerClass, "crit_1", "crit_notable_1");
		setSkillPosition(playerClass, "offensive_notable_1", 12, -120, "subclass_1_defensive_1");
		setSkillPosition(playerClass, "defensive_notable_1", 12, 120, "subclass_2_defensive_1");
		setSkillBranchPosition(playerClass, "subclass_1_defensive_1", 10, "subclass_1_defensive", -30, 0, 2, 4);
		setSkillBranchPosition(playerClass, "subclass_2_defensive_1", 10, "subclass_2_defensive", 30, 0, 2, 4);
		setSkillPosition(playerClass, "subclass_1_defensive_4", 12, 0, "subclass_1");
		setSkillPosition(playerClass, "subclass_2_defensive_4", 12, 0, "subclass_2");
		setSkillPosition(playerClass, "subclass_2_defensive_1", 8, 180, "gateway");
		setSkillPosition(playerClass, "subclass_1", 12, 30, "subclass_1_offensive_1");
		setSkillPosition(playerClass, "subclass_2", 12, 30, "subclass_2_crafting_1");
		setSkillBranchPosition(playerClass, "subclass_1_offensive_1", 10, "subclass_1_offensive", 0, 0, 2, 4);
		setSkillBranchPosition(playerClass, "subclass_2_crafting_1", 10, "subclass_2_crafting", 0, 0, 2, 4);
		setSkillPosition(playerClass, "subclass_1", 12, -30, "subclass_1_crafting_1");
		setSkillPosition(playerClass, "subclass_2", 12, -30, "subclass_2_life_1");
		setSkillBranchPosition(playerClass, "subclass_1_crafting_1", 10, "subclass_1_crafting", 0, 0, 2, 4);
		setSkillBranchPosition(playerClass, "subclass_2_life_1", 10, "subclass_2_life", 0, 0, 2, 4);
		setSkillPosition(playerClass, "subclass_1_crafting_2", 10, -60, "subclass_1_crafting_5");
		setSkillPosition(playerClass, "subclass_2_crafting_2", 10, 60, "subclass_2_crafting_5");
		setSkillPosition(playerClass, "subclass_1_crafting_5", 14, -120, "subclass_special");
		setSkillPosition(playerClass, "subclass_1_crafting_4", 12, 30, "subclass_1_mastery");
		connectSkills(playerClass, "subclass_1_mastery", "subclass_1_offensive_4");
		setSkillPosition(playerClass, "subclass_2_crafting_4", 12, -30, "subclass_2_mastery");
		connectSkills(playerClass, "subclass_2_mastery", "subclass_2_life_4");
		setSkillPosition(playerClass, "subclass_1_crafting_4", 12, -60, "subclass_1_crafting_notable_1");
		setSkillPosition(playerClass, "subclass_1_offensive_4", 12, 60, "subclass_1_offensive_notable_1");
		setSkillPosition(playerClass, "subclass_2_life_4", 12, -60, "subclass_2_life_notable_1");
		setSkillPosition(playerClass, "subclass_2_crafting_4", 12, 60, "subclass_2_crafting_notable_1");
		connectSkills(playerClass, "subclass_2_crafting_3", "subclass_2_crafting_5");
		connectSkills(playerClass, "subclass_1_crafting_3", "subclass_1_crafting_5");
	}

	protected void connectClassTrees() {
		connectSkillsBetweenClasses("healing_2", "offensive_crafting_3");
		connectSkillsBetweenClasses("subclass_2_defensive_1", "subclass_1_defensive_1");
		connectSkillsBetweenClasses("gateway", "subclass_1_defensive_1");
		connectSkillsBetweenClasses("subclass_2_crafting_5", "subclass_special");
	}

	private void setSkillsAttributeModifiers() {
		// alchemist skills
		addSkillAttributeModifier("alchemist_class", BREWED_POTIONS_DURATION, 0.4, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("alchemist_defensive_crafting", BREWED_BENEFICIAL_POTIONS_STRENGTH, 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("alchemist_offensive_crafting", BREWED_HARMFUL_POTIONS_STRENGTH, 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("alchemist_defensive", EVASION, 0.01, MULTIPLY_BASE, 1, 8);
		addSkillBranchAttributeModifier("alchemist_offensive", DAMAGE_AGAINST_POISONED, 0.05, MULTIPLY_BASE, 1, 8);
		addSkillAttributeModifier("alchemist_defensive_notable_1", EVASION_UNDER_POTION_EFFECT, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_offensive_notable_1", DAMAGE_AGAINST_POISONED, 0.25, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("alchemist_life", MAXIMUM_LIFE_UNDER_POTION_EFFECT, 2, ADDITION, 1, 2);
		addSkillAttributeModifier("alchemist_life_notable_1", MAXIMUM_LIFE_UNDER_POTION_EFFECT, 6, ADDITION);
		addSkillBranchAttributeModifier("alchemist_speed", ATTACK_SPEED_UNDER_POTION_EFFECT, 0.05, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("alchemist_speed_notable_1", ATTACK_SPEED_UNDER_POTION_EFFECT, 0.1, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("alchemist_lesser", BREWED_POTIONS_DURATION, 0.05, MULTIPLY_BASE, 1, 6);
		addSkillAttributeModifier("alchemist_mastery", BREWED_POTIONS_STRENGTH, 1, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("alchemist_crit", CRIT_CHANCE_AGAINST_POISONED, 0.02, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("alchemist_crit_notable_1", CRIT_DAMAGE_AGAINST_POISONED, 0.35, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("alchemist_crafting", BREWED_POTIONS_STRENGTH, 0.05, MULTIPLY_BASE, 1, 3);
		addSkillAttributeModifier("alchemist_defensive_crafting_keystone_1", BREWED_BENEFICIAL_POTIONS_STRENGTH, 1, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_offensive_crafting_keystone_1", BREWED_HARMFUL_POTIONS_STRENGTH, 0.3, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_offensive_crafting_keystone_1", CAN_POISON_WEAPONS, 1, ADDITION);
		addSkillBranchAttributeModifier("alchemist_healing", LIFE_PER_HIT, 0.25, ADDITION, 1, 4);
		addSkillAttributeModifier("alchemist_healing_notable_1", LIFE_PER_HIT_UNDER_POTION_EFFECT, 0.5, ADDITION);
		addSkillAttributeModifier("alchemist_crafting_notable_1", CAN_MIX_POTIONS, 1, ADDITION);
		addSkillAttributeModifier("alchemist_defensive_keystone_1", EVASION_PER_POTION_EFFECT, 0.02, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_offensive_keystone_1", DAMAGE_PER_POTION_EFFECT, 0.15, MULTIPLY_BASE);
		// assassin skills
		addSkillBranchAttributeModifier("alchemist_subclass_1_defensive", EVASION, 0.005, MULTIPLY_BASE, 1, 4);
		addSkillBranchAttributeModifier("alchemist_subclass_1_defensive", ARMOR, 0.5, ADDITION, 1, 4);
		addSkillAttributeModifier("alchemist_subclass_1", CRIT_CHANCE, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_subclass_1", CRIT_DAMAGE, 0.15, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("alchemist_subclass_1_offensive", CRIT_CHANCE, 0.02, MULTIPLY_BASE, 1, 4);
		addSkillBranchAttributeModifier("alchemist_subclass_1_crafting", BREWED_POISONS_STRENGTH, 0.1, MULTIPLY_BASE, 1, 5);
		addSkillAttributeModifier("alchemist_subclass_1_crafting_notable_1", BREWED_HARMFUL_POTIONS_DURATION, 0.25, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_subclass_1_offensive_notable_1", CRIT_CHANCE, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_subclass_1_mastery", CRIT_DAMAGE, 0.5, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_subclass_special", CRAFTED_RINGS_CRITICAL_DAMAGE, 0.1, MULTIPLY_BASE);
		// healer skills
		addSkillBranchAttributeModifier("alchemist_subclass_2_defensive", EVASION, 0.01, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("alchemist_subclass_2", INCOMING_HEALING, 0.15, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("alchemist_subclass_2_life", MAX_HEALTH, 2, ADDITION, 1, 4);
		addSkillBranchAttributeModifier("alchemist_subclass_2_life", INCOMING_HEALING, 0.05, MULTIPLY_BASE, 1, 4);
		addSkillBranchAttributeModifier("alchemist_subclass_2_crafting", BREWED_HEALING_POTIONS_STRENGTH, 0.1, MULTIPLY_BASE, 1, 5);
		addSkillAttributeModifier("alchemist_subclass_2_crafting_notable_1", BREWED_BENEFICIAL_POTIONS_DURATION, 0.25, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_subclass_2_life_notable_1", MAX_HEALTH, 4, ADDITION);
		addSkillAttributeModifier("alchemist_subclass_2_life_notable_1", INCOMING_HEALING, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("alchemist_subclass_2_mastery", MAX_HEALTH, 10, ADDITION);
		addSkillAttributeModifier("alchemist_subclass_2_mastery", INCOMING_HEALING, 0.05, MULTIPLY_BASE);
		// hunter skills
		addSkillAttributeModifier("hunter_class", DOUBLE_LOOT_CHANCE, 0.15, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("hunter_defensive_crafting", CRAFTED_ARMOR_EVASION, 0.01, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("hunter_offensive_crafting", CRAFTED_RANGED_WEAPON_ATTACK_SPEED, 0.04, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("hunter_defensive", EVASION, 0.01, MULTIPLY_BASE, 1, 8);
		addSkillBranchAttributeModifier("hunter_offensive", PROJECTILE_DAMAGE, 0.1, MULTIPLY_BASE, 1, 8);
		addSkillAttributeModifier("hunter_defensive_notable_1", EVASION_CHANCE_WHEN_WOUNDED, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_offensive_notable_1", PROJECTILE_DAMAGE, 0.25, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("hunter_life", MAXIMUM_LIFE_PER_EVASION, 0.05, ADDITION, 1, 2);
		addSkillAttributeModifier("hunter_life_notable_1", MAXIMUM_LIFE_PER_EVASION, 0.1, ADDITION);
		addSkillBranchAttributeModifier("hunter_speed", ATTACK_SPEED_WITH_RANGED_WEAPON, 0.02, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("hunter_speed_notable_1", ATTACK_SPEED_WITH_RANGED_WEAPON, 0.05, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("hunter_lesser", DOUBLE_LOOT_CHANCE, 0.05, MULTIPLY_BASE, 1, 6);
		addSkillAttributeModifier("hunter_mastery", TRIPLE_LOOT_CHANCE, 0.15, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("hunter_crit", PROJECTILE_CRIT_CHANCE, 0.02, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("hunter_crit_notable_1", PROJECTILE_CRIT_DAMAGE, 0.25, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("hunter_crafting", CHANCE_TO_RETRIEVE_ARROWS, 0.05, MULTIPLY_BASE, 1, 3);
		addSkillAttributeModifier("hunter_defensive_crafting_keystone_1", CRAFTED_HELMETS_SOCKETS, 1, ADDITION);
		addSkillAttributeModifier("hunter_offensive_crafting_keystone_1", CRAFTED_RANGED_WEAPON_SOCKETS, 1, ADDITION);
		addSkillBranchAttributeModifier("hunter_healing", LIFE_PER_HIT, 0.25, ADDITION, 1, 4);
		addSkillAttributeModifier("hunter_healing_notable_1", LIFE_PER_PROJECTILE_HIT, 0.5, ADDITION);
		addSkillAttributeModifier("hunter_crafting_notable_1", CHANCE_TO_RETRIEVE_ARROWS, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_defensive_keystone_1", ARMOR_PER_EVASION, 0.25, ADDITION);
		addSkillAttributeModifier("hunter_offensive_keystone_1", DAMAGE_PER_DISTANCE_TO_ENEMY, 0.05, MULTIPLY_BASE);
		// ranger skills
		addSkillBranchAttributeModifier("hunter_subclass_1_defensive", EVASION, 0.01, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("hunter_subclass_1", STEALTH, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_subclass_1", JUMP_HEIGHT, 0.1, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("hunter_subclass_1_offensive", STEALTH, 0.05, MULTIPLY_BASE, 1, 4);
		addSkillBranchAttributeModifier("hunter_subclass_1_offensive", ATTACK_SPEED, 0.02, MULTIPLY_BASE, 1, 4);
		addSkillBranchAttributeModifier("hunter_subclass_1_crafting", CRAFTED_ARMOR_STEALTH, 0.01, MULTIPLY_BASE, 1, 5);
		addSkillAttributeModifier("hunter_subclass_1_crafting_notable_1", CRAFTED_BOOTS_STEALTH, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_subclass_1_offensive_notable_1", ATTACK_SPEED, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_subclass_1_offensive_notable_1", STEALTH, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_subclass_1_mastery", STEALTH, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_subclass_1_mastery", JUMP_HEIGHT, 0.5, MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_subclass_special", CRAFTED_WEAPON_LIFE_PER_HIT, 0.5, ADDITION);
		// fletcher skills
		addSkillBranchAttributeModifier("hunter_subclass_2_defensive", EVASION, 0.005, MULTIPLY_BASE, 1, 4);
		addSkillBranchAttributeModifier("hunter_subclass_2_defensive", BLOCK_CHANCE, 0.005, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("hunter_subclass_2", CRAFTED_QUIVERS_CHANCE_TO_RETRIEVE_ARROWS, 0.05, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("hunter_subclass_2_life", MAXIMUM_LIFE_PER_ARROW_IN_QUIVER, 0.01, ADDITION, 1, 4);
		addSkillBranchAttributeModifier("hunter_subclass_2_crafting", CRAFTED_QUIVERS_CAPACITY, 0.05, MULTIPLY_BASE, 1, 5);
		addSkillAttributeModifier("hunter_subclass_2_crafting_notable_1", CRAFTED_QUIVERS_CHANCE_TO_RETRIEVE_ARROWS, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("hunter_subclass_2_life_notable_1", CRAFTED_QUIVERS_MAXIMUM_LIFE, 5, ADDITION);
		addSkillAttributeModifier("hunter_subclass_2_mastery", CRAFTED_QUIVERS_CAPACITY, 0.25, MULTIPLY_BASE);
		// miner skills
		addSkillAttributeModifier("miner_class", MINING_SPEED, 0.15, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("miner_defensive_crafting", GEM_POWER_IN_ARMOR, 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("miner_offensive_crafting", GEM_POWER_IN_WEAPON, 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("miner_defensive", ARMOR, 1, ADDITION, 1, 8);
		addSkillBranchAttributeModifier("miner_offensive", DAMAGE_WITH_GEM_IN_WEAPON, 0.05, MULTIPLY_BASE, 1, 8);
		addSkillAttributeModifier("miner_defensive_notable_1", ARMOR_PER_GEM_IN_HELMET, 2, ADDITION);
		addSkillAttributeModifier("miner_offensive_notable_1", DAMAGE_WITH_GEM_IN_WEAPON, 0.15, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("miner_life", MAXIMUM_LIFE_PER_GEM_IN_HELMET, 1, ADDITION, 1, 2);
		addSkillAttributeModifier("miner_life_notable_1", MAXIMUM_LIFE_PER_GEM_IN_ARMOR, 1, ADDITION);
		addSkillBranchAttributeModifier("miner_speed", ATTACK_SPEED_WITH_GEM_IN_WEAPON, 0.02, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("miner_speed_notable_1", ATTACK_SPEED_PER_GEM_IN_WEAPON, 0.04, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("miner_lesser", MINING_SPEED, 0.05, MULTIPLY_BASE, 1, 6);
		addSkillAttributeModifier("miner_mastery", MAXIMUM_EQUIPMENT_SOCKETS, 1, ADDITION);
		addSkillBranchAttributeModifier("miner_crit", CRIT_CHANCE_PER_GEM_IN_WEAPON, 0.01, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("miner_crit_notable_1", CRIT_DAMAGE_PER_GEM_IN_WEAPON, 0.1, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("miner_crafting", GEM_DROP_CHANCE, 0.02, MULTIPLY_BASE, 1, 3);
		addSkillAttributeModifier("miner_defensive_crafting_keystone_1", GEM_POWER_IN_ARMOR, 0.3, MULTIPLY_BASE);
		addSkillAttributeModifier("miner_defensive_crafting_keystone_1", MAXIMUM_CHESTPLATE_SOCKETS, 1, ADDITION);
		addSkillAttributeModifier("miner_offensive_crafting_keystone_1", GEM_POWER_IN_WEAPON, 0.3, MULTIPLY_BASE);
		addSkillAttributeModifier("miner_offensive_crafting_keystone_1", MAXIMUM_WEAPON_SOCKETS, 1, ADDITION);
		addSkillBranchAttributeModifier("miner_healing", LIFE_REGENERATION, 0.25, ADDITION, 1, 4);
		addSkillAttributeModifier("miner_healing_notable_1", LIFE_REGENERATION_PER_GEM_IN_HELMET, 0.25, ADDITION);
		addSkillAttributeModifier("miner_crafting_notable_1", GEM_DROP_CHANCE, 0.04, MULTIPLY_BASE);
		addSkillAttributeModifier("miner_defensive_keystone_1", ARMOR_PER_GEM_IN_CHESTPLATE, 5, ADDITION);
		addSkillAttributeModifier("miner_offensive_keystone_1", DAMAGE_PER_GEM_IN_WEAPON, 0.1, MULTIPLY_BASE);
		// traveler skills
		addSkillBranchAttributeModifier("miner_subclass_1_defensive", ARMOR, 1, ADDITION, 1, 4);
		addSkillAttributeModifier("miner_subclass_1", ATTACK_SPEED, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("miner_subclass_1", MOVEMENT_SPEED, 0.1, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("miner_subclass_1_offensive", ATTACK_SPEED, 0.02, MULTIPLY_BASE, 1, 4);
		addSkillBranchAttributeModifier("miner_subclass_1_crafting", CRAFTED_BOOTS_MOVEMENT_SPEED, 0.02, MULTIPLY_BASE, 1, 5);
		addSkillAttributeModifier("miner_subclass_1_crafting_notable_1", CRAFTED_BOOTS_MOVEMENT_SPEED, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("miner_subclass_1_offensive_notable_1", ATTACK_SPEED, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("miner_subclass_1_offensive_notable_1", MOVEMENT_SPEED, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("miner_subclass_1_mastery", DAMAGE_PER_DISTANCE_TO_SPAWN, 0.0001, MULTIPLY_BASE);
		addSkillAttributeModifier("miner_subclass_special", CRAFTED_BOOTS_SOCKETS, 1, ADDITION);
		// jeweler skills
		addSkillBranchAttributeModifier("miner_subclass_2_defensive", EVASION, 0.005, MULTIPLY_BASE, 1, 4);
		addSkillBranchAttributeModifier("miner_subclass_2_defensive", ARMOR, 0.5, ADDITION, 1, 4);
		addSkillAttributeModifier("miner_subclass_2", MAXIMUM_RING_SOCKETS, 1, ADDITION);
		addSkillBranchAttributeModifier("miner_subclass_2_life", MAXIMUM_LIFE_PER_EQUIPPED_JEWELRY, 1, ADDITION, 1, 4);
		addSkillBranchAttributeModifier("miner_subclass_2_crafting", GEM_POWER_IN_JEWELRY, 0.05, MULTIPLY_BASE, 1, 5);
		addSkillAttributeModifier("miner_subclass_2_crafting_notable_1", GEM_POWER_IN_JEWELRY, 0.25, MULTIPLY_BASE);
		addSkillAttributeModifier("miner_subclass_2_life_notable_1", CRAFTED_NECKLACES_MAXIMUM_LIFE, 5, ADDITION);
		addSkillAttributeModifier("miner_subclass_2_mastery", CuriosHelper.getOrCreateSlotAttribute("ring"), 1, ADDITION);
		// blacksmith skills
		addSkillAttributeModifier("blacksmith_class", CRAFTED_EQUIPMENT_DURABILITY, 0.25, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_defensive_crafting", CRAFTED_ARMOR_DEFENCE, 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("blacksmith_offensive_crafting", CRAFTED_MELEE_WEAPON_DAMAGE_BONUS, 1, ADDITION, 1, 7);
		addSkillBranchAttributeModifier("blacksmith_defensive", ARMOR, 1, ADDITION, 1, 8);
		addSkillBranchAttributeModifier("blacksmith_offensive", DAMAGE_WITH_SHIELD, 0.05, MULTIPLY_BASE, 1, 8);
		addSkillAttributeModifier("blacksmith_defensive_notable_1", ARMOR, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_offensive_notable_1", DAMAGE_WITH_SHIELD, 0.25, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_life", MAXIMUM_LIFE_PER_BOOTS_ARMOR, 0.5, ADDITION, 1, 2);
		addSkillAttributeModifier("blacksmith_life_notable_1", MAXIMUM_LIFE_PER_BOOTS_ARMOR, 1, ADDITION);
		addSkillBranchAttributeModifier("blacksmith_speed", ATTACK_SPEED_WITH_SHIELD, 0.02, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("blacksmith_speed_notable_1", ATTACK_SPEED_WITH_SHIELD, 0.05, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_lesser", CRAFTED_EQUIPMENT_DURABILITY, 0.05, MULTIPLY_BASE, 1, 6);
		addSkillAttributeModifier("blacksmith_mastery", CHANCE_TO_CRAFT_TOUGHER_ARMOR, 1, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_crit", CRIT_CHANCE_WITH_SHIELD, 0.02, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("blacksmith_crit_notable_1", CRIT_DAMAGE_WITH_SHIELD, 0.3, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_crafting", CRAFTED_SHIELDS_ARMOR, 2, ADDITION, 1, 3);
		addSkillAttributeModifier("blacksmith_defensive_crafting_keystone_1", CRAFTED_ARMOR_DEFENCE, 0.30, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_offensive_crafting_keystone_1", CRAFTED_MELEE_WEAPON_ATTACK_SPEED, 0.25, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_healing", LIFE_REGENERATION, 0.25, ADDITION, 1, 4);
		addSkillAttributeModifier("blacksmith_healing_notable_1", LIFE_REGENERATION_WITH_SHIELD, 0.5, ADDITION);
		addSkillAttributeModifier("blacksmith_crafting_notable_1", CRAFTED_SHIELDS_ARMOR, 4, ADDITION);
		addSkillAttributeModifier("blacksmith_defensive_keystone_1", CHESTPLATE_ARMOR, 1, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_offensive_keystone_1", ATTACK_DAMAGE_PER_ARMOR, 0.1, ADDITION);
		// soldier skills
		addSkillBranchAttributeModifier("blacksmith_subclass_1_defensive", ARMOR, 0.5, ADDITION, 1, 4);
		addSkillBranchAttributeModifier("blacksmith_subclass_1_defensive", BLOCK_CHANCE, 0.005, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("blacksmith_subclass_1", ARMOR, 5, ADDITION);
		addSkillAttributeModifier("blacksmith_subclass_1", BLOCK_CHANCE, 0.05, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_subclass_1_offensive", MELEE_DAMAGE, 0.01, MULTIPLY_BASE, 1, 4);
		addSkillBranchAttributeModifier("blacksmith_subclass_1_offensive", BLOCK_CHANCE, 0.01, MULTIPLY_BASE, 1, 4);
		addSkillBranchAttributeModifier("blacksmith_subclass_1_crafting", CRAFTED_MELEE_WEAPON_CRIT_CHANCE, 0.01, MULTIPLY_BASE, 1, 5);
		addSkillAttributeModifier("blacksmith_subclass_1_crafting_notable_1", CRAFTED_MELEE_WEAPON_CRIT_CHANCE, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_subclass_1_offensive_notable_1", MELEE_DAMAGE, 0.2, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_subclass_1_offensive_notable_1", MELEE_CRIT_DAMAGE, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_subclass_1_mastery", BLOCK_CHANCE, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_subclass_1_mastery", ARMOR, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_subclass_1_mastery", MELEE_DAMAGE, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_subclass_special", CRAFTED_WEAPON_DOUBLE_LOOT_CHANCE, 0.05, MULTIPLY_BASE);
		// artisan skills
		addSkillBranchAttributeModifier("blacksmith_subclass_2_defensive", ARMOR, 1, ADDITION, 1, 4);
		addSkillAttributeModifier("blacksmith_subclass_2", EQUIPMENT_REPAIR_EFFICIENCY, 1, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("blacksmith_subclass_2_life", CRAFTED_ARMOR_MAXIMUM_LIFE, 1, ADDITION, 1, 4);
		addSkillBranchAttributeModifier("blacksmith_subclass_2_crafting", EQUIPMENT_REPAIR_EFFICIENCY, 0.05, MULTIPLY_BASE, 1, 5);
		addSkillAttributeModifier("blacksmith_subclass_2_crafting_notable_1", CRAFTED_SHIELDS_BLOCK_CHANCE, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_subclass_2_life_notable_1", CRAFTED_SHIELDS_MAXIMUM_LIFE, 5, ADDITION);
		addSkillAttributeModifier("blacksmith_subclass_2_mastery", EQUIPMENT_REPAIR_EFFICIENCY, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_subclass_2_mastery", CRAFTED_EQUIPMENT_DURABILITY, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_subclass_2_mastery", CRAFTED_WEAPON_ATTACK_SPEED, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_subclass_2_mastery", CHANCE_TO_CRAFT_TOUGHER_ARMOR, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("blacksmith_subclass_2_mastery", CRAFTED_SHIELDS_ARMOR, 5, ADDITION);
		// enchanter skills
		addSkillAttributeModifier("enchanter_class", ENCHANTMENT_LEVEL_REQUIREMENT, -0.3, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_defensive_crafting", ARMOR_ENCHANTMENT_POWER, 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("enchanter_offensive_crafting", WEAPON_ENCHANTMENT_POWER, 0.1, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("enchanter_defensive", BLOCK_CHANCE, 0.01, MULTIPLY_BASE, 1, 8);
		addSkillBranchAttributeModifier("enchanter_offensive", DAMAGE_WITH_ENCHANTED_WEAPON, 0.05, MULTIPLY_BASE, 1, 8);
		addSkillAttributeModifier("enchanter_defensive_notable_1", BLOCK_CHANCE_WITH_ENCHANTED_SHIELD, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_offensive_notable_1", DAMAGE_WITH_ENCHANTED_WEAPON, 0.2, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_life", MAXIMUM_LIFE_WITH_ENCHANTED_ITEM, 2, ADDITION, 1, 2);
		addSkillAttributeModifier("enchanter_life_notable_1", MAXIMUM_LIFE_PER_ARMOR_ENCHANTMENT, 1, ADDITION);
		addSkillBranchAttributeModifier("enchanter_speed", ATTACK_SPEED_WITH_ENCHANTED_WEAPON, 0.02, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("enchanter_speed_notable_1", ATTACK_SPEED_WITH_ENCHANTED_WEAPON, 0.05, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_lesser", ENCHANTMENT_LEVEL_REQUIREMENT, -0.05, MULTIPLY_BASE, 1, 6);
		addSkillAttributeModifier("enchanter_mastery", ENCHANTMENT_POWER, 1, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_crit", CRIT_CHANCE_WITH_ENCHANTED_WEAPON, 0.02, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("enchanter_crit_notable_1", CRIT_DAMAGE_PER_WEAPON_ENCHANTMENT, 0.05, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_crafting", FREE_ENCHANTMENT_CHANCE, 0.05, MULTIPLY_BASE, 1, 3);
		addSkillAttributeModifier("enchanter_defensive_crafting_keystone_1", ARMOR_ENCHANTMENT_POWER, 0.4, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_offensive_crafting_keystone_1", WEAPON_ENCHANTMENT_POWER, 0.4, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_healing", LIFE_ON_BLOCK, 0.25, ADDITION, 1, 4);
		addSkillAttributeModifier("enchanter_healing_notable_1", LIFE_ON_BLOCK_PER_SHIELD_ENCHANTMENT, 0.25, ADDITION);
		addSkillAttributeModifier("enchanter_crafting_notable_1", FREE_ENCHANTMENT_CHANCE, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_defensive_keystone_1", BLOCK_CHANCE_PER_SHIELD_ENCHANTMENT, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_offensive_keystone_1", DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL, 0.05, MULTIPLY_BASE);
		// arsonist skills
		addSkillBranchAttributeModifier("enchanter_subclass_1_defensive", EVASION, 0.005, MULTIPLY_BASE, 1, 4);
		addSkillBranchAttributeModifier("enchanter_subclass_1_defensive", BLOCK_CHANCE, 0.005, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("enchanter_subclass_1", DAMAGE_AGAINST_BURNING, 0.15, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_subclass_1", CHANCE_TO_IGNITE, 0.15, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("enchanter_subclass_1_offensive", DAMAGE_AGAINST_BURNING, 0.05, MULTIPLY_BASE, 1, 4);
		addSkillBranchAttributeModifier("enchanter_subclass_1_crafting", CRAFTED_WEAPON_CHANCE_TO_IGNITE, 0.05, MULTIPLY_BASE, 1, 5);
		addSkillAttributeModifier("enchanter_subclass_1_crafting_notable_1", CRAFTED_WEAPON_DAMAGE_AGAINST_BURNING, 0.2, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_subclass_1_offensive_notable_1", CRIT_CHANCE_AGAINST_BURNING, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_subclass_1_mastery", CHANCE_TO_IGNITE, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_subclass_1_mastery", DAMAGE_AGAINST_BURNING, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_subclass_1_mastery", CRIT_CHANCE_AGAINST_BURNING, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_subclass_special", CRAFTED_QUIVERS_DAMAGE_AGAINST_BURNING, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_subclass_special", CRAFTED_QUIVERS_CHANCE_TO_IGNITE, 0.1, MULTIPLY_BASE);
		// scholar skills
		addSkillBranchAttributeModifier("enchanter_subclass_2_defensive", BLOCK_CHANCE, 0.01, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("enchanter_subclass_2", EXPERIENCE_PER_HOUR, 2, ADDITION);
		addSkillBranchAttributeModifier("enchanter_subclass_2_life", MAX_HEALTH, 2, ADDITION, 1, 4);
		addSkillBranchAttributeModifier("enchanter_subclass_2_life", EXPERIENCE_PER_HOUR, 0.1, ADDITION, 1, 4);
		addSkillBranchAttributeModifier("enchanter_subclass_2_crafting", EXPERIENCE_PER_HOUR, 0.2, ADDITION, 1, 5);
		addSkillAttributeModifier("enchanter_subclass_2_crafting_notable_1", EXPERIENCE_FROM_ORE, 0.5, MULTIPLY_BASE);
		addSkillAttributeModifier("enchanter_subclass_2_life_notable_1", MAX_HEALTH, 6, ADDITION);
		addSkillAttributeModifier("enchanter_subclass_2_life_notable_1", EXPERIENCE_PER_HOUR, 0.1, ADDITION);
		addSkillAttributeModifier("enchanter_subclass_2_mastery", EXPERIENCE_PER_HOUR, 1.5, ADDITION);
		// cook skills
		addSkillAttributeModifier("cook_class", COOKED_FOOD_SATURATION, 0.2, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_defensive_crafting", COOKED_FOOD_HEALING_PER_SATURATION, 0.05, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("cook_offensive_crafting", COOKED_FOOD_DAMAGE_PER_SATURATION, 0.0025, MULTIPLY_BASE, 1, 7);
		addSkillBranchAttributeModifier("cook_defensive", BLOCK_CHANCE, 0.01, MULTIPLY_BASE, 1, 8);
		addSkillBranchAttributeModifier("cook_offensive", DAMAGE_IF_NOT_HUNGRY, 0.05, MULTIPLY_BASE, 1, 8);
		addSkillAttributeModifier("cook_defensive_notable_1", BLOCK_CHANCE_IF_NOT_HUNGRY, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("cook_offensive_notable_1", DAMAGE_IF_NOT_HUNGRY, 0.2, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_life", MAXIMUM_LIFE_IF_NOT_HUNGRY, 2, ADDITION, 1, 2);
		addSkillAttributeModifier("cook_life_notable_1", MAXIMUM_LIFE_PER_SATISFIED_HUNGER, 0.5, ADDITION);
		addSkillBranchAttributeModifier("cook_speed", ATTACK_SPEED_IF_NOT_HUNGRY, 0.02, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("cook_speed_notable_1", ATTACK_SPEED_IF_NOT_HUNGRY, 0.1, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_lesser", COOKED_FOOD_SATURATION, 0.05, MULTIPLY_BASE, 1, 6);
		addSkillAttributeModifier("cook_mastery", COOKED_FOOD_SATURATION, 0.5, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_crit", CRIT_CHANCE_IF_NOT_HUNGRY, 0.02, MULTIPLY_BASE, 1, 2);
		addSkillAttributeModifier("cook_crit_notable_1", CRIT_DAMAGE_PER_SATISFIED_HUNGER, 0.02, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_crafting", COOKED_FOOD_LIFE_REGENERATION, 0.1, ADDITION, 1, 3);
		addSkillAttributeModifier("cook_defensive_crafting_keystone_1", COOKED_FOOD_HEALING_PER_SATURATION, 0.2, MULTIPLY_BASE);
		addSkillAttributeModifier("cook_offensive_crafting_keystone_1", COOKED_FOOD_CRIT_DAMAGE_PER_SATURATION, 0.01, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_healing", LIFE_ON_BLOCK, 0.25, ADDITION, 1, 4);
		addSkillAttributeModifier("cook_healing_notable_1", LIFE_ON_BLOCK_IF_NOT_HUNGRY, 0.5, ADDITION);
		addSkillAttributeModifier("cook_crafting_notable_1", COOKED_FOOD_LIFE_REGENERATION, 0.2, ADDITION);
		addSkillAttributeModifier("cook_defensive_keystone_1", BLOCK_CHANCE_PER_SATISFIED_HUNGER, 0.01, MULTIPLY_BASE);
		addSkillAttributeModifier("cook_offensive_keystone_1", DAMAGE_PER_SATISFIED_HUNGER, 0.02, MULTIPLY_BASE);
		// berserker skills
		addSkillBranchAttributeModifier("cook_subclass_1_defensive", BLOCK_CHANCE, 0.01, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("cook_subclass_1", DAMAGE_IF_DAMAGED, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("cook_subclass_1", DAMAGE_IF_WOUNDED, 0.2, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_subclass_1_offensive", ATTACK_SPEED, 0.01, MULTIPLY_BASE, 1, 4);
		addSkillBranchAttributeModifier("cook_subclass_1_crafting", CRAFTED_AXES_CRIT_CHANCE, 0.01, MULTIPLY_BASE, 1, 5);
		addSkillAttributeModifier("cook_subclass_1_crafting_notable_1", CRAFTED_AXES_CRIT_CHANCE, 0.05, MULTIPLY_BASE);
		addSkillAttributeModifier("cook_subclass_1_offensive_notable_1", ATTACK_SPEED_IF_WOUNDED, 0.1, MULTIPLY_BASE);
		addSkillAttributeModifier("cook_subclass_1_mastery", CRIT_CHANCE_IF_WOUNDED, 0.15, MULTIPLY_BASE);
		addSkillAttributeModifier("cook_subclass_1_mastery", LIFE_PER_HIT_IF_WOUNDED, 1, ADDITION);
		addSkillAttributeModifier("cook_subclass_special", EXPERIENCE_FROM_MOBS, 0.5, MULTIPLY_BASE);
		// fisherman skills
		addSkillBranchAttributeModifier("cook_subclass_2_defensive", ARMOR, 0.5, ADDITION, 1, 4);
		addSkillBranchAttributeModifier("cook_subclass_2_defensive", BLOCK_CHANCE, 0.005, MULTIPLY_BASE, 1, 4);
		addSkillAttributeModifier("cook_subclass_2", DOUBLE_FISHING_LOOT_CHANCE, 0.15, MULTIPLY_BASE);
		addSkillBranchAttributeModifier("cook_subclass_2_life", MAX_HEALTH, 1, ADDITION, 1, 4);
		addSkillBranchAttributeModifier("cook_subclass_2_crafting", EXPERIENCE_FROM_FISHING, 0.2, MULTIPLY_BASE, 1, 5);
		addSkillAttributeModifier("cook_subclass_2_crafting_notable_1", EXPERIENCE_FROM_FISHING, 0.5, MULTIPLY_BASE);
		addSkillAttributeModifier("cook_subclass_2_life_notable_1", MAX_HEALTH, 4, ADDITION);
		addSkillAttributeModifier("cook_subclass_2_life_notable_1", LUCK_WHILE_FISHING, 1, ADDITION);
		addSkillAttributeModifier("cook_subclass_2_mastery", EXPERIENCE_FROM_FISHING, 0.5, MULTIPLY_BASE);
		addSkillAttributeModifier("cook_subclass_2_mastery", DOUBLE_FISHING_LOOT_CHANCE, 0.2, MULTIPLY_BASE);
	}

	private void addSkillBranchAttributeModifier(String branchName, Attribute attribute, double amount, Operation operation, int from, int to) {
		for (int node = from; node <= to; node++) {
			addSkillAttributeModifier(branchName + "_" + node, attribute, amount, operation);
		}
	}

	private void addSkillAttributeModifier(String skillName, Attribute attribute, double amount, Operation operation) {
		getSkill(skillName).addAttributeBonus(attribute, new AttributeModifier(UUID.randomUUID(), "Passive Skill Bonus", amount, operation));
	}

	private void addSkillBranchAttributeModifier(String branchName, RegistryObject<Attribute> attribute, double amount, Operation operation, int from,
			int to) {
		addSkillBranchAttributeModifier(branchName, attribute.get(), amount, operation, from, to);
	}

	private void addSkillAttributeModifier(String skillName, RegistryObject<Attribute> attribute, double amount, Operation operation) {
		addSkillAttributeModifier(skillName, attribute.get(), amount, operation);
	}

	public void addSkillBranch(String playerClass, String branchName, String iconName, int nodeSize, int from, int to) {
		for (int node = from; node <= to; node++) {
			addSkill(playerClass, branchName + "_" + node, iconName, nodeSize);
		}
	}

	private void connectSkillsBetweenClasses(String from, String to) {
		for (int classId = 0; classId < playerClasses.length - 1; classId++) {
			connectSkills(playerClasses[classId] + "_" + from, playerClasses[classId + 1] + "_" + to);
		}
		connectSkills(playerClasses[5] + "_" + from, playerClasses[0] + "_" + to);
	}

	private void setSkillBranchPosition(String playerClass, String nodeName, int distance, String branchName, float rotation, float rotationPerNode,
			int from, int to) {
		String branchNode = nodeName;
		for (int node = from; node <= to; node++) {
			setSkillPosition(playerClass, branchNode, distance, rotation + (node - from) * rotationPerNode, branchName + "_" + node);
			branchNode = branchName + "_" + node;
		}
	}

	private void setSkillPosition(String playerClass, @Nullable String previousSkillName, float distance, float angle, String skillName) {
		setSkillPosition(getClassId(playerClass), playerClass + "_" + previousSkillName, distance, angle, playerClass + "_" + skillName);
	}

	private void setSkillPosition(int classId, @Nullable String previousSkillName, float distance, float angle, String skillName) {
		angle *= Mth.PI / 180F;
		angle += getClassBranchRotation(classId);
		PassiveSkill previous = previousSkillName == null ? null : getSkill(previousSkillName);
		PassiveSkill skill = getSkill(skillName);
		float centerX = 0F;
		float centerY = 0F;
		int buttonSize = skill.getButtonSize();
		distance += buttonSize / 2F;
		if (previous != null) {
			int previousButtonRadius = previous.getButtonSize() / 2;
			distance += previousButtonRadius;
			centerX = previous.getPositionX() + previousButtonRadius;
			centerY = previous.getPositionY() + previousButtonRadius;
		}
		float skillX = centerX + Mth.sin(angle) * distance - buttonSize / 2F;
		float skillY = centerY + Mth.cos(angle) * distance - buttonSize / 2F;
		skill.setPosition(skillX, skillY);
		if (previous != null) previous.connect(skill);
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

	private void addGateway(String playerClass, String gatewayId) {
		ResourceLocation skillId = new ResourceLocation(SkillTreeMod.MOD_ID, playerClass + "_gateway");
		ResourceLocation treeId = new ResourceLocation(SkillTreeMod.MOD_ID, "tree");
		ResourceLocation backgroundTexture = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/background/gateway.png");
		ResourceLocation iconTexture = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/void.png");
		ResourceLocation borderTexture = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/tooltip/gateway.png");
		PassiveSkill skill = new PassiveSkill(skillId, treeId, 33, backgroundTexture, iconTexture, borderTexture, false);
		skill.setGatewayId(new ResourceLocation(SkillTreeMod.MOD_ID, gatewayId));
		data.put(skillId, skill);
	}

	private void addSkill(String name, String icon, int size) {
		ResourceLocation skillId = new ResourceLocation(SkillTreeMod.MOD_ID, name);
		ResourceLocation treeId = new ResourceLocation(SkillTreeMod.MOD_ID, "tree");
		String background = name.endsWith("class") || name.endsWith("subclass_1") || name.endsWith("subclass_2") ? "class"
				: size == 24 ? "keystone" : size == 20 ? "notable" : "lesser";
		ResourceLocation backgroundTexture = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/background/" + background + ".png");
		ResourceLocation iconTexture = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/" + icon + ".png");
		String border = size == 24 ? "keystone" : size == 20 ? "notable" : "lesser";
		ResourceLocation borderTexture = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/tooltip/" + border + ".png");
		data.put(skillId, new PassiveSkill(skillId, treeId, size, backgroundTexture, iconTexture, borderTexture, name.endsWith("class")));
	}

	@Override
	public void run(CachedOutput output) throws IOException {
		addSkills();
		shapeSkillTree();
		setSkillsAttributeModifiers();
		data.values().forEach(skill -> save(output, skill));
	}

	private void save(CachedOutput output, PassiveSkill skill) {
		Path path = dataGenerator.getOutputFolder().resolve(getSkillPath(skill));
		JsonObject json = JsonHelper.writePassiveSkill(skill);
		try {
			DataProvider.saveStable(output, json, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getSkillPath(PassiveSkill skill) {
		return "data/" + skill.getId().getNamespace() + "/skills/" + skill.getId().getPath() + ".json";
	}

	public Map<ResourceLocation, PassiveSkill> getSkills() {
		return data;
	}

	@Override
	public String getName() {
		return "Skills Provider";
	}
}
