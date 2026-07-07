package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.TickingSkillBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class TickingSkillBonusHandler {
    @SubscribeEvent
    public static void tickSkillBonuses(TickEvent.PlayerTickEvent event) {
        if (event.player.isDeadOrDying()) {
            return;
        }
        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }
        if (event.phase == TickEvent.Phase.END) {
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
