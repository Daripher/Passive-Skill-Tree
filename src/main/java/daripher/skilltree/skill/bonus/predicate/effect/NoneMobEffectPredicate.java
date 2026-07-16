package daripher.skilltree.skill.bonus.predicate.effect;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.predicate.PSTMobEffectPredicates;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;

public enum NoneMobEffectPredicate implements MobEffectPredicate {
    INSTANCE;

    @Override
    public boolean test(MobEffect mobEffect) {
        return true;
    }

    @Override
    public boolean testsForHarmfulEffects() {
        return false;
    }

    @Override
    public MobEffectPredicate.Serializer getSerializer() {
        return PSTMobEffectPredicates.NONE.get();
    }

    @Override
    public Component getTooltip() {
        return Component.translatable(MobEffectType.ANY.getDescriptionId());
    }

    @Override
    public Component getTooltip(String type) {
        return Component.translatable("%s.%s".formatted(MobEffectType.ANY.getDescriptionId(), type));
    }

    public static class Serializer implements MobEffectPredicate.Serializer {
        @Override
        public MobEffectPredicate deserialize(JsonObject json) throws JsonParseException {
            return NoneMobEffectPredicate.INSTANCE;
        }

        @Override
        public void serialize(JsonObject json, MobEffectPredicate condition) {
            if (condition != NoneMobEffectPredicate.INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public MobEffectPredicate deserialize(CompoundTag tag) {
            return NoneMobEffectPredicate.INSTANCE;
        }

        @Override
        public CompoundTag serialize(MobEffectPredicate condition) {
            if (condition != NoneMobEffectPredicate.INSTANCE) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public MobEffectPredicate deserialize(FriendlyByteBuf buf) {
            return NoneMobEffectPredicate.INSTANCE;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, MobEffectPredicate condition) {
            if (condition != NoneMobEffectPredicate.INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public MobEffectPredicate createDefaultInstance() {
            return NoneMobEffectPredicate.INSTANCE;
        }
    }
}
