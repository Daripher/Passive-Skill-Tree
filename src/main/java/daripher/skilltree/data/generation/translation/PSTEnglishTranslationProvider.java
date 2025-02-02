package daripher.skilltree.data.generation.translation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.*;
import daripher.skilltree.skill.bonus.player.GainedExperienceBonus;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;

public class PSTEnglishTranslationProvider extends PSTTranslationProvider {
  public PSTEnglishTranslationProvider(DataGenerator gen) {
    super(gen, SkillTreeMod.MOD_ID, "en_us");
  }

  @Override
  protected void addTranslations() {
    // skill bonuses
    add(PSTSkillBonuses.DAMAGE.get(), "Damage");
    add(PSTSkillBonuses.CRIT_DAMAGE.get(), "Critical Damage Multiplier");
    add(PSTSkillBonuses.CRIT_DAMAGE.get(), "damage", "Critical %s Damage Multiplier");
    add(PSTSkillBonuses.CRIT_CHANCE.get(), "Critical Hit Chance");
    add(PSTSkillBonuses.CRIT_CHANCE.get(), "damage", "%s Critical Hit Chance");
    add(PSTSkillBonuses.BLOCK_BREAK_SPEED.get(), "Block break speed");
    add(PSTSkillBonuses.REPAIR_EFFICIENCY.get(), "Repaired %s: %s");
    add(PSTSkillBonuses.REPAIR_EFFICIENCY.get(), "bonus", "Durability restored");
    add(PSTSkillBonuses.FREE_ENCHANTMENT.get(), "Chance to enchant item for free");
    add(PSTSkillBonuses.RECIPE_UNLOCK.get(), "Unlocks Recipe: %s");
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
    add(PSTSkillBonuses.CANT_USE_ITEM.get(), "Can not use %s");
    add(PSTSkillBonuses.HEALING.get(), "player", "Recover %s life");
    add(PSTSkillBonuses.HEALING.get(), "player.chance", "Chance to recover %s life");
    add(PSTSkillBonuses.HEALING.get(), "enemy", "Enemies recover %s life");
    add(PSTSkillBonuses.HEALING.get(), "enemy.chance", "Chance for enemies to recover %s life");
    add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "player", "Take %s %s");
    add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "player.chance", "Chance to take %s %s");
    add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "enemy", "Inflict %s %s");
    add(PSTSkillBonuses.INFLICT_DAMAGE.get(), "enemy.chance", "Chance to inflict %s %s");
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
    add(LootDuplicationBonus.LootType.MOBS.getDescriptionId(), "mobs loot");
    add(LootDuplicationBonus.LootType.FISHING.getDescriptionId(), "fishing loot");
    add(LootDuplicationBonus.LootType.GEMS.getDescriptionId(), "gems from ore");
    add(LootDuplicationBonus.LootType.CHESTS.getDescriptionId(), "loot in chests");
    add(LootDuplicationBonus.LootType.ORE.getDescriptionId(), "loot from ore");
    add(LootDuplicationBonus.LootType.ARCHAEOLOGY.getDescriptionId(), "loot from archaeology");
    // living conditions
    add(PSTLivingConditions.HAS_ITEM_EQUIPPED.get(), "target.player", "with");
    add(PSTLivingConditions.HAS_ITEM_EQUIPPED.get(), "target.enemy", "if enemy has");
    add(PSTLivingConditions.HAS_ITEM_EQUIPPED.get(), "%s %s %s equipped");
    add(PSTLivingConditions.HAS_EFFECT.get(), "target.player", "you are");
    add(PSTLivingConditions.HAS_EFFECT.get(), "target.enemy", "enemy is");
    add(PSTLivingConditions.HAS_EFFECT.get(), "%s if %s affected by %s");
    add(PSTLivingConditions.HAS_EFFECT.get(), "amplifier", "%s if %s affected by %s or higher");
    add(PSTLivingConditions.BURNING.get(), "target.player", "you are");
    add(PSTLivingConditions.BURNING.get(), "target.enemy", "enemy is");
    add(PSTLivingConditions.BURNING.get(), "%s if %s burning");
    add(PSTLivingConditions.FISHING.get(), "target.player", "you are");
    add(PSTLivingConditions.FISHING.get(), "target.enemy", "enemy is");
    add(PSTLivingConditions.FISHING.get(), "%s if %s fishing");
    add(PSTLivingConditions.UNDERWATER.get(), "target.player", "you are");
    add(PSTLivingConditions.UNDERWATER.get(), "target.enemy", "enemy is");
    add(PSTLivingConditions.UNDERWATER.get(), "%s if %s under water");
    add(PSTLivingConditions.DUAL_WIELDING.get(), "target.player", "you have");
    add(PSTLivingConditions.DUAL_WIELDING.get(), "target.enemy", "enemy has");
    add(PSTLivingConditions.DUAL_WIELDING.get(), "%s if %s %s in both hands");
    add(PSTLivingConditions.HAS_ITEM_IN_HAND.get(), "target.player", "with");
    add(PSTLivingConditions.HAS_ITEM_IN_HAND.get(), "target.enemy", "if enemy has");
    add(PSTLivingConditions.HAS_ITEM_IN_HAND.get(), "%s %s %s in hand");
    add(PSTLivingConditions.CROUCHING.get(), "target.player", "while crouching");
    add(PSTLivingConditions.CROUCHING.get(), "target.enemy", "if enemy is crouching");
    add(PSTLivingConditions.CROUCHING.get(), "%s %s");
    add(PSTLivingConditions.UNARMED.get(), "target.player", "while unarmed");
    add(PSTLivingConditions.UNARMED.get(), "target.enemy", "if enemy is unarmed");
    add(PSTLivingConditions.UNARMED.get(), "%s %s");
    add(PSTLivingConditions.NUMERIC_VALUE.get(), "more", "more than %s");
    add(PSTLivingConditions.NUMERIC_VALUE.get(), "less", "less than %s");
    add(PSTLivingConditions.NUMERIC_VALUE.get(), "equal", "equal to %s");
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
    add(PSTTags.Items.QUIVERS, "Quiver");
    add(PSTTags.Items.QUIVERS, "plural", "Quivers");
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
    add(PSTNumericValueProviders.ATTRIBUTE_VALUE.get(), "multiplier.player.plural", "%s per %s %s");
    add(PSTNumericValueProviders.ATTRIBUTE_VALUE.get(), "multiplier.player", "%s per 1 %s");
    add(PSTNumericValueProviders.ATTRIBUTE_VALUE.get(), "multiplier.enemy.plural", "%s per %s enemy's %s");
    add(PSTNumericValueProviders.ATTRIBUTE_VALUE.get(), "multiplier.enemy", "%s per 1 enemy's %s");

    add(PSTNumericValueProviders.ATTRIBUTE_VALUE.get(), "condition.player", "%s if %s is %s");
    add(PSTNumericValueProviders.ATTRIBUTE_VALUE.get(), "condition.enemy", "%s if enemy's %s is %s");

    add(PSTNumericValueProviders.DISTANCE_TO_TARGET.get(), "multiplier.player.plural", "%s per %s blocks between you and enemy");
    add(PSTNumericValueProviders.DISTANCE_TO_TARGET.get(), "multiplier.player", "%s per block between you and enemy");

    add(PSTNumericValueProviders.DISTANCE_TO_TARGET.get(), "condition.player", "if distance to target is %s");

    add("effect_type.beneficial", "beneficial effect");
    add("effect_type.beneficial.plural", "beneficial effects");
    add("effect_type.harmful", "harmful effect");
    add("effect_type.harmful.plural", "harmful effects");
    add("effect_type.neutral", "neutral effect");
    add("effect_type.neutral.plural", "neutral effects");
    add("effect_type.any", "effect");
    add("effect_type.any.plural", "effects");

    add(PSTNumericValueProviders.EFFECT_AMOUNT.get(), "multiplier.player.plural", "%s per %s %s on you");
    add(PSTNumericValueProviders.EFFECT_AMOUNT.get(), "multiplier.player", "%s per %s on you");
    add(PSTNumericValueProviders.EFFECT_AMOUNT.get(), "multiplier.enemy.plural", "%s per %s %s on enemy");
    add(PSTNumericValueProviders.EFFECT_AMOUNT.get(), "multiplier.enemy", "%s per %s on enemy");

    add(PSTNumericValueProviders.EFFECT_AMOUNT.get(), "condition.player", "%s while affected by %s %s");
    add(PSTNumericValueProviders.EFFECT_AMOUNT.get(), "condition.player.any", "%s while affected by any %s");
    add(PSTNumericValueProviders.EFFECT_AMOUNT.get(), "condition.player.none", "%s while not affected by any %s");
    add(PSTNumericValueProviders.EFFECT_AMOUNT.get(), "condition.enemy", "%s if enemy is affected by %s %s");
    add(PSTNumericValueProviders.EFFECT_AMOUNT.get(), "condition.enemy.any", "%s if enemy is affected by any %s");
    add(PSTNumericValueProviders.EFFECT_AMOUNT.get(), "condition.enemy.none", "%s if enemy is not affected by any %s");

    add(PSTLivingConditions.NUMERIC_VALUE.get(), "equal.effect_amount", "exactly %s");

    add(PSTNumericValueProviders.ENCHANTMENT_AMOUNT.get(), "multiplier.player.plural", "%s per %s enchantments on your %s");
    add(PSTNumericValueProviders.ENCHANTMENT_AMOUNT.get(), "multiplier.player", "%s per enchantment on your %s");
    add(PSTNumericValueProviders.ENCHANTMENT_AMOUNT.get(), "multiplier.enemy.plural", "%s per %s enchantments on enemy's %s");
    add(PSTNumericValueProviders.ENCHANTMENT_AMOUNT.get(), "multiplier.enemy", "%s per enchantment on enemy's %s");

    add(PSTNumericValueProviders.ENCHANTMENT_AMOUNT.get(), "enchantment", "enchantment");
    add(PSTNumericValueProviders.ENCHANTMENT_AMOUNT.get(), "enchantment.plural", "enchantments");

    add(PSTNumericValueProviders.ENCHANTMENT_AMOUNT.get(), "condition.player", "%s if you have %s %s on %s");
    add(PSTNumericValueProviders.ENCHANTMENT_AMOUNT.get(), "condition.player.any", "%s if your %s is enchanted");
    add(PSTNumericValueProviders.ENCHANTMENT_AMOUNT.get(), "condition.player.none", "%s if your %s is not enchanted");
    add(PSTNumericValueProviders.ENCHANTMENT_AMOUNT.get(), "condition.enemy", "%s if enemy has %s %s on %s");
    add(PSTNumericValueProviders.ENCHANTMENT_AMOUNT.get(), "condition.enemy.any", "%s if enemy's %s is enchanted");
    add(PSTNumericValueProviders.ENCHANTMENT_AMOUNT.get(), "condition.enemy.none", "%s if enemy's %s is not enchanted");

    add(PSTLivingConditions.NUMERIC_VALUE.get(), "equal.enchantment_amount", "exactly %s");

    add(PSTNumericValueProviders.ENCHANTMENT_LEVELS.get(), "multiplier.player.plural", "%s per %s enchantment levels on your %s");
    add(PSTNumericValueProviders.ENCHANTMENT_LEVELS.get(), "multiplier.player", "%s per enchantment level on your %s");
    add(PSTNumericValueProviders.ENCHANTMENT_LEVELS.get(), "multiplier.enemy.plural", "%s per %s enchantment levels on enemy's %s");
    add(PSTNumericValueProviders.ENCHANTMENT_LEVELS.get(), "multiplier.enemy", "%s per enchantment level on enemy's %s");

    add(PSTNumericValueProviders.ENCHANTMENT_LEVELS.get(), "level", "level");
    add(PSTNumericValueProviders.ENCHANTMENT_LEVELS.get(), "level.plural", "levels");

    add(PSTNumericValueProviders.ENCHANTMENT_LEVELS.get(), "condition.player", "%s if you have %s enchantment %s on %s");
    add(PSTNumericValueProviders.ENCHANTMENT_LEVELS.get(), "condition.player.any", "%s if your %s is enchanted");
    add(PSTNumericValueProviders.ENCHANTMENT_LEVELS.get(), "condition.player.none", "%s if your %s is not enchanted");
    add(PSTNumericValueProviders.ENCHANTMENT_LEVELS.get(), "condition.enemy", "%s if enemy has %s enchantment %s on %s");
    add(PSTNumericValueProviders.ENCHANTMENT_LEVELS.get(), "condition.enemy.any", "%s if enemy's %s is enchanted");
    add(PSTNumericValueProviders.ENCHANTMENT_LEVELS.get(), "condition.enemy.none", "%s if enemy's %s is not enchanted");

    add(PSTNumericValueProviders.EQUIPMENT_DURABILITY.get(), "multiplier.player.plural", "%s per %s durability of your %s");
    add(PSTNumericValueProviders.EQUIPMENT_DURABILITY.get(), "multiplier.player", "%s per durability of your %s");
    add(PSTNumericValueProviders.EQUIPMENT_DURABILITY.get(), "multiplier.enemy.plural", "%s per %s durability of enemy's %s");
    add(PSTNumericValueProviders.EQUIPMENT_DURABILITY.get(), "multiplier.enemy", "%s per durability of enemy's %s");

    add(PSTNumericValueProviders.EQUIPMENT_DURABILITY.get(), "condition.player", "%s if your %s has %s durability");
    add(PSTNumericValueProviders.EQUIPMENT_DURABILITY.get(), "condition.enemy", "%s if enemy's has %s %s durability");

    add(PSTLivingConditions.NUMERIC_VALUE.get(), "equal.equipment_durability", "exactly %s");

    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "point", "hunger point");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "point.plural", "hunger points");

    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "multiplier.player", "%s per current %s");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "multiplier.player.plural", "%s per %s current %s");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "multiplier.player.missing", "%s per missing %s");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "multiplier.player.plural.missing", "%s per %s missing %s");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "multiplier.enemy", "%s per enemy's current %s");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "multiplier.enemy.plural", "%s per %s enemy's current %s");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "multiplier.enemy.missing", "%s per enemy's missing %s");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "multiplier.enemy.plural.missing", "%s per %s enemy's missing %s");

    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "condition.player", "%s if you have %s %s");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "condition.enemy", "%s if enemy has %s %s");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "condition.player.missing", "%s if you are missing %s %s");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "condition.enemy.missing", "%s if enemy is missing %s %s");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "condition.player.full", "%s if you are not hungry");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "condition.enemy.full", "%s if enemy is not hungry");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "condition.player.not_full", "%s if you are hungry");
    add(PSTNumericValueProviders.FOOD_LEVEL.get(), "condition.enemy.not_full", "%s if enemy is hungry");

    add(PSTLivingConditions.NUMERIC_VALUE.get(), "equal.food_level", "exactly %s");

    add(PSTNumericValueProviders.GEM_AMOUNT.get(), "gem", "gem");
    add(PSTNumericValueProviders.GEM_AMOUNT.get(), "gem.plural", "gems");

    add(PSTNumericValueProviders.GEM_AMOUNT.get(), "multiplier.player.plural", "%s per %s gems in your %s");
    add(PSTNumericValueProviders.GEM_AMOUNT.get(), "multiplier.player", "%s per gem in your %s");
    add(PSTNumericValueProviders.GEM_AMOUNT.get(), "multiplier.enemy.plural", "%s per %s gems in enemy's %s");
    add(PSTNumericValueProviders.GEM_AMOUNT.get(), "multiplier.enemy", "%s per gem in enemy's %s");

    add(PSTNumericValueProviders.GEM_AMOUNT.get(), "condition.player", "%s if you have %s %s in %s");
    add(PSTNumericValueProviders.GEM_AMOUNT.get(), "condition.player.any", "%s if you have a gem in %s");
    add(PSTNumericValueProviders.GEM_AMOUNT.get(), "condition.player.none", "%s if you don't have gems in %s");
    add(PSTNumericValueProviders.GEM_AMOUNT.get(), "condition.enemy", "%s if enemy has %s %s in %s");
    add(PSTNumericValueProviders.GEM_AMOUNT.get(), "condition.enemy.any", "%s if enemy has a gem in %s");
    add(PSTNumericValueProviders.GEM_AMOUNT.get(), "condition.enemy.none", "%s if enemy doesn't have gems in %s");

    add(PSTLivingConditions.NUMERIC_VALUE.get(), "equal.gem_amount", "exactly %s");

    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "point", "health point");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "point.plural", "health points");

    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "multiplier.player", "%s per current %s");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "multiplier.player.plural", "%s per %s current %s");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "multiplier.player.missing", "%s per missing %s");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "multiplier.player.plural.missing", "%s per %s missing %s");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "multiplier.enemy", "%s per enemy's current %s");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "multiplier.enemy.plural", "%s per %s enemy's current %s");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "multiplier.enemy.missing", "%s per enemy's missing %s");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "multiplier.enemy.plural.missing", "%s per %s enemy's missing %s");

    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "condition.player", "%s if you have %s %s");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "condition.enemy", "%s if enemy has %s %s");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "condition.player.missing", "%s if you are missing %s %s");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "condition.enemy.missing", "%s if enemy is missing %s %s");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "condition.player.full", "%s while at full health");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "condition.enemy.full", "%s if enemy is at full health");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "condition.player.not_full", "%s while injured");
    add(PSTNumericValueProviders.HEALTH_LEVEL.get(), "condition.enemy.not_full", "%s if enemy is injured");

    add(PSTLivingConditions.NUMERIC_VALUE.get(), "equal.health_level", "exactly %s");

    // recipes
    add("recipe.skilltree.weapon_poisoning", "Weapon Poisoning");
    add("recipe.skilltree.weapon_poisoning.info", "(Combine a melee weapon and a harmful potion on a crafting bench to poison a weapon)");
    add("recipe.skilltree.potion_mixing", "Potion Mixing");
    add("recipe.skilltree.potion_mixing.info", "(Combine two different potions on a crafting bench to create a mixture)");
    add("upgrade_recipe.chance", "Chance: %s%%");
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
    add(PSTItems.ANCIENT_ALLOY_GILDED.get(), "Gilded Ancient Alloy");
    add(PSTItems.ANCIENT_ALLOY_LIGHTWEIGHT.get(), "Lightweight Ancient Alloy");
    add(PSTItems.ANCIENT_ALLOY_CURATIVE.get(), "Curative Ancient Alloy");
    add(PSTItems.ANCIENT_ALLOY_TOXIC.get(), "Toxic Ancient Alloy");
    add(PSTItems.ANCIENT_ALLOY_ENCHANTED.get(), "Enchanted Ancient Alloy");
    add(PSTItems.ANCIENT_ALLOY_SPATIAL.get(), "Spatial Ancient Alloy");
    add(PSTItems.ANCIENT_ALLOY_DURABLE.get(), "Durable Ancient Alloy");
    add(PSTItems.ANCIENT_ALLOY_HOT.get(), "Hot Ancient Alloy");
    add(PSTItems.ANCIENT_BOOK.get(), "Ancient Book");
    addTooltip(PSTItems.WISDOM_SCROLL.get(), "Grants one passive skill point");
    addTooltip(PSTItems.AMNESIA_SCROLL.get(), "Resets your passive skill tree");
    addWarning(PSTItems.AMNESIA_SCROLL.get(), "%d%% of your skill points will be lost");
    addTooltip(PSTItems.ANCIENT_ALLOY_GILDED.get(), "It's impossible to stop admiring it");
    addTooltip(PSTItems.ANCIENT_ALLOY_LIGHTWEIGHT.get(), "Much lighter than it seems");
    addTooltip(PSTItems.ANCIENT_ALLOY_CURATIVE.get(), "You calm down holding it in your hands");
    addTooltip(PSTItems.ANCIENT_ALLOY_TOXIC.get(), "Extremely dangerous");
    addTooltip(PSTItems.ANCIENT_ALLOY_ENCHANTED.get(), "Emits magical energy");
    addTooltip(PSTItems.ANCIENT_ALLOY_SPATIAL.get(), "Distorts the space around itself");
    addTooltip(PSTItems.ANCIENT_ALLOY_DURABLE.get(), "There is not a scratch on the ingot");
    addTooltip(PSTItems.ANCIENT_ALLOY_HOT.get(), "Burns your palms");
    addTooltip(PSTItems.ANCIENT_BOOK.get(), "This item's magic has been depleted");
    add("ancient_material.tooltip", "Requires certain knowledge to be used");
    // slots
    addCurioSlot("ring", "Ring Slot");
    addCurioSlot("ring", "plural", "Ring Slots");
    addCurioSlot("necklace", "Necklace Slot");
    addCurioSlot("necklace", "plural", "Necklace Slots");
    // attributes
    add(PSTAttributes.REGENERATION.get(), "Life Regeneration");
    add(PSTAttributes.EXP_PER_MINUTE.get(), "Experience Per Minute");
    add(PSTAttributes.DEXTERITY.get(), "Dexterity");
    addInfo(PSTAttributes.DEXTERITY.get(), "By default, 1 Dexterity grants +1% Projectile Damage");
    add(PSTAttributes.STRENGTH.get(), "Strength");
    addInfo(PSTAttributes.STRENGTH.get(), "By default, 1 Strength grants +1% Melee Damage");
    add(PSTAttributes.INTELLIGENCE.get(), "Intelligence");
    addInfo(PSTAttributes.INTELLIGENCE.get(), "By default, 1 Intelligence grants +1% Magic Damage");
    // effects
    add(PSTMobEffects.LIQUID_FIRE.get(), "Liquid Fire");
    // potions
    add(PSTPotions.LIQUID_FIRE_1.get(), "Liquid Fire");
    add(PSTPotions.LIQUID_FIRE_2.get(), "Liquid Fire");
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
    add("skill.limitation", "Limited to: %s");
    // jei info
    add("skilltree.jei.gem_info",
        "Gems can be inserted into items with sockets on a smithing table. Drop from any ore with" + " a small chance " + "(requires no silk touch " +
            "tool).");
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
    // enchantments
    add(PSTEnchantments.STEEL_MIND.get(), "Steel Mind");
    add(PSTEnchantments.MAGIC_FLOW.get(), "Magic Flow");
    add(PSTEnchantments.DRAGON_BLOOD.get(), "Dragon Blood");
    add(PSTEnchantments.BOTTOMLESS_FLASK.get(), "Bottomless Flask");
    add(PSTEnchantments.FIRE_WALL.get(), "Fire Wall");
  }

  protected void addMixture(String name, MobEffect... effects) {
    name = "Mixture of " + name;
    addMixture(name, "potion", effects);
    addMixture("Splash " + name, "splash_potion", effects);
    addMixture("Lingering " + name, "lingering_potion", effects);
  }

  protected void add(Potion potion, String name) {
    add(potion.getName(Items.POTION.getDescriptionId() + ".effect."), "Potion of " + name);
    add(potion.getName(Items.SPLASH_POTION.getDescriptionId() + ".effect."), "Splash Potion of " + name);
    add(potion.getName(Items.LINGERING_POTION.getDescriptionId() + ".effect."), "Lingering Potion of " + name);
  }

  protected void addGem(String type, String name) {
    super.addGem(type, name, "Crumbled", "Broken", "Low-Quality", "Big", "Rare", "Exceptional");
  }
}
