package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.loot.modifier.SkillBonusesModifier;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class PSTGlobalLootModifierProvider extends GlobalLootModifierProvider {
  public PSTGlobalLootModifierProvider(DataGenerator generator) {
    super(generator.getPackOutput(), SkillTreeMod.MOD_ID);
  }

  @Override
  protected void start() {
    add("skill_bonuses", new SkillBonusesModifier());
  }
}
