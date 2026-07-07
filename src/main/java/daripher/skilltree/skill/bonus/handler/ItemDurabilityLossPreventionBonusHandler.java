package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.ItemDurabilityLossPreventionBonus;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemDurabilityLossPreventionBonusHandler {
    public static boolean shouldPreventItemDurabilityLoss(Player player, ItemStack itemStack, RandomSource random) {
        List<ItemDurabilityLossPreventionBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, ItemDurabilityLossPreventionBonus.class);
        if (skillBonuses.isEmpty()) {
            return false;
        }
        float durabilityLossPreventionChance = 0f;
        for (ItemDurabilityLossPreventionBonus skillBonus : skillBonuses) {
            durabilityLossPreventionChance += skillBonus.getDurabilityLossChanceReduction(player, itemStack);
        }
        if (durabilityLossPreventionChance <= 0f) {
            return false;
        }
        return random.nextFloat() < durabilityLossPreventionChance;
    }
}
