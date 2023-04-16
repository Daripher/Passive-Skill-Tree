package daripher.skilltree.datagen;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeEffects;
import daripher.skilltree.init.SkillTreeItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.data.LanguageProvider;

public class ModUsTranslationProvider extends LanguageProvider {
	public ModUsTranslationProvider(DataGenerator gen) {
		super(gen, SkillTreeMod.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		addSkill("enchanter_class", "Enchanter", "-50% to Level Requirement for Enchantments");
		addSkill("enchanter_lesser_left_1", "Better Enchantments", "+10% chance of applying better Enchantment");
		addSkill("enchanter_lesser_left_2", "Better Enchantments", "+30% chance of applying better Enchantment");
		addSkill("enchanter_lesser_left_3", "Better Enchantments", "+10% chance of applying better Enchantment");
		addSkill("enchanter_lesser_right_1", "Damage with Enchanted Items", "+2% Damage per Enchanted Item equipped");
		addSkill("enchanter_lesser_right_2", "Damage with Enchanted Items", "+5% Damage per Enchanted Item equipped");
		addSkill("enchanter_lesser_right_3", "Damage with Enchanted Items", "+2% Damage per Enchanted Item equipped");
		addSkill("enchanter_mastery", "Master Enchanter", "You always apply higher level Enchantments");
		addSkill("enchanter_lesser_right_4", "Crit Chance with Enchanted Items", "+1% Crit Chance per Enchanted Item equipped");
		addSkill("enchanter_lesser_right_5", "Crit Chance with Enchanted Items", "+1% Crit Chance per Enchanted Item equipped");
		addSkill("enchanter_lesser_right_6", "Crit Chance with Enchanted Items", "+1% Crit Chance per Enchanted Item equipped");
		addSkill("enchanter_lesser_right_7", "Crit Chance with Enchanted Items", "+1% Crit Chance per Enchanted Item equipped");
		addSkill("enchanter_lesser_right_8", "Crit Chance with Enchanted Items", "+1% Crit Chance per Enchanted Item equipped");
		addSkill("enchanter_notable_right", "Embodiment of Power", "+5% to Crit Damage per Enchanted Item equipped");
		addSkill("enchanter_lesser_left_4", "Free Enchantments", "+2% chance to Enchant Items for Free");
		addSkill("enchanter_lesser_left_5", "Free Enchantments", "+2% chance to Enchant Items for Free");
		addSkill("enchanter_lesser_left_6", "Free Enchantments", "+2% chance to Enchant Items for Free");
		addSkill("enchanter_lesser_left_7", "Free Enchantments", "+2% chance to Enchant Items for Free");
		addSkill("enchanter_lesser_left_8", "Free Enchantments", "+2% chance to Enchant Items for Free");
		addSkill("enchanter_notable_left", "Deep Knowledge", "+55% chance of applying better Enchantment");
		addSkill("alchemist_class", "Alchemist", "+50% Duration of Potions you Brew");
		addSkill("alchemist_lesser_left_1", "Stronger Potions", "+5% chance of Brewing Stonger Potion");
		addSkill("alchemist_lesser_left_2", "Stronger Potions", "+15% chance of Brewing Stonger Potion");
		addSkill("alchemist_lesser_left_3", "Stronger Potions", "+5% chance of Brewing Stonger Potion");
		addSkill("alchemist_lesser_right_1", "Damage Under Potion Effect", "+10% Damage Under Potion Effect");
		addSkill("alchemist_lesser_right_2", "Damage Under Potion Effect", "+25% Damage Under Potion Effect");
		addSkill("alchemist_lesser_right_3", "Damage Under Potion Effect", "+10% Damage Under Potion Effect");
		addSkill("alchemist_mastery", "Master Alchemist", "All Potions you Brew have Stronger effect");
		addSkill("alchemist_lesser_right_4", "Damage against Poisoned", "+5% to Damage against Poisoned Enemies");
		addSkill("alchemist_lesser_right_5", "Damage against Poisoned", "+5% to Damage against Poisoned Enemies");
		addSkill("alchemist_lesser_right_6", "Damage against Poisoned", "+5% to Damage against Poisoned Enemies");
		addSkill("alchemist_lesser_right_7", "Damage against Poisoned", "+5% to Damage against Poisoned Enemies");
		addSkill("alchemist_lesser_right_8", "Damage against Poisoned", "+5% to Damage against Poisoned Enemies");
		addSkill("alchemist_notable_right", "Cruelty", "+50% Crit Chance against Poisoned Enemies");
		addSkill("alchemist_lesser_left_4", "Potion Duration", "+5% Duration of Potions you Brew");
		addSkill("alchemist_lesser_left_5", "Potion Duration", "+5% Duration of Potions you Brew");
		addSkill("alchemist_lesser_left_6", "Potion Duration", "+5% Duration of Potions you Brew");
		addSkill("alchemist_lesser_left_7", "Potion Duration", "+5% Duration of Potions you Brew");
		addSkill("alchemist_lesser_left_8", "Potion Duration", "+5% Duration of Potions you Brew");
		addSkill("alchemist_notable_left", "Alchemy Genius", "You can Combine two Potions together");
		addSkill("blacksmith_class", "Blacksmith", "+50% Defence from Armor you Craft");
		addSkill("blacksmith_lesser_left_1", "Tougher Armor", "+10% chance of Forging Tougher Armor");
		addSkill("blacksmith_lesser_left_2", "Tougher Armor", "+25% chance of Forging Tougher Armor");
		addSkill("blacksmith_lesser_left_3", "Tougher Armor", "+10% chance of Forging Tougher Armor");
		addSkill("blacksmith_lesser_right_1", "Damage with Shield", "+10% Damage while holding a Shield");
		addSkill("blacksmith_lesser_right_2", "Damage with Shield", "+20% Damage while holding a Shield");
		addSkill("blacksmith_lesser_right_3", "Damage with Shield", "+10% Damage while holding a Shield");
		addSkill("blacksmith_mastery", "Master Blacksmith", "You always Craft Tougher Armor");
		addSkill("blacksmith_lesser_right_4", "Double Damage with Shield", "+3% to deal Double Damage while holding a Shield");
		addSkill("blacksmith_lesser_right_5", "Double Damage with Shield", "+3% to deal Double Damage while holding a Shield");
		addSkill("blacksmith_lesser_right_6", "Double Damage with Shield", "+3% to deal Double Damage while holding a Shield");
		addSkill("blacksmith_lesser_right_7", "Double Damage with Shield", "+3% to deal Double Damage while holding a Shield");
		addSkill("blacksmith_lesser_right_8", "Double Damage with Shield", "+3% to deal Double Damage while holding a Shield");
		addSkill("blacksmith_notable_right", "Best Defence", "+1% Crit Chance per Armor point");
		addSkill("blacksmith_lesser_left_4", "Weapon Damage", "+5% Damage of Weapons Crafted by you");
		addSkill("blacksmith_lesser_left_5", "Weapon Damage", "+5% Damage of Weapons Crafted by you");
		addSkill("blacksmith_lesser_left_6", "Weapon Damage", "+5% Damage of Weapons Crafted by you");
		addSkill("blacksmith_lesser_left_7", "Weapon Damage", "+5% Damage of Weapons Crafted by you");
		addSkill("blacksmith_lesser_left_8", "Weapon Damage", "+5% Damage of Weapons Crafted by you");
		addSkill("blacksmith_notable_left", "Mistic Alloy", "+25% Attack Speed of Weapons Crafted by you");
		addSkill("miner_class", "Miner", "+25% Mining Speed");
		addSkill("miner_lesser_left_1", "Minerals Duplication", "+3% chance to Duplicate found Minerals");
		addSkill("miner_lesser_left_2", "Minerals Duplication", "+9% chance to Duplicate found Minerals");
		addSkill("miner_lesser_left_3", "Minerals Duplication", "+3% chance to Duplicate found Minerals");
		addSkill("miner_lesser_right_1", "Pickaxe Damage", "+15% Damage with Pickaxes");
		addSkill("miner_lesser_right_2", "Pickaxe Damage", "+25% Damage with Pickaxes");
		addSkill("miner_lesser_right_3", "Pickaxe Damage", "+15% Damage with Pickaxes");
		addSkill("miner_mastery", "Master Miner", "You can find Gemstones in Ore");
		addSkill("miner_lesser_right_4", "Pickaxe Attack Speed", "+5% Attack Speed with Pickaxes");
		addSkill("miner_lesser_right_5", "Pickaxe Attack Speed", "+5% Attack Speed with Pickaxes");
		addSkill("miner_lesser_right_6", "Pickaxe Attack Speed", "+5% Attack Speed with Pickaxes");
		addSkill("miner_lesser_right_7", "Pickaxe Attack Speed", "+5% Attack Speed with Pickaxes");
		addSkill("miner_lesser_right_8", "Pickaxe Attack Speed", "+5% Attack Speed with Pickaxes");
		addSkill("miner_notable_right", "Strong Hands", "+30% chance to ignore Enemy Armor");
		addSkill("miner_lesser_left_4", "Gemstone Bonuses", "+20% to Gemstone bonuses");
		addSkill("miner_lesser_left_5", "Gemstone Bonuses", "+20% to Gemstone bonuses");
		addSkill("miner_lesser_left_6", "Gemstone Bonuses", "+20% to Gemstone bonuses");
		addSkill("miner_lesser_left_7", "Gemstone Bonuses", "+20% to Gemstone bonuses");
		addSkill("miner_lesser_left_8", "Gemstone Bonuses", "+20% to Gemstone bonuses");
		addSkill("miner_notable_left", "Craftsmanship", "You can apply an additional Gemstone");
		addSkill("hunter_class", "Hunter", "+50% chance to Duplicate loot from Monsters");
		addSkill("hunter_lesser_left_1", "Crafted Bows Charge Speed", "+5% Charge Speed of Bows you Craft");
		addSkill("hunter_lesser_left_2", "Crafted Bows Charge Speed", "+10% Charge Speed of Bows you Craft");
		addSkill("hunter_lesser_left_3", "Crafted Bows Charge Speed", "+5% Charge Speed of Bows you Craft");
		addSkill("hunter_lesser_right_1", "Bow Crit Damage", "+5% Crit Damage with Bows");
		addSkill("hunter_lesser_right_2", "Bow Crit Damage", "+10% Crit Damage with Bows");
		addSkill("hunter_lesser_right_3", "Bow Crit Damage", "+5% Crit Damage with Bows");
		addSkill("hunter_mastery", "Master Hunter", "Bows you craft ignore 25% of Enemy Armor");
		addSkill("hunter_lesser_right_4", "Bow Charge Speed", "+3% Bow Charge Speed");
		addSkill("hunter_lesser_right_5", "Bow Charge Speed", "+3% Bow Charge Speed");
		addSkill("hunter_lesser_right_6", "Bow Charge Speed", "+3% Bow Charge Speed");
		addSkill("hunter_lesser_right_7", "Bow Charge Speed", "+3% Bow Charge Speed");
		addSkill("hunter_lesser_right_8", "Bow Charge Speed", "+3% Bow Charge Speed");
		addSkill("hunter_notable_right", "Eagle Eye", "+2% Damage for each Block between you and Enemy");
		addSkill("hunter_lesser_left_4", "Crafted Bows Damage", "+2% Damage of Bows you Craft");
		addSkill("hunter_lesser_left_5", "Crafted Bows Damage", "+2% Damage of Bows you Craft");
		addSkill("hunter_lesser_left_6", "Crafted Bows Damage", "+2% Damage of Bows you Craft");
		addSkill("hunter_lesser_left_7", "Crafted Bows Damage", "+2% Damage of Bows you Craft");
		addSkill("hunter_lesser_left_8", "Crafted Bows Damage", "+2% Damage of Bows you Craft");
		addSkill("hunter_notable_left", "Tight bowstring", "+10% Chance to deal Double Damage with Bows you Craft");
		addSkill("cook_class", "Cook", "+50% Restoration from Food you Cook");
		addSkill("cook_lesser_left_1", "Cooked Food Healing", "Food you Cook heals for 15% of its Restoration");
		addSkill("cook_lesser_left_2", "Cooked Food Healing", "Food you Cook heals for 20% of its Restoration");
		addSkill("cook_lesser_left_3", "Cooked Food Healing", "Food you Cook heals for 15% of its Restoration");
		addSkill("cook_lesser_right_1", "Damage when Full", "+15% Damage when Full");
		addSkill("cook_lesser_right_2", "Damage when Full", "+25% Damage when Full");
		addSkill("cook_lesser_right_3", "Damage when Full", "+15% Damage when Full");
		addSkill("cook_mastery", "Master Cook", "Food you Cook provides 5% Damage per point of Restoration");
		addSkill("cook_lesser_right_4", "Crit Chance when Full", "+5% Crit Chance when Full");
		addSkill("cook_lesser_right_5", "Crit Chance when Full", "+5% Crit Chance when Full");
		addSkill("cook_lesser_right_6", "Crit Chance when Full", "+5% Crit Chance when Full");
		addSkill("cook_lesser_right_7", "Crit Chance when Full", "+5% Crit Chance when Full");
		addSkill("cook_lesser_right_8", "Crit Chance when Full", "+5% Crit Chance when Full");
		addSkill("cook_notable_right", "Gourmet", "+50% Crit Damage if you've Eaten in past minute");
		addSkill("cook_lesser_left_4", "Food Crit Chance", "Food you Cook provides 5% Crit Chance");
		addSkill("cook_lesser_left_5", "Food Crit Chance", "Food you Cook provides 5% Crit Chance");
		addSkill("cook_lesser_left_6", "Food Crit Chance", "Food you Cook provides 5% Crit Chance");
		addSkill("cook_lesser_left_7", "Food Crit Chance", "Food you Cook provides 5% Crit Chance");
		addSkill("cook_lesser_left_8", "Food Crit Chance", "Food you Cook provides 5% Crit Chance");
		addSkill("cook_notable_left", "Banquet", "Food you Cook provides 30% Attack Speed");
		// potions info
		add("potion.superior", "Superior %s");
		add("potion.combined", "Combined Potion");
		// gems info
		add("gemstone.modifier.helmet", "When in Helmet:");
		add("gemstone.modifier.chestplate", "When in Chestplate:");
		add("gemstone.modifier.leggings", "When in Leggings:");
		add("gemstone.modifier.boots", "When in Boots:");
		add("gemstone.modifier.weapon", "When in Weapon:");
		add("gemstone.modifier.shield", "When in Shield:");
		add("gemstone.modifier.bow", "When in Bow:");
		// food info
		add("food.bonus", "When Eaten:");
		add("food.bonus.attack_speed", "+%d%% Attack Speed");
		add("food.bonus.damage", "+%d%% Damage");
		add("food.bonus.crit_chance", "+%d%% Crit Chance");
		// items
		add(SkillTreeItems.SOOTHING_GEMSTONE.get(), "Soothing Gemstone");
		add(SkillTreeItems.STURDY_GEMSTONE.get(), "Heavy Gemstone");
		add(SkillTreeItems.LIGHT_GEMSTONE.get(), "Light Gemstone");
		// attributes
		add(SkillTreeAttributes.HEALTH_REGENERATION_BONUS.get(), "Life Regeneration");
		add(SkillTreeAttributes.HEALING_PER_HIT.get(), "Life per Hit");
		add(SkillTreeAttributes.ARROW_ARMOR_REDUCTION_MULTIPLIER.get(), "Enemy Armor Reduction");
		add(SkillTreeAttributes.ARROW_CRIT_DAMAGE_BONUS.get(), "Arrow Critical Damage");
		add(SkillTreeAttributes.ARROW_DAMAGE_MULTIPLIER.get(), "Arrow Damage");
		add(SkillTreeAttributes.ARROW_DOUBLE_DAMAGE_CHANCE_MULTIPLIER.get(), "Double Damage Chance");
		add(SkillTreeAttributes.BOW_CHARGE_SPEED_MULTIPLIER.get(), "Bow Charge Speed");
		// effects
		add(SkillTreeEffects.DELICACY.get(), "Delicacy");
		add(SkillTreeEffects.GOURMET.get(), "Gourmet");
		// misc
		add("skilltree.message.skillpoint", "Skill point gained. Open skill tree menu to spend it.");
	}

	private void add(Attribute attribute, String name) {
		add(attribute.getDescriptionId(), name);
	}

	private void addSkill(String skillName, String name, String description) {
		var skillId = "skill." + SkillTreeMod.MOD_ID + "." + skillName;
		add(skillId + ".name", name);
		add(skillId + ".description", description);
	}
}
