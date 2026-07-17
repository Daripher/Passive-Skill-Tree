package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.effect.LiquidFireEffect;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import daripher.skilltree.util.ForgeRegistries;

public class PSTMobEffects {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SkillTreeMod.MOD_ID);

    public static final DeferredHolder<MobEffect, ? extends MobEffect> LIQUID_FIRE = REGISTRY.register("liquid_fire", LiquidFireEffect::new);
}
