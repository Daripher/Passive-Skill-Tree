package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.init.PSTDamageTypes;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.LethalPoisonBonus;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.extensions.IForgeMobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEffect.class)
public abstract class MobEffectMixin implements IForgeMobEffect {
  @Inject(method = "applyEffectTick", at = @At("HEAD"), cancellable = true)
  public void inflictPoisonDamage(LivingEntity livingEntity, int amplifier, CallbackInfo callbackInfo) {
    //noinspection ConstantValue
    if (((Object) this) != MobEffects.POISON) {
        return;
    }
    handlePoisonDamage(livingEntity);
    callbackInfo.cancel();
  }

  private static void handlePoisonDamage(LivingEntity livingEntity) {
    LivingEntity attacker = livingEntity.getKillCredit();
    float damage = 1f;
    boolean isLowHealth = livingEntity.getHealth() <= damage;
    boolean isPoisonLethal = isPoisonLethal(attacker);
    if (isLowHealth && !isPoisonLethal) {
        return;
    }
    DamageSources damageSources = livingEntity.damageSources();
    DamageSource damageSource = damageSources.magic();
    if (attacker instanceof Player player) {
      Registry<DamageType> damageTypes = player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
      Holder.Reference<DamageType> damageType = damageTypes.getHolderOrThrow(PSTDamageTypes.POISON);
      damageSource = new DamageSource(damageType, player, null);
      // resets hurt timer
      livingEntity.setLastHurtByPlayer(player);
    }
    SkillBonusHandler.forcefullyInflictDamage(damageSource, damage, livingEntity);
  }

  private static boolean isPoisonLethal(LivingEntity attacker) {
    return attacker instanceof Player player && !SkillBonusHandler.getSkillBonuses(player, LethalPoisonBonus.class).isEmpty();
  }
}
