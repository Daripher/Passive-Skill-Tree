package daripher.skilltree.data.generation.loot;

import com.google.common.collect.Maps;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.generation.PSTGemTypesProvider;
import daripher.skilltree.item.gem.GemType;
import daripher.skilltree.item.gem.loot.GemLootPoolEntry;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public class PSTBlockLoot implements LootTableSubProvider {
  private final Map<ResourceLocation, LootTable.Builder> lootTables = Maps.newHashMap();
  private final PSTGemTypesProvider gemTypesProvider;

  public PSTBlockLoot(PSTGemTypesProvider gemTypesProvider) {
    this.gemTypesProvider = gemTypesProvider;
  }

  @Override
  public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
    lootTables.put(new ResourceLocation(SkillTreeMod.MOD_ID, "gems"), gemsLootTable());
    lootTables.put(
        new ResourceLocation(SkillTreeMod.MOD_ID, "apotheosis_gems"), apotheosisGemsLootTable());
    lootTables.forEach(consumer);
  }

  protected LootTable.Builder gemsLootTable() {
    LootPool.Builder lootPool = LootPool.lootPool();
    gemTypesProvider
        .getGemTypes()
        .values()
        .forEach(gemType -> lootPool.add(new GemLootPoolEntry.Builder(gemType.id())));
    return LootTable.lootTable().withPool(lootPool);
  }

  protected LootTable.Builder apotheosisGemsLootTable() {
    LootPool.Builder lootPool = LootPool.lootPool();
    gemTypesProvider.getGemTypes().values().stream()
        .map(GemType::id)
        .filter(id -> !id.getPath().contains("vacucite") && !id.getPath().contains("iriscite"))
        .forEach(id -> lootPool.add(new GemLootPoolEntry.Builder(id)));
    return LootTable.lootTable().withPool(lootPool);
  }
}
