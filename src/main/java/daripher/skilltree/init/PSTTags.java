package daripher.skilltree.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

public class PSTTags {
    public static class DamageTypes {
        public static final TagKey<DamageType> IS_MAGIC = Tags.DamageTypes.IS_MAGIC;
    }

    public static class Items {
        public static final TagKey<Item> RINGS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "ring"));
        public static final TagKey<Item> NECKLACES = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "necklace"));
        public static final TagKey<Item> JEWELRY = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "jewelry"));
        public static final TagKey<Item> MELEE_WEAPON = ItemTags.create(ResourceLocation.fromNamespaceAndPath("skilltree", "melee_weapon"));
        public static final TagKey<Item> RANGED_WEAPON = ItemTags.create(ResourceLocation.fromNamespaceAndPath("skilltree", "ranged_weapon"));
        public static final TagKey<Item> LEATHER_ARMOR = ItemTags.create(ResourceLocation.fromNamespaceAndPath("skilltree", "armors/leather"));
    }
}
