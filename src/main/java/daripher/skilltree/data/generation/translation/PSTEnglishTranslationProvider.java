package daripher.skilltree.data.generation.translation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.compat.ironsspellbooks.IronsSpellbooksCompat;
import daripher.skilltree.init.*;
import daripher.skilltree.skill.bonus.player.GainedExperienceBonus;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;

public class PSTEnglishTranslationProvider extends PSTTranslationProvider {
    public PSTEnglishTranslationProvider(DataGenerator gen) {
        super(gen, SkillTreeMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // skill trees
        add("skilltree:hunter", "Hunter");
        add("skilltree:alchemist", "Alchemist");
        add("skilltree:cook", "Cook");
        // skills
        addSkill("alchemist", 1, "Alchemist");
        addSkills("alchemist", 2, 26, 29, "Poison Chance");
        addSkill("alchemist", 32, "Immunity Bypass");
        addSkill("alchemist", 49, "Weakness Chance");
        addSkill("alchemist", 52, "Wither Chance");
        addSkill("alchemist", 55, "Nausea Chance");
        addSkill("alchemist", 58, "Spreading Plague");
        addSkills("alchemist", 5, 8, 11, "Harmful Effects Duration");
        addSkill("alchemist", 14, "Deep Infection");
        addSkills("alchemist", 22, 25, 34, "Slowness Chance");
        addSkill("alchemist", 37, "Cruelty");
        addSkills("alchemist", 38, 41, 44, "Poison Damage");
        addSkill("alchemist", 60, "Melting Flesh");
        addSkills("alchemist", 3, 27, 30, "Beneficial Effects Duration");
        addSkill("alchemist", 33, "Overdose");
        addSkills("alchemist", 47, 50, 53, "Potion Healing");
        addSkill("alchemist", 56, "Limb Regeneration");
        addSkills("alchemist", 6, 9, 12, "Incoming Healing");
        addSkill("alchemist", 15, "Immune Response");
        addSkills("alchemist", 20, 23, 28, "Maximum Health");
        addSkill("alchemist", 35, "Mutation");
        addSkills("alchemist", 39, 42, 45, "Movement Speed");
        addSkill("alchemist", 61, "Adrenalin");
        addSkills("alchemist", 4, 17, 18, "Projectile Damage");
        addSkill("alchemist", 19, "More Gunpowder!");
        addSkills("alchemist", 48, 51, 54, "Additional Projectile Chance");
        addSkill("alchemist", 57, "Double Flasks");
        addSkills("alchemist", 7, 10, 13, "Magic Damage");
        addSkill("alchemist", 16, "Antimagic Field");
        addSkills("alchemist", 21, 24, 31, "Projectile Speed");
        addSkill("alchemist", 36, "Powerful Throw");
        addSkills("alchemist", 40, 43, 46, "Fire Damage");
        addSkill("alchemist", 59, "Incineration");

        addSkill("cook", 1, "Cook");
        addSkills("cook", 2, 26, 29, "Unarmed Damage");
        addSkill("cook", 32, "Heavy Punch");
        addSkills("cook", 49, 52, 55, "Unarmed Attack Speed");
        addSkill("cook", 58, "Iron Fist");
        addSkills("cook", 5, 8, 11, "Attack Damage");
        addSkill("cook", 14, "Spicy!");
        addSkills("cook", 22, 25, 34, "Melee Damage");
        addSkill("cook", 37, "Healthy Body");
        addSkills("cook", 38, 41, 44, "Unarmed Critical Hit Chance");
        addSkill("cook", 60, "Living Weapon");
        addSkills("cook", 4, 17, 18, "Maximum Health");
        addSkill("cook", 19, "Healthy Diet");
        addSkills("cook", 48, 51, 54, "Food Healing");
        addSkill("cook", 57, "Pile of Muscles");
        addSkills("cook", 7, 10, 13, "Incoming Healing");
        addSkill("cook", 16, "Accelerated Metabolism");
        addSkills("cook", 21, 24, 31, "Healing on Hit");
        addSkill("cook", 36, "Lifesteal");
        addSkills("cook", 40, 43, 46, "Saturation Chance");
        addSkill("cook", 59, "Comfort");
        addSkills("cook", 3, 27, 30, "Movement Speed While Eating");
        addSkill("cook", 33, "Snack");
        addSkills("cook", 47, 50, 53, "Food Usage Speed");
        addSkill("cook", 56, "Ambidexterity");
        addSkills("cook", 6, 9, 12, "Double Fishing Loot Chance");
        addSkill("cook", 15, "Sea Blessing");
        addSkills("cook", 20, 23, 28, "Experience From Fishing");
        addSkill("cook", 35, "Experienced Fisherman");
        addSkills("cook", 39, 42, 45, "Damage Taken While Fishing");
        addSkill("cook", 61, "Unseen Fisherman");

        addSkill("hunter", 1, "Hunter");
        addSkills("hunter", 2, 26, 29, "Leather Armor Durability");
        addSkill("hunter", 32, "Armor Fitting");
        addSkills("hunter", 49, 52, 55, "Damage Avoidance");
        addSkill("hunter", 58, "Acrobatics");
        addSkills("hunter", 5, 8, 11, "Armor");
        addSkill("hunter", 14, "Tanned Leather");
        addSkills("hunter", 22, 25, 34, "Movement Speed");
        addSkill("hunter", 37, "Elusiveness");
        addSkills("hunter", 38, 41, 44, "Projectile Damage Avoidance");
        addSkill("hunter", 60, "Double Layer Armor");
        addSkills("hunter", 4, 17, 18, "Projectile Speed");
        addSkill("hunter", 19, "Penetrating Shot");
        addSkills("hunter", 48, 51, 54, "Bow Shooting Speed");
        addSkill("hunter", 57, "Rapid Shot");
        addSkills("hunter", 7, 10, 13, "Movement Speed While Shooting");
        addSkill("hunter", 16, "Trained Archer");
        addSkills("hunter", 21, 24, 31, "Critical Projectile Damage");
        addSkill("hunter", 36, "Heavy Arrows");
        addSkills("hunter", 40, 43, 46, "Projectile Damage");
        addSkill("hunter", 59, "Ballistic Effect");
        addSkills("hunter", 3, 27, 30, "Mobs Loot Duplication");
        addSkill("hunter", 33, "Hunter's Trophy");
        addSkills("hunter", 47, 50, 53, "Chest Loot Duplication");
        addSkill("hunter", 56, "Treasure Hunt");
        addSkills("hunter", 6, 9, 12, "Arrow Retrieval Chance");
        addSkill("hunter", 15, "Careful Shot");
        addSkills("hunter", 20, 23, 28, "Experience From Mobs");
        addSkill("hunter", 35, "Experienced Hunter");
        addSkills("hunter", 39, 42, 45, "Luck");
        addSkill("hunter", 61, "Lucky Beggar");
        // skill bonuses
        add(PSTSkillBonuses.DAMAGE.get(), "Damage");
        add(PSTSkillBonuses.CRIT_DAMAGE.get(), "Critical Damage Multiplier");
        add(PSTSkillBonuses.CRIT_DAMAGE.get(), "damage", "Critical %s Damage Multiplier");
        add(PSTSkillBonuses.CRIT_CHANCE.get(), "Critical Hit Chance");
        add(PSTSkillBonuses.CRIT_CHANCE.get(), "damage", "%s Critical Hit Chance");
        add(PSTSkillBonuses.BLOCK_BREAK_SPEED.get(), "Block break speed");
        add(PSTSkillBonuses.REPAIR_EFFICIENCY.get(), "Repaired %s: %s");
        add(PSTSkillBonuses.REPAIR_EFFICIENCY.get(), "bonus", "Durability restored");
        add(PSTSkillBonuses.FREE_ENCHANTMENT.get(), "chance", "Chance to enchant %s for free");
        add(PSTSkillBonuses.FREE_ENCHANTMENT.get(), "Enchant %s for free");
        add(PSTSkillBonuses.JUMP_HEIGHT.get(), "Jump Height");
        add(PSTSkillBonuses.INCOMING_HEALING.get(), "Incoming Healing");
        add(PSTSkillBonuses.LOOT_DUPLICATION.get(), "Chance to get %s %s");
        add(PSTSkillBonuses.LOOT_DUPLICATION.get(), "multiplier", "+%s%%");
        add(PSTSkillBonuses.LOOT_DUPLICATION.get(), "guaranteed", "You always get %s %s");
        add(PSTSkillBonuses.LOOT_DUPLICATION.get(), "double", "double");
        add(PSTSkillBonuses.LOOT_DUPLICATION.get(), "triple", "triple");
        add(PSTSkillBonuses.GAINED_EXPERIENCE.get(), "Experience from %s");
        add(PSTSkillBonuses.INFLICT_IGNITE.get(), "player", "You catch fire for %s");
        add(PSTSkillBonuses.INFLICT_IGNITE.get(), "player.chance", "Chance to catch fire for %s");
        add(PSTSkillBonuses.INFLICT_IGNITE.get(), "enemy", "Set enemies on fire for %s");
        add(PSTSkillBonuses.INFLICT_IGNITE.get(), "enemy.chance", "Chance to set enemies on fire for %s");
        add(PSTSkillBonuses.ARROW_RETRIEVAL.get(), "Arrow retrieval chance");
        add(PSTSkillBonuses.HEALTH_RESERVATION.get(), "Health Reservation");
        add(PSTSkillBonuses.ALL_ATTRIBUTES.get(), "All Attributes");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "player", "Gain %s%s");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "player.chance", "Chance to gain %s%s");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "enemy", "Inflict %s%s");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "enemy.chance", "Chance to inflict %s%s");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "seconds", " for %s seconds");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "minutes", " for %s minutes");
        add(PSTSkillBonuses.INFLICT_EFFECT.get(), "stacks", "%s, stacks up to %s times");
        add(PSTSkillBonuses.CANT_USE_ITEM.get(), "Can not use %s");
        add(PSTSkillBonuses.HEALING.get(), "player", "Recover %s life");
        add(PSTSkillBonuses.HEALING.get(), "player.chance", "Chance to recover %s life");
        add(PSTSkillBonuses.HEALING.get(), "enemy", "Enemies recover %s life");
        add(PSTSkillBonuses.HEALING.get(), "enemy.chance", "Chance for enemies to recover %s life");
        add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "player", "+%s %s taken");
        add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "player.chance", "Chance to take %s %s");
        add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "enemy", "+%s %s Damage inflicted");
        add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "enemy.chance", "Chance to inflict %s %s Damage");
        add(PSTSkillBonuses.CAN_POISON_ANYONE.get(), "Your poisons can affect any enemies");
        add(PSTSkillBonuses.LETHAL_POISON.get(), "Your poisons are lethal");
        add(PSTSkillBonuses.DAMAGE_TAKEN.get(), "%s taken");
        add(PSTSkillBonuses.DAMAGE_AVOIDANCE.get(), "Chance to avoid %s");
        add(PSTSkillBonuses.DAMAGE_CONVERSION.get(), "%s%% of %s is being converted to %s");
        add(PSTSkillBonuses.GRANT_ITEM.get(), "Grants %s when learned");
        add(PSTSkillBonuses.GRANT_ITEM.get(), "amount", "Grants %sx %s when learned");
        add(PSTSkillBonuses.EFFECT_DURATION.get(), "player", "Duration of %s on you");
        add(PSTSkillBonuses.EFFECT_DURATION.get(), "enemy", "Duration of inflicted %s");
        add(PSTSkillBonuses.PROJECTILE_DUPLICATION.get(), "chance", "Chance to fire additional projectile");
        add(PSTSkillBonuses.PROJECTILE_DUPLICATION.get(), "amount", "Fire %s additional projectiles");
        add(PSTSkillBonuses.PROJECTILE_DUPLICATION.get(), "Fire an additional projectile");
        add(PSTSkillBonuses.SELF_SPLASH_IMMUNE.get(), "Your splash potions do not affect you");
        add(PSTSkillBonuses.PROJECTILE_SPEED.get(), "Projectile Speed");
        add(PSTSkillBonuses.ITEM_DURABILITY_LOSS_AVOIDANCE.get(), "chance", "Chance to prevent %s durability loss");
        add(PSTSkillBonuses.ITEM_DURABILITY_LOSS_AVOIDANCE.get(), "Prevent %s durability loss");
        add(PSTSkillBonuses.ITEM_USAGE_SPEED.get(), "positive", "You use %s %s%% faster");
        add(PSTSkillBonuses.ITEM_USAGE_SPEED.get(), "negative", "You use %s %s%% slower");
        add(PSTSkillBonuses.ITEM_USE_MOVEMENT_SPEED.get(), "positive", "Reduces movement speed penalty from using %s by %s%%");
        add(PSTSkillBonuses.ITEM_USE_MOVEMENT_SPEED.get(), "remove", "Removes movement speed penalty from using %s");
        add(PSTSkillBonuses.ITEM_USE_MOVEMENT_SPEED.get(), "negative", "Increases movement speed penalty from using %s by %s%%");
        add(PSTSkillBonuses.MORE_ITEM_BONUSES.get(), "one", "You can upgrade %s an additional time using advanced workbench");
        add(PSTSkillBonuses.MORE_ITEM_BONUSES.get(), "You can upgrade %s %s additional times using advanced workbench");
        add(PSTSkillBonuses.RECIPE_UNLOCK.get(), "Unlocks Recipe: %s");
        add(PSTSkillBonuses.GAIN_EXPERIENCE.get(), "Gain %s experience");
        add(PSTSkillBonuses.GAIN_EXPERIENCE.get(), "chance", "Chance to gain %s experience");
        add(PSTSkillBonuses.VANILLA_RECIPE_UNLOCK.get(), "You can create %s on advanced workbench");
        add(PSTSkillBonuses.CRAFTED_ITEM_BONUS.get(), "%s created on advanced workbench gain:");
        add(PSTSkillBonuses.CRAFTED_ITEM_BONUS.get(), "list_item_prefix", " • ");
        add(PSTSkillBonuses.STEALTH.get(), "You are %s%% harder to detect");
        add(PSTSkillBonuses.STEALTH.get(), "negative", "You are %s%% easier to detect");
        add(IronsSpellbooksCompat.GRANT_SPELL_BONUS.get(), "Grants %s %s spell");
        add(IronsSpellbooksCompat.SPELL_LEVEL_BONUS.get(), "to level of %s spell");
        // experience sources
        add(GainedExperienceBonus.ExperienceSource.MOBS.getDescriptionId(), "Mobs");
        add(GainedExperienceBonus.ExperienceSource.ORE.getDescriptionId(), "Ores");
        add(GainedExperienceBonus.ExperienceSource.FISHING.getDescriptionId(), "Fishing");
        // loot conditions
        add(LootDuplicationBonus.LootType.MOBS.getDescriptionId(), "mobs loot");
        add(LootDuplicationBonus.LootType.FISHING.getDescriptionId(), "fishing loot");
        add(LootDuplicationBonus.LootType.GEMS.getDescriptionId(), "gems from ore");
        add(LootDuplicationBonus.LootType.CHESTS.getDescriptionId(), "loot in chests");
        add(LootDuplicationBonus.LootType.ORE.getDescriptionId(), "loot from ore");
        add(LootDuplicationBonus.LootType.ARCHAEOLOGY.getDescriptionId(), "loot from archaeology");
        // living conditions
        add(PSTLivingEntityPredicates.HAS_ITEM_EQUIPPED.get(), "target.player", "with");
        add(PSTLivingEntityPredicates.HAS_ITEM_EQUIPPED.get(), "target.enemy", "if enemy has");
        add(PSTLivingEntityPredicates.HAS_ITEM_EQUIPPED.get(), "%s %s %s equipped");
        add(PSTLivingEntityPredicates.HAS_EFFECT.get(), "target.player", "you are");
        add(PSTLivingEntityPredicates.HAS_EFFECT.get(), "target.enemy", "enemy is");
        add(PSTLivingEntityPredicates.HAS_EFFECT.get(), "%s if %s affected by %s");
        add(PSTLivingEntityPredicates.HAS_EFFECT.get(), "amplifier", "%s if %s affected by %s or higher");
        add(PSTLivingEntityPredicates.BURNING.get(), "target.player", "you are");
        add(PSTLivingEntityPredicates.BURNING.get(), "target.enemy", "enemy is");
        add(PSTLivingEntityPredicates.BURNING.get(), "%s if %s burning");
        add(PSTLivingEntityPredicates.FISHING.get(), "target.player", "you are");
        add(PSTLivingEntityPredicates.FISHING.get(), "target.enemy", "enemy is");
        add(PSTLivingEntityPredicates.FISHING.get(), "%s if %s fishing");
        add(PSTLivingEntityPredicates.UNDERWATER.get(), "target.player", "you are");
        add(PSTLivingEntityPredicates.UNDERWATER.get(), "target.enemy", "enemy is");
        add(PSTLivingEntityPredicates.UNDERWATER.get(), "%s if %s under water");
        add(PSTLivingEntityPredicates.DUAL_WIELDING.get(), "target.player", "you have");
        add(PSTLivingEntityPredicates.DUAL_WIELDING.get(), "target.enemy", "enemy has");
        add(PSTLivingEntityPredicates.DUAL_WIELDING.get(), "%s if %s %s in both hands");
        add(PSTLivingEntityPredicates.HAS_ITEM_IN_HAND.get(), "target.player", "with");
        add(PSTLivingEntityPredicates.HAS_ITEM_IN_HAND.get(), "target.enemy", "if enemy has");
        add(PSTLivingEntityPredicates.HAS_ITEM_IN_HAND.get(), "%s %s %s in hand");
        add(PSTLivingEntityPredicates.CROUCHING.get(), "target.player", "while crouching");
        add(PSTLivingEntityPredicates.CROUCHING.get(), "target.enemy", "if enemy is crouching");
        add(PSTLivingEntityPredicates.CROUCHING.get(), "%s %s");
        add(PSTLivingEntityPredicates.UNARMED.get(), "target.player", "while unarmed");
        add(PSTLivingEntityPredicates.UNARMED.get(), "target.enemy", "if enemy is unarmed");
        add(PSTLivingEntityPredicates.UNARMED.get(), "%s %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "more", "more than %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "less", "less than %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal", "equal to %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "at_least", "at least %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "at_most", "at most %s");
        add(PSTLivingEntityPredicates.ALL_ARMOR.get(), "target.player", "if all your armor is");
        add(PSTLivingEntityPredicates.ALL_ARMOR.get(), "target.enemy", "if all target's armor is");
        add(PSTLivingEntityPredicates.ALL_ARMOR.get(), "%s %s %s");
        // event listeners
        add(PSTEventListeners.ATTACK.get(), "%s on hit");
        add(PSTEventListeners.ATTACK.get(), "damage", "%s on %s hit");
        add(PSTEventListeners.BLOCK.get(), "%s on block");
        add(PSTEventListeners.BLOCK.get(), "damage", "%s on %s block");
        add(PSTEventListeners.EVASION.get(), "%s on evasion");
        add(PSTEventListeners.ITEM_USED.get(), "%s on %s use");
        add(PSTEventListeners.DAMAGE_TAKEN.get(), "%s when you take %s");
        add(PSTEventListeners.ON_KILL.get(), "%s on kill");
        add(PSTEventListeners.ON_KILL.get(), "damage", "%s on %s kill");
        add(PSTEventListeners.SKILL_LEARNED.get(), "%s when you learn this");
        add(PSTEventListeners.SKILL_REMOVED.get(), "%s when this skill is removed");
        add(PSTEventListeners.TICKING.get(), "second", "%s every second");
        add(PSTEventListeners.TICKING.get(), "seconds", "%s every %s seconds");
        add(PSTEventListeners.TICKING.get(), "minute", "%s every minute");
        add(PSTEventListeners.TICKING.get(), "minutes", "%s every %s minutes");
        add(PSTEventListeners.CRITICAL_HIT.get(), "%s on critical hit");
        // damage conditions
        add(PSTDamageConditions.PROJECTILE.get(), "Projectile Damage");
        add(PSTDamageConditions.PROJECTILE.get(), "type", "Projectile");
        add(PSTDamageConditions.MELEE.get(), "Melee Damage");
        add(PSTDamageConditions.MELEE.get(), "type", "Melee");
        add(PSTDamageConditions.MAGIC.get(), "Magic Damage");
        add(PSTDamageConditions.MAGIC.get(), "type", "Magic");
        add(PSTDamageConditions.NONE.get(), "Damage");
        add(PSTDamageConditions.FALL.get(), "Fall Damage");
        add(PSTDamageConditions.FALL.get(), "type", "Fall");
        add(PSTDamageConditions.FIRE.get(), "Fire Damage");
        add(PSTDamageConditions.FIRE.get(), "type", "Fire");
        add(PSTDamageConditions.POISON.get(), "Poison Damage");
        add(PSTDamageConditions.POISON.get(), "type", "Poison");
        add(PSTDamageConditions.THORNS.get(), "Thorns Damage");
        add(PSTDamageConditions.THORNS.get(), "type", "Thorns");
        // death messages
        deathMessage("poison", "%1$s died from poison");
        deathMessage("poison.player", "%1$s was poisoned by %2$s");
        // enchantment conditions
        add(PSTEnchantmentConditions.WEAPON.get(), "Weapon Enchantments");
        add(PSTEnchantmentConditions.ARMOR.get(), "Armor Enchantments");
        add(PSTEnchantmentConditions.NONE.get(), "Enchantments");
        // item conditions
        add(PSTItemConditions.NONE.get(), "Item");
        add(PSTItemConditions.NONE.get(), "plural", "Items");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "weapon", "Weapon");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "weapon.plural", "Weapons");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "ranged_weapon", "Ranged Weapon");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "ranged_weapon.plural", "Ranged Weapons");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "bow", "Bow");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "bow.plural", "Bows");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "crossbow", "Crossbow");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "crossbow.plural", "Crossbows");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "melee_weapon", "Melee Weapon");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "melee_weapon.plural", "Melee Weapons");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "sword", "Sword");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "sword.plural", "Swords");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "trident", "Trident");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "trident.plural", "Tridents");
        add(PSTTags.Items.RINGS, "Ring");
        add(PSTTags.Items.RINGS, "plural", "Rings");
        add(PSTTags.Items.NECKLACES, "Necklace");
        add(PSTTags.Items.NECKLACES, "plural", "Necklaces");
        add(PSTTags.Items.LEATHER_ARMOR, "Leather Armor");
        add(PSTTags.Items.LEATHER_ARMOR, "plural", "Leather Armor");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "armor", "Armor");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "helmet", "Helmet");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "helmet.plural", "Helmets");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "chestplate", "Chestplate");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "chestplate.plural", "Chestplates");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "leggings", "Leggings");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "boots", "Boots");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shield", "Shield");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shield.plural", "Shields");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "any", "Equipment");
        add(PSTItemConditions.POTIONS.get(), "any", "Potion");
        add(PSTItemConditions.POTIONS.get(), "any.plural", "Potions");
        add(PSTItemConditions.POTIONS.get(), "beneficial", "Beneficial Potion");
        add(PSTItemConditions.POTIONS.get(), "beneficial.plural", "Beneficial Potions");
        add(PSTItemConditions.POTIONS.get(), "harmful", "Harmful Potion");
        add(PSTItemConditions.POTIONS.get(), "harmful.plural", "Harmful Potions");
        add(PSTItemConditions.POTIONS.get(), "neutral", "Neutral Potion");
        add(PSTItemConditions.POTIONS.get(), "neutral.plural", "Neutral Potions");
        add(PSTItemConditions.FOOD.get(), "Food");
        add(PSTTags.Items.JEWELRY, "Jewelry");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "tool", "Tool");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "tool.plural", "Tools");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "axe", "Axe");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "axe.plural", "Axes");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "hoe", "Hoe");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "hoe.plural", "Hoes");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "pickaxe", "Pickaxe");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "pickaxe.plural", "Pickaxes");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shovel", "Shovel");
        add(PSTItemConditions.EQUIPMENT_TYPE.get(), "shovel.plural", "Shovels");
        add(PSTItemConditions.ENCHANTED.get(), "Enchanted %s");
        // value providers
        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "multiplier.player.plural", "%s per %s %s");
        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "multiplier.player", "%s per 1 %s");
        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "multiplier.enemy.plural", "%s per %s enemy's %s");
        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "multiplier.enemy", "%s per 1 enemy's %s");

        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "condition.player", "%s if %s is %s");
        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "condition.enemy", "%s if enemy's %s is %s");

        add(PSTFloatFunctions.ATTRIBUTE_VALUE.get(), "requirement", "Reach %s %s");

        add(PSTFloatFunctions.DISTANCE_TO_TARGET.get(), "multiplier.player.plural", "%s per %s blocks between you and enemy");
        add(PSTFloatFunctions.DISTANCE_TO_TARGET.get(), "multiplier.player", "%s per block between you and enemy");

        add(PSTFloatFunctions.DISTANCE_TO_TARGET.get(), "condition.player", "%s if distance to target is %s");

        add("effect_type.beneficial", "beneficial effect");
        add("effect_type.beneficial.plural", "beneficial effects");
        add("effect_type.harmful", "harmful effect");
        add("effect_type.harmful.plural", "harmful effects");
        add("effect_type.neutral", "neutral effect");
        add("effect_type.neutral.plural", "neutral effects");
        add("effect_type.any", "effect");
        add("effect_type.any.plural", "effects");

        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "multiplier.player.plural", "%s per %s %s on you");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "multiplier.player", "%s per %s on you");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "multiplier.enemy.plural", "%s per %s %s on target");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "multiplier.enemy", "%s per %s on target");

        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "condition.player", "%s while affected by %s %s");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "condition.player.any", "%s while affected by any %s");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "condition.player.none", "%s while not affected by any %s");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "condition.enemy", "%s if target is affected by %s %s");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "condition.enemy.any", "%s if target is affected by any %s");
        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "condition.enemy.none", "%s if target is not affected by any %s");

        add(PSTFloatFunctions.EFFECT_AMOUNT.get(), "requirement", "Have %s %s on you");

        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal.effect_amount", "exactly %s");

        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "multiplier.player.plural", "%s per %s enchantments on your %s");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "multiplier.player", "%s per enchantment on your %s");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "multiplier.enemy.plural", "%s per %s enchantments on target's %s");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "multiplier.enemy", "%s per enchantment on target's %s");

        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "enchantment", "enchantment");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "enchantment.plural", "enchantments");

        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "condition.player", "%s if you have %s %s on %s");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "condition.player.any", "%s if your %s is enchanted");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "condition.player.none", "%s if your %s is not enchanted");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "condition.enemy", "%s if target has %s %s on %s");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "condition.enemy.any", "%s if target's %s is enchanted");
        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "condition.enemy.none", "%s if target's %s is not enchanted");

        add(PSTFloatFunctions.ENCHANTMENT_AMOUNT.get(), "requirement", "Have %s %s on your %s");

        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal.enchantment_amount", "exactly %s");

        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "multiplier.player.plural", "%s per %s enchantment levels on your %s");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "multiplier.player", "%s per enchantment level on your %s");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "multiplier.enemy.plural", "%s per %s enchantment levels on target's %s");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "multiplier.enemy", "%s per enchantment level on target's %s");

        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "level", "level");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "level.plural", "levels");

        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "condition.player", "%s if you have %s enchantment %s on %s");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "condition.player.any", "%s if your %s is enchanted");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "condition.player.none", "%s if your %s is not enchanted");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "condition.enemy", "%s if target has %s enchantment %s on %s");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "condition.enemy.any", "%s if target's %s is enchanted");
        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "condition.enemy.none", "%s if target's %s is not enchanted");

        add(PSTFloatFunctions.ENCHANTMENT_LEVELS.get(), "requirement", "Have %s enchantment %s on your %s");

        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "multiplier.player.plural", "%s per %s durability of your %s");
        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "multiplier.player", "%s per durability of your %s");
        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "multiplier.enemy.plural", "%s per %s durability of target's %s");
        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "multiplier.enemy", "%s per durability of target's %s");

        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "condition.player", "%s if your %s has %s durability");
        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "condition.enemy", "%s if target's has %s %s durability");

        add(PSTFloatFunctions.EQUIPMENT_DURABILITY.get(), "requirement", "Have %s durability on your %s");

        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal.equipment_durability", "exactly %s");

        add(PSTFloatFunctions.FOOD_LEVEL.get(), "point", "hunger point");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "point.plural", "hunger points");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.player", "%s per current %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.player.plural", "%s per %s current %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.player.missing", "%s per missing %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.player.plural.missing", "%s per %s missing %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.enemy", "%s per target's current %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.enemy.plural", "%s per %s target's current %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.enemy.missing", "%s per target's missing %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "multiplier.enemy.plural.missing", "%s per %s target's missing %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.player", "%s if you have %s %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.enemy", "%s if target has %s %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.player.missing", "%s if you are missing %s %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.enemy.missing", "%s if target is missing %s %s");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.player.full", "%s if you are not hungry");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.enemy.full", "%s if target is not hungry");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.player.not_full", "%s if you are hungry");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "condition.enemy.not_full", "%s if target is hungry");
        add(PSTFloatFunctions.FOOD_LEVEL.get(), "requirement", "Have %s %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal.food_level", "exactly %s");

        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "point", "health point");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "point.plural", "health points");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "percentage", "health");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "percentage.plural", "health");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.player", "%s per current %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.player.plural", "%s per %s current %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.player.missing", "%s per missing %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.player.plural.missing", "%s per %s missing %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.enemy", "%s per target's current %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.enemy.plural", "%s per %s target's current %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.enemy.missing", "%s per target's missing %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "multiplier.enemy.plural.missing", "%s per %s target's missing %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.player", "%s if you have %s %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.enemy", "%s if target has %s %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.player.missing", "%s if you are missing %s %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.enemy.missing", "%s if target is missing %s %s");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.player.full", "%s while at full health");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.enemy.full", "%s if target is at full health");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.player.not_full", "%s while injured");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "condition.enemy.not_full", "%s if target is injured");
        add(PSTFloatFunctions.HEALTH_LEVEL.get(), "requirement", "Have %s %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal.health_level", "exactly %s");

        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "point", "mana point");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "point.plural", "mana points");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "percentage", "mana");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "percentage.plural", "mana");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.player", "%s per current %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.player.plural", "%s per %s current %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.player.missing", "%s per missing %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.player.plural.missing", "%s per %s missing %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.enemy", "%s per target's current %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.enemy.plural", "%s per %s target's current %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.enemy.missing", "%s per target's missing %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "multiplier.enemy.plural.missing", "%s per %s target's missing %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.player", "%s if you have %s %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.enemy", "%s if target has %s %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.player.missing", "%s if you are missing %s %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.enemy.missing", "%s if target is missing %s %s");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.player.full", "%s while at full mana");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.enemy.full", "%s if target is at full mana");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.player.not_full", "%s while injured");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "condition.enemy.not_full", "%s if target is injured");
        add(IronsSpellbooksCompat.MANA_LEVEL_FUNCTION.get(), "requirement", "Have %s %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal.mana_level", "exactly %s");

        add(PSTFloatFunctions.LEARNED_SKILLS_AMOUNT.get(), "point", "skill");
        add(PSTFloatFunctions.LEARNED_SKILLS_AMOUNT.get(), "point.plural", "skills");
        add(PSTFloatFunctions.LEARNED_SKILLS_AMOUNT.get(), "multiplier.player", "%s per learned skill");
        add(PSTFloatFunctions.LEARNED_SKILLS_AMOUNT.get(), "multiplier.player.plural", "%s per %s learned skills");
        add(PSTFloatFunctions.LEARNED_SKILLS_AMOUNT.get(), "multiplier.enemy", "%s per skill learned by target");
        add(PSTFloatFunctions.LEARNED_SKILLS_AMOUNT.get(), "multiplier.enemy.plural", "%s per %s skills learned by target");
        add(PSTFloatFunctions.LEARNED_SKILLS_AMOUNT.get(), "condition.player", "%s if you have learned %s %s");
        add(PSTFloatFunctions.LEARNED_SKILLS_AMOUNT.get(), "condition.enemy", "%s if target has learned %s %s");
        add(PSTFloatFunctions.LEARNED_SKILLS_AMOUNT.get(), "requirement", "Learn %s %s");
        add(PSTLivingEntityPredicates.NUMERIC_VALUE.get(), "equal.learned_skills_amount", "exactly %s");

        // skill requirements
        add(PSTSkillRequirements.ADVANCEMENT.get(), "Get %s advancement");
        add(PSTSkillRequirements.LEARNED_SKILL.get(), "Learn %s skill");
        // items
        add("item.cant_use.info", "You can not use this");
        add(PSTItems.WISDOM_SCROLL.get(), "Wisdom Scroll");
        add(PSTItems.AMNESIA_SCROLL.get(), "Amnesia Scroll");
        add(PSTItems.WORKBENCH.get(), "Advanced Workbench");
        addTooltip(PSTItems.WISDOM_SCROLL.get(), "Grants one passive skill point");
        addTooltip(PSTItems.AMNESIA_SCROLL.get(), "Resets your passive skill tree");
        addWarning(PSTItems.AMNESIA_SCROLL.get(), "%d%% of your skill points will be lost");
        // effects
        add(PSTMobEffects.LIQUID_FIRE.get(), "Liquid Fire");
        // potions
        add(PSTPotions.LIQUID_FIRE_1.get(), "Liquid Fire");
        add(PSTPotions.LIQUID_FIRE_2.get(), "Liquid Fire");
        // system messages
        add("skilltree.message.reset", "Skill Tree has changed. Your skill points have been restored.");
        add("skilltree.message.reset_command", "Your skill tree has been reset.");
        add("skilltree.message.point_command", "Skill point gained.");
        add("skilltree.message.grant_skill_command", "You were granted the %s skill.");
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
        add("skill.limitation", "Limited to: %s");
        add("skill.requirements", "Requirements:");
        // tabs
        add("itemGroup.skilltree", "Passive Skill Tree");
        // recipes
        add(PSTRecipeSerializers.WORKBENCH_ITEM_BONUS.get(), "%s [%s]");
    }

    protected void add(Potion potion, String name) {
        add(potion.getName(Items.POTION.getDescriptionId() + ".effect."), "Potion of " + name);
        add(potion.getName(Items.SPLASH_POTION.getDescriptionId() + ".effect."), "Splash Potion of " + name);
        add(potion.getName(Items.LINGERING_POTION.getDescriptionId() + ".effect."), "Lingering Potion of " + name);
    }
}
