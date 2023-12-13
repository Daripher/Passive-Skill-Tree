package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.bonus.item.FoodEffectBonus;
import daripher.skilltree.skill.bonus.item.FoodSaturationBonus;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
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
public abstract class ItemMixin implements IForgeItem {
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
    if (properties == null) return null;
    float restorationMultiplier = 1f;
    restorationMultiplier +=
        ItemHelper.getItemBonuses(stack, FoodSaturationBonus.class).stream()
            .map(FoodSaturationBonus::getMultiplier)
            .reduce(Float::sum)
            .orElse(0f);
    List<MobEffectInstance> bonusEffects =
        ItemHelper.getItemBonuses(stack, FoodEffectBonus.class).stream()
            .map(FoodEffectBonus::getEffectInstance)
            .map(MobEffectInstance::new)
            .toList();
    if (restorationMultiplier == 1f && bonusEffects.isEmpty()) return properties;
    FoodProperties.Builder newProperties = new FoodProperties.Builder();
    if (properties.canAlwaysEat()) newProperties.alwaysEat();
    if (properties.isFastFood()) newProperties.fast();
    if (properties.isMeat()) newProperties.meat();
    properties.getEffects().forEach(pair -> newProperties.effect(pair::getFirst, pair.getSecond()));
    bonusEffects.forEach(effect -> newProperties.effect(() -> effect, 1f));
    newProperties.nutrition((int) (properties.getNutrition() * restorationMultiplier));
    newProperties.saturationMod(properties.getSaturationModifier() / restorationMultiplier);
    return newProperties.build();
  }
}
