package daripher.skilltree.skill.bonus.predicate.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.predicate.PSTLivingEntityPredicates;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public enum NoneLivingEntityPredicate implements LivingEntityPredicate {
    INSTANCE;

    @Override
    public boolean test(LivingEntity living) {
        return true;
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        return bonusTooltip;
    }

    @Override
    public LivingEntityPredicate.Serializer getSerializer() {
        return PSTLivingEntityPredicates.NONE.get();
    }

    public static class Serializer implements LivingEntityPredicate.Serializer {
        @Override
        public LivingEntityPredicate deserialize(JsonObject json) throws JsonParseException {
            return NoneLivingEntityPredicate.INSTANCE;
        }

        @Override
        public void serialize(JsonObject json, LivingEntityPredicate predicate) {
            validatePredicate(predicate);
        }

        @Override
        public LivingEntityPredicate deserialize(CompoundTag tag) {
            return NoneLivingEntityPredicate.INSTANCE;
        }

        @Override
        public CompoundTag serialize(LivingEntityPredicate predicate) {
            validatePredicate(predicate);
            return new CompoundTag();
        }

        @Override
        public LivingEntityPredicate deserialize(FriendlyByteBuf buf) {
            return NoneLivingEntityPredicate.INSTANCE;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingEntityPredicate predicate) {
            validatePredicate(predicate);
        }

        private void validatePredicate(LivingEntityPredicate predicate) {
            if (predicate != NoneLivingEntityPredicate.INSTANCE) {
                throw new IllegalArgumentException("Expected NoneLivingEntityPredicate, got: " + predicate);
            }
        }

        @Override
        public LivingEntityPredicate createDefaultInstance() {
            return NoneLivingEntityPredicate.INSTANCE;
        }
    }
}
