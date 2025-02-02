package daripher.skilltree.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SkillBonusesModifier extends LootModifier {
  public SkillBonusesModifier(LootItemCondition... conditionsIn) {
    super(conditionsIn);
  }

  public static final Supplier<Codec<SkillBonusesModifier>> CODEC =
      Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, SkillBonusesModifier::new)));

  @Override
  protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext lootContext) {
    for (LootItemCondition condition : this.conditions) {
      if (!condition.test(lootContext)) {
        return generatedLoot;
      }
    }
    Player player = null;
    float lootMultiplier = 0f;
    for (LootDuplicationBonus.LootType lootType : LootDuplicationBonus.LootType.values()) {
      if (lootType.canAffect(lootContext)) {
        player = (Player) lootContext.getParam(lootType.getPlayerLootContextParam());
        lootMultiplier = getLootMultiplier(player, lootType);
      }
    }
    if (player == null) {
      return generatedLoot;
    }
    if (lootMultiplier == 0f) return generatedLoot;
    RandomSource random = lootContext.getRandom();
    ObjectArrayList<ItemStack> newLoot = new ObjectArrayList<>();
    int copies = (int) lootMultiplier;
    lootMultiplier -= copies;
    copies++;
    for (ItemStack stack : generatedLoot) {
      int itemCopies = copies;
      if (random.nextFloat() < lootMultiplier) {
        itemCopies++;
      }
      for (int i = 0; i < itemCopies; i++) {
        newLoot.add(stack.copy());
      }
    }
    return newLoot;
  }

  private static float getLootMultiplier(Player player, LootDuplicationBonus.LootType lootType) {
    RandomSource random = player.getRandom();
    Map<Float, Float> multipliers = getLootMultipliers(player, lootType);
    float multiplier = 0f;
    for (Map.Entry<Float, Float> entry : multipliers.entrySet()) {
      float chance = entry.getValue();
      while (chance > 1) {
        multiplier += entry.getKey();
        chance--;
      }
      if (random.nextFloat() < chance) {
        multiplier += entry.getKey();
      }
    }
    return multiplier;
  }

  @Nonnull
  private static Map<Float, Float> getLootMultipliers(Player player, LootDuplicationBonus.LootType lootType) {
    Map<Float, Float> multipliers = new HashMap<>();
    for (LootDuplicationBonus bonus : SkillBonusHandler.getSkillBonuses(player, LootDuplicationBonus.class)) {
      if (bonus.getLootType() != lootType) continue;
      float chance = bonus.getChance() + multipliers.getOrDefault(bonus.getMultiplier(), 0f);
      multipliers.put(bonus.getMultiplier(), chance);
    }
    return multipliers;
  }

  @Override
  public Codec<? extends IGlobalLootModifier> codec() {
    return CODEC.get();
  }
}
