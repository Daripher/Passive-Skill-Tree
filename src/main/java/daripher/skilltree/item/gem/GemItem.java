package daripher.skilltree.item.gem;

import daripher.skilltree.compat.apotheosis.ApotheosisCompatibility;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

public abstract class GemItem extends Item {
  public GemItem(Properties properties) {
    super(properties);
  }

  public static MutableComponent formatGemClass(String gemClass) {
    return Component.translatable(
        "gem_class_format", Component.translatable("gem_class." + gemClass));
  }

  @Override
  public void appendHoverText(
      @NotNull ItemStack stack,
      @Nullable Level level,
      @NotNull List<Component> components,
      @NotNull TooltipFlag tooltipFlag) {
    if (ModList.get().isLoaded("apotheosis")) {
      if (ApotheosisCompatibility.ISNTANCE.adventureModuleEnabled()) {
        components.add(Component.translatable("gem.disabled").withStyle(ChatFormatting.RED));
        return;
      }
    }
    MutableComponent gemTooltip =
        Component.translatable("gem.tooltip").withStyle(ChatFormatting.YELLOW);
    components.add(gemTooltip);
    appendBonusesTooltip(stack, components);
  }

  public boolean canInsertInto(Player player, ItemStack stack, ItemStack gemStack, int socket) {
    return !GemHelper.hasGem(stack, socket);
  }

  public void insertInto(
      Player player, ItemStack itemStack, ItemStack gemStack, int gemSlot, double gemPower) {
    GemHelper.insertGem(player, itemStack, gemStack, gemSlot, gemPower);
  }

  public abstract @Nullable SkillBonus<?> getGemBonus(
      Player player, ItemStack itemStack, ItemStack gemStack);

  protected abstract void appendBonusesTooltip(ItemStack stack, List<Component> components);
}
