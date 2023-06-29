package daripher.skilltree.datagen.loot;

import java.util.Map;
import java.util.function.BiConsumer;

import com.google.common.collect.Maps;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeItems;
import daripher.skilltree.item.gem.GemItem;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockLoot extends BlockLoot {
	private final Map<ResourceLocation, LootTable.Builder> lootTables = Maps.newHashMap();

	@Override
	protected void addTables() {
		lootTables.put(new ResourceLocation(SkillTreeMod.MOD_ID, "gems"), gemsLootTable());
	}

	protected LootTable.Builder gemsLootTable() {
		LootPool.Builder gems = LootPool.lootPool();
		SkillTreeItems.REGISTRY.getEntries().stream()
				.map(RegistryObject::get)
				.filter(GemItem.class::isInstance)
				.map(this::gemLootItem)
				.forEach(gems::add);
		return LootTable.lootTable().withPool(gems);
	}

	protected LootPoolSingletonContainer.Builder<?> gemLootItem(Item item) {
		LootPoolSingletonContainer.Builder<?> lootItem = LootItem.lootTableItem(item);
		if (item == SkillTreeItems.VACUCITE.get()) {
			lootItem.setQuality(1);
		} else {
			lootItem.setWeight(3);
		}
		return lootItem;
	}

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
		addTables();
		lootTables.forEach(consumer);
	}
}
