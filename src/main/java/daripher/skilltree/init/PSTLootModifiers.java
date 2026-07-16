package daripher.skilltree.init;

import com.mojang.serialization.MapCodec;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.loot.modifier.AddItemModifier;
import daripher.skilltree.loot.modifier.SkillBonusesLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class PSTLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SkillTreeMod.MOD_ID);

    static {
        REGISTRY.register("add_item", AddItemModifier.CODEC);
        REGISTRY.register("skill_bonuses", SkillBonusesLootModifier.CODEC);
    }

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
