package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import java.util.Collection;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.common.ForgeHooks;
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

  public static final RegistryObject<Attribute> EXP_PER_MINUTE = create("exp_per_minute", 1000d);
  public static final RegistryObject<Attribute> REGENERATION = create("regeneration", 1000d);

  private static RegistryObject<Attribute> create(String name, double maxValue) {
    return create(name, 0, maxValue);
  }

  private static RegistryObject<Attribute> create(String name, double minValue, double maxValue) {
    String descriptionId = "attribute.name.%s.%s".formatted(SkillTreeMod.MOD_ID, name);
    return REGISTRY.register(
        name,
        () -> new RangedAttribute(descriptionId, minValue, minValue, maxValue).setSyncable(true));
  }

  @SubscribeEvent
  public static void attachAttributes(EntityAttributeModificationEvent event) {
    REGISTRY.getEntries().stream()
        .map(RegistryObject::get)
        .forEach(attribute -> event.add(EntityType.PLAYER, attribute));
  }

  public static Collection<Attribute> attributeList() {
    //noinspection deprecation
    return ForgeRegistries.ATTRIBUTES.getValues().stream()
        .filter(ForgeHooks.getAttributesView().get(EntityType.PLAYER)::hasAttribute)
        .toList();
  }

  public static String getName(Attribute attribute) {
    ResourceLocation id = ForgeRegistries.ATTRIBUTES.getKey(attribute);
    Objects.requireNonNull(id);
    return id.toString();
  }
}
