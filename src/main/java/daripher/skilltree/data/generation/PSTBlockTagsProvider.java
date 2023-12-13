package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class PSTBlockTagsProvider extends BlockTagsProvider {
  public PSTBlockTagsProvider(
      DataGenerator dataGenerator,
      CompletableFuture<HolderLookup.Provider> provider,
      ExistingFileHelper fileHelper) {
    super(dataGenerator.getPackOutput(), provider, SkillTreeMod.MOD_ID, fileHelper);
  }

  @Override
  protected void addTags(@NotNull HolderLookup.Provider provider) {}
}
