package daripher.skilltree.skill.bonus.predicate.damage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.predicate.PSTDamagePredicates;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;

public record FallDamageCondition() implements DamageCondition {
    @Override
    public boolean met(DamageSource source) {
        return source.is(DamageTypeTags.IS_FALL);
    }

    @Override
    public DamageCondition.Serializer getSerializer() {
        return PSTDamagePredicates.FALL.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return getSerializer().hashCode();
    }

    public static class Serializer implements DamageCondition.Serializer {
        @Override
        public DamageCondition deserialize(JsonObject json) throws JsonParseException {
            return new FallDamageCondition();
        }

        @Override
        public void serialize(JsonObject json, DamageCondition condition) {
            if (!(condition instanceof FallDamageCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public DamageCondition deserialize(CompoundTag tag) {
            return new FallDamageCondition();
        }

        @Override
        public CompoundTag serialize(DamageCondition condition) {
            if (!(condition instanceof FallDamageCondition)) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public DamageCondition deserialize(FriendlyByteBuf buf) {
            return new FallDamageCondition();
        }

        @Override
        public void serialize(FriendlyByteBuf buf, DamageCondition condition) {
            if (!(condition instanceof FallDamageCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public DamageCondition createDefaultInstance() {
            return new FallDamageCondition();
        }
    }
}
