package daripher.skilltree.item.gem;

import com.mojang.datafixers.util.Either;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.SocketTooltipRenderer;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class GemTooltipHandler {
  @SubscribeEvent
  public static void addGemTooltipComponent(RenderTooltipEvent.GatherComponents event) {
    if (!ItemHelper.canInsertGem(event.getItemStack())) return;
    event
        .getTooltipElements()
        .add(Either.right(new SocketTooltipRenderer.SocketComponent(event.getItemStack())));
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void removeDuplicateTooltips(ItemTooltipEvent event) {
    ItemStack stack = event.getItemStack();
    if (!ItemHelper.canInsertGem(stack)) return;
    List<ItemStack> gems = GemItem.getGems(stack);
    for (int i = 0; i < gems.size(); i++) {
      removeGemTooltip(event, i);
    }
  }

  private static void removeGemTooltip(ItemTooltipEvent event, int socket) {
    ItemBonus<?> bonus = GemItem.getGemBonuses(event.getItemStack()).get(socket);
    if (bonus instanceof ItemSkillBonus aBonus && aBonus.getBonus() instanceof AttributeBonus) {
      removeTooltip(event.getToolTip(), aBonus.getTooltip());
    }
  }

  private static void removeTooltip(List<Component> tooltips, MutableComponent tooltip) {
    Iterator<Component> iterator = tooltips.iterator();
    while (iterator.hasNext()) {
      if (!iterator.next().getString().equals(tooltip.getString())) continue;
      iterator.remove();
      break;
    }
  }

  @Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class TooltipComponentRegistration {
    @SubscribeEvent
    public static void registerTooltipComponent(
        RegisterClientTooltipComponentFactoriesEvent event) {
      event.register(SocketTooltipRenderer.SocketComponent.class, SocketTooltipRenderer::new);
    }
  }
}
