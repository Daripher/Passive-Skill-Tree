package daripher.skilltree.data.generation.loot;

import com.google.common.collect.Maps;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.generation.PSTGemTypesProvider;
import daripher.skilltree.item.gem.GemLootPoolEntry;
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
    lootTables.forEach(consumer);
  }

  protected LootTable.Builder gemsLootTable() {
    LootPool.Builder lootPool = LootPool.lootPool();
    gemTypesProvider
        .getGemTypes()
        .values()
        .forEach(
            gemType -> {
              String gemId = gemType.id().getPath();
              int tier = Integer.parseInt(gemId.substring(gemId.length() - 1));
              if (tier < 4) {
                int weight =
                    switch (tier) {
                      case 0 -> 1000;
                      case 1 -> 50;
                      case 2 -> 10;
                      case 3 -> 1;
                      default -> 0;
                    };
                if (gemId.contains("vacucite")) {
                  weight = 100;
                }
                int quality = tier > 2 ? 1 : 0;
                lootPool.add(
                    new GemLootPoolEntry.Builder(gemType.id())
                        .setWeight(weight)
                        .setQuality(quality));
              }
            });
    return LootTable.lootTable().withPool(lootPool);
  }
}
