package daripher.skilltree.datagen.translation;

import java.util.Arrays;

import javax.annotation.Nullable;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeAttributes;
import daripher.skilltree.init.SkillTreeEffects;
import daripher.skilltree.init.SkillTreeItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;

public class PSTEnglishTranslationProvider extends LanguageProvider {
	public PSTEnglishTranslationProvider(DataGenerator gen) {
		super(gen, SkillTreeMod.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		addSkill("void", "Void", "Grants Nothing");
		// hunter skills
		addSkill("hunter_class", "Hunter");
		addSkill("hunter_crafting_notable_1", "Thrift");
		addSkill("hunter_defensive_notable_1", "Will to Survive");
		addSkill("hunter_offensive_notable_1", "Skillful Archer");
		addSkill("hunter_life_notable_1", "Movement is Life");
		addSkill("hunter_speed_notable_1", "Rapid Fire");
		addSkill("hunter_healing_notable_1", "Bloodthirsty Arrows");
		addSkill("hunter_crit_notable_1", "Precision");
		addSkill("hunter_defensive_crafting_keystone_1", "Hatter");
		addSkill("hunter_offensive_crafting_keystone_1", "Decorative Bows");
		addSkill("hunter_defensive_keystone_1", "Fitted Armor");
		addSkill("hunter_offensive_keystone_1", "Sniper");
		addSkill("hunter_mastery", "Treasure Hunter");
		addSkill("hunter_offensive_notable_2", "Archery");
		addSkill("hunter_offensive_notable_3", "Magic Arrows");
		addSkill("hunter_offensive_notable_4", "Master of the Sea");
		addSkill("hunter_offensive_notable_5", "Serrated Tips");
		addSkill("hunter_offensive_notable_6", "Soldier Training");
		// cook skills
		addSkill("cook_class", "Cook");
		addSkill("cook_crafting_notable_1", "Ambrosia Fruit");
		addSkill("cook_defensive_notable_1", "Thick Arms");
		addSkill("cook_offensive_notable_1", "Heavy Strike");
		addSkill("cook_life_notable_1", "Healthy Diet");
		addSkill("cook_speed_notable_1", "Energy Reserve");
		addSkill("cook_healing_notable_1", "Snack");
		addSkill("cook_crit_notable_1", "Spicy Meal");
		addSkill("cook_defensive_crafting_keystone_1", "Vegetarianism");
		addSkill("cook_offensive_crafting_keystone_1", "Hot Food");
		addSkill("cook_defensive_keystone_1", "Overweight");
		addSkill("cook_offensive_keystone_1", "Fat Body");
		addSkill("cook_mastery", "Large Servings");
		addSkill("cook_offensive_notable_2", "Healthy Muscles");
		addSkill("cook_offensive_notable_3", "Butcher");
		addSkill("cook_offensive_notable_4", "Spearfishing");
		addSkill("cook_offensive_notable_5", "Meat Cutting");
		addSkill("cook_offensive_notable_6", "Healthy Eyesight");
		// alchemist skills
		addSkill("alchemist_class", "Alchemist");
		addSkill("alchemist_crafting_notable_1", "Experiment");
		addSkill("alchemist_defensive_notable_1", "Improved Reflexes");
		addSkill("alchemist_offensive_notable_1", "Cruelty");
		addSkill("alchemist_life_notable_1", "Addiction");
		addSkill("alchemist_speed_notable_1", "Adrenalin");
		addSkill("alchemist_healing_notable_1", "Blood Potion");
		addSkill("alchemist_crit_notable_1", "Intoxication");
		addSkill("alchemist_defensive_crafting_keystone_1", "Purity");
		addSkill("alchemist_offensive_crafting_keystone_1", "Poisoned Blade");
		addSkill("alchemist_defensive_keystone_1", "Mutation");
		addSkill("alchemist_offensive_keystone_1", "Overdose");
		addSkill("alchemist_mastery", "Secret Ingredient");
		addSkill("alchemist_offensive_notable_2", "Assasin");
		addSkill("alchemist_offensive_notable_3", "Ranger");
		addSkill("alchemist_offensive_notable_4", "Poison Gas");
		addSkill("alchemist_offensive_notable_5", "Berserker Potion");
		addSkill("alchemist_offensive_notable_6", "Toxic Coating");
		// enchanter skills
		addSkill("enchanter_class", "Enchanter");
		addSkill("enchanter_crafting_notable_1", "Magic Flow");
		addSkill("enchanter_defensive_notable_1", "Runic Barrier");
		addSkill("enchanter_offensive_notable_1", "Runic Blade");
		addSkill("enchanter_life_notable_1", "Life from Magic");
		addSkill("enchanter_speed_notable_1", "Animate Weapon");
		addSkill("enchanter_healing_notable_1", "Energy Prism");
		addSkill("enchanter_crit_notable_1", "Reaper");
		addSkill("enchanter_defensive_crafting_keystone_1", "Protection Rune");
		addSkill("enchanter_offensive_crafting_keystone_1", "Destruction Rune");
		addSkill("enchanter_defensive_keystone_1", "Aegis");
		addSkill("enchanter_offensive_keystone_1", "Excalibur");
		addSkill("enchanter_mastery", "Hidden Knowledge");
		addSkill("enchanter_offensive_notable_2", "Runic Swords");
		addSkill("enchanter_offensive_notable_3", "Runic Bows");
		addSkill("enchanter_offensive_notable_4", "Runic Tridents");
		addSkill("enchanter_offensive_notable_5", "Runic Crossbows");
		addSkill("enchanter_offensive_notable_6", "Runic Axes");
		// blacksmith skills
		addSkill("blacksmith_class", "Blacksmith");
		addSkill("blacksmith_crafting_notable_1", "Shield Maker");
		addSkill("blacksmith_defensive_notable_1", "Iron Shell");
		addSkill("blacksmith_offensive_notable_1", "Counterweight");
		addSkill("blacksmith_life_notable_1", "Confident Stance");
		addSkill("blacksmith_speed_notable_1", "Ambidexter");
		addSkill("blacksmith_healing_notable_1", "Shelter");
		addSkill("blacksmith_crit_notable_1", "Impact");
		addSkill("blacksmith_defensive_crafting_keystone_1", "Precious Metal");
		addSkill("blacksmith_offensive_crafting_keystone_1", "Light Alloy");
		addSkill("blacksmith_defensive_keystone_1", "Living Fortress");
		addSkill("blacksmith_offensive_keystone_1", "Colossus");
		addSkill("blacksmith_mastery", "Black Steel");
		addSkill("blacksmith_offensive_notable_2", "Soldier");
		addSkill("blacksmith_offensive_notable_3", "Guardian");
		addSkill("blacksmith_offensive_notable_4", "Sea Warrior");
		addSkill("blacksmith_offensive_notable_5", "Slinger");
		addSkill("blacksmith_offensive_notable_6", "Viking");
		// miner skills
		addSkill("miner_class", "Miner");
		addSkill("miner_crafting_notable_1", "Excavation");
		addSkill("miner_defensive_notable_1", "Safety Helmet");
		addSkill("miner_offensive_notable_1", "Polishing");
		addSkill("miner_life_notable_1", "Life Crystal");
		addSkill("miner_speed_notable_1", "Light Crystals");
		addSkill("miner_healing_notable_1", "Healing Crystal");
		addSkill("miner_crit_notable_1", "Cursed Stone");
		addSkill("miner_defensive_crafting_keystone_1", "Cullinan");
		addSkill("miner_offensive_crafting_keystone_1", "Estrela de Fura");
		addSkill("miner_defensive_keystone_1", "Stone Heart");
		addSkill("miner_offensive_keystone_1", "Precious Weapon");
		addSkill("miner_mastery", "Jeweler");
		addSkill("miner_offensive_notable_2", "Diversity");
		addSkill("miner_offensive_notable_3", "Precious Blades");
		addSkill("miner_offensive_notable_4", "Inconsistency");
		addSkill("miner_offensive_notable_5", "Precious Arrows");
		addSkill("miner_offensive_notable_6", "Monotony");
		// potions info
		add("potion.superior", "Superior %s");
		add("item.minecraft.potion.mixture", "Mixture");
		add("item.minecraft.splash_potion.mixture", "Splash Mixture");
		add("item.minecraft.lingering_potion.mixture", "Lingering Mixture");
		addMixture("Diving", MobEffects.NIGHT_VISION, MobEffects.WATER_BREATHING);
		addMixture("Eternal Youth", MobEffects.HEAL, MobEffects.REGENERATION);
		addMixture("Sickness", MobEffects.POISON, MobEffects.WEAKNESS);
		addMixture("Owl", MobEffects.INVISIBILITY, MobEffects.NIGHT_VISION);
		addMixture("Coward", MobEffects.INVISIBILITY, MobEffects.MOVEMENT_SPEED);
		addMixture("Dragon Blood", MobEffects.FIRE_RESISTANCE, MobEffects.REGENERATION);
		addMixture("Demon", MobEffects.FIRE_RESISTANCE, MobEffects.DAMAGE_BOOST);
		addMixture("Assasin", MobEffects.HARM, MobEffects.POISON);
		addMixture("Antigravity", MobEffects.JUMP, MobEffects.SLOW_FALLING);
		addMixture("Aging", MobEffects.MOVEMENT_SLOWDOWN, MobEffects.WEAKNESS);
		addMixture("Athlete", MobEffects.JUMP, MobEffects.MOVEMENT_SPEED);
		addMixture("Thief", MobEffects.INVISIBILITY, MobEffects.LUCK);
		addMixture("Treasure Hunter", MobEffects.LUCK, MobEffects.WATER_BREATHING);
		addMixture("Knight", MobEffects.REGENERATION, MobEffects.DAMAGE_BOOST);
		addMixture("Slow Motion", MobEffects.SLOW_FALLING, MobEffects.MOVEMENT_SLOWDOWN);
		addMixture("Soldier", MobEffects.HEAL, MobEffects.DAMAGE_BOOST);
		addMixture("Ninja", MobEffects.DAMAGE_BOOST, MobEffects.MOVEMENT_SPEED);
		addMixture("Blessing", MobEffects.LUCK, MobEffects.DAMAGE_BOOST);
		addMixture("Plague", MobEffects.POISON, MobEffects.MOVEMENT_SLOWDOWN);
		// gems info
		add("gem.socket", "[Empty Socket]");
		add("gem.additional_socket", "• Has an additional socket.");
		add("gem.disabled", "Disabled with Apotheosis adventure module enabled.");
		add("gem.slot.helmet", "• Helmets: ");
		add("gem.slot.chestplate", "• Chestplates: ");
		add("gem.slot.boots", "• Boots: ");
		add("gem.slot.other_armor", "• Other Armor: ");
		add("gem.slot.weapon", "• Weapons: ");
		add("gem.slot.shield", "• Shields: ");
		add("gem.slot.bow", "• Bows: ");
		add("gem.slot.melee_weapon", "• Melee Weapons: ");
		add("gem.slot.armor_and_shield", "• Armor and Shields: ");
		add("gem.slot.anything", "• Anything: ");
		add("gem.slot.armor", "• Armor: ");
		add("gem.slot.pickaxe", "• Pickaxes: ");
		add("gem.slots", "Fits in:");
		addTooltip(SkillTreeItems.VACUCITE.get(), "Consumes gems from item");
		addTooltip(SkillTreeItems.IRISCITE.get(), "Outcome unpredictable");
		// food info
		add("food.bonus.damage", "+%d%% Damage");
		add("food.bonus.crit_damage", "+%d%% Crit Damage");
		add("food.bonus.life_regeneration", "+%s Life Regeneration");
		// weapon info
		add("weapon.poisoned", "Poisoned:");
		// items
		add(SkillTreeItems.ADAMITE.get(), "Adamite");
		add(SkillTreeItems.CITRINE.get(), "Citrine");
		add(SkillTreeItems.IRISCITE.get(), "Iriscite");
		add(SkillTreeItems.MOONSTONE.get(), "Moonstone");
		add(SkillTreeItems.ONYX.get(), "Onyx");
		add(SkillTreeItems.OPAL.get(), "Opal");
		add(SkillTreeItems.RUBY.get(), "Ruby");
		add(SkillTreeItems.VACUCITE.get(), "Vacucite");
		add(SkillTreeItems.WISDOM_SCROLL.get(), "Wisdom Scroll");
		addTooltip(SkillTreeItems.WISDOM_SCROLL.get(), "Grants one passive skill point");
		add(SkillTreeItems.AMNESIA_SCROLL.get(), "Amnesia Scroll");
		addTooltip(SkillTreeItems.AMNESIA_SCROLL.get(), "Resets your passive skill tree");
		// attributes
		add(SkillTreeAttributes.LIFE_REGENERATION.get(), "Life Regeneration");
		add(SkillTreeAttributes.LIFE_PER_HIT.get(), "Life per Hit");
		add(SkillTreeAttributes.PROJECTILE_CRIT_DAMAGE.get(), "Projectile Critical Damage");
		add(SkillTreeAttributes.PROJECTILE_DAMAGE.get(), "Projectile Damage");
		add(SkillTreeAttributes.EVASION_CHANCE.get(), "Evasion Chance");
		add(SkillTreeAttributes.BLOCK_CHANCE.get(), "Block Chance");
		add(SkillTreeAttributes.LIFE_ON_BLOCK.get(), "Life on Block");
		add(SkillTreeAttributes.CRIT_CHANCE.get(), "Critical Hit Chance");
		add(SkillTreeAttributes.DOUBLE_LOOT_CHANCE.get(), "Double Loot Chance");
		add(SkillTreeAttributes.TRIPLE_LOOT_CHANCE.get(), "Triple Loot Chance");
		add(SkillTreeAttributes.CRAFTED_ARMOR_EVASION.get(), "Crafted Armor Evasion Chance");
		add(SkillTreeAttributes.CRAFTED_RANGED_WEAPON_ADDITIONAL_SOCKETS.get(), "Crafted Ranged Weapon Sockets");
		add(SkillTreeAttributes.CRAFTED_HELMETS_ADDITIONAL_GEMSTONE_SLOTS.get(), "Crafted Helmets Sockets");
		add(SkillTreeAttributes.CRAFTED_RANGED_WEAPON_ATTACK_SPEED.get(), "Crafted Ranged Weapon Attack Speed");
		add(SkillTreeAttributes.EVASION_CHANCE_WHEN_WOUNDED.get(), "Evasion Chance when Wounded");
		add(SkillTreeAttributes.ARMOR_PER_EVASION.get(), "Armor per Evasion Chance");
		add(SkillTreeAttributes.DAMAGE_PER_DISTANCE.get(), "Damage per Distance to Enemy");
		add(SkillTreeAttributes.LIFE_PER_PROJECTILE_HIT.get(), "Life per Projectile Hit");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_EVASION.get(), "Maximum Life per Evasion Chance");
		add(SkillTreeAttributes.ATTACK_SPEED_WITH_RANGED_WEAPON.get(), "Attack Speed with Ranged Weapon");
		add(SkillTreeAttributes.CHANCE_TO_RETRIEVE_ARROWS.get(), "Chance to retrieve Arrows");
		add(SkillTreeAttributes.PROJECTILE_CRIT_CHANCE.get(), "Projectile Critical Hit Chance");
		add(SkillTreeAttributes.MAXIMUM_LIFE_UNDER_POTION_EFFECT.get(), "Maximum Life under Potion Effect");
		add(SkillTreeAttributes.ATTACK_SPEED_UNDER_POTION_EFFECT.get(), "Attack Speed under Potion Effect");
		add(SkillTreeAttributes.BREWED_POTIONS_DURATION.get(), "Brewed Potions Duration");
		add(SkillTreeAttributes.CHANCE_TO_BREW_STRONGER_POTION.get(), "Brewed Potions Strength");
		add(SkillTreeAttributes.CHANCE_TO_BREW_STRONGER_BENEFICIAL_POTION.get(), "Brewed Beneficial Potions Strength");
		add(SkillTreeAttributes.CHANCE_TO_BREW_STRONGER_HARMFUL_POTION.get(), "Brewed Harmful Potions Strength");
		add(SkillTreeAttributes.DAMAGE_AGAINST_POISONED.get(), "Damage against Poisoned");
		add(SkillTreeAttributes.CRIT_CHANCE_AGAINST_POISONED.get(), "Critical Hit Chance against Poisoned");
		add(SkillTreeAttributes.EVASION_UNDER_POTION_EFFECT.get(), "Evasion Chance under Potion Effect");
		add(SkillTreeAttributes.CAN_POISON_WEAPONS.get(), "You can apply Poisons on Melee Weapons");
		add(SkillTreeAttributes.CAN_MIX_POTIONS.get(), "You can mix Potions together");
		add(SkillTreeAttributes.EVASION_PER_POTION_EFFECT.get(), "Evasion Chance per Potion Effect");
		add(SkillTreeAttributes.DAMAGE_PER_POTION_EFFECT.get(), "Damage per Potion Effect");
		add(SkillTreeAttributes.LIFE_PER_HIT_UNDER_POTION_EFFECT.get(), "Life per Hit under Potion Effect");
		add(SkillTreeAttributes.CRIT_DAMAGE_AGAINST_POISONED.get(), "Critical Hit Damage against Poisoned");
		add(SkillTreeAttributes.GEM_POWER_IN_ARMOR.get(), "Gem Power in Armor");
		add(SkillTreeAttributes.GEM_POWER_IN_WEAPON.get(), "Gem Power in Weapon");
		add(SkillTreeAttributes.MAXIMUM_WEAPON_SOCKETS.get(), "Weapon Gem Sockets");
		add(SkillTreeAttributes.MAXIMUM_CHESTPLATE_SOCKETS.get(), "Chestplate Gem Sockets");
		add(SkillTreeAttributes.MAXIMUM_SOCKETS.get(), "Equipment Gem Sockets");
		add(SkillTreeAttributes.ARMOR_PER_GEM_IN_HELMET.get(), "Armor per Gem in Helmet");
		add(SkillTreeAttributes.ARMOR_PER_GEM_IN_CHESTPLATE.get(), "Armor per Gem in Chestplate");
		add(SkillTreeAttributes.CRIT_CHANCE_PER_GEM_IN_WEAPON.get(), "Critical Hit Chance per Gem in Weapon");
		add(SkillTreeAttributes.MINING_SPEED.get(), "Mining Speed");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_GEM_IN_HELMET.get(), "Maximum Life per Gem in Helmet");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_GEM_IN_ARMOR.get(), "Maximum Life per Gem in Armor");
		add(SkillTreeAttributes.CRIT_DAMAGE_PER_GEM_IN_WEAPON.get(), "Critical Hit Damage per Gem in Weapon");
		add(SkillTreeAttributes.GEM_DROP_CHANCE.get(), "Chance to find Gem in Ore");
		add(SkillTreeAttributes.LIFE_REGENERATION_PER_GEM_IN_HELMET.get(), "Life Regeneration per Gem in Helmet");
		add(SkillTreeAttributes.CRAFTED_ARMOR_DEFENCE.get(), "Crafted Armor Defence");
		add(SkillTreeAttributes.CRAFTED_SHIELDS_ARMOR_BONUS.get(), "Crafted Shields Armor");
		add(SkillTreeAttributes.LIFE_REGENERATION_WITH_SHIELD.get(), "Life Regeneration with Shield");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_BOOTS_ARMOR.get(), "Maximum Life per Armor from Boots");
		add(SkillTreeAttributes.ATTACK_SPEED_WITH_SHIELD.get(), "Attack Speed with Shield");
		add(SkillTreeAttributes.CRAFTED_MELEE_WEAPON_DAMAGE_BONUS.get(), "Crafted Melee Weapon Damage");
		add(SkillTreeAttributes.CRAFTED_MELEE_WEAPON_ATTACK_SPEED.get(), "Crafted Melee Weapon Attack Speed");
		add(SkillTreeAttributes.CRIT_DAMAGE_WITH_SHIELD.get(), "Critical Hit Damage with Shield");
		add(SkillTreeAttributes.CRIT_CHANCE_WITH_SHIELD.get(), "Critical Hit Chance with Shield");
		add(SkillTreeAttributes.DAMAGE_WITH_SHIELD.get(), "Damage with Shield");
		add(SkillTreeAttributes.CHANCE_TO_CRAFT_TOUGHER_ARMOR.get(), "Chance to Craft Tougher Armor");
		add(SkillTreeAttributes.ATTACK_DAMAGE_PER_ARMOR.get(), "Attack Damage per Armor");
		add(SkillTreeAttributes.CHESTPLATE_ARMOR.get(), "Chestplate Armor");
		add(SkillTreeAttributes.ENCHANTMENT_LEVEL_REQUIREMENT_REDUCTION.get(), "Enchantment Level Requirement reduction");
		add(SkillTreeAttributes.CHANCE_TO_APPLY_BETTER_ARMOR_ENCHANTMENT.get(), "Applied Armor Enchantments Strength");
		add(SkillTreeAttributes.CHANCE_TO_APPLY_BETTER_WEAPON_ENCHANTMENT.get(), "Applied Weapon Enchantments Strength");
		add(SkillTreeAttributes.DAMAGE_WITH_ENCHANTED_WEAPON.get(), "Damage with Enchanted Weapon");
		add(SkillTreeAttributes.MAXIMUM_LIFE_WITH_ENCHANTED_ITEM.get(), "Maximum Life with Enchanted Item");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_ARMOR_ENCHANTMENT.get(), "Maximum Life per Armor Enchantment");
		add(SkillTreeAttributes.ATTACK_SPEED_WITH_ENCHANTED_WEAPON.get(), "Attack Speed with Enchanted Weapon");
		add(SkillTreeAttributes.CHANCE_TO_APPLY_BETTER_ENCHANTMENT.get(), "Chance to apply better Enchantment");
		add(SkillTreeAttributes.CRIT_CHANCE_WITH_ENCHANTED_WEAPON.get(), "Critical Hit Chance with Enchanted Weapon");
		add(SkillTreeAttributes.CRIT_DAMAGE_PER_WEAPON_ENCHANTMENT.get(), "Critical Hit Damage per Weapon Enchantment");
		add(SkillTreeAttributes.LIFE_ON_BLOCK_PER_SHIELD_ENCHANTMENT.get(), "Life on Block per Shield Enchantment");
		add(SkillTreeAttributes.BLOCK_CHANCE_PER_SHIELD_ENCHANTMENT.get(), "Block Chance per Shield Enchantment");
		add(SkillTreeAttributes.DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL.get(), "Damage per Weapon Enchantment Level");
		add(SkillTreeAttributes.BLOCK_CHANCE_WITH_ENCHANTED_SHIELD.get(), "Block Chance with Enchanted Shield");
		add(SkillTreeAttributes.FREE_ENCHANTMENT_CHANCE.get(), "Free Enchantment Chance");
		add(SkillTreeAttributes.COOKED_FOOD_SATURATION.get(), "Cooked Food Saturation");
		add(SkillTreeAttributes.COOKED_FOOD_LIFE_REGENERATION.get(), "Cooked Food Life Regeneration");
		add(SkillTreeAttributes.COOKED_FOOD_HEALING_PER_SATURATION.get(), "Cooked Food Healing per Saturation Point");
		add(SkillTreeAttributes.COOKED_FOOD_DAMAGE_PER_SATURATION.get(), "Cooked Food Damage per Saturation Point");
		add(SkillTreeAttributes.DAMAGE_IF_NOT_HUNGRY.get(), "Damage if not Hungry");
		add(SkillTreeAttributes.BLOCK_CHANCE_IF_NOT_HUNGRY.get(), "Block Chance if not Hungry");
		add(SkillTreeAttributes.LIFE_ON_BLOCK_IF_NOT_HUNGRY.get(), "Life on Block if not Hungry");
		add(SkillTreeAttributes.CRIT_CHANCE_IF_NOT_HUNGRY.get(), "Critical Hit Chance if not Hungry");
		add(SkillTreeAttributes.MAXIMUM_LIFE_IF_NOT_HUNGRY.get(), "Maximum Life if not Hungry");
		add(SkillTreeAttributes.MAXIMUM_LIFE_PER_SATISFIED_HUNGER.get(), "Maximum Life per Satisfield Hunger Point");
		add(SkillTreeAttributes.ATTACK_SPEED_IF_NOT_HUNGRY.get(), "Attack Speed if not Hungry");
		add(SkillTreeAttributes.BLOCK_CHANCE_PER_SATISFIED_HUNGER.get(), "Block Chance per Satisfied Hunger Point");
		add(SkillTreeAttributes.DAMAGE_PER_SATISFIED_HUNGER.get(), "Damage per Satisfied Hunger Point");
		add(SkillTreeAttributes.COOKED_FOOD_CRITICAL_DAMAGE_PER_SATURATION.get(), "Cooked Food Critical Hit Damage per Saturation Point");
		add(SkillTreeAttributes.CRIT_DAMAGE_PER_SATISFIED_HUNGER.get(), "Critical Hit Damage per Satisfied Hunger Point");
		add(SkillTreeAttributes.CRAFTED_EQUIPMENT_DURABILITY.get(), "Crafted Equipment Durability");
		add(SkillTreeAttributes.DAMAGE_WITH_GEM_IN_WEAPON.get(), "Damage with Gem in Weapon");
		add(SkillTreeAttributes.DAMAGE_PER_GEM_IN_WEAPON.get(), "Damage per Gem in Weapon");
		add(SkillTreeAttributes.ATTACK_SPEED_WITH_GEM_IN_WEAPON.get(), "Attack Speed with Gem in Weapon");
		add(SkillTreeAttributes.ATTACK_SPEED_PER_GEM_IN_WEAPON.get(), "Attack Speed per Gem in Weapon");
		add(SkillTreeAttributes.DAMAGE_WITH_POISONED_WEAPON.get(), "Damage with Poisoned Weapon");
		add(SkillTreeAttributes.PROJECTILE_DAMAGE_AGAINST_POISONED.get(), "Projectile Damage against Poisoned");
		add(SkillTreeAttributes.SPLASH_POTION_DAMAGE.get(), "Splash Potion Damage");
		add(SkillTreeAttributes.DAMAGE_PER_ADAMITE_IN_WEAPON.get(), "Damage per Adamite in Weapon");
		add(SkillTreeAttributes.BOW_DAMAGE.get(), "Bow Damage");
		add(SkillTreeAttributes.TIPPED_ARROW_DAMAGE.get(), "Tipped Arrow Damage");
		add(SkillTreeAttributes.ARROW_DAMAGE.get(), "Arrow Damage");
		add(SkillTreeAttributes.TRIDENT_DAMAGE.get(), "Trident Damage");
		add(SkillTreeAttributes.CROSSBOW_DAMAGE.get(), "Crossbow Damage");
		add(SkillTreeAttributes.ENCHANTED_SWORD_DAMAGE.get(), "Enchanted Sword Damage");
		add(SkillTreeAttributes.ENCHANTED_BOW_DAMAGE.get(), "Enchanted Bow Damage");
		add(SkillTreeAttributes.ENCHANTED_TRIDENT_DAMAGE.get(), "Enchanted Trident Damage");
		add(SkillTreeAttributes.ENCHANTED_CROSSBOW_DAMAGE.get(), "Enchanted Crossbow Damage");
		add(SkillTreeAttributes.ENCHANTED_AXE_DAMAGE.get(), "Enchanted Axe Damage");
		add(SkillTreeAttributes.MELEE_DAMAGE_IF_NOT_HUNGRY.get(), "Melee Damage if not Hungry");
		add(SkillTreeAttributes.AXE_DAMAGE.get(), "Axe Damage");
		add(SkillTreeAttributes.SWORD_DAMAGE.get(), "Sword Damage");
		add(SkillTreeAttributes.PROJECTILE_DAMAGE_IF_NOT_HUNGRY.get(), "Projectile Damage if not Hungry");
		add(SkillTreeAttributes.MELEE_DAMAGE_WITH_SHIELD.get(), "Melee Damage with Shield");
		add(SkillTreeAttributes.PROJECTILE_DAMAGE_WITH_SHIELD.get(), "Projectile Damage with Shield");
		add(SkillTreeAttributes.DAMAGE_WITH_DIFFERENT_WEAPON_GEMS.get(), "Damage with Different Gems in Weapon");
		add(SkillTreeAttributes.DAMAGE_WITH_SAME_WEAPON_GEMS.get(), "Damage with Same Gems in Weapon");
		add(SkillTreeAttributes.MELEE_DAMAGE_PER_GEM_IN_WEAPON.get(), "Melee Damage per Gem in Weapon");
		add(SkillTreeAttributes.PROJECTILE_DAMAGE_PER_GEM_IN_WEAPON.get(), "Projectile Damage per Gem in Weapon");
		add(SkillTreeAttributes.DAMAGE_PER_IRISCITE_IN_WEAPON.get(), "Damage per Iriscite in Weapon");
		add(SkillTreeAttributes.DAMAGE_UNDER_POTION_EFFECT.get(), "Damage under Potion Effect");
		// effects
		add(SkillTreeEffects.CRIT_DAMAGE_BONUS.get(), "Crit Damage");
		add(SkillTreeEffects.DAMAGE_BONUS.get(), "Damage");
		add(SkillTreeEffects.LIFE_REGENERATION_BONUS.get(), "Life Regeneration");
		// system messages
		add("skilltree.message.reset", "Skill Tree has changed. Your skill points have been restored.");
		add("skilltree.message.reset_command", "Your skill tree has been reset.");
		add("skilltree.message.point_command", "Skill point gained.");
		// screen info
		add("widget.skill_points_left", "Points left: %s");
		add("widget.skill_button.not_learned", "Skill not learned");
		add("widget.buy_skill_button", "Buy Skill Point");
		add("key.categories.skilltree", "Passive Skill Tree");
		add("key.display_skill_tree", "Open Skill Tree");
		// apotheosis compatibility
		add("item.apotheosis.gem.skilltree:ruby", "Ruby");
		add("item.apotheosis.gem.skilltree:onyx", "Onyx");
		add("item.apotheosis.gem.skilltree:moonstone", "Moonstone");
		add("item.apotheosis.gem.skilltree:opal", "Opal");
		add("item.apotheosis.gem.skilltree:adamite", "Adamite");
		add("item.apotheosis.gem.skilltree:third_eye", "Third Eye");
		add("item.apotheosis.gem.skilltree:citrine", "Citrine");
		add("gem_class.any_armor", "Any Armor");
		add("gem_class.armor_or_shield", "Armor or Shields");
		add("gem_class.any_weapon", "Any Weapon");
		add("gem_class.other_armor", "Other Armor");
		add("gem_class.other_weapon", "Other Weapon");
		add("gem_class.pickaxe", "Pickaxes");
		// jei compatibility
		add("skilltree.jei.gem_info", "Gems can be inserted into items with sockets on a smithing table. Drop from any ore with a small chance (requires no silk touch tool).");
	}

	private void addTooltip(Item item, String tooltip) {
		add(item.getDescriptionId() + ".tooltip", tooltip);
	}

	private void add(Attribute attribute, String name) {
		add(attribute.getDescriptionId(), name);
	}

	private void addSkill(String skillName, String name, @Nullable String description) {
		var skillId = "skill." + SkillTreeMod.MOD_ID + "." + skillName;
		add(skillId + ".name", name);
		if (description != null) {
			add(skillId + ".description", description);
		}
	}

	private void addSkill(String skillName, String name) {
		addSkill(skillName, name, null);
	}

	private void addMixture(String name, MobEffect... effects) {
		name = "Mixture of " + name;
		addMixture(name, "potion", effects);
		addMixture("Splash " + name, "splash_potion", effects);
		addMixture("Lingering " + name, "lingering_potion", effects);
	}

	protected void addMixture(String name, String potionType, MobEffect... effects) {
		var potionName = new StringBuilder("item.minecraft." + potionType + ".mixture");
		Arrays.asList(effects).stream().map(MobEffect::getDescriptionId).map(id -> id.replaceAll("effect.", "")).forEach(id -> potionName.append("." + id));
		add(potionName.toString(), name);
	}
}
