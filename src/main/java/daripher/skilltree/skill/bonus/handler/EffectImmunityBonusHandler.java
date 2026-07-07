package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.EffectImmunityBonus;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class EffectImmunityBonusHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void applyEffectImmunity(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        MobEffect mobEffect = event.getEffectInstance().getEffect();
        List<EffectImmunityBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, EffectImmunityBonus.class);
        for (EffectImmunityBonus skillBonus : skillBonuses) {
            if (skillBonus.shouldProvideImmunity(mobEffect, player)) {
                event.setResult(Event.Result.DENY);
                return;
            }
        }
    }
}
