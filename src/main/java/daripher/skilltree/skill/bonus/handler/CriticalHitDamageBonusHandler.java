package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.CritDamageBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class CriticalHitDamageBonusHandler {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void applyCritBonuses(CriticalHitEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        if (!(event.getTarget() instanceof LivingEntity hurtEntity)) {
            return;
        }
        boolean isCrit = event.isCriticalHit();
        if (!isCrit) {
            return;
        }
        DamageSource damageSource = player.level().damageSources().playerAttack(player);
        float modCritMultiplier = getCritDamageModifier(player, damageSource, hurtEntity);
        event.setDamageMultiplier(1.5f + modCritMultiplier);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void applyIndirectHitCritDamage(LivingIncomingDamageEvent event) {
        DamageSource damageSource = event.getSource();
        Entity directDamagingEntity = damageSource.getDirectEntity();
        // direct damage, handled by the method above, ignoring
        if (directDamagingEntity instanceof Player) {
            return;
        }
        Entity damagingEntity = damageSource.getEntity();
        if (!(damagingEntity instanceof ServerPlayer player)) {
            return;
        }
        boolean isVanillaCrit = false;
        if (directDamagingEntity instanceof AbstractArrow arrow) {
            isVanillaCrit = arrow.isCritArrow();
        }
        float critChance = CriticalHitChanceBonusHandler.getCritChance(player, damageSource, event.getEntity());
        boolean isModCrit = player.getRandom().nextFloat() < critChance;
        if (!isVanillaCrit && !isModCrit) {
            return;
        }
        LivingEntity hurtEntity = event.getEntity();
        float modCritMultiplier = getCritDamageModifier(player, damageSource, hurtEntity);
        if (isVanillaCrit) {
            event.setAmount(event.getAmount() * (1f + modCritMultiplier));
        } else {
            float vanillaCritMultiplier = 1.5f;
            event.setAmount(event.getAmount() * (vanillaCritMultiplier + modCritMultiplier));
        }
    }

    public static float getCritDamageModifier(Player player, DamageSource source, LivingEntity target) {
        float multiplier = 0f;
        for (CritDamageBonus bonus : SkillBonusProvider.getSkillBonuses(player, CritDamageBonus.class)) {
            multiplier += bonus.getDamageBonus(source, player, target);
        }
        return multiplier;
    }
}
