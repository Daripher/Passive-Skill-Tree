package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.LootAmountModifierBonus;
import it.unimi.dsi.fastutil.floats.Float2FloatMap;
import it.unimi.dsi.fastutil.floats.Float2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class LootAmountModifierBonusHandler {
    public static @NotNull ObjectArrayList<ItemStack> modifyLoot(ObjectArrayList<ItemStack> defaultLoot, LootContext lootContext) {
        Player player = null;
        float lootAmountModifier = 0f;
        for (LootAmountModifierBonus.LootType lootType : LootAmountModifierBonus.LootType.values()) {
            if (!lootType.canAffect(lootContext)) {
                continue;
            }
            if (lootContext.hasParam(LootContextParams.TOOL)) {
                ItemStack tool = lootContext.getParam(LootContextParams.TOOL);
                Holder<Enchantment> silkTouch = lootContext.getLevel().registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(Enchantments.SILK_TOUCH);
                if (tool.getEnchantmentLevel(silkTouch) > 0) {
                    return defaultLoot;
                }
            }
            player = (Player) lootContext.getParam(lootType.getPlayerLootContextParam());
            lootAmountModifier = LootAmountModifierBonusHandler.getLootAmountModifier(player, lootType);
            break;
        }
        if (player == null || lootAmountModifier == 0f) {
            return defaultLoot;
        }
        RandomSource random = lootContext.getRandom();
        ObjectArrayList<ItemStack> modifiedLoot = new ObjectArrayList<>();
        float totalMultiplier = 1f + lootAmountModifier;
        if (totalMultiplier < 0f) {
            totalMultiplier = 0f;
        }
        if (totalMultiplier == 0f) {
            return modifiedLoot;
        }
        int guaranteedCopies = (int) totalMultiplier;
        float fractionalChance = totalMultiplier - guaranteedCopies;
        for (ItemStack stack : defaultLoot) {
            int finalCopies = guaranteedCopies;
            if (random.nextFloat() < fractionalChance) {
                finalCopies++;
            }
            for (int i = 0; i < finalCopies; i++) {
                modifiedLoot.add(stack.copy());
            }
        }
        return modifiedLoot;
    }

    public static float getLootAmountModifier(Player player, LootAmountModifierBonus.LootType lootType) {
        RandomSource random = player.getRandom();
        Float2FloatMap lootAmountModifierToChanceMap = getLootAmountModifierToChanceMap(player, lootType);
        if (lootAmountModifierToChanceMap.isEmpty()) {
            return 0f;
        }
        float amountModifier = 0f;
        for (Float2FloatMap.Entry entry : lootAmountModifierToChanceMap.float2FloatEntrySet()) {
            float chance = entry.getFloatValue();
            while (chance > 1) {
                amountModifier += entry.getFloatKey();
                chance--;
            }
            if (random.nextFloat() < chance) {
                amountModifier += entry.getFloatKey();
            }
        }
        return amountModifier;
    }

    @Nonnull
    public static Float2FloatMap getLootAmountModifierToChanceMap(Player player, LootAmountModifierBonus.LootType lootType) {
        Float2FloatMap multipliers = new Float2FloatOpenHashMap();
        List<LootAmountModifierBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, LootAmountModifierBonus.class);
        if (skillBonuses.isEmpty()) {
            return multipliers;
        }
        for (LootAmountModifierBonus bonus : skillBonuses) {
            if (bonus.getLootType() != lootType) {
                continue;
            }
            float amountModifier = bonus.getLootAmountModifier();
            float chance = bonus.getChance() + multipliers.getOrDefault(amountModifier, 0f);
            multipliers.put(amountModifier, chance);
        }
        return multipliers;
    }
}
