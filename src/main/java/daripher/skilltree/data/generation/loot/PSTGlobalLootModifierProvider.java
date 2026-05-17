package daripher.skilltree.data.generation.loot;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.loot.modifier.SkillBonusesModifier;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

public class PSTGlobalLootModifierProvider extends GlobalLootModifierProvider {
  public PSTGlobalLootModifierProvider(
      DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(generator.getPackOutput(), lookupProvider, SkillTreeMod.MOD_ID);
  }

  @Override
  protected void start() {
    add("skill_bonuses", new SkillBonusesModifier());
  }
}
