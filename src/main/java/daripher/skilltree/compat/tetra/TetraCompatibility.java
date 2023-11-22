package daripher.skilltree.compat.tetra;

import daripher.skilltree.item.ItemHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import se.mickelus.tetra.module.ItemUpgradeRegistry;

public enum TetraCompatibility {
  ISNTANCE;

  public void addCompatibility() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    modEventBus.addListener(this::registerReplacementHook);
  }

  private void registerReplacementHook(RegisterEvent event) {
    //    TODO: apply item bonuses?
//    ItemUpgradeRegistry.instance.registerReplacementHook(
//        (original, modular) -> {
//          ItemHelper.getBonuses().stream()
//              .filter(s -> ItemHelper.hasBonus(original, s))
//              .forEach(s -> ItemHelper.setBonus(modular, s, ItemHelper.getBonus(original, s)));
//          return modular;
//        });
  }
}
