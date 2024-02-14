package daripher.skilltree.data.generation.skills;

import static daripher.skilltree.init.PSTAttributes.*;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_BASE;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.init.PSTEffects;
import daripher.skilltree.init.PSTTags;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.MeleeDamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.ProjectileDamageCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.ArmorEnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.WeaponEnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.item.*;
import daripher.skilltree.skill.bonus.condition.living.*;
import daripher.skilltree.skill.bonus.event.AttackEventListener;
import daripher.skilltree.skill.bonus.event.BlockEventListener;
import daripher.skilltree.skill.bonus.event.ItemUsedEventListener;
import daripher.skilltree.skill.bonus.item.*;
import daripher.skilltree.skill.bonus.multiplier.*;
import daripher.skilltree.skill.bonus.player.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotAttribute;

public class PSTSkillsProvider implements DataProvider {
  private final Map<ResourceLocation, PassiveSkill> skills = new HashMap<>();
  private final PackOutput packOutput;
  private final String[] playerClasses =
      new String[] {"alchemist", "hunter", "enchanter", "cook", "blacksmith", "miner"};

  public PSTSkillsProvider(DataGenerator dataGenerator) {
    this.packOutput = dataGenerator.getPackOutput();
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
    addSkillBonus(
        "alchemist_class",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.ANY), new PotionDurationBonus(0.4f)));
    addSkillBranchBonuses(
        "alchemist_defensive_crafting",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.BENEFICIAL),
            new PotionAmplificationBonus(0.1f)),
        1,
        7);
    addSkillBranchBonuses(
        "alchemist_offensive_crafting",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.HARMFUL), new PotionAmplificationBonus(0.1f)),
        1,
        7);
    addSkillBranchBonuses("alchemist_defensive", EVASION, 1, ADDITION, 1, 8);
    addSkillBranchBonuses(
        "alchemist_offensive",
        new DamageBonus(0.05f, MULTIPLY_BASE)
            .setTargetCondition(new HasEffectCondition(MobEffects.POISON)),
        1,
        8);
    addSkillBonus(
        "alchemist_defensive_notable_1",
        createAttributeBonus(EVASION.get(), 10, ADDITION)
            .setCondition(new EffectAmountCondition(1, -1)));
    addSkillBonus(
        "alchemist_offensive_notable_1",
        new DamageBonus(0.25f, MULTIPLY_BASE)
            .setTargetCondition(new HasEffectCondition(MobEffects.POISON)));
    addSkillBranchBonuses(
        "alchemist_life",
        createAttributeBonus(MAX_HEALTH, 2, ADDITION)
            .setCondition(new EffectAmountCondition(1, -1)),
        1,
        2);
    addSkillBonus(
        "alchemist_life_notable_1",
        createAttributeBonus(MAX_HEALTH, 6, ADDITION)
            .setCondition(new EffectAmountCondition(1, -1)));
    addSkillBranchBonuses(
        "alchemist_speed",
        createAttributeBonus(ATTACK_SPEED, 0.05, MULTIPLY_BASE)
            .setCondition(new EffectAmountCondition(1, -1)),
        1,
        2);
    addSkillBonus(
        "alchemist_speed_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.1, MULTIPLY_BASE)
            .setCondition(new EffectAmountCondition(1, -1)));
    addSkillBranchBonuses(
        "alchemist_lesser",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.ANY), new PotionDurationBonus(0.05f)),
        1,
        6);
    addSkillBonus(
        "alchemist_mastery",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.ANY), new PotionAmplificationBonus(1f)));
    addSkillBranchBonuses(
        "alchemist_crit",
        new CritChanceBonus(0.02f).setTargetCondition(new HasEffectCondition(MobEffects.POISON)),
        1,
        2);
    addSkillBonus(
        "alchemist_crit_notable_1",
        new CritDamageBonus(0.35f).setTargetCondition(new HasEffectCondition(MobEffects.POISON)));
    addSkillBranchBonuses(
        "alchemist_crafting",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.ANY), new PotionAmplificationBonus(0.1f)),
        1,
        3);
    addSkillBonus(
        "alchemist_defensive_crafting_keystone_1",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.BENEFICIAL),
            new PotionAmplificationBonus(1f)));
    addSkillBonus(
        "alchemist_offensive_crafting_keystone_1",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.HARMFUL), new PotionAmplificationBonus(0.5f)));
    addSkillBonus(
        "alchemist_offensive_crafting_keystone_1",
        new RecipeUnlockBonus(new ResourceLocation("skilltree:weapon_poisoning")));
    addSkillBranchBonuses("alchemist_healing", new HealingBonus(1f, 0.1f), 1, 4);
    addSkillBonus(
        "alchemist_healing_notable_1",
        new HealingBonus(
            1f, 2f, new ItemUsedEventListener(new PotionCondition(PotionCondition.Type.ANY))));
    addSkillBonus(
        "alchemist_crafting_notable_1",
        new RecipeUnlockBonus(new ResourceLocation("skilltree:potion_mixing")));
    addSkillBonus(
        "alchemist_defensive_keystone_1",
        createAttributeBonus(EVASION.get(), 2, ADDITION)
            .setMultiplier(new EffectAmountMultiplier()));
    addSkillBonus(
        "alchemist_offensive_keystone_1",
        new DamageBonus(0.15f, MULTIPLY_BASE).setPlayerMultiplier(new EffectAmountMultiplier()));
    // assassin skills
    addSkillBranchBonuses("alchemist_subclass_1_defensive", EVASION, 0.5, ADDITION, 1, 4);
    addSkillBranchBonuses("alchemist_subclass_1_defensive", ARMOR, 0.5, ADDITION, 1, 4);
    addSkillBonus("alchemist_subclass_1", new CritChanceBonus(0.05f));
    addSkillBonus("alchemist_subclass_1", new CritDamageBonus(0.15f));
    addSkillBranchBonuses("alchemist_subclass_1_offensive", new CritChanceBonus(0.02f), 1, 4);
    addSkillBranchBonuses(
        "alchemist_subclass_1_crafting",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.HARMFUL), new PotionAmplificationBonus(0.1f)),
        1,
        5);
    addSkillBonus(
        "alchemist_subclass_1_crafting_notable_1",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.HARMFUL), new PotionDurationBonus(0.25f)));
    addSkillBonus("alchemist_subclass_1_offensive_notable_1", new CritChanceBonus(0.05f));
    addSkillBonus("alchemist_subclass_1_mastery", new CritDamageBonus(0.5f));
    addSkillBonus(
        "alchemist_subclass_special",
        new CraftedItemBonus(
            new ItemTagCondition(PSTTags.RINGS.location()),
            new ItemSkillBonus(new CritDamageBonus(0.1f))));
    // healer skills
    addSkillBranchBonuses("alchemist_subclass_2_defensive", EVASION, 1, ADDITION, 1, 4);
    addSkillBonus("alchemist_subclass_2", new IncomingHealingBonus(0.15f));
    addSkillBranchBonuses("alchemist_subclass_2_life", MAX_HEALTH, 2, ADDITION, 1, 4);
    addSkillBranchBonuses("alchemist_subclass_2_life", new IncomingHealingBonus(0.05f), 1, 4);
    addSkillBranchBonuses(
        "alchemist_subclass_2_crafting",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.BENEFICIAL),
            new PotionAmplificationBonus(0.1f)),
        1,
        5);
    addSkillBonus(
        "alchemist_subclass_2_crafting_notable_1",
        new CraftedItemBonus(
            new PotionCondition(PotionCondition.Type.BENEFICIAL), new PotionDurationBonus(0.1f)));
    addSkillBonus(
        "alchemist_subclass_2_life_notable_1",
        new IncomingHealingBonus(0.1f).setCondition(new HealthPercentageCondition(-1, 0.5f)));
    addSkillBonus(
        "alchemist_subclass_2_mastery",
        new IncomingHealingBonus(0.25f).setCondition(new HealthPercentageCondition(-1, 0.5f)));
    // hunter skills
    addSkillBonus(
        "hunter_class", new LootDuplicationBonus(0.15f, 1f, LootDuplicationBonus.LootType.MOBS));
    addSkillBranchBonuses(
        "hunter_defensive_crafting",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.ARMOR),
            new ItemSkillBonus(createAttributeBonus(EVASION.get(), 1, ADDITION))),
        1,
        7);
    addSkillBranchBonuses(
        "hunter_offensive_crafting",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.RANGED_WEAPON),
            new ItemSkillBonus(createAttributeBonus(ATTACK_SPEED, 0.04, MULTIPLY_BASE))),
        1,
        7);
    addSkillBranchBonuses("hunter_defensive", EVASION, 1, ADDITION, 1, 8);
    addSkillBranchBonuses(
        "hunter_offensive",
        new DamageBonus(0.1f, MULTIPLY_BASE).setDamageCondition(new ProjectileDamageCondition()),
        1,
        8);
    addSkillBonus(
        "hunter_defensive_notable_1",
        createAttributeBonus(EVASION.get(), 10, ADDITION)
            .setCondition(new HealthPercentageCondition(-1f, 0.5f)));
    addSkillBonus(
        "hunter_offensive_notable_1",
        new DamageBonus(0.25f, MULTIPLY_BASE).setDamageCondition(new ProjectileDamageCondition()));
    addSkillBranchBonuses(
        "hunter_life",
        createAttributeBonus(MAX_HEALTH, 0.05, ADDITION)
            .setMultiplier(new AttributeValueMultiplier(EVASION.get())),
        1,
        2);
    addSkillBonus(
        "hunter_life_notable_1",
        createAttributeBonus(MAX_HEALTH, 0.1, ADDITION)
            .setMultiplier(new AttributeValueMultiplier(EVASION.get())));
    addSkillBranchBonuses(
        "hunter_speed",
        createAttributeBonus(ATTACK_SPEED, 0.02, MULTIPLY_BASE)
            .setCondition(
                new HasItemInHandCondition(
                    new EquipmentCondition(EquipmentCondition.Type.RANGED_WEAPON))),
        1,
        2);
    addSkillBonus(
        "hunter_speed_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.05, MULTIPLY_BASE)
            .setCondition(
                new HasItemInHandCondition(
                    new EquipmentCondition(EquipmentCondition.Type.RANGED_WEAPON))));
    addSkillBranchBonuses(
        "hunter_lesser",
        new LootDuplicationBonus(0.05f, 1f, LootDuplicationBonus.LootType.MOBS),
        1,
        6);
    addSkillBonus(
        "hunter_mastery", new LootDuplicationBonus(0.15f, 2f, LootDuplicationBonus.LootType.MOBS));
    addSkillBranchBonuses(
        "hunter_crit",
        new CritChanceBonus(0.02f).setDamageCondition(new ProjectileDamageCondition()),
        1,
        2);
    addSkillBonus(
        "hunter_crit_notable_1",
        new CritDamageBonus(0.25f).setDamageCondition(new ProjectileDamageCondition()));
    addSkillBranchBonuses("hunter_crafting", new ArrowRetrievalBonus(0.05f), 1, 3);
    addSkillBonus(
        "hunter_defensive_crafting_keystone_1",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.HELMET), new ItemSocketsBonus(1)));
    addSkillBonus(
        "hunter_offensive_crafting_keystone_1",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.RANGED_WEAPON),
            new ItemSocketsBonus(1)));
    addSkillBranchBonuses("hunter_healing", new HealingBonus(1f, 0.1f), 1, 4);
    addSkillBonus(
        "hunter_healing_notable_1",
        new HealingBonus(
            1f,
            1f,
            new AttackEventListener()
                .setTarget(SkillBonus.Target.PLAYER)
                .setDamageCondition(new ProjectileDamageCondition())));
    addSkillBonus("hunter_crafting_notable_1", new ArrowRetrievalBonus(0.1f));
    addSkillBonus(
        "hunter_defensive_keystone_1",
        createAttributeBonus(ARMOR, 0.25, ADDITION)
            .setMultiplier(new AttributeValueMultiplier(EVASION.get())));
    addSkillBonus(
        "hunter_offensive_keystone_1",
        new DamageBonus(0.05f, MULTIPLY_BASE)
            .setPlayerMultiplier(new DistanceToTargetMultiplier()));
    // ranger skills
    addSkillBranchBonuses("hunter_subclass_1_defensive", EVASION, 1, ADDITION, 1, 4);
    addSkillBonus("hunter_subclass_1", STEALTH, 10, ADDITION);
    addSkillBonus("hunter_subclass_1", new JumpHeightBonus(0.1f));
    addSkillBranchBonuses("hunter_subclass_1_offensive", STEALTH, 5, ADDITION, 1, 4);
    addSkillBranchBonuses("hunter_subclass_1_offensive", ATTACK_SPEED, 0.02, MULTIPLY_BASE, 1, 4);
    addSkillBranchBonuses(
        "hunter_subclass_1_crafting",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.ARMOR),
            new ItemSkillBonus(createAttributeBonus(STEALTH.get(), 1, ADDITION))),
        1,
        5);
    addSkillBonus(
        "hunter_subclass_1_crafting_notable_1",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.BOOTS),
            new ItemSkillBonus(createAttributeBonus(STEALTH.get(), 5, ADDITION))));
    addSkillBonus("hunter_subclass_1_offensive_notable_1", ATTACK_SPEED, 0.05, MULTIPLY_BASE);
    addSkillBonus("hunter_subclass_1_offensive_notable_1", STEALTH, 5, ADDITION);
    addSkillBonus("hunter_subclass_1_mastery", STEALTH, 10, ADDITION);
    addSkillBonus("hunter_subclass_1_mastery", new JumpHeightBonus(0.5f));
    addSkillBonus(
        "hunter_subclass_special",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.WEAPON),
            new ItemSkillBonus(
                new HealingBonus(
                    1f, 0.5f, new AttackEventListener().setTarget(SkillBonus.Target.PLAYER)))));
    // fletcher skills
    addSkillBranchBonuses("hunter_subclass_2_defensive", EVASION, 0.5, ADDITION, 1, 4);
    addSkillBranchBonuses("hunter_subclass_2_defensive", BLOCKING, 0.5, ADDITION, 1, 4);
    addSkillBonus(
        "hunter_subclass_2",
        new CraftedItemBonus(
            new ItemTagCondition(PSTTags.QUIVERS.location()),
            new ItemSkillBonus(new ArrowRetrievalBonus(0.05f))));
    addSkillBranchBonuses(
        "hunter_subclass_2_life",
        createAttributeBonus(MAX_HEALTH, 1, ADDITION)
            .setCondition(
                new HasItemInHandCondition(
                    new EquipmentCondition(EquipmentCondition.Type.RANGED_WEAPON))),
        1,
        4);
    addSkillBranchBonuses(
        "hunter_subclass_2_crafting",
        new CraftedItemBonus(
            new ItemTagCondition(PSTTags.QUIVERS.location()),
            new QuiverCapacityBonus(10, ADDITION)),
        1,
        5);
    addSkillBonus(
        "hunter_subclass_2_crafting_notable_1",
        new CraftedItemBonus(
            new ItemTagCondition(PSTTags.QUIVERS.location()),
            new ItemSkillBonus(new ArrowRetrievalBonus(0.1f))));
    addSkillBonus(
        "hunter_subclass_2_life_notable_1",
        new CraftedItemBonus(
            new ItemTagCondition(PSTTags.QUIVERS.location()),
            new ItemSkillBonus(createAttributeBonus(MAX_HEALTH, 5, ADDITION))));
    addSkillBonus(
        "hunter_subclass_2_mastery",
        new CraftedItemBonus(
            new ItemTagCondition(PSTTags.QUIVERS.location()),
            new QuiverCapacityBonus(25, ADDITION)));
    // miner skills
    addSkillBonus(
        "miner_class",
        new BlockBreakSpeedBonus(
            new HasItemInHandCondition(new EquipmentCondition(EquipmentCondition.Type.PICKAXE)),
            0.15f));
    addSkillBranchBonuses(
        "miner_defensive_crafting",
        new GemPowerBonus(new EquipmentCondition(EquipmentCondition.Type.ARMOR), 0.1f),
        1,
        7);
    addSkillBranchBonuses(
        "miner_offensive_crafting",
        new GemPowerBonus(new EquipmentCondition(EquipmentCondition.Type.WEAPON), 0.1f),
        1,
        7);
    addSkillBranchBonuses("miner_defensive", ARMOR, 1, ADDITION, 1, 8);
    addSkillBranchBonuses(
        "miner_offensive",
        new DamageBonus(0.05f, MULTIPLY_BASE)
            .setPlayerCondition(
                new HasGemsCondition(
                    1, -1, new EquipmentCondition(EquipmentCondition.Type.WEAPON))),
        1,
        8);
    addSkillBonus(
        "miner_defensive_notable_1",
        createAttributeBonus(ARMOR, 2, ADDITION)
            .setMultiplier(
                new GemsAmountMultiplier(new EquipmentCondition(EquipmentCondition.Type.HELMET))));
    addSkillBonus(
        "miner_offensive_notable_1",
        new DamageBonus(0.15f, MULTIPLY_BASE)
            .setPlayerCondition(
                new HasGemsCondition(
                    1, -1, new EquipmentCondition(EquipmentCondition.Type.WEAPON))));
    addSkillBranchBonuses(
        "miner_life",
        createAttributeBonus(MAX_HEALTH, 1, ADDITION)
            .setMultiplier(
                new GemsAmountMultiplier(new EquipmentCondition(EquipmentCondition.Type.HELMET))),
        1,
        2);
    addSkillBonus(
        "miner_life_notable_1",
        createAttributeBonus(MAX_HEALTH, 1, ADDITION)
            .setMultiplier(
                new GemsAmountMultiplier(new EquipmentCondition(EquipmentCondition.Type.ARMOR))));
    addSkillBranchBonuses(
        "miner_speed",
        createAttributeBonus(ATTACK_SPEED, 0.02, MULTIPLY_BASE)
            .setCondition(
                new HasGemsCondition(
                    1, -1, new EquipmentCondition(EquipmentCondition.Type.WEAPON))),
        1,
        2);
    addSkillBonus(
        "miner_speed_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.04, MULTIPLY_BASE)
            .setMultiplier(
                new GemsAmountMultiplier(new EquipmentCondition(EquipmentCondition.Type.WEAPON))));
    addSkillBranchBonuses(
        "miner_lesser",
        new BlockBreakSpeedBonus(
            new HasItemInHandCondition(new EquipmentCondition(EquipmentCondition.Type.PICKAXE)),
            0.05f),
        1,
        6);
    addSkillBonus(
        "miner_mastery",
        new PlayerSocketsBonus(new EquipmentCondition(EquipmentCondition.Type.ANY), 1));
    addSkillBranchBonuses(
        "miner_crit",
        new CritChanceBonus(0.01f)
            .setPlayerMultiplier(
                new GemsAmountMultiplier(new EquipmentCondition(EquipmentCondition.Type.WEAPON))),
        1,
        2);
    addSkillBonus(
        "miner_crit_notable_1",
        new CritDamageBonus(0.1f)
            .setPlayerMultiplier(
                new GemsAmountMultiplier(new EquipmentCondition(EquipmentCondition.Type.WEAPON))));
    addSkillBranchBonuses(
        "miner_crafting",
        new LootDuplicationBonus(0.05f, 1f, LootDuplicationBonus.LootType.GEMS),
        1,
        3);
    addSkillBonus(
        "miner_defensive_crafting_keystone_1",
        new GemPowerBonus(new EquipmentCondition(EquipmentCondition.Type.ARMOR), 0.3f));
    addSkillBonus(
        "miner_defensive_crafting_keystone_1",
        new PlayerSocketsBonus(new EquipmentCondition(EquipmentCondition.Type.CHESTPLATE), 1));
    addSkillBonus(
        "miner_offensive_crafting_keystone_1",
        new GemPowerBonus(new EquipmentCondition(EquipmentCondition.Type.WEAPON), 0.3f));
    addSkillBonus(
        "miner_offensive_crafting_keystone_1",
        new PlayerSocketsBonus(new EquipmentCondition(EquipmentCondition.Type.WEAPON), 1));
    addSkillBranchBonuses("miner_healing", REGENERATION, 0.1, ADDITION, 1, 4);
    addSkillBonus(
        "miner_healing_notable_1",
        createAttributeBonus(REGENERATION.get(), 0.1, ADDITION)
            .setMultiplier(
                new GemsAmountMultiplier(new EquipmentCondition(EquipmentCondition.Type.HELMET))));
    addSkillBonus(
        "miner_crafting_notable_1",
        new LootDuplicationBonus(0.1f, 1f, LootDuplicationBonus.LootType.GEMS));
    addSkillBonus(
        "miner_defensive_keystone_1",
        createAttributeBonus(ARMOR, 5, ADDITION)
            .setMultiplier(
                new GemsAmountMultiplier(
                    new EquipmentCondition(EquipmentCondition.Type.CHESTPLATE))));
    addSkillBonus(
        "miner_offensive_keystone_1",
        new DamageBonus(0.1f, MULTIPLY_BASE)
            .setPlayerMultiplier(
                new GemsAmountMultiplier(new EquipmentCondition(EquipmentCondition.Type.WEAPON))));
    // traveler skills
    addSkillBranchBonuses("miner_subclass_1_defensive", ARMOR, 1, ADDITION, 1, 4);
    addSkillBonus("miner_subclass_1", ATTACK_SPEED, 0.1, MULTIPLY_BASE);
    addSkillBonus("miner_subclass_1", MOVEMENT_SPEED, 0.1, MULTIPLY_BASE);
    addSkillBranchBonuses("miner_subclass_1_offensive", ATTACK_SPEED, 0.02, MULTIPLY_BASE, 1, 4);
    addSkillBranchBonuses(
        "miner_subclass_1_crafting",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.BOOTS),
            new ItemSkillBonus(createAttributeBonus(MOVEMENT_SPEED, 0.02, MULTIPLY_BASE))),
        1,
        5);
    addSkillBonus(
        "miner_subclass_1_crafting_notable_1",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.BOOTS),
            new ItemSkillBonus(createAttributeBonus(MOVEMENT_SPEED, 0.05, MULTIPLY_BASE))));
    addSkillBonus("miner_subclass_1_offensive_notable_1", ATTACK_SPEED, 0.05, MULTIPLY_BASE);
    addSkillBonus("miner_subclass_1_offensive_notable_1", MOVEMENT_SPEED, 0.05, MULTIPLY_BASE);
    addSkillBonus(
        "miner_subclass_1_mastery",
        new DamageBonus(0.1f, MULTIPLY_BASE)
            .setPlayerMultiplier(
                new GemsAmountMultiplier(new EquipmentCondition(EquipmentCondition.Type.BOOTS))));
    addSkillBonus(
        "miner_subclass_special",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.BOOTS), new ItemSocketsBonus(1)));
    // jeweler skills
    addSkillBranchBonuses("miner_subclass_2_defensive", EVASION, 0.5, ADDITION, 1, 4);
    addSkillBranchBonuses("miner_subclass_2_defensive", ARMOR, 0.5, ADDITION, 1, 4);
    addSkillBonus(
        "miner_subclass_2",
        new PlayerSocketsBonus(new ItemTagCondition(PSTTags.RINGS.location()), 1));
    addSkillBranchBonuses(
        "miner_subclass_2_life",
        createAttributeBonus(MAX_HEALTH, 1d, ADDITION)
            .setMultiplier(
                new GemsAmountMultiplier(new ItemTagCondition(PSTTags.JEWELRY.location()))),
        1,
        4);
    addSkillBranchBonuses(
        "miner_subclass_2_crafting",
        new GemPowerBonus(new ItemTagCondition(PSTTags.JEWELRY.location()), 0.05f),
        1,
        5);
    addSkillBonus(
        "miner_subclass_2_crafting_notable_1",
        new GemPowerBonus(new ItemTagCondition(PSTTags.JEWELRY.location()), 0.25f));
    addSkillBonus(
        "miner_subclass_2_life_notable_1",
        new CraftedItemBonus(
            new ItemTagCondition(PSTTags.NECKLACES.location()),
            new ItemSkillBonus(createAttributeBonus(MAX_HEALTH, 5, ADDITION))));
    addSkillBonus("miner_subclass_2_mastery", SlotAttribute.getOrCreate("ring"), 1, ADDITION);
    // blacksmith skills
    addSkillBonus(
        "blacksmith_class",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.ANY),
            new ItemDurabilityBonus(0.25f, MULTIPLY_BASE)));
    addSkillBranchBonuses(
        "blacksmith_defensive_crafting",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.ARMOR),
            new ItemSkillBonus(createAttributeBonus(ARMOR, 0.1, ADDITION))),
        1,
        7);
    addSkillBranchBonuses(
        "blacksmith_offensive_crafting",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.MELEE_WEAPON),
            new ItemSkillBonus(createAttributeBonus(ATTACK_DAMAGE, 1, ADDITION))),
        1,
        7);
    addSkillBranchBonuses("blacksmith_defensive", ARMOR, 1, ADDITION, 1, 8);
    addSkillBranchBonuses(
        "blacksmith_offensive",
        new DamageBonus(0.05f, MULTIPLY_BASE)
            .setPlayerCondition(
                new HasItemInHandCondition(new EquipmentCondition(EquipmentCondition.Type.SHIELD))),
        1,
        8);
    addSkillBonus("blacksmith_defensive_notable_1", ARMOR, 0.05, MULTIPLY_BASE);
    addSkillBonus(
        "blacksmith_offensive_notable_1",
        new DamageBonus(0.25f, MULTIPLY_BASE)
            .setPlayerCondition(
                new HasItemInHandCondition(
                    new EquipmentCondition(EquipmentCondition.Type.SHIELD))));
    addSkillBranchBonuses(
        "blacksmith_life",
        createAttributeBonus(MAX_HEALTH, 1, ADDITION)
            .setCondition(
                new HasItemInHandCondition(new EquipmentCondition(EquipmentCondition.Type.SHIELD))),
        1,
        2);
    addSkillBonus(
        "blacksmith_life_notable_1",
        createAttributeBonus(MAX_HEALTH, 4, ADDITION)
            .setCondition(
                new HasItemInHandCondition(
                    new EquipmentCondition(EquipmentCondition.Type.SHIELD))));
    addSkillBranchBonuses(
        "blacksmith_speed",
        createAttributeBonus(ATTACK_SPEED, 0.02, MULTIPLY_BASE)
            .setCondition(
                new HasItemInHandCondition(new EquipmentCondition(EquipmentCondition.Type.SHIELD))),
        1,
        2);
    addSkillBonus(
        "blacksmith_speed_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.05, MULTIPLY_BASE)
            .setCondition(
                new HasItemInHandCondition(
                    new EquipmentCondition(EquipmentCondition.Type.SHIELD))));
    addSkillBranchBonuses(
        "blacksmith_lesser",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.ANY),
            new ItemDurabilityBonus(0.05f, MULTIPLY_BASE)),
        1,
        6);
    addSkillBonus(
        "blacksmith_mastery",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.ANY),
            new ItemSkillBonus(createAttributeBonus(ARMOR_TOUGHNESS, 1f, ADDITION))));
    addSkillBranchBonuses(
        "blacksmith_crit",
        new CritChanceBonus(0.02f)
            .setPlayerCondition(
                new HasItemInHandCondition(new EquipmentCondition(EquipmentCondition.Type.SHIELD))),
        1,
        2);
    addSkillBonus(
        "blacksmith_crit_notable_1",
        new CritDamageBonus(0.3f)
            .setPlayerCondition(
                new HasItemInHandCondition(
                    new EquipmentCondition(EquipmentCondition.Type.SHIELD))));
    addSkillBranchBonuses(
        "blacksmith_crafting",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.SHIELD),
            new ItemSkillBonus(createAttributeBonus(ARMOR, 2, ADDITION))),
        1,
        3);
    addSkillBonus(
        "blacksmith_defensive_crafting_keystone_1",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.ARMOR),
            new ItemSkillBonus(createAttributeBonus(ARMOR, 1, ADDITION))));
    addSkillBonus(
        "blacksmith_offensive_crafting_keystone_1",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.MELEE_WEAPON),
            new ItemSkillBonus(createAttributeBonus(ATTACK_SPEED, 0.25, MULTIPLY_BASE))));
    addSkillBranchBonuses("blacksmith_healing", REGENERATION, 0.1, ADDITION, 1, 4);
    addSkillBonus(
        "blacksmith_healing_notable_1",
        createAttributeBonus(REGENERATION.get(), 0.2, ADDITION)
            .setCondition(
                new HasItemInHandCondition(
                    new EquipmentCondition(EquipmentCondition.Type.SHIELD))));
    addSkillBonus(
        "blacksmith_crafting_notable_1",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.SHIELD),
            new ItemSkillBonus(createAttributeBonus(ARMOR, 4, ADDITION))));
    addSkillBonus(
        "blacksmith_defensive_keystone_1",
        createAttributeBonus(ARMOR, 0.1, MULTIPLY_BASE)
            .setCondition(new AttributeValueCondition(ARMOR, 50, -1)));
    addSkillBonus(
        "blacksmith_defensive_keystone_1",
        createAttributeBonus(ARMOR, 0.1, MULTIPLY_BASE)
            .setCondition(new AttributeValueCondition(ARMOR, 100, -1)));
    addSkillBonus(
        "blacksmith_offensive_keystone_1",
        createAttributeBonus(ATTACK_DAMAGE, 0.1, ADDITION)
            .setMultiplier(new AttributeValueMultiplier(ARMOR)));
    // soldier skills
    addSkillBranchBonuses("blacksmith_subclass_1_defensive", ARMOR, 0.5, ADDITION, 1, 4);
    addSkillBranchBonuses("blacksmith_subclass_1_defensive", BLOCKING, 0.5, ADDITION, 1, 4);
    addSkillBonus("blacksmith_subclass_1", ARMOR, 5, ADDITION);
    addSkillBonus("blacksmith_subclass_1", BLOCKING, 5, ADDITION);
    addSkillBranchBonuses(
        "blacksmith_subclass_1_offensive",
        new DamageBonus(0.01f, MULTIPLY_BASE).setDamageCondition(new MeleeDamageCondition()),
        1,
        4);
    addSkillBranchBonuses("blacksmith_subclass_1_offensive", BLOCKING, 1, ADDITION, 1, 4);
    addSkillBranchBonuses(
        "blacksmith_subclass_1_crafting",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.MELEE_WEAPON),
            new ItemSkillBonus(new CritChanceBonus(0.01f))),
        1,
        5);
    addSkillBonus(
        "blacksmith_subclass_1_crafting_notable_1",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.MELEE_WEAPON),
            new ItemSkillBonus(new CritChanceBonus(0.05f))));
    addSkillBonus(
        "blacksmith_subclass_1_offensive_notable_1",
        new DamageBonus(0.2f, MULTIPLY_BASE).setDamageCondition(new MeleeDamageCondition()));
    addSkillBonus(
        "blacksmith_subclass_1_offensive_notable_1",
        new CritDamageBonus(0.1f).setDamageCondition(new MeleeDamageCondition()));
    addSkillBonus("blacksmith_subclass_1_mastery", BLOCKING, 10, ADDITION);
    addSkillBonus("blacksmith_subclass_1_mastery", ARMOR, 0.05, MULTIPLY_BASE);
    addSkillBonus(
        "blacksmith_subclass_1_mastery",
        new DamageBonus(0.05f, MULTIPLY_BASE).setDamageCondition(new MeleeDamageCondition()));
    addSkillBonus(
        "blacksmith_subclass_special",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.WEAPON),
            new ItemSkillBonus(
                new LootDuplicationBonus(0.05f, 1f, LootDuplicationBonus.LootType.MOBS))));
    // artisan skills
    addSkillBranchBonuses("blacksmith_subclass_2_defensive", ARMOR, 1, ADDITION, 1, 4);
    addSkillBonus(
        "blacksmith_subclass_2",
        new RepairEfficiencyBonus(new EquipmentCondition(EquipmentCondition.Type.ANY), 1f));
    addSkillBranchBonuses(
        "blacksmith_subclass_2_life",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.ARMOR),
            new ItemSkillBonus(createAttributeBonus(MAX_HEALTH, 1, ADDITION))),
        1,
        4);
    addSkillBranchBonuses(
        "blacksmith_subclass_2_crafting",
        new RepairEfficiencyBonus(new EquipmentCondition(EquipmentCondition.Type.ANY), 0.05f),
        1,
        5);
    addSkillBonus(
        "blacksmith_subclass_2_crafting_notable_1",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.SHIELD),
            new ItemSkillBonus(createAttributeBonus(BLOCKING.get(), 5, ADDITION))));
    addSkillBonus(
        "blacksmith_subclass_2_life_notable_1",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.SHIELD),
            new ItemSkillBonus(createAttributeBonus(MAX_HEALTH, 5, ADDITION))));
    addSkillBonus(
        "blacksmith_subclass_2_mastery",
        new RepairEfficiencyBonus(new EquipmentCondition(EquipmentCondition.Type.ANY), 0.05f));
    addSkillBonus(
        "blacksmith_subclass_2_mastery",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.ANY),
            new ItemDurabilityBonus(0.05f, MULTIPLY_BASE)));
    addSkillBonus(
        "blacksmith_subclass_2_mastery",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.WEAPON),
            new ItemSkillBonus(createAttributeBonus(ATTACK_SPEED, 0.05, MULTIPLY_BASE))));
    addSkillBonus(
        "blacksmith_subclass_2_mastery",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.ARMOR),
            new ItemSkillBonus(createAttributeBonus(ARMOR_TOUGHNESS, 0.05f, MULTIPLY_BASE))));
    addSkillBonus(
        "blacksmith_subclass_2_mastery",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.SHIELD),
            new ItemSkillBonus(createAttributeBonus(ARMOR, 5, ADDITION))));
    // enchanter skills
    addSkillBonus("enchanter_class", new EnchantmentRequirementBonus(-0.3f));
    addSkillBranchBonuses(
        "enchanter_defensive_crafting",
        new EnchantmentAmplificationBonus(new ArmorEnchantmentCondition(), 0.1f),
        1,
        7);
    addSkillBranchBonuses(
        "enchanter_offensive_crafting",
        new EnchantmentAmplificationBonus(new WeaponEnchantmentCondition(), 0.1f),
        1,
        7);
    addSkillBranchBonuses("enchanter_defensive", BLOCKING, 1, ADDITION, 1, 8);
    addSkillBranchBonuses(
        "enchanter_offensive",
        new DamageBonus(0.05f, MULTIPLY_BASE)
            .setPlayerCondition(
                new HasItemInHandCondition(
                    new EnchantedCondition(
                        new EquipmentCondition(EquipmentCondition.Type.WEAPON)))),
        1,
        8);
    addSkillBonus(
        "enchanter_defensive_notable_1",
        createAttributeBonus(BLOCKING.get(), 10, ADDITION)
            .setCondition(
                new HasItemInHandCondition(
                    new EnchantedCondition(
                        new EquipmentCondition(EquipmentCondition.Type.SHIELD)))));
    addSkillBonus(
        "enchanter_offensive_notable_1",
        new DamageBonus(0.2f, MULTIPLY_BASE)
            .setPlayerCondition(
                new HasItemInHandCondition(
                    new EnchantedCondition(
                        new EquipmentCondition(EquipmentCondition.Type.WEAPON)))));
    addSkillBranchBonuses(
        "enchanter_life",
        createAttributeBonus(MAX_HEALTH, 2, ADDITION)
            .setCondition(
                new HasItemEquippedCondition(new EnchantedCondition(NoneItemCondition.INSTANCE))),
        1,
        2);
    addSkillBonus(
        "enchanter_life_notable_1",
        createAttributeBonus(MAX_HEALTH, 1, ADDITION)
            .setMultiplier(
                new EnchantsAmountMultiplier(
                    new EquipmentCondition(EquipmentCondition.Type.ARMOR))));
    addSkillBranchBonuses(
        "enchanter_speed",
        createAttributeBonus(ATTACK_SPEED, 0.02, MULTIPLY_BASE)
            .setCondition(
                new HasItemInHandCondition(
                    new EnchantedCondition(
                        new EquipmentCondition(EquipmentCondition.Type.WEAPON)))),
        1,
        2);
    addSkillBonus(
        "enchanter_speed_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.05, MULTIPLY_BASE)
            .setCondition(
                new HasItemInHandCondition(
                    new EnchantedCondition(
                        new EquipmentCondition(EquipmentCondition.Type.WEAPON)))));
    addSkillBranchBonuses("enchanter_lesser", new EnchantmentRequirementBonus(-0.05f), 1, 6);
    addSkillBonus("enchanter_mastery", new EnchantmentAmplificationBonus(1f));
    addSkillBranchBonuses(
        "enchanter_crit",
        new CritChanceBonus(0.02f)
            .setPlayerCondition(
                new HasItemInHandCondition(
                    new EnchantedCondition(
                        new EquipmentCondition(EquipmentCondition.Type.WEAPON)))),
        1,
        2);
    addSkillBonus(
        "enchanter_crit_notable_1",
        new CritDamageBonus(0.05f)
            .setPlayerMultiplier(
                new EnchantsAmountMultiplier(
                    new EquipmentCondition(EquipmentCondition.Type.WEAPON))));
    addSkillBranchBonuses("enchanter_crafting", new FreeEnchantmentBonus(0.05f), 1, 3);
    addSkillBonus(
        "enchanter_defensive_crafting_keystone_1",
        new EnchantmentAmplificationBonus(new ArmorEnchantmentCondition(), 0.4f));
    addSkillBonus(
        "enchanter_offensive_crafting_keystone_1",
        new EnchantmentAmplificationBonus(new WeaponEnchantmentCondition(), 0.4f));
    addSkillBranchBonuses(
        "enchanter_healing",
        new HealingBonus(1f, 0.1f, new BlockEventListener().setTarget(SkillBonus.Target.PLAYER)),
        1,
        4);
    addSkillBonus(
        "enchanter_healing_notable_1",
        new HealingBonus(
            1f,
            0.2f,
            new BlockEventListener()
                .setTarget(SkillBonus.Target.PLAYER)
                .setPlayerMultiplier(
                    new EnchantsAmountMultiplier(
                        new EquipmentCondition(EquipmentCondition.Type.SHIELD)))));
    addSkillBonus("enchanter_crafting_notable_1", new FreeEnchantmentBonus(0.1f));
    addSkillBonus(
        "enchanter_defensive_keystone_1",
        createAttributeBonus(BLOCKING.get(), 5, ADDITION)
            .setMultiplier(
                new EnchantsAmountMultiplier(
                    new EquipmentCondition(EquipmentCondition.Type.SHIELD))));
    addSkillBonus(
        "enchanter_offensive_keystone_1",
        new DamageBonus(0.05f, MULTIPLY_BASE)
            .setPlayerMultiplier(
                new EnchantLevelsAmountMultiplier(
                    new EquipmentCondition(EquipmentCondition.Type.WEAPON))));
    // arsonist skills
    addSkillBranchBonuses("enchanter_subclass_1_defensive", EVASION, 0.5, ADDITION, 1, 4);
    addSkillBranchBonuses("enchanter_subclass_1_defensive", BLOCKING, 0.5, ADDITION, 1, 4);
    addSkillBonus(
        "enchanter_subclass_1",
        new DamageBonus(0.15f, MULTIPLY_BASE).setTargetCondition(new BurningCondition()));
    addSkillBonus("enchanter_subclass_1", new IgniteBonus(0.15f, 5));
    addSkillBranchBonuses(
        "enchanter_subclass_1_offensive",
        new DamageBonus(0.05f, MULTIPLY_BASE).setTargetCondition(new BurningCondition()),
        1,
        4);
    addSkillBranchBonuses(
        "enchanter_subclass_1_crafting",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.WEAPON),
            new ItemSkillBonus(new IgniteBonus(0.05f, 5))),
        1,
        5);
    addSkillBonus(
        "enchanter_subclass_1_crafting_notable_1",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.WEAPON),
            new ItemSkillBonus(
                new DamageBonus(0.2f, MULTIPLY_BASE).setTargetCondition(new BurningCondition()))));
    addSkillBonus(
        "enchanter_subclass_1_offensive_notable_1",
        new CritChanceBonus(0.1f).setTargetCondition(new BurningCondition()));
    addSkillBonus("enchanter_subclass_1_mastery", new IgniteBonus(0.1f, 5));
    addSkillBonus(
        "enchanter_subclass_1_mastery",
        new DamageBonus(0.1f, MULTIPLY_BASE).setTargetCondition(new BurningCondition()));
    addSkillBonus(
        "enchanter_subclass_1_mastery",
        new CritChanceBonus(0.1f).setTargetCondition(new BurningCondition()));
    addSkillBonus(
        "enchanter_subclass_special",
        new CraftedItemBonus(
            new ItemTagCondition(PSTTags.QUIVERS.location()),
            new ItemSkillBonus(
                new DamageBonus(0.1f, MULTIPLY_BASE).setTargetCondition(new BurningCondition()))));
    addSkillBonus(
        "enchanter_subclass_special",
        new CraftedItemBonus(
            new ItemTagCondition(PSTTags.QUIVERS.location()),
            new ItemSkillBonus(new IgniteBonus(0.1f, 5))));
    // scholar skills
    addSkillBranchBonuses("enchanter_subclass_2_defensive", BLOCKING, 1, ADDITION, 1, 4);
    addSkillBonus("enchanter_subclass_2", EXP_PER_MINUTE, 2, ADDITION);
    addSkillBranchBonuses("enchanter_subclass_2_life", MAX_HEALTH, 2, ADDITION, 1, 4);
    addSkillBranchBonuses("enchanter_subclass_2_life", EXP_PER_MINUTE, 0.1, ADDITION, 1, 4);
    addSkillBranchBonuses("enchanter_subclass_2_crafting", EXP_PER_MINUTE, 0.2, ADDITION, 1, 5);
    addSkillBonus(
        "enchanter_subclass_2_crafting_notable_1",
        new GainedExperienceBonus(1f, GainedExperienceBonus.ExperienceSource.ORE));
    addSkillBonus("enchanter_subclass_2_life_notable_1", MAX_HEALTH, 6, ADDITION);
    addSkillBonus("enchanter_subclass_2_life_notable_1", EXP_PER_MINUTE, 0.1, ADDITION);
    addSkillBonus("enchanter_subclass_2_mastery", EXP_PER_MINUTE, 1.5, ADDITION);
    // cook skills
    addSkillBonus(
        "cook_class", new CraftedItemBonus(new FoodCondition(), new FoodSaturationBonus(0.2f)));
    addSkillBranchBonuses(
        "cook_defensive_crafting",
        new CraftedItemBonus(new FoodCondition(), new FoodHealingBonus(0.1f)),
        1,
        7);
    addSkillBranchBonuses(
        "cook_offensive_crafting",
        new CraftedItemBonus(
            new FoodCondition(),
            new FoodEffectBonus(new MobEffectInstance(PSTEffects.DAMAGE_BONUS.get(), 1200, 1))),
        1,
        7);
    addSkillBranchBonuses("cook_defensive", BLOCKING, 1, ADDITION, 1, 8);
    addSkillBranchBonuses(
        "cook_offensive",
        new DamageBonus(0.05f, MULTIPLY_BASE).setPlayerCondition(new FoodLevelCondition(15, -1)),
        1,
        8);
    addSkillBonus(
        "cook_defensive_notable_1",
        createAttributeBonus(BLOCKING.get(), 10, ADDITION)
            .setCondition(new FoodLevelCondition(15, -1)));
    addSkillBonus(
        "cook_offensive_notable_1",
        new DamageBonus(0.2f, MULTIPLY_BASE).setPlayerCondition(new FoodLevelCondition(15, -1)));
    addSkillBranchBonuses(
        "cook_life",
        createAttributeBonus(MAX_HEALTH, 2, ADDITION).setCondition(new FoodLevelCondition(15, -1)),
        1,
        2);
    addSkillBonus(
        "cook_life_notable_1",
        createAttributeBonus(MAX_HEALTH, 1, ADDITION).setMultiplier(new HungerLevelMultiplier()));
    addSkillBranchBonuses(
        "cook_speed",
        createAttributeBonus(ATTACK_SPEED, 0.02, MULTIPLY_BASE)
            .setCondition(new FoodLevelCondition(15, -1)),
        1,
        2);
    addSkillBonus(
        "cook_speed_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.1, MULTIPLY_BASE)
            .setCondition(new FoodLevelCondition(15, -1)));
    addSkillBranchBonuses(
        "cook_lesser",
        new CraftedItemBonus(new FoodCondition(), new FoodSaturationBonus(0.05f)),
        1,
        6);
    addSkillBonus(
        "cook_mastery", new CraftedItemBonus(new FoodCondition(), new FoodSaturationBonus(0.5f)));
    addSkillBranchBonuses(
        "cook_crit",
        new CritChanceBonus(0.02f).setPlayerCondition(new FoodLevelCondition(15, -1)),
        1,
        2);
    addSkillBonus(
        "cook_crit_notable_1",
        new CritDamageBonus(0.01f).setPlayerMultiplier(new HungerLevelMultiplier()));
    addSkillBranchBonuses(
        "cook_crafting",
        new CraftedItemBonus(
            new FoodCondition(),
            new FoodEffectBonus(
                new MobEffectInstance(PSTEffects.LIFE_REGENERATION_BONUS.get(), 1200, 9))),
        1,
        3);
    addSkillBonus(
        "cook_defensive_crafting_keystone_1",
        new CraftedItemBonus(new FoodCondition(), new FoodHealingBonus(0.3f)));
    addSkillBonus(
        "cook_offensive_crafting_keystone_1",
        new CraftedItemBonus(
            new FoodCondition(),
            new FoodEffectBonus(
                new MobEffectInstance(PSTEffects.CRIT_DAMAGE_BONUS.get(), 1200, 19))));
    addSkillBranchBonuses(
        "cook_healing",
        new HealingBonus(1f, 0.1f, new BlockEventListener().setTarget(SkillBonus.Target.PLAYER)),
        1,
        4);
    addSkillBonus(
        "cook_healing_notable_1",
        new HealingBonus(
            1f,
            1f,
            new BlockEventListener()
                .setTarget(SkillBonus.Target.PLAYER)
                .setPlayerCondition(new FoodLevelCondition(15, -1))));
    addSkillBonus(
        "cook_crafting_notable_1",
        new CraftedItemBonus(
            new FoodCondition(),
            new FoodEffectBonus(
                new MobEffectInstance(PSTEffects.LIFE_REGENERATION_BONUS.get(), 1200, 19))));
    addSkillBonus(
        "cook_defensive_keystone_1",
        createAttributeBonus(BLOCKING.get(), 1, ADDITION)
            .setMultiplier(new HungerLevelMultiplier()));
    addSkillBonus(
        "cook_offensive_keystone_1",
        new DamageBonus(0.02f, MULTIPLY_BASE).setPlayerMultiplier(new HungerLevelMultiplier()));
    // berserker skills
    addSkillBranchBonuses("cook_subclass_1_defensive", BLOCKING, 1, ADDITION, 1, 4);
    addSkillBonus(
        "cook_subclass_1",
        new DamageBonus(0.1f, MULTIPLY_BASE)
            .setPlayerCondition(new HealthPercentageCondition(-1f, 0.75f)));
    addSkillBonus(
        "cook_subclass_1",
        new DamageBonus(0.2f, MULTIPLY_BASE)
            .setPlayerCondition(new HealthPercentageCondition(-1f, 0.5f)));
    addSkillBranchBonuses("cook_subclass_1_offensive", ATTACK_SPEED, 0.01, MULTIPLY_BASE, 1, 4);
    addSkillBranchBonuses(
        "cook_subclass_1_crafting",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.AXE),
            new ItemSkillBonus(new CritChanceBonus(0.01f))),
        1,
        5);
    addSkillBonus(
        "cook_subclass_1_crafting_notable_1",
        new CraftedItemBonus(
            new EquipmentCondition(EquipmentCondition.Type.AXE),
            new ItemSkillBonus(new CritChanceBonus(0.05f))));
    addSkillBonus(
        "cook_subclass_1_offensive_notable_1",
        createAttributeBonus(ATTACK_SPEED, 0.1, MULTIPLY_BASE)
            .setCondition(new HealthPercentageCondition(-1, 0.5f)));
    addSkillBonus(
        "cook_subclass_1_mastery",
        new CritChanceBonus(0.15f).setPlayerCondition(new HealthPercentageCondition(-1, 0.5f)));
    addSkillBonus(
        "cook_subclass_1_mastery",
        new HealingBonus(
            1f,
            1f,
            new AttackEventListener()
                .setTarget(SkillBonus.Target.PLAYER)
                .setPlayerCondition(new HealthPercentageCondition(-1, 0.5f))));
    addSkillBonus(
        "cook_subclass_special",
        new GainedExperienceBonus(0.5f, GainedExperienceBonus.ExperienceSource.MOBS));
    // fisherman skills
    addSkillBranchBonuses("cook_subclass_2_defensive", ARMOR, 0.5, ADDITION, 1, 4);
    addSkillBranchBonuses("cook_subclass_2_defensive", BLOCKING, 0.5, ADDITION, 1, 4);
    addSkillBonus(
        "cook_subclass_2",
        new LootDuplicationBonus(0.15f, 1f, LootDuplicationBonus.LootType.FISHING));
    addSkillBranchBonuses("cook_subclass_2_life", MAX_HEALTH, 1, ADDITION, 1, 4);
    addSkillBranchBonuses(
        "cook_subclass_2_crafting",
        new GainedExperienceBonus(0.2f, GainedExperienceBonus.ExperienceSource.FISHING),
        1,
        5);
    addSkillBonus(
        "cook_subclass_2_crafting_notable_1",
        new GainedExperienceBonus(0.5f, GainedExperienceBonus.ExperienceSource.FISHING));
    addSkillBonus("cook_subclass_2_life_notable_1", MAX_HEALTH, 4, ADDITION);
    addSkillBonus(
        "cook_subclass_2_life_notable_1",
        createAttributeBonus(LUCK, 1, ADDITION).setCondition(new FishingCondition()));
    addSkillBonus(
        "cook_subclass_2_mastery",
        new GainedExperienceBonus(0.5f, GainedExperienceBonus.ExperienceSource.FISHING));
    addSkillBonus(
        "cook_subclass_2_mastery",
        new LootDuplicationBonus(0.05f, 5f, LootDuplicationBonus.LootType.FISHING));
  }

  private void addSkillBranchBonuses(
      String branchName,
      Attribute attribute,
      double amount,
      Operation operation,
      int from,
      int to) {
    for (int node = from; node <= to; node++) {
      addSkillBonus(branchName + "_" + node, attribute, amount, operation);
    }
  }

  private void addSkillBranchBonuses(String branchName, SkillBonus<?> bonus, int from, int to) {
    for (int node = from; node <= to; node++) {
      addSkillBonus(branchName + "_" + node, bonus);
    }
  }

  private void addSkillBonus(
      String skillName, Attribute attribute, double amount, Operation operation) {
    getSkill(skillName).addSkillBonus(createAttributeBonus(attribute, amount, operation));
  }

  private void addSkillBonus(String skillName, SkillBonus<?> bonus) {
    getSkill(skillName).addSkillBonus(bonus);
  }

  @NotNull
  private static AttributeBonus createAttributeBonus(
      Attribute attribute, double amount, Operation operation) {
    return new AttributeBonus(
        attribute, new AttributeModifier(UUID.randomUUID(), "SkillBonus", amount, operation));
  }

  private void addSkillBranchBonuses(
      String branchName,
      RegistryObject<Attribute> attribute,
      double amount,
      Operation operation,
      int from,
      int to) {
    addSkillBranchBonuses(branchName, attribute.get(), amount, operation, from, to);
  }

  private void addSkillBonus(
      String skillName, RegistryObject<Attribute> attribute, double amount, Operation operation) {
    addSkillBonus(skillName, attribute.get(), amount, operation);
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
    getSkill(gatewayId1).getLongConnections().add(new ResourceLocation("skilltree", gatewayId2));
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
  public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
    ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();
    addSkills();
    shapeSkillTree();
    setSkillsAttributeModifiers();
    skills.values().forEach(skill -> futuresBuilder.add(save(output, skill)));
    return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
  }

  private CompletableFuture<?> save(CachedOutput output, PassiveSkill skill) {
    Path path = packOutput.getOutputFolder().resolve(getPath(skill));
    JsonElement json = SkillsReloader.GSON.toJsonTree(skill);
    return DataProvider.saveStable(output, json, path);
  }

  public String getPath(PassiveSkill skill) {
    ResourceLocation id = skill.getId();
    return "data/%s/skills/%s.json".formatted(id.getNamespace(), id.getPath());
  }

  public Map<ResourceLocation, PassiveSkill> getSkills() {
    return skills;
  }

  @Override
  public @NotNull String getName() {
    return "Skills Provider";
  }
}
