package daripher.skilltree.item.quiver;

import daripher.skilltree.item.ItemBonusProvider;
import daripher.skilltree.skill.bonus.condition.living.HasEffectCondition;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.DamageBonus;

import java.util.function.Consumer;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class ToxicQuiverItem extends QuiverItem implements ItemBonusProvider {
  @Override
  public void getItemBonuses(Consumer<ItemBonus<?>> consumer) {
    consumer.accept(
        new ItemSkillBonus(
            new DamageBonus(0.05f, Operation.MULTIPLY_BASE)
                .setTargetCondition(new HasEffectCondition(MobEffects.POISON))));
  }
}
