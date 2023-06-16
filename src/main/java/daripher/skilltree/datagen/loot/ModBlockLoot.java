package daripher.skilltree.datagen.loot;

import java.util.Map;
import java.util.function.BiConsumer;

import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.SkillTreeItems;
import daripher.skilltree.item.gem.GemItem;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockLoot extends BlockLoot {
	private static final LootItemCondition.Builder NO_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1)))).invert();
	private final Map<ResourceLocation, LootTable.Builder> lootTables = Maps.newHashMap();

	@Override
	protected void addTables() {
		var gemsLootPool = LootPool.lootPool().when(NO_SILK_TOUCH);
		var vacucite = SkillTreeItems.VACUCITE.get();
		SkillTreeItems.REGISTRY.getEntries().stream()
				.map(RegistryObject::get)
				.filter(GemItem.class::isInstance)
				.filter(Predicates.not(vacucite::equals))
				.map(LootItem::lootTableItem)
				.forEach(gemsLootPool::add);
		var rareGemsLootPool = LootPool.lootPool().when(NO_SILK_TOUCH)
				.add(LootItem.lootTableItem(vacucite))
				.setRolls(ConstantValue.exactly(0.2F))
				.setBonusRolls(ConstantValue.exactly(0.1F));
		var lootTable = LootTable.lootTable()
				.withPool(gemsLootPool)
				.withPool(rareGemsLootPool);
		lootTables.put(new ResourceLocation(SkillTreeMod.MOD_ID, "gems"), lootTable);
	}

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
		addTables();
		lootTables.forEach(consumer);
	}
}
