package daripher.skilltree.skill.requirement;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import java.util.function.Consumer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public interface SkillRequirement<T extends SkillRequirement<T>> {

  boolean isRequirementMet(Player player);

  MutableComponent getTooltip();

  void addEditorWidgets(SkillTreeEditor editor, Consumer<T> consumer);

  Serializer getSerializer();

  T copy();

  interface Serializer extends daripher.skilltree.data.serializers.Serializer<SkillRequirement<?>> {
    SkillRequirement<?> createDefaultInstance();
  }
}
