package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.mixin.MobEffectInstanceAccessor;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.EffectDurationBonus;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class EffectDurationBonusHandler {
    @SubscribeEvent
    public static void applyEffectDurationBonuses(MobEffectEvent.Added event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) {
            return;
        }
        Player playerEffectSource = null;
        if (event.getEffectSource() instanceof Player player) {
            playerEffectSource = player;
        }
        if (event.getEffectSource() instanceof Projectile projectile && projectile.getOwner() instanceof Player player) {
            playerEffectSource = player;
        }
        float durationMultiplier = 1f;
        MobEffectInstance effectInstance = event.getEffectInstance();
        MobEffect mobEffect = effectInstance.getEffect().value();
        // outgoing effects, inflicted by players
        if (playerEffectSource != null) {
            List<EffectDurationBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(playerEffectSource, EffectDurationBonus.class);
            for (EffectDurationBonus skillBonus : skillBonuses) {
                if (skillBonus.getTarget() == SkillBonus.Target.ENEMY) {
                    durationMultiplier += skillBonus.getDurationModifier(mobEffect, playerEffectSource, target);
                }
            }
        }
        // incoming effects, inflicted onto players
        if (target instanceof Player playerTarget) {
            List<EffectDurationBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(playerTarget, EffectDurationBonus.class);
            for (EffectDurationBonus skillBonus : skillBonuses) {
                if (skillBonus.getTarget() == SkillBonus.Target.PLAYER) {
                    durationMultiplier += skillBonus.getDurationModifier(mobEffect, playerEffectSource, target);
                }
            }
        }
        if (durationMultiplier == 1f) {
            return;
        }
        int newDuration = (int) (effectInstance.getDuration() * durationMultiplier);
        newDuration = Math.max(1, newDuration);
        ((MobEffectInstanceAccessor) effectInstance).setDuration(newDuration);
    }
}
