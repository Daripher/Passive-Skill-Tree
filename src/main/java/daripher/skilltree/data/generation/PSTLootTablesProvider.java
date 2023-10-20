package daripher.skilltree.data.generation;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import daripher.skilltree.data.generation.loot.PSTBlockLoot;
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

public class PSTLootTablesProvider extends LootTableProvider {
  public PSTLootTablesProvider(DataGenerator dataGenerator) {
    super(dataGenerator);
  }

  @Override
  protected List<
          Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>>
      getTables() {
    return ImmutableList.of(Pair.of(PSTBlockLoot::new, LootContextParamSets.BLOCK));
  }

  @Override
  protected void validate(
      Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {}
}
