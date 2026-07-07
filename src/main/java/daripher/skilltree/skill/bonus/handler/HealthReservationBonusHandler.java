package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.HealthReservationBonus;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class HealthReservationBonusHandler {
    @SubscribeEvent
    public static void applyHealthReservation(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.side == LogicalSide.CLIENT) {
            return;
        }
        Player player = event.player;
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
