package daripher.skilltree.data.generation.loot;

import daripher.skilltree.init.PSTBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class PSTBlockLoot extends BlockLootSubProvider {
    protected PSTBlockLoot() {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(PSTBlocks.WORKBENCH.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return PSTBlocks.REGISTRY.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
