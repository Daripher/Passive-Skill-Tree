package daripher.skilltree.data.generation.loot;

import java.util.function.BiConsumer;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public class PSTBlockLoot implements LootTableSubProvider {
  public PSTBlockLoot() {}

  @Override
  public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> consumer) {}
}
