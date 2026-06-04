package daripher.skilltree.data.generation.loot;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PSTLootTablesProvider extends LootTableProvider {
  public static final Set<ResourceLocation> REQUIRED_TABLES = Set.of();

  public PSTLootTablesProvider(DataGenerator generator) {
    super(generator.getPackOutput(), REQUIRED_TABLES, List.of(createBlockLootProvider()));
  }

  @NotNull
  private static SubProviderEntry createBlockLootProvider() {
    return new SubProviderEntry(PSTBlockLoot::new, LootContextParamSets.BLOCK);
  }

  @Override
  protected void validate(
      @NotNull Map<ResourceLocation, LootTable> map, @NotNull ValidationContext ctx) {}
}
