package daripher.skilltree.container;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ContainerEvents {
  @SubscribeEvent
  public static void setViewingPlayer(PlayerInteractEvent.RightClickBlock event) {
    BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
    if (blockEntity != null) {
      ((InteractiveContainer) blockEntity).setUser(event.getEntity());
    }
  }
}
