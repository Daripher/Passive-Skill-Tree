package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class PSTCreativeTabs {
  public static final CreativeModeTab SKILLTREE =
      new CreativeModeTab(SkillTreeMod.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
          return new ItemStack(PSTItems.AMNESIA_SCROLL.get());
        }
      };
}
