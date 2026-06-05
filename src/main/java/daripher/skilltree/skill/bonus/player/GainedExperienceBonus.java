package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public final class GainedExperienceBonus implements SkillBonus<GainedExperienceBonus> {
    private ExperienceSource experienceSource;
    private float multiplier;
    private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;

    public GainedExperienceBonus(float multiplier, ExperienceSource source) {
        this.multiplier = multiplier;
        this.experienceSource = source;
    }

    public GainedExperienceBonus(float multiplier, ExperienceSource source, LivingMultiplier playerMultiplier) {
        this.multiplier = multiplier;
        this.experienceSource = source;
        this.playerMultiplier = playerMultiplier;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.GAINED_EXPERIENCE.get();
    }

    @Override
    public GainedExperienceBonus copy() {
        return new GainedExperienceBonus(multiplier, experienceSource, playerMultiplier);
    }

    @Override
    public GainedExperienceBonus multiply(double multiplier) {
        this.multiplier = (float) (this.multiplier * multiplier);
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof GainedExperienceBonus otherBonus)) {
            return false;
        }
        if (!Objects.equals(otherBonus.experienceSource, this.experienceSource)) {
            return false;
        }
        return Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier);
    }

    @Override
    public SkillBonus<GainedExperienceBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof GainedExperienceBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        return new GainedExperienceBonus(otherBonus.multiplier + this.multiplier, experienceSource, playerMultiplier);
    }

    @Override
    public MutableComponent getTooltip() {
        Component sourceDescription = Component.translatable(experienceSource.getDescriptionId());
        MutableComponent tooltip = Component.translatable(getDescriptionId(), sourceDescription);
        tooltip = TooltipHelper.getSkillBonusTooltip(tooltip, multiplier, AttributeModifier.Operation.MULTIPLY_BASE);
        tooltip = playerMultiplier.getTooltip(tooltip, Target.PLAYER);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }

    @Override
    public boolean isPositive() {
        return multiplier > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<GainedExperienceBonus> consumer) {
        editor.addLabel(110, 0, "Multiplier", ChatFormatting.GOLD);
        editor.addLabel(0, 0, "Source", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(110, 0, 90, 14, multiplier).setNumericResponder(value -> selectMultiplier(consumer, value));
        editor.addSelection(0, 0, 80, 1, experienceSource).setNameGetter(ExperienceSource::getFormattedName)
                .setResponder(experienceSource -> selectExperienceSource(consumer, experienceSource));
        editor.increaseHeight(29);
        editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, playerMultiplier)
                .setResponder(multiplier -> selectPlayerMultiplier(editor, consumer, multiplier))
                .setMenuInitFunc(() -> addPlayerMultiplierWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<GainedExperienceBonus> consumer, LivingMultiplier playerMultiplier) {
        setPlayerMultiplier(playerMultiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectExperienceSource(Consumer<GainedExperienceBonus> consumer, ExperienceSource experienceSource) {
        setExpericenSource(experienceSource);
        consumer.accept(this.copy());
    }

    private void selectMultiplier(Consumer<GainedExperienceBonus> consumer, Double value) {
        setMultiplier(value.floatValue());
        consumer.accept(this.copy());
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<GainedExperienceBonus> consumer) {
        playerMultiplier.addEditorWidgets(editor, m -> {
            setPlayerMultiplier(m);
            consumer.accept(this.copy());
        });
    }


    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public void setExpericenSource(ExperienceSource experienceSource) {
        this.experienceSource = experienceSource;
    }

    public SkillBonus<?> setPlayerMultiplier(LivingMultiplier playerMultiplier) {
        this.playerMultiplier = playerMultiplier;
        return this;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public ExperienceSource getSource() {
        return experienceSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GainedExperienceBonus that = (GainedExperienceBonus) o;
        if (Float.compare(multiplier, that.multiplier) != 0) {
            return false;
        }
        return experienceSource == that.experienceSource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(experienceSource, multiplier);
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public GainedExperienceBonus deserialize(JsonObject json) throws JsonParseException {
            float multiplier = SerializationHelper.getElement(json, "multiplier").getAsFloat();
            ExperienceSource experienceSource = ExperienceSource.byName(json.get("experience_source").getAsString());
            LivingMultiplier playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            return new GainedExperienceBonus(multiplier, experienceSource, playerMultiplier);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof GainedExperienceBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("multiplier", aBonus.multiplier);
            json.addProperty("experience_source", aBonus.experienceSource.name);
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
        }

        @Override
        public GainedExperienceBonus deserialize(CompoundTag tag) {
            float multiplier = tag.getFloat("multiplier");
            ExperienceSource experienceSource = ExperienceSource.byName(tag.getString("experience_source"));
            LivingMultiplier playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            return new GainedExperienceBonus(multiplier, experienceSource, playerMultiplier);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof GainedExperienceBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putFloat("multiplier", aBonus.multiplier);
            tag.putString("experience_source", aBonus.experienceSource.name);
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            return tag;
        }

        @Override
        public GainedExperienceBonus deserialize(FriendlyByteBuf buf) {
            float multiplier = buf.readFloat();
            ExperienceSource experienceSource = ExperienceSource.values()[buf.readInt()];
            LivingMultiplier playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            return new GainedExperienceBonus(multiplier, experienceSource, playerMultiplier);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof GainedExperienceBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeFloat(aBonus.multiplier);
            buf.writeInt(aBonus.experienceSource.ordinal());
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new GainedExperienceBonus(0.25f, ExperienceSource.MOBS);
        }
    }

    public enum ExperienceSource {
        MOBS("mobs"), FISHING("fishing"), ORE("ore");

        final String name;

        ExperienceSource(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Component getFormattedName() {
            return Component.literal(getName().substring(0, 1).toUpperCase(Locale.ROOT) + getName().substring(1));
        }

        public static ExperienceSource byName(String name) {
            for (ExperienceSource type : values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
            return MOBS;
        }

        public String getDescriptionId() {
            return "experience.source." + getName();
        }
    }
}
