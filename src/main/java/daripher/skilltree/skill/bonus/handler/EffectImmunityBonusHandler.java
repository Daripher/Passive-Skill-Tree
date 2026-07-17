package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.EffectImmunityBonus;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class EffectImmunityBonusHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void applyEffectImmunity(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        MobEffect mobEffect = event.getEffectInstance().getEffect().value();
        List<EffectImmunityBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, EffectImmunityBonus.class);
        for (EffectImmunityBonus skillBonus : skillBonuses) {
            if (skillBonus.shouldProvideImmunity(mobEffect, player)) {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
                return;
            }
        }
    }
}
