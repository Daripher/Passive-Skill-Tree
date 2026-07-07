package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.ItemUsageSpeedBonus;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ItemUsageSpeedBonusHandler {
    @SubscribeEvent
    public static void applyItemUsageSpeed(LivingEntityUseItemEvent.Tick event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        List<ItemUsageSpeedBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, ItemUsageSpeedBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float itemUsageSpeedModifier = 0f;
        for (ItemUsageSpeedBonus skillBonus : skillBonuses) {
            itemUsageSpeedModifier += skillBonus.getItemUsageSpeedModifier(player, event.getItem());
        }
        if (itemUsageSpeedModifier == 0f) {
            return;
        }
        // speed up
        if (itemUsageSpeedModifier > 0f) {
            int ticksToRemove = (int) itemUsageSpeedModifier;
            float fractionalPart = itemUsageSpeedModifier - ticksToRemove;
            int newDuration = event.getDuration() - ticksToRemove;
            if (fractionalPart > 0 && player.tickCount * fractionalPart % 1 < fractionalPart) {
                newDuration--;
            }
            event.setDuration(Math.max(1, newDuration));
        }
        // slow down
        else {
            float penalty = Math.abs(itemUsageSpeedModifier);
            if (penalty >= 1f) {
                // cap slowdown effect to 99%
                penalty = 0.99f;
            }
            if (player.tickCount * penalty % 1 < penalty) {
                event.setDuration(event.getDuration() + 1);
            }
        }
    }
}
