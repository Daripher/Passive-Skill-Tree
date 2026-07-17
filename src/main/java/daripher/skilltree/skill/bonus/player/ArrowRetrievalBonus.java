package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Objects;
import java.util.function.Consumer;

public final class ArrowRetrievalBonus implements SkillBonus<ArrowRetrievalBonus> {
    private float chance;

    public ArrowRetrievalBonus(float chance) {
        this.chance = chance;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.ARROW_RETRIEVAL.get();
    }

    @Override
    public ArrowRetrievalBonus copy() {
        return new ArrowRetrievalBonus(chance);
    }

    @Override
    public ArrowRetrievalBonus multiply(double multiplier) {
        return new ArrowRetrievalBonus((float) (getChance() * multiplier));
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        return other instanceof ArrowRetrievalBonus;
    }

    @Override
    public SkillBonus<ArrowRetrievalBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof ArrowRetrievalBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        return new ArrowRetrievalBonus(otherBonus.chance + this.chance);
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        return TooltipHelper.getSkillBonusTooltip(getDescriptionId(), chance, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                .withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return chance > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<ArrowRetrievalBonus> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, chance).setNumericResponder(value -> selectChance(consumer, value));
        editor.increaseHeight(19);
    }

    private void selectChance(Consumer<ArrowRetrievalBonus> consumer, Double value) {
        setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public float getChance() {
        return chance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ArrowRetrievalBonus that = (ArrowRetrievalBonus) obj;
        return Float.floatToIntBits(this.chance) == Float.floatToIntBits(that.chance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chance);
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public ArrowRetrievalBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
            return new ArrowRetrievalBonus(chance);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof ArrowRetrievalBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("chance", aBonus.chance);
        }

        @Override
        public ArrowRetrievalBonus deserialize(CompoundTag tag) {
            float chance = tag.getFloat("chance");
            return new ArrowRetrievalBonus(chance);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof ArrowRetrievalBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("chance", aBonus.chance);
            return tag;
        }

        @Override
        public ArrowRetrievalBonus deserialize(FriendlyByteBuf buf) {
            return new ArrowRetrievalBonus(buf.readFloat());
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof ArrowRetrievalBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.chance);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new ArrowRetrievalBonus(0.05f);
        }
    }
}
