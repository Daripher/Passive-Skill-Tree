package daripher.skilltree.skill.bonus.predicate.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.predicate.PSTItemPredicates;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public enum NoneItemStackPredicate implements ItemStackPredicate {
    INSTANCE;

    @Override
    public boolean test(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStackPredicate.Serializer getSerializer() {
        return PSTItemPredicates.NONE.get();
    }

    public static class Serializer implements ItemStackPredicate.Serializer {
        @Override
        public ItemStackPredicate deserialize(JsonObject json) throws JsonParseException {
            return NoneItemStackPredicate.INSTANCE;
        }

        @Override
        public void serialize(JsonObject json, ItemStackPredicate condition) {
            if (condition != NoneItemStackPredicate.INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public ItemStackPredicate deserialize(CompoundTag tag) {
            return NoneItemStackPredicate.INSTANCE;
        }

        @Override
        public CompoundTag serialize(ItemStackPredicate condition) {
            if (condition != NoneItemStackPredicate.INSTANCE) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public ItemStackPredicate deserialize(FriendlyByteBuf buf) {
            return NoneItemStackPredicate.INSTANCE;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemStackPredicate condition) {
            if (condition != NoneItemStackPredicate.INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public ItemStackPredicate createDefaultInstance() {
            return NoneItemStackPredicate.INSTANCE;
        }
    }
}
