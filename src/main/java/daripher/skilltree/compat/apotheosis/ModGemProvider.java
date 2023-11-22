package daripher.skilltree.compat.apotheosis;

import com.mojang.serialization.JsonOps;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.item.gem.SimpleGemItem;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import shadows.apotheosis.adventure.affix.socket.gem.Gem;
import shadows.apotheosis.adventure.affix.socket.gem.GemClass;
import shadows.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import shadows.apotheosis.adventure.loot.LootCategory;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.util.StepFunction;

public class ModGemProvider extends JsonCodecProvider<Gem> {
  public ModGemProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
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
    shadows.apotheosis.adventure.affix.socket.gem.bonus.AttributeBonus.initCodecs();
    Map<String, GemClass> gemClasses = createGemsClasses();
    HashMap<ResourceLocation, Gem> gems = new HashMap<>();
    getSkillTreeGems()
        .forEach(
            gem ->
                gem.getBonuses()
                    .forEach((type, bonus) -> createApothGem(gem, type, bonus, gemClasses, gems)));
    return gems;
  }

  private static void createApothGem(
      SimpleGemItem gem,
      String type,
      ItemBonus<?> bonus,
      Map<String, GemClass> gemClasses,
      HashMap<ResourceLocation, Gem> gems) {
    if (!(bonus instanceof ItemSkillBonus skillBonus)) return;
    if (!(skillBonus.bonus() instanceof AttributeBonus attributeBonus)) return;
    List<GemBonus> bonuses = new ArrayList<>();
    GemClass gemClass = gemClasses.get(type);
    if (gemClass == null) return;
    Map<LootRarity, StepFunction> func =
        generateBonuses((float) attributeBonus.getModifier().getAmount());
    shadows.apotheosis.adventure.affix.socket.gem.bonus.AttributeBonus apothBonus =
        new shadows.apotheosis.adventure.affix.socket.gem.bonus.AttributeBonus(
            gemClass,
            attributeBonus.getAttribute(),
            attributeBonus.getModifier().getOperation(),
            func);
    bonuses.add(apothBonus);
    ResourceLocation id = ForgeRegistries.ITEMS.getKey(gem);
    gems.put(id, createGem(bonuses));
  }

  private static Map<String, GemClass> createGemsClasses() {
    Map<String, GemClass> classes = new HashMap<>();
    classes.put("pickaxe", new GemClass("pickaxe", Set.of(LootCategory.PICKAXE)));
    classes.put(
        "melee_weapon",
        new GemClass(
            "melee_weapon",
            Set.of(LootCategory.HEAVY_WEAPON, LootCategory.SWORD, LootCategory.TRIDENT)));
    classes.put("shield", new GemClass("shield", Set.of(LootCategory.SHIELD)));
    classes.put("helmet", new GemClass("helmet", Set.of(LootCategory.HELMET)));
    classes.put("chestplate", new GemClass("chestplate", Set.of(LootCategory.CHESTPLATE)));
    classes.put("leggings", new GemClass("leggings", Set.of(LootCategory.LEGGINGS)));
    classes.put("boots", new GemClass("boots", Set.of(LootCategory.BOOTS)));
    classes.put(
        "ranged_weapon",
        new GemClass("ranged_weapon", Set.of(LootCategory.BOW, LootCategory.CROSSBOW)));
    classes.put("ring", new GemClass("ring", Set.of(ApotheosisCompatibility.INSTANCE.ring)));
    classes.put(
        "necklace", new GemClass("necklace", Set.of(ApotheosisCompatibility.INSTANCE.necklace)));
    classes.put(
        "jewelry",
        new GemClass(
            "jewelry",
            Set.of(
                ApotheosisCompatibility.INSTANCE.necklace, ApotheosisCompatibility.INSTANCE.ring)));
    classes.put(
        "weapon",
        new GemClass(
            "weapon",
            Set.of(
                LootCategory.HEAVY_WEAPON,
                LootCategory.SWORD,
                LootCategory.TRIDENT,
                LootCategory.BOW,
                LootCategory.CROSSBOW)));
    return classes;
  }

  private static Stream<SimpleGemItem> getSkillTreeGems() {
    return PSTItems.REGISTRY.getEntries().stream()
        .map(RegistryObject::get)
        .filter(SimpleGemItem.class::isInstance)
        .map(SimpleGemItem.class::cast);
  }

  private static Map<LootRarity, StepFunction> generateBonuses(float min) {
    HashMap<LootRarity, StepFunction> bonuses = new HashMap<>();
    LootRarity.values()
        .forEach(
            rarity -> {
              StepFunction function = new StepFunction(min, 1, 0);
              bonuses.put(rarity, function);
            });
    return bonuses;
  }

  private static Gem createGem(List<GemBonus> gemBonuses) {
    return createGem(1, 0F, gemBonuses);
  }

  private static Gem createGem(int weight, float quality, List<GemBonus> gemBonuses) {
    Set<ResourceLocation> dimensions =
        Set.of(new ResourceLocation(SkillTreeMod.MOD_ID, "fake_dimension"));
    return new Gem(
        weight,
        quality,
        dimensions,
        Optional.of(LootRarity.EPIC),
        Optional.of(LootRarity.EPIC),
        gemBonuses,
        false,
        Optional.empty());
  }
}
