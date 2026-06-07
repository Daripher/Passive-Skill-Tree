package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.predicate.damage.DamageCondition;
import daripher.skilltree.skill.bonus.predicate.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PSTRegistries {
    public static final Supplier<IForgeRegistry<SkillBonus.Serializer>> SKILL_BONUSES = PSTSkillBonuses.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<LivingMultiplier.Serializer>> LIVING_MULTIPLIERS = PSTLivingMultipliers.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<LivingEntityPredicate.Serializer>> LIVING_CONDITIONS = PSTLivingEntityPredicates.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<DamageCondition.Serializer>> DAMAGE_CONDITIONS = PSTDamageConditions.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<ItemStackPredicate.Serializer>> ITEM_CONDITIONS = PSTItemConditions.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<EnchantmentCondition.Serializer>> ENCHANTMENT_CONDITIONS = PSTEnchantmentConditions.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<SkillEventListener.Serializer>> EVENT_LISTENERS = PSTEventListeners.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<FloatFunction.Serializer>> FLOAT_FUNCTIONS = PSTFloatFunctions.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<SkillRequirement.Serializer>> SKILL_REQUIREMENTS = PSTSkillRequirements.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<ItemBonus.Serializer>> ITEM_BONUSES = PSTItemBonuses.REGISTRY.makeRegistry(RegistryBuilder::new);

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        createRegistry(event, PSTSkillBonuses.REGISTRY_ID);
        createRegistry(event, PSTLivingMultipliers.REGISTRY_ID);
        createRegistry(event, PSTLivingEntityPredicates.REGISTRY_ID);
        createRegistry(event, PSTDamageConditions.REGISTRY_ID);
        createRegistry(event, PSTItemConditions.REGISTRY_ID);
        createRegistry(event, PSTEnchantmentConditions.REGISTRY_ID);
        createRegistry(event, PSTEventListeners.REGISTRY_ID);
        createRegistry(event, PSTFloatFunctions.REGISTRY_ID);
        createRegistry(event, PSTSkillRequirements.REGISTRY_ID);
        createRegistry(event, PSTItemBonuses.REGISTRY_ID);
    }

    private static <T> void createRegistry(NewRegistryEvent event, ResourceLocation id) {
        event.create(new RegistryBuilder<T>().setName(id));
    }
}
