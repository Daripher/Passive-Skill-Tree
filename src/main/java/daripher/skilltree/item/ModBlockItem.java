package daripher.skilltree.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockItem extends BlockItem {
    public ModBlockItem(DeferredHolder<Block, ? extends Block> blockRegistryObject) {
        super(blockRegistryObject.get(), new Properties());
    }
}
