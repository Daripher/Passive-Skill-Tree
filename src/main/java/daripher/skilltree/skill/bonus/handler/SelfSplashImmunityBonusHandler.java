package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.SelfSplashImmuneBonus;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class SelfSplashImmunityBonusHandler {
    public static boolean isPlayerImmuneToOwnSplashPotions(Player player) {
        List<SelfSplashImmuneBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, SelfSplashImmuneBonus.class);
        return !skillBonuses.isEmpty();
    }
}
