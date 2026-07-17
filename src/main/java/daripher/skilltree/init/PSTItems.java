package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.AmnesiaScrollItem;
import daripher.skilltree.item.ModBlockItem;
import daripher.skilltree.item.WisdomScrollItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import daripher.skilltree.util.ForgeRegistries;

public class PSTItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SkillTreeMod.MOD_ID);

    // scrolls
    public static final DeferredHolder<Item, ? extends Item> WISDOM_SCROLL = REGISTRY.register("wisdom_scroll", WisdomScrollItem::new);
    public static final DeferredHolder<Item, ? extends Item> AMNESIA_SCROLL = REGISTRY.register("amnesia_scroll", AmnesiaScrollItem::new);

    // blocks
    public static final DeferredHolder<Item, ? extends Item> WORKBENCH = REGISTRY.register("workbench", () -> new ModBlockItem(PSTBlocks.WORKBENCH));
}
