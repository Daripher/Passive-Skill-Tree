package daripher.skilltree.event;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.config.ServerConfig;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class PSTEvents {
  @SubscribeEvent
  public static void applyGrindstoneExpPenalty(GrindstoneEvent.OnTakeItem event) {
    event.setXp((int) (event.getXp() * ServerConfig.grindstone_exp_multiplier));
  }
}
