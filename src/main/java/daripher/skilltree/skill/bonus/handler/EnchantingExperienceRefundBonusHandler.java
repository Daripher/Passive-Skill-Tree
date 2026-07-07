package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.EnchantingExperienceRefundBonus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class EnchantingExperienceRefundBonusHandler {
    public static boolean shouldRefundEnchantingExperience(@Nonnull Player player, ItemStack itemStack) {
        List<EnchantingExperienceRefundBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, EnchantingExperienceRefundBonus.class);
        if (skillBonuses.isEmpty()) {
            return false;
        }
        float refundChance = 0f;
        for (EnchantingExperienceRefundBonus skillBonus : skillBonuses) {
            refundChance += skillBonus.getRefundChance(player, itemStack);
        }
        return player.getRandom().nextFloat() < refundChance;
    }
}
