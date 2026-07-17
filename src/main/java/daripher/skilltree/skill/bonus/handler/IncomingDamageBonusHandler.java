package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.IncomingDamageBonus;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class IncomingDamageBonusHandler {
    @SubscribeEvent
    public static void modifyIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource damageSource = event.getSource();
        LivingEntity attacker = getAttacker(damageSource);
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        List<IncomingDamageBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, IncomingDamageBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float flatDamageBonus = 0f;
        float baseDamageMultiplier = 1f;
        float totalDamageMultiplier = 1f;
        for (IncomingDamageBonus bonus : skillBonuses) {
            flatDamageBonus += bonus.getDamageModifier(AttributeModifier.Operation.ADD_VALUE, damageSource, player, attacker);
            baseDamageMultiplier += bonus.getDamageModifier(AttributeModifier.Operation.ADD_MULTIPLIED_BASE, damageSource, player, attacker);
            totalDamageMultiplier *= 1f + bonus.getDamageModifier(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, damageSource, player, attacker);
        }
        float amount = event.getAmount();
        amount += flatDamageBonus;
        amount *= baseDamageMultiplier;
        amount *= totalDamageMultiplier;
        event.setAmount(amount);
    }

    private static @Nullable LivingEntity getAttacker(DamageSource damageSource) {
        if (!(damageSource.getEntity() instanceof LivingEntity attacker)) {
            return null;
        }
        return attacker;
    }
}
