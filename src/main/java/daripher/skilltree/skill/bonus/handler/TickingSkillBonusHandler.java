package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.TickingSkillBonus;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class TickingSkillBonusHandler {
    @SubscribeEvent
    public static void tickSkillBonuses(PlayerTickEvent.Post event) {
        if (event.getEntity().isDeadOrDying()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        List<TickingSkillBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, TickingSkillBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        for (TickingSkillBonus bonus : skillBonuses) {
            bonus.tick(player);
        }
    }
}
