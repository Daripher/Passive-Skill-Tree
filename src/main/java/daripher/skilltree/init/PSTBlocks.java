package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.block.WorkbenchBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import daripher.skilltree.util.ForgeRegistries;

public class PSTBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, SkillTreeMod.MOD_ID);

    // crafting stations
    public static final DeferredHolder<Block, ? extends Block> WORKBENCH = REGISTRY.register("workbench", WorkbenchBlock::new);
}
