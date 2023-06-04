package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.effect.CritDamageBonusEffect;
import daripher.skilltree.effect.DamageBonusEffect;
import daripher.skilltree.effect.LifeRegenerationBonusEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SkillTreeEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SkillTreeMod.MOD_ID);

	public static final RegistryObject<MobEffect> DAMAGE_BONUS = REGISTRY.register("damage_bonus", DamageBonusEffect::new);
	public static final RegistryObject<MobEffect> CRIT_DAMAGE_BONUS = REGISTRY.register("crit_damage_bonus", CritDamageBonusEffect::new);
	public static final RegistryObject<MobEffect> LIFE_REGENERATION_BONUS = REGISTRY.register("life_regeneration_bonus", LifeRegenerationBonusEffect::new);
}
