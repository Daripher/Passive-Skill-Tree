package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.reloader.GemTypesReloader;
import daripher.skilltree.item.gem.GemItem;
import java.util.function.Predicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTCreativeTabs {
  public static final DeferredRegister<CreativeModeTab> REGISTRY =
      DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SkillTreeMod.MOD_ID);

  static {
    REGISTRY.register(
        "skilltree",
        () ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.skilltree"))
                .icon(() -> new ItemStack(PSTItems.AMNESIA_SCROLL.get()))
                .displayItems(
                    (params, output) -> {
                      GemTypesReloader.getGemTypes().values().stream()
                          .sorted()
                          .map(GemItem::getDefaultGemStack)
                          .forEach(output::accept);
                      PSTItems.REGISTRY.getEntries().stream()
                          .map(RegistryObject::get)
                          .filter(Predicate.not(PSTItems.GEM.get()::equals))
                          .forEach(output::accept);
                    })
                .build());
  }
}
