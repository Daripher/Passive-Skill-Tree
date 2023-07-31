package daripher.skilltree.datagen.loot;

import java.util.Map;
import java.util.function.BiConsumer;

import com.google.common.collect.Maps;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.item.gem.GemItem;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraftforge.registries.RegistryObject;

public class PSTBlockLoot extends BlockLoot {
	private final Map<ResourceLocation, LootTable.Builder> lootTables = Maps.newHashMap();

	@Override
	protected void addTables() {
		lootTables.put(new ResourceLocation(SkillTreeMod.MOD_ID, "gems"), gemsLootTable());
	}

	protected LootTable.Builder gemsLootTable() {
		LootPool.Builder gems = LootPool.lootPool();
		// formatter:off
		PSTItems.REGISTRY.getEntries().stream()
			.map(RegistryObject::get)
			.filter(GemItem.class::isInstance)
			.map(this::gemLootItem)
			.forEach(gems::add);
		// formatter:on
		return LootTable.lootTable().withPool(gems);
	}

	protected LootPoolSingletonContainer.Builder<?> gemLootItem(Item item) {
		LootPoolSingletonContainer.Builder<?> lootItem = LootItem.lootTableItem(item);
		if (item == PSTItems.VACUCITE.get()) lootItem.setQuality(1);
		else if (item == PSTItems.IRISCITE.get()) lootItem.setQuality(1);
		else lootItem.setWeight(3);
		return lootItem;
	}

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
		addTables();
		lootTables.forEach(consumer::accept);
	}
}
