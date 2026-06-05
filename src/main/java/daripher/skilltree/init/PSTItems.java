package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.AmnesiaScrollItem;
import daripher.skilltree.item.ModBlockItem;
import daripher.skilltree.item.WisdomScrollItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PSTItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SkillTreeMod.MOD_ID);

    // scrolls
    public static final RegistryObject<Item> WISDOM_SCROLL = REGISTRY.register("wisdom_scroll", WisdomScrollItem::new);
    public static final RegistryObject<Item> AMNESIA_SCROLL = REGISTRY.register("amnesia_scroll", AmnesiaScrollItem::new);

    // blocks
    public static final RegistryObject<Item> WORKBENCH = REGISTRY.register("workbench", () -> new ModBlockItem(PSTBlocks.WORKBENCH));
}
