package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.item.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class PSTItemBonuses {
  public static final ResourceLocation REGISTRY_ID =
      ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "item_bonuses");
  public static final DeferredRegister<ItemBonus.Serializer> REGISTRY =
      DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

  public static final DeferredHolder<ItemBonus.Serializer, ? extends ItemBonus.Serializer> SKILL_BONUS =
      REGISTRY.register("skill_bonus", SkillBonusItemBonus.Serializer::new);
  public static final DeferredHolder<ItemBonus.Serializer, ? extends ItemBonus.Serializer> ITEM_BONUS_LIST =
      REGISTRY.register("item_bonus_list", ItemBonusListItemBonus.Serializer::new);
}
