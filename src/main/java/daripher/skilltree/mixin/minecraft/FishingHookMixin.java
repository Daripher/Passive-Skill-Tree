package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
  @Redirect(
      method = "retrieve",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
  private boolean multiplyFishingLoot(Level level, Entity entity) {
    if (!(entity instanceof ItemEntity item)) return level.addFreshEntity(entity);
    Player player = getPlayerOwner();
    float multiplier =
        SkillBonusHandler.getLootMultiplier(player, LootDuplicationBonus.LootType.FISHING);
    while (multiplier > 1) {
      ItemEntity copy = item.copy();
      copy.setDeltaMovement(item.getDeltaMovement());
      level.addFreshEntity(copy);
      multiplier--;
    }
    if (player.getRandom().nextFloat() < multiplier) {
      ItemEntity copy = item.copy();
      copy.setDeltaMovement(item.getDeltaMovement());
      level.addFreshEntity(copy);
    }
    return level.addFreshEntity(entity);
  }

  public abstract @Shadow Player getPlayerOwner();
}
