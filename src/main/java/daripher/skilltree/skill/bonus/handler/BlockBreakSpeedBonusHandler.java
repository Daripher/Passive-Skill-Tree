package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.BlockBreakSpeedBonus;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class BlockBreakSpeedBonusHandler {
    @SubscribeEvent
    public static void modifyBlockBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        List<BlockBreakSpeedBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, BlockBreakSpeedBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float multiplier = 1f;
        for (BlockBreakSpeedBonus bonus : skillBonuses) {
            multiplier += bonus.getMultiplier(player);
        }
        event.setNewSpeed(event.getNewSpeed() * multiplier);
    }
}
