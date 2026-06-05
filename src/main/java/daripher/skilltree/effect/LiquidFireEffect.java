package daripher.skilltree.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LiquidFireEffect extends MobEffect {
    public LiquidFireEffect() {
        super(MobEffectCategory.HARMFUL, 0xfa440c);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, @NotNull LivingEntity target, int amplifier, double health) {
        float damage = (int) (health * (double) (6 << amplifier) + 0.5);
        DamageSources damageSources = target.damageSources();
        if (source == null) {
            target.hurt(damageSources.onFire(), damage);
        } else {
            Registry<DamageType> damageTypes = target.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
            Holder.Reference<DamageType> damageType = damageTypes.getHolderOrThrow(DamageTypes.ON_FIRE);
            DamageSource damageSource = new DamageSource(damageType, source, indirectSource);
            target.hurt(damageSource, damage);
        }
        target.setSecondsOnFire((int) damage / 2);
    }
}
