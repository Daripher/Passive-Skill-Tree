package daripher.skilltree.skill.bonus.predicate.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.predicate.PSTLivingEntityPredicates;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public record FishingEntityPredicate() implements LivingEntityPredicate {
    @Override
    public boolean test(LivingEntity living) {
        return living instanceof Player player && player.fishing != null;
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        String key = getDescriptionId();
        MutableComponent targetDescription = Component.translatable("%s.target.%s".formatted(key, target.getName()));
        return Component.translatable(key, bonusTooltip, targetDescription);
    }

    @Override
    public LivingEntityPredicate.Serializer getSerializer() {
        return PSTLivingEntityPredicates.FISHING.get();
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
        return Objects.hash(getSerializer());
    }

    public static class Serializer implements LivingEntityPredicate.Serializer {
        @Override
        public LivingEntityPredicate deserialize(JsonObject json) throws JsonParseException {
            return new FishingEntityPredicate();
        }

        @Override
        public void serialize(JsonObject json, LivingEntityPredicate predicate) {
            validatePredicate(predicate);
        }

        @Override
        public LivingEntityPredicate deserialize(CompoundTag tag) {
            return new FishingEntityPredicate();
        }

        @Override
        public CompoundTag serialize(LivingEntityPredicate predicate) {
            validatePredicate(predicate);
            return new CompoundTag();
        }

        @Override
        public LivingEntityPredicate deserialize(FriendlyByteBuf buf) {
            return new FishingEntityPredicate();
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingEntityPredicate predicate) {
            validatePredicate(predicate);
        }

        private static void validatePredicate(LivingEntityPredicate predicate) {
            if (!(predicate instanceof FishingEntityPredicate)) {
                throw new IllegalArgumentException("Expected FishingEntityPredicate, got: " + predicate);
            }
        }

        @Override
        public LivingEntityPredicate createDefaultInstance() {
            return new FishingEntityPredicate();
        }
    }
}
