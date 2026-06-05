package daripher.skilltree.skill.requirement;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface SkillRequirement<T extends SkillRequirement<T>> extends Predicate<Player> {
    MutableComponent getTooltip();

    void addEditorWidgets(SkillTreeEditor editor, Consumer<T> consumer);

    Serializer getSerializer();

    T copy();

    default String getDescriptionId() {
        ResourceLocation id = PSTRegistries.SKILL_REQUIREMENTS.get().getKey(getSerializer());
        Objects.requireNonNull(id);
        return "skill_requirements.%s.%s".formatted(id.getNamespace(), id.getPath());
    }

    interface Serializer extends daripher.skilltree.data.serializers.Serializer<SkillRequirement<?>> {
        SkillRequirement<?> createDefaultInstance();
    }
}
