package daripher.skilltree.item;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.Config;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class AmnesiaScrollItem extends Item {
  public AmnesiaScrollItem() {
    super(new Properties());
  }

  @Override
  public @NotNull InteractionResultHolder<ItemStack> use(
      @NotNull Level level, Player player, @NotNull InteractionHand hand) {
    ItemStack scroll = player.getItemInHand(hand);
    IPlayerSkills skills = PlayerSkillsProvider.get(player);
    if (!player.getAbilities().instabuild) {
      scroll.shrink(1);
    }
    if (!level.isClientSide) {
      level.playSound(
          null,
          player,
          SoundEvents.BOOK_PAGE_TURN,
          player.getSoundSource(),
          0.9F,
          0.7F + player.getRandom().nextFloat() * 0.3F);
      level.playSound(
          null,
          player,
          SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM,
          player.getSoundSource(),
          0.4F,
          0.2F + player.getRandom().nextFloat() * 0.2F);
      skills.resetTree((ServerPlayer) player);
      skills.setSkillPoints((int) (skills.getSkillPoints() * (1 - Config.amnesia_scroll_penalty)));
      player.sendSystemMessage(
          Component.translatable("skilltree.message.reset_command")
              .withStyle(ChatFormatting.YELLOW));
      NetworkDispatcher.network_channel.send(
          PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
          new SyncPlayerSkillsMessage(player));
    }
    return InteractionResultHolder.sidedSuccess(scroll, level.isClientSide);
  }

  @Override
  public void appendHoverText(
      @NotNull ItemStack itemStack,
      Level level,
      List<Component> components,
      @NotNull TooltipFlag tooltipFlag) {
    components.add(
        Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GOLD));
    double penalty = Config.amnesia_scroll_penalty;
    if (penalty > 0) {
      int textPenalty = (int) (penalty * 100);
      components.add(
          Component.translatable(getDescriptionId() + ".warning", textPenalty)
              .withStyle(ChatFormatting.RED));
    }
  }
}
