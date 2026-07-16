package daripher.skilltree.skill.bonus.predicate.damage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.predicate.PSTDamagePredicates;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;

public record ThornsDamageCondition() implements DamageCondition {
    @Override
    public boolean met(DamageSource source) {
        return source.is(DamageTypes.THORNS);
    }

    @Override
    public DamageCondition.Serializer getSerializer() {
        return PSTDamagePredicates.THORNS.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    @Override
    public DamageSource createDamageSource(Player player) {
        Registry<DamageType> damageTypes = player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        Holder.Reference<DamageType> damageType = damageTypes.getHolderOrThrow(DamageTypes.THORNS);
        return new DamageSource(damageType, null, player);
    }

    @Override
    public boolean canCreateDamageSource() {
        return true;
    }

    @Override
    public int hashCode() {
        return getSerializer().hashCode();
    }

    public static class Serializer implements DamageCondition.Serializer {
        @Override
        public DamageCondition deserialize(JsonObject json) throws JsonParseException {
            return new ThornsDamageCondition();
        }

        @Override
        public void serialize(JsonObject json, DamageCondition condition) {
            if (!(condition instanceof ThornsDamageCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public DamageCondition deserialize(CompoundTag tag) {
            return new ThornsDamageCondition();
        }

        @Override
        public CompoundTag serialize(DamageCondition condition) {
            if (!(condition instanceof ThornsDamageCondition)) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public DamageCondition deserialize(FriendlyByteBuf buf) {
            return new ThornsDamageCondition();
        }

        @Override
        public void serialize(FriendlyByteBuf buf, DamageCondition condition) {
            if (!(condition instanceof ThornsDamageCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public DamageCondition createDefaultInstance() {
            return new ThornsDamageCondition();
        }
    }
}
