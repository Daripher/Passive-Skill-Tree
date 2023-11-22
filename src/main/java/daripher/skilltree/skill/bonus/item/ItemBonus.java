package daripher.skilltree.skill.bonus.item;

import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface ItemBonus<T extends ItemBonus<T>> {
  default void itemCrafted(ItemStack stack) {}

  boolean canMerge(ItemBonus<?> other);

  default boolean sameBonus(ItemBonus<?> other) {
    return canMerge(other);
  }

  T merge(ItemBonus<?> other);

  T copy();

  T multiply(double multiplier);

  ItemBonus.Serializer getSerializer();

  default String getDescriptionId() {
    ResourceLocation id = PSTRegistries.ITEM_BONUSES.get().getKey(getSerializer());
    assert id != null;
    return "item_bonus.%s.%s".formatted(id.getNamespace(), id.getPath());
  }

  MutableComponent getTooltip();

  default MutableComponent getAdvancedTooltip() {
    return Component.empty();
  }

  void addEditorWidgets(SkillTreeEditor editor, int row);

  interface Serializer extends daripher.skilltree.data.serializers.Serializer<ItemBonus<?>> {}
}
