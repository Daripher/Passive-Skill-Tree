package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.*;
import daripher.skilltree.item.gem.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SkillTreeItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SkillTreeMod.MOD_ID);

	public static final RegistryObject<Item> RUBY = REGISTRY.register("ruby", RubyItem::new);
	public static final RegistryObject<Item> ONYX = REGISTRY.register("onyx", OnyxItem::new);
	public static final RegistryObject<Item> MOONSTONE = REGISTRY.register("moonstone", MoonstoneItem::new);
	public static final RegistryObject<Item> VACUCITE = REGISTRY.register("vacucite", VacuciteItem::new);
	public static final RegistryObject<Item> IRISCITE = REGISTRY.register("iriscite", IrisciteItem::new);
	public static final RegistryObject<Item> OPAL = REGISTRY.register("opal", OpalItem::new);
	public static final RegistryObject<Item> CITRINE = REGISTRY.register("citrine", CitrineItem::new);
	public static final RegistryObject<Item> ADAMITE = REGISTRY.register("adamite", AdamiteItem::new);
	public static final RegistryObject<Item> WISDOM_SCROLL = REGISTRY.register("wisdom_scroll", WisdomScrollItem::new);
	public static final RegistryObject<Item> AMNESIA_SCROLL = REGISTRY.register("amnesia_scroll", AmnesiaScrollItem::new);
}
