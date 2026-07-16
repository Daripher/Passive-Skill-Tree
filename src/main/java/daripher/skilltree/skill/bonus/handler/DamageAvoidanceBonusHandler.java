package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.event.EvasionEventListener;
import daripher.skilltree.skill.bonus.player.DamageAvoidanceChanceBonus;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class DamageAvoidanceBonusHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void applyDamageAvoidanceBonuses(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        List<DamageAvoidanceChanceBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, DamageAvoidanceChanceBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        DamageSource damageSource = event.getSource();
        LivingEntity attacker = getAttacker(damageSource);
        float avoidanceChance = 0f;
        for (DamageAvoidanceChanceBonus skillBonus : skillBonuses) {
            avoidanceChance += skillBonus.getChance(damageSource, player, attacker);
        }
        if (player.getRandom().nextFloat() < avoidanceChance) {
            event.setCanceled(true);
            EventListenerBonusHandler.triggerEvent(player, EvasionEventListener.class, (eventListener, skillBonus) -> {
                eventListener.onEvent(player, attacker, skillBonus);
            });
        }
    }

    private static @Nullable LivingEntity getAttacker(DamageSource damageSource) {
        Entity sourceEntity = damageSource.getEntity();
        if (sourceEntity instanceof LivingEntity attacker) {
            return attacker;
        } else if (sourceEntity instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity attacker) {
            return attacker;
        }
        return null;
    }
}
