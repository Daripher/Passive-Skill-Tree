package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
public class PSTAttributes {
  public static final DeferredRegister<Attribute> REGISTRY =
      DeferredRegister.create(ForgeRegistries.ATTRIBUTES, SkillTreeMod.MOD_ID);

  public static final RegistryObject<Attribute> EXP_PER_MINUTE = create("exp_per_minute", 100d);
  public static final RegistryObject<Attribute> EVASION = create("evasion", 90d);
  public static final RegistryObject<Attribute> REGENERATION = create("regeneration", 100d);
  public static final RegistryObject<Attribute> BLOCKING = create("blocking", 90d);
  public static final RegistryObject<Attribute> STEALTH = create("stealth", 90d);

  private static RegistryObject<Attribute> create(String name, double maxValue) {
    String descriptionId = "attribute.name.%s.%s".formatted(SkillTreeMod.MOD_ID, name);
    return REGISTRY.register(
        name, () -> new RangedAttribute(descriptionId, 0d, 0d, maxValue).setSyncable(true));
  }

  @SubscribeEvent
  public static void attachAttributes(EntityAttributeModificationEvent event) {
    REGISTRY.getEntries().stream()
        .map(RegistryObject::get)
        .forEach(attribute -> event.add(EntityType.PLAYER, attribute));
  }
}
