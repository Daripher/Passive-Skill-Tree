package daripher.skilltree.datagen;

import java.util.List;
import java.util.Map;
import java.util.Set;

import daripher.skilltree.datagen.loot.PSTBlockLoot;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class PSTLootTablesProvider extends LootTableProvider {
	public PSTLootTablesProvider(DataGenerator generator) {
		super(generator.getPackOutput(), Set.of(), List.of(new SubProviderEntry(PSTBlockLoot::new, LootContextParamSets.BLOCK)));
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
	}
}
