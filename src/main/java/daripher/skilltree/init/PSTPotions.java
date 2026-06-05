package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PSTPotions {
    public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, SkillTreeMod.MOD_ID);

    public static final RegistryObject<Potion> LIQUID_FIRE_1 = REGISTRY.register("liquid_fire_1", () -> new Potion(new MobEffectInstance(PSTMobEffects.LIQUID_FIRE.get())));
    public static final RegistryObject<Potion> LIQUID_FIRE_2 = REGISTRY.register("liquid_fire_2", () -> new Potion(new MobEffectInstance(PSTMobEffects.LIQUID_FIRE.get(), 0, 1)));
}
