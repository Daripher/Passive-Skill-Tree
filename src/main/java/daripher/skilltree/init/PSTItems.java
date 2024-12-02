package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.AmnesiaScrollItem;
import daripher.skilltree.item.ResourceItem;
import daripher.skilltree.item.UpgradeMaterialItem;
import daripher.skilltree.item.WisdomScrollItem;
import daripher.skilltree.item.gem.GemItem;
import daripher.skilltree.item.necklace.*;
import daripher.skilltree.item.ring.CopperRingItem;
import daripher.skilltree.item.ring.GoldenRingItem;
import daripher.skilltree.item.ring.IronRingItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PSTItems {
  public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SkillTreeMod.MOD_ID);

  public static final RegistryObject<Item> GEM = REGISTRY.register("gem", GemItem::new);
  // scrolls
  public static final RegistryObject<Item> WISDOM_SCROLL = REGISTRY.register("wisdom_scroll", WisdomScrollItem::new);
  public static final RegistryObject<Item> AMNESIA_SCROLL = REGISTRY.register("amnesia_scroll", AmnesiaScrollItem::new);
  // rings
  public static final RegistryObject<Item> COPPER_RING = REGISTRY.register("copper_ring", CopperRingItem::new);
  public static final RegistryObject<Item> IRON_RING = REGISTRY.register("iron_ring", IronRingItem::new);
  public static final RegistryObject<Item> GOLDEN_RING = REGISTRY.register("golden_ring", GoldenRingItem::new);
  // necklaces
  public static final RegistryObject<Item> SIMPLE_NECKLACE = REGISTRY.register("simple_necklace", SimpleNecklace::new);
  public static final RegistryObject<Item> TRAVELER_NECKLACE = REGISTRY.register("traveler_necklace", TravelerNecklace::new);
  public static final RegistryObject<Item> FISHERMAN_NECKLACE = REGISTRY.register("fisherman_necklace", FishermanNecklace::new);
  public static final RegistryObject<Item> ASSASSIN_NECKLACE = REGISTRY.register("assassin_necklace", AssassinNecklace::new);
  public static final RegistryObject<Item> HEALER_NECKLACE = REGISTRY.register("healer_necklace", HealerNecklace::new);
  public static final RegistryObject<Item> SCHOLAR_NECKLACE = REGISTRY.register("scholar_necklace", ScholarNecklace::new);
  public static final RegistryObject<Item> ARSONIST_NECKLACE = REGISTRY.register("arsonist_necklace", ArsonistNecklace::new);
  // resources
  public static final RegistryObject<Item> COPPER_NUGGET = REGISTRY.register("copper_nugget", ResourceItem::new);
  // upgrade materials
  public static final RegistryObject<Item> ANCIENT_BOOK = REGISTRY.register("ancient_book", UpgradeMaterialItem::new);
  public static final RegistryObject<Item> ANCIENT_ALLOY_GILDED = REGISTRY.register("ancient_alloy_gilded", UpgradeMaterialItem::new);
  public static final RegistryObject<Item> ANCIENT_ALLOY_LIGHTWEIGHT = REGISTRY.register("ancient_alloy_lightweight", UpgradeMaterialItem::new);
  public static final RegistryObject<Item> ANCIENT_ALLOY_CURATIVE = REGISTRY.register("ancient_alloy_curative", UpgradeMaterialItem::new);
  public static final RegistryObject<Item> ANCIENT_ALLOY_TOXIC = REGISTRY.register("ancient_alloy_toxic", UpgradeMaterialItem::new);
  public static final RegistryObject<Item> ANCIENT_ALLOY_ENCHANTED = REGISTRY.register("ancient_alloy_enchanted", UpgradeMaterialItem::new);
  public static final RegistryObject<Item> ANCIENT_ALLOY_SPATIAL = REGISTRY.register("ancient_alloy_spatial", UpgradeMaterialItem::new);
  public static final RegistryObject<Item> ANCIENT_ALLOY_DURABLE = REGISTRY.register("ancient_alloy_durable", UpgradeMaterialItem::new);
  public static final RegistryObject<Item> ANCIENT_ALLOY_HOT = REGISTRY.register("ancient_alloy_hot", UpgradeMaterialItem::new);
}
