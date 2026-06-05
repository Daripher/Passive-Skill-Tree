package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.effect.LiquidFireEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PSTMobEffects {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SkillTreeMod.MOD_ID);

    public static final RegistryObject<MobEffect> LIQUID_FIRE = REGISTRY.register("liquid_fire", LiquidFireEffect::new);
}
