package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.enchantment.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PSTEnchantments {
  public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, SkillTreeMod.MOD_ID);

  public static final RegistryObject<Enchantment> STEEL_MIND = REGISTRY.register("steel_mind", SteelMindEnchantment::new);
  public static final RegistryObject<Enchantment> MAGIC_FLOW = REGISTRY.register("magic_flow", MagicFlowEnchantment::new);
  public static final RegistryObject<Enchantment> DRAGON_BLOOD = REGISTRY.register("dragon_blood", DragonBloodEnchantment::new);
  public static final RegistryObject<Enchantment> BOTTOMLESS_FLASK = REGISTRY.register("bottomless_flask", BottomlessFlaskEnchantment::new);
  public static final RegistryObject<Enchantment> FIRE_WALL = REGISTRY.register("fire_wall", FireWallEnchantment::new);
}
