package daripher.skilltree.item.gem;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.skill.bonus.item.ItemBonus;
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
    if (SkillTreeMod.apotheosisEnabled()) {
      components.add(Component.translatable("gem.disabled").withStyle(ChatFormatting.RED));
      return;
    }
    MutableComponent gemTooltip =
        Component.translatable("gem.tooltip").withStyle(ChatFormatting.YELLOW);
    components.add(gemTooltip);
    appendBonusesTooltip(stack, components);
  }

  public boolean canInsertInto(Player player, ItemStack stack, ItemStack gemStack, int socket) {
    return !GemBonusHandler.hasGem(stack, socket);
  }

  public void insertInto(
      Player player, ItemStack itemStack, ItemStack gemStack, int gemSlot) {
    float gemPower = GemBonusHandler.getGemPower(player, itemStack);
    GemBonusHandler.insertGem(player, itemStack, gemStack, gemSlot, gemPower);
  }

  public abstract @Nullable ItemBonus<?> getGemBonus(
      Player player, ItemStack itemStack, ItemStack gemStack);

  protected abstract void appendBonusesTooltip(ItemStack stack, List<Component> components);
}
