package daripher.skilltree.data.generation.translation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.*;
import daripher.skilltree.skill.bonus.player.GainedExperienceBonus;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.Tags;
import top.theillusivec4.curios.api.SlotAttribute;

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
    add(PSTSkillBonuses.EFFECT_ON_ATTACK.get(), "Chance to inflict %s for %s");
    add(PSTSkillBonuses.CANT_USE_ITEM.get(), "Can not use %s");
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
    add(PSTLivingConditions.HAS_EFFECT.get(), "amplifier", "%s if %s affected by %s or higher");
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
    add(PSTLivingConditions.HAS_ITEM_IN_HAND.get(), "target.you", "You have");
    add(PSTLivingConditions.HAS_ITEM_IN_HAND.get(), "target.target", "Target has");
    add(PSTLivingConditions.HAS_ITEM_IN_HAND.get(), "%s if %s %s in hand");
    // damage conditions
    add(PSTDamageConditions.IS_PROJECTILE.get(), "Projectile Damage");
    add(PSTDamageConditions.IS_MELEE.get(), "Melee Damage");
    add(PSTDamageConditions.NONE.get(), "Damage");
    // enchantment conditions
    add(PSTEnchantmentConditions.WEAPON.get(), "Weapon Enchantments");
    add(PSTEnchantmentConditions.ARMOR.get(), "Armor Enchantments");
    add(PSTEnchantmentConditions.NONE.get(), "Enchantments");
    // item conditions
    add(PSTItemConditions.NONE.get(), "Item");
    add(PSTItemConditions.NONE.get(), "plural.type", "Items");
    add(PSTItemConditions.NONE.get(), "plural", "Items");
    add(PSTTags.WEAPONS, "Weapon");
    add(PSTTags.WEAPONS, "plural.type", "Weapons");
    add(PSTTags.WEAPONS, "plural", "Weapons");
    add(PSTTags.RANGED_WEAPONS, "Ranged Weapon");
    add(PSTTags.RANGED_WEAPONS, "plural.type", "Ranged Weapons");
    add(PSTTags.RANGED_WEAPONS, "plural", "Ranged Weapons");
    add(Tags.Items.TOOLS_BOWS, "Bow");
    add(Tags.Items.TOOLS_BOWS, "plural.type", "Bows");
    add(Tags.Items.TOOLS_BOWS, "plural", "Bows");
    add(Tags.Items.TOOLS_CROSSBOWS, "Crossbow");
    add(Tags.Items.TOOLS_CROSSBOWS, "plural.type", "Crossbows");
    add(Tags.Items.TOOLS_CROSSBOWS, "plural", "Crossbows");
    add(PSTTags.MELEE_WEAPONS, "Melee Weapon");
    add(PSTTags.MELEE_WEAPONS, "plural.type", "Melee Weapons");
    add(PSTTags.MELEE_WEAPONS, "plural", "Melee Weapons");
    add(ItemTags.SWORDS, "Sword");
    add(ItemTags.SWORDS, "plural.type", "Swords");
    add(ItemTags.SWORDS, "plural", "Swords");
    add(Tags.Items.TOOLS_TRIDENTS, "Trident");
    add(Tags.Items.TOOLS_TRIDENTS, "plural.type", "Tridents");
    add(Tags.Items.TOOLS_TRIDENTS, "plural", "Tridents");
    add(PSTTags.RINGS, "Ring");
    add(PSTTags.RINGS, "plural.type", "Rings");
    add(PSTTags.RINGS, "plural", "Rings");
    add(PSTTags.NECKLACES, "Necklace");
    add(PSTTags.NECKLACES, "plural.type", "Necklaces");
    add(PSTTags.NECKLACES, "plural", "Necklaces");
    add(PSTTags.QUIVERS, "Quiver");
    add(PSTTags.QUIVERS, "plural.type", "Quivers");
    add(PSTTags.QUIVERS, "plural", "Quivers");
    add(Tags.Items.ARMORS, "Armor");
    add(Tags.Items.ARMORS_HELMETS, "Helmet");
    add(Tags.Items.ARMORS_HELMETS, "plural.type", "Helmets");
    add(Tags.Items.ARMORS_HELMETS, "plural", "Helmets");
    add(Tags.Items.ARMORS_CHESTPLATES, "Chestplate");
    add(Tags.Items.ARMORS_CHESTPLATES, "plural.type", "Chestplates");
    add(Tags.Items.ARMORS_CHESTPLATES, "plural", "Chestplates");
    add(Tags.Items.ARMORS_LEGGINGS, "Leggings");
    add(Tags.Items.ARMORS_BOOTS, "Boots");
    add(Tags.Items.TOOLS_SHIELDS, "Shield");
    add(Tags.Items.TOOLS_SHIELDS, "plural.type", "Shields");
    add(Tags.Items.TOOLS_SHIELDS, "plural", "Shields");
    add(PSTTags.EQUIPMENT, "Equipment");
    add(PSTItemConditions.POTIONS.get(), "any", "Potion");
    add(PSTItemConditions.POTIONS.get(), "any.plural.type", "Potions");
    add(PSTItemConditions.POTIONS.get(), "any.plural", "Potions");
    add(PSTItemConditions.POTIONS.get(), "beneficial", "Beneficial Potion");
    add(PSTItemConditions.POTIONS.get(), "beneficial.plural.type", "Beneficial Potions");
    add(PSTItemConditions.POTIONS.get(), "beneficial.plural", "Beneficial Potions");
    add(PSTItemConditions.POTIONS.get(), "harmful", "Harmful Potion");
    add(PSTItemConditions.POTIONS.get(), "harmful.plural.type", "Harmful Potions");
    add(PSTItemConditions.POTIONS.get(), "harmful.plural", "Harmful Potions");
    add(PSTItemConditions.POTIONS.get(), "neutral", "Neutral Potion");
    add(PSTItemConditions.POTIONS.get(), "neutral.plural.type", "Neutral Potions");
    add(PSTItemConditions.POTIONS.get(), "neutral.plural", "Neutral Potions");
    add(PSTItemConditions.FOOD.get(), "Food");
    add(PSTTags.JEWELRY, "Jewelry");
    add(Tags.Items.TOOLS, "Tool");
    add(Tags.Items.TOOLS, "plural.type", "Tools");
    add(Tags.Items.TOOLS, "plural", "Tools");
    add(ItemTags.AXES, "Axe");
    add(ItemTags.AXES, "plural.type", "Axes");
    add(ItemTags.AXES, "plural", "Axes");
    add(ItemTags.HOES, "Hoe");
    add(ItemTags.HOES, "plural.type", "Hoes");
    add(ItemTags.HOES, "plural", "Hoes");
    add(ItemTags.PICKAXES, "Pickaxe");
    add(ItemTags.PICKAXES, "plural.type", "Pickaxes");
    add(ItemTags.PICKAXES, "plural", "Pickaxes");
    add(ItemTags.SHOVELS, "Shovel");
    add(ItemTags.SHOVELS, "plural.type", "Shovels");
    add(ItemTags.SHOVELS, "plural", "Shovels");
    add(PSTItemConditions.ENCHANTED.get(), "Enchanted %s");
    // skill multipliers
    add(PSTLivingMultipliers.EFFECT_AMOUNT.get(), "%s for each effect on you");
    add(PSTLivingMultipliers.ATTRIBUTE_VALUE.get(), "%s per %s point");
    add(PSTLivingMultipliers.ATTRIBUTE_VALUE.get(), "divisor", "%s per %s %s points");
    add(PSTLivingMultipliers.ENCHANTS_AMOUNT.get(), "%s for each enchantment on your %s");
    add(PSTLivingMultipliers.ENCHANTS_LEVELS.get(), "%s for each enchantment level on your %s");
    add(PSTLivingMultipliers.GEMS_AMOUNT.get(), "%s for each Gem in your %s");
    add(PSTLivingMultipliers.FOOD_LEVEL.get(), "%s for each Hunger point");
    add(PSTLivingMultipliers.DISTANCE_TO_TARGET.get(), "%s for each Block between You and Target");
    add(PSTLivingMultipliers.MISSING_HEALTH_POINTS.get(), "%s per missing health point");
    add(
        PSTLivingMultipliers.MISSING_HEALTH_POINTS.get(),
        "divisor",
        "%s per %s missing health points");
    add(PSTLivingMultipliers.MISSING_HEALTH_PERCENTAGE.get(), "%s per missing health percent");
    add(
        PSTLivingMultipliers.MISSING_HEALTH_PERCENTAGE.get(),
        "divisor",
        "%s per %s missing health");
    // recipes
    add("recipe.skilltree.weapon_poisoning", "Weapon Poisoning");
    add(
        "recipe.skilltree.weapon_poisoning.info",
        "(Combine a Melee Weapon and a Harmful Potion on a Crafting Bench to poison a weapon)");
    add("recipe.skilltree.potion_mixing", "Potion Mixing");
    add(
        "recipe.skilltree.potion_mixing.info",
        "(Combine two different potions on a Crafting Bench to create a mixture)");
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
    add("gem.tooltip", "• Can be inserted into items with sockets");
    add("gem_bonus.removal", "Destroys gems in the item");
    add("gem_bonus.random", "Outcome unpredictable");
    // weapon info
    add("weapon.poisoned", "Poisoned:");
    // quiver info
    add("quiver.capacity", "• Can hold up to %s arrows");
    add("quiver.contents", "• Contents: %s");
    // items
    add("item.cant_use.info", "You can not use this");
    addGem("citrine", "Citrine");
    addGem("ruby", "Ruby");
    addGem("sapphire", "Sapphire");
    addGem("jade", "Jade");
    addGem("iriscite", "Iriscite");
    addGem("vacucite", "Vacucite");
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
    add(
        PSTAttributes.EVASION.get().getDescriptionId() + ".info",
        "(Evasion grants a chance to avoid damage from some attacks)");
    add(PSTAttributes.BLOCKING.get(), "Blocking");
    add(
        PSTAttributes.BLOCKING.get().getDescriptionId() + ".info",
        "(Blocking grants a chance to block damage from some attacks, requires a shield)");
    add(PSTAttributes.LIFE_ON_BLOCK.get(), "Life on Block");
    add(PSTAttributes.EXP_PER_MINUTE.get(), "Experience Per Minute");
    add(SlotAttribute.getOrCreate("ring"), "Ring Slots");
    add(PSTAttributes.STEALTH.get(), "Stealth");
    add(PSTAttributes.STEALTH.get().getDescriptionId() + ".info",
        "(Stealth reduces monsters aggression range)");
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
    // jei info
    add(
        "skilltree.jei.gem_info",
        "Gems can be inserted into items with sockets on a smithing table. Drop from any ore with"
            + " a small chance (requires no silk touch tool).");
    // curios info
    add("curios.identifier.quiver", "Quiver");
    add("curios.modifiers.quiver", "When worn:");
    // tabs
    add("itemGroup.skilltree", "Passive Skill Tree");
    // misc
    add("item.modifiers.both_hands", "When Held:");
    // apotheosis compatibility
    add("text.apotheosis.category.curios:ring.plural", "Rings");
    add("text.apotheosis.category.curios:necklace.plural", "Necklaces");
    add("gem_class.jewelry", "Jewelry");
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
    add("affix.skilltree:jewelry/attribute/hidden", "Hidden");
    add("affix.skilltree:jewelry/attribute/hidden.suffix", "of Hiding");
    add("affix.skilltree:jewelry/attribute/healthy", "Healthy");
    add("affix.skilltree:jewelry/attribute/healthy.suffix", "of Health");
  }

  protected void addGem(String type, String name) {
    super.addGem(type, name, "Crumbled", "Broken", "Low-Quality", "Big", "Rare", "Exceptional");
  }
}
