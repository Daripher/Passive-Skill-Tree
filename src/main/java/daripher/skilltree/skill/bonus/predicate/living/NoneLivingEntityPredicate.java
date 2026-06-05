package daripher.skilltree.skill.bonus.predicate.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTLivingConditions;
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
        return PSTLivingConditions.NONE.get();
    }

    public static class Serializer implements LivingEntityPredicate.Serializer {
        @Override
        public LivingEntityPredicate deserialize(JsonObject json) throws JsonParseException {
            return NoneLivingEntityPredicate.INSTANCE;
        }

        @Override
        public void serialize(JsonObject json, LivingEntityPredicate condition) {
            if (condition != NoneLivingEntityPredicate.INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public LivingEntityPredicate deserialize(CompoundTag tag) {
            return NoneLivingEntityPredicate.INSTANCE;
        }

        @Override
        public CompoundTag serialize(LivingEntityPredicate condition) {
            if (condition != NoneLivingEntityPredicate.INSTANCE) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public LivingEntityPredicate deserialize(FriendlyByteBuf buf) {
            return NoneLivingEntityPredicate.INSTANCE;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingEntityPredicate condition) {
            if (condition != NoneLivingEntityPredicate.INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public LivingEntityPredicate createDefaultInstance() {
            return NoneLivingEntityPredicate.INSTANCE;
        }
    }
}
