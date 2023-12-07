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
  @SuppressWarnings("DataFlowIssue")
  @ModifyReturnValue(method = "getMaxDamage", at = @At("RETURN"))
  private int applyDurabilityModifiers(int original) {
    ItemStack stack = (ItemStack) (Object) this;
    List<ItemDurabilityBonus> bonuses = ItemHelper.getItemBonuses(stack, ItemDurabilityBonus.class);
    original +=
        bonuses.stream()
            .filter(bonus -> bonus.getOperation() == AttributeModifier.Operation.ADDITION)
            .map(ItemDurabilityBonus::getAmount)
            .reduce(Float::sum)
            .orElse(0f);
    original *=
        bonuses.stream()
            .filter(bonus -> bonus.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE)
            .map(ItemDurabilityBonus::getAmount)
            .reduce(Float::sum)
            .orElse(1f);
    original *=
        bonuses.stream()
            .filter(bonus -> bonus.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL)
            .map(ItemDurabilityBonus::getAmount)
            .reduce(Float::sum)
            .orElse(1f);
    return original;
  }
}
