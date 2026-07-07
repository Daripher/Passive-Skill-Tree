package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.LethalPoisonBonus;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class LethalPoisonBonusHandler {
    public static boolean canEntityKillWithPoison(LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player player)) {
            return false;
        }
        List<LethalPoisonBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, LethalPoisonBonus.class);
        return !skillBonuses.isEmpty();
    }
}
