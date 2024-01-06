package daripher.skilltree.item.gem;

import daripher.skilltree.item.gem.bonus.GemBonusProvider;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import java.util.Comparator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record GemType(ResourceLocation id, Map<ItemCondition, GemBonusProvider> bonuses)
    implements Comparable<GemType> {
  @Nullable
  public GemBonusProvider getBonusProvider(ItemStack itemStack) {
    for (Map.Entry<ItemCondition, GemBonusProvider> entry : bonuses.entrySet()) {
      if (entry.getKey().met(itemStack)) {
        return entry.getValue();
      }
    }
    return null;
  }

  @Override
  public int compareTo(@NotNull GemType other) {
    return Comparator.comparing(GemType::id).compare(this, other);
  }
}
