package daripher.skilltree.item.quiver;

import daripher.skilltree.item.ItemBonusProvider;
import daripher.skilltree.skill.bonus.condition.living.BurningCondition;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.DamageBonus;
import java.util.List;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.jetbrains.annotations.NotNull;

public class FieryQuiverItem extends QuiverItem implements ItemBonusProvider {
  @Override
  public @NotNull List<ItemBonus<?>> getItemBonuses() {
    return List.of(
        new ItemSkillBonus(
            new DamageBonus(0.05f, Operation.MULTIPLY_BASE)
                .setTargetCondition(new BurningCondition())));
  }
}
