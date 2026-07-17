package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.EffectImmunityBypassBonus;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class EffectImmunityBypassBonusHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void bypassEffectImmunity(MobEffectEvent.Applicable event) {
        LivingEntity affectedEntity = event.getEntity();
        if (!(affectedEntity.getKillCredit() instanceof Player effectSource)) {
            return;
        }
        List<EffectImmunityBypassBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(effectSource, EffectImmunityBypassBonus.class);
        MobEffect mobEffect = event.getEffectInstance().getEffect().value();
        for (EffectImmunityBypassBonus skillBonus : skillBonuses) {
            if (skillBonus.shouldIgnoreEffectImmunity(mobEffect, effectSource, affectedEntity)) {
                event.setResult(MobEffectEvent.Applicable.Result.APPLY);
                return;
            }
        }
    }
}
