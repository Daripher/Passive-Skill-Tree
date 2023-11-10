package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.function.Supplier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PSTRegistries {
  public static Supplier<IForgeRegistry<SkillBonus.Serializer<?>>> SKILL_BONUS_SERIALIZERS =
      PSTSkillBonusSerializers.REGISTRY.makeRegistry(RegistryBuilder::new);

  @SubscribeEvent
  public static void registerRegistries(NewRegistryEvent event) {
    RegistryBuilder<SkillBonus.Serializer<?>> builder =
        new RegistryBuilder<SkillBonus.Serializer<?>>()
            .setName(PSTSkillBonusSerializers.REGISTRY_ID);
    event.create(builder);
  }
}
