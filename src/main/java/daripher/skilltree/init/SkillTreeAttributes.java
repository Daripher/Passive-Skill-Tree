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
	public static final RegistryObject<Attribute> BREWED_POTIONS_DURATION_MULTIPLIER = rangedAttribute("player", "brewed_potions_duration_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> BREWED_HARMFUL_POTIONS_STRENGTH_MULTIPLIER = rangedAttribute("player", "brewed_harmful_potions_strength_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> BREWED_BENEFICIAL_POTIONS_STRENGTH_MULTIPLIER = rangedAttribute("player", "brewed_beneficial_potions_strength_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> BREWED_POTIONS_STRENGTH_MULTIPLIER = rangedAttribute("player", "brewed_potions_strength_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_AGAINST_POISONED_MULTIPLIER = rangedAttribute("player", "damage_against_poisoned_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_UNDER_POTION_EFFECT_MULTIPLIER = rangedAttribute("player", "damage_under_potion_effect_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_AGAINST_POISONED_MULTIPLIER = rangedAttribute("player", "crit_chance_against_poisoned_multiplier", 1D, 1D, 1D);
	public static final RegistryObject<Attribute> EVASION_UNDER_POTION_EFFECT_MULTIPLIER = rangedAttribute("player", "evasion_under_potion_effect_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_UNDER_POTION_EFFECT_MULTIPLIER = rangedAttribute("player", "attack_speed_under_potion_effect_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_UNDER_POTION_EFFECT = rangedAttribute("player", "maximum_life_under_potion_effect", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_POTION_EFFECT = rangedAttribute("player", "maximum_life_per_potion_effect", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_PER_POTION_EFFECT_MULTIPLIER = rangedAttribute("player", "damage_per_potion_effect_multiplier", 1D, 1D, 10D);
	// enchanter attributes
	public static final RegistryObject<Attribute> DAMAGE_PER_ENCHANTMENT_MULTIPLIER = rangedAttribute("player", "damage_per_enchantment_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_PER_ENCHANTMENT_MULTIPLIER = rangedAttribute("player", "crit_chance_per_enchantment_multiplier", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_PER_ENCHANTMENT_MULTIPLIER = rangedAttribute("player", "crit_damage_per_enchantment_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ENCHANTMENT_LEVEL_REQUIREMENT_MULTIPLIER = rangedAttribute("player", "enchantment_level_requirement_decrease", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> ENCHANTMENTS_AMPLIFICATION_CHANCE = rangedAttribute("player", "enchantments_amplification_chance", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> ARMOR_ENCHANTMENTS_AMPLIFICATION_CHANCE = rangedAttribute("player", "armor_enchantments_amplification_chance", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> WEAPON_ENCHANTMENTS_AMPLIFICATION_CHANCE = rangedAttribute("player", "weapon_enchantments_amplification_chance", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_WITH_ENCHANTED_ITEM = rangedAttribute("player", "maximum_life_with_enchanted_item", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> ARMOR_WITH_ENCHANTED_SHIELD = rangedAttribute("player", "armor_with_enchanted_shield", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_ENCHANTMENT = rangedAttribute("player", "maximum_life_per_enchantment", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_PER_WEAPON_ENCHANTMENT_LEVEL_MULTIPLIER = rangedAttribute("player", "damage_per_weapon_enchantment_level_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ARMOR_PER_CHESTPLATE_ENCHANTMENT = rangedAttribute("player", "armor_per_chestplate_enchantment", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_ARMOR_ENCHANTMENT = rangedAttribute("player", "maximum_life_per_armor_enchantment", 0D, 0D, 10D);
	// blacksmith attributes
	public static final RegistryObject<Attribute> CRAFTED_ARMOR_DEFENCE_MULTIPLIER = rangedAttribute("player", "crafted_armor_defence_bonus", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_WEAPON_DAMAGE_BONUS = rangedAttribute("player", "crafted_weapon_damage_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_WEAPON_ATTACK_SPEED_MULTIPLIER = rangedAttribute("player", "crafted_weapon_attack_speed_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_ARMOR_BONUS_TOUGHNESS_CHANCE_MULTIPLIER = rangedAttribute("player", "crafted_armor_bonus_toughness_chance_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_WITH_SHIELD_MULTIPLIER = rangedAttribute("player", "damage_with_shield_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_WITH_SHIELD_MULTIPLIER = rangedAttribute("player", "crit_chance_with_shield_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CHESTPLATE_ARMOR_MULTIPLIER = rangedAttribute("player", "chestplate_armor_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_WITH_SHIELD_MULTIPLIER = rangedAttribute("player", "crit_damage_with_shield_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ARMOR_WITH_SHIELD_MULTIPLIER = rangedAttribute("player", "armor_with_shield_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ATTACK_DAMAGE_PER_ARMOR = rangedAttribute("player", "damage_per_armor_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_ARMOR = rangedAttribute("player", "maximum_life_per_armor_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_BOOTS_ARMOR = rangedAttribute("player", "maximum_life_per_boots_armor_bonus", 0D, 0D, 10D);
	// miner attributes
	public static final RegistryObject<Attribute> MINING_SPEED_MULTIPLIER = rangedAttribute("player", "mining_speed_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_GEMSTONE_SLOTS_BONUS = rangedAttribute("player", "maximum_gemstone_slots_bonus", 0D, 0D, 5D);
	public static final RegistryObject<Attribute> GEMSTONES_STRENGTH_MULTIPLIER = rangedAttribute("player", "gemstones_strength_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> GEMSTONES_STRENGTH_IN_ARMOR_MULTIPLIER = rangedAttribute("player", "gemstones_strength_in_armor_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> GEMSTONES_STRENGTH_IN_WEAPON_MULTIPLIER = rangedAttribute("player", "gemstones_strength_in_weapon_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> PICKAXE_DAMAGE_BONUS = rangedAttribute("player", "pickaxe_damage_bonus", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> PICKAXE_DAMAGE_MULTIPLIER = rangedAttribute("player", "pickaxe_damage_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> PICKAXE_ATTACK_SPEED_MULTIPLIER = rangedAttribute("player", "pickaxe_attack_speed_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ARMOR_PER_GEMSTONE_IN_HELMET_BONUS = rangedAttribute("player", "armor_per_gemstone_in_helmet_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_PER_GEMSTONE_IN_WEAPON_MULTIPLIER = rangedAttribute("player", "damage_per_gemstone_in_weapon_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ARMOR_PER_GEMSTONE_IN_CHESTPLATE = rangedAttribute("player", "armor_per_gemstone_in_chestplate_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_GEMSTONE_IN_ARMOR = rangedAttribute("player", "maximum_life_per_gemstone_in_armor_bonus", 0D, 0D, 10D);
	// hunter attributes
	public static final RegistryObject<Attribute> DOUBLE_LOOT_CHANCE = rangedAttribute("player", "double_loot_chance", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> TRIPLE_LOOT_CHANCE = rangedAttribute("player", "triple_loot_chance", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_ARMOR_EVASION_BONUS = rangedAttribute("player", "crafted_armor_evasion_bonus", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> ARROW_DAMAGE_MULTIPLIER = rangedAttribute("player", "arrow_damage_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ARROW_DAMAGE_BONUS = rangedAttribute("player", "arrow_damage_bonus", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> ARROW_CRIT_DAMAGE_MULTIPLIER = rangedAttribute("player", "arrow_crit_damage_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_BOWS_CHARGE_SPEED_BONUS = rangedAttribute("player", "crafted_bows_charge_speed_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> ARROW_DAMAGE_PER_DISTANCE_MULTIPLIER = rangedAttribute("player", "arrow_damage_per_distance_multiplier", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> BOW_CHARGE_SPEED_MULTIPLIER = rangedAttribute("player", "bow_charge_speed_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ARMOR_PER_EVASION = rangedAttribute("player", "armor_per_evasion_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_HELMETS_ADDITIONAL_GEMSTONE_SLOTS = rangedAttribute("player", "crafted_helmets_additional_gemstone_slots", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_EVASION = rangedAttribute("player", "maximum_life_per_evasion", 0D, 0D, 1D);
	// cook attriutes
	public static final RegistryObject<Attribute> COOKED_FOOD_RESTORATION_MULTIPLIER = rangedAttribute("player", "cooked_food_restoration_bonus", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> COOKED_FOOD_DAMAGE_PER_RESTORATION_MULTIPLIER = rangedAttribute("player", "cooked_food_damage_per_restoration_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> COOKED_FOOD_LIFE_REGENERATION_PER_RESTORATION = rangedAttribute("player", "cooked_food_life_regeneration_per_restoration", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> COOKED_FOOD_CRITICAL_DAMAGE_PER_RESTORATION_MULTIPLIER = rangedAttribute("player", "cooked_food_critical_damage_per_restoration_multiplier", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> AXE_ATTACK_SPEED_MULTIPLIER = rangedAttribute("player", "axe_attack_speed_multiplier", 1D, 1D, 5D);
	public static final RegistryObject<Attribute> AXE_CRIT_CHANCE_MULTIPLIER = rangedAttribute("player", "axe_crit_chance_multiplier", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_IF_ATE_RECENTLY = rangedAttribute("player", "maximum_life_if_ate_recently", 0D, 0D, 50D);
	public static final RegistryObject<Attribute> DAMAGE_MULTIPLIER_IF_ATE_RECENTLY = rangedAttribute("player", "damage_multiplier_if_ate_recently", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_MULTIPLIER_PER_SATISFIED_HUNGER = rangedAttribute("player", "crit_damage_multiplier_per_satisfied_hunger", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> MAXIMUM_LIFE_PER_SATISFIED_HUNGER = rangedAttribute("player", "maximum_life_per_satisfied_hunger", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> COOKED_FOOD_HEALING_PER_RESTORATION_MULTIPLIER = rangedAttribute("player", "cooked_food_healing_per_restoration_multiplier", 1D, 1D, 10D);
	// shared attributes
	public static final RegistryObject<Attribute> EVASION_CHANCE_MULTIPLIER = rangedAttribute("player", "evasion_chance_multiplier", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> GEMSTONE_FINDING_CHANCE = rangedAttribute("player", "gemstone_finding_chance", 0.05D, 0D, 1D);
	public static final RegistryObject<Attribute> LIFE_PER_HIT = rangedAttribute("player", "life_per_hit", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> LIFE_ON_BLOCK = rangedAttribute("player", "life_on_block", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> LIFE_REGENERATION_BONUS = rangedAttribute("player", "life_regeneration_bonus", 0D, 0D, 100D);
	public static final RegistryObject<Attribute> BLOCK_CHANCE_MULTIPLIER = rangedAttribute("player", "block_chance_multiplier", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_MULTIPLIER = rangedAttribute("player", "crit_damage_multiplier", 1D, 1D, 5D);

	private static RegistryObject<Attribute> rangedAttribute(String category, String name, double defaultValue, double minValue, double maxValue) {
		return REGISTRY.register(category + "." + name, () -> new RangedAttribute(category + "." + name, defaultValue, minValue, maxValue).setSyncable(true));
	}

	@SubscribeEvent
	public static void attachModAttributes(EntityAttributeModificationEvent event) {
		var modAttributes = SkillTreeAttributes.REGISTRY.getEntries().stream().map(RegistryObject::get);
		modAttributes.forEach(attribute -> event.add(EntityType.PLAYER, attribute));
	}
}
