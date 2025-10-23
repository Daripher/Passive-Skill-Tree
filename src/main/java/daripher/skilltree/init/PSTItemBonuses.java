package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTItemBonuses {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "item_bonuses");
  public static final DeferredRegister<ItemBonus.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<ItemBonus.Serializer> SKILL_BONUS =
      REGISTRY.register("skill_bonus", ItemSkillBonus.Serializer::new);
}
