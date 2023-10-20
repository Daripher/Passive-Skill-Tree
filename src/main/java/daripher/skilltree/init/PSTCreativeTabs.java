package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PSTCreativeTabs {
  public static final CreativeModeTab SKILLTREE =
      new CreativeModeTab(SkillTreeMod.MOD_ID) {
        @Override
        public @NotNull ItemStack makeIcon() {
          return new ItemStack(PSTItems.AMNESIA_SCROLL.get());
        }
      };
}
