package daripher.skilltree.mixin.apotheosis;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadows.apotheosis.adventure.affix.socket.gem.bonus.AttributeBonus;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.util.StepFunction;

@Mixin(value = AttributeBonus.class, remap = false)
public class AttributeBonusMixin {
  @Redirect(
      method = "read",
      at = @At(value = "INVOKE", target = "Lshadows/placebo/util/StepFunction;get(F)F"))
  private float amplifyGemPower(
      StepFunction function, float level, ItemStack gem, LootRarity rarity) {
    float bonus = function.get(level);
    CompoundTag tag = gem.getOrCreateTag();
    if (!tag.contains("gem_power")) return bonus;
    return bonus * tag.getFloat("gem_power");
  }
}
