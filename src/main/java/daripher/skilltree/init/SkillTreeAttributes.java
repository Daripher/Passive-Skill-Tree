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
	public static final RegistryObject<Attribute> BREWED_POTIONS_DURATION_BONUS = rangedAttribute("player", "brewed_potions_duration_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> BREWED_POTIONS_AMPLIFICATION_CHANCE = rangedAttribute("player", "brewed_potions_amplification_chance", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_BONUS_AGAINST_POISONED = rangedAttribute("player", "damage_bonus_against_poisoned", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> DAMAGE_BONUS_UNDER_POTION_EFFECT = rangedAttribute("player", "damage_bonus_under_potion_effect", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_BONUS_AGAINST_POISONED = rangedAttribute("player", "crit_chance_bonus_against_poisoned", 0D, 0D, 1D);
	// enchanter attributes
	public static final RegistryObject<Attribute> DAMAGE_BONUS_PER_ENCHANTED_ITEM = rangedAttribute("player", "damage_bonus_per_enchanted_item", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_BONUS_PER_ENCHANTED_ITEM = rangedAttribute("player", "crit_chance_bonus_per_enchanted_item", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_BONUS_PER_ENCHANTED_ITEM = rangedAttribute("player", "crit_damage_bonus_per_enchanted_item", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> ENCHANTMENT_LEVEL_REQUIREMENT_DECREASE = rangedAttribute("player", "enchantment_level_requirement_decrease", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> APPLIED_ENCHANTMENTS_AMPLIFICATION_CHANCE = rangedAttribute("player", "applied_enchantments_amplification_chance", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> FREE_ENCHANTMENT_CHANCE = rangedAttribute("player", "free_enchantment_chance", 0D, 0D, 1D);
	// blacksmith attributes
	public static final RegistryObject<Attribute> CRAFTED_ARMOR_DEFENCE_BONUS = rangedAttribute("player", "crafted_armor_defence_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_WEAPON_DAMAGE_BONUS = rangedAttribute("player", "crafted_weapon_damage_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_WEAPON_ATTACK_SPEED_BONUS = rangedAttribute("player", "crafted_weapon_attack_speed_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> TOUGHER_ARMOR_CRAFTING_CHANCE = rangedAttribute("player", "tougher_armor_crafting_chance", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_BONUS_WITH_SHIELD = rangedAttribute("player", "damage_bonus_with_shield", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> DOUBLE_DAMAGE_CHANCE_WITH_SHIELD = rangedAttribute("player", "double_damage_chance_with_shield", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_BONUS_PER_ARMOR_POINT = rangedAttribute("player", "crit_chance_bonus_per_armor_point", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> CHANCE_TO_IGNORE_ARMOR = rangedAttribute("player", "chance_to_ignore_armor", 0D, 0D, 1D);
	// miner attributes
	public static final RegistryObject<Attribute> MINING_SPEED_BONUS = rangedAttribute("player", "mining_speed_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> MINERALS_DUPLICATION_CHANCE = rangedAttribute("player", "minerals_duplication_chance", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_BONUS_WITH_PICKAXE = rangedAttribute("player", "damage_bonus_with_pickaxe", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> APPLIED_GEMSTONES_STRENGTH_BONUS = rangedAttribute("player", "applied_gemstones_strength_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> ATTACK_SPEED_BONUS_WITH_PICKAXE = rangedAttribute("player", "attack_speed_bonus_with_pickaxe", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> GEMSTONE_FINDING_CHANCE = rangedAttribute("player", "gemstone_finding_chance", 0D, 0D, 1D);
	// hunter attributes
	public static final RegistryObject<Attribute> LOOT_DUPLICATION_CHANCE = rangedAttribute("player", "loot_duplication_chance", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_BOWS_CHARGE_SPEED_BONUS = rangedAttribute("player", "crafted_bows_charge_speed_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_BOWS_DAMAGE_BONUS = rangedAttribute("player", "crafted_bows_damage_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> CRAFTED_BOWS_DOUBLE_DAMAGE_CHANCE = rangedAttribute("player", "crafted_bows_double_damage_chance", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> CRAFTED_BOWS_ARMOR_REDUCTION = rangedAttribute("player", "crafted_bows_armor_reduction", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> ARROW_DAMAGE_MULTIPLIER_PER_DISTANCE = rangedAttribute("player", "arrow_damage_multiplier_per_distance", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> ARROW_CRIT_DAMAGE_BONUS = rangedAttribute("player", "arrow_crit_damage_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> ARROW_DAMAGE_MULTIPLIER = rangedAttribute("player", "arrow_damage_bonus", 1D, 1D, 10D);
	public static final RegistryObject<Attribute> ARROW_DOUBLE_DAMAGE_CHANCE_MULTIPLIER = rangedAttribute("player", "arrow_double_damage_chance", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> ARROW_ARMOR_REDUCTION_MULTIPLIER = rangedAttribute("player", "arrow_armor_reduction", 1D, 1D, 2D);
	public static final RegistryObject<Attribute> BOW_CHARGE_SPEED_MULTIPLIER = rangedAttribute("player", "bow_charge_speed", 1D, 1D, 10D);
	// cook attriutes
	public static final RegistryObject<Attribute> COOKED_FOOD_RESTORATION_BONUS = rangedAttribute("player", "cooked_food_restoration_bonus", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> COOKED_FOOD_HEALING_PER_RESTORATION = rangedAttribute("player", "cooked_food_healing_per_restoration", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> COOKED_FOOD_CRIT_CHANCE_BONUS = rangedAttribute("player", "cooked_food_crit_chance_bonus", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> COOKED_FOOD_DAMAGE_PER_RESTORATION_BONUS = rangedAttribute("player", "cooked_food_damage_per_restoration_bonus", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> COOKED_FOOD_ATTACK_SPEED_BONUS = rangedAttribute("player", "cooked_food_attack_speed_bonus", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> DAMAGE_BONUS_WHEN_FULL = rangedAttribute("player", "damage_bonus_when_full", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_BONUS_WHEN_FULL = rangedAttribute("player", "crit_chance_bonus_when_full", 0D, 0D, 1D);
	public static final RegistryObject<Attribute> CRIT_DAMAGE_BONUS_IF_ATE_RECENTLY = rangedAttribute("player", "crit_damage_bonus_if_ate_recently", 0D, 0D, 1D);
	// other attributes
	public static final RegistryObject<Attribute> HEALING_PER_HIT = rangedAttribute("player", "healing_per_hit", 0D, 0D, 10D);
	public static final RegistryObject<Attribute> HEALTH_REGENERATION_BONUS = rangedAttribute("player", "health_regeneration_bonus", 0D, 0D, 100D);
	public static final RegistryObject<Attribute> CRIT_CHANCE_BONUS = rangedAttribute("player", "crit_chance_bonus", 0D, 0D, 1D);

	private static RegistryObject<Attribute> rangedAttribute(String category, String name, double defaultValue, double minValue, double maxValue) {
		return REGISTRY.register(category + "." + name, () -> new RangedAttribute(category + "." + name, defaultValue, minValue, maxValue).setSyncable(true));
	}

	@SubscribeEvent
	public static void attachModAttributes(EntityAttributeModificationEvent event) {
		var modAttributes = SkillTreeAttributes.REGISTRY.getEntries().stream().map(RegistryObject::get);
		modAttributes.forEach(attribute -> event.add(EntityType.PLAYER, attribute));
	}
}
