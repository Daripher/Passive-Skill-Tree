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

public class WisdomScrollItem extends Item {
  public WisdomScrollItem() {
    super(new Properties());
  }

  @Override
  public @NotNull InteractionResultHolder<ItemStack> use(
      @NotNull Level level, Player player, @NotNull InteractionHand hand) {
    ItemStack itemInHand = player.getItemInHand(hand);
    IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
    int totalSkillPoints =
        skillsCapability.getPlayerSkills().size() + skillsCapability.getSkillPoints();
    if (totalSkillPoints >= Config.max_skill_points) {
      return InteractionResultHolder.fail(itemInHand);
    }
    if (!player.getAbilities().instabuild) {
      itemInHand.shrink(1);
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
          SoundEvents.PLAYER_LEVELUP,
          player.getSoundSource(),
          0.4F,
          0.2F + player.getRandom().nextFloat() * 0.3F);
      skillsCapability.grantSkillPoints(1);
      NetworkDispatcher.network_channel.send(
          PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
          new SyncPlayerSkillsMessage(player));
      if (Config.show_chat_messages) {
        player.sendSystemMessage(
            Component.translatable("skilltree.message.point_command")
                .withStyle(ChatFormatting.YELLOW));
      }
    }
    return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide);
  }

  @Override
  public void appendHoverText(
      @NotNull ItemStack itemStack,
      Level level,
      List<Component> components,
      @NotNull TooltipFlag tooltipFlag) {
    components.add(
        Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GOLD));
  }
}
