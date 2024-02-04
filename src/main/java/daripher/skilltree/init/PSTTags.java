package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class PSTTags {
  public static final TagKey<Item> GEMS =
      ItemTags.create(new ResourceLocation(SkillTreeMod.MOD_ID, "gems"));
  public static final TagKey<Item> RINGS = ItemTags.create(new ResourceLocation("curios", "ring"));
  public static final TagKey<Item> NECKLACES =
      ItemTags.create(new ResourceLocation("curios", "necklace"));
  public static final TagKey<Item> QUIVERS =
      ItemTags.create(new ResourceLocation("curios", "quiver"));
  public static final TagKey<Item> JEWELRY =
      ItemTags.create(new ResourceLocation("forge", "curios/jewelry"));
  public static final TagKey<Item> NUGGETS_COPPER =
      ItemTags.create(new ResourceLocation("forge", "nuggets/copper"));
  public static final TagKey<Item> WEAPONS =
      ItemTags.create(new ResourceLocation("forge", "weapons"));
  public static final TagKey<Item> MELEE_WEAPONS =
      ItemTags.create(new ResourceLocation("forge", "weapons/melee"));
  public static final TagKey<Item> RANGED_WEAPONS =
      ItemTags.create(new ResourceLocation("forge", "weapons/ranged"));
  public static final TagKey<Item> EQUIPMENT =
      ItemTags.create(new ResourceLocation("forge", "equipment"));
}
