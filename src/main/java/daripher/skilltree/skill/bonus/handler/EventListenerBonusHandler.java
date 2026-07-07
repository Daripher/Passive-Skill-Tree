package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.event.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.BiConsumer;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class EventListenerBonusHandler {
    @SubscribeEvent
    public static void triggerHurtEvents(LivingHurtEvent event) {
        DamageSource damageSource = event.getSource();
        LivingEntity target = event.getEntity();
        Entity damagingEntity = damageSource.getEntity();
        if (damagingEntity instanceof Player player) {
            triggerEvent(player, OutgoingDamageEventListener.class, (eventListener, skillBonus) -> {
                eventListener.onEvent(player, target, damageSource, skillBonus);
            });
        }
        if (target instanceof Player player) {
            LivingEntity attacker = damagingEntity instanceof LivingEntity livingAttacker ? livingAttacker : null;
            triggerEvent(player, IncomingDamageEventListener.class, (eventListener, skillBonus) -> {
                eventListener.onEvent(player, attacker, damageSource, skillBonus);
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void triggerCritEvents(CriticalHitEvent event) {
        Event.Result eventResult = event.getResult();
        boolean isCrit = eventResult == Event.Result.ALLOW || (eventResult == Event.Result.DEFAULT && event.isVanillaCritical());
        if (!isCrit) {
            return;
        }
        if (!(event.getTarget() instanceof LivingEntity target)) {
            return;
        }
        Player player = event.getEntity();
        triggerEvent(player, CriticalHitEventListener.class, (eventListener, skillBonus) -> {
            eventListener.onEvent(player, target, skillBonus);
        });
    }

    @SubscribeEvent
    public static void triggerShieldBlockEvents(ShieldBlockEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        DamageSource source = event.getDamageSource();
        Entity sourceEntity = source.getEntity();
        LivingEntity attacker = sourceEntity instanceof LivingEntity livingEntity ? livingEntity : null;
        triggerEvent(player, ShieldBlockEventListener.class, (eventListener, skillBonus) -> {
            eventListener.onEvent(player, attacker, source, skillBonus);
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void triggerItemUsedEvents(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        triggerEvent(player, ItemUseEventListener.class, (eventListener, skillBonus) -> {
            eventListener.onEvent(player, event.getItem(), skillBonus);
        });
    }

    @SubscribeEvent
    public static void triggerKillEvents(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        DamageSource damageSource = event.getSource();
        LivingEntity killedEntity = event.getEntity();
        triggerEvent(player, KillEventListener.class, (eventListener, skillBonus) -> {
            eventListener.onEvent(player, killedEntity, damageSource, skillBonus);
        });
    }

    @SuppressWarnings({"rawtypes"})
    public static <T extends SkillEventListener> void triggerEvent(Player player, Class<T> listenerClass, BiConsumer<T, EventListenerBonus<?>> action) {
        List<EventListenerBonus> skillBonuses = SkillBonusProvider.getMergedSkillBonuses(player, EventListenerBonus.class);
        for (EventListenerBonus<?> skillBonus : skillBonuses) {
            SkillEventListener listener = skillBonus.getEventListener();
            if (listenerClass.isInstance(listener)) {
                T eventListener = listenerClass.cast(listener);
                action.accept(eventListener, skillBonus);
            }
        }
    }
}
