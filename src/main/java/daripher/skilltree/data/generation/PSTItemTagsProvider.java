package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
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
  public static final ResourceLocation KNIVES = new ResourceLocation("forge", "tools/knives");

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
    tag(PSTTags.Items.JEWELRY).addTags(PSTTags.Items.RINGS, PSTTags.Items.NECKLACES);
    tag(Tags.Items.TOOLS).addOptionalTag(KNIVES);
    tag(PSTTags.Items.MELEE_WEAPON)
        .addTags(ItemTags.SWORDS, ItemTags.AXES, Tags.Items.TOOLS_TRIDENTS);
    tag(PSTTags.Items.RANGED_WEAPON).addTags(Tags.Items.TOOLS_BOWS, Tags.Items.TOOLS_CROSSBOWS);
  }

  private void add(TagKey<Item> itemTag, Class<? extends Item> itemClass) {
    ForgeRegistries.ITEMS.getValues().stream()
        .filter(itemClass::isInstance)
        .forEach(tag(itemTag)::add);
  }
}
