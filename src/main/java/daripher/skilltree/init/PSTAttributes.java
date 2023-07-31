package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
public class PSTAttributes {
	public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, SkillTreeMod.MOD_ID);

	// alchemist attributes
	public static final RegistryObject<Attribute> ATTACK_SPEED_UNDER_POTION_EFFECT = create("attack_speed_under_potion_effect", 1D, 1D);
	public static final RegistryObject<Attribute> BREWED_POTIONS_DURATION = create("brewed_potions_duration", 1D, 1D);
	public static final RegistryObject<Attribute> CAN_POISON_WEAPONS = create("can_poison_weapons", 0D, 0D);
	public static final RegistryObject<Attribute> BREWED_HARMFUL_POTIONS_STRENGTH = create("brewed_harmful_potions_strength", 1D, 1D);
	public static final RegistryObject<Attribute> BREWED_BENEFICIAL_POTIONS_STRENGTH = create("brewed_beneficial_potions_strength", 1D, 1D);
	public static final RegistryObject<Attribute> BREWED_HEALING_POTIONS_STRENGTH = create("brewed_healing_potions_strength", 1D, 1D);
	public static final RegistryObject<Attribute> BREWED_BENEFICIAL_POTIONS_DURATION = create("brewed_beneficial_potions_duration", 1D, 1D);
	public static final RegistryObject<Attribute> BREWED_POTIONS_STRENGTH = create("brewed_potions_strength", 1D, 1D);
	public static final RegistryObject<Attribute> BREWED_POISONS_STRENGTH = create("brewed_poisons_strength", 1D, 1D);
	public static final RegistryObject<Attribute> BREWED_HARMFUL_POTIONS_DURATION = create("brewed_harmful_potions_duration", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_AGAINST_POISONED = create("crit_chance_against_poisoned", 1D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_AGAINST_POISONED = create("damage_against_poisoned", 1D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_UNDER_POTION_EFFECT = create("damage_under_potion_effect", 1D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_PER_POTION_EFFECT = create("damage_per_potion_effect", 1D, 1D);
	public static final RegistryObject<Attribute> EVASION_UNDER_POTION_EFFECT = create("evasion_under_potion_effect", 1D, 1D);
	public static final RegistryObject<Attribute> EVASION_PER_POTION_EFFECT = create("evasion_per_potion_effect", 1D, 1D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_UNDER_POTION_EFFECT = create("maximum_life_under_potion_effect", 0D, 0D);
	public static final RegistryObject<Attribute> CAN_MIX_POTIONS = create("can_mix_potions", 0D, 0D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_AGAINST_POISONED = create("crit_damage_against_poisoned", 1D, 1D);
	public static final RegistryObject<Attribute> LIFE_PER_HIT_UNDER_POTION_EFFECT = create("life_per_hit_under_potion_effect", 0D, 0D);
	// healier attributes
	public static final RegistryObject<Attribute> INCOMING_HEALING = create("incoming_healing", 1D, 1D);
	// enchanter attributes
	public static final RegistryObject<Attribute> DAMAGE_PER_ENCHANTMENT = create("damage_per_enchantment", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_PER_ENCHANTMENT = create("crit_chance_per_enchantment", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_PER_WEAPON_ENCHANTMENT = create("crit_damage_per_weapon_enchantment", 1D, 1D);
	public static final RegistryObject<Attribute> ENCHANTMENT_LEVEL_REQUIREMENT = create("enchantment_level_requirement", 1D, 0D);
	public static final RegistryObject<Attribute> ENCHANTMENT_POWER = create("chance_to_apply_better_enchantment", 1D, 1D);
	public static final RegistryObject<Attribute> ARMOR_ENCHANTMENT_POWER = create("chance_to_apply_better_armor_enchantment", 1D, 1D);
	public static final RegistryObject<Attribute> WEAPON_ENCHANTMENT_POWER = create("chance_to_apply_better_weapon_enchantment", 1D, 1D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_WITH_ENCHANTED_ITEM = create("maximum_life_with_enchanted_item", 0D, 0D);
	public static final RegistryObject<Attribute> ARMOR_WITH_ENCHANTED_SHIELD = create("armor_with_enchanted_shield", 0D, 0D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_ENCHANTMENT = create("maximum_life_per_enchantment", 0D, 0D);
	public static final RegistryObject<Attribute> DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL = create("damage_per_weapon_enchantment_level", 1D, 1D);
	public static final RegistryObject<Attribute> ARMOR_PER_CHESTPLATE_ENCHANTMENT = create("armor_per_chestplate_enchantment", 0D, 0D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_ARMOR_ENCHANTMENT = create("maximum_life_per_armor_enchantment", 0D, 0D);
	public static final RegistryObject<Attribute> BLOCK_CHANCE_WITH_ENCHANTED_SHIELD = create("block_chance_with_enchanted_shield", 1D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_WITH_ENCHANTED_WEAPON = create("damage_with_enchanted_weapon", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_WITH_ENCHANTED_WEAPON = create("crit_chance_with_enchanted_weapon", 1D, 1D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_WITH_ENCHANTED_WEAPON = create("attack_speed_with_enchanted_weapon", 1D, 1D);
	public static final RegistryObject<Attribute> FREE_ENCHANTMENT_CHANCE = create("free_enchantment_chance", 1D, 1D);
	public static final RegistryObject<Attribute> LIFE_ON_BLOCK_PER_SHIELD_ENCHANTMENT = create("life_on_block_per_shield_enchantment", 0D, 0D);
	public static final RegistryObject<Attribute> BLOCK_CHANCE_PER_SHIELD_ENCHANTMENT = create("block_chance_per_shield_enchantment", 1D, 1D);
	// arsonist attributes
	public static final RegistryObject<Attribute> DAMAGE_AGAINST_BURNING = create("damage_against_burning", 1D, 1D);
	public static final RegistryObject<Attribute> CHANCE_TO_IGNITE = create("chance_to_ignite", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_WEAPON_CHANCE_TO_IGNITE = create("crafted_weapon_chance_to_ignite", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_WEAPON_DAMAGE_AGAINST_BURNING = create("crafted_weapon_damage_against_burning", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_AGAINST_BURNING = create("crit_chance_against_burning", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_QUIVERS_DAMAGE_AGAINST_BURNING = create("crafted_quivers_damage_against_burning", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_QUIVERS_CHANCE_TO_IGNITE = create("crafted_quivers_chance_to_ignite", 1D, 1D);
	// scholar attributes
	public static final RegistryObject<Attribute> EXPERIENCE_PER_HOUR = create("experience_per_hour", 0D, 0D);
	public static final RegistryObject<Attribute> EXPERIENCE_FROM_ORE = create("experience_from_ore", 1D, 1D);
	public static final RegistryObject<Attribute> EXPERIENCE_FROM_MOBS = create("experience_from_mobs", 1D, 1D);
	// blacksmith attributes
	public static final RegistryObject<Attribute> CRAFTED_ARMOR_DEFENCE = create("crafted_armor_defence", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_MELEE_WEAPON_DAMAGE_BONUS = create("crafted_melee_weapon_damage_bonus", 0D, 0D);
	public static final RegistryObject<Attribute> CRAFTED_MELEE_WEAPON_ATTACK_SPEED = create("crafted_melee_weapon_attack_speed", 1D, 1D);
	public static final RegistryObject<Attribute> CHANCE_TO_CRAFT_TOUGHER_ARMOR = create("chance_to_craft_tougher_armor", 1D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_WITH_SHIELD = create("damage_with_shield", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_WITH_SHIELD = create("crit_chance_with_shield", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_WITH_SHIELD = create("crit_damage_with_shield", 1D, 1D);
	public static final RegistryObject<Attribute> ARMOR_WITH_SHIELD = create("armor_with_shield", 1D, 1D);
	public static final RegistryObject<Attribute> ATTACK_DAMAGE_PER_ARMOR = create("attack_damage_per_armor", 0D, 0D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_ARMOR = create("maximum_life_per_armor_bonus", 0D, 0D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_BOOTS_ARMOR = create("maximum_life_per_boots_armor_bonus", 0D, 0D);
	public static final RegistryObject<Attribute> CRAFTED_SHIELDS_ARMOR = create("crafted_shields_armor_bonus", 0D, 0D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_WITH_SHIELD = create("attack_speed_with_shield", 1D, 1D);
	public static final RegistryObject<Attribute> LIFE_REGENERATION_WITH_SHIELD = create("life_regeneration_with_shield", 0D, 0D);
	public static final RegistryObject<Attribute> CHESTPLATE_ARMOR = create("chestplate_armor", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_EQUIPMENT_DURABILITY = create("crafted_equipment_durability", 1D, 1D);
	// artisan attributes
	public static final RegistryObject<Attribute> CHANCE_TO_SAVE_CRAFITNG_MATERIALS = create("chance_to_save_crafitng_materials", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_ARMOR_MAXIMUM_LIFE = create("crafted_armor_maximum_life", 0D, 0D);
	public static final RegistryObject<Attribute> CRAFTED_SHIELDS_MAXIMUM_LIFE = create("crafted_shields_maximum_life", 0D, 0D);
	public static final RegistryObject<Attribute> CRAFTED_WEAPON_ATTACK_SPEED = create("crafted_weapon_attack_speed", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_SHIELDS_BLOCK_CHANCE = create("crafted_shields_block_chance", 1D, 1D);
	// soldier attributes
	public static final RegistryObject<Attribute> MELEE_DAMAGE = create("melee_damage", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_MELEE_WEAPON_CRIT_CHANCE = create("crafted_melee_weapon_crit_chance", 1D, 1D);
	public static final RegistryObject<Attribute> MELEE_CRIT_DAMAGE = create("melee_crit_damage", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_WEAPON_DOUBLE_LOOT_CHANCE = create("crafted_weapon_double_loot_chance", 1D, 1D);
	// miner attributes
	public static final RegistryObject<Attribute> MINING_SPEED = create("mining_speed", 1D, 1D);
	public static final RegistryObject<Attribute> MAXIMUM_EQUIPMENT_SOCKETS = create("maximum_equipment_sockets", 0D, 0D);
	public static final RegistryObject<Attribute> MAXIMUM_WEAPON_SOCKETS = create("maximum_weapon_sockets", 0D, 0D);
	public static final RegistryObject<Attribute> MAXIMUM_CHESTPLATE_SOCKETS = create("maximum_chestplate_sockets", 0D, 0D);
	public static final RegistryObject<Attribute> GEM_POWER = create("gem_power", 1D, 1D);
	public static final RegistryObject<Attribute> GEM_POWER_IN_ARMOR = create("gem_power_in_armor", 1D, 1D);
	public static final RegistryObject<Attribute> GEM_POWER_IN_WEAPON = create("gem_power_in_weapon", 1D, 1D);
	public static final RegistryObject<Attribute> ARMOR_PER_GEM_IN_HELMET = create("armor_per_gem_in_helmet", 0D, 0D);
	public static final RegistryObject<Attribute> DAMAGE_PER_GEM_IN_WEAPON = create("damage_per_gem_in_weapon", 1D, 1D);
	public static final RegistryObject<Attribute> ARMOR_PER_GEM_IN_CHESTPLATE = create("armor_per_gem_in_chestplate", 0D, 0D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_GEM_IN_ARMOR = create("maximum_life_per_gem_in_armor", 0D, 0D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_GEM_IN_HELMET = create("maximum_life_per_gem_in_helmet", 0D, 0D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_PER_GEM_IN_WEAPON = create("attack_speed_per_gem_in_weapon", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_PER_GEM_IN_WEAPON = create("crit_chance_per_gem_in_weapon", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_PER_GEM_IN_WEAPON = create("crit_damage_per_gem_in_weapon", 1D, 1D);
	public static final RegistryObject<Attribute> LIFE_REGENERATION_PER_GEM_IN_HELMET = create("life_regeneration_per_gem_in_helmet", 0D, 0D);
	public static final RegistryObject<Attribute> DAMAGE_WITH_GEM_IN_WEAPON = create("damage_with_gem_in_weapon", 1D, 1D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_WITH_GEM_IN_WEAPON = create("attack_speed_with_gem_in_weapon", 1D, 1D);
	// adventurer attributes
	public static final RegistryObject<Attribute> CRAFTED_BOOTS_SOCKETS = create("crafted_boots_sockets", 0D, 0D);
	public static final RegistryObject<Attribute> CRAFTED_BOOTS_MOVEMENT_SPEED = create("crafted_boots_movement_speed", 1D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_PER_DISTANCE_TO_SPAWN = create("damage_per_distance_to_spawn", 1D, 1D);
	// jeweler attributes
	public static final RegistryObject<Attribute> CRAFTED_RINGS_CRITICAL_DAMAGE = create("crafted_rings_critical_damage", 1D, 1D);
	public static final RegistryObject<Attribute> MAXIMUM_RING_SOCKETS = create("maximum_ring_sockets", 0D, 0D);
	public static final RegistryObject<Attribute> GEM_POWER_IN_JEWELRY = create("gem_power_in_jewelry", 1D, 1D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_EQUIPPED_JEWELRY = create("maximum_life_per_equipped_jewelry", 0D, 0D);
	public static final RegistryObject<Attribute> CRAFTED_NECKLACES_MAXIMUM_LIFE = create("crafted_necklaces_maximum_life", 0D, 0D);
	// hunter attributes
	public static final RegistryObject<Attribute> DOUBLE_LOOT_CHANCE = create("double_loot_chance", 1D, 1D);
	public static final RegistryObject<Attribute> TRIPLE_LOOT_CHANCE = create("triple_loot_chance", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_ARMOR_EVASION = create("crafted_armor_evasion", 1D, 1D);
	public static final RegistryObject<Attribute> PROJECTILE_DAMAGE = create("projectile_damage", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_RANGED_WEAPON_ATTACK_SPEED = create("crafted_ranged_weapon_attack_speed", 1D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_PER_DISTANCE_TO_ENEMY = create("damage_per_distance_to_enemy", 1D, 1D);
	public static final RegistryObject<Attribute> ARMOR_PER_EVASION = create("armor_per_evasion_bonus", 0D, 0D);
	public static final RegistryObject<Attribute> CRAFTED_HELMETS_SOCKETS = create("crafted_helmets_sockets", 0D, 0D);
	public static final RegistryObject<Attribute> CRAFTED_RANGED_WEAPON_SOCKETS = create("crafted_ranged_weapon_sockets", 0D, 0D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_EVASION = create("maximum_life_per_evasion", 0D, 0D);
	public static final RegistryObject<Attribute> EVASION_CHANCE_WHEN_WOUNDED = create("evasion_chance_when_wounded", 0D, 0D);
	public static final RegistryObject<Attribute> LIFE_PER_PROJECTILE_HIT = create("life_per_arrow_hit", 0D, 0D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_WITH_RANGED_WEAPON = create("attack_speed_with_ranged_weapon", 1D, 1D);
	public static final RegistryObject<Attribute> CHANCE_TO_RETRIEVE_ARROWS = create("chance_to_retrieve_arrows", 1D, 1D);
	public static final RegistryObject<Attribute> PROJECTILE_CRIT_CHANCE = create("projectile_crit_chance", 1D, 1D);
	public static final RegistryObject<Attribute> PROJECTILE_CRIT_DAMAGE = create("projectile_crit_damage", 1D, 1D);
	// ranger attributes
	public static final RegistryObject<Attribute> STEALTH = create("stealth", 1D, 1D);
	public static final RegistryObject<Attribute> JUMP_HEIGHT = create("jump_height", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_ARMOR_STEALTH = create("crafted_armor_stealth", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_BOOTS_STEALTH = create("crafted_boots_stealth", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_WEAPON_LIFE_PER_HIT = create("crafted_weapon_life_per_hit", 0D, 0D);
	// fletcher attributes
	public static final RegistryObject<Attribute> CRAFTED_QUIVERS_CAPACITY = create("crafted_quivers_capacity", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_QUIVERS_MAXIMUM_LIFE = create("crafted_quivers_maximum_life", 0D, 0D);
	public static final RegistryObject<Attribute> CHANCE_TO_SAVE_ARROWS_CRAFTING_MATERIALS = create("chance_to_save_arrows_crafting_materials", 1D,
			1D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_ARROW_IN_QUIVER = create("maximum_life_per_arrow_in_quiver", 0D, 0D);
	// cook attriutes
	public static final RegistryObject<Attribute> COOKED_FOOD_SATURATION = create("cooked_food_saturation", 1D, 1D);
	public static final RegistryObject<Attribute> COOKED_FOOD_DAMAGE_PER_SATURATION = create("cooked_food_damage_per_saturation", 1D, 1D);
	public static final RegistryObject<Attribute> COOKED_FOOD_LIFE_REGENERATION = create("cooked_food_life_regeneration", 0D, 0D);
	public static final RegistryObject<Attribute> COOKED_FOOD_CRIT_DAMAGE_PER_SATURATION = create("cooked_food_crit_damage_per_saturation", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_PER_SATISFIED_HUNGER = create("crit_damage_per_satisfied_hunger", 1D, 1D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_SATISFIED_HUNGER = create("maximum_life_per_satisfied_hunger", 0D, 0D);
	public static final RegistryObject<Attribute> COOKED_FOOD_HEALING_PER_SATURATION = create("cooked_food_healing_per_saturation", 1D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_IF_NOT_HUNGRY = create("damage_if_not_hungry", 1D, 1D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_IF_NOT_HUNGRY = create("attack_speed_if_not_hungry", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_IF_NOT_HUNGRY = create("crit_chance_if_not_hungry", 1D, 1D);
	public static final RegistryObject<Attribute> BLOCK_CHANCE_IF_NOT_HUNGRY = create("block_chance_if_not_hungry", 1D, 1D);
	public static final RegistryObject<Attribute> LIFE_ON_BLOCK_IF_NOT_HUNGRY = create("life_on_block_if_not_hungry", 0D, 0D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_IF_NOT_HUNGRY = create("maximum_life_if_not_hungry", 0D, 0D);
	public static final RegistryObject<Attribute> BLOCK_CHANCE_PER_SATISFIED_HUNGER = create("block_chance_per_satisfied_hunger", 1D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_PER_SATISFIED_HUNGER = create("damage_per_satisfied_hunger", 1D, 1D);
	// berserker skills
	public static final RegistryObject<Attribute> DAMAGE_IF_DAMAGED = create("damage_if_damaged", 1D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_IF_WOUNDED = create("damage_if_wounded", 1D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_AXES_CRIT_CHANCE = create("crafted_axes_crit_chance", 1D, 1D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_IF_WOUNDED = create("attack_speed_if_wounded", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_IF_WOUNDED = create("crit_chance_if_wounded", 1D, 1D);
	public static final RegistryObject<Attribute> LIFE_PER_HIT_IF_WOUNDED = create("life_per_hit_if_wounded", 0D, 0D);
	// fisherman attributes
	public static final RegistryObject<Attribute> DOUBLE_FISHING_LOOT_CHANCE = create("double_fishing_loot_chance", 1D, 1D);
	public static final RegistryObject<Attribute> EXPERIENCE_FROM_FISHING = create("experience_from_fishing", 1D, 1D);
	public static final RegistryObject<Attribute> LUCK_WHILE_FISHING = create("luck_while_fishing", 0D, 0D);
	// shared attributes
	public static final RegistryObject<Attribute> EVASION = create("evasion_chance", 1D, 1D);
	public static final RegistryObject<Attribute> LIFE_PER_HIT = create("life_per_hit", 0D, 0D);
	public static final RegistryObject<Attribute> LIFE_ON_BLOCK = create("life_on_block", 0D, 0D);
	public static final RegistryObject<Attribute> LIFE_REGENERATION = create("life_regeneration_bonus", 0D, 0D);
	public static final RegistryObject<Attribute> BLOCK_CHANCE = create("block_chance", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE = create("crit_damage", 1D, 1D);
	public static final RegistryObject<Attribute> CRIT_CHANCE = create("crit_chance", 1D, 1D);
	public static final RegistryObject<Attribute> GEM_DROP_CHANCE = create("chance_to_find_gemstone", 1D, 1D);

	private static RegistryObject<Attribute> create(String name, double defaultValue, double minValue) {
		String descriptionId = "attribute.name." + SkillTreeMod.MOD_ID + "." + name;
		return REGISTRY.register(name, () -> new RangedAttribute(descriptionId, defaultValue, minValue, 1024D).setSyncable(true));
	}

	@SubscribeEvent
	public static void attachAttributes(EntityAttributeModificationEvent event) {
		REGISTRY.getEntries().stream().map(RegistryObject::get).forEach(attribute -> event.add(EntityType.PLAYER, attribute));
	}
}
