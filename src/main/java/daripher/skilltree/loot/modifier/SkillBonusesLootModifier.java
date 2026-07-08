package daripher.skilltree.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.skill.bonus.handler.LootAmountModifierBonusHandler;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SkillBonusesLootModifier extends LootModifier {
    public SkillBonusesLootModifier(LootItemCondition... conditionsIn) {
        super(conditionsIn);
    }

    public static final Supplier<Codec<SkillBonusesLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, SkillBonusesLootModifier::new)));

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> defaultLoot, LootContext lootContext) {
        for (LootItemCondition condition : conditions) {
            if (!condition.test(lootContext)) {
                return defaultLoot;
            }
        }
        return LootAmountModifierBonusHandler.modifyLoot(defaultLoot, lootContext);
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
