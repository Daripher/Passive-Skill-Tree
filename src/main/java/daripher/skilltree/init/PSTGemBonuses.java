package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.gem.bonus.GemBonusProvider;
import daripher.skilltree.item.gem.bonus.GemRemovalBonusProvider;
import daripher.skilltree.item.gem.bonus.RandomGemBonusProvider;
import daripher.skilltree.item.gem.bonus.SimpleGemBonusProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTGemBonuses {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "gem_bonuses");
  public static final DeferredRegister<GemBonusProvider.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<GemBonusProvider.Serializer> SIMPLE =
      REGISTRY.register("simple", SimpleGemBonusProvider.Serializer::new);
  public static final RegistryObject<GemBonusProvider.Serializer> RANDOM =
      REGISTRY.register("random", RandomGemBonusProvider.Serializer::new);
  public static final RegistryObject<GemBonusProvider.Serializer> GEM_REMOVAL =
      REGISTRY.register("gem_removal", GemRemovalBonusProvider.Serializer::new);
}
