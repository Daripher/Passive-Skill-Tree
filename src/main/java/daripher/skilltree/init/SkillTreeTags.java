package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class SkillTreeTags {
	public static final TagKey<Item> GEMSTONES = ItemTags.create(new ResourceLocation(SkillTreeMod.MOD_ID, "gemstones"));
}
