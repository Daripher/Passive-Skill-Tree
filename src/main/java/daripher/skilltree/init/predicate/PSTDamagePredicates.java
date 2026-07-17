package daripher.skilltree.init.predicate;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.predicate.damage.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Objects;

public class PSTDamagePredicates {
    public static final ResourceLocation REGISTRY_ID = ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "damage_conditions");
    public static final DeferredRegister<DamageCondition.Serializer> REGISTRY = DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

    public static final DeferredHolder<DamageCondition.Serializer, ? extends DamageCondition.Serializer> NONE = REGISTRY.register("none", NoneDamageCondition.Serializer::new);
    public static final DeferredHolder<DamageCondition.Serializer, ? extends DamageCondition.Serializer> PROJECTILE = REGISTRY.register("projectile", ProjectileDamageCondition.Serializer::new);
    public static final DeferredHolder<DamageCondition.Serializer, ? extends DamageCondition.Serializer> MELEE = REGISTRY.register("melee", MeleeDamageCondition.Serializer::new);
    public static final DeferredHolder<DamageCondition.Serializer, ? extends DamageCondition.Serializer> MAGIC = REGISTRY.register("magic", MagicDamageCondition.Serializer::new);
    public static final DeferredHolder<DamageCondition.Serializer, ? extends DamageCondition.Serializer> FALL = REGISTRY.register("fall", FallDamageCondition.Serializer::new);
    public static final DeferredHolder<DamageCondition.Serializer, ? extends DamageCondition.Serializer> FIRE = REGISTRY.register("fire", FireDamageCondition.Serializer::new);
    public static final DeferredHolder<DamageCondition.Serializer, ? extends DamageCondition.Serializer> POISON = REGISTRY.register("poison", PoisonDamageCondition.Serializer::new);
    public static final DeferredHolder<DamageCondition.Serializer, ? extends DamageCondition.Serializer> THORNS = REGISTRY.register("thorns", ThornsDamageCondition.Serializer::new);

    public static List<DamageCondition> conditionsList() {
        return PSTRegistries.DAMAGE_CONDITIONS.get().getValues().stream().map(DamageCondition.Serializer::createDefaultInstance).toList();
    }

    public static String getName(DamageCondition condition) {
        ResourceLocation id = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(condition.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
    }
}
