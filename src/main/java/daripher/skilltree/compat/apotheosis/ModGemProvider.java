package daripher.skilltree.compat.apotheosis;

import static shadows.apotheosis.adventure.loot.LootCategory.BOOTS;
import static shadows.apotheosis.adventure.loot.LootCategory.BOW;
import static shadows.apotheosis.adventure.loot.LootCategory.CHESTPLATE;
import static shadows.apotheosis.adventure.loot.LootCategory.CROSSBOW;
import static shadows.apotheosis.adventure.loot.LootCategory.HEAVY_WEAPON;
import static shadows.apotheosis.adventure.loot.LootCategory.HELMET;
import static shadows.apotheosis.adventure.loot.LootCategory.LEGGINGS;
import static shadows.apotheosis.adventure.loot.LootCategory.PICKAXE;
import static shadows.apotheosis.adventure.loot.LootCategory.SHIELD;
import static shadows.apotheosis.adventure.loot.LootCategory.SHOVEL;
import static shadows.apotheosis.adventure.loot.LootCategory.SWORD;
import static shadows.apotheosis.adventure.loot.LootCategory.TRIDENT;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.mojang.serialization.JsonOps;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeAttributes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import shadows.apotheosis.adventure.affix.socket.gem.Gem;
import shadows.apotheosis.adventure.affix.socket.gem.GemClass;
import shadows.apotheosis.adventure.affix.socket.gem.bonus.AttributeBonus;
import shadows.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.util.StepFunction;

public class ModGemProvider extends JsonCodecProvider<Gem> {
	private static final GemClass ARMOR_CLASS = new GemClass("any_armor", Set.of(BOOTS, CHESTPLATE, HELMET, LEGGINGS));
	private static final GemClass SHIELDS_CLASS = new GemClass("shield", Set.of(SHIELD));
	private static final GemClass ARMOR_OR_SHIELD_CLASS = new GemClass("armor_or_shield", Set.of(SHIELD, BOOTS, CHESTPLATE, HELMET, LEGGINGS));
	private static final GemClass MELEE_WEAPON_CLASS = new GemClass("melee_weapon", Set.of(HEAVY_WEAPON, PICKAXE, SHOVEL, SWORD, TRIDENT));
	private static final GemClass WEAPON_CLASS = new GemClass("any_weapon", Set.of(HEAVY_WEAPON, PICKAXE, SHOVEL, SWORD, TRIDENT, BOW));
	private static final GemClass BOOTS_CLASS = new GemClass("boots", Set.of(BOOTS));
	private static final GemClass HELMET_CLASS = new GemClass("helmet", Set.of(HELMET));
	private static final GemClass PICKAXE_CLASS = new GemClass("pickaxe", Set.of(PICKAXE));
	private static final GemClass WEAPON_WITHOUT_PICKAXE_CLASS = new GemClass("other_weapon", Set.of(HEAVY_WEAPON, SHOVEL, SWORD, TRIDENT, BOW));
	private static final GemClass ANITHYNG_CLASS = new GemClass("anything", Set.of(BOW, CROSSBOW, PICKAXE, SHOVEL, HEAVY_WEAPON, HELMET, CHESTPLATE, LEGGINGS, BOOTS, SHIELD, TRIDENT, SWORD));

	public ModGemProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, existingFileHelper, SkillTreeMod.MOD_ID, JsonOps.INSTANCE, PackType.SERVER_DATA, "gems", Gem.CODEC, generateGems());
	}

	private static Map<ResourceLocation, Gem> generateGems() {
		AttributeBonus.initCodecs();
		var gems = new HashMap<ResourceLocation, Gem>();
		gems.put(new ResourceLocation(SkillTreeMod.MOD_ID, "ruby"), createGem(
				new AttributeBonus(ARMOR_CLASS, SkillTreeAttributes.LIFE_REGENERATION.get(), Operation.ADDITION, generateBonuses(0.1F, 5, 0.1F)),
				new AttributeBonus(SHIELDS_CLASS, Attributes.MAX_HEALTH, Operation.ADDITION, generateBonuses(0.5F, 5, 0.5F))));
		gems.put(new ResourceLocation(SkillTreeMod.MOD_ID, "onyx"), createGem(
				new AttributeBonus(MELEE_WEAPON_CLASS, Attributes.ATTACK_DAMAGE, Operation.ADDITION, generateBonuses(0.5F, 5, 0.5F)),
				new AttributeBonus(ARMOR_OR_SHIELD_CLASS, Attributes.ARMOR, Operation.ADDITION, generateBonuses(0.5F, 5, 0.5F))));
		gems.put(new ResourceLocation(SkillTreeMod.MOD_ID, "moonstone"), createGem(
				new AttributeBonus(PICKAXE_CLASS, SkillTreeAttributes.MINING_SPEED.get(), Operation.MULTIPLY_BASE, generateBonuses(0.01F, 5, 0.02F)),
				new AttributeBonus(WEAPON_WITHOUT_PICKAXE_CLASS, Attributes.ATTACK_SPEED, Operation.MULTIPLY_BASE, generateBonuses(0.005F, 5, 0.01F)),
				new AttributeBonus(BOOTS_CLASS, Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_BASE, generateBonuses(0.01F, 5, 0.01F))));
		gems.put(new ResourceLocation(SkillTreeMod.MOD_ID, "opal"), createGem(
				new AttributeBonus(WEAPON_CLASS, SkillTreeAttributes.LIFE_PER_HIT.get(), Operation.ADDITION, generateBonuses(0.1F, 5, 0.1F)),
				new AttributeBonus(SHIELDS_CLASS, SkillTreeAttributes.LIFE_ON_BLOCK.get(), Operation.ADDITION, generateBonuses(0.1F, 5, 0.1F))));
		gems.put(new ResourceLocation(SkillTreeMod.MOD_ID, "adamite"), createGem(
				new AttributeBonus(WEAPON_CLASS, SkillTreeAttributes.DAMAGE_AGAINST_POISONED.get(), Operation.MULTIPLY_BASE, generateBonuses(0.01F, 5, 0.015F)),
				new AttributeBonus(HELMET_CLASS, SkillTreeAttributes.CHANCE_TO_BREW_STRONGER_HARMFUL_POTION.get(), Operation.MULTIPLY_BASE, generateBonuses(0.01F, 5, 0.01F))));
		gems.put(new ResourceLocation(SkillTreeMod.MOD_ID, "third_eye"), createGem(0, 0.4F, true,
				new AttributeBonus(HELMET_CLASS, SkillTreeAttributes.MAXIMUM_SOCKETS.get(), Operation.ADDITION, generateBonus(LootRarity.MYTHIC, 1F, 1, 0F))));
		gems.put(new ResourceLocation(SkillTreeMod.MOD_ID, "citrine"), createGem(
				new AttributeBonus(ANITHYNG_CLASS, Attributes.LUCK, Operation.ADDITION, generateBonuses(0.1F, 5, 0.15F))));
		return gems;
	}

	private static Map<LootRarity, StepFunction> generateBonuses(float min, int steps, float step) {
		var bonuses = new HashMap<LootRarity, StepFunction>();
		LootRarity.values().stream().filter(rarity -> rarity != LootRarity.ANCIENT).forEach(rarity -> {
			var stepFunction = new StepFunction(min + rarity.ordinal() * step, steps, step);
			bonuses.put(rarity, stepFunction);
		});
		return bonuses;
	}

	private static Map<LootRarity, StepFunction> generateBonus(LootRarity rarity, float min, int steps, float step) {
		var bonuses = new HashMap<LootRarity, StepFunction>();
		var stepFunction = new StepFunction(min + rarity.ordinal() * step, steps, step);
		bonuses.put(rarity, stepFunction);
		return bonuses;
	}

	private static Gem createGem(GemBonus... gemBonuses) {
		return createGem(1, 0F, false, gemBonuses);
	}

	private static Gem createGem(int weight, float quality, boolean unique, GemBonus... gemBonuses) {
		var dimensions = Set.of(new ResourceLocation(SkillTreeMod.MOD_ID, "fake_dimension"));
		if (unique) dimensions = Set.of();
		return new Gem(weight, quality, dimensions, null, null, Arrays.asList(gemBonuses), unique, Optional.empty());
	}
}
