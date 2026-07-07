package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.CritChanceBonus;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class CriticalHitChanceBonusHandler {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void applyDirectHitCritChance(CriticalHitEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        if (!(event.getTarget() instanceof LivingEntity hurtEntity)) {
            return;
        }
        DamageSource damageSource = player.level().damageSources().playerAttack(player);
        boolean isVanillaCrit = event.isVanillaCritical();
        float critChance = getCritChance(player, damageSource, hurtEntity);
        boolean isModCrit = player.getRandom().nextFloat() < critChance;
        if (!isVanillaCrit && !isModCrit) {
            return;
        }
        if (!isVanillaCrit) {
            event.setResult(Event.Result.ALLOW);
        }
    }

    public static float getCritChance(Player player, DamageSource source, LivingEntity target) {
        float critChance = 0f;
        for (CritChanceBonus bonus : SkillBonusProvider.getSkillBonuses(player, CritChanceBonus.class)) {
            critChance += bonus.getChanceBonus(source, player, target);
        }
        return critChance;
    }
}
