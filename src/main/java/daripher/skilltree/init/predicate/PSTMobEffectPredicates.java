package daripher.skilltree.init.predicate;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectIdPredicate;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectPredicate;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectTypePredicate;
import daripher.skilltree.skill.bonus.predicate.effect.NoneMobEffectPredicate;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Objects;

public class PSTMobEffectPredicates {
    public static final ResourceLocation REGISTRY_ID = ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "mob_effect_conditions");
    public static final DeferredRegister<MobEffectPredicate.Serializer> REGISTRY = DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

    public static final DeferredHolder<MobEffectPredicate.Serializer, ? extends MobEffectPredicate.Serializer> NONE = REGISTRY.register("none", NoneMobEffectPredicate.Serializer::new);
    public static final DeferredHolder<MobEffectPredicate.Serializer, ? extends MobEffectPredicate.Serializer> EFFECT_CATEGORY = REGISTRY.register("effect_category", MobEffectTypePredicate.Serializer::new);
    public static final DeferredHolder<MobEffectPredicate.Serializer, ? extends MobEffectPredicate.Serializer> EFFECT_ID = REGISTRY.register("effect_id", MobEffectIdPredicate.Serializer::new);

    public static List<MobEffectPredicate> defaultInstances() {
        return PSTRegistries.MOB_EFFECT_PREDICATES.get().getValues().stream().map(MobEffectPredicate.Serializer::createDefaultInstance)
                .toList();
    }

    public static String getName(MobEffectPredicate condition) {
        ResourceLocation id = PSTRegistries.MOB_EFFECT_PREDICATES.get().getKey(condition.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
    }
}
