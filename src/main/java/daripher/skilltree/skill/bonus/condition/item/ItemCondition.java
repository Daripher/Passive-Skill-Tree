package daripher.skilltree.skill.bonus.condition.item;

import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTRegistries;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface ItemCondition {
  boolean met(ItemStack stack);

  default String getDescriptionId() {
    ResourceLocation id = PSTRegistries.ITEM_CONDITIONS.get().getKey(getSerializer());
    Objects.requireNonNull(id);
    return "item_condition.%s.%s".formatted(id.getNamespace(), id.getPath());
  }

  default Component getTooltip() {
    return Component.translatable(getDescriptionId());
  }

  default Component getTooltip(String type) {
    return TooltipHelper.getOptionalTooltip(getDescriptionId(), type);
  }

  ItemCondition.Serializer getSerializer();

  default void addEditorWidgets(SkillTreeEditorScreen editor, Consumer<ItemCondition> consumer) {}

  interface Serializer extends daripher.skilltree.data.serializers.Serializer<ItemCondition> {
    ItemCondition createDefaultInstance();
  }
}
