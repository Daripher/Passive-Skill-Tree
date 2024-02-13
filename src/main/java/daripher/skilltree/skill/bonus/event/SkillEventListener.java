package daripher.skilltree.skill.bonus.event;

import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.init.PSTRegistries;
import java.util.Objects;
import java.util.function.Consumer;

import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

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

  void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<SkillEventListener> consumer);

  interface Serializer extends daripher.skilltree.data.serializers.Serializer<SkillEventListener> {
    SkillEventListener createDefaultInstance();
  }
}
