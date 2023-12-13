package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PSTRegistries {
  public static final Supplier<IForgeRegistry<SkillBonus.Serializer>> SKILL_BONUSES =
      PSTSkillBonuses.REGISTRY.makeRegistry(RegistryBuilder::new);
  public static final Supplier<IForgeRegistry<LivingMultiplier.Serializer>> LIVING_MULTIPLIERS =
      PSTLivingMultipliers.REGISTRY.makeRegistry(RegistryBuilder::new);
  public static final Supplier<IForgeRegistry<LivingCondition.Serializer>> LIVING_CONDITIONS =
      PSTLivingConditions.REGISTRY.makeRegistry(RegistryBuilder::new);
  public static final Supplier<IForgeRegistry<DamageCondition.Serializer>> DAMAGE_CONDITIONS =
      PSTDamageConditions.REGISTRY.makeRegistry(RegistryBuilder::new);
  public static final Supplier<IForgeRegistry<ItemCondition.Serializer>> ITEM_CONDITIONS =
      PSTItemConditions.REGISTRY.makeRegistry(RegistryBuilder::new);
  public static final Supplier<IForgeRegistry<ItemBonus.Serializer>> ITEM_BONUSES =
      PSTItemBonuses.REGISTRY.makeRegistry(RegistryBuilder::new);
  public static final Supplier<IForgeRegistry<EnchantmentCondition.Serializer>> ENCHANTMENT_CONDITIONS =
      PSTEnchantmentConditions.REGISTRY.makeRegistry(RegistryBuilder::new);

  @SubscribeEvent
  public static void registerRegistries(NewRegistryEvent event) {
    createRegistry(event, PSTSkillBonuses.REGISTRY_ID);
    createRegistry(event, PSTLivingMultipliers.REGISTRY_ID);
    createRegistry(event, PSTLivingConditions.REGISTRY_ID);
    createRegistry(event, PSTDamageConditions.REGISTRY_ID);
    createRegistry(event, PSTItemConditions.REGISTRY_ID);
    createRegistry(event, PSTItemBonuses.REGISTRY_ID);
    createRegistry(event, PSTEnchantmentConditions.REGISTRY_ID);
  }

  private static <T> void createRegistry(NewRegistryEvent event, ResourceLocation id) {
    event.create(new RegistryBuilder<T>().setName(id));
  }
}
