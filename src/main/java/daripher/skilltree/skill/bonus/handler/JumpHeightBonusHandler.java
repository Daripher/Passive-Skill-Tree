package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.JumpHeightBonus;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class JumpHeightBonusHandler {
    public static float getJumpHeightMultiplier(Player player) {
        float multiplier = 1f;
        List<JumpHeightBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, JumpHeightBonus.class);
        for (JumpHeightBonus bonus : skillBonuses) {
            multiplier += bonus.getJumpHeightMultiplier(player);
        }
        return multiplier;
    }

    @SubscribeEvent
    public static void reduceFallDistance(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        float multiplier = getJumpHeightMultiplier(player);
        if (multiplier <= 1) {
            return;
        }
        event.setDistance(event.getDistance() / multiplier);
    }
}
