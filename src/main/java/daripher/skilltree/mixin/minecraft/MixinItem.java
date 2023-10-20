package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.util.FoodHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Item.class)
public abstract class MixinItem implements IForgeItem {
  @Redirect(
      method = "getBarWidth",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/item/Item;getMaxDamage(Lnet/minecraft/world/item/ItemStack;)I"))
  private int applyBonusDurability(Item item, ItemStack stack) {
    return stack.getMaxDamage();
  }

  @Override
  public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
    FoodProperties properties = IForgeItem.super.getFoodProperties(stack, entity);
    if (properties == null) return properties;
    if (!FoodHelper.hasRestorationBonus(stack)) return properties;
    float restorationBonus = 1 + FoodHelper.getRestorationBonus(stack);
    FoodProperties.Builder newProperties = new FoodProperties.Builder();
    if (properties.canAlwaysEat()) newProperties.alwaysEat();
    if (properties.isFastFood()) newProperties.fast();
    if (properties.isMeat()) newProperties.meat();
    properties.getEffects().forEach(pair -> newProperties.effect(pair::getFirst, pair.getSecond()));
    newProperties.nutrition((int) (properties.getNutrition() * restorationBonus));
    newProperties.saturationMod(properties.getSaturationModifier() / restorationBonus);
    return newProperties.build();
  }
}
