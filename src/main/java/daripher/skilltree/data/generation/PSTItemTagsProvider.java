package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.init.PSTTags;
import daripher.skilltree.item.gem.GemItem;
import daripher.skilltree.item.necklace.NecklaceItem;
import daripher.skilltree.item.quiver.QuiverItem;
import daripher.skilltree.item.ring.RingItem;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PSTItemTagsProvider extends ItemTagsProvider {
  public PSTItemTagsProvider(
      DataGenerator dataGenerator,
      CompletableFuture<HolderLookup.Provider> provider,
      BlockTagsProvider blockTagsProvider,
      @Nullable ExistingFileHelper fileHelper) {
    super(
        dataGenerator.getPackOutput(),
        provider,
        blockTagsProvider.contentsGetter(),
        SkillTreeMod.MOD_ID,
        fileHelper);
  }

  @Override
  protected void addTags(HolderLookup.@NotNull Provider provider) {
    add(PSTTags.GEMS, GemItem.class);
    add(PSTTags.RINGS, RingItem.class);
    add(PSTTags.NECKLACES, NecklaceItem.class);
    add(PSTTags.QUIVERS, QuiverItem.class);
    add(PSTTags.NUGGETS_COPPER, PSTItems.COPPER_NUGGET.get());
    tag(PSTTags.JEWELRY).addTags(PSTTags.RINGS, PSTTags.NECKLACES);
    tag(PSTTags.MELEE_WEAPONS).addTags(ItemTags.SWORDS, ItemTags.AXES, Tags.Items.TOOLS_TRIDENTS);
    tag(PSTTags.RANGED_WEAPONS).addTags(Tags.Items.TOOLS_BOWS, Tags.Items.TOOLS_CROSSBOWS);
    tag(PSTTags.WEAPONS).addTags(PSTTags.MELEE_WEAPONS, PSTTags.RANGED_WEAPONS);
    tag(PSTTags.EQUIPMENT).addTags(PSTTags.WEAPONS, Tags.Items.TOOLS, Tags.Items.ARMORS);
  }

  private void add(TagKey<Item> itemTag, Class<? extends Item> itemClass) {
    ForgeRegistries.ITEMS.getValues().stream()
        .filter(itemClass::isInstance)
        .forEach(tag(itemTag)::add);
  }

  private void add(TagKey<Item> itemTag, Item item) {
    tag(itemTag).add(item);
  }
}
