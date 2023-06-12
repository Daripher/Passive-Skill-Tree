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
public class SkillTreeAttributes {
	public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, SkillTreeMod.MOD_ID);

	// alchemist attributes
	public static final RegistryObject<Attribute> ATTACK_SPEED_UNDER_POTION_EFFECT = rangedAttribute("player", "attack_speed_under_potion_effect", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> BREWED_POTIONS_DURATION = rangedAttribute("player", "brewed_potions_duration", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CAN_POISON_WEAPONS = rangedAttribute("player", "can_poison_weapons", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> CHANCE_TO_BREW_STRONGER_HARMFUL_POTION = rangedAttribute("player", "chance_to_brew_stronger_harmful_potion", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CHANCE_TO_BREW_STRONGER_BENEFICIAL_POTION = rangedAttribute("player", "chance_to_brew_stronger_beneficial_potion", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CHANCE_TO_BREW_STRONGER_POTION = rangedAttribute("player", "chance_to_brew_stronger_potion", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_AGAINST_POISONED = rangedAttribute("player", "crit_chance_against_poisoned", 1D, 1D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_AGAINST_POISONED = rangedAttribute("player", "damage_against_poisoned", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_UNDER_POTION_EFFECT = rangedAttribute("player", "damage_under_potion_effect", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_PER_POTION_EFFECT = rangedAttribute("player", "damage_per_potion_effect", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> EVASION_UNDER_POTION_EFFECT = rangedAttribute("player", "evasion_under_potion_effect", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> EVASION_PER_POTION_EFFECT = rangedAttribute("player", "evasion_per_potion_effect", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_UNDER_POTION_EFFECT = rangedAttribute("player", "maximum_life_under_potion_effect", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> CAN_MIX_POTIONS = rangedAttribute("player", "can_mix_potions", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_AGAINST_POISONED = rangedAttribute("player", "crit_damage_against_poisoned", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> LIFE_PER_HIT_UNDER_POTION_EFFECT = rangedAttribute("player", "life_per_hit_under_potion_effect", 0D, 0D, 10D);
	// enchanter attributes
	public static final RegistryObject<Attribute> DAMAGE_PER_ENCHANTMENT = rangedAttribute("player", "damage_per_enchantment", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_PER_ENCHANTMENT = rangedAttribute("player", "crit_chance_per_enchantment", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_PER_WEAPON_ENCHANTMENT = rangedAttribute("player", "crit_damage_per_weapon_enchantment", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ENCHANTMENT_LEVEL_REQUIREMENT_REDUCTION = rangedAttribute("player", "enchantment_level_requirement_reduction", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> CHANCE_TO_APPLY_BETTER_ENCHANTMENT = rangedAttribute("player", "chance_to_apply_better_enchantment", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CHANCE_TO_APPLY_BETTER_ARMOR_ENCHANTMENT = rangedAttribute("player", "chance_to_apply_better_armor_enchantment", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CHANCE_TO_APPLY_BETTER_WEAPON_ENCHANTMENT = rangedAttribute("player", "chance_to_apply_better_weapon_enchantment", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_WITH_ENCHANTED_ITEM = rangedAttribute("player", "maximum_life_with_enchanted_item", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> ARMOR_WITH_ENCHANTED_SHIELD = rangedAttribute("player", "armor_with_enchanted_shield", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_ENCHANTMENT = rangedAttribute("player", "maximum_life_per_enchantment", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL = rangedAttribute("player", "damage_per_weapon_enchantment_level", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ARMOR_PER_CHESTPLATE_ENCHANTMENT = rangedAttribute("player", "armor_per_chestplate_enchantment", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_ARMOR_ENCHANTMENT = rangedAttribute("player", "maximum_life_per_armor_enchantment", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> BLOCK_CHANCE_WITH_ENCHANTED_SHIELD = rangedAttribute("player", "block_chance_with_enchanted_shield", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> DAMAGE_WITH_ENCHANTED_WEAPON = rangedAttribute("player", "damage_with_enchanted_weapon", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_WITH_ENCHANTED_WEAPON = rangedAttribute("player", "crit_chance_with_enchanted_weapon", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_WITH_ENCHANTED_WEAPON = rangedAttribute("player", "attack_speed_with_enchanted_weapon", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> FREE_ENCHANTMENT_CHANCE = rangedAttribute("player", "free_enchantment_chance", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> LIFE_ON_BLOCK_PER_SHIELD_ENCHANTMENT = rangedAttribute("player", "life_on_block_per_shield_enchantment", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> BLOCK_CHANCE_PER_SHIELD_ENCHANTMENT = rangedAttribute("player", "block_chance_per_shield_enchantment", 1D, 1D, 2D);
	// blacksmith attributes
	public static final RegistryObject<Attribute> CRAFTED_ARMOR_DEFENCE = rangedAttribute("player", "crafted_armor_defence", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_WEAPON_DAMAGE_BONUS = rangedAttribute("player", "crafted_weapon_damage_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_WEAPON_ATTACK_SPEED = rangedAttribute("player", "crafted_weapon_attack_speed", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CHANCE_TO_CRAFT_TOUGHER_ARMOR = rangedAttribute("player", "chance_to_craft_tougher_armor", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_WITH_SHIELD = rangedAttribute("player", "damage_with_shield", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_WITH_SHIELD = rangedAttribute("player", "crit_chance_with_shield", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_WITH_SHIELD = rangedAttribute("player", "crit_damage_with_shield", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ARMOR_WITH_SHIELD = rangedAttribute("player", "armor_with_shield", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ATTACK_DAMAGE_PER_ARMOR = rangedAttribute("player", "attack_damage_per_armor", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_ARMOR = rangedAttribute("player", "maximum_life_per_armor_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_BOOTS_ARMOR = rangedAttribute("player", "maximum_life_per_boots_armor_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_SHIELDS_ARMOR_BONUS = rangedAttribute("player", "crafted_shields_armor_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_WITH_SHIELD = rangedAttribute("player", "attack_speed_with_shield", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> LIFE_REGENERATION_WITH_SHIELD = rangedAttribute("player", "life_regeneration_with_shield", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> CHESTPLATE_ARMOR = rangedAttribute("player", "chestplate_armor", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_EQUIPMENT_DURABILITY = rangedAttribute("player", "crafted_equipment_durability", 1D, 1D, 10D);
	// miner attributes
	public static final RegistryObject<Attribute> MINING_SPEED = rangedAttribute("player", "mining_speed", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_SOCKETS = rangedAttribute("player", "maximum_sockets", 0D, 0D, 5D);
	public static final RegistryObject<Attribute> MAXIMUM_WEAPON_SOCKETS = rangedAttribute("player", "maximum_weapon_sockets", 0D, 0D, 5D);
	public static final RegistryObject<Attribute> MAXIMUM_CHESTPLATE_SOCKETS = rangedAttribute("player", "maximum_chestplate_sockets", 0D, 0D, 5D);
	public static final RegistryObject<Attribute> GEM_POWER = rangedAttribute("player", "gemstones_strength", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> GEM_POWER_IN_ARMOR = rangedAttribute("player", "gemstones_strength_in_armor", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> GEM_POWER_IN_WEAPON = rangedAttribute("player", "gemstones_strength_in_weapon", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> PICKAXE_DAMAGE_BONUS = rangedAttribute("player", "pickaxe_damage_bonus", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> PICKAXE_DAMAGE = rangedAttribute("player", "pickaxe_damage", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_WITH_PICKAXE = rangedAttribute("player", "attack_speed_with_pickaxe", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ARMOR_PER_GEMSTONE_IN_HELMET = rangedAttribute("player", "armor_per_gemstone_in_helmet", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_PER_GEMSTONE_IN_WEAPON = rangedAttribute("player", "damage_per_gemstone_in_weapon", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ARMOR_PER_GEMSTONE_IN_CHESTPLATE = rangedAttribute("player", "armor_per_gemstone_in_chestplate_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_GEMSTONE_IN_ARMOR = rangedAttribute("player", "maximum_life_per_gemstone_in_armor_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_GEMSTONE_IN_HELMET = rangedAttribute("player", "maximum_life_per_gemstone_in_helmet", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_PER_GEMSTONE_IN_WEAPON = rangedAttribute("player", "attack_speed_per_gemstone_in_weapon", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_PER_GEMSTONE_IN_WEAPON = rangedAttribute("player", "crit_chance_per_gemstone_in_weapon", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_PER_GEMSTONE_IN_WEAPON = rangedAttribute("player", "crit_damage_per_gemstone_in_weapon", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> LIFE_REGENERATION_PER_GEMSTONE_IN_HELMET = rangedAttribute("player", "life_regeneration_per_gemstone_in_helmet", 0D, 0D, 10D);
	// hunter attributes
	public static final RegistryObject<Attribute> DOUBLE_LOOT_CHANCE = rangedAttribute("player", "double_loot_chance", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> TRIPLE_LOOT_CHANCE = rangedAttribute("player", "triple_loot_chance", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> CRAFTED_ARMOR_EVASION = rangedAttribute("player", "crafted_armor_evasion", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> ARROW_DAMAGE = rangedAttribute("player", "arrow_damage", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ARROW_DAMAGE_BONUS = rangedAttribute("player", "arrow_damage_bonus", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> ARROW_CRIT_DAMAGE = rangedAttribute("player", "arrow_crit_damage", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_BOWS_ATTACK_SPEED = rangedAttribute("player", "crafted_bows_attack_speed", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ARROW_DAMAGE_PER_DISTANCE = rangedAttribute("player", "arrow_damage_per_distance", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> ARMOR_PER_EVASION = rangedAttribute("player", "armor_per_evasion_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_HELMETS_ADDITIONAL_GEMSTONE_SLOTS = rangedAttribute("player", "crafted_helmets_additional_gemstone_slots", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_BOWS_ADDITIONAL_GEMSTONE_SLOTS = rangedAttribute("player", "crafted_bows_additional_gemstone_slots", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_EVASION = rangedAttribute("player", "maximum_life_per_evasion", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> EVASION_CHANCE_WHEN_WOUNDED = rangedAttribute("player", "evasion_chance_when_wounded", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> LIFE_PER_ARROW_HIT = rangedAttribute("player", "life_per_arrow_hit", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_WITH_BOW = rangedAttribute("player", "attack_speed_with_bow", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_PER_ARROW_IN_ENEMY = rangedAttribute("player", "damage_per_arrow_in_enemy", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_WITH_BOW = rangedAttribute("player", "crit_chance_with_bow", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> CHANCE_TO_RETRIEVE_ARROWS = rangedAttribute("player", "chance_to_retrieve_arrows", 1D, 1D, 2D);
	// cook attriutes
	public static final RegistryObject<Attribute> COOKED_FOOD_SATURATION = rangedAttribute("player", "cooked_food_saturation", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> COOKED_FOOD_DAMAGE_PER_SATURATION = rangedAttribute("player", "cooked_food_damage_per_saturation", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> COOKED_FOOD_LIFE_REGENERATION = rangedAttribute("player", "cooked_food_life_regeneration", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> COOKED_FOOD_CRITICAL_DAMAGE_PER_SATURATION = rangedAttribute("player", "cooked_food_critical_damage_per_saturation", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_PER_SATISFIED_HUNGER = rangedAttribute("player", "crit_damage_per_satisfied_hunger", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_SATISFIED_HUNGER = rangedAttribute("player", "maximum_life_per_satisfied_hunger", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> COOKED_FOOD_HEALING_PER_SATURATION = rangedAttribute("player", "cooked_food_healing_per_saturation", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_IF_NOT_HUNGRY = rangedAttribute("player", "damage_if_not_hungry", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_IF_NOT_HUNGRY = rangedAttribute("player", "attack_speed_if_not_hungry", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_IF_NOT_HUNGRY = rangedAttribute("player", "crit_chance_if_not_hungry", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> BLOCK_CHANCE_IF_NOT_HUNGRY = rangedAttribute("player", "block_chance_if_not_hungry", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> LIFE_ON_BLOCK_IF_NOT_HUNGRY = rangedAttribute("player", "life_on_block_if_not_hungry", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_IF_NOT_HUNGRY = rangedAttribute("player", "maximum_life_if_not_hungry", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> BLOCK_CHANCE_PER_SATISFIED_HUNGER = rangedAttribute("player", "block_chance_per_satisfied_hunger", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> DAMAGE_PER_SATISFIED_HUNGER = rangedAttribute("player", "damage_per_satisfied_hunger", 1D, 1D, 2D);
	// shared attributes
	public static final RegistryObject<Attribute> EVASION_CHANCE = rangedAttribute("player", "evasion_chance", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> LIFE_PER_HIT = rangedAttribute("player", "life_per_hit", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> LIFE_ON_BLOCK = rangedAttribute("player", "life_on_block", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> LIFE_REGENERATION = rangedAttribute("player", "life_regeneration_bonus", 0D, 0D, 100D);
	public static final RegistryObject<Attribute> BLOCK_CHANCE = rangedAttribute("player", "block_chance", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE = rangedAttribute("player", "crit_damage", 1D, 1D, 5D);
	public static final RegistryObject<Attribute> CRIT_CHANCE = rangedAttribute("player", "crit_chance", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> CHANCE_TO_FIND_GEMSTONE = rangedAttribute("player", "chance_to_find_gemstone", 1D, 1D, 2D);

	private static RegistryObject<Attribute> rangedAttribute(String category, String name, double defaultValue, double minValue, double maxValue) {
		return REGISTRY.register(category + "." + name, () -> new RangedAttribute(category + "." + name, defaultValue, minValue, maxValue).setSyncable(true));
	}

	@SubscribeEvent
	public static void attachModAttributes(EntityAttributeModificationEvent event) {
		var modAttributes = SkillTreeAttributes.REGISTRY.getEntries().stream().map(RegistryObject::get);
		modAttributes.forEach(attribute -> event.add(EntityType.PLAYER, attribute));
	}
}
