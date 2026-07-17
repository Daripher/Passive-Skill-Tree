package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.ItemUseMovementSpeedBonus;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, value = Dist.CLIENT)
public class ItemUseMovementSpeedBonusHandler {
    @SubscribeEvent
    public static void modifyItemUseMovementSpeed(MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        if (!player.isUsingItem() || player.isPassenger()) {
            return;
        }
        List<ItemUseMovementSpeedBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, ItemUseMovementSpeedBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float penaltyReduction = 0f;
        ItemStack itemStack = player.getUseItem();
        for (ItemUseMovementSpeedBonus skillBonus : skillBonuses) {
            penaltyReduction += skillBonus.getMovementPenaltyReduction(player, itemStack);
        }
        if (penaltyReduction <= 0f) {
            return;
        }
        penaltyReduction = Math.min(1f, penaltyReduction);
        float inputScaleMultiplier = 1f + (4f * penaltyReduction);
        Input input = event.getInput();
        input.leftImpulse *= inputScaleMultiplier;
        input.forwardImpulse *= inputScaleMultiplier;
    }
}
