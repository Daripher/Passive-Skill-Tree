package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.loot.modifier.AddItemModifier;
import daripher.skilltree.loot.modifier.SkillBonusesModifier;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class PSTGlobalLootModifierProvider extends GlobalLootModifierProvider {
  public PSTGlobalLootModifierProvider(DataGenerator generator) {
    super(generator.getPackOutput(), SkillTreeMod.MOD_ID);
  }

  @Override
  protected void start() {
    upgradeMaterial("entities/zombified_piglin", PSTItems.ANCIENT_ALLOY_GILDED);
    upgradeMaterial("entities/phantom", PSTItems.ANCIENT_ALLOY_LIGHTWEIGHT);
    upgradeMaterial("entities/creeper", PSTItems.ANCIENT_ALLOY_CURATIVE);
    upgradeMaterial("entities/spider", PSTItems.ANCIENT_ALLOY_TOXIC);
    upgradeMaterial("entities/wither_skeleton", PSTItems.ANCIENT_ALLOY_ENCHANTED);
    upgradeMaterial("entities/enderman", PSTItems.ANCIENT_ALLOY_SPATIAL);
    upgradeMaterial("entities/pillager", PSTItems.ANCIENT_ALLOY_DURABLE);
    upgradeMaterial("entities/blaze", PSTItems.ANCIENT_ALLOY_HOT);
    upgradeMaterial("entities/stray", PSTItems.ANCIENT_BOOK);
    add("skill_bonuses", new SkillBonusesModifier());
  }

  private void upgradeMaterial(String lootTablePath, RegistryObject<Item> item) {
    addItem(lootTablePath, item, 0.05f, 0.05f);
  }

  private void addItem(String lootTablePath, RegistryObject<Item> item, float chance, float lootingMultiplier) {
    ResourceLocation itemId = item.getId();
    String modifierName = lootTablePath.replaceAll("/", "_") + "_" + itemId.getPath();
    ItemStack itemStack = new ItemStack(item.get());
    AddItemModifier modifier = new AddItemModifier(itemStack, lootTableCondition(lootTablePath), randomChanceCondition(chance, lootingMultiplier));
    add(modifierName, modifier);
  }

  private static LootItemCondition lootTableCondition(String lootTablePath) {
    ResourceLocation lootTableId = new ResourceLocation(lootTablePath);
    LootTableIdCondition.Builder builder = LootTableIdCondition.builder(lootTableId);
    return builder.build();
  }

  @NotNull
  private static LootItemCondition randomChanceCondition(float chance, float lootingMultiplier) {
    LootItemCondition.Builder builder = LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(chance, lootingMultiplier);
    return builder.build();
  }
}
