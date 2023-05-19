package daripher.skilltree.datagen;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeEffects;
import daripher.skilltree.init.SkillTreeItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;

public class ModEnglishTranslationProvider extends LanguageProvider {
	public ModEnglishTranslationProvider(DataGenerator gen) {
		super(gen, SkillTreeMod.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		addSkill("center_node", "Void", "Grants nothing");
		// hunter skills
		addSkill("hunter_class", "Hunter", "+15% chance to Double loot from Monsters");
		addSkillBranch("hunter_offensive_crafting_", "Crafted Bow Speed", "+4% Charge Speed of Bows you Craft", 1, 8);
		addSkillBranch("hunter_defensive_crafting_", "Crafted Armor Evasion", "+1% Evasion from Armor you Craft", 1, 8);
		addSkillBranch("hunter_offensive_combat_", "Arrow Damage", "+1 Arrow Damage", 1, 6);
		addSkillBranch("hunter_defensive_combat_", "Evasion", "+1% Evasion", 1, 6);
		addSkillBranch("hunter_offensive_combat_", "Arrow Crit Damage", "+5% Arrow Crit Damage", 7, 11);
		addSkillBranch("hunter_defensive_combat_", "Movement Speed", "+2% Movement Speed", 7, 11);
		addSkill("hunter_offensive_notable_1", "Skillful Archer", "+20% Arrow Damage");
		addSkill("hunter_defensive_notable_1", "Elusiveness", "+5% Evasion");
		addSkill("hunter_mixed_notable_1", "Swiftness", "+10% Bow Charge Speed/n+5% Movement Speed");
		addSkill("hunter_offensive_keystone_1", "Eagle Eye", "+2% Damage for each Block between you and Enemy");
		addSkill("hunter_defensive_keystone_1", "Fitted Armor", "+1 Armor per Evasion chance");
		addSkillBranch("hunter_lesser_", "Double Loot chance", "+5% chance to Double loot from Monsters", 1, 7);
		addSkill("hunter_life_notable_1", "Movement is Life", "+1 Maximum Life per 3% Evasion");
		addSkill("hunter_mastery", "Treasure Hunter", "+5% chance to Triple loot from Monsters");
		addSkill("hunter_defensive_crafting_keystone_1", "Hatter", "Helmets you craft have an additional Gemstone slot");
		addSkill("hunter_offensive_crafting_keystone_1", "Decorative Bows", "Bows you craft have an additional Gemstone slot");
		// cook skills
		addSkill("cook_class", "Cook", "+25% Restoration from Food you Cook");
		addSkillBranch("cook_offensive_crafting_", "Cooked Food Damage Bonus", "+1% Damage per Restoration point from Food you Cook", 1, 8);
		addSkillBranch("cook_defensive_crafting_", "Cooked Food Regeneration", "+0.1 Life Regeneration per Restoration point from Food you Cook", 1, 8);
		addSkillBranch("cook_offensive_combat_", "Axe Attack Speed", "+10% Attack Speed with Axes", 1, 6);
		addSkillBranch("cook_defensive_combat_", "Block Chance", "+1% Block Chance", 1, 6);
		addSkillBranch("cook_offensive_combat_", "Axe Crit Chance", "+5% Crit Chance with Axes", 7, 11);
		addSkillBranch("cook_defensive_combat_", "Maximum Life if ate Recently", "+1 Maximum Life if you ate Recently", 7, 11);
		addSkill("cook_offensive_notable_1", "Butcher", "+20% Crit Chance with Axes");
		addSkill("cook_defensive_notable_1", "Big Belly", "+5% Block Chance");
		addSkill("cook_mixed_notable_1", "Voracity", "+5 Maximum Life if you ate recently/n+50% Damage if you ate recently");
		addSkill("cook_offensive_keystone_1", "Overweight", "+5% Crit Damage per Satisfied Hunger point");
		addSkill("cook_defensive_keystone_1", "You are what you eat", "+1 Maximum Life per Satisfied Hunger point");
		addSkillBranch("cook_lesser_", "Cooked Food Restoration", "+5% Restoration from Food you Cook", 1, 7);
		addSkill("cook_life_notable_1", "Healty Diet", "+10 Maximum Life if you ate Recently");
		addSkill("cook_mastery", "Chef", "Food you Cook Heals for 50% of its Restoration");
		addSkill("cook_defensive_crafting_keystone_1", "Large Servings", "+60% Restoration from Food you Cook");
		addSkill("cook_offensive_crafting_keystone_1", "Spicy Meal", "+5% Crit Damage per Restoration point from Food you Cook");
		// alchemist skills
		addSkill("alchemist_class", "Alchemist", "+40% Brewed Potions Duration");
		addSkillBranch("alchemist_offensive_crafting_", "Harmful Potions Strength", "+5% chance to Brew better Harmful Potion", 1, 8);
		addSkillBranch("alchemist_defensive_crafting_", "Beneficial Potions Strength", "+5% chance to Brew better Beneficial Potion", 1, 8);
		addSkillBranch("alchemist_offensive_combat_", "Damage against Poisoned", "+20% Damage against Poisoned Enemies", 1, 6);
		addSkillBranch("alchemist_defensive_combat_", "Evasion under Potion Effect", "+2% Evasion under Potion Effect", 1, 6);
		addSkillBranch("alchemist_offensive_combat_", "Attack Speed under Potion Effect", "+3% Attack Speed under Potion Effect", 7, 11);
		addSkillBranch("alchemist_defensive_combat_", "Maximum Life under Potion Effect", "+2 Maximum Life under Potion Effect", 7, 11);
		addSkill("alchemist_offensive_notable_1", "Cruelty", "+30% Crit Chance against Poisoned Enemies");
		addSkill("alchemist_defensive_notable_1", "Adrenalin", "+10% Evasion under Potion Effect");
		addSkill("alchemist_mixed_notable_1", "Addiction", "+10 Maximum Life under Potion Effect/n+20% Damage under Potion Effect");
		addSkill("alchemist_offensive_keystone_1", "Intoxication", "+10% Damage per Potion Effect on you");
		addSkill("alchemist_defensive_keystone_1", "Mutation", "+2 Maximum Life per Potion Effect on you");
		addSkillBranch("alchemist_lesser_", "Brewed Potions Duration", "+10% Brewed Potions Duration", 1, 7);
		addSkill("alchemist_life_notable_1", "Elixir of Life", "+10 Maximum Life under Potion Effect");
		addSkill("alchemist_mastery", "Secret Ingredient", "+100% chance to Brew better Potion");
		addSkill("alchemist_defensive_crafting_keystone_1", "Purity", "+175% chance to Brew better Beneficial Potion");
		addSkill("alchemist_offensive_crafting_keystone_1", "Poisoned Blade", "+75% chance to Brew better Harmful Potion/nYou can apply poisons to weapons");
		// enchanter skills
		addSkill("enchanter_class", "Enchanter", "-30% Level Requirement for Enchantments");
		addSkillBranch("enchanter_offensive_crafting_", "Weapon Enchantment Strenth", "+10% chance to apply better Weapon Enchantment", 1, 8);
		addSkillBranch("enchanter_defensive_crafting_", "Armor Enchantment Strenth", "+10% chance to apply better Armor Enchantment", 1, 8);
		addSkillBranch("enchanter_offensive_combat_", "Damage per Enchantment", "+1% Damage per Enchantment", 1, 6);
		addSkillBranch("enchanter_defensive_combat_", "Armor", "+1 Armor", 1, 6);
		addSkillBranch("enchanter_offensive_combat_", "Crit Chance per Enchantment", "+1% Crit Chance per Enchantment", 7, 11);
		addSkillBranch("enchanter_defensive_combat_", "Maximum Life with Enchtanted Item", "+2 Maximum Life if you have Enchanted Item Equipped", 7, 11);
		addSkill("enchanter_offensive_notable_1", "Embodiment of Power", "+5% Crit Damage per Enchantment");
		addSkill("enchanter_defensive_notable_1", "Mirror Shield", "+10 Armor if your Shield is Echanted");
		addSkill("enchanter_mixed_notable_1", "Knowledge is Power", "+2 Maximum Life per Enchantment/n+5% Damage per Enchantment");
		addSkill("enchanter_offensive_keystone_1", "Excalibur", "+5% Damage per Enchantment Level on your Weapon");
		addSkill("enchanter_defensive_keystone_1", "Celestial Protection", "+2 Armor per Enchantment on your Chestplate");
		addSkillBranch("enchanter_lesser_", "Level Requirement for Enchantments", "-5% Level Requirement for Enchantments", 1, 7);
		addSkill("enchanter_life_notable_1", "Life from Magic", "+1 Maximum Life per Enchantment on your Armor");
		addSkill("enchanter_mastery", "Hidden Knowledge", "+100% chance to apply better Enchantment");
		addSkill("enchanter_defensive_crafting_keystone_1", "Runic Barrier", "+50% chance to apply better Armor Enchantment");
		addSkill("enchanter_offensive_crafting_keystone_1", "Runic Power", "+50% chance to apply better Weapon Enchantment");
		// blacksmith skills
		addSkill("blacksmith_class", "Blacksmith", "+40% Defence from Armor you Craft");
		addSkillBranch("blacksmith_offensive_crafting_", "Crafted Weapon Damage", "+1 Damage of Weapons you Craft", 1, 8);
		addSkillBranch("blacksmith_defensive_crafting_", "Crafted Armor Defense", "+10% Defence from Armor you Craft", 1, 8);
		addSkillBranch("blacksmith_offensive_combat_", "Damage with Shield", "+10% Damage while holding Shield", 1, 6);
		addSkillBranch("blacksmith_defensive_combat_", "Block Chance", "+1% Block Chance", 1, 6);
		addSkillBranch("blacksmith_offensive_combat_", "Crit Chance with Shield", "+5% Crit Chance while holding Shield", 7, 11);
		addSkillBranch("blacksmith_defensive_combat_", "Chestplate Defence", "+4% Armor from your Chestplate", 7, 11);
		addSkill("blacksmith_offensive_notable_1", "Impact", "+25% Crit Damage while holding Shield");
		addSkill("blacksmith_defensive_notable_1", "Iron Shell", "+2 Armor/n+10% Armor");
		addSkill("blacksmith_mixed_notable_1", "Guardian", "+20% Damage while holding Shield/n+10% Armor while holding Shield");
		addSkill("blacksmith_offensive_keystone_1", "Colossus", "Gain damage equal to 10% of your Armor");
		addSkill("blacksmith_defensive_keystone_1", "Unbreakable", "Gain Maximum Life equal to 20% of your Armor");
		addSkillBranch("blacksmith_lesser_", "Tougher Armor chance", "+10% chance to Craft Tougher Armor", 1, 7);
		addSkill("blacksmith_life_notable_1", "Confident Stance", "+1 Maximum Life per Armor point on your Boots");
		addSkill("blacksmith_mastery", "Precious Metal", "+100% chance to Craft Tougher Armor");
		addSkill("blacksmith_defensive_crafting_keystone_1", "Shield Maker", "+5 Armor from Shields you Craft/n+30% Defence from Armor you Craft");
		addSkill("blacksmith_offensive_crafting_keystone_1", "Mystic Alloy", "+25% Attack Speed of Weapons you Craft");
		// miner skills
		addSkill("miner_class", "Miner", "+15% Mining Speed");
		addSkillBranch("miner_offensive_crafting_", "Gemstone Strength in Weapon", "+10% Strength of Gemstones you insert into Weapon", 1, 8);
		addSkillBranch("miner_defensive_crafting_", "Gemstone Strength in Armor", "+10% Strength of Gemstones you insert into Armor", 1, 8);
		addSkillBranch("miner_offensive_combat_", "Pickaxe Damage", "+1 Pickaxe Damage", 1, 6);
		addSkillBranch("miner_defensive_combat_", "Armor", "+1 Armor", 1, 6);
		addSkillBranch("miner_offensive_combat_", "Pickaxe Attack Speed", "+10% Pickaxe Attack Speed", 7, 11);
		addSkillBranch("miner_defensive_combat_", "Movement Speed", "+2% Movement Speed", 7, 11);
		addSkill("miner_offensive_notable_1", "Cursed Stones", "+25% Damage per Gemstone in your Weapon");
		addSkill("miner_defensive_notable_1", "Safety Helmet", "+2 Armor per Gemstone in your Helmet");
		addSkill("miner_mixed_notable_1", "Reliable Steel", "+5 Pickaxe Damage/n+5 Armor");
		addSkill("miner_offensive_keystone_1", "Strong Hands", "+50% Pickaxe Damage");
		addSkill("miner_defensive_keystone_1", "Stone Heart", "+5 Armor per Gemstone in your Chestplate");
		addSkillBranch("miner_lesser_", "Mining Speed", "+5% Mining Speed", 1, 7);
		addSkill("miner_life_notable_1", "Life Crystals", "+1 Maximum Life per Gemstone in your Armor");
		addSkill("miner_mastery", "Jeweler", "You can insert an additional Gemstone into Equipment");
		addSkill("miner_defensive_crafting_keystone_1", "Cullinan Diamond", "+20% Strength of Gemstones you insert into Armor/nYou can insert an additional Gemstone into Chestplates");
		addSkill("miner_offensive_crafting_keystone_1", "Estrela de Fura", "+20% Strength of Gemstones you insert into Weapon/nYou can insert an additional Gemstone into Weapons");
		// potions info
		add("potion.superior", "Superior %s");
		// gems info
		add("gemstone.modifier.helmet", "When in Helmet:");
		add("gemstone.modifier.chestplate", "When in Chestplate:");
		add("gemstone.modifier.leggings", "When in Leggings:");
		add("gemstone.modifier.boots", "When in Boots:");
		add("gemstone.modifier.weapon", "When in Weapon:");
		add("gemstone.modifier.shield", "When in Shield:");
		add("gemstone.modifier.bow", "When in Bow:");
		add("gemstone.empty", "<Empty Gemstone Slot>");
		// food info
		add("food.bonus", "When Eaten:");
		add("food.bonus.damage", "+%d%% Damage");
		add("food.bonus.crit_damage", "+%d%% Crit Damage");
		add("food.bonus.life_regeneration", "+%s Life Regeneration");
		// weapon info
		add("weapon.poisoned", "Poisoned:");
		// items
		add(SkillTreeItems.SOOTHING_GEMSTONE.get(), "Soothing Gemstone");
		addTooltip(SkillTreeItems.SOOTHING_GEMSTONE.get(), "Warm to the touch");
		add(SkillTreeItems.STURDY_GEMSTONE.get(), "Sturdy Gemstone");
		addTooltip(SkillTreeItems.STURDY_GEMSTONE.get(), "Unbreakable");
		add(SkillTreeItems.LIGHT_GEMSTONE.get(), "Light Gemstone");
		addTooltip(SkillTreeItems.LIGHT_GEMSTONE.get(), "Almost weightless");
		add(SkillTreeItems.VOID_GEMSTONE.get(), "Void Gemstone");
		addTooltip(SkillTreeItems.VOID_GEMSTONE.get(), "Seems empty inside");
		add(SkillTreeItems.RAINBOW_GEMSTONE.get(), "Rainbow Gemstone");
		addTooltip(SkillTreeItems.RAINBOW_GEMSTONE.get(), "Its magic is inconsistent");
		add(SkillTreeItems.WISDOM_SCROLL.get(), "Wisdom Scroll");
		// attributes
		add(SkillTreeAttributes.LIFE_REGENERATION_BONUS.get(), "Life Regeneration");
		add(SkillTreeAttributes.LIFE_PER_HIT.get(), "Life per Hit");
		add(SkillTreeAttributes.ARROW_CRIT_DAMAGE_MULTIPLIER.get(), "Arrow Critical Damage");
		add(SkillTreeAttributes.ARROW_DAMAGE_MULTIPLIER.get(), "Arrow Damage");
		add(SkillTreeAttributes.BOW_CHARGE_SPEED_MULTIPLIER.get(), "Bow Charge Speed");
		add(SkillTreeAttributes.EVASION_CHANCE_MULTIPLIER.get(), "Evasion Chance");
		add(SkillTreeAttributes.ARROW_DAMAGE_BONUS.get(), "Arrow Damage");
		add(SkillTreeAttributes.BLOCK_CHANCE_MULTIPLIER.get(), "Block Chance");
		add(SkillTreeAttributes.LIFE_ON_BLOCK.get(), "Life on Block");
		// effects
		add(SkillTreeEffects.DELICACY.get(), "Delicacy");
		add(SkillTreeEffects.CRIT_DAMAGE_BONUS.get(), "Crit Damage");
		add(SkillTreeEffects.DAMAGE_BONUS.get(), "Damage");
		add(SkillTreeEffects.LIFE_REGENERATION_BONUS.get(), "Life Regeneration");
		// system messages
		add("skilltree.message.skillpoint", "Skill point gained. Open skill tree menu to spend it.");
		add("skilltree.message.reset", "Skill Tree has changed. Your skill points have been restored.");
		add("skilltree.message.reset_command", "Your skill tree has been reset.");
		// screen info
		add("widget.skill_point_progress_bar.text", "Gather Experience to gain Skill Points");
		add("widget.skill_point_progress_bar.points", "Points left: %s");
	}

	private void addTooltip(Item item, String tooltip) {
		add(item.getDescriptionId() + ".tooltip", tooltip);
	}

	private void add(Attribute attribute, String name) {
		add(attribute.getDescriptionId(), name);
	}

	private void addSkill(String skillName, String name, String description) {
		var skillId = "skill." + SkillTreeMod.MOD_ID + "." + skillName;
		add(skillId + ".name", name);
		add(skillId + ".description", description);
	}

	private void addSkillBranch(String branchName, String name, String description, int from, int to) {
		for (var node = from; node <= to; node++) {
			addSkill(branchName + node, name, description);
		}
	}
}
