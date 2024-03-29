package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.bonus.item.ItemDurabilityBonus;
import java.util.List;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IForgeItemStack {
  @SuppressWarnings({"DataFlowIssue", "lossy-conversions"})
  @ModifyReturnValue(method = "getMaxDamage", at = @At("RETURN"))
  private int applyDurabilityModifiers(int original) {
    List<ItemDurabilityBonus> durabilityBonuses =
        ItemHelper.getDurabilityBonuses((ItemStack) (Object) this);
    original += getDurabilityBonus(durabilityBonuses, AttributeModifier.Operation.ADDITION);
    original *= getDurabilityBonus(durabilityBonuses, AttributeModifier.Operation.MULTIPLY_BASE);
    original *= getDurabilityBonus(durabilityBonuses, AttributeModifier.Operation.MULTIPLY_TOTAL);
    return original;
  }

  private static float getDurabilityBonus(
      List<ItemDurabilityBonus> durabilityBonuses, AttributeModifier.Operation operation) {
    float bonus = operation == AttributeModifier.Operation.ADDITION ? 0f : 1f;
    bonus +=
        durabilityBonuses.stream()
            .filter(b -> b.getOperation() == operation)
            .map(ItemDurabilityBonus::getAmount)
            .reduce(Float::sum)
            .orElse(0f);
    return bonus;
  }
}
