package daripher.skilltree.event;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.config.ServerConfig;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class PSTEvents {
    @SubscribeEvent
    public static void applyGrindstoneExpPenalty(GrindstoneEvent.OnTakeItem event) {
        event.setXp((int) (event.getXp() * ServerConfig.grindstone_exp_multiplier));
    }
}
