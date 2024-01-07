package daripher.skilltree.compat.apotheosis.gem;

import com.mojang.serialization.JsonOps;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemDurabilityBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.*;
import java.util.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import shadows.apotheosis.adventure.affix.socket.gem.Gem;
import shadows.apotheosis.adventure.affix.socket.gem.GemClass;
import shadows.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import shadows.apotheosis.adventure.loot.LootCategory;
import shadows.apotheosis.adventure.loot.LootRarity;

public class PSTGemsProvider extends JsonCodecProvider<Gem> {
  private static final Map<ResourceLocation, Gem> GEMS = new HashMap<>();
  private static final Map<String, GemClass> GEM_CLASSES = new HashMap<>();

  public PSTGemsProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
    super(
        dataGenerator,
        existingFileHelper,
        SkillTreeMod.MOD_ID,
        JsonOps.INSTANCE,
        PackType.SERVER_DATA,
        "gems",
        Gem.CODEC,
        generateGems());
  }

  private static Map<ResourceLocation, Gem> generateGems() {
    GEM_CLASSES.clear();
    GEMS.clear();
    createGemsClasses();
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
    return GEMS;
  }

  private static void addSimpleGems(String name, List<PSTGemBonus> bonuses) {
    for (int i = 0; i < 6; i++) {
      ResourceLocation id = new ResourceLocation(SkillTreeMod.MOD_ID, "%s_%d".formatted(name, i));
      List<GemBonus> bonusProviders = new ArrayList<>();
      int tier = i;
      bonuses.forEach(
          b -> {
            PSTGemBonus bonusProvider = b.copy().multiply(tier < 5 ? 1 + tier : 10);
            bonusProviders.add(bonusProvider);
          });
      GEMS.put(id, createGem(bonusProviders));
    }
  }

  private static List<PSTGemBonus> createGemBonuses(
      ItemBonus<?> weaponBonus,
      ItemBonus<?> chestplateBonus,
      ItemBonus<?> helmetBonus,
      ItemBonus<?> bootsBonus,
      ItemBonus<?> shieldBonus,
      ItemBonus<?> jewelryBonus) {
    List<PSTGemBonus> bonuses = new ArrayList<>();
    bonuses.add(new PSTGemBonus(GEM_CLASSES.get("weapon"), weaponBonus));
    bonuses.add(new PSTGemBonus(GEM_CLASSES.get("chestplate"), chestplateBonus));
    bonuses.add(new PSTGemBonus(GEM_CLASSES.get("helmet"), helmetBonus));
    bonuses.add(new PSTGemBonus(GEM_CLASSES.get("boots"), bootsBonus));
    bonuses.add(new PSTGemBonus(GEM_CLASSES.get("shield"), shieldBonus));
    bonuses.add(new PSTGemBonus(GEM_CLASSES.get("jewelry"), jewelryBonus));
    return bonuses;
  }

  private static void createGemsClasses() {
    GEM_CLASSES.put("shield", new GemClass("shield", Set.of(LootCategory.SHIELD)));
    GEM_CLASSES.put("helmet", new GemClass("helmet", Set.of(LootCategory.HELMET)));
    GEM_CLASSES.put("chestplate", new GemClass("chestplate", Set.of(LootCategory.CHESTPLATE)));
    GEM_CLASSES.put("boots", new GemClass("boots", Set.of(LootCategory.BOOTS)));
    GEM_CLASSES.put(
        "jewelry", new GemClass("jewelry", Set.of(getRingCategory(), getNecklaceCategory())));
    GEM_CLASSES.put(
        "weapon",
        new GemClass(
            "weapon",
            Set.of(
                LootCategory.HEAVY_WEAPON,
                LootCategory.SWORD,
                LootCategory.TRIDENT,
                LootCategory.BOW,
                LootCategory.CROSSBOW)));
  }

  public static Map<ResourceLocation, Gem> getGems() {
    return GEMS;
  }

  private static LootCategory getNecklaceCategory() {
    return LootCategory.byId("curios:necklace");
  }

  private static LootCategory getRingCategory() {
    return LootCategory.byId("curios:ring");
  }

  private static Gem createGem(List<GemBonus> gemBonuses) {
    ResourceLocation fakeDimension = new ResourceLocation(SkillTreeMod.MOD_ID, "fake_dimension");
    return new Gem(
        0,
        0,
        Set.of(fakeDimension),
        Optional.of(LootRarity.EPIC),
        Optional.of(LootRarity.EPIC),
        gemBonuses,
        false,
        Optional.empty());
  }
}
