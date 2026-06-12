package daripher.skilltree.skill.bonus.predicate.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.predicate.PSTLivingEntityPredicates;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.function.Consumer;

public final class CrouchingEntityPredicate implements LivingEntityPredicate {
    private boolean reverseLogic;

    public CrouchingEntityPredicate(boolean reverseLogic) {
        this.reverseLogic = reverseLogic;
    }

    @Override
    public boolean test(LivingEntity living) {
        return living.isCrouching() ^ reverseLogic;
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        String key = getDescriptionId();
        MutableComponent targetDescription = Component.translatable("%s%s.target.%s".formatted(key, reverseLogic ? ".reverse" : "", target.getName()));
        return Component.translatable(key, bonusTooltip, targetDescription);
    }

    @Override
    public LivingEntityPredicate.Serializer getSerializer() {
        return PSTLivingEntityPredicates.CROUCHING.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer) {
        editor.addLabel(0, 0, "Reverse Logic", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addCheckBox(0, 0, reverseLogic).setResponder(value -> setReverseLogic(value, consumer));
        editor.increaseHeight(19);
    }

    public void setReverseLogic(boolean reverseLogic, Consumer<LivingEntityPredicate> consumer) {
        this.reverseLogic = reverseLogic;
        consumer.accept(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CrouchingEntityPredicate that = (CrouchingEntityPredicate) o;
        return reverseLogic == that.reverseLogic;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reverseLogic);
    }

    public static class Serializer implements LivingEntityPredicate.Serializer {
        @Override
        public LivingEntityPredicate deserialize(JsonObject json) throws JsonParseException {
            boolean reverseLogic = json.has("reverse_logic") && json.get("reverse_logic").getAsBoolean();
            return new CrouchingEntityPredicate(reverseLogic);
        }

        @Override
        public void serialize(JsonObject json, LivingEntityPredicate predicate) {
            CrouchingEntityPredicate validPredicate = validatePredicate(predicate);
            json.addProperty("reverse_logic", validPredicate.reverseLogic);
        }

        @Override
        public LivingEntityPredicate deserialize(CompoundTag tag) {
            boolean reverseLogic = tag.contains("reverse_logic") && tag.getBoolean("reverse_logic");
            return new CrouchingEntityPredicate(reverseLogic);
        }

        @Override
        public CompoundTag serialize(LivingEntityPredicate predicate) {
            CrouchingEntityPredicate validPredicate = validatePredicate(predicate);
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putBoolean("reverse_logic", validPredicate.reverseLogic);
            return compoundTag;
        }

        @Override
        public LivingEntityPredicate deserialize(FriendlyByteBuf buf) {
            boolean reverseLogic = buf.readBoolean();
            return new CrouchingEntityPredicate(reverseLogic);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingEntityPredicate predicate) {
            CrouchingEntityPredicate validPredicate = validatePredicate(predicate);
            buf.writeBoolean(validPredicate.reverseLogic);
        }

        private static CrouchingEntityPredicate validatePredicate(LivingEntityPredicate predicate) {
            if (!(predicate instanceof CrouchingEntityPredicate validPredicate)) {
                throw new IllegalArgumentException("Expected CrouchingEntityPredicate, got: " + predicate);
            }
            return validPredicate;
        }

        @Override
        public LivingEntityPredicate createDefaultInstance() {
            return new CrouchingEntityPredicate(false);
        }
    }
}
