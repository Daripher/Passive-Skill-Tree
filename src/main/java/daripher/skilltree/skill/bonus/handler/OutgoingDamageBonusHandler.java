package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.OutgoingDamageBonus;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class OutgoingDamageBonusHandler {
    @SubscribeEvent
    public static void modifyOutgoingDamage(LivingHurtEvent event) {
        DamageSource damageSource = event.getSource();
        if (!(damageSource.getEntity() instanceof Player player)) {
            return;
        }
        LivingEntity target = event.getEntity();
        List<OutgoingDamageBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, OutgoingDamageBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float flatDamageBonus = 0f;
        float baseDamageMultiplier = 1f;
        float totalDamageMultiplier = 1f;
        for (OutgoingDamageBonus bonus : skillBonuses) {
            flatDamageBonus += bonus.getDamageModifier(AttributeModifier.Operation.ADDITION, damageSource, player, target);
            baseDamageMultiplier += bonus.getDamageModifier(AttributeModifier.Operation.MULTIPLY_BASE, damageSource, player, target);
            totalDamageMultiplier *= 1f + bonus.getDamageModifier(AttributeModifier.Operation.MULTIPLY_TOTAL, damageSource, player, target);
        }
        float amount = event.getAmount();
        amount += flatDamageBonus;
        amount *= baseDamageMultiplier;
        amount *= totalDamageMultiplier;
        event.setAmount(amount);
    }
}
