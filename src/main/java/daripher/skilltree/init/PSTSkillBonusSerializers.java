package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.AttributeSkillBonus;
import daripher.skilltree.skill.bonus.CommandSkillBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.*;

public class PSTSkillBonusSerializers {
  public static final ResourceLocation REGISTRY_ID =
      new ResourceLocation(SkillTreeMod.MOD_ID, "skill_bonus_serializers");
  public static final DeferredRegister<SkillBonus.Serializer<?>> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final RegistryObject<SkillBonus.Serializer<AttributeSkillBonus>> ATTRIBUTE_BONUS =
      REGISTRY.register("attribute", AttributeSkillBonus.Serializer::new);
  public static final RegistryObject<SkillBonus.Serializer<CommandSkillBonus>> COMMAND_BONUS =
      REGISTRY.register("command", CommandSkillBonus.Serializer::new);
}
