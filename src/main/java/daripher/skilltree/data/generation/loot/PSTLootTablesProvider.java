package daripher.skilltree.data.generation.loot;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import daripher.skilltree.data.generation.PSTGemTypesProvider;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

public class PSTLootTablesProvider extends LootTableProvider {
  private final PSTGemTypesProvider gemTypesProvider;

  public PSTLootTablesProvider(DataGenerator dataGenerator, PSTGemTypesProvider gemTypesProvider) {
    super(dataGenerator);
    this.gemTypesProvider = gemTypesProvider;
  }

  @Override
  protected @NotNull List<
          Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>>
      getTables() {
    return ImmutableList.of(
        Pair.of(() -> new PSTBlockLoot(gemTypesProvider), LootContextParamSets.BLOCK));
  }

  @Override
  protected void validate(
      @NotNull Map<ResourceLocation, LootTable> map,
      @NotNull ValidationContext validationtracker) {}
}
