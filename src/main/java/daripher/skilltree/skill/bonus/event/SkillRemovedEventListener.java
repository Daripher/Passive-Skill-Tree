package daripher.skilltree.skill.bonus.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTEventListeners;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public class SkillRemovedEventListener implements SkillEventListener {
    private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;

    public void onEvent(@Nonnull Player player, @Nonnull EventListenerBonus<?> skill) {
        skill.multiply(playerMultiplier.getValue(player)).applyEffect(player, player);
    }

    @Override
    public MutableComponent getTooltip(Component bonusTooltip) {
        MutableComponent eventTooltip = Component.translatable(getDescriptionId(), bonusTooltip);
        eventTooltip = playerMultiplier.getTooltip(eventTooltip, SkillBonus.Target.PLAYER);
        return eventTooltip;
    }

    @Override
    public SkillBonus.Target getTarget() {
        return SkillBonus.Target.PLAYER;
    }

    @Override
    public SkillEventListener.Serializer getSerializer() {
        return PSTEventListeners.SKILL_REMOVED.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SkillRemovedEventListener listener = (SkillRemovedEventListener) o;
        return Objects.equals(playerMultiplier, listener.playerMultiplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerMultiplier);
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
        editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, playerMultiplier)
                .setResponder(multiplier -> selectPlayerMultiplier(editor, consumer, multiplier))
                .setMenuInitFunc(() -> addPlayerMultiplierWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
        playerMultiplier.addEditorWidgets(editor, multiplier -> {
            setPlayerMultiplier(multiplier);
            consumer.accept(this);
        });
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<SkillEventListener> consumer, LivingMultiplier multiplier) {
        setPlayerMultiplier(multiplier);
        consumer.accept(this);
        editor.rebuildWidgets();
    }

    public SkillRemovedEventListener setPlayerMultiplier(LivingMultiplier playerMultiplier) {
        this.playerMultiplier = playerMultiplier;
        return this;
    }

    public static class Serializer implements SkillEventListener.Serializer {
        @Override
        public SkillEventListener deserialize(JsonObject json) throws JsonParseException {
            SkillRemovedEventListener listener = new SkillRemovedEventListener();
            listener.setPlayerMultiplier(SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier"));
            return listener;
        }

        @Override
        public void serialize(JsonObject json, SkillEventListener listener) {
            if (!(listener instanceof SkillRemovedEventListener aListener)) {
                throw new IllegalArgumentException();
            }
            SerializationHelper.serializeLivingMultiplier(json, aListener.playerMultiplier, "player_multiplier");
        }

        @Override
        public SkillEventListener deserialize(CompoundTag tag) {
            SkillRemovedEventListener listener = new SkillRemovedEventListener();
            listener.setPlayerMultiplier(SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier"));
            return listener;
        }

        @Override
        public CompoundTag serialize(SkillEventListener listener) {
            if (!(listener instanceof SkillRemovedEventListener aListener)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeLivingMultiplier(tag, aListener.playerMultiplier, "player_multiplier");
            return tag;
        }

        @Override
        public SkillEventListener deserialize(FriendlyByteBuf buf) {
            SkillRemovedEventListener listener = new SkillRemovedEventListener();
            listener.setPlayerMultiplier(NetworkHelper.readLivingMultiplier(buf));
            return listener;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillEventListener listener) {
            if (!(listener instanceof SkillRemovedEventListener aListener)) {
                throw new IllegalArgumentException();
            }
            NetworkHelper.writeLivingMultiplier(buf, aListener.playerMultiplier);
        }

        @Override
        public SkillEventListener createDefaultInstance() {
            return new SkillRemovedEventListener();
        }
    }
}
