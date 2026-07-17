package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PSTItemTagsProvider extends ItemTagsProvider {
    public static final ResourceLocation KNIVES = ResourceLocation.fromNamespaceAndPath("c", "tools/knives");

    public PSTItemTagsProvider(DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> provider, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper fileHelper) {
        super(dataGenerator.getPackOutput(), provider, blockTagsProvider.contentsGetter(), SkillTreeMod.MOD_ID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(Tags.Items.TOOLS).addOptionalTag(KNIVES);
        tag(PSTTags.Items.MELEE_WEAPON)
                .addTags(ItemTags.SWORDS, ItemTags.AXES, Tags.Items.TOOLS_SPEAR);
        tag(PSTTags.Items.RANGED_WEAPON)
                .addTags(Tags.Items.TOOLS_BOW, Tags.Items.TOOLS_CROSSBOW);
        tag(PSTTags.Items.LEATHER_ARMOR).add(Items.LEATHER_BOOTS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_LEGGINGS);
    }
}
