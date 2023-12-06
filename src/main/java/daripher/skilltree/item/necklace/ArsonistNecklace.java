package daripher.skilltree.item.necklace;

import daripher.skilltree.skill.bonus.condition.living.BurningCondition;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.DamageBonus;
import java.util.function.Consumer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class ArsonistNecklace extends NecklaceItem {
  @Override
  public void getItemBonuses(Consumer<ItemBonus<?>> consumer) {
    consumer.accept(
        new ItemSkillBonus(
            new DamageBonus(0.05f, Operation.MULTIPLY_BASE)
                .setTargetCondition(new BurningCondition())));
  }
}
