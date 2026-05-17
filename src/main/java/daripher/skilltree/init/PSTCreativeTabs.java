package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class PSTCreativeTabs {
  public static final DeferredRegister<CreativeModeTab> REGISTRY =
      DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SkillTreeMod.MOD_ID);
  public static final MutableComponent TAB_TITLE = Component.translatable("itemGroup.skilltree");
  public static final Supplier<ItemStack> TAB_ICON_STACK =
      () -> new ItemStack(PSTItems.AMNESIA_SCROLL.get());

  static {
    REGISTRY.register(
        "skilltree",
        () ->
            CreativeModeTab.builder()
                .title(TAB_TITLE)
                .icon(TAB_ICON_STACK)
                .displayItems((params, output) -> collectModItems(output))
                .build());
  }

  private static void collectModItems(CreativeModeTab.Output output) {
    PSTItems.REGISTRY.getEntries().stream().map(DeferredHolder::get).forEach(output::accept);
  }
}
