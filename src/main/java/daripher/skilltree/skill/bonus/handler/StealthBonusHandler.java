package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.StealthBonus;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class StealthBonusHandler {
    @SubscribeEvent
    public static void applyVisibilityMultiplier(LivingEvent.LivingVisibilityEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!(event.getLookingEntity() instanceof LivingEntity lookingEntity)) {
            return;
        }
        List<StealthBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, StealthBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float stealthMultiplier = 0f;
        for (StealthBonus skillBonus : skillBonuses) {
            stealthMultiplier += skillBonus.getStealthMultiplier(player, lookingEntity);
        }
        event.modifyVisibility(1f - stealthMultiplier);
    }
}
