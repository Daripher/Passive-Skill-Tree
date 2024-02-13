package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.bonus.event.AttackEventListener;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTEventListeners {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "event_listeners");
  public static final DeferredRegister<SkillEventListener.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<SkillEventListener.Serializer> ATTACK =
      REGISTRY.register("attack", AttackEventListener.Serializer::new);

  public static List<SkillEventListener> eventsList() {
    return PSTRegistries.EVENT_LISTENERS.get().getValues().stream()
        .map(SkillEventListener.Serializer::createDefaultInstance)
        .toList();
  }

  public static String getName(SkillEventListener eventType) {
    ResourceLocation id = PSTRegistries.EVENT_LISTENERS.get().getKey(eventType.getSerializer());
    return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
  }
}
