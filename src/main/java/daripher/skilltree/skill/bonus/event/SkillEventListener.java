package daripher.skilltree.skill.bonus.event;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Consumer;

public interface SkillEventListener {
    default String getDescriptionId() {
        ResourceLocation id = PSTRegistries.EVENT_LISTENERS.get().getKey(getSerializer());
        Objects.requireNonNull(id);
        return "event_listener.%s.%s".formatted(id.getNamespace(), id.getPath());
    }

    default MutableComponent getTooltip(Component bonusTooltip) {
        return Component.translatable(getDescriptionId(), bonusTooltip);
    }

    SkillBonus.Target getTarget();

    Serializer getSerializer();

    void addEditorWidgets(SkillTreeEditor editor, Consumer<SkillEventListener> consumer);

    interface Serializer extends daripher.skilltree.data.serializers.Serializer<SkillEventListener> {
        SkillEventListener createDefaultInstance();
    }
}
