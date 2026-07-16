package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import daripher.skilltree.util.ForgeRegistries;

public class PSTPotions {
    public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, SkillTreeMod.MOD_ID);

    public static final DeferredHolder<Potion, ? extends Potion> LIQUID_FIRE_1 = REGISTRY.register("liquid_fire_1", () -> new Potion(new MobEffectInstance(PSTMobEffects.LIQUID_FIRE)));
    public static final DeferredHolder<Potion, ? extends Potion> LIQUID_FIRE_2 = REGISTRY.register("liquid_fire_2", () -> new Potion(new MobEffectInstance(PSTMobEffects.LIQUID_FIRE, 0, 1)));
}
