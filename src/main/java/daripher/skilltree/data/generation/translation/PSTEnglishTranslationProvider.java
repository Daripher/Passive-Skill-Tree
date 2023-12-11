package daripher.skilltree.data.generation.translation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.*;
import daripher.skilltree.skill.bonus.player.GainedExperienceBonus;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.effect.MobEffects;
import top.theillusivec4.curios.common.CuriosHelper;

public class PSTEnglishTranslationProvider extends PSTTranslationProvider {
  public PSTEnglishTranslationProvider(DataGenerator gen) {
    super(gen, SkillTreeMod.MOD_ID, "en_us");
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
    addSkillBranch("hunter_crafting", "Arrow Retrieval", 1, 3);
    addSkillBranch("hunter_offensive_crafting", "Crafted Weapon Attack Speed", 1, 7);
    addSkillBranch("hunter_defensive_crafting", "Crafted Armor Evasion", 1, 7);
    addSkillBranch("hunter_offensive", "Projectile Damage", 1, 8);
    addSkillBranch("hunter_defensive", "Evasion", 1, 8);
    addSkillBranch("hunter_lesser", "Loot Multiplication", 1, 6);
    addSkillBranch("hunter_life", "Max Health", 1, 2);
    addSkillBranch("hunter_speed", "Attack Speed", 1, 2);
    addSkillBranch("hunter_crit", "Crit Chance", 1, 2);
    addSkillBranch("hunter_healing", "Life per Hit", 1, 4);
    // ranger skills
    addSkill("hunter_subclass_1", "Ranger");
    addSkill("hunter_subclass_1_mastery", "Elusiveness");
    addSkill("hunter_subclass_1_crafting_notable_1", "Soft Soles");
    addSkill("hunter_subclass_1_offensive_notable_1", "Without a Trace");
    addSkill("hunter_subclass_special", "Bloodthirsty Blade");
    addSkillBranch("hunter_subclass_1_defensive", "Evasion", 1, 4);
    addSkillBranch("hunter_subclass_1_offensive", "Stealth and Attack Speed", 1, 4);
    addSkillBranch("hunter_subclass_1_crafting", "Crafted Armor Stealth", 1, 5);
    // fletcher skills
    addSkill("hunter_subclass_2", "Fletcher");
    addSkill("hunter_subclass_2_mastery", "Bottomless Quiver");
    addSkill("hunter_subclass_2_crafting_notable_1", "Lightweight Arrows");
    addSkill("hunter_subclass_2_life_notable_1", "Confidence");
    addSkillBranch("hunter_subclass_2_defensive", "Blocking and Evasion", 1, 4);
    addSkillBranch("hunter_subclass_2_life", "Max Health", 1, 4);
    addSkillBranch("hunter_subclass_2_crafting", "Crafted Quivers Capacity", 1, 5);
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
    addSkillBranch("cook_crafting", "Crafted Food Regeneration", 1, 3);
    addSkillBranch("cook_offensive_crafting", "Crafted Food Damage Bonus", 1, 7);
    addSkillBranch("cook_defensive_crafting", "Crafted Food Healing", 1, 7);
    addSkillBranch("cook_offensive", "Damage if not Hungry", 1, 8);
    addSkillBranch("cook_defensive", "Blocking", 1, 8);
    addSkillBranch("cook_lesser", "Crafted Food Saturation", 1, 6);
    addSkillBranch("cook_life", "Max Health", 1, 2);
    addSkillBranch("cook_speed", "Attack Speed", 1, 2);
    addSkillBranch("cook_crit", "Crit Chance", 1, 2);
    addSkillBranch("cook_healing", "Life on Block", 1, 4);
    // berserker skills
    addSkill("cook_subclass_1", "Berserker");
    addSkill("cook_subclass_1_mastery", "Blood Veil");
    addSkill("cook_subclass_1_crafting_notable_1", "Headsman's Axe");
    addSkill("cook_subclass_1_offensive_notable_1", "Verge of Death");
    addSkill("cook_subclass_special", "Studying Remains");
    addSkillBranch("cook_subclass_1_defensive", "Blocking", 1, 4);
    addSkillBranch("cook_subclass_1_offensive", "Attack Speed", 1, 4);
    addSkillBranch("cook_subclass_1_crafting", "Crafted Axes Crit Chance", 1, 5);
    // fisherman skills
    addSkill("cook_subclass_2", "Fisherman");
    addSkill("cook_subclass_2_mastery", "Sea Gift");
    addSkill("cook_subclass_2_crafting_notable_1", "Experienced Fisherman");
    addSkill("cook_subclass_2_life_notable_1", "Lucky Fisherman");
    addSkillBranch("cook_subclass_2_defensive", "Armor and Blocking", 1, 4);
    addSkillBranch("cook_subclass_2_life", "Max Health", 1, 4);
    addSkillBranch("cook_subclass_2_crafting", "Experience from Fishing", 1, 5);
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
    addSkillBranch("alchemist_crafting", "Crafted Potions Amplification", 1, 3);
    addSkillBranch("alchemist_offensive_crafting", "Crafted Harmful Potions Amplification", 1, 7);
    addSkillBranch(
        "alchemist_defensive_crafting", "Crafted Beneficial Potions Amplification", 1, 7);
    addSkillBranch("alchemist_offensive", "Damage against Poisoned", 1, 8);
    addSkillBranch("alchemist_defensive", "Evasion", 1, 8);
    addSkillBranch("alchemist_lesser", "Crafted Potions Duration", 1, 6);
    addSkillBranch("alchemist_life", "Max Health", 1, 2);
    addSkillBranch("alchemist_speed", "Attack Speed", 1, 2);
    addSkillBranch("alchemist_crit", "Crit Chance", 1, 2);
    addSkillBranch("alchemist_healing", "Life per Hit", 1, 4);
    // assassin skills
    addSkill("alchemist_subclass_1", "Assassin");
    addSkill("alchemist_subclass_1_mastery", "Gutting");
    addSkill("alchemist_subclass_1_crafting_notable_1", "Poisoner");
    addSkill("alchemist_subclass_1_offensive_notable_1", "Backstab");
    addSkill("alchemist_subclass_special", "Spiked Rings");
    addSkillBranch("alchemist_subclass_1_defensive", "Armor and Evasion", 1, 4);
    addSkillBranch("alchemist_subclass_1_offensive", "Crit Chance", 1, 4);
    addSkillBranch("alchemist_subclass_1_crafting", "Crafted Harmful Potions Amplification", 1, 5);
    // healer skills
    addSkill("alchemist_subclass_2", "Healer");
    addSkill("alchemist_subclass_2_mastery", "Self-Treatment");
    addSkill("alchemist_subclass_2_crafting_notable_1", "Panacea");
    addSkill("alchemist_subclass_2_life_notable_1", "Strong Health");
    addSkillBranch("alchemist_subclass_2_defensive", "Evasion", 1, 4);
    addSkillBranch("alchemist_subclass_2_life", "Max Health and Incoming Healing", 1, 4);
    addSkillBranch(
        "alchemist_subclass_2_crafting", "Crafted Beneficial Potions Amplification", 1, 5);
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
    addSkillBranch("enchanter_crafting", "Free Enchantment Chance", 1, 3);
    addSkillBranch("enchanter_offensive_crafting", "Weapon Enchantments Amplification", 1, 7);
    addSkillBranch("enchanter_defensive_crafting", "Armor Enchantments Amplification", 1, 7);
    addSkillBranch("enchanter_offensive", "Damage with Enchanted Weapon", 1, 8);
    addSkillBranch("enchanter_defensive", "Blocking", 1, 8);
    addSkillBranch("enchanter_lesser", "Enchantments Level Requirement", 1, 6);
    addSkillBranch("enchanter_life", "Max Health", 1, 2);
    addSkillBranch("enchanter_speed", "Attack Speed", 1, 2);
    addSkillBranch("enchanter_crit", "Crit Chance", 1, 2);
    addSkillBranch("enchanter_healing", "Life on Block", 1, 4);
    // arsonist skills
    addSkill("enchanter_subclass_1", "Arsonist");
    addSkill("enchanter_subclass_1_mastery", "Incineration");
    addSkill("enchanter_subclass_1_crafting_notable_1", "Flaming Blade");
    addSkill("enchanter_subclass_1_offensive_notable_1", "Scorched Flesh");
    addSkill("enchanter_subclass_special", "Infernal Quivers");
    addSkillBranch("enchanter_subclass_1_defensive", "Blocking and Evasion", 1, 4);
    addSkillBranch("enchanter_subclass_1_offensive", "Damage against Burning", 1, 4);
    addSkillBranch("enchanter_subclass_1_crafting", "Crafted Weapons Ignite Chance", 1, 5);
    // scholar skills
    addSkill("enchanter_subclass_2", "Scholar");
    addSkill("enchanter_subclass_2_mastery", "Studying Stars");
    addSkill("enchanter_subclass_2_crafting_notable_1", "Studying Minerals");
    addSkill("enchanter_subclass_2_life_notable_1", "Meditation");
    addSkillBranch("enchanter_subclass_2_defensive", "Blocking", 1, 4);
    addSkillBranch("enchanter_subclass_2_life", "Max Health and Experience Gain", 1, 4);
    addSkillBranch("enchanter_subclass_2_crafting", "Experience Gain", 1, 5);
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
    addSkillBranch("blacksmith_crafting", "Crafted Shields Defence", 1, 3);
    addSkillBranch("blacksmith_offensive_crafting", "Crafted Melee Weapons Damage", 1, 7);
    addSkillBranch("blacksmith_defensive_crafting", "Crafted Armor Defence", 1, 7);
    addSkillBranch("blacksmith_offensive", "Damage with Shield Equipped", 1, 8);
    addSkillBranch("blacksmith_defensive", "Armor", 1, 8);
    addSkillBranch("blacksmith_lesser", "Crafted Equipment Durability", 1, 6);
    addSkillBranch("blacksmith_life", "Max Health", 1, 2);
    addSkillBranch("blacksmith_speed", "Attack Speed", 1, 2);
    addSkillBranch("blacksmith_crit", "Crit Chance", 1, 2);
    addSkillBranch("blacksmith_healing", "Life Regeneration", 1, 4);
    // soldier skills
    addSkill("blacksmith_subclass_1", "Soldier");
    addSkill("blacksmith_subclass_1_mastery", "Military Training");
    addSkill("blacksmith_subclass_1_crafting_notable_1", "Sharpening");
    addSkill("blacksmith_subclass_1_offensive_notable_1", "Experienced Fighter");
    addSkill("blacksmith_subclass_special", "Greedy Blades");
    addSkillBranch("blacksmith_subclass_1_defensive", "Armor and Blocking", 1, 4);
    addSkillBranch("blacksmith_subclass_1_offensive", "Melee Damage and Blocking", 1, 4);
    addSkillBranch("blacksmith_subclass_1_crafting", "Crafted Melee Weapons Crit Chance", 1, 5);
    // artisan skills
    addSkill("blacksmith_subclass_2", "Artisan");
    addSkill("blacksmith_subclass_2_mastery", "Handyman");
    addSkill("blacksmith_subclass_2_crafting_notable_1", "Lightweight Shields");
    addSkill("blacksmith_subclass_2_life_notable_1", "Tempered in Blood");
    addSkillBranch("blacksmith_subclass_2_defensive", "Armor", 1, 4);
    addSkillBranch("blacksmith_subclass_2_life", "Crafted Armor Max Health", 1, 4);
    addSkillBranch("blacksmith_subclass_2_crafting", "Repair Efficiency", 1, 5);
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
    addSkillBranch("miner_crafting", "Gems Multiplication", 1, 3);
    addSkillBranch("miner_offensive_crafting", "Gems Power in Weapons", 1, 7);
    addSkillBranch("miner_defensive_crafting", "Gems Power in Armor", 1, 7);
    addSkillBranch("miner_offensive", "Damage with Gems in Weapon", 1, 8);
    addSkillBranch("miner_defensive", "Armor", 1, 8);
    addSkillBranch("miner_lesser", "Mining Speed", 1, 6);
    addSkillBranch("miner_life", "Max Health", 1, 2);
    addSkillBranch("miner_speed", "Attack Speed", 1, 2);
    addSkillBranch("miner_crit", "Crit Chance", 1, 2);
    addSkillBranch("miner_healing", "Life Regeneration", 1, 4);
    // explorer skills
    addSkill("miner_subclass_1", "Explorer");
    addSkill("miner_subclass_1_mastery", "Discoverer");
    addSkill("miner_subclass_1_crafting_notable_1", "Seven-League Boots");
    addSkill("miner_subclass_1_offensive_notable_1", "Haste");
    addSkill("miner_subclass_special", "Decorative Boots");
    addSkillBranch("miner_subclass_1_defensive", "Armor", 1, 4);
    addSkillBranch("miner_subclass_1_offensive", "Attack Speed", 1, 4);
    addSkillBranch("miner_subclass_1_crafting", "Crafted Boots Speed", 1, 5);
    // jeweler skills
    addSkill("miner_subclass_2", "Jeweler");
    addSkill("miner_subclass_2_mastery", "Aristocrat");
    addSkill("miner_subclass_2_crafting_notable_1", "Star Shards");
    addSkill("miner_subclass_2_life_notable_1", "Talisman");
    addSkillBranch("miner_subclass_2_defensive", "Armor and Evasion", 1, 4);
    addSkillBranch("miner_subclass_2_life", "Max Health", 1, 4);
    addSkillBranch("miner_subclass_2_crafting", "Gems Power in Jewelry", 1, 5);
    // skill bonuses
    add(PSTSkillBonuses.DAMAGE.get(), "Damage");
    add(PSTSkillBonuses.CRIT_DAMAGE.get(), "Critical Hit Damage");
    add(PSTSkillBonuses.CRIT_CHANCE.get(), "Critical Hit Chance");
    add(PSTSkillBonuses.CRAFTED_ITEM_BONUS.get(), "Crafted %s: %s");
    add(PSTSkillBonuses.GEM_POWER.get(), "Gems inserted into %s: %s");
    add(PSTSkillBonuses.GEM_POWER.get(), "bonus", "Effect Power");
    add(PSTSkillBonuses.PLAYER_SOCKETS.get(), "Gem Sockets in %s");
    add(PSTSkillBonuses.BLOCK_BREAK_SPEED.get(), "Block Break Speed");
    add(PSTSkillBonuses.REPAIR_EFFICIENCY.get(), "Repaired %s: %s");
    add(PSTSkillBonuses.REPAIR_EFFICIENCY.get(), "bonus", "Durability Restored");
    add(PSTSkillBonuses.ENCHANTMENT_AMPLIFICATION.get(), "%s: %s");
    add(PSTSkillBonuses.ENCHANTMENT_AMPLIFICATION.get(), "bonus", "Amplification Chance");
    add(PSTSkillBonuses.ENCHANTMENT_REQUIREMENT.get(), "Enchantments: %s");
    add(PSTSkillBonuses.ENCHANTMENT_REQUIREMENT.get(), "bonus", "Level Requirement");
    add(PSTSkillBonuses.FREE_ENCHANTMENT.get(), "Enchantments: %s");
    add(PSTSkillBonuses.FREE_ENCHANTMENT.get(), "bonus", "Free Enchantment chance");
    add(PSTSkillBonuses.RECIPE_UNLOCK.get(), "Unlocks Recipe: %s");
    add(PSTSkillBonuses.JUMP_HEIGHT.get(), "Jump Height");
    add(PSTSkillBonuses.INCOMING_HEALING.get(), "Incoming Healing");
    add(PSTSkillBonuses.LOOT_DUPLICATION.get(), "Chance to get +%s%% %s");
    add(PSTSkillBonuses.GAINED_EXPERIENCE.get(), "Experience from %s");
    add(PSTSkillBonuses.IGNITE_CHANCE.get(), "Chance to inflict Ignite for %s");
    add(PSTSkillBonuses.ARROW_RETRIEVAL.get(), "Arrow retrieval chance");
    add(PSTSkillBonuses.HEALTH_RESERVATION.get(), "Health Reservation");
    add(PSTSkillBonuses.ALL_ATTRIBUTES.get(), "All Attributes");
    // item bonuses
    add(PSTItemBonuses.SOCKETS.get(), "+%d Gem Sockets");
    add(PSTItemBonuses.DURABILITY.get(), "Durability");
    add(PSTItemBonuses.QUIVER_CAPACITY.get(), "Capacity");
    add(PSTItemBonuses.POTION_AMPLIFICATION.get(), "Amplification Chance");
    add(PSTItemBonuses.POTION_DURATION.get(), "Duration");
    add(PSTItemBonuses.FOOD_EFFECT.get(), "%s for %s");
    add(PSTItemBonuses.FOOD_SATURATION.get(), "Saturation");
    add(PSTItemBonuses.FOOD_HEALING.get(), "Restores %s Health");
    // experience sources
    add(GainedExperienceBonus.ExperienceSource.MOBS.getDescriptionId(), "Mobs");
    add(GainedExperienceBonus.ExperienceSource.ORE.getDescriptionId(), "Ores");
    add(GainedExperienceBonus.ExperienceSource.FISHING.getDescriptionId(), "Fishing");
    // loot conditions
    add(LootDuplicationBonus.LootType.MOBS.getDescriptionId(), "Mobs Loot");
    add(LootDuplicationBonus.LootType.FISHING.getDescriptionId(), "Fishing Loot");
    add(LootDuplicationBonus.LootType.GEMS.getDescriptionId(), "Gems from Ore");
    // living conditions
    add(PSTLivingConditions.EFFECT_AMOUNT.get(), "target.you", "You have");
    add(PSTLivingConditions.EFFECT_AMOUNT.get(), "target.target", "Target has");
    add(PSTLivingConditions.EFFECT_AMOUNT.get(), "min.1", "%s if %s effects");
    add(PSTLivingConditions.EFFECT_AMOUNT.get(), "min", "%s if %s at least %d effects");
    add(PSTLivingConditions.EFFECT_AMOUNT.get(), "max", "%s if %s at most %d effects");
    add(PSTLivingConditions.EFFECT_AMOUNT.get(), "range", "%s if %s %d to %d effects");
    add(PSTLivingConditions.HEALTH_PERCENTAGE.get(), "target.you", "You have");
    add(PSTLivingConditions.HEALTH_PERCENTAGE.get(), "target.target", "Target has");
    add(PSTLivingConditions.HEALTH_PERCENTAGE.get(), "min", "%s if %s at least %s%% health");
    add(PSTLivingConditions.HEALTH_PERCENTAGE.get(), "max", "%s if %s at most %s%% health");
    add(PSTLivingConditions.HEALTH_PERCENTAGE.get(), "range", "%s if %s %s%% to %s%% health");
    add(PSTLivingConditions.HAS_ENCHANTED_ITEM.get(), "target.you", "You have");
    add(PSTLivingConditions.HAS_ENCHANTED_ITEM.get(), "target.target", "Target has");
    add(PSTLivingConditions.HAS_ENCHANTED_ITEM.get(), "%s if %s enchanted %s equipped");
    add(PSTLivingConditions.HAS_ITEM_EQUIPPED.get(), "target.you", "You have");
    add(PSTLivingConditions.HAS_ITEM_EQUIPPED.get(), "target.target", "Target has");
    add(PSTLivingConditions.HAS_ITEM_EQUIPPED.get(), "%s if %s %s equipped");
    add(PSTLivingConditions.HAS_GEMS.get(), "target.you", "You have");
    add(PSTLivingConditions.HAS_GEMS.get(), "target.target", "Target has");
    add(PSTLivingConditions.HAS_GEMS.get(), "min.1", "%s if %s gems in %s");
    add(PSTLivingConditions.HAS_GEMS.get(), "min", "%s %s at least %d gems in %s");
    add(PSTLivingConditions.HAS_GEMS.get(), "max", "%s %s at most %d gems in %s");
    add(PSTLivingConditions.HAS_GEMS.get(), "range", "%s %s %d to %d gems in %s");
    add(PSTLivingConditions.HAS_EFFECT.get(), "target.you", "You are");
    add(PSTLivingConditions.HAS_EFFECT.get(), "target.target", "Target is");
    add(PSTLivingConditions.HAS_EFFECT.get(), "%s if %s affected by %s");
    add(PSTLivingConditions.BURNING.get(), "target.you", "You are");
    add(PSTLivingConditions.BURNING.get(), "target.target", "Target is");
    add(PSTLivingConditions.BURNING.get(), "%s if %s burning");
    add(PSTLivingConditions.ATTRIBUTE_VALUE.get(), "target.you", "You have");
    add(PSTLivingConditions.ATTRIBUTE_VALUE.get(), "target.target", "Target has");
    add(PSTLivingConditions.ATTRIBUTE_VALUE.get(), "min", "%s if %s at least %s %s");
    add(PSTLivingConditions.ATTRIBUTE_VALUE.get(), "max", "%s if %s at most %s %s");
    add(PSTLivingConditions.ATTRIBUTE_VALUE.get(), "range", "%s if %s %s%% to %s %s");
    add(PSTLivingConditions.FOOD_LEVEL.get(), "target.you", "You have");
    add(PSTLivingConditions.FOOD_LEVEL.get(), "target.target", "Target has");
    add(PSTLivingConditions.FOOD_LEVEL.get(), "min", "%s if %s at least %s Hunger points");
    add(PSTLivingConditions.FOOD_LEVEL.get(), "max", "%s if %s at most %s Hunger points");
    add(PSTLivingConditions.FOOD_LEVEL.get(), "range", "%s if %s %s%% to %s Hunger points");
    add(PSTLivingConditions.FISHING.get(), "target.you", "You are");
    add(PSTLivingConditions.FISHING.get(), "target.target", "Target is");
    add(PSTLivingConditions.FISHING.get(), "%s if %s fishing");
    add(PSTLivingConditions.UNDERWATER.get(), "target.you", "You are");
    add(PSTLivingConditions.UNDERWATER.get(), "target.target", "Target is");
    add(PSTLivingConditions.UNDERWATER.get(), "%s if %s under water");
    add(PSTLivingConditions.DUAL_WIELDING.get(), "target.you", "You have");
    add(PSTLivingConditions.DUAL_WIELDING.get(), "target.target", "Target has");
    add(PSTLivingConditions.DUAL_WIELDING.get(), "%s if %s %s in both hands");
    // damage conditions
    add(PSTDamageConditions.IS_PROJECTILE.get(), "Projectile %s");
    add(PSTDamageConditions.IS_MELEE.get(), "Melee %s");
    // enchantment conditions
    add(PSTEnchantmentConditions.WEAPON.get(), "Weapon Enchantments");
    add(PSTEnchantmentConditions.ARMOR.get(), "Armor Enchantments");
    add(PSTEnchantmentConditions.NONE.get(), "Enchantments");
    // item conditions
    add(PSTItemConditions.WEAPON.get(), "any", "Weapon");
    add(PSTItemConditions.WEAPON.get(), "ranged", "Ranged Weapon");
    add(PSTItemConditions.WEAPON.get(), "bow", "Bow");
    add(PSTItemConditions.WEAPON.get(), "crossbow", "Crossbow");
    add(PSTItemConditions.WEAPON.get(), "melee", "Melee Weapon");
    add(PSTItemConditions.WEAPON.get(), "axe", "Axe");
    add(PSTItemConditions.WEAPON.get(), "sword", "Sword");
    add(PSTItemConditions.WEAPON.get(), "trident", "Trident");
    add(PSTItemConditions.CURIO.get(), "ring", "Ring");
    add(PSTItemConditions.CURIO.get(), "necklace", "Necklace");
    add(PSTItemConditions.CURIO.get(), "quiver", "Quiver");
    add(PSTItemConditions.ARMOR.get(), "any", "Armor");
    add(PSTItemConditions.ARMOR.get(), "helmet", "Helmet");
    add(PSTItemConditions.ARMOR.get(), "chestplate", "Chestplate");
    add(PSTItemConditions.ARMOR.get(), "leggings", "Leggings");
    add(PSTItemConditions.ARMOR.get(), "boots", "Boots");
    add(PSTItemConditions.ARMOR.get(), "shield", "Shield");
    add(PSTItemConditions.EQUIPMENT.get(), "Equipment");
    add(PSTItemConditions.POTIONS.get(), "any", "Potions");
    add(PSTItemConditions.POTIONS.get(), "beneficial", "Beneficial Potions");
    add(PSTItemConditions.POTIONS.get(), "harmful", "Harmful Potions");
    add(PSTItemConditions.POTIONS.get(), "neutral", "Neutral Potions");
    add(PSTItemConditions.FOOD.get(), "Food");
    add(PSTItemConditions.JEWELRY.get(), "Jewelry");
    add(PSTItemConditions.PICKAXE.get(), "Pickaxe");
    // skill multipliers
    add(PSTLivingMultipliers.EFFECT_AMOUNT.get(), "%s for each effect on you");
    add(PSTLivingMultipliers.ATTRIBUTE_VALUE.get(), "%s for each %s point");
    add(PSTLivingMultipliers.ENCHANTS_AMOUNT.get(), "%s for each enchantment on your %s");
    add(PSTLivingMultipliers.ENCHANTS_LEVELS.get(), "%s for each enchantment level on your %s");
    add(PSTLivingMultipliers.GEMS_AMOUNT.get(), "%s for each Gem in your %s");
    add(PSTLivingMultipliers.FOOD_LEVEL.get(), "%s for each Hunger point");
    add(PSTLivingMultipliers.DISTANCE_TO_TARGET.get(), "%s for each Block between You and Target");
    // recipes
    addRecipe("skilltree:weapon_poisoning", "Weapon Poisoning");
    addRecipe("skilltree:potion_mixing", "Potion Mixing");
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
    add("gem_class_format", "• %s: %s");
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
    add("gem.tooltip", "• Can be inserted into items with chance");
    addTooltip(PSTItems.VACUCITE.get(), "Destroys gems in the item");
    addTooltip(PSTItems.IRISCITE.get(), "Outcome unpredictable");
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
    add(PSTItems.TRAVELER_NECKLACE.get(), "Traveler's Necklace");
    add(PSTItems.SIMPLE_NECKLACE.get(), "Simple Necklace");
    add(PSTItems.SCHOLAR_NECKLACE.get(), "Scholar's Necklace");
    add(PSTItems.ARSONIST_NECKLACE.get(), "Arsonist's Necklace");
    add(PSTItems.FISHERMAN_NECKLACE.get(), "Fisherman's Necklace");
    add(PSTItems.QUIVER.get(), "Quiver");
    add(PSTItems.ARMORED_QUIVER.get(), "Armored Quiver");
    add(PSTItems.DIAMOND_QUIVER.get(), "Diamond Quiver");
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
    add(PSTAttributes.REGENERATION.get(), "Life Regeneration");
    add(PSTAttributes.LIFE_PER_HIT.get(), "Life per Hit");
    add(PSTAttributes.EVASION.get(), "Evasion");
    add(PSTAttributes.BLOCKING.get(), "Blocking");
    add(PSTAttributes.LIFE_ON_BLOCK.get(), "Life on Block");
    add(PSTAttributes.EXP_PER_MINUTE.get(), "Experience Per Minute");
    add(CuriosHelper.getOrCreateSlotAttribute("ring"), "Ring Slots");
    add(PSTAttributes.STEALTH.get(), "Stealth");
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
    add("widget.skill_button.multiple_bonuses", "%s and %s");
    add("widget.confirm_button", "Confirm");
    add("widget.cancel_button", "Cancel");
    add("widget.show_stats", "Show Stats");
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
    add(
        "skilltree.jei.gem_info",
        "Gems can be inserted into items with chance on a smithing table. Drop from any ore with a small chance (requires no silk touch tool).");
    // curios info
    add("curios.identifier.quiver", "Quiver");
    add("curios.modifiers.quiver", "When worn:");
    // tabs
    add("itemGroup.skilltree", "Passive Skill Tree");
    // misc
    add("item.modifiers.both_hands", "When Held:");
    // affix names
    add("affix.skilltree:jewelry/dmg_reduction/tempered", "Tempered");
    add("affix.skilltree:jewelry/dmg_reduction/tempered.suffix", "of Hardening");
    add("affix.skilltree:jewelry/attribute/immortal", "Immortal");
    add("affix.skilltree:jewelry/attribute/immortal.suffix", "of Immortality");
    add("affix.skilltree:jewelry/attribute/experienced", "Experienced");
    add("affix.skilltree:jewelry/attribute/experienced.suffix", "of Experience");
    add("affix.skilltree:jewelry/attribute/lucky", "Lucky");
    add("affix.skilltree:jewelry/attribute/lucky.suffix", "of Luck");
    add("affix.skilltree:jewelry/attribute/hasty", "Hasty");
    add("affix.skilltree:jewelry/attribute/hasty.suffix", "of Haste");
    add("affix.skilltree:jewelry/attribute/greedy", "Greedy");
    add("affix.skilltree:jewelry/attribute/greedy.suffix", "of Greed");
    add("affix.skilltree:jewelry/attribute/healthy", "Healthy");
    add("affix.skilltree:jewelry/attribute/healthy.suffix", "of Health");
  }
}
