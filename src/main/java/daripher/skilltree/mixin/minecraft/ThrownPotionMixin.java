package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.SelfSplashImmuneBonus;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ThrownPotion.class)
public abstract class ThrownPotionMixin extends ThrowableItemProjectile implements ItemSupplier {
  @SuppressWarnings("DataFlowIssue")
  private ThrownPotionMixin() {
    super(null, null);
  }

  @Redirect(method = "applySplash",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/entity/LivingEntity;" +
                              "addEffect(" +
                              "Lnet/minecraft/world/effect/MobEffectInstance;" +
                              "Lnet/minecraft/world/entity/Entity;" +
                              ")Z"))
  private boolean setAttackerOnHit(LivingEntity entity, MobEffectInstance effectInstance, Entity effectSource) {
    if (getOwner() instanceof Player player) {
      entity.setLastHurtByPlayer(player);
    }
    return entity.addEffect(effectInstance, effectSource);
  }

  @Redirect(method = "applySplash",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/level/Level;" +
                              "getEntitiesOfClass(" +
                              "Ljava/lang/Class;" +
                              "Lnet/minecraft/world/phys/AABB;" +
                              ")Ljava/util/List;"))
  private <T extends Entity> List<T> removePlayerTarget(Level level, Class<T> entityClass, AABB area) {
    List<T> targets = level.getEntitiesOfClass(entityClass, area);
    Entity owner = getOwner();
    if (!(owner instanceof Player player)) {
      return targets;
    }
    //noinspection SuspiciousMethodCalls
    if (!targets.contains(player)) {
      return targets;
    }
    List<SelfSplashImmuneBonus> bonuses = SkillBonusHandler.getSkillBonuses(player, SelfSplashImmuneBonus.class);
    if (bonuses.isEmpty()) {
      return targets;
    }
    targets.removeIf(owner::equals);
    return targets;
  }
}

