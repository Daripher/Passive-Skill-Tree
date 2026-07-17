package daripher.skilltree.init;

import daripher.skilltree.init.predicate.PSTDamagePredicates;
import daripher.skilltree.init.predicate.PSTEnchantmentPredicates;
import daripher.skilltree.init.predicate.PSTItemPredicates;
import daripher.skilltree.init.predicate.PSTLivingEntityPredicates;
import daripher.skilltree.init.predicate.PSTMobEffectPredicates;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.predicate.damage.DamageCondition;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectPredicate;
import daripher.skilltree.skill.bonus.predicate.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.requirement.SkillRequirement;
import daripher.skilltree.util.ForgeRegistries.ForgeRegistry;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PSTRegistries {
    public static final Supplier<ForgeRegistry<SkillBonus.Serializer>> SKILL_BONUSES =
            createRegistry(PSTSkillBonuses.REGISTRY);
    public static final Supplier<ForgeRegistry<LivingMultiplier.Serializer>> LIVING_MULTIPLIERS =
            createRegistry(PSTLivingMultipliers.REGISTRY);
    public static final Supplier<ForgeRegistry<LivingEntityPredicate.Serializer>> LIVING_CONDITIONS =
            createRegistry(PSTLivingEntityPredicates.REGISTRY);
    public static final Supplier<ForgeRegistry<DamageCondition.Serializer>> DAMAGE_CONDITIONS =
            createRegistry(PSTDamagePredicates.REGISTRY);
    public static final Supplier<ForgeRegistry<ItemStackPredicate.Serializer>> ITEM_CONDITIONS =
            createRegistry(PSTItemPredicates.REGISTRY);
    public static final Supplier<ForgeRegistry<EnchantmentCondition.Serializer>>
            ENCHANTMENT_CONDITIONS = createRegistry(PSTEnchantmentPredicates.REGISTRY);
    public static final Supplier<ForgeRegistry<SkillEventListener.Serializer>> EVENT_LISTENERS =
            createRegistry(PSTEventListeners.REGISTRY);
    public static final Supplier<ForgeRegistry<FloatFunction.Serializer>> FLOAT_FUNCTIONS =
            createRegistry(PSTFloatFunctions.REGISTRY);
    public static final Supplier<ForgeRegistry<SkillRequirement.Serializer>> SKILL_REQUIREMENTS =
            createRegistry(PSTSkillRequirements.REGISTRY);
    public static final Supplier<ForgeRegistry<ItemBonus.Serializer>> ITEM_BONUSES =
            createRegistry(PSTItemBonuses.REGISTRY);
    public static final Supplier<ForgeRegistry<MobEffectPredicate.Serializer>> MOB_EFFECT_PREDICATES =
            createRegistry(PSTMobEffectPredicates.REGISTRY);

    public static void bootstrap() {
    }

    private static <T> Supplier<ForgeRegistry<T>> createRegistry(
            DeferredRegister<T> deferredRegister) {
        Registry<T> registry = deferredRegister.makeRegistry(builder -> {
        });
        return () -> new ForgeRegistry<>(registry);
    }
}
