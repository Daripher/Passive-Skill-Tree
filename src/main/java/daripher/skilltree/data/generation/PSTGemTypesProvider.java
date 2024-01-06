package daripher.skilltree.data.generation;

import com.google.gson.JsonElement;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.reloader.GemTypesReloader;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.item.gem.GemType;
import daripher.skilltree.item.gem.bonus.GemBonusProvider;
import daripher.skilltree.item.gem.bonus.GemRemovalBonusProvider;
import daripher.skilltree.item.gem.bonus.RandomGemBonusProvider;
import daripher.skilltree.item.gem.bonus.SimpleGemBonusProvider;
import daripher.skilltree.skill.bonus.condition.item.*;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemDurabilityBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

public class PSTGemTypesProvider implements DataProvider {
  private final Map<ResourceLocation, GemType> gemTypes = new HashMap<>();
  private final DataGenerator dataGenerator;

  public PSTGemTypesProvider(DataGenerator dataGenerator) {
    this.dataGenerator = dataGenerator;
  }

  private void addGemTypes() {
    addSimpleGems(
        "sapphire",
        createGemBonuses(
            new ItemSkillBonus(new DamageBonus(0.01f, AttributeModifier.Operation.MULTIPLY_BASE)),
            new ItemSkillBonus(
                new AttributeBonus(
                    Attributes.ARMOR_TOUGHNESS,
                    "Sapphire",
                    0.1f,
                    AttributeModifier.Operation.ADDITION)),
            new ItemSkillBonus(
                new AttributeBonus(
                    Attributes.ARMOR,
                    "Sapphire",
                    0.01f,
                    AttributeModifier.Operation.MULTIPLY_BASE)),
            new ItemSkillBonus(
                new AttributeBonus(
                    Attributes.KNOCKBACK_RESISTANCE,
                    "Sapphire",
                    0.01f,
                    AttributeModifier.Operation.ADDITION)),
            new ItemDurabilityBonus(10, AttributeModifier.Operation.ADDITION),
            new ItemSkillBonus(
                new AttributeBonus(
                    PSTAttributes.BLOCKING.get(),
                    "Sapphire",
                    0.005f,
                    AttributeModifier.Operation.MULTIPLY_BASE))));
    addSimpleGems(
        "jade",
        createGemBonuses(
            new ItemSkillBonus(
                new AttributeBonus(
                    Attributes.ATTACK_SPEED,
                    "Jade",
                    0.01f,
                    AttributeModifier.Operation.MULTIPLY_BASE)),
            new ItemSkillBonus(
                new AttributeBonus(
                    PSTAttributes.EVASION.get(),
                    "Jade",
                    0.01f,
                    AttributeModifier.Operation.MULTIPLY_BASE)),
            new ItemSkillBonus(
                new GainedExperienceBonus(0.025f, GainedExperienceBonus.ExperienceSource.MOBS)),
            new ItemSkillBonus(
                new AttributeBonus(
                    Attributes.MOVEMENT_SPEED,
                    "Jade",
                    0.01f,
                    AttributeModifier.Operation.MULTIPLY_BASE)),
            new ItemSkillBonus(
                new AttributeBonus(
                    PSTAttributes.BLOCKING.get(),
                    "Jade",
                    0.01f,
                    AttributeModifier.Operation.MULTIPLY_BASE)),
            new ItemSkillBonus(
                new AttributeBonus(
                    PSTAttributes.EVASION.get(),
                    "Jade",
                    0.005f,
                    AttributeModifier.Operation.MULTIPLY_BASE))));
    addSimpleGems(
        "ruby",
        createGemBonuses(
            new ItemSkillBonus(
                new AttributeBonus(
                    PSTAttributes.LIFE_PER_HIT.get(),
                    "Ruby",
                    0.05f,
                    AttributeModifier.Operation.ADDITION)),
            new ItemSkillBonus(new IncomingHealingBonus(0.01f)),
            new ItemSkillBonus(
                new AttributeBonus(
                    Attributes.MAX_HEALTH,
                    "Ruby",
                    0.01f,
                    AttributeModifier.Operation.MULTIPLY_BASE)),
            new ItemSkillBonus(
                new AttributeBonus(
                    PSTAttributes.REGENERATION.get(),
                    "Ruby",
                    0.01f,
                    AttributeModifier.Operation.ADDITION)),
            new ItemSkillBonus(
                new AttributeBonus(
                    PSTAttributes.LIFE_ON_BLOCK.get(),
                    "Ruby",
                    0.05f,
                    AttributeModifier.Operation.ADDITION)),
            new ItemSkillBonus(new IncomingHealingBonus(0.005f))));
    addSimpleGems(
        "citrine",
        createGemBonuses(
            new ItemSkillBonus(new CritChanceBonus(0.01f)),
            new ItemSkillBonus(
                new AttributeBonus(
                    Attributes.LUCK, "Citrine", 0.1f, AttributeModifier.Operation.ADDITION)),
            new ItemSkillBonus(
                new LootDuplicationBonus(0.01f, 2f, LootDuplicationBonus.LootType.GEMS)),
            new ItemSkillBonus(new JumpHeightBonus(new NoneLivingCondition(), 0.01f)),
            new ItemSkillBonus(
                new AttributeBonus(
                    Attributes.LUCK, "Citrine", 0.01f, AttributeModifier.Operation.MULTIPLY_BASE)),
            new ItemSkillBonus(
                new LootDuplicationBonus(0.01f, 1f, LootDuplicationBonus.LootType.FISHING))));
    Map<Integer, List<GemBonusProvider>> irisciteBonuses = new HashMap<>();
    gemTypes.forEach(
        (key, value) -> {
          String gemId = key.getPath();
          int gemTier = Integer.parseInt(gemId.substring(gemId.length() - 1));
          List<GemBonusProvider> providers =
              irisciteBonuses.getOrDefault(gemTier, new ArrayList<>());
          providers.addAll(value.bonuses().values());
          irisciteBonuses.put(gemTier, providers);
        });
    for (int i = 0; i < 6; i++) {
      ResourceLocation id = new ResourceLocation(SkillTreeMod.MOD_ID, "iriscite_" + i);
      Map<ItemCondition, GemBonusProvider> bonuses =
          Map.of(new NoneItemCondition(), new RandomGemBonusProvider(irisciteBonuses.get(i)));
      gemTypes.put(id, new GemType(id, bonuses));
    }
    ResourceLocation vacuciteId = new ResourceLocation(SkillTreeMod.MOD_ID, "vacucite_3");
    Map<ItemCondition, GemBonusProvider> vacuciteBonuses =
        Map.of(new NoneItemCondition(), new GemRemovalBonusProvider());
    gemTypes.put(vacuciteId, new GemType(vacuciteId, vacuciteBonuses));
  }

  private void addSimpleGems(String name, Map<ItemCondition, ItemBonus<?>> bonuses) {
    for (int i = 0; i < 6; i++) {
      ResourceLocation id = new ResourceLocation(SkillTreeMod.MOD_ID, "%s_%d".formatted(name, i));
      HashMap<ItemCondition, GemBonusProvider> bonusProviders = new HashMap<>();
      int tier = i;
      bonuses.forEach(
          (c, b) -> {
            SimpleGemBonusProvider bonusProvider =
                new SimpleGemBonusProvider(b.copy().multiply(tier < 5 ? 1 + tier : 10));
            bonusProviders.put(c, bonusProvider);
          });
      gemTypes.put(id, new GemType(id, bonusProviders));
    }
  }

  private Map<ItemCondition, ItemBonus<?>> createGemBonuses(
      ItemBonus<?> weaponBonus,
      ItemBonus<?> chestplateBonus,
      ItemBonus<?> helmetBonus,
      ItemBonus<?> bootsBonus,
      ItemBonus<?> shieldBonus,
      ItemBonus<?> jewelryBonus) {
    Map<ItemCondition, ItemBonus<?>> bonuses = new HashMap<>();
    bonuses.put(new WeaponCondition(WeaponCondition.Type.ANY), weaponBonus);
    bonuses.put(new ArmorCondition(ArmorCondition.Type.CHESTPLATE), chestplateBonus);
    bonuses.put(new ArmorCondition(ArmorCondition.Type.HELMET), helmetBonus);
    bonuses.put(new ArmorCondition(ArmorCondition.Type.BOOTS), bootsBonus);
    bonuses.put(new ArmorCondition(ArmorCondition.Type.SHIELD), shieldBonus);
    bonuses.put(new JewelryCondition(), jewelryBonus);
    return bonuses;
  }

  @Override
  public void run(@NotNull CachedOutput output) {
    addGemTypes();
    gemTypes.values().forEach(gemType -> save(output, gemType));
  }

  private void save(CachedOutput output, GemType gemType) {
    Path path = dataGenerator.getOutputFolder().resolve(getPath(gemType));
    JsonElement json = GemTypesReloader.GSON.toJsonTree(gemType);
    try {
      DataProvider.saveStable(output, json, path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getPath(GemType gemType) {
    ResourceLocation id = gemType.id();
    return "data/%s/gem_types/%s.json".formatted(id.getNamespace(), id.getPath());
  }

  public Map<ResourceLocation, GemType> getGemTypes() {
    return gemTypes;
  }

  @Override
  public @NotNull String getName() {
    return "Gem Types Provider";
  }
}
