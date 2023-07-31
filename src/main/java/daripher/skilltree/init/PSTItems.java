package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.AmnesiaScrollItem;
import daripher.skilltree.item.ResourceItem;
import daripher.skilltree.item.WisdomScrollItem;
import daripher.skilltree.item.gem.AdamiteItem;
import daripher.skilltree.item.gem.CitrineItem;
import daripher.skilltree.item.gem.IrisciteItem;
import daripher.skilltree.item.gem.JadeItem;
import daripher.skilltree.item.gem.MoonstoneItem;
import daripher.skilltree.item.gem.OnyxItem;
import daripher.skilltree.item.gem.RubyItem;
import daripher.skilltree.item.gem.SapphireItem;
import daripher.skilltree.item.gem.TourmalineItem;
import daripher.skilltree.item.gem.TurquoiseItem;
import daripher.skilltree.item.gem.VacuciteItem;
import daripher.skilltree.item.necklace.AssassinNecklace;
import daripher.skilltree.item.necklace.HealerNecklace;
import daripher.skilltree.item.necklace.ScholarNecklace;
import daripher.skilltree.item.necklace.SimpleNecklace;
import daripher.skilltree.item.necklace.TravelerNecklace;
import daripher.skilltree.item.quiver.ArmoredQuiverItem;
import daripher.skilltree.item.quiver.BoneQuiverItem;
import daripher.skilltree.item.quiver.DiamondQuiverItem;
import daripher.skilltree.item.quiver.ExplosiveQuiverItem;
import daripher.skilltree.item.quiver.FieryQuiverItem;
import daripher.skilltree.item.quiver.GildedQuiverItem;
import daripher.skilltree.item.quiver.HealingQuiverItem;
import daripher.skilltree.item.quiver.QuiverItem;
import daripher.skilltree.item.quiver.SilentQuiverItem;
import daripher.skilltree.item.quiver.ToxicQuiverItem;
import daripher.skilltree.item.ring.CopperRingItem;
import daripher.skilltree.item.ring.GoldenRingItem;
import daripher.skilltree.item.ring.IronRingItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PSTItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SkillTreeMod.MOD_ID);

	// gems
	public static final RegistryObject<Item> RUBY = REGISTRY.register("ruby", RubyItem::new);
	public static final RegistryObject<Item> ONYX = REGISTRY.register("onyx", OnyxItem::new);
	public static final RegistryObject<Item> MOONSTONE = REGISTRY.register("moonstone", MoonstoneItem::new);
	public static final RegistryObject<Item> VACUCITE = REGISTRY.register("vacucite", VacuciteItem::new);
	public static final RegistryObject<Item> IRISCITE = REGISTRY.register("iriscite", IrisciteItem::new);
	public static final RegistryObject<Item> CITRINE = REGISTRY.register("citrine", CitrineItem::new);
	public static final RegistryObject<Item> ADAMITE = REGISTRY.register("adamite", AdamiteItem::new);
	public static final RegistryObject<Item> JADE = REGISTRY.register("jade", JadeItem::new);
	public static final RegistryObject<Item> SAPPHIRE = REGISTRY.register("sapphire", SapphireItem::new);
	public static final RegistryObject<Item> TOURMALINE = REGISTRY.register("tourmaline", TourmalineItem::new);
	public static final RegistryObject<Item> TURQUOISE = REGISTRY.register("turquoise", TurquoiseItem::new);
	// scrolls
	public static final RegistryObject<Item> WISDOM_SCROLL = REGISTRY.register("wisdom_scroll", WisdomScrollItem::new);
	public static final RegistryObject<Item> AMNESIA_SCROLL = REGISTRY.register("amnesia_scroll", AmnesiaScrollItem::new);
	// rings
	public static final RegistryObject<Item> COPPER_RING = REGISTRY.register("copper_ring", CopperRingItem::new);
	public static final RegistryObject<Item> IRON_RING = REGISTRY.register("iron_ring", IronRingItem::new);
	public static final RegistryObject<Item> GOLDEN_RING = REGISTRY.register("golden_ring", GoldenRingItem::new);
	// necklaces
	public static final RegistryObject<Item> ASSASSIN_NECKLACE = REGISTRY.register("assassin_necklace", AssassinNecklace::new);
	public static final RegistryObject<Item> TRAVELER_NECKLACE = REGISTRY.register("traveler_necklace", TravelerNecklace::new);
	public static final RegistryObject<Item> HEALER_NECKLACE = REGISTRY.register("healer_necklace", HealerNecklace::new);
	public static final RegistryObject<Item> SIMPLE_NECKLACE = REGISTRY.register("simple_necklace", SimpleNecklace::new);
	public static final RegistryObject<Item> SCHOLAR_NECKLACE = REGISTRY.register("scholar_necklace", ScholarNecklace::new);
	// quivers
	public static final RegistryObject<Item> QUIVER = REGISTRY.register("quiver", QuiverItem::new);
	public static final RegistryObject<Item> FIERY_QUIVER = REGISTRY.register("fiery_quiver", FieryQuiverItem::new);
	public static final RegistryObject<Item> ARMORED_QUIVER = REGISTRY.register("armored_quiver", ArmoredQuiverItem::new);
	public static final RegistryObject<Item> GILDED_QUIVER = REGISTRY.register("gilded_quiver", GildedQuiverItem::new);
	public static final RegistryObject<Item> TOXIC_QUIVER = REGISTRY.register("toxic_quiver", ToxicQuiverItem::new);
	public static final RegistryObject<Item> DIAMOND_QUIVER = REGISTRY.register("diamond_quiver", DiamondQuiverItem::new);
	public static final RegistryObject<Item> HEALING_QUIVER = REGISTRY.register("healing_quiver", HealingQuiverItem::new);
	public static final RegistryObject<Item> EXPLOSIVE_QUIVER = REGISTRY.register("explosive_quiver", ExplosiveQuiverItem::new);
	public static final RegistryObject<Item> SILENT_QUIVER = REGISTRY.register("silent_quiver", SilentQuiverItem::new);
	public static final RegistryObject<Item> BONE_QUIVER = REGISTRY.register("bone_quiver", BoneQuiverItem::new);
	// resources
	public static final RegistryObject<Item> COPPER_NUGGET = REGISTRY.register("copper_nugget", ResourceItem::new);
}
