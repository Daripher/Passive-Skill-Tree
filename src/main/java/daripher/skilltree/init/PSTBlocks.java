package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.block.WorkbenchBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PSTBlocks {
  public static final DeferredRegister<Block> REGISTRY =
      DeferredRegister.create(ForgeRegistries.BLOCKS, SkillTreeMod.MOD_ID);

  // crafting stations
  public static final RegistryObject<Block> WORKBENCH =
      REGISTRY.register("workbench", WorkbenchBlock::new);
}
