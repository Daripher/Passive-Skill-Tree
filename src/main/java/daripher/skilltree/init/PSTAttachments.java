package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkills;
import java.util.function.Supplier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class PSTAttachments {
  public static final DeferredRegister<AttachmentType<?>> REGISTRY =
      DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SkillTreeMod.MOD_ID);

  public static final Supplier<AttachmentType<PlayerSkills>> PLAYER_SKILLS =
      REGISTRY.register(
          "player_skills", () -> AttachmentType.serializable(PlayerSkills::new).copyOnDeath().build());
}
