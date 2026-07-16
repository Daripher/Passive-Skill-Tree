package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.RepairEfficiencyBonus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class RepairEfficiencyBonusHandler {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void applyRepairEfficiency(AnvilUpdateEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        ItemStack resultItem = event.getOutput();
        if (resultItem.isEmpty() || !resultItem.isDamageableItem()) {
            return;
        }
        ItemStack baseItem = event.getLeft();
        if (baseItem.getItem() != resultItem.getItem()) {
            return;
        }
        int vanillaDurabilityRestored = baseItem.getDamageValue() - resultItem.getDamageValue();
        if (vanillaDurabilityRestored <= 0) {
            return;
        }
        List<RepairEfficiencyBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, RepairEfficiencyBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float efficiencyBonus = 0f;
        for (RepairEfficiencyBonus bonus : skillBonuses) {
            efficiencyBonus += bonus.getRepairEfficiencyMultiplier(baseItem);
        }
        if (efficiencyBonus <= 0f) {
            return;
        }
        int totalDurabilityToRestore = (int) (vanillaDurabilityRestored * (1f + efficiencyBonus));
        int currentItemDamage = baseItem.getDamageValue();
        resultItem.setDamageValue(Math.max(0, currentItemDamage - totalDurabilityToRestore));
        event.setOutput(resultItem);
    }
}
