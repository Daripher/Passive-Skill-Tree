package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.HealthReservationBonus;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class HealthReservationBonusHandler {
    @SubscribeEvent
    public static void applyHealthReservation(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        float reservation = getHealthReservation(player);
        if (reservation == 0) {
            return;
        }
        float maxHealth = player.getMaxHealth();
        float currentHealth = player.getHealth();
        float maxAllowedHealth = maxHealth * (1f - reservation);
        if (currentHealth > maxAllowedHealth) {
            player.setHealth(maxAllowedHealth);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void preventReservedHealthHealing(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        float reservation = getHealthReservation(player);
        if (reservation == 0) {
            return;
        }
        float maxHealth = player.getMaxHealth();
        float currentHealth = player.getHealth();
        float maxAllowedHealth = maxHealth * (1f - reservation);
        if (currentHealth > maxAllowedHealth) {
            event.setCanceled(true);
            return;
        }
        if (currentHealth + event.getAmount() > maxAllowedHealth) {
            float maxAllowedHealing = maxAllowedHealth - currentHealth;
            event.setAmount(maxAllowedHealing);
        }
    }

    private static float getHealthReservation(Player player) {
        float reservation = 0f;
        List<HealthReservationBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, HealthReservationBonus.class);
        for (HealthReservationBonus bonus : skillBonuses) {
            reservation += bonus.getAmount(player);
        }
        return reservation;
    }
}
