package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTDamageTypes;
import daripher.skilltree.init.PSTTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PSTDamageTagsProvider extends DamageTypeTagsProvider {
  public PSTDamageTagsProvider(
      DataGenerator dataGenerator,
      CompletableFuture<HolderLookup.Provider> provider,
      @Nullable ExistingFileHelper fileHelper) {
    super(dataGenerator.getPackOutput(), provider, SkillTreeMod.MOD_ID, fileHelper);
  }

  @Override
  protected void addTags(HolderLookup.@NotNull Provider provider) {
    add(PSTTags.DamageTypes.IS_MAGIC, DamageTypes.MAGIC, DamageTypes.INDIRECT_MAGIC, PSTDamageTypes.POISON);
  }

  @SafeVarargs
  private void add(TagKey<DamageType> damageTag, ResourceKey<DamageType>... types) {
    for (ResourceKey<DamageType> type : types) {
      tag(damageTag).add(type);
    }
  }
}
