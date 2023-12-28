package daripher.skilltree.data.generation.skills;

import static daripher.skilltree.init.PSTAttributes.*;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_BASE;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;

import com.google.gson.JsonElement;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.init.PSTEffects;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.MeleeDamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.ProjectileDamageCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.ArmorEnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.NoneEnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.WeaponEnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.item.*;
import daripher.skilltree.skill.bonus.condition.living.*;
import daripher.skilltree.skill.bonus.item.*;
import daripher.skilltree.skill.bonus.multiplier.*;
import daripher.skilltree.skill.bonus.player.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import javax.annotation.Nullable;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.common.CuriosHelper;

public class PSTSkillsProvider implements DataProvider {
  private final Map<ResourceLocation, PassiveSkill> skills = new HashMap<>();
  private final DataGenerator dataGenerator;
  private final String[] playerClasses =
      new String[] {"alchemist", "hunter", "enchanter", "cook", "blacksmith", "miner"};

  public PSTSkillsProvider(DataGenerator dataGenerator) {
    this.dataGenerator = dataGenerator;
  }

  private void addSkills() {
    Arrays.stream(playerClasses).forEach(this::addClassSkills);
    addGateways();
  }

  private void addClassSkills(String playerClass) {
    // class skills
    addSkill(playerClass, "class", "class", 24);
    // lesser skills
    addSkillBranch(playerClass, "defensive", "defensive_1", 16, 1, 8);
    addSkillBranch(playerClass, "offensive", "offensive_1", 16, 1, 8);
    addSkillBranch(playerClass, "defensive_crafting", "defensive_crafting_1", 16, 1, 7);
    addSkillBranch(playerClass, "offensive_crafting", "offensive_crafting_1", 16, 1, 7);
    addSkillBranch(playerClass, "crafting", "crafting_1", 16, 1, 3);
    addSkillBranch(playerClass, "life", "life_1", 16, 1, 2);
    addSkillBranch(playerClass, "speed", "speed_1", 16, 1, 2);
    addSkillBranch(playerClass, "healing", "healing_1", 16, 1, 4);
    addSkillBranch(playerClass, "lesser", "lesser_1", 16, 1, 6);
    addSkillBranch(playerClass, "crit", "crit_1", 16, 1, 2);
    addSkillBranch(playerClass, "subclass_1_defensive", "subclass_1_defensive_1", 16, 1, 4);
    addSkillBranch(playerClass, "subclass_2_defensive", "subclass_2_defensive_1", 16, 1, 4);
    addSkillBranch(playerClass, "subclass_1_crafting", "subclass_1_crafting_1", 16, 1, 5);
    addSkillBranch(playerClass, "subclass_1_offensive", "subclass_1_offensive_1", 16, 1, 4);
    addSkillBranch(playerClass, "subclass_2_crafting", "subclass_2_crafting_1", 16, 1, 5);
    addSkillBranch(playerClass, "subclass_2_life", "subclass_2_life_1", 16, 1, 4);
    // notable skills
    addSkill(playerClass, "defensive_notable_1", "defensive_notable_1", 20);
    addSkill(playerClass, "offensive_notable_1", "offensive_notable_1", 20);
    addSkill(playerClass, "crafting_notable_1", "crafting_notable_1", 20);
    addSkill(playerClass, "life_notable_1", "life_notable_1", 20);
    addSkill(playerClass, "speed_notable_1", "speed_notable_1", 20);
    addSkill(playerClass, "healing_notable_1", "healing_notable_1", 20);
    addSkill(playerClass, "crit_notable_1", "crit_notable_1", 20);
    addSkill(playerClass, "subclass_special", "subclass_special_1", 20);
    addSkill(playerClass, "subclass_1_offensive_notable_1", "subclass_1_offensive_notable_1", 20);
    addSkill(playerClass, "subclass_1_crafting_notable_1", "subclass_1_crafting_notable_1", 20);
    addSkill(playerClass, "subclass_2_life_notable_1", "subclass_2_life_notable_1", 20);
    addSkill(playerClass, "subclass_2_crafting_notable_1", "subclass_2_crafting_notable_1", 20);
    // keystone skills
    addSkill(playerClass, "defensive_keystone_1", "defensive_keystone_1", 24);
    addSkill(playerClass, "offensive_keystone_1", "offensive_keystone_1", 24);
    addSkill(playerClass, "defensive_crafting_keystone_1", "defensive_crafting_keystone_1", 24);
    addSkill(playerClass, "offensive_crafting_keystone_1", "offensive_crafting_keystone_1", 24);
    addSkill(playerClass, "mastery", "mastery", 24);
    addSkill(playerClass, "subclass_1", "subclass_1", 24);
    addSkill(playerClass, "subclass_2", "subclass_2", 24);
    addSkill(playerClass, "subclass_1_mastery", "subclass_1_mastery", 24);
    addSkill(playerClass, "subclass_2_mastery", "subclass_2_mastery", 24);
  }

  protected void addGateways() {
    Arrays.stream(playerClasses).forEach(this::addGateway);
    addGatewayConnection("alchemist_gateway", "cook_gateway");
    addGatewayConnection("hunter_gateway", "blacksmith_gateway");
    addGatewayConnection("enchanter_gateway", "miner_gateway");
  }

  private void shapeSkillTree() {
    Arrays.stream(playerClasses).forEach(this::shapeClassTree);
    connectClassTrees();
  }

  private void shapeClassTree(String playerClass) {
    setSkillPosition(playerClass, null, 140, 0, "class");
    setSkillBranchPosition(playerClass, "class", 10, "defensive", 30, 0, 1, 4);
    setSkillBranchPosition(playerClass, "class", 10, "offensive", -30, 0, 1, 4);
    setSkillPosition(playerClass, "class", 10, 120, "defensive_crafting_1");
    setSkillPosition(playerClass, "class", 10, -120, "offensive_crafting_1");
    setSkillBranchPosition(
        playerClass, "defensive_crafting_1", 10, "defensive_crafting", 30, 0, 2, 4);
    setSkillBranchPosition(
        playerClass, "offensive_crafting_1", 10, "offensive_crafting", -30, 0, 2, 4);
    setSkillPosition(playerClass, "defensive_crafting_4", 12, 30, "defensive_notable_1");
    setSkillPosition(playerClass, "offensive_crafting_4", 12, -30, "offensive_notable_1");
    connectSkills(playerClass, "defensive_notable_1", "defensive_4");
    connectSkills(playerClass, "offensive_notable_1", "offensive_4");
    setSkillBranchPosition(playerClass, "class", 11, "crafting", 180, 0, 1, 3);
    setSkillPosition(playerClass, "crafting_3", 12, 180, "crafting_notable_1");
    setSkillPosition(playerClass, "defensive_4", 10, -90, "life_1");
    setSkillPosition(playerClass, "offensive_4", 10, 90, "life_2");
    setSkillPosition(playerClass, "life_2", 10, 90, "life_notable_1");
    connectSkills(playerClass, "life_1", "life_notable_1");
    setSkillBranchPosition(playerClass, "defensive_4", 10, "defensive", 0, 0, 5, 8);
    setSkillBranchPosition(playerClass, "offensive_4", 10, "offensive", 0, 0, 5, 8);
    setSkillPosition(playerClass, "defensive_5", 10, -90, "speed_1");
    setSkillPosition(playerClass, "offensive_5", 10, 90, "speed_2");
    setSkillPosition(playerClass, "speed_2", 10, 90, "speed_notable_1");
    connectSkills(playerClass, "speed_1", "speed_notable_1");
    setSkillPosition(playerClass, "defensive_8", 10, 0, "defensive_keystone_1");
    setSkillPosition(playerClass, "offensive_8", 10, 0, "offensive_keystone_1");
    setSkillPosition(playerClass, "defensive_notable_1", 8, 30, "defensive_crafting_5");
    setSkillPosition(playerClass, "offensive_notable_1", 8, -30, "offensive_crafting_5");
    setSkillBranchPosition(
        playerClass, "defensive_crafting_5", 10, "defensive_crafting", 0, 0, 6, 7);
    setSkillBranchPosition(
        playerClass, "offensive_crafting_5", 10, "offensive_crafting", 0, 0, 6, 7);
    setSkillPosition(playerClass, "defensive_crafting_7", 10, 0, "defensive_crafting_keystone_1");
    setSkillPosition(playerClass, "offensive_crafting_7", 10, 0, "offensive_crafting_keystone_1");
    setSkillPosition(playerClass, "defensive_crafting_3", 12, 120, "healing_1");
    setSkillPosition(playerClass, "healing_1", 10, 165, "healing_3");
    setSkillPosition(playerClass, "healing_3", 10, 75, "healing_2");
    setSkillPosition(playerClass, "healing_3", 10, 210, "healing_4");
    setSkillPosition(playerClass, "healing_4", 12, 210, "healing_notable_1");
    setSkillPosition(playerClass, "defensive_7", 10, -90, "lesser_1");
    setSkillPosition(playerClass, "offensive_7", 10, 90, "lesser_2");
    setSkillPosition(playerClass, "lesser_1", 10, 0, "lesser_3");
    setSkillPosition(playerClass, "lesser_2", 10, 0, "lesser_4");
    setSkillPosition(playerClass, "lesser_4", 12, 90, "lesser_5");
    connectSkills(playerClass, "lesser_5", "lesser_3");
    setSkillPosition(playerClass, "lesser_5", 10, 0, "lesser_6");
    setSkillPosition(playerClass, "lesser_6", 10, 0, "mastery");
    setSkillPosition(playerClass, "defensive_6", 10, -90, "crit_1");
    setSkillPosition(playerClass, "offensive_6", 10, 90, "crit_2");
    setSkillPosition(playerClass, "crit_2", 10, 90, "crit_notable_1");
    connectSkills(playerClass, "crit_1", "crit_notable_1");
    setSkillPosition(playerClass, "offensive_notable_1", 12, -120, "subclass_1_defensive_1");
    setSkillPosition(playerClass, "defensive_notable_1", 12, 120, "subclass_2_defensive_1");
    setSkillBranchPosition(
        playerClass, "subclass_1_defensive_1", 10, "subclass_1_defensive", -30, 0, 2, 4);
    setSkillBranchPosition(
        playerClass, "subclass_2_defensive_1", 10, "subclass_2_defensive", 30, 0, 2, 4);
    setSkillPosition(playerClass, "subclass_1_defensive_4", 12, 0, "subclass_1");
    setSkillPosition(playerClass, "subclass_2_defensive_4", 12, 0, "subclass_2");
    setSkillPosition(playerClass, "subclass_2_defensive_1", 8, 180, "gateway");
    setSkillPosition(playerClass, "subclass_1", 12, 30, "subclass_1_offensive_1");
    setSkillPosition(playerClass, "subclass_2", 12, 30, "subclass_2_crafting_1");
    setSkillBranchPosition(
        playerClass, "subclass_1_offensive_1", 10, "subclass_1_offensive", 0, 0, 2, 4);
    setSkillBranchPosition(
        playerClass, "subclass_2_crafting_1", 10, "subclass_2_crafting", 0, 0, 2, 4);
    setSkillPosition(playerClass, "subclass_1", 12, -30, "subclass_1_crafting_1");
    setSkillPosition(playerClass, "subclass_2", 12, -30, "subclass_2_life_1");
    setSkillBranchPosition(
        playerClass, "subclass_1_crafting_1", 10, "subclass_1_crafting", 0, 0, 2, 4);
    setSkillBranchPosition(playerClass, "subclass_2_life_1", 10, "subclass_2_life", 0, 0, 2, 4);
    setSkillPosition(playerClass, "subclass_1_crafting_2", 10, -60, "subclass_1_crafting_5");
    setSkillPosition(playerClass, "subclass_2_crafting_2", 10, 60, "subclass_2_crafting_5");
    setSkillPosition(playerClass, "subclass_1_crafting_5", 14, -120, "subclass_special");
    setSkillPosition(playerClass, "subclass_1_crafting_4", 12, 30, "subclass_1_mastery");
    connectSkills(playerClass, "subclass_1_mastery", "subclass_1_offensive_4");
    setSkillPosition(playerClass, "subclass_2_crafting_4", 12, -30, "subclass_2_mastery");
    connectSkills(playerClass, "subclass_2_mastery", "subclass_2_life_4");
    setSkillPosition(
        playerClass, "subclass_1_crafting_4", 12, -60, "subclass_1_crafting_notable_1");
    setSkillPosition(
        playerClass, "subclass_1_offensive_4", 12, 60, "subclass_1_offensive_notable_1");
    setSkillPosition(playerClass, "subclass_2_life_4", 12, -60, "subclass_2_life_notable_1");
    setSkillPosition(playerClass, "subclass_2_crafting_4", 12, 60, "subclass_2_crafting_notable_1");
    connectSkills(playerClass, "subclass_2_crafting_3", "subclass_2_crafting_5");
    connectSkills(playerClass, "subclass_1_crafting_3", "subclass_1_crafting_5");
  }

  protected void connectClassTrees() {
    connectSkillsBetweenClasses("healing_2", "offensive_crafting_3");
    connectSkillsBetweenClasses("subclass_2_defensive_1", "subclass_1_defensive_1");
    connectSkillsBetweenClasses("gateway", "subclass_1_defensive_1");
    connectSkillsBetweenClasses("subclass_2_crafting_5", "subclass_special");
  }

  private void setSkillsAttributeModifiers() {
    // alchemist skills
    addSkillAttributeBonus(
        "alchemist_class",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.ANY), new PotionDurationBonus(0.4f)));
    addSkillBranchAttributeModifier(
        "alchemist_defensive_crafting",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.BENEFICIAL),
            new PotionAmplificationBonus(0.1f)),
        1,
        7);
    addSkillBranchAttributeModifier(
        "alchemist_offensive_crafting",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.HARMFUL), new PotionAmplificationBonus(0.1f)),
        1,
        7);
    addSkillBranchAttributeModifier("alchemist_defensive", EVASION, 1, ADDITION, 1, 8);
    addSkillBranchAttributeModifier(
        "alchemist_offensive",
        new DamageBonus(0.05f, MULTIPLY_BASE)
            .setTargetCondition(new HasEffectCondition(MobEffects.POISON)),
        1,
        8);
    addSkillAttributeBonus(
        "alchemist_defensive_notable_1",
        createAttributeBonus(EVASION.get(), 10, ADDITION)
            .setCondition(new EffectAmountCondition(1, -1)));
    addSkillAttributeBonus(
        "alchemist_offensive_notable_1",
        new DamageBonus(0.25f, MULTIPLY_BASE)
            .setTargetCondition(new HasEffectCondition(MobEffects.POISON)));
    addSkillBranchAttributeModifier(
        "alchemist_life",
        createAttributeBonus(MAX_HEALTH, 2, ADDITION)
            .setCondition(new EffectAmountCondition(1, -1)),
        1,
        2);
    addSkillAttributeBonus(
        "alchemist_life_notable_1",
        createAttributeBonus(MAX_HEALTH, 6, ADDITION)
            .setCondition(new EffectAmountCondition(1, -1)));
    addSkillBranchAttributeModifier(
        "alchemist_speed",
        createAttributeBonus(ATTACK_SPEED, 0.05, MULTIPLY_BASE)
            .setCondition(new EffectAmountCondition(1, -1)),
        1,
        2);
    addSkillAttributeBonus(
        "alchemist_speed_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.1, MULTIPLY_BASE)
            .setCondition(new EffectAmountCondition(1, -1)));
    addSkillBranchAttributeModifier(
        "alchemist_lesser",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.ANY), new PotionDurationBonus(0.05f)),
        1,
        6);
    addSkillAttributeBonus(
        "alchemist_mastery",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.ANY), new PotionAmplificationBonus(1f)));
    addSkillBranchAttributeModifier(
        "alchemist_crit",
        new CritChanceBonus(0.02f).setTargetCondition(new HasEffectCondition(MobEffects.POISON)),
        1,
        2);
    addSkillAttributeBonus(
        "alchemist_crit_notable_1",
        new CritDamageBonus(0.35f).setTargetCondition(new HasEffectCondition(MobEffects.POISON)));
    addSkillBranchAttributeModifier(
        "alchemist_crafting",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.ANY), new PotionAmplificationBonus(0.1f)),
        1,
        3);
    addSkillAttributeBonus(
        "alchemist_defensive_crafting_keystone_1",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.BENEFICIAL),
            new PotionAmplificationBonus(1f)));
    addSkillAttributeBonus(
        "alchemist_offensive_crafting_keystone_1",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.HARMFUL), new PotionAmplificationBonus(0.5f)));
    addSkillAttributeBonus(
        "alchemist_offensive_crafting_keystone_1",
        new RecipeUnlockBonus(new ResourceLocation("skilltree:weapon_poisoning")));
    addSkillBranchAttributeModifier("alchemist_healing", LIFE_PER_HIT, 0.25, ADDITION, 1, 4);
    addSkillAttributeBonus(
        "alchemist_healing_notable_1",
        createAttributeBonus(LIFE_PER_HIT.get(), 0.5, ADDITION)
            .setCondition(new EffectAmountCondition(1, -1)));
    addSkillAttributeBonus(
        "alchemist_crafting_notable_1",
        new RecipeUnlockBonus(new ResourceLocation("skilltree:potion_mixing")));
    addSkillAttributeBonus(
        "alchemist_defensive_keystone_1",
        createAttributeBonus(EVASION.get(), 2, ADDITION)
            .setMultiplier(new EffectAmountMultiplier()));
    addSkillAttributeBonus(
        "alchemist_offensive_keystone_1",
        new DamageBonus(0.15f, MULTIPLY_BASE).setPlayerMultiplier(new EffectAmountMultiplier()));
    // assassin skills
    addSkillBranchAttributeModifier("alchemist_subclass_1_defensive", EVASION, 0.5, ADDITION, 1, 4);
    addSkillBranchAttributeModifier("alchemist_subclass_1_defensive", ARMOR, 0.5, ADDITION, 1, 4);
    addSkillAttributeBonus("alchemist_subclass_1", new CritChanceBonus(0.05f));
    addSkillAttributeBonus("alchemist_subclass_1", new CritDamageBonus(0.15f));
    addSkillBranchAttributeModifier(
        "alchemist_subclass_1_offensive", new CritChanceBonus(0.02f), 1, 4);
    addSkillBranchAttributeModifier(
        "alchemist_subclass_1_crafting",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.HARMFUL), new PotionAmplificationBonus(0.1f)),
        1,
        5);
    addSkillAttributeBonus(
        "alchemist_subclass_1_crafting_notable_1",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.HARMFUL), new PotionDurationBonus(0.25f)));
    addSkillAttributeBonus("alchemist_subclass_1_offensive_notable_1", new CritChanceBonus(0.05f));
    addSkillAttributeBonus("alchemist_subclass_1_mastery", new CritDamageBonus(0.5f));
    addSkillAttributeBonus(
        "alchemist_subclass_special",
        new CraftedItemBonus(
            new CurioCondition("ring"), new ItemSkillBonus(new CritDamageBonus(0.1f))));
    // healer skills
    addSkillBranchAttributeModifier("alchemist_subclass_2_defensive", EVASION, 1, ADDITION, 1, 4);
    addSkillAttributeBonus("alchemist_subclass_2", new IncomingHealingBonus(0.15f));
    addSkillBranchAttributeModifier("alchemist_subclass_2_life", MAX_HEALTH, 2, ADDITION, 1, 4);
    addSkillBranchAttributeModifier(
        "alchemist_subclass_2_life", new IncomingHealingBonus(0.05f), 1, 4);
    addSkillBranchAttributeModifier(
        "alchemist_subclass_2_crafting",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.BENEFICIAL),
            new PotionAmplificationBonus(0.1f)),
        1,
        5);
    addSkillAttributeBonus(
        "alchemist_subclass_2_crafting_notable_1",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.BENEFICIAL), new PotionDurationBonus(0.1f)));
    addSkillAttributeBonus(
        "alchemist_subclass_2_life_notable_1",
        new IncomingHealingBonus(0.1f).setCondition(new HealthPercentageCondition(-1, 0.5f)));
    addSkillAttributeBonus(
        "alchemist_subclass_2_mastery",
        new IncomingHealingBonus(0.25f).setCondition(new HealthPercentageCondition(-1, 0.5f)));
    // hunter skills
    addSkillAttributeBonus(
        "hunter_class", new LootDuplicationBonus(0.15f, 1f, LootDuplicationBonus.LootType.MOBS));
    addSkillBranchAttributeModifier(
        "hunter_defensive_crafting",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.ANY),
            new ItemSkillBonus(createAttributeBonus(EVASION.get(), 1, ADDITION))),
        1,
        7);
    addSkillBranchAttributeModifier(
        "hunter_offensive_crafting",
        new CraftedItemBonus(
            new WeaponCondition(WeaponCondition.Type.RANGED),
            new ItemSkillBonus(createAttributeBonus(ATTACK_SPEED, 0.04, MULTIPLY_BASE))),
        1,
        7);
    addSkillBranchAttributeModifier("hunter_defensive", EVASION, 1, ADDITION, 1, 8);
    addSkillBranchAttributeModifier(
        "hunter_offensive",
        new DamageBonus(0.1f, MULTIPLY_BASE).setDamageCondition(new ProjectileDamageCondition()),
        1,
        8);
    addSkillAttributeBonus(
        "hunter_defensive_notable_1",
        createAttributeBonus(EVASION.get(), 10, ADDITION)
            .setCondition(new HealthPercentageCondition(-1f, 0.5f)));
    addSkillAttributeBonus(
        "hunter_offensive_notable_1",
        new DamageBonus(0.25f, MULTIPLY_BASE).setDamageCondition(new ProjectileDamageCondition()));
    addSkillBranchAttributeModifier(
        "hunter_life",
        createAttributeBonus(MAX_HEALTH, 0.05, ADDITION)
            .setMultiplier(new AttributeValueMultiplier(EVASION.get())),
        1,
        2);
    addSkillAttributeBonus(
        "hunter_life_notable_1",
        createAttributeBonus(MAX_HEALTH, 0.1, ADDITION)
            .setMultiplier(new AttributeValueMultiplier(EVASION.get())));
    addSkillBranchAttributeModifier(
        "hunter_speed",
        createAttributeBonus(ATTACK_SPEED, 0.02, MULTIPLY_BASE)
            .setCondition(
                new HasItemInHandCondition(new WeaponCondition(WeaponCondition.Type.RANGED))),
        1,
        2);
    addSkillAttributeBonus(
        "hunter_speed_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.05, MULTIPLY_BASE)
            .setCondition(
                new HasItemInHandCondition(new WeaponCondition(WeaponCondition.Type.RANGED))));
    addSkillBranchAttributeModifier(
        "hunter_lesser",
        new LootDuplicationBonus(0.05f, 1f, LootDuplicationBonus.LootType.MOBS),
        1,
        6);
    addSkillAttributeBonus(
        "hunter_mastery", new LootDuplicationBonus(0.15f, 2f, LootDuplicationBonus.LootType.MOBS));
    addSkillBranchAttributeModifier(
        "hunter_crit",
        new CritChanceBonus(0.02f).setDamageCondition(new ProjectileDamageCondition()),
        1,
        2);
    addSkillAttributeBonus(
        "hunter_crit_notable_1",
        new CritDamageBonus(0.25f).setDamageCondition(new ProjectileDamageCondition()));
    addSkillBranchAttributeModifier("hunter_crafting", new ArrowRetrievalBonus(0.05f), 1, 3);
    addSkillAttributeBonus(
        "hunter_defensive_crafting_keystone_1",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.HELMET), new ItemSocketsBonus(1)));
    addSkillAttributeBonus(
        "hunter_offensive_crafting_keystone_1",
        new CraftedItemBonus(
            new WeaponCondition(WeaponCondition.Type.RANGED), new ItemSocketsBonus(1)));
    addSkillBranchAttributeModifier("hunter_healing", LIFE_PER_HIT, 0.25, ADDITION, 1, 4);
    addSkillAttributeBonus(
        "hunter_healing_notable_1",
        createAttributeBonus(LIFE_PER_HIT.get(), 0.5, ADDITION)
            .setCondition(
                new HasItemInHandCondition(new WeaponCondition(WeaponCondition.Type.RANGED))));
    addSkillAttributeBonus("hunter_crafting_notable_1", new ArrowRetrievalBonus(0.1f));
    addSkillAttributeBonus(
        "hunter_defensive_keystone_1",
        createAttributeBonus(ARMOR, 0.25, ADDITION)
            .setMultiplier(new AttributeValueMultiplier(EVASION.get())));
    addSkillAttributeBonus(
        "hunter_offensive_keystone_1",
        new DamageBonus(0.05f, MULTIPLY_BASE)
            .setPlayerMultiplier(new DistanceToTargetMultiplier()));
    // ranger skills
    addSkillBranchAttributeModifier("hunter_subclass_1_defensive", EVASION, 1, ADDITION, 1, 4);
    addSkillAttributeBonus("hunter_subclass_1", STEALTH, 10, ADDITION);
    addSkillAttributeBonus(
        "hunter_subclass_1", new JumpHeightBonus(new NoneLivingCondition(), 0.1f));
    addSkillBranchAttributeModifier("hunter_subclass_1_offensive", STEALTH, 5, ADDITION, 1, 4);
    addSkillBranchAttributeModifier(
        "hunter_subclass_1_offensive", ATTACK_SPEED, 0.02, MULTIPLY_BASE, 1, 4);
    addSkillBranchAttributeModifier(
        "hunter_subclass_1_crafting",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.ANY),
            new ItemSkillBonus(createAttributeBonus(STEALTH.get(), 1, ADDITION))),
        1,
        5);
    addSkillAttributeBonus(
        "hunter_subclass_1_crafting_notable_1",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.BOOTS),
            new ItemSkillBonus(createAttributeBonus(STEALTH.get(), 5, ADDITION))));
    addSkillAttributeBonus(
        "hunter_subclass_1_offensive_notable_1", ATTACK_SPEED, 0.05, MULTIPLY_BASE);
    addSkillAttributeBonus("hunter_subclass_1_offensive_notable_1", STEALTH, 5, ADDITION);
    addSkillAttributeBonus("hunter_subclass_1_mastery", STEALTH, 10, ADDITION);
    addSkillAttributeBonus(
        "hunter_subclass_1_mastery", new JumpHeightBonus(new NoneLivingCondition(), 0.5f));
    addSkillAttributeBonus(
        "hunter_subclass_special",
        new CraftedItemBonus(
            new WeaponCondition(WeaponCondition.Type.ANY),
            new ItemSkillBonus(createAttributeBonus(LIFE_PER_HIT.get(), 0.5, ADDITION))));
    // fletcher skills
    addSkillBranchAttributeModifier("hunter_subclass_2_defensive", EVASION, 0.5, ADDITION, 1, 4);
    addSkillBranchAttributeModifier("hunter_subclass_2_defensive", BLOCKING, 0.5, ADDITION, 1, 4);
    addSkillAttributeBonus(
        "hunter_subclass_2",
        new CraftedItemBonus(
            new CurioCondition("quiver"), new ItemSkillBonus(new ArrowRetrievalBonus(0.05f))));
    addSkillBranchAttributeModifier(
        "hunter_subclass_2_life",
        createAttributeBonus(MAX_HEALTH, 1, ADDITION)
            .setCondition(
                new HasItemInHandCondition(new WeaponCondition(WeaponCondition.Type.RANGED))),
        1,
        4);
    addSkillBranchAttributeModifier(
        "hunter_subclass_2_crafting",
        new CraftedItemBonus(new CurioCondition("quiver"), new QuiverCapacityBonus(10, ADDITION)),
        1,
        5);
    addSkillAttributeBonus(
        "hunter_subclass_2_crafting_notable_1",
        new CraftedItemBonus(
            new CurioCondition("quiver"), new ItemSkillBonus(new ArrowRetrievalBonus(0.1f))));
    addSkillAttributeBonus(
        "hunter_subclass_2_life_notable_1",
        new CraftedItemBonus(
            new CurioCondition("quiver"),
            new ItemSkillBonus(createAttributeBonus(MAX_HEALTH, 5, ADDITION))));
    addSkillAttributeBonus(
        "hunter_subclass_2_mastery",
        new CraftedItemBonus(new CurioCondition("quiver"), new QuiverCapacityBonus(25, ADDITION)));
    // miner skills
    addSkillAttributeBonus(
        "miner_class",
        new BlockBreakSpeedBonus(new HasItemInHandCondition(new ToolCondition(ToolCondition.Type.PICKAXE)), 0.15f));
    addSkillBranchAttributeModifier(
        "miner_defensive_crafting",
        new GemPowerBonus(new ArmorCondition(ArmorCondition.Type.ANY), 0.1f),
        1,
        7);
    addSkillBranchAttributeModifier(
        "miner_offensive_crafting",
        new GemPowerBonus(new WeaponCondition(WeaponCondition.Type.ANY), 0.1f),
        1,
        7);
    addSkillBranchAttributeModifier("miner_defensive", ARMOR, 1, ADDITION, 1, 8);
    addSkillBranchAttributeModifier(
        "miner_offensive",
        new DamageBonus(0.05f, MULTIPLY_BASE)
            .setPlayerCondition(
                new HasGemsCondition(1, -1, new WeaponCondition(WeaponCondition.Type.ANY))),
        1,
        8);
    addSkillAttributeBonus(
        "miner_defensive_notable_1",
        createAttributeBonus(ARMOR, 2, ADDITION)
            .setMultiplier(
                new GemsAmountMultiplier(new ArmorCondition(ArmorCondition.Type.HELMET))));
    addSkillAttributeBonus(
        "miner_offensive_notable_1",
        new DamageBonus(0.15f, MULTIPLY_BASE)
            .setPlayerCondition(
                new HasGemsCondition(1, -1, new WeaponCondition(WeaponCondition.Type.ANY))));
    addSkillBranchAttributeModifier(
        "miner_life",
        createAttributeBonus(MAX_HEALTH, 1, ADDITION)
            .setMultiplier(
                new GemsAmountMultiplier(new ArmorCondition(ArmorCondition.Type.HELMET))),
        1,
        2);
    addSkillAttributeBonus(
        "miner_life_notable_1",
        createAttributeBonus(MAX_HEALTH, 1, ADDITION)
            .setMultiplier(new GemsAmountMultiplier(new ArmorCondition(ArmorCondition.Type.ANY))));
    addSkillBranchAttributeModifier(
        "miner_speed",
        createAttributeBonus(ATTACK_SPEED, 0.02, MULTIPLY_BASE)
            .setCondition(
                new HasGemsCondition(1, -1, new WeaponCondition(WeaponCondition.Type.ANY))),
        1,
        2);
    addSkillAttributeBonus(
        "miner_speed_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.04, MULTIPLY_BASE)
            .setMultiplier(
                new GemsAmountMultiplier(new WeaponCondition(WeaponCondition.Type.ANY))));
    addSkillBranchAttributeModifier(
        "miner_lesser",
        new BlockBreakSpeedBonus(new HasItemInHandCondition(new ToolCondition(ToolCondition.Type.PICKAXE)), 0.05f),
        1,
        6);
    addSkillAttributeBonus("miner_mastery", new PlayerSocketsBonus(new EquipmentCondition(), 1));
    addSkillBranchAttributeModifier(
        "miner_crit",
        new CritChanceBonus(0.01f)
            .setPlayerMultiplier(
                new GemsAmountMultiplier(new WeaponCondition(WeaponCondition.Type.ANY))),
        1,
        2);
    addSkillAttributeBonus(
        "miner_crit_notable_1",
        new CritDamageBonus(0.1f)
            .setPlayerMultiplier(
                new GemsAmountMultiplier(new WeaponCondition(WeaponCondition.Type.ANY))));
    addSkillBranchAttributeModifier(
        "miner_crafting",
        new LootDuplicationBonus(0.05f, 1f, LootDuplicationBonus.LootType.GEMS),
        1,
        3);
    addSkillAttributeBonus(
        "miner_defensive_crafting_keystone_1",
        new GemPowerBonus(new ArmorCondition(ArmorCondition.Type.ANY), 0.3f));
    addSkillAttributeBonus(
        "miner_defensive_crafting_keystone_1",
        new PlayerSocketsBonus(new ArmorCondition(ArmorCondition.Type.CHESTPLATE), 1));
    addSkillAttributeBonus(
        "miner_offensive_crafting_keystone_1",
        new GemPowerBonus(new WeaponCondition(WeaponCondition.Type.ANY), 0.3f));
    addSkillAttributeBonus(
        "miner_offensive_crafting_keystone_1",
        new PlayerSocketsBonus(new WeaponCondition(WeaponCondition.Type.ANY), 1));
    addSkillBranchAttributeModifier("miner_healing", REGENERATION, 0.25, ADDITION, 1, 4);
    addSkillAttributeBonus(
        "miner_healing_notable_1",
        createAttributeBonus(REGENERATION.get(), 0.25, ADDITION)
            .setMultiplier(
                new GemsAmountMultiplier(new ArmorCondition(ArmorCondition.Type.HELMET))));
    addSkillAttributeBonus(
        "miner_crafting_notable_1",
        new LootDuplicationBonus(0.1f, 1f, LootDuplicationBonus.LootType.GEMS));
    addSkillAttributeBonus(
        "miner_defensive_keystone_1",
        createAttributeBonus(ARMOR, 5, ADDITION)
            .setMultiplier(
                new GemsAmountMultiplier(new ArmorCondition(ArmorCondition.Type.CHESTPLATE))));
    addSkillAttributeBonus(
        "miner_offensive_keystone_1",
        new DamageBonus(0.1f, MULTIPLY_BASE)
            .setPlayerMultiplier(
                new GemsAmountMultiplier(new WeaponCondition(WeaponCondition.Type.ANY))));
    // traveler skills
    addSkillBranchAttributeModifier("miner_subclass_1_defensive", ARMOR, 1, ADDITION, 1, 4);
    addSkillAttributeBonus("miner_subclass_1", ATTACK_SPEED, 0.1, MULTIPLY_BASE);
    addSkillAttributeBonus("miner_subclass_1", MOVEMENT_SPEED, 0.1, MULTIPLY_BASE);
    addSkillBranchAttributeModifier(
        "miner_subclass_1_offensive", ATTACK_SPEED, 0.02, MULTIPLY_BASE, 1, 4);
    addSkillBranchAttributeModifier(
        "miner_subclass_1_crafting",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.BOOTS),
            new ItemSkillBonus(createAttributeBonus(MOVEMENT_SPEED, 0.02, MULTIPLY_BASE))),
        1,
        5);
    addSkillAttributeBonus(
        "miner_subclass_1_crafting_notable_1",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.BOOTS),
            new ItemSkillBonus(createAttributeBonus(MOVEMENT_SPEED, 0.05, MULTIPLY_BASE))));
    addSkillAttributeBonus(
        "miner_subclass_1_offensive_notable_1", ATTACK_SPEED, 0.05, MULTIPLY_BASE);
    addSkillAttributeBonus(
        "miner_subclass_1_offensive_notable_1", MOVEMENT_SPEED, 0.05, MULTIPLY_BASE);
    addSkillAttributeBonus(
        "miner_subclass_1_mastery",
        new DamageBonus(0.1f, MULTIPLY_BASE)
            .setPlayerMultiplier(
                new GemsAmountMultiplier(new ArmorCondition(ArmorCondition.Type.BOOTS))));
    addSkillAttributeBonus(
        "miner_subclass_special",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.BOOTS), new ItemSocketsBonus(1)));
    // jeweler skills
    addSkillBranchAttributeModifier("miner_subclass_2_defensive", EVASION, 0.5, ADDITION, 1, 4);
    addSkillBranchAttributeModifier("miner_subclass_2_defensive", ARMOR, 0.5, ADDITION, 1, 4);
    addSkillAttributeBonus(
        "miner_subclass_2", new PlayerSocketsBonus(new CurioCondition("ring"), 1));
    addSkillBranchAttributeModifier(
        "miner_subclass_2_life",
        createAttributeBonus(MAX_HEALTH, 1d, ADDITION)
            .setMultiplier(new GemsAmountMultiplier(new JewelryCondition())),
        1,
        4);
    addSkillBranchAttributeModifier(
        "miner_subclass_2_crafting", new GemPowerBonus(new JewelryCondition(), 0.05f), 1, 5);
    addSkillAttributeBonus(
        "miner_subclass_2_crafting_notable_1", new GemPowerBonus(new JewelryCondition(), 0.25f));
    addSkillAttributeBonus(
        "miner_subclass_2_life_notable_1",
        new CraftedItemBonus(
            new CurioCondition("necklace"),
            new ItemSkillBonus(createAttributeBonus(MAX_HEALTH, 5, ADDITION))));
    addSkillAttributeBonus(
        "miner_subclass_2_mastery", CuriosHelper.getOrCreateSlotAttribute("ring"), 1, ADDITION);
    // blacksmith skills
    addSkillAttributeBonus(
        "blacksmith_class",
        new CraftedItemBonus(
            new EquipmentCondition(), new ItemDurabilityBonus(0.25f, MULTIPLY_BASE)));
    addSkillBranchAttributeModifier(
        "blacksmith_defensive_crafting",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.ANY),
            new ItemSkillBonus(createAttributeBonus(ARMOR, 0.1, ADDITION))),
        1,
        7);
    addSkillBranchAttributeModifier(
        "blacksmith_offensive_crafting",
        new CraftedItemBonus(
            new WeaponCondition(WeaponCondition.Type.MELEE),
            new ItemSkillBonus(createAttributeBonus(ATTACK_DAMAGE, 1, ADDITION))),
        1,
        7);
    addSkillBranchAttributeModifier("blacksmith_defensive", ARMOR, 1, ADDITION, 1, 8);
    addSkillBranchAttributeModifier(
        "blacksmith_offensive",
        new DamageBonus(0.05f, MULTIPLY_BASE)
            .setPlayerCondition(
                new HasItemInHandCondition(new ArmorCondition(ArmorCondition.Type.SHIELD))),
        1,
        8);
    addSkillAttributeBonus("blacksmith_defensive_notable_1", ARMOR, 0.05, MULTIPLY_BASE);
    addSkillAttributeBonus(
        "blacksmith_offensive_notable_1",
        new DamageBonus(0.25f, MULTIPLY_BASE)
            .setPlayerCondition(
                new HasItemInHandCondition(new ArmorCondition(ArmorCondition.Type.SHIELD))));
    addSkillBranchAttributeModifier(
        "blacksmith_life",
        createAttributeBonus(MAX_HEALTH, 1, ADDITION)
            .setCondition(
                new HasItemInHandCondition(new ArmorCondition(ArmorCondition.Type.SHIELD))),
        1,
        2);
    addSkillAttributeBonus(
        "blacksmith_life_notable_1",
        createAttributeBonus(MAX_HEALTH, 4, ADDITION)
            .setCondition(
                new HasItemInHandCondition(new ArmorCondition(ArmorCondition.Type.SHIELD))));
    addSkillBranchAttributeModifier(
        "blacksmith_speed",
        createAttributeBonus(ATTACK_SPEED, 0.02, MULTIPLY_BASE)
            .setCondition(
                new HasItemInHandCondition(new ArmorCondition(ArmorCondition.Type.SHIELD))),
        1,
        2);
    addSkillAttributeBonus(
        "blacksmith_speed_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.05, MULTIPLY_BASE)
            .setCondition(
                new HasItemInHandCondition(new ArmorCondition(ArmorCondition.Type.SHIELD))));
    addSkillBranchAttributeModifier(
        "blacksmith_lesser",
        new CraftedItemBonus(
            new EquipmentCondition(), new ItemDurabilityBonus(0.05f, MULTIPLY_BASE)),
        1,
        6);
    addSkillAttributeBonus(
        "blacksmith_mastery",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.ANY),
            new ItemSkillBonus(createAttributeBonus(ARMOR_TOUGHNESS, 1f, ADDITION))));
    addSkillBranchAttributeModifier(
        "blacksmith_crit",
        new CritChanceBonus(0.02f)
            .setPlayerCondition(
                new HasItemInHandCondition(new ArmorCondition(ArmorCondition.Type.SHIELD))),
        1,
        2);
    addSkillAttributeBonus(
        "blacksmith_crit_notable_1",
        new CritDamageBonus(0.3f)
            .setPlayerCondition(
                new HasItemInHandCondition(new ArmorCondition(ArmorCondition.Type.SHIELD))));
    addSkillBranchAttributeModifier(
        "blacksmith_crafting",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.SHIELD),
            new ItemSkillBonus(createAttributeBonus(ARMOR, 2, ADDITION))),
        1,
        3);
    addSkillAttributeBonus(
        "blacksmith_defensive_crafting_keystone_1",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.ANY),
            new ItemSkillBonus(createAttributeBonus(ARMOR, 1, ADDITION))));
    addSkillAttributeBonus(
        "blacksmith_offensive_crafting_keystone_1",
        new CraftedItemBonus(
            new WeaponCondition(WeaponCondition.Type.MELEE),
            new ItemSkillBonus(createAttributeBonus(ATTACK_SPEED, 0.25, MULTIPLY_BASE))));
    addSkillBranchAttributeModifier("blacksmith_healing", REGENERATION, 0.25, ADDITION, 1, 4);
    addSkillAttributeBonus(
        "blacksmith_healing_notable_1",
        createAttributeBonus(REGENERATION.get(), 0.5, ADDITION)
            .setCondition(
                new HasItemInHandCondition(new ArmorCondition(ArmorCondition.Type.SHIELD))));
    addSkillAttributeBonus(
        "blacksmith_crafting_notable_1",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.SHIELD),
            new ItemSkillBonus(createAttributeBonus(ARMOR, 4, ADDITION))));
    addSkillAttributeBonus(
        "blacksmith_defensive_keystone_1",
        createAttributeBonus(ARMOR, 0.1, MULTIPLY_BASE)
            .setCondition(new AttributeValueCondition(ARMOR, 50, -1)));
    addSkillAttributeBonus(
        "blacksmith_defensive_keystone_1",
        createAttributeBonus(ARMOR, 0.1, MULTIPLY_BASE)
            .setCondition(new AttributeValueCondition(ARMOR, 100, -1)));
    addSkillAttributeBonus(
        "blacksmith_offensive_keystone_1",
        createAttributeBonus(ATTACK_DAMAGE, 0.1, ADDITION)
            .setMultiplier(new AttributeValueMultiplier(ARMOR)));
    // soldier skills
    addSkillBranchAttributeModifier("blacksmith_subclass_1_defensive", ARMOR, 0.5, ADDITION, 1, 4);
    addSkillBranchAttributeModifier(
        "blacksmith_subclass_1_defensive", BLOCKING, 0.5, ADDITION, 1, 4);
    addSkillAttributeBonus("blacksmith_subclass_1", ARMOR, 5, ADDITION);
    addSkillAttributeBonus("blacksmith_subclass_1", BLOCKING, 5, ADDITION);
    addSkillBranchAttributeModifier(
        "blacksmith_subclass_1_offensive",
        new DamageBonus(0.01f, MULTIPLY_BASE).setDamageCondition(new MeleeDamageCondition()),
        1,
        4);
    addSkillBranchAttributeModifier("blacksmith_subclass_1_offensive", BLOCKING, 1, ADDITION, 1, 4);
    addSkillBranchAttributeModifier(
        "blacksmith_subclass_1_crafting",
        new CraftedItemBonus(
            new WeaponCondition(WeaponCondition.Type.MELEE),
            new ItemSkillBonus(new CritChanceBonus(0.01f))),
        1,
        5);
    addSkillAttributeBonus(
        "blacksmith_subclass_1_crafting_notable_1",
        new CraftedItemBonus(
            new WeaponCondition(WeaponCondition.Type.MELEE),
            new ItemSkillBonus(new CritChanceBonus(0.05f))));
    addSkillAttributeBonus(
        "blacksmith_subclass_1_offensive_notable_1",
        new DamageBonus(0.2f, MULTIPLY_BASE).setDamageCondition(new MeleeDamageCondition()));
    addSkillAttributeBonus(
        "blacksmith_subclass_1_offensive_notable_1",
        new CritDamageBonus(0.1f).setDamageCondition(new MeleeDamageCondition()));
    addSkillAttributeBonus("blacksmith_subclass_1_mastery", BLOCKING, 10, ADDITION);
    addSkillAttributeBonus("blacksmith_subclass_1_mastery", ARMOR, 0.05, MULTIPLY_BASE);
    addSkillAttributeBonus(
        "blacksmith_subclass_1_mastery",
        new DamageBonus(0.05f, MULTIPLY_BASE).setDamageCondition(new MeleeDamageCondition()));
    addSkillAttributeBonus(
        "blacksmith_subclass_special",
        new CraftedItemBonus(
            new WeaponCondition(WeaponCondition.Type.ANY),
            new ItemSkillBonus(
                new LootDuplicationBonus(0.05f, 1f, LootDuplicationBonus.LootType.MOBS))));
    // artisan skills
    addSkillBranchAttributeModifier("blacksmith_subclass_2_defensive", ARMOR, 1, ADDITION, 1, 4);
    addSkillAttributeBonus(
        "blacksmith_subclass_2", new RepairEfficiencyBonus(new EquipmentCondition(), 1f));
    addSkillBranchAttributeModifier(
        "blacksmith_subclass_2_life",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.ANY),
            new ItemSkillBonus(createAttributeBonus(MAX_HEALTH, 1, ADDITION))),
        1,
        4);
    addSkillBranchAttributeModifier(
        "blacksmith_subclass_2_crafting",
        new RepairEfficiencyBonus(new EquipmentCondition(), 0.05f),
        1,
        5);
    addSkillAttributeBonus(
        "blacksmith_subclass_2_crafting_notable_1",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.SHIELD),
            new ItemSkillBonus(createAttributeBonus(BLOCKING.get(), 5, ADDITION))));
    addSkillAttributeBonus(
        "blacksmith_subclass_2_life_notable_1",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.SHIELD),
            new ItemSkillBonus(createAttributeBonus(MAX_HEALTH, 5, ADDITION))));
    addSkillAttributeBonus(
        "blacksmith_subclass_2_mastery",
        new RepairEfficiencyBonus(new EquipmentCondition(), 0.05f));
    addSkillAttributeBonus(
        "blacksmith_subclass_2_mastery",
        new CraftedItemBonus(
            new EquipmentCondition(), new ItemDurabilityBonus(0.05f, MULTIPLY_BASE)));
    addSkillAttributeBonus(
        "blacksmith_subclass_2_mastery",
        new CraftedItemBonus(
            new WeaponCondition(WeaponCondition.Type.ANY),
            new ItemSkillBonus(createAttributeBonus(ATTACK_SPEED, 0.05, MULTIPLY_BASE))));
    addSkillAttributeBonus(
        "blacksmith_subclass_2_mastery",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.ANY),
            new ItemSkillBonus(createAttributeBonus(ARMOR_TOUGHNESS, 0.05f, MULTIPLY_BASE))));
    addSkillAttributeBonus(
        "blacksmith_subclass_2_mastery",
        new CraftedItemBonus(
            new ArmorCondition(ArmorCondition.Type.SHIELD),
            new ItemSkillBonus(createAttributeBonus(ARMOR, 5, ADDITION))));
    // enchanter skills
    addSkillAttributeBonus("enchanter_class", new EnchantmentRequirementBonus(-0.3f));
    addSkillBranchAttributeModifier(
        "enchanter_defensive_crafting",
        new EnchantmentAmplificationBonus(new ArmorEnchantmentCondition(), 0.1f),
        1,
        7);
    addSkillBranchAttributeModifier(
        "enchanter_offensive_crafting",
        new EnchantmentAmplificationBonus(new WeaponEnchantmentCondition(), 0.1f),
        1,
        7);
    addSkillBranchAttributeModifier("enchanter_defensive", BLOCKING, 1, ADDITION, 1, 8);
    addSkillBranchAttributeModifier(
        "enchanter_offensive",
        new DamageBonus(0.05f, MULTIPLY_BASE)
            .setPlayerCondition(
                new HasItemInHandCondition(
                    new EnchantedCondition(new WeaponCondition(WeaponCondition.Type.ANY)))),
        1,
        8);
    addSkillAttributeBonus(
        "enchanter_defensive_notable_1",
        createAttributeBonus(BLOCKING.get(), 10, ADDITION)
            .setCondition(
                new HasItemInHandCondition(
                    new EnchantedCondition(new ArmorCondition(ArmorCondition.Type.SHIELD)))));
    addSkillAttributeBonus(
        "enchanter_offensive_notable_1",
        new DamageBonus(0.2f, MULTIPLY_BASE)
            .setPlayerCondition(
                new HasItemInHandCondition(
                    new EnchantedCondition(new WeaponCondition(WeaponCondition.Type.ANY)))));
    addSkillBranchAttributeModifier(
        "enchanter_life",
        createAttributeBonus(MAX_HEALTH, 2, ADDITION)
            .setCondition(
                new HasItemEquippedCondition(new EnchantedCondition(new NoneItemCondition()))),
        1,
        2);
    addSkillAttributeBonus(
        "enchanter_life_notable_1",
        createAttributeBonus(MAX_HEALTH, 1, ADDITION)
            .setMultiplier(
                new EnchantsAmountMultiplier(new ArmorCondition(ArmorCondition.Type.ANY))));
    addSkillBranchAttributeModifier(
        "enchanter_speed",
        createAttributeBonus(ATTACK_SPEED, 0.02, MULTIPLY_BASE)
            .setCondition(
                new HasItemInHandCondition(
                    new EnchantedCondition(new WeaponCondition(WeaponCondition.Type.ANY)))),
        1,
        2);
    addSkillAttributeBonus(
        "enchanter_speed_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.05, MULTIPLY_BASE)
            .setCondition(
                new HasItemInHandCondition(
                    new EnchantedCondition(new WeaponCondition(WeaponCondition.Type.ANY)))));
    addSkillBranchAttributeModifier(
        "enchanter_lesser", new EnchantmentRequirementBonus(-0.05f), 1, 6);
    addSkillAttributeBonus(
        "enchanter_mastery", new EnchantmentAmplificationBonus(new NoneEnchantmentCondition(), 1f));
    addSkillBranchAttributeModifier(
        "enchanter_crit",
        new CritChanceBonus(0.02f)
            .setPlayerCondition(
                new HasItemInHandCondition(
                    new EnchantedCondition(new WeaponCondition(WeaponCondition.Type.ANY)))),
        1,
        2);
    addSkillAttributeBonus(
        "enchanter_crit_notable_1",
        new CritDamageBonus(0.05f)
            .setPlayerMultiplier(
                new EnchantsAmountMultiplier(new WeaponCondition(WeaponCondition.Type.ANY))));
    addSkillBranchAttributeModifier("enchanter_crafting", new FreeEnchantmentBonus(0.05f), 1, 3);
    addSkillAttributeBonus(
        "enchanter_defensive_crafting_keystone_1",
        new EnchantmentAmplificationBonus(new ArmorEnchantmentCondition(), 0.4f));
    addSkillAttributeBonus(
        "enchanter_offensive_crafting_keystone_1",
        new EnchantmentAmplificationBonus(new WeaponEnchantmentCondition(), 0.4f));
    addSkillBranchAttributeModifier("enchanter_healing", LIFE_ON_BLOCK, 0.25, ADDITION, 1, 4);
    addSkillAttributeBonus(
        "enchanter_healing_notable_1",
        createAttributeBonus(LIFE_ON_BLOCK.get(), 0.25, ADDITION)
            .setMultiplier(
                new EnchantsAmountMultiplier(new ArmorCondition(ArmorCondition.Type.SHIELD))));
    addSkillAttributeBonus("enchanter_crafting_notable_1", new FreeEnchantmentBonus(0.1f));
    addSkillAttributeBonus(
        "enchanter_defensive_keystone_1",
        createAttributeBonus(BLOCKING.get(), 5, ADDITION)
            .setMultiplier(
                new EnchantsAmountMultiplier(new ArmorCondition(ArmorCondition.Type.SHIELD))));
    addSkillAttributeBonus(
        "enchanter_offensive_keystone_1",
        new DamageBonus(0.05f, MULTIPLY_BASE)
            .setPlayerMultiplier(
                new EnchantLevelsAmountMultiplier(new WeaponCondition(WeaponCondition.Type.ANY))));
    // arsonist skills
    addSkillBranchAttributeModifier("enchanter_subclass_1_defensive", EVASION, 0.5, ADDITION, 1, 4);
    addSkillBranchAttributeModifier(
        "enchanter_subclass_1_defensive", BLOCKING, 0.5, ADDITION, 1, 4);
    addSkillAttributeBonus(
        "enchanter_subclass_1",
        new DamageBonus(0.15f, MULTIPLY_BASE).setTargetCondition(new BurningCondition()));
    addSkillAttributeBonus("enchanter_subclass_1", new IgniteChanceBonus(0.15f, 5));
    addSkillBranchAttributeModifier(
        "enchanter_subclass_1_offensive",
        new DamageBonus(0.05f, MULTIPLY_BASE).setTargetCondition(new BurningCondition()),
        1,
        4);
    addSkillBranchAttributeModifier(
        "enchanter_subclass_1_crafting",
        new CraftedItemBonus(
            new WeaponCondition(WeaponCondition.Type.ANY),
            new ItemSkillBonus(new IgniteChanceBonus(0.05f, 5))),
        1,
        5);
    addSkillAttributeBonus(
        "enchanter_subclass_1_crafting_notable_1",
        new CraftedItemBonus(
            new WeaponCondition(WeaponCondition.Type.ANY),
            new ItemSkillBonus(
                new DamageBonus(0.2f, MULTIPLY_BASE).setTargetCondition(new BurningCondition()))));
    addSkillAttributeBonus(
        "enchanter_subclass_1_offensive_notable_1",
        new CritChanceBonus(0.1f).setTargetCondition(new BurningCondition()));
    addSkillAttributeBonus("enchanter_subclass_1_mastery", new IgniteChanceBonus(0.1f, 5));
    addSkillAttributeBonus(
        "enchanter_subclass_1_mastery",
        new DamageBonus(0.1f, MULTIPLY_BASE).setTargetCondition(new BurningCondition()));
    addSkillAttributeBonus(
        "enchanter_subclass_1_mastery",
        new CritChanceBonus(0.1f).setTargetCondition(new BurningCondition()));
    addSkillAttributeBonus(
        "enchanter_subclass_special",
        new CraftedItemBonus(
            new CurioCondition("quiver"),
            new ItemSkillBonus(
                new DamageBonus(0.1f, MULTIPLY_BASE).setTargetCondition(new BurningCondition()))));
    addSkillAttributeBonus(
        "enchanter_subclass_special",
        new CraftedItemBonus(
            new CurioCondition("quiver"), new ItemSkillBonus(new IgniteChanceBonus(0.1f, 5))));
    // scholar skills
    addSkillBranchAttributeModifier("enchanter_subclass_2_defensive", BLOCKING, 1, ADDITION, 1, 4);
    addSkillAttributeBonus("enchanter_subclass_2", EXP_PER_MINUTE, 2, ADDITION);
    addSkillBranchAttributeModifier("enchanter_subclass_2_life", MAX_HEALTH, 2, ADDITION, 1, 4);
    addSkillBranchAttributeModifier(
        "enchanter_subclass_2_life", EXP_PER_MINUTE, 0.1, ADDITION, 1, 4);
    addSkillBranchAttributeModifier(
        "enchanter_subclass_2_crafting", EXP_PER_MINUTE, 0.2, ADDITION, 1, 5);
    addSkillAttributeBonus(
        "enchanter_subclass_2_crafting_notable_1",
        new GainedExperienceBonus(1f, GainedExperienceBonus.ExperienceSource.ORE));
    addSkillAttributeBonus("enchanter_subclass_2_life_notable_1", MAX_HEALTH, 6, ADDITION);
    addSkillAttributeBonus("enchanter_subclass_2_life_notable_1", EXP_PER_MINUTE, 0.1, ADDITION);
    addSkillAttributeBonus("enchanter_subclass_2_mastery", EXP_PER_MINUTE, 1.5, ADDITION);
    // cook skills
    addSkillAttributeBonus(
        "cook_class", new CraftedItemBonus(new FoodCondition(), new FoodSaturationBonus(0.2f)));
    addSkillBranchAttributeModifier(
        "cook_defensive_crafting",
        new CraftedItemBonus(new FoodCondition(), new FoodHealingBonus(0.25f)),
        1,
        7);
    addSkillBranchAttributeModifier(
        "cook_offensive_crafting",
        new CraftedItemBonus(
            new FoodCondition(),
            new FoodEffectBonus(new MobEffectInstance(PSTEffects.DAMAGE_BONUS.get(), 1200, 1))),
        1,
        7);
    addSkillBranchAttributeModifier("cook_defensive", BLOCKING, 1, ADDITION, 1, 8);
    addSkillBranchAttributeModifier(
        "cook_offensive",
        new DamageBonus(0.05f, MULTIPLY_BASE).setPlayerCondition(new FoodLevelCondition(15, -1)),
        1,
        8);
    addSkillAttributeBonus(
        "cook_defensive_notable_1",
        createAttributeBonus(BLOCKING.get(), 10, ADDITION)
            .setCondition(new FoodLevelCondition(15, -1)));
    addSkillAttributeBonus(
        "cook_offensive_notable_1",
        new DamageBonus(0.2f, MULTIPLY_BASE).setPlayerCondition(new FoodLevelCondition(15, -1)));
    addSkillBranchAttributeModifier(
        "cook_life",
        createAttributeBonus(MAX_HEALTH, 2, ADDITION).setCondition(new FoodLevelCondition(15, -1)),
        1,
        2);
    addSkillAttributeBonus(
        "cook_life_notable_1",
        createAttributeBonus(MAX_HEALTH, 1, ADDITION).setMultiplier(new HungerLevelMultiplier()));
    addSkillBranchAttributeModifier(
        "cook_speed",
        createAttributeBonus(ATTACK_SPEED, 0.02, MULTIPLY_BASE)
            .setCondition(new FoodLevelCondition(15, -1)),
        1,
        2);
    addSkillAttributeBonus(
        "cook_speed_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.1, MULTIPLY_BASE)
            .setCondition(new FoodLevelCondition(15, -1)));
    addSkillBranchAttributeModifier(
        "cook_lesser",
        new CraftedItemBonus(new FoodCondition(), new FoodSaturationBonus(0.05f)),
        1,
        6);
    addSkillAttributeBonus(
        "cook_mastery", new CraftedItemBonus(new FoodCondition(), new FoodSaturationBonus(0.5f)));
    addSkillBranchAttributeModifier(
        "cook_crit",
        new CritChanceBonus(0.02f).setPlayerCondition(new FoodLevelCondition(15, -1)),
        1,
        2);
    addSkillAttributeBonus(
        "cook_crit_notable_1",
        new CritDamageBonus(0.01f).setPlayerMultiplier(new HungerLevelMultiplier()));
    addSkillBranchAttributeModifier(
        "cook_crafting",
        new CraftedItemBonus(
            new FoodCondition(),
            new FoodEffectBonus(
                new MobEffectInstance(PSTEffects.LIFE_REGENERATION_BONUS.get(), 1200, 9))),
        1,
        3);
    addSkillAttributeBonus(
        "cook_defensive_crafting_keystone_1",
        new CraftedItemBonus(new FoodCondition(), new FoodHealingBonus(1f)));
    addSkillAttributeBonus(
        "cook_offensive_crafting_keystone_1",
        new CraftedItemBonus(
            new FoodCondition(),
            new FoodEffectBonus(
                new MobEffectInstance(PSTEffects.CRIT_DAMAGE_BONUS.get(), 1200, 19))));
    addSkillBranchAttributeModifier("cook_healing", LIFE_ON_BLOCK, 0.25, ADDITION, 1, 4);
    addSkillAttributeBonus(
        "cook_healing_notable_1",
        createAttributeBonus(LIFE_ON_BLOCK.get(), 0.5, ADDITION)
            .setCondition(new FoodLevelCondition(15, -1)));
    addSkillAttributeBonus(
        "cook_crafting_notable_1",
        new CraftedItemBonus(
            new FoodCondition(),
            new FoodEffectBonus(
                new MobEffectInstance(PSTEffects.LIFE_REGENERATION_BONUS.get(), 1200, 19))));
    addSkillAttributeBonus(
        "cook_defensive_keystone_1",
        createAttributeBonus(BLOCKING.get(), 1, ADDITION)
            .setMultiplier(new HungerLevelMultiplier()));
    addSkillAttributeBonus(
        "cook_offensive_keystone_1",
        new DamageBonus(0.02f, MULTIPLY_BASE).setPlayerMultiplier(new HungerLevelMultiplier()));
    // berserker skills
    addSkillBranchAttributeModifier("cook_subclass_1_defensive", BLOCKING, 1, ADDITION, 1, 4);
    addSkillAttributeBonus(
        "cook_subclass_1",
        new DamageBonus(0.1f, MULTIPLY_BASE)
            .setPlayerCondition(new HealthPercentageCondition(-1f, 0.75f)));
    addSkillAttributeBonus(
        "cook_subclass_1",
        new DamageBonus(0.2f, MULTIPLY_BASE)
            .setPlayerCondition(new HealthPercentageCondition(-1f, 0.5f)));
    addSkillBranchAttributeModifier(
        "cook_subclass_1_offensive", ATTACK_SPEED, 0.01, MULTIPLY_BASE, 1, 4);
    addSkillBranchAttributeModifier(
        "cook_subclass_1_crafting",
        new CraftedItemBonus(
            new WeaponCondition(WeaponCondition.Type.AXE),
            new ItemSkillBonus(new CritChanceBonus(0.01f))),
        1,
        5);
    addSkillAttributeBonus(
        "cook_subclass_1_crafting_notable_1",
        new CraftedItemBonus(
            new WeaponCondition(WeaponCondition.Type.AXE),
            new ItemSkillBonus(new CritChanceBonus(0.05f))));
    addSkillAttributeBonus(
        "cook_subclass_1_offensive_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.1, MULTIPLY_BASE)
            .setCondition(new HealthPercentageCondition(-1, 0.5f)));
    addSkillAttributeBonus(
        "cook_subclass_1_mastery",
        new CritChanceBonus(0.15f).setPlayerCondition(new HealthPercentageCondition(-1, 0.5f)));
    addSkillAttributeBonus(
        "cook_subclass_1_mastery",
        createAttributeBonus(LIFE_PER_HIT.get(), 1, ADDITION)
            .setCondition(new HealthPercentageCondition(-1, 0.5f)));
    addSkillAttributeBonus(
        "cook_subclass_special",
        new GainedExperienceBonus(0.5f, GainedExperienceBonus.ExperienceSource.MOBS));
    // fisherman skills
    addSkillBranchAttributeModifier("cook_subclass_2_defensive", ARMOR, 0.5, ADDITION, 1, 4);
    addSkillBranchAttributeModifier("cook_subclass_2_defensive", BLOCKING, 0.5, ADDITION, 1, 4);
    addSkillAttributeBonus(
        "cook_subclass_2",
        new LootDuplicationBonus(0.15f, 1f, LootDuplicationBonus.LootType.FISHING));
    addSkillBranchAttributeModifier("cook_subclass_2_life", MAX_HEALTH, 1, ADDITION, 1, 4);
    addSkillBranchAttributeModifier(
        "cook_subclass_2_crafting",
        new GainedExperienceBonus(0.2f, GainedExperienceBonus.ExperienceSource.FISHING),
        1,
        5);
    addSkillAttributeBonus(
        "cook_subclass_2_crafting_notable_1",
        new GainedExperienceBonus(0.5f, GainedExperienceBonus.ExperienceSource.FISHING));
    addSkillAttributeBonus("cook_subclass_2_life_notable_1", MAX_HEALTH, 4, ADDITION);
    addSkillAttributeBonus(
        "cook_subclass_2_life_notable_1",
        createAttributeBonus(LUCK, 1, ADDITION).setCondition(new FishingCondition()));
    addSkillAttributeBonus(
        "cook_subclass_2_mastery",
        new GainedExperienceBonus(0.5f, GainedExperienceBonus.ExperienceSource.FISHING));
    addSkillAttributeBonus(
        "cook_subclass_2_mastery",
        new LootDuplicationBonus(0.05f, 5f, LootDuplicationBonus.LootType.FISHING));
  }

  private void addSkillBranchAttributeModifier(
      String branchName,
      Attribute attribute,
      double amount,
      Operation operation,
      int from,
      int to) {
    for (int node = from; node <= to; node++) {
      addSkillAttributeBonus(branchName + "_" + node, attribute, amount, operation);
    }
  }

  private void addSkillBranchAttributeModifier(
      String branchName, SkillBonus<?> bonus, int from, int to) {
    for (int node = from; node <= to; node++) {
      addSkillAttributeBonus(branchName + "_" + node, bonus);
    }
  }

  private void addSkillAttributeBonus(
      String skillName, Attribute attribute, double amount, Operation operation) {
    getSkill(skillName).addSkillBonus(createAttributeBonus(attribute, amount, operation));
  }

  private void addSkillAttributeBonus(String skillName, SkillBonus<?> bonus) {
    getSkill(skillName).addSkillBonus(bonus);
  }

  @NotNull
  private static AttributeBonus createAttributeBonus(
      Attribute attribute, double amount, Operation operation) {
    return new AttributeBonus(
        attribute, new AttributeModifier(UUID.randomUUID(), "SkillBonus", amount, operation));
  }

  private void addSkillBranchAttributeModifier(
      String branchName,
      RegistryObject<Attribute> attribute,
      double amount,
      Operation operation,
      int from,
      int to) {
    addSkillBranchAttributeModifier(branchName, attribute.get(), amount, operation, from, to);
  }

  private void addSkillAttributeBonus(
      String skillName, RegistryObject<Attribute> attribute, double amount, Operation operation) {
    addSkillAttributeBonus(skillName, attribute.get(), amount, operation);
  }

  public void addSkillBranch(
      String playerClass, String branchName, String iconName, int nodeSize, int from, int to) {
    for (int node = from; node <= to; node++) {
      addSkill(playerClass, branchName + "_" + node, iconName, nodeSize);
    }
  }

  private void connectSkillsBetweenClasses(String from, String to) {
    for (int classId = 0; classId < playerClasses.length - 1; classId++) {
      connectSkills(playerClasses[classId] + "_" + from, playerClasses[classId + 1] + "_" + to);
    }
    connectSkills(playerClasses[5] + "_" + from, playerClasses[0] + "_" + to);
  }

  private void setSkillBranchPosition(
      String playerClass,
      String nodeName,
      int distance,
      String branchName,
      float rotation,
      float rotationPerNode,
      int from,
      int to) {
    String branchNode = nodeName;
    for (int node = from; node <= to; node++) {
      setSkillPosition(
          playerClass,
          branchNode,
          distance,
          rotation + (node - from) * rotationPerNode,
          branchName + "_" + node);
      branchNode = branchName + "_" + node;
    }
  }

  private void setSkillPosition(
      String playerClass,
      @Nullable String previousSkillName,
      float distance,
      float angle,
      String skillName) {
    setSkillPosition(
        getClassId(playerClass),
        playerClass + "_" + previousSkillName,
        distance,
        angle,
        playerClass + "_" + skillName);
  }

  private void setSkillPosition(
      int classId,
      @Nullable String previousSkillName,
      float distance,
      float angle,
      String skillName) {
    angle *= Mth.PI / 180F;
    angle += getClassBranchRotation(classId);
    PassiveSkill previous = previousSkillName == null ? null : getSkill(previousSkillName);
    PassiveSkill skill = getSkill(skillName);
    float centerX = 0F;
    float centerY = 0F;
    int buttonSize = skill.getButtonSize();
    distance += buttonSize / 2F;
    if (previous != null) {
      int previousButtonRadius = previous.getButtonSize() / 2;
      distance += previousButtonRadius;
      centerX = previous.getPositionX();
      centerY = previous.getPositionY();
    }
    float skillX = centerX + Mth.sin(angle) * distance;
    float skillY = centerY + Mth.cos(angle) * distance;
    skill.setPosition(skillX, skillY);
    if (previous != null) previous.connect(skill);
  }

  protected int getClassId(String playerClass) {
    return Arrays.asList(playerClasses).indexOf(playerClass);
  }

  public float getClassBranchRotation(int classId) {
    return classId * Mth.PI * 2F / 6F;
  }

  private PassiveSkill getSkill(String skillName) {
    return getSkills().get(getSkillId(skillName));
  }

  private void connectSkills(String skillName1, String skillName2) {
    getSkill(skillName1).connect(getSkill(skillName2));
  }

  private void connectSkills(String playerClass, String skillName1, String skillName2) {
    getSkill(playerClass + "_" + skillName1).connect(getSkill(playerClass + "_" + skillName2));
  }

  private ResourceLocation getSkillId(String skillName) {
    return new ResourceLocation(SkillTreeMod.MOD_ID, skillName);
  }

  private void addSkill(String playerClass, String skillName, String iconName, int buttonSize) {
    addSkill(playerClass + "_" + skillName, playerClass + "_" + iconName, buttonSize);
  }

  private void addGateway(String playerClass) {
    ResourceLocation skillId = new ResourceLocation(SkillTreeMod.MOD_ID, playerClass + "_gateway");
    ResourceLocation backgroundTexture =
        new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/background/gateway.png");
    ResourceLocation iconTexture =
        new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/void.png");
    ResourceLocation borderTexture =
        new ResourceLocation(SkillTreeMod.MOD_ID, "textures/tooltip/gateway.png");
    PassiveSkill skill =
        new PassiveSkill(skillId, 30, backgroundTexture, iconTexture, borderTexture, false);
    skills.put(skillId, skill);
  }

  private void addGatewayConnection(String gatewayId1, String gatewayId2) {
    getSkill(gatewayId1).getGatewayConnections().add(new ResourceLocation("skilltree", gatewayId2));
  }

  private void addSkill(String name, String icon, int size) {
    ResourceLocation skillId = new ResourceLocation(SkillTreeMod.MOD_ID, name);
    String background =
        name.endsWith("class") || name.endsWith("subclass_1") || name.endsWith("subclass_2")
            ? "class"
            : size == 24 ? "keystone" : size == 20 ? "notable" : "lesser";
    ResourceLocation backgroundTexture =
        new ResourceLocation(
            SkillTreeMod.MOD_ID, "textures/icons/background/" + background + ".png");
    ResourceLocation iconTexture =
        new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/" + icon + ".png");
    String border = size == 24 ? "keystone" : size == 20 ? "notable" : "lesser";
    ResourceLocation borderTexture =
        new ResourceLocation(SkillTreeMod.MOD_ID, "textures/tooltip/" + border + ".png");
    skills.put(
        skillId,
        new PassiveSkill(
            skillId, size, backgroundTexture, iconTexture, borderTexture, name.endsWith("class")));
  }

  @Override
  public void run(@NotNull CachedOutput output) {
    addSkills();
    shapeSkillTree();
    setSkillsAttributeModifiers();
    skills.values().forEach(skill -> save(output, skill));
  }

  private void save(CachedOutput output, PassiveSkill skill) {
    Path path = dataGenerator.getOutputFolder().resolve(getPath(skill));
    JsonElement json = SkillsReloader.GSON.toJsonTree(skill);
    try {
      DataProvider.saveStable(output, json, path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getPath(PassiveSkill skill) {
    ResourceLocation id = skill.getId();
    return "data/" + id.getNamespace() + "/skills/" + id.getPath() + ".json";
  }

  public Map<ResourceLocation, PassiveSkill> getSkills() {
    return skills;
  }

  @Override
  public @NotNull String getName() {
    return "Skills Provider";
  }
}
