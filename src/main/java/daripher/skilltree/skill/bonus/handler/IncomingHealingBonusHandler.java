package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.IncomingHealingBonus;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class IncomingHealingBonusHandler {
    @SubscribeEvent
    public static void modifyIncomingHealing(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        List<IncomingHealingBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, IncomingHealingBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float multiplier = 1f;
        for (IncomingHealingBonus bonus : skillBonuses) {
            multiplier += bonus.getHealingMultiplier(player);
        }
        event.setAmount(event.getAmount() * multiplier);
    }
}
