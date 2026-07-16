package daripher.skilltree.skill.bonus.item;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public interface ItemBonus<T extends ItemBonus<T>> {
    boolean canMerge(ItemBonus<?> other);

    T merge(ItemBonus<?> other);

    T copy();

    T multiply(double multiplier);

    ItemBonus.Serializer getSerializer();

    default String getDescriptionId() {
        ResourceLocation id = PSTRegistries.ITEM_BONUSES.get().getKey(getSerializer());
        Objects.requireNonNull(id);
        return "item_bonus.%s.%s".formatted(id.getNamespace(), id.getPath());
    }

    List<MutableComponent> getFullTooltip();

    boolean isPositive();

    void addEditorWidgets(SkillTreeEditor editor, Consumer<T> consumer);

    interface Serializer extends daripher.skilltree.data.serializers.Serializer<ItemBonus<?>> {
        ItemBonus<?> createDefaultInstance();
    }
}
