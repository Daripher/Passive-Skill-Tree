package daripher.skilltree.datagen.translation;

import java.util.Arrays;

import javax.annotation.Nullable;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.init.PSTEffects;
import daripher.skilltree.init.PSTItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;
import top.theillusivec4.curios.common.CuriosHelper;

public class PSTEnglishTranslationProvider extends LanguageProvider {
	public PSTEnglishTranslationProvider(DataGenerator generator) {
		super(generator.getPackOutput(), SkillTreeMod.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
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
		addSkill("hunter_gateway", "Dimensional Gateway", "Connects to Dimensional Gateway");
		// ranger skills
		addSkill("hunter_subclass_1", "Ranger");
		addSkill("hunter_subclass_1_mastery", "Elusiveness");
		addSkill("hunter_subclass_1_crafting_notable_1", "Soft Soles");
		addSkill("hunter_subclass_1_offensive_notable_1", "Without a Trace");
		addSkill("hunter_subclass_special", "Bloodthirsty Blade");
		// fletcher skills
		addSkill("hunter_subclass_2", "Fletcher");
		addSkill("hunter_subclass_2_mastery", "Bottomless Quiver");
		addSkill("hunter_subclass_2_crafting_notable_1", "Lightweight Arrows");
		addSkill("hunter_subclass_2_life_notable_1", "Confidence");
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
		addSkill("cook_gateway", "Spiritual Gateway", "Connects to Spiritual Gateway");
		// berserker skills
		addSkill("cook_subclass_1", "Berserker");
		addSkill("cook_subclass_1_mastery", "Blood Veil");
		addSkill("cook_subclass_1_crafting_notable_1", "Headsman's Axe");
		addSkill("cook_subclass_1_offensive_notable_1", "Verge of Death");
		addSkill("cook_subclass_special", "Studying Remains");
		// fisherman skills
		addSkill("cook_subclass_2", "Fisherman");
		addSkill("cook_subclass_2_mastery", "Sea Gift");
		addSkill("cook_subclass_2_crafting_notable_1", "Experienced Fisherman");
		addSkill("cook_subclass_2_life_notable_1", "Lucky Fisherman");
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
		addSkill("alchemist_offensive_crafting_keystone_1", "Persistent Toxin");
		addSkill("alchemist_defensive_keystone_1", "Mutation");
		addSkill("alchemist_offensive_keystone_1", "Overdose");
		addSkill("alchemist_mastery", "Secret Ingredient");
		addSkill("alchemist_gateway", "Spiritual Gateway", "Connects to Spiritual Gateway");
		// assassin skills
		addSkill("alchemist_subclass_1", "Assassin");
		addSkill("alchemist_subclass_1_mastery", "Gutting");
		addSkill("alchemist_subclass_1_crafting_notable_1", "Poisoner");
		addSkill("alchemist_subclass_1_offensive_notable_1", "Backstab");
		addSkill("alchemist_subclass_special", "Spiked Rings");
		// healer skills
		addSkill("alchemist_subclass_2", "Healer");
		addSkill("alchemist_subclass_2_mastery", "Self-Treatment");
		addSkill("alchemist_subclass_2_crafting_notable_1", "Panacea");
		addSkill("alchemist_subclass_2_life_notable_1", "Strong Health");
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
		addSkill("enchanter_gateway", "Astral Gateway", "Connects to Astral Gateway");
		// arsonist skills
		addSkill("enchanter_subclass_1", "Arsonist");
		addSkill("enchanter_subclass_1_mastery", "Incineration");
		addSkill("enchanter_subclass_1_crafting_notable_1", "Flaming Blade");
		addSkill("enchanter_subclass_1_offensive_notable_1", "Scorched Flesh");
		addSkill("enchanter_subclass_special", "Infernal Quivers");
		// scholar skills
		addSkill("enchanter_subclass_2", "Scholar");
		addSkill("enchanter_subclass_2_mastery", "Studying Stars");
		addSkill("enchanter_subclass_2_crafting_notable_1", "Studying Minerals");
		addSkill("enchanter_subclass_2_life_notable_1", "Meditation");
		// blacksmith skills
		addSkill("blacksmith_class", "Blacksmith");
		addSkill("blacksmith_crafting_notable_1", "Shield Maker");
		addSkill("blacksmith_defensive_notable_1", "Iron Shell");
		addSkill("blacksmith_offensive_notable_1", "Counterweight");
		addSkill("blacksmith_life_notable_1", "Confident Stance");
		addSkill("blacksmith_speed_notable_1", "Ambidexter");
		addSkill("blacksmith_healing_notable_1", "Shelter");
		addSkill("blacksmith_crit_notable_1", "Impact");
		addSkill("blacksmith_defensive_crafting_keystone_1", "Strong Metal");
		addSkill("blacksmith_offensive_crafting_keystone_1", "Light Alloy");
		addSkill("blacksmith_defensive_keystone_1", "Living Fortress");
		addSkill("blacksmith_offensive_keystone_1", "Colossus");
		addSkill("blacksmith_mastery", "Black Steel");
		addSkill("blacksmith_gateway", "Dimensional Gateway", "Connects to Dimensional Gateway");
		// soldier skills
		addSkill("blacksmith_subclass_1", "Soldier");
		addSkill("blacksmith_subclass_1_mastery", "Military Training");
		addSkill("blacksmith_subclass_1_crafting_notable_1", "Sharpening");
		addSkill("blacksmith_subclass_1_offensive_notable_1", "Experienced Fighter");
		addSkill("blacksmith_subclass_special", "Greedy Blades");
		// artisan skills
		addSkill("blacksmith_subclass_2", "Artisan");
		addSkill("blacksmith_subclass_2_mastery", "Handyman");
		addSkill("blacksmith_subclass_2_crafting_notable_1", "Lightweight Shields");
		addSkill("blacksmith_subclass_2_life_notable_1", "Tempered in Blood");
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
		addSkill("miner_mastery", "Greed");
		addSkill("miner_gateway", "Astral Gateway", "Connects to Astral Gateway");
		// explorer skills
		addSkill("miner_subclass_1", "Explorer");
		addSkill("miner_subclass_1_mastery", "Discoverer");
		addSkill("miner_subclass_1_crafting_notable_1", "Seven-League Boots");
		addSkill("miner_subclass_1_offensive_notable_1", "Haste");
		addSkill("miner_subclass_special", "Decorative Boots");
		// jeweler skills
		addSkill("miner_subclass_2", "Jeweler");
		addSkill("miner_subclass_2_mastery", "Aristocrat");
		addSkill("miner_subclass_2_crafting_notable_1", "Star Shards");
		addSkill("miner_subclass_2_life_notable_1", "Talisman");
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
		add("gem.socket", "Empty Socket");
		add("gem.additional_socket_1", "• Has an additional socket");
		add("gem.disabled", "Disabled with Apotheosis adventure module enabled");
		add("gem_class_format", "• %s: ");
		add("gem_class.helmet", "Helmets");
		add("gem_class.chestplate", "Chestplates");
		add("gem_class.boots", "Boots");
		add("gem_class.other_armor", "Other Armor");
		add("gem_class.weapon", "Weapons");
		add("gem_class.shield", "Shields");
		add("gem_class.bow", "Bows");
		add("gem_class.melee_weapon", "Melee Weapon");
		add("gem_class.armor_and_shield", "Armor and Shields");
		add("gem_class.anything", "Anything");
		add("gem_class.armor", "Armor");
		add("gem_class.pickaxe", "Pickaxes");
		add("gem_class.ring", "Rings");
		add("gem_class.necklace", "Necklaces");
		add("gem_class.jewelry", "Jewelry");
		add("gem_class.ranged_weapon", "Ranged Weapon");
		add("gem.tooltip", "• Can be inserted into items with sockets");
		addTooltip(PSTItems.VACUCITE.get(), "Destroys gems in the item");
		addTooltip(PSTItems.IRISCITE.get(), "Outcome unpredictable");
		// food info
		add("food.bonus.healing", "• Heals for %s Life");
		add("food.bonus.damage", "• %s Damage");
		add("food.bonus.crit_damage", "• %s Critical Damage");
		add("food.bonus.life_regeneration", "• %s Life Regeneration");
		// weapon info
		add("weapon.poisoned", "Poisoned:");
		// quiver info
		add("quiver.capacity", "• Can hold up to %s arrows");
		add("quiver.contents", "• Contents: %s");
		// items
		add(PSTItems.ADAMITE.get(), "Adamite");
		add(PSTItems.CITRINE.get(), "Citrine");
		add(PSTItems.IRISCITE.get(), "Iriscite");
		add(PSTItems.MOONSTONE.get(), "Moonstone");
		add(PSTItems.ONYX.get(), "Onyx");
		add(PSTItems.RUBY.get(), "Ruby");
		add(PSTItems.VACUCITE.get(), "Vacucite");
		add(PSTItems.JADE.get(), "Jade");
		add(PSTItems.SAPPHIRE.get(), "Sapphire");
		add(PSTItems.TOURMALINE.get(), "Tourmaline");
		add(PSTItems.TURQUOISE.get(), "Turquoise");
		add(PSTItems.WISDOM_SCROLL.get(), "Wisdom Scroll");
		add(PSTItems.AMNESIA_SCROLL.get(), "Amnesia Scroll");
		add(PSTItems.COPPER_RING.get(), "Copper Ring");
		add(PSTItems.IRON_RING.get(), "Iron Ring");
		add(PSTItems.GOLDEN_RING.get(), "Golden Ring");
		add(PSTItems.COPPER_NUGGET.get(), "Copper Nugget");
		add(PSTItems.ASSASSIN_NECKLACE.get(), "Assassin's Necklace");
		add(PSTItems.HEALER_NECKLACE.get(), "Healer's Necklace");
		add(PSTItems.TRAVELER_NECKLACE.get(), "Traveler's Traveler");
		add(PSTItems.SIMPLE_NECKLACE.get(), "Simple Necklace");
		add(PSTItems.SCHOLAR_NECKLACE.get(), "Scholar's Necklace");
		add(PSTItems.ARSONIST_NECKLACE.get(), "Arsonist's Necklace");
		add(PSTItems.FISHERMAN_NECKLACE.get(), "Fisherman's Necklace");
		add(PSTItems.QUIVER.get(), "Quiver");
		add(PSTItems.ARMORED_QUIVER.get(), "Armored Quiver");
		add(PSTItems.DIAMOND_QUIVER.get(), "Diamond Quiver");
		add(PSTItems.EXPLOSIVE_QUIVER.get(), "Explosive Quiver");
		add(PSTItems.FIERY_QUIVER.get(), "Fiery Quiver");
		add(PSTItems.GILDED_QUIVER.get(), "Gilded Quiver");
		add(PSTItems.HEALING_QUIVER.get(), "Healing Quiver");
		add(PSTItems.TOXIC_QUIVER.get(), "Toxic Quiver");
		add(PSTItems.SILENT_QUIVER.get(), "Silent Quiver");
		add(PSTItems.BONE_QUIVER.get(), "Bone Quiver");
		addTooltip(PSTItems.WISDOM_SCROLL.get(), "Grants one passive skill point");
		addTooltip(PSTItems.AMNESIA_SCROLL.get(), "Resets your passive skill tree");
		addWarning(PSTItems.AMNESIA_SCROLL.get(), "%d%% of your skill points will be lost");
		// attributes
		add(PSTAttributes.LIFE_REGENERATION.get(), "Life Regeneration");
		add(PSTAttributes.LIFE_PER_HIT.get(), "Life per Hit");
		add(PSTAttributes.PROJECTILE_CRIT_DAMAGE.get(), "Projectile Critical Damage");
		add(PSTAttributes.PROJECTILE_DAMAGE.get(), "Projectile Damage");
		add(PSTAttributes.EVASION.get(), "Evasion");
		add(PSTAttributes.BLOCK_CHANCE.get(), "Block chance");
		add(PSTAttributes.LIFE_ON_BLOCK.get(), "Life on Block");
		add(PSTAttributes.CRIT_CHANCE.get(), "Critical Hit chance");
		add(PSTAttributes.DOUBLE_LOOT_CHANCE.get(), "Double Loot chance");
		add(PSTAttributes.TRIPLE_LOOT_CHANCE.get(), "Triple Loot chance");
		add(PSTAttributes.CRAFTED_ARMOR_EVASION.get(), "Crafted Armor Evasion chance");
		add(PSTAttributes.CRAFTED_RANGED_WEAPON_SOCKETS.get(), "Crafted Ranged Weapon Sockets");
		add(PSTAttributes.CRAFTED_HELMETS_SOCKETS.get(), "Crafted Helmets Sockets");
		add(PSTAttributes.CRAFTED_RANGED_WEAPON_ATTACK_SPEED.get(), "Crafted Ranged Weapon Attack Speed");
		add(PSTAttributes.EVASION_CHANCE_WHEN_WOUNDED.get(), "Evasion chance if you are Wounded");
		add(PSTAttributes.ARMOR_PER_EVASION.get(), "Armor per Evasion chance");
		add(PSTAttributes.DAMAGE_PER_DISTANCE_TO_ENEMY.get(), "Damage per Distance to Enemy");
		add(PSTAttributes.LIFE_PER_PROJECTILE_HIT.get(), "Life per Projectile Hit");
		add(PSTAttributes.MAXIMUM_LIFE_PER_EVASION.get(), "Maximum Life per Evasion chance");
		add(PSTAttributes.ATTACK_SPEED_WITH_RANGED_WEAPON.get(), "Attack Speed with Ranged Weapon");
		add(PSTAttributes.CHANCE_TO_RETRIEVE_ARROWS.get(), "Chance to retrieve Arrows");
		add(PSTAttributes.PROJECTILE_CRIT_CHANCE.get(), "Projectile Critical Hit chance");
		add(PSTAttributes.MAXIMUM_LIFE_UNDER_POTION_EFFECT.get(), "Maximum Life under Potion Effect");
		add(PSTAttributes.ATTACK_SPEED_UNDER_POTION_EFFECT.get(), "Attack Speed under Potion Effect");
		add(PSTAttributes.BREWED_POTIONS_DURATION.get(), "Brewed Potions Duration");
		add(PSTAttributes.BREWED_POTIONS_STRENGTH.get(), "Brewed Potions Strength");
		add(PSTAttributes.BREWED_BENEFICIAL_POTIONS_STRENGTH.get(), "Brewed Beneficial Potions Strength");
		add(PSTAttributes.BREWED_HARMFUL_POTIONS_STRENGTH.get(), "Brewed Harmful Potions Strength");
		add(PSTAttributes.DAMAGE_AGAINST_POISONED.get(), "Damage against Poisoned enemies");
		add(PSTAttributes.CRIT_CHANCE_AGAINST_POISONED.get(), "Critical Hit chance against Poisoned enemies");
		add(PSTAttributes.EVASION_UNDER_POTION_EFFECT.get(), "Evasion chance under Potion Effect");
		add(PSTAttributes.CAN_POISON_WEAPONS.get(), "You can apply Poisons on Melee Weapons");
		add(PSTAttributes.CAN_MIX_POTIONS.get(), "You can mix Potions together");
		add(PSTAttributes.EVASION_PER_POTION_EFFECT.get(), "Evasion chance per Potion Effect");
		add(PSTAttributes.DAMAGE_PER_POTION_EFFECT.get(), "Damage per Potion Effect");
		add(PSTAttributes.LIFE_PER_HIT_UNDER_POTION_EFFECT.get(), "Life per Hit under Potion Effect");
		add(PSTAttributes.CRIT_DAMAGE_AGAINST_POISONED.get(), "Critical Damage against Poisoned enemies");
		add(PSTAttributes.GEM_POWER_IN_ARMOR.get(), "Gem Power in Armor");
		add(PSTAttributes.GEM_POWER_IN_WEAPON.get(), "Gem Power in Weapon");
		add(PSTAttributes.MAXIMUM_WEAPON_SOCKETS.get(), "Weapon Sockets");
		add(PSTAttributes.MAXIMUM_CHESTPLATE_SOCKETS.get(), "Chestplate Sockets");
		add(PSTAttributes.MAXIMUM_EQUIPMENT_SOCKETS.get(), "Equipment Sockets");
		add(PSTAttributes.ARMOR_PER_GEM_IN_HELMET.get(), "Armor per Gem in Helmet");
		add(PSTAttributes.ARMOR_PER_GEM_IN_CHESTPLATE.get(), "Armor per Gem in Chestplate");
		add(PSTAttributes.CRIT_CHANCE_PER_GEM_IN_WEAPON.get(), "Critical Hit chance per Gem in Weapon");
		add(PSTAttributes.MINING_SPEED.get(), "Mining Speed");
		add(PSTAttributes.MAXIMUM_LIFE_PER_GEM_IN_HELMET.get(), "Maximum Life per Gem in Helmet");
		add(PSTAttributes.MAXIMUM_LIFE_PER_GEM_IN_ARMOR.get(), "Maximum Life per Gem in Armor");
		add(PSTAttributes.CRIT_DAMAGE_PER_GEM_IN_WEAPON.get(), "Critical Damage per Gem in Weapon");
		add(PSTAttributes.GEM_DROP_CHANCE.get(), "Chance to find Gem in Ore");
		add(PSTAttributes.LIFE_REGENERATION_PER_GEM_IN_HELMET.get(), "Life Regeneration per Gem in Helmet");
		add(PSTAttributes.CRAFTED_ARMOR_DEFENCE.get(), "Crafted Armor Defence");
		add(PSTAttributes.CRAFTED_SHIELDS_ARMOR.get(), "Crafted Shields Armor");
		add(PSTAttributes.LIFE_REGENERATION_WITH_SHIELD.get(), "Life Regeneration with Shield");
		add(PSTAttributes.MAXIMUM_LIFE_PER_BOOTS_ARMOR.get(), "Maximum Life per Armor from Boots");
		add(PSTAttributes.ATTACK_SPEED_WITH_SHIELD.get(), "Attack Speed with Shield");
		add(PSTAttributes.CRAFTED_MELEE_WEAPON_DAMAGE_BONUS.get(), "Crafted Melee Weapon Damage");
		add(PSTAttributes.CRAFTED_MELEE_WEAPON_ATTACK_SPEED.get(), "Crafted Melee Weapon Attack Speed");
		add(PSTAttributes.CRIT_DAMAGE_WITH_SHIELD.get(), "Critical Damage with Shield");
		add(PSTAttributes.CRIT_CHANCE_WITH_SHIELD.get(), "Critical Hit chance with Shield");
		add(PSTAttributes.DAMAGE_WITH_SHIELD.get(), "Damage with Shield");
		add(PSTAttributes.CHANCE_TO_CRAFT_TOUGHER_ARMOR.get(), "Chance to Craft Tougher Armor");
		add(PSTAttributes.ATTACK_DAMAGE_PER_ARMOR.get(), "Attack Damage per Armor");
		add(PSTAttributes.CHESTPLATE_ARMOR.get(), "Chestplate Armor");
		add(PSTAttributes.ENCHANTMENT_LEVEL_REQUIREMENT.get(), "Enchantment Level Requirement");
		add(PSTAttributes.ARMOR_ENCHANTMENT_POWER.get(), "Applied Armor Enchantments Strength");
		add(PSTAttributes.WEAPON_ENCHANTMENT_POWER.get(), "Applied Weapon Enchantments Strength");
		add(PSTAttributes.DAMAGE_WITH_ENCHANTED_WEAPON.get(), "Damage with Enchanted Weapon");
		add(PSTAttributes.MAXIMUM_LIFE_WITH_ENCHANTED_ITEM.get(), "Maximum Life with Enchanted Item");
		add(PSTAttributes.MAXIMUM_LIFE_PER_ARMOR_ENCHANTMENT.get(), "Maximum Life per Armor Enchantment");
		add(PSTAttributes.ATTACK_SPEED_WITH_ENCHANTED_WEAPON.get(), "Attack Speed with Enchanted Weapon");
		add(PSTAttributes.ENCHANTMENT_POWER.get(), "Chance to apply better Enchantment");
		add(PSTAttributes.CRIT_CHANCE_WITH_ENCHANTED_WEAPON.get(), "Critical Hit chance with Enchanted Weapon");
		add(PSTAttributes.CRIT_DAMAGE_PER_WEAPON_ENCHANTMENT.get(), "Critical Damage per Weapon Enchantment");
		add(PSTAttributes.LIFE_ON_BLOCK_PER_SHIELD_ENCHANTMENT.get(), "Life on Block per Shield Enchantment");
		add(PSTAttributes.BLOCK_CHANCE_PER_SHIELD_ENCHANTMENT.get(), "Block chance per Shield Enchantment");
		add(PSTAttributes.DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL.get(), "Damage per Weapon Enchantment Level");
		add(PSTAttributes.BLOCK_CHANCE_WITH_ENCHANTED_SHIELD.get(), "Block chance with Enchanted Shield");
		add(PSTAttributes.FREE_ENCHANTMENT_CHANCE.get(), "Free Enchantment chance");
		add(PSTAttributes.COOKED_FOOD_SATURATION.get(), "Cooked Food Saturation");
		add(PSTAttributes.COOKED_FOOD_LIFE_REGENERATION.get(), "Cooked Food Life Regeneration");
		add(PSTAttributes.COOKED_FOOD_HEALING_PER_SATURATION.get(), "Cooked Food Healing per Saturation Point");
		add(PSTAttributes.COOKED_FOOD_DAMAGE_PER_SATURATION.get(), "Cooked Food Damage per Saturation Point");
		add(PSTAttributes.DAMAGE_IF_NOT_HUNGRY.get(), "Damage if you are not Hungry");
		add(PSTAttributes.BLOCK_CHANCE_IF_NOT_HUNGRY.get(), "Block chance if you are not Hungry");
		add(PSTAttributes.LIFE_ON_BLOCK_IF_NOT_HUNGRY.get(), "Life on Block if you are not Hungry");
		add(PSTAttributes.CRIT_CHANCE_IF_NOT_HUNGRY.get(), "Critical Hit chance if you are not Hungry");
		add(PSTAttributes.MAXIMUM_LIFE_IF_NOT_HUNGRY.get(), "Maximum Life if you are not Hungry");
		add(PSTAttributes.MAXIMUM_LIFE_PER_SATISFIED_HUNGER.get(), "Maximum Life per Satisfield Hunger Point");
		add(PSTAttributes.ATTACK_SPEED_IF_NOT_HUNGRY.get(), "Attack Speed if you are not Hungry");
		add(PSTAttributes.BLOCK_CHANCE_PER_SATISFIED_HUNGER.get(), "Block chance per Satisfied Hunger Point");
		add(PSTAttributes.DAMAGE_PER_SATISFIED_HUNGER.get(), "Damage per Satisfied Hunger Point");
		add(PSTAttributes.COOKED_FOOD_CRIT_DAMAGE_PER_SATURATION.get(), "Cooked Food Critical Damage per Saturation Point");
		add(PSTAttributes.CRIT_DAMAGE_PER_SATISFIED_HUNGER.get(), "Critical Damage per Satisfied Hunger Point");
		add(PSTAttributes.CRAFTED_EQUIPMENT_DURABILITY.get(), "Crafted Equipment Durability");
		add(PSTAttributes.DAMAGE_WITH_GEM_IN_WEAPON.get(), "Damage with Gem in Weapon");
		add(PSTAttributes.DAMAGE_PER_GEM_IN_WEAPON.get(), "Damage per Gem in Weapon");
		add(PSTAttributes.ATTACK_SPEED_WITH_GEM_IN_WEAPON.get(), "Attack Speed with Gem in Weapon");
		add(PSTAttributes.ATTACK_SPEED_PER_GEM_IN_WEAPON.get(), "Attack Speed per Gem in Weapon");
		add(PSTAttributes.DAMAGE_UNDER_POTION_EFFECT.get(), "Damage under Potion Effect");
		add(PSTAttributes.BREWED_POISONS_STRENGTH.get(), "Brewed Poisons Strength");
		add(PSTAttributes.BREWED_HEALING_POTIONS_STRENGTH.get(), "Brewed Healing Potions Strength");
		add(PSTAttributes.BREWED_HARMFUL_POTIONS_DURATION.get(), "Brewed Harmful Potions Duration");
		add(PSTAttributes.BREWED_BENEFICIAL_POTIONS_DURATION.get(), "Brewed Beneficial Potions Duration");
		add(PSTAttributes.INCOMING_HEALING.get(), "Incoming Healing");
		add(PSTAttributes.CRIT_DAMAGE.get(), "Critical Damage");
		add(PSTAttributes.EXPERIENCE_PER_HOUR.get(), "Experience Per Hour");
		add(PSTAttributes.CRAFTED_BOOTS_SOCKETS.get(), "Crafted Boots Sockets");
		add(PSTAttributes.MAXIMUM_RING_SOCKETS.get(), "Ring Sockets");
		add(PSTAttributes.GEM_POWER_IN_JEWELRY.get(), "Gem Power in Jewelry");
		add(PSTAttributes.MAXIMUM_LIFE_PER_EQUIPPED_JEWELRY.get(), "Maximum Life per Jewelry Equipped");
		add(PSTAttributes.CRAFTED_RINGS_CRITICAL_DAMAGE.get(), "Crafted Rings Critical Damage");
		add(PSTAttributes.CRAFTED_NECKLACES_MAXIMUM_LIFE.get(), "Crafted Necklaces Maximum Life");
		add(CuriosHelper.getOrCreateSlotAttribute("ring"), "Ring Slots");
		add(PSTAttributes.EXPERIENCE_FROM_MOBS.get(), "Experience from Killed Creatures");
		add(PSTAttributes.EXPERIENCE_FROM_ORE.get(), "Experience from Mined Ore");
		add(PSTAttributes.DAMAGE_IF_DAMAGED.get(), "Damage if you are Damaged");
		add(PSTAttributes.DAMAGE_IF_WOUNDED.get(), "Damage if you are Wounded");
		add(PSTAttributes.CRAFTED_AXES_CRIT_CHANCE.get(), "Crafted Axes Critical Hit chance");
		add(PSTAttributes.CRIT_CHANCE_IF_WOUNDED.get(), "Critical Hit chance if you are Wounded");
		add(PSTAttributes.ATTACK_SPEED_IF_WOUNDED.get(), "Attack Speed if you are Wounded");
		add(PSTAttributes.LIFE_PER_HIT_IF_WOUNDED.get(), "Life per Hit if you are Wounded");
		add(PSTAttributes.CRAFTED_BOOTS_MOVEMENT_SPEED.get(), "Crafted Boots Movement Speed");
		add(PSTAttributes.DAMAGE_PER_DISTANCE_TO_SPAWN.get(), "Damage per Distance to Spawn (up to 50%)");
		add(PSTAttributes.CRAFTED_ARMOR_MAXIMUM_LIFE.get(), "Crafted Armored Maximum Life");
		add(PSTAttributes.CHANCE_TO_SAVE_CRAFITNG_MATERIALS.get(), "Chance to save Crafting Materials");
		add(PSTAttributes.CRAFTED_SHIELDS_MAXIMUM_LIFE.get(), "Crafted Shields Maximum Life");
		add(PSTAttributes.CRAFTED_WEAPON_ATTACK_SPEED.get(), "Crafted Weapon Attack Speed");
		add(PSTAttributes.CRAFTED_SHIELDS_BLOCK_CHANCE.get(), "Crafted Shields Block chance");
		add(PSTAttributes.DAMAGE_AGAINST_BURNING.get(), "Damage against Burning enemies");
		add(PSTAttributes.CHANCE_TO_IGNITE.get(), "Chance to Ignite");
		add(PSTAttributes.CRAFTED_WEAPON_CHANCE_TO_IGNITE.get(), "Crafted Weapon chance to Ignite");
		add(PSTAttributes.CRIT_CHANCE_AGAINST_BURNING.get(), "Critical Hit chance against Burning enemies");
		add(PSTAttributes.CRAFTED_QUIVERS_CHANCE_TO_IGNITE.get(), "Crafted Quivers chance to Ignite");
		add(PSTAttributes.CRAFTED_QUIVERS_DAMAGE_AGAINST_BURNING.get(), "Crafted Quivers Damage against Burning enemies");
		add(PSTAttributes.CHANCE_TO_SAVE_ARROWS_CRAFTING_MATERIALS.get(), "Chance to save Arrow Crafting Materials");
		add(PSTAttributes.MAXIMUM_LIFE_PER_ARROW_IN_QUIVER.get(), "Maximum Life per Arrow in Quiver");
		add(PSTAttributes.CRAFTED_QUIVERS_CAPACITY.get(), "Crafted Quivers Capacity");
		add(PSTAttributes.CRAFTED_QUIVERS_MAXIMUM_LIFE.get(), "Crafted Quivers Maximum Life");
		add(PSTAttributes.STEALTH.get(), "Stealth");
		add(PSTAttributes.CRAFTED_ARMOR_STEALTH.get(), "Crafted Armor Stealth");
		add(PSTAttributes.JUMP_HEIGHT.get(), "Jump Height");
		add(PSTAttributes.CRAFTED_BOOTS_STEALTH.get(), "Crafted Boots Stealth");
		add(PSTAttributes.MELEE_DAMAGE.get(), "Melee Damage");
		add(PSTAttributes.MELEE_CRIT_DAMAGE.get(), "Melee Critical Damage");
		add(PSTAttributes.CRAFTED_MELEE_WEAPON_CRIT_CHANCE.get(), "Crafted Melee Weapon Critical Hit chance");
		add(PSTAttributes.CRAFTED_WEAPON_DOUBLE_LOOT_CHANCE.get(), "Crafted Weapon Double Loot chance");
		add(PSTAttributes.DOUBLE_FISHING_LOOT_CHANCE.get(), "Double Fishing Loot chance");
		add(PSTAttributes.EXPERIENCE_FROM_FISHING.get(), "Experience from Fishing");
		add(PSTAttributes.LUCK_WHILE_FISHING.get(), "Luck while Fishing");
		add(PSTAttributes.CRAFTED_WEAPON_LIFE_PER_HIT.get(), "Crafted Weapon Life per Hit");
		add(PSTAttributes.CRAFTED_WEAPON_DAMAGE_AGAINST_BURNING.get(), "Crafted Weapon Damage against Burning enemies");
		add(PSTAttributes.CHANCE_TO_EXPLODE_ENEMY.get(), "Chance to Explode enemy");
		// effects
		add(PSTEffects.CRIT_DAMAGE_BONUS.get(), "Critical Damage");
		add(PSTEffects.DAMAGE_BONUS.get(), "Damage");
		add(PSTEffects.LIFE_REGENERATION_BONUS.get(), "Life Regeneration");
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
		add("item.apotheosis.gem.skilltree:citrine", "Citrine");
		add("item.apotheosis.gem.skilltree:jade", "Jade");
		add("item.apotheosis.gem.skilltree:sapphire", "Sapphire");
		add("item.apotheosis.gem.skilltree:tourmaline", "Tourmaline");
		add("item.apotheosis.gem.skilltree:turquoise", "Turquoise");
		add("text.apotheosis.category.ring.plural", "Rings");
		add("text.apotheosis.category.necklace.plural", "Necklaces");
		// jei info
		add("skilltree.jei.gem_info",
				"Gems can be inserted into items with sockets on a smithing table. Drop from any ore with a small chance (requires no silk touch tool).");
		// curios info
		add("curios.identifier.quiver", "Quiver");
		add("curios.modifiers.quiver", "When worn:");
	}

	private void addTooltip(Item item, String tooltip) {
		add(item.getDescriptionId() + ".tooltip", tooltip);
	}

	private void addWarning(Item item, String tooltip) {
		add(item.getDescriptionId() + ".warning", tooltip);
	}

	private void add(Attribute attribute, String name) {
		add(attribute.getDescriptionId(), name);
	}

	private void addSkill(String skillName, String name, @Nullable String description) {
		var skillId = "skill." + SkillTreeMod.MOD_ID + "." + skillName;
		add(skillId + ".name", name);
		if (description != null) add(skillId + ".description", description);
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
		Arrays.asList(effects).stream().map(MobEffect::getDescriptionId).map(id -> id.replaceAll("effect.", ""))
				.forEach(id -> potionName.append("." + id));
		add(potionName.toString(), name);
	}
}
