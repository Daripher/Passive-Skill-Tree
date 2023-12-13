package daripher.skilltree.data.generation.loot;

import com.google.common.collect.Maps;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.item.gem.GemItem;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class PSTBlockLoot implements LootTableSubProvider {
  private final Map<ResourceLocation, LootTable.Builder> lootTables = Maps.newHashMap();

  @Override
  public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
    lootTables.put(new ResourceLocation(SkillTreeMod.MOD_ID, "gems"), gemsLootTable());
    lootTables.forEach(consumer);
  }

  protected LootTable.Builder gemsLootTable() {
    LootPool.Builder gems = LootPool.lootPool();
    PSTItems.REGISTRY.getEntries().stream()
        .map(RegistryObject::get)
        .filter(GemItem.class::isInstance)
        .map(this::gemLootItem)
        .forEach(gems::add);
    return LootTable.lootTable().withPool(gems);
  }

  protected LootPoolSingletonContainer.Builder<?> gemLootItem(Item item) {
    LootPoolSingletonContainer.Builder<?> lootItem = LootItem.lootTableItem(item);
    if (item == PSTItems.VACUCITE.get() || item == PSTItems.IRISCITE.get()) {
      lootItem.setQuality(1);
    } else {
      lootItem.setWeight(3);
    }
    return lootItem;
  }
}
