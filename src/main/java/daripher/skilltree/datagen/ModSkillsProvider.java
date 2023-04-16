package daripher.skilltree.datagen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class ModSkillsProvider implements DataProvider {
	private Map<ResourceLocation, PassiveSkill> data = new HashMap<>();
	private DataGenerator dataGenerator;

	public ModSkillsProvider(DataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	private void addSkills() {
		addSkill("enchanter_class", "enchanter_class", 24, SkillTreeAttributes.ENCHANTMENT_LEVEL_REQUIREMENT_DECREASE.get(), 0.5D, Operation.ADDITION);
		addSkill("enchanter_lesser_left_1", "enchanter_lesser_left_lower", 16, SkillTreeAttributes.APPLIED_ENCHANTMENTS_AMPLIFICATION_CHANCE.get(), 0.1D, Operation.ADDITION);
		addSkill("enchanter_lesser_left_2", "enchanter_lesser_left_lower", 16, SkillTreeAttributes.APPLIED_ENCHANTMENTS_AMPLIFICATION_CHANCE.get(), 0.3D, Operation.ADDITION);
		addSkill("enchanter_lesser_left_3", "enchanter_lesser_left_lower", 16, SkillTreeAttributes.APPLIED_ENCHANTMENTS_AMPLIFICATION_CHANCE.get(), 0.1D, Operation.ADDITION);
		addSkill("enchanter_lesser_right_1", "enchanter_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_PER_ENCHANTED_ITEM.get(), 0.02D, Operation.ADDITION);
		addSkill("enchanter_lesser_right_2", "enchanter_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_PER_ENCHANTED_ITEM.get(), 0.05D, Operation.ADDITION);
		addSkill("enchanter_lesser_right_3", "enchanter_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_PER_ENCHANTED_ITEM.get(), 0.02D, Operation.ADDITION);
		addSkill("enchanter_mastery", "enchanter_mastery", 24, SkillTreeAttributes.APPLIED_ENCHANTMENTS_AMPLIFICATION_CHANCE.get(), 1D, Operation.ADDITION);
		addSkill("enchanter_lesser_left_4", "enchanter_lesser_left_upper", 16, SkillTreeAttributes.FREE_ENCHANTMENT_CHANCE.get(), 0.02D, Operation.ADDITION);
		addSkill("enchanter_lesser_left_5", "enchanter_lesser_left_upper", 16, SkillTreeAttributes.FREE_ENCHANTMENT_CHANCE.get(), 0.02D, Operation.ADDITION);
		addSkill("enchanter_lesser_left_6", "enchanter_lesser_left_upper", 16, SkillTreeAttributes.FREE_ENCHANTMENT_CHANCE.get(), 0.02D, Operation.ADDITION);
		addSkill("enchanter_lesser_left_7", "enchanter_lesser_left_upper", 16, SkillTreeAttributes.FREE_ENCHANTMENT_CHANCE.get(), 0.02D, Operation.ADDITION);
		addSkill("enchanter_lesser_left_8", "enchanter_lesser_left_upper", 16, SkillTreeAttributes.FREE_ENCHANTMENT_CHANCE.get(), 0.02D, Operation.ADDITION);
		addSkill("enchanter_notable_left", "enchanter_notable_left", 20, SkillTreeAttributes.APPLIED_ENCHANTMENTS_AMPLIFICATION_CHANCE.get(), 0.55D, Operation.ADDITION);
		addSkill("enchanter_lesser_right_4", "enchanter_lesser_right_upper", 16, SkillTreeAttributes.CRIT_CHANCE_BONUS_PER_ENCHANTED_ITEM.get(), 0.01D, Operation.ADDITION);
		addSkill("enchanter_lesser_right_5", "enchanter_lesser_right_upper", 16, SkillTreeAttributes.CRIT_CHANCE_BONUS_PER_ENCHANTED_ITEM.get(), 0.01D, Operation.ADDITION);
		addSkill("enchanter_lesser_right_6", "enchanter_lesser_right_upper", 16, SkillTreeAttributes.CRIT_CHANCE_BONUS_PER_ENCHANTED_ITEM.get(), 0.01D, Operation.ADDITION);
		addSkill("enchanter_lesser_right_7", "enchanter_lesser_right_upper", 16, SkillTreeAttributes.CRIT_CHANCE_BONUS_PER_ENCHANTED_ITEM.get(), 0.01D, Operation.ADDITION);
		addSkill("enchanter_lesser_right_8", "enchanter_lesser_right_upper", 16, SkillTreeAttributes.CRIT_CHANCE_BONUS_PER_ENCHANTED_ITEM.get(), 0.01D, Operation.ADDITION);
		addSkill("enchanter_notable_right", "enchanter_notable_right", 20, SkillTreeAttributes.CRIT_DAMAGE_BONUS_PER_ENCHANTED_ITEM.get(), 0.05D, Operation.ADDITION);
		addSkill("alchemist_class", "alchemist_class", 24, SkillTreeAttributes.BREWED_POTIONS_DURATION_BONUS.get(), 0.5D, Operation.ADDITION);
		addSkill("alchemist_lesser_left_1", "alchemist_lesser_left_lower", 16, SkillTreeAttributes.BREWED_POTIONS_AMPLIFICATION_CHANCE.get(), 0.05D, Operation.ADDITION);
		addSkill("alchemist_lesser_left_2", "alchemist_lesser_left_lower", 16, SkillTreeAttributes.BREWED_POTIONS_AMPLIFICATION_CHANCE.get(), 0.15D, Operation.ADDITION);
		addSkill("alchemist_lesser_left_3", "alchemist_lesser_left_lower", 16, SkillTreeAttributes.BREWED_POTIONS_AMPLIFICATION_CHANCE.get(), 0.05D, Operation.ADDITION);
		addSkill("alchemist_lesser_right_1", "alchemist_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_UNDER_POTION_EFFECT.get(), 0.1D, Operation.ADDITION);
		addSkill("alchemist_lesser_right_2", "alchemist_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_UNDER_POTION_EFFECT.get(), 0.25D, Operation.ADDITION);
		addSkill("alchemist_lesser_right_3", "alchemist_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_UNDER_POTION_EFFECT.get(), 0.1D, Operation.ADDITION);
		addSkill("alchemist_mastery", "alchemist_mastery", 24, SkillTreeAttributes.BREWED_POTIONS_AMPLIFICATION_CHANCE.get(), 1D, Operation.ADDITION);
		addSkill("alchemist_lesser_left_4", "alchemist_lesser_left_upper", 16, SkillTreeAttributes.BREWED_POTIONS_DURATION_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("alchemist_lesser_left_5", "alchemist_lesser_left_upper", 16, SkillTreeAttributes.BREWED_POTIONS_DURATION_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("alchemist_lesser_left_6", "alchemist_lesser_left_upper", 16, SkillTreeAttributes.BREWED_POTIONS_DURATION_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("alchemist_lesser_left_7", "alchemist_lesser_left_upper", 16, SkillTreeAttributes.BREWED_POTIONS_DURATION_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("alchemist_lesser_left_8", "alchemist_lesser_left_upper", 16, SkillTreeAttributes.BREWED_POTIONS_DURATION_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("alchemist_notable_left", "alchemist_notable_left", 20);
		addSkill("alchemist_lesser_right_4", "alchemist_lesser_right_upper", 16, SkillTreeAttributes.DAMAGE_BONUS_AGAINST_POISONED.get(), 0.05D, Operation.ADDITION);
		addSkill("alchemist_lesser_right_5", "alchemist_lesser_right_upper", 16, SkillTreeAttributes.DAMAGE_BONUS_AGAINST_POISONED.get(), 0.05D, Operation.ADDITION);
		addSkill("alchemist_lesser_right_6", "alchemist_lesser_right_upper", 16, SkillTreeAttributes.DAMAGE_BONUS_AGAINST_POISONED.get(), 0.05D, Operation.ADDITION);
		addSkill("alchemist_lesser_right_7", "alchemist_lesser_right_upper", 16, SkillTreeAttributes.DAMAGE_BONUS_AGAINST_POISONED.get(), 0.05D, Operation.ADDITION);
		addSkill("alchemist_lesser_right_8", "alchemist_lesser_right_upper", 16, SkillTreeAttributes.DAMAGE_BONUS_AGAINST_POISONED.get(), 0.05D, Operation.ADDITION);
		addSkill("alchemist_notable_right", "alchemist_notable_right", 20, SkillTreeAttributes.CRIT_CHANCE_BONUS_AGAINST_POISONED.get(), 0.5D, Operation.ADDITION);
		addSkill("blacksmith_class", "blacksmith_class", 24, SkillTreeAttributes.CRAFTED_ARMOR_DEFENCE_BONUS.get(), 0.5D, Operation.ADDITION);
		addSkill("blacksmith_lesser_left_1", "blacksmith_lesser_left_lower", 16, SkillTreeAttributes.TOUGHER_ARMOR_CRAFTING_CHANCE.get(), 0.1D, Operation.ADDITION);
		addSkill("blacksmith_lesser_left_2", "blacksmith_lesser_left_lower", 16, SkillTreeAttributes.TOUGHER_ARMOR_CRAFTING_CHANCE.get(), 0.25D, Operation.ADDITION);
		addSkill("blacksmith_lesser_left_3", "blacksmith_lesser_left_lower", 16, SkillTreeAttributes.TOUGHER_ARMOR_CRAFTING_CHANCE.get(), 0.1D, Operation.ADDITION);
		addSkill("blacksmith_lesser_right_1", "blacksmith_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_WITH_SHIELD.get(), 0.1D, Operation.ADDITION);
		addSkill("blacksmith_lesser_right_2", "blacksmith_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_WITH_SHIELD.get(), 0.2D, Operation.ADDITION);
		addSkill("blacksmith_lesser_right_3", "blacksmith_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_WITH_SHIELD.get(), 0.1D, Operation.ADDITION);
		addSkill("blacksmith_mastery", "blacksmith_mastery", 24, SkillTreeAttributes.TOUGHER_ARMOR_CRAFTING_CHANCE.get(), 1D, Operation.ADDITION);
		addSkill("blacksmith_lesser_left_4", "blacksmith_lesser_left_upper", 16, SkillTreeAttributes.CRAFTED_WEAPON_DAMAGE_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("blacksmith_lesser_left_5", "blacksmith_lesser_left_upper", 16, SkillTreeAttributes.CRAFTED_WEAPON_DAMAGE_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("blacksmith_lesser_left_6", "blacksmith_lesser_left_upper", 16, SkillTreeAttributes.CRAFTED_WEAPON_DAMAGE_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("blacksmith_lesser_left_7", "blacksmith_lesser_left_upper", 16, SkillTreeAttributes.CRAFTED_WEAPON_DAMAGE_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("blacksmith_lesser_left_8", "blacksmith_lesser_left_upper", 16, SkillTreeAttributes.CRAFTED_WEAPON_DAMAGE_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("blacksmith_notable_left", "blacksmith_notable_left", 20, SkillTreeAttributes.CRAFTED_WEAPON_ATTACK_SPEED_BONUS.get(), 0.25D, Operation.ADDITION);
		addSkill("blacksmith_lesser_right_4", "blacksmith_lesser_right_upper", 16, SkillTreeAttributes.DOUBLE_DAMAGE_CHANCE_WITH_SHIELD.get(), 0.03D, Operation.ADDITION);
		addSkill("blacksmith_lesser_right_5", "blacksmith_lesser_right_upper", 16, SkillTreeAttributes.DOUBLE_DAMAGE_CHANCE_WITH_SHIELD.get(), 0.03D, Operation.ADDITION);
		addSkill("blacksmith_lesser_right_6", "blacksmith_lesser_right_upper", 16, SkillTreeAttributes.DOUBLE_DAMAGE_CHANCE_WITH_SHIELD.get(), 0.03D, Operation.ADDITION);
		addSkill("blacksmith_lesser_right_7", "blacksmith_lesser_right_upper", 16, SkillTreeAttributes.DOUBLE_DAMAGE_CHANCE_WITH_SHIELD.get(), 0.03D, Operation.ADDITION);
		addSkill("blacksmith_lesser_right_8", "blacksmith_lesser_right_upper", 16, SkillTreeAttributes.DOUBLE_DAMAGE_CHANCE_WITH_SHIELD.get(), 0.03D, Operation.ADDITION);
		addSkill("blacksmith_notable_right", "blacksmith_notable_right", 20, SkillTreeAttributes.CRIT_CHANCE_BONUS_PER_ARMOR_POINT.get(), 0.01D, Operation.ADDITION);
		addSkill("miner_class", "miner_class", 24, SkillTreeAttributes.MINING_SPEED_BONUS.get(), 0.25D, Operation.ADDITION);
		addSkill("miner_lesser_left_1", "miner_lesser_left_lower", 16, SkillTreeAttributes.MINERALS_DUPLICATION_CHANCE.get(), 0.03D, Operation.ADDITION);
		addSkill("miner_lesser_left_2", "miner_lesser_left_lower", 16, SkillTreeAttributes.MINERALS_DUPLICATION_CHANCE.get(), 0.09D, Operation.ADDITION);
		addSkill("miner_lesser_left_3", "miner_lesser_left_lower", 16, SkillTreeAttributes.MINERALS_DUPLICATION_CHANCE.get(), 0.03D, Operation.ADDITION);
		addSkill("miner_lesser_right_1", "miner_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_WITH_PICKAXE.get(), 0.1D, Operation.ADDITION);
		addSkill("miner_lesser_right_2", "miner_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_WITH_PICKAXE.get(), 0.25D, Operation.ADDITION);
		addSkill("miner_lesser_right_3", "miner_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_WITH_PICKAXE.get(), 0.1D, Operation.ADDITION);
		addSkill("miner_mastery", "miner_mastery", 24, SkillTreeAttributes.GEMSTONE_FINDING_CHANCE.get(), 0.1D, Operation.ADDITION);
		addSkill("miner_lesser_left_4", "miner_lesser_left_upper", 16, SkillTreeAttributes.APPLIED_GEMSTONES_STRENGTH_BONUS.get(), 0.2D, Operation.ADDITION);
		addSkill("miner_lesser_left_5", "miner_lesser_left_upper", 16, SkillTreeAttributes.APPLIED_GEMSTONES_STRENGTH_BONUS.get(), 0.2D, Operation.ADDITION);
		addSkill("miner_lesser_left_6", "miner_lesser_left_upper", 16, SkillTreeAttributes.APPLIED_GEMSTONES_STRENGTH_BONUS.get(), 0.2D, Operation.ADDITION);
		addSkill("miner_lesser_left_7", "miner_lesser_left_upper", 16, SkillTreeAttributes.APPLIED_GEMSTONES_STRENGTH_BONUS.get(), 0.2D, Operation.ADDITION);
		addSkill("miner_lesser_left_8", "miner_lesser_left_upper", 16, SkillTreeAttributes.APPLIED_GEMSTONES_STRENGTH_BONUS.get(), 0.2D, Operation.ADDITION);
		addSkill("miner_notable_left", "miner_notable_left", 20);
		addSkill("miner_lesser_right_4", "miner_lesser_right_upper", 16, SkillTreeAttributes.ATTACK_SPEED_BONUS_WITH_PICKAXE.get(), 0.05D, Operation.ADDITION);
		addSkill("miner_lesser_right_5", "miner_lesser_right_upper", 16, SkillTreeAttributes.ATTACK_SPEED_BONUS_WITH_PICKAXE.get(), 0.05D, Operation.ADDITION);
		addSkill("miner_lesser_right_6", "miner_lesser_right_upper", 16, SkillTreeAttributes.ATTACK_SPEED_BONUS_WITH_PICKAXE.get(), 0.05D, Operation.ADDITION);
		addSkill("miner_lesser_right_7", "miner_lesser_right_upper", 16, SkillTreeAttributes.ATTACK_SPEED_BONUS_WITH_PICKAXE.get(), 0.05D, Operation.ADDITION);
		addSkill("miner_lesser_right_8", "miner_lesser_right_upper", 16, SkillTreeAttributes.ATTACK_SPEED_BONUS_WITH_PICKAXE.get(), 0.05D, Operation.ADDITION);
		addSkill("miner_notable_right", "miner_notable_right", 20, SkillTreeAttributes.CHANCE_TO_IGNORE_ARMOR.get(), 0.3D, Operation.ADDITION);
		addSkill("hunter_class", "hunter_class", 24, SkillTreeAttributes.LOOT_DUPLICATION_CHANCE.get(), 0.5D, Operation.ADDITION);
		addSkill("hunter_lesser_left_1", "hunter_lesser_left_lower", 16, SkillTreeAttributes.CRAFTED_BOWS_CHARGE_SPEED_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("hunter_lesser_left_2", "hunter_lesser_left_lower", 16, SkillTreeAttributes.CRAFTED_BOWS_CHARGE_SPEED_BONUS.get(), 0.1D, Operation.ADDITION);
		addSkill("hunter_lesser_left_3", "hunter_lesser_left_lower", 16, SkillTreeAttributes.CRAFTED_BOWS_CHARGE_SPEED_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("hunter_lesser_right_1", "hunter_lesser_right_lower", 16, SkillTreeAttributes.ARROW_CRIT_DAMAGE_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("hunter_lesser_right_2", "hunter_lesser_right_lower", 16, SkillTreeAttributes.ARROW_CRIT_DAMAGE_BONUS.get(), 0.1D, Operation.ADDITION);
		addSkill("hunter_lesser_right_3", "hunter_lesser_right_lower", 16, SkillTreeAttributes.ARROW_CRIT_DAMAGE_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("hunter_mastery", "hunter_mastery", 24, SkillTreeAttributes.CRAFTED_BOWS_ARMOR_REDUCTION.get(), 0.25D, Operation.ADDITION);
		addSkill("hunter_lesser_left_4", "hunter_lesser_left_upper", 16, SkillTreeAttributes.CRAFTED_BOWS_DAMAGE_BONUS.get(), 0.02D, Operation.ADDITION);
		addSkill("hunter_lesser_left_5", "hunter_lesser_left_upper", 16, SkillTreeAttributes.CRAFTED_BOWS_DAMAGE_BONUS.get(), 0.02D, Operation.ADDITION);
		addSkill("hunter_lesser_left_6", "hunter_lesser_left_upper", 16, SkillTreeAttributes.CRAFTED_BOWS_DAMAGE_BONUS.get(), 0.02D, Operation.ADDITION);
		addSkill("hunter_lesser_left_7", "hunter_lesser_left_upper", 16, SkillTreeAttributes.CRAFTED_BOWS_DAMAGE_BONUS.get(), 0.02D, Operation.ADDITION);
		addSkill("hunter_lesser_left_8", "hunter_lesser_left_upper", 16, SkillTreeAttributes.CRAFTED_BOWS_DAMAGE_BONUS.get(), 0.02D, Operation.ADDITION);
		addSkill("hunter_notable_left", "hunter_notable_left", 20, SkillTreeAttributes.CRAFTED_BOWS_DOUBLE_DAMAGE_CHANCE.get(), 0.1D, Operation.ADDITION);
		addSkill("hunter_lesser_right_4", "hunter_lesser_right_upper", 16, SkillTreeAttributes.BOW_CHARGE_SPEED_MULTIPLIER.get(), 0.03D, Operation.ADDITION);
		addSkill("hunter_lesser_right_5", "hunter_lesser_right_upper", 16, SkillTreeAttributes.BOW_CHARGE_SPEED_MULTIPLIER.get(), 0.03D, Operation.ADDITION);
		addSkill("hunter_lesser_right_6", "hunter_lesser_right_upper", 16, SkillTreeAttributes.BOW_CHARGE_SPEED_MULTIPLIER.get(), 0.03D, Operation.ADDITION);
		addSkill("hunter_lesser_right_7", "hunter_lesser_right_upper", 16, SkillTreeAttributes.BOW_CHARGE_SPEED_MULTIPLIER.get(), 0.03D, Operation.ADDITION);
		addSkill("hunter_lesser_right_8", "hunter_lesser_right_upper", 16, SkillTreeAttributes.BOW_CHARGE_SPEED_MULTIPLIER.get(), 0.03D, Operation.ADDITION);
		addSkill("hunter_notable_right", "hunter_notable_right", 20, SkillTreeAttributes.ARROW_DAMAGE_MULTIPLIER_PER_DISTANCE.get(), 0.02D, Operation.ADDITION);
		addSkill("cook_class", "cook_class", 24, SkillTreeAttributes.COOKED_FOOD_RESTORATION_BONUS.get(), 0.5D, Operation.ADDITION);
		addSkill("cook_lesser_left_1", "cook_lesser_left_lower", 16, SkillTreeAttributes.COOKED_FOOD_HEALING_PER_RESTORATION.get(), 0.15D, Operation.ADDITION);
		addSkill("cook_lesser_left_2", "cook_lesser_left_lower", 16, SkillTreeAttributes.COOKED_FOOD_HEALING_PER_RESTORATION.get(), 0.2D, Operation.ADDITION);
		addSkill("cook_lesser_left_3", "cook_lesser_left_lower", 16, SkillTreeAttributes.COOKED_FOOD_HEALING_PER_RESTORATION.get(), 0.15D, Operation.ADDITION);
		addSkill("cook_lesser_right_1", "cook_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_WHEN_FULL.get(), 0.15D, Operation.ADDITION);
		addSkill("cook_lesser_right_2", "cook_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_WHEN_FULL.get(), 0.25D, Operation.ADDITION);
		addSkill("cook_lesser_right_3", "cook_lesser_right_lower", 16, SkillTreeAttributes.DAMAGE_BONUS_WHEN_FULL.get(), 0.15D, Operation.ADDITION);
		addSkill("cook_mastery", "cook_mastery", 24, SkillTreeAttributes.COOKED_FOOD_DAMAGE_PER_RESTORATION_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("cook_lesser_left_4", "cook_lesser_left_upper", 16, SkillTreeAttributes.COOKED_FOOD_CRIT_CHANCE_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("cook_lesser_left_5", "cook_lesser_left_upper", 16, SkillTreeAttributes.COOKED_FOOD_CRIT_CHANCE_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("cook_lesser_left_6", "cook_lesser_left_upper", 16, SkillTreeAttributes.COOKED_FOOD_CRIT_CHANCE_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("cook_lesser_left_7", "cook_lesser_left_upper", 16, SkillTreeAttributes.COOKED_FOOD_CRIT_CHANCE_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("cook_lesser_left_8", "cook_lesser_left_upper", 16, SkillTreeAttributes.COOKED_FOOD_CRIT_CHANCE_BONUS.get(), 0.05D, Operation.ADDITION);
		addSkill("cook_notable_left", "cook_notable_left", 20, SkillTreeAttributes.COOKED_FOOD_ATTACK_SPEED_BONUS.get(), 0.3D, Operation.ADDITION);
		addSkill("cook_lesser_right_4", "cook_lesser_right_upper", 16, SkillTreeAttributes.CRIT_CHANCE_BONUS_WHEN_FULL.get(), 0.05D, Operation.ADDITION);
		addSkill("cook_lesser_right_5", "cook_lesser_right_upper", 16, SkillTreeAttributes.CRIT_CHANCE_BONUS_WHEN_FULL.get(), 0.05D, Operation.ADDITION);
		addSkill("cook_lesser_right_6", "cook_lesser_right_upper", 16, SkillTreeAttributes.CRIT_CHANCE_BONUS_WHEN_FULL.get(), 0.05D, Operation.ADDITION);
		addSkill("cook_lesser_right_7", "cook_lesser_right_upper", 16, SkillTreeAttributes.CRIT_CHANCE_BONUS_WHEN_FULL.get(), 0.05D, Operation.ADDITION);
		addSkill("cook_lesser_right_8", "cook_lesser_right_upper", 16, SkillTreeAttributes.CRIT_CHANCE_BONUS_WHEN_FULL.get(), 0.05D, Operation.ADDITION);
		addSkill("cook_notable_right", "cook_notable_right", 20, SkillTreeAttributes.CRIT_DAMAGE_BONUS_IF_ATE_RECENTLY.get(), 0.5D, Operation.ADDITION);
	}

	private void generateSkillTree() {
		var playerClasses = new String[] { "hunter", "cook", "alchemist", "enchanter", "blacksmith", "miner" };

		for (var classId = 0; classId < playerClasses.length; classId++) {
			setClassSkillsPosition(playerClasses[classId], classId);
		}

		for (var classId = 0; classId < playerClasses.length - 1; classId++) {
			connectSkills(playerClasses[classId] + "_lesser_left_6", playerClasses[classId + 1] + "_lesser_right_6");
		}

		connectSkills("miner_lesser_left_6", "hunter_lesser_right_6");
	}

	private void setClassSkillsPosition(String playerClass, int classId) {
		var classButton = setSkillPosition(null, classId, 20F, 0F, getSkill(playerClass + "_class"));
		var rightBranchButton = setSkillPosition(classButton, classId, 8F, -180F / 6F, getSkill(playerClass + "_lesser_right_1"));
		rightBranchButton = setSkillPosition(rightBranchButton, classId, 8F, -5F, getSkill(playerClass + "_lesser_right_2"));
		rightBranchButton = setSkillPosition(rightBranchButton, classId, 8F, 5F, getSkill(playerClass + "_lesser_right_3"));
		var leftBranchButton = setSkillPosition(classButton, classId, 8F, 180F / 6F, getSkill(playerClass + "_lesser_left_1"));
		leftBranchButton = setSkillPosition(leftBranchButton, classId, 8F, 5F, getSkill(playerClass + "_lesser_left_2"));
		leftBranchButton = setSkillPosition(leftBranchButton, classId, 8F, -5F, getSkill(playerClass + "_lesser_left_3"));
		setSkillPosition(rightBranchButton, classId, 8F, 180F / 6F, getSkill(playerClass + "_mastery"));
		connectSkills(playerClass + "_lesser_left_3", playerClass + "_mastery");
		rightBranchButton = setSkillPosition(rightBranchButton, classId, 8F, -360F / 6F, getSkill(playerClass + "_lesser_right_4"));
		rightBranchButton = setSkillPosition(rightBranchButton, classId, 8F, -290F / 6F, getSkill(playerClass + "_lesser_right_5"));
		rightBranchButton = setSkillPosition(rightBranchButton, classId, 8F, -220F / 6F, getSkill(playerClass + "_lesser_right_6"));
		rightBranchButton = setSkillPosition(rightBranchButton, classId, 8F, -150F / 6F, getSkill(playerClass + "_lesser_right_7"));
		var innerBranchButton = setSkillPosition(rightBranchButton, classId, 8F, 500F / 6F, getSkill(playerClass + "_lesser_right_8"));
		setSkillPosition(innerBranchButton, classId, 8F, 460F / 6F, getSkill(playerClass + "_notable_right"));
		leftBranchButton = setSkillPosition(leftBranchButton, classId, 8F, 360F / 6F, getSkill(playerClass + "_lesser_left_4"));
		leftBranchButton = setSkillPosition(leftBranchButton, classId, 8F, 290F / 6F, getSkill(playerClass + "_lesser_left_5"));
		leftBranchButton = setSkillPosition(leftBranchButton, classId, 8F, 220F / 6F, getSkill(playerClass + "_lesser_left_6"));
		leftBranchButton = setSkillPosition(leftBranchButton, classId, 8F, 150F / 6F, getSkill(playerClass + "_lesser_left_7"));
		innerBranchButton = setSkillPosition(leftBranchButton, classId, 8F, -500F / 6F, getSkill(playerClass + "_lesser_left_8"));
		setSkillPosition(innerBranchButton, classId, 8F, -460F / 6F, getSkill(playerClass + "_notable_left"));
	}

	private PassiveSkill setSkillPosition(@Nullable PassiveSkill previousSkill, int classId, float distance, float angle, PassiveSkill skill) {
		angle *= Mth.PI / 180F;
		angle += getClassBranchRotation(classId);
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
		skill.setPosition((int) skillX, (int) skillY);

		if (previousSkill != null) {
			previousSkill.connect(skill);
		}

		return skill;
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
		addSkill(skillName, iconName, buttonSize, null);
	}

	private void addSkill(String skillName, String iconName, int buttonSize, Attribute modifiedAttribute, double attributeBonus, Operation modifierOperation) {
		addSkill(skillName, iconName, buttonSize, Triple.of(modifiedAttribute, attributeBonus, modifierOperation));
	}

	private void addSkill(String skillName, String iconName, int buttonSize, Triple<Attribute, Double, Operation> attributeBonus) {
		var skillId = new ResourceLocation(SkillTreeMod.MOD_ID, skillName);
		var backgroundName = buttonSize == 24 ? "keystone" : buttonSize == 20 ? "notable" : "lesser";
		var backgroundTexture = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/background/" + backgroundName + ".png");
		var iconTexture = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/" + iconName + ".png");
		data.put(skillId, new PassiveSkill(skillId, buttonSize, backgroundTexture, iconTexture, skillName.endsWith("_class"), attributeBonus));
	}

	@Override
	public void run(CachedOutput cachedOutput) throws IOException {
		addSkills();
		generateSkillTree();

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
