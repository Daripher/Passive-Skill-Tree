package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.effect.AttackSpeedBonusEffect;
import daripher.skilltree.effect.CritChanceBonusEffect;
import daripher.skilltree.effect.DamageBonusEffect;
import daripher.skilltree.effect.FoodBonusEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SkillTreeEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SkillTreeMod.MOD_ID);

	public static final RegistryObject<MobEffect> ATTACK_SPEED_BONUS = REGISTRY.register("attack_speed_bonus", AttackSpeedBonusEffect::new);
	public static final RegistryObject<MobEffect> DAMAGE_BONUS = REGISTRY.register("damage_bonus", DamageBonusEffect::new);
	public static final RegistryObject<MobEffect> CRIT_CHANCE_BONUS = REGISTRY.register("crit_chance_bonus", CritChanceBonusEffect::new);
	public static final RegistryObject<MobEffect> DELICACY = REGISTRY.register("delicacy", FoodBonusEffect::new);
	public static final RegistryObject<MobEffect> GOURMET = REGISTRY.register("gourmet", FoodBonusEffect::new);
}
