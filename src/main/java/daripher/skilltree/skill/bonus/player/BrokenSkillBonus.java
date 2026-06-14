package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Consumer;

public class BrokenSkillBonus implements SkillBonus<BrokenSkillBonus> {
    private final String errorMessage;

    public BrokenSkillBonus(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        return false;
    }

    @Override
    public SkillBonus<BrokenSkillBonus> merge(SkillBonus<?> other) {
        return this;
    }

    @Override
    public SkillBonus<BrokenSkillBonus> copy() {
        return this;
    }

    @Override
    public BrokenSkillBonus multiply(double multiplier) {
        return this;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.BROKEN.get();
    }

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        return Component.literal(errorMessage).withStyle(ChatFormatting.RED);
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<BrokenSkillBonus> consumer) {
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public BrokenSkillBonus deserialize(JsonObject json) throws JsonParseException {
            String errorMessage = json.get("error_message").getAsString();
            return new BrokenSkillBonus(errorMessage);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            BrokenSkillBonus validBonus = validateBonus(bonus);
            json.addProperty("error_message", validBonus.errorMessage);
        }

        @Override
        public BrokenSkillBonus deserialize(CompoundTag tag) {
            String errorMessage = tag.getString("error_message");
            return new BrokenSkillBonus(errorMessage);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            BrokenSkillBonus validBonus = validateBonus(bonus);
            CompoundTag tag = new CompoundTag();
            tag.putString("error_message", validBonus.errorMessage);
            return tag;
        }

        @Override
        public BrokenSkillBonus deserialize(FriendlyByteBuf buf) {
            String errorMessage = buf.readUtf();
            return new BrokenSkillBonus(errorMessage);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            BrokenSkillBonus validBonus = validateBonus(bonus);
            buf.writeUtf(validBonus.errorMessage);
        }

        private BrokenSkillBonus validateBonus(SkillBonus<?> bonus) {
            if (!(bonus instanceof BrokenSkillBonus validBonus)) {
                throw new IllegalArgumentException();
            }
            return validBonus;
        }

        @Override
        public BrokenSkillBonus createDefaultInstance() {
            return null;
        }
    }
}
