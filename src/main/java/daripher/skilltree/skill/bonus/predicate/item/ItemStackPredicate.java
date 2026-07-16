package daripher.skilltree.skill.bonus.predicate.item;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface ItemStackPredicate extends Predicate<ItemStack> {
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

    ItemStackPredicate.Serializer getSerializer();

    default void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemStackPredicate> consumer) {
    }

    interface Serializer extends daripher.skilltree.data.serializers.Serializer<ItemStackPredicate> {
        ItemStackPredicate createDefaultInstance();
    }
}
