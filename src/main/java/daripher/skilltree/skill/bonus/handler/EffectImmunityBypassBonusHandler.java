package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.EffectImmunityBypassBonus;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class EffectImmunityBypassBonusHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void bypassEffectImmunity(MobEffectEvent.Applicable event) {
        LivingEntity affectedEntity = event.getEntity();
        if (!(affectedEntity.getKillCredit() instanceof Player effectSource)) {
            return;
        }
        List<EffectImmunityBypassBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(effectSource, EffectImmunityBypassBonus.class);
        MobEffect mobEffect = event.getEffectInstance().getEffect();
        for (EffectImmunityBypassBonus skillBonus : skillBonuses) {
            if (skillBonus.shouldIgnoreEffectImmunity(mobEffect, effectSource, affectedEntity)) {
                event.setResult(Event.Result.ALLOW);
                return;
            }
        }
    }
}
