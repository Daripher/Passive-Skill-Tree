package daripher.skilltree.skill.bonus.condition.item;

import daripher.skilltree.init.PSTRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface ItemCondition {
  boolean met(ItemStack stack);

  default String getDescriptionId() {
    ResourceLocation id = PSTRegistries.ITEM_CONDITIONS.get().getKey(getSerializer());
    assert id != null;
    return "item_condition.%s.%s".formatted(id.getNamespace(), id.getPath());
  }

  ItemCondition.Serializer getSerializer();

  interface Serializer extends daripher.skilltree.data.serializers.Serializer<ItemCondition> {}
}
