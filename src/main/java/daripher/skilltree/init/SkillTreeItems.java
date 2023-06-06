package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.*;
import daripher.skilltree.item.gem.LightGemstoneItem;
import daripher.skilltree.item.gem.RainbowGemstoneItem;
import daripher.skilltree.item.gem.SoothingGemstoneItem;
import daripher.skilltree.item.gem.SturdyGemstoneItem;
import daripher.skilltree.item.gem.VoidGemstoneItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SkillTreeItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SkillTreeMod.MOD_ID);

	public static final RegistryObject<Item> SOOTHING_GEMSTONE = REGISTRY.register("soothing_gemstone", SoothingGemstoneItem::new);
	public static final RegistryObject<Item> STURDY_GEMSTONE = REGISTRY.register("sturdy_gemstone", SturdyGemstoneItem::new);
	public static final RegistryObject<Item> LIGHT_GEMSTONE = REGISTRY.register("light_gemstone", LightGemstoneItem::new);
	public static final RegistryObject<Item> VOID_GEMSTONE = REGISTRY.register("void_gemstone", VoidGemstoneItem::new);
	public static final RegistryObject<Item> RAINBOW_GEMSTONE = REGISTRY.register("rainbow_gemstone", RainbowGemstoneItem::new);
	public static final RegistryObject<Item> WISDOM_SCROLL = REGISTRY.register("wisdom_scroll", WisdomScrollItem::new);
}
