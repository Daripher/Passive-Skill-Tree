package daripher.skilltree.data.generation.loot;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

public class PSTLootTablesProvider extends LootTableProvider {
  public static final Set<ResourceKey<LootTable>> REQUIRED_TABLES = Set.of();

  public PSTLootTablesProvider(
      DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(generator.getPackOutput(), REQUIRED_TABLES, List.of(createBlockLootProvider()), lookupProvider);
  }

  @NotNull
  private static SubProviderEntry createBlockLootProvider() {
    return new SubProviderEntry(PSTBlockLoot::new, LootContextParamSets.BLOCK);
  }

  @Override
  protected void validate(
      @NotNull net.minecraft.core.WritableRegistry<LootTable> registry,
      @NotNull ValidationContext ctx,
      @NotNull net.minecraft.util.ProblemReporter.Collector problemReporter) {}
}
