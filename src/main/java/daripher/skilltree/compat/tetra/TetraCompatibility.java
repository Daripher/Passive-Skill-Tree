package daripher.skilltree.compat.tetra;

import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import se.mickelus.tetra.module.ItemUpgradeRegistry;

public enum TetraCompatibility {
  INSTANCE;

  public void register() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    modEventBus.addListener(this::registerReplacementHook);
  }

  private void registerReplacementHook(RegisterEvent event) {
    ItemUpgradeRegistry.instance.registerReplacementHook(
        (original, modular) -> {
          ItemHelper.removeItemBonuses(modular);
          ItemHelper.getItemBonuses(original, ItemBonus.class)
              .forEach(bonus -> ItemHelper.addItemBonus(modular, bonus));
          return modular;
        });
  }
}
