package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.gem.GemLootPoolEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraftforge.registries.DeferredRegister;

public class PSTLootPoolEntries {
  public static final DeferredRegister<LootPoolEntryType> REGISTRY =
      DeferredRegister.create(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.key(), SkillTreeMod.MOD_ID);

  static {
    REGISTRY.register("gem", () -> GemLootPoolEntry.TYPE);
  }
}
