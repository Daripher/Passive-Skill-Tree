package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.NoneLivingEntityPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class JumpHeightBonus implements SkillBonus<JumpHeightBonus> {
    private @Nonnull LivingEntityPredicate playerCondition;
    private float multiplier;

    public JumpHeightBonus(@Nonnull LivingEntityPredicate playerCondition, float multiplier) {
        this.playerCondition = playerCondition;
        this.multiplier = multiplier;
    }

    public JumpHeightBonus(float multiplier) {
        this(NoneLivingEntityPredicate.INSTANCE, multiplier);
    }

    public float getJumpHeightMultiplier(Player player) {
        if (!playerCondition.test(player)) {
            return 0f;
        }
        return multiplier;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.JUMP_HEIGHT.get();
    }

    @Override
    public JumpHeightBonus copy() {
        return new JumpHeightBonus(playerCondition, multiplier);
    }

    @Override
    public JumpHeightBonus multiply(double multiplier) {
        this.multiplier = (float) (this.multiplier * multiplier);
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof JumpHeightBonus otherBonus)) {
            return false;
        }
        return Objects.equals(otherBonus.playerCondition, this.playerCondition);
    }

    @Override
    public SkillBonus<JumpHeightBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof JumpHeightBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        return new JumpHeightBonus(playerCondition, otherBonus.multiplier + this.multiplier);
    }

    @Override
    public MutableComponent getTooltip() {
        MutableComponent tooltip = TooltipHelper.getSkillBonusTooltip(getDescriptionId(), multiplier, AttributeModifier.Operation.MULTIPLY_BASE);
        tooltip = playerCondition.getTooltip(tooltip, Target.PLAYER);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return multiplier > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<JumpHeightBonus> consumer) {
        editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, multiplier).setNumericResponder(value -> selectMultiplier(consumer, value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, playerCondition).setResponder(condition -> selectPlayerCondition(editor, consumer, condition))
                .setMenuInitFunc(() -> addPlayerConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<JumpHeightBonus> consumer) {
        playerCondition.addEditorWidgets(editor, condition -> {
            setPlayerCondition(condition);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<JumpHeightBonus> consumer, LivingEntityPredicate condition) {
        setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectMultiplier(Consumer<JumpHeightBonus> consumer, Double value) {
        setMultiplier(value.floatValue());
        consumer.accept(this.copy());
    }

    public void setPlayerCondition(@Nonnull LivingEntityPredicate playerCondition) {
        this.playerCondition = playerCondition;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public float getMultiplier() {
        return multiplier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        JumpHeightBonus that = (JumpHeightBonus) obj;
        if (!Objects.equals(this.playerCondition, that.playerCondition)) {
            return false;
        }
        return this.multiplier == that.multiplier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerCondition, multiplier);
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public JumpHeightBonus deserialize(JsonObject json) throws JsonParseException {
            LivingEntityPredicate condition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            float multiplier = SerializationHelper.getElement(json, "multiplier").getAsFloat();
            return new JumpHeightBonus(condition, multiplier);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof JumpHeightBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
            json.addProperty("multiplier", aBonus.multiplier);
        }

        @Override
        public JumpHeightBonus deserialize(CompoundTag tag) {
            LivingEntityPredicate condition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            float multiplier = tag.getFloat("multiplier");
            return new JumpHeightBonus(condition, multiplier);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof JumpHeightBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            tag.putFloat("multiplier", aBonus.multiplier);
            return tag;
        }

        @Override
        public JumpHeightBonus deserialize(FriendlyByteBuf buf) {
            return new JumpHeightBonus(NetworkHelper.readLivingCondition(buf), buf.readFloat());
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof JumpHeightBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
            buf.writeFloat(aBonus.multiplier);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new JumpHeightBonus(0.1f);
        }
    }
}
