package daripher.skilltree.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AddItemModifier extends LootModifier {
  private final ItemStack itemStack;

  public AddItemModifier(ItemStack item, LootItemCondition... conditionsIn) {
    super(conditionsIn);
    this.itemStack = item;
  }

  public static final Supplier<Codec<AddItemModifier>> CODEC =
      Suppliers.memoize(
          () ->
              RecordCodecBuilder.create(
                  inst ->
                      codecStart(inst)
                          .and(ItemStack.CODEC.fieldOf("item").forGetter(m -> m.itemStack))
                          .apply(
                              inst,
                              (conditionsIn, item) -> new AddItemModifier(item, conditionsIn))));

  @Override
  protected @NotNull ObjectArrayList<ItemStack> doApply(
      ObjectArrayList<ItemStack> generatedLoot, LootContext lootContext) {
    for (LootItemCondition condition : this.conditions) {
      if (!condition.test(lootContext)) {
        return generatedLoot;
      }
    }
    generatedLoot.add(this.itemStack);
    return generatedLoot;
  }

  @Override
  public Codec<? extends IGlobalLootModifier> codec() {
    return CODEC.get();
  }
}
