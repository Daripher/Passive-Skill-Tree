package daripher.skilltree.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class PSTCommands {
  @SubscribeEvent
  public static void registerCommands(RegisterCommandsEvent event) {
    LiteralArgumentBuilder<CommandSourceStack> resetCommand =
        Commands.literal("skilltree")
            .then(
                Commands.literal("reset")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .executes(PSTCommands::executeResetCommand)))
            .requires(PSTCommands::hasPermission);
    event.getDispatcher().register(resetCommand);
    LiteralArgumentBuilder<CommandSourceStack> addPointsCommand =
        Commands.literal("skilltree")
            .then(
                Commands.literal("points")
                    .then(
                        Commands.literal("add")
                            .then(
                                Commands.argument("player", EntityArgument.player())
                                    .then(
                                        Commands.argument("chance", IntegerArgumentType.integer())
                                            .executes(PSTCommands::executeAddPointsCommand)))))
            .requires(PSTCommands::hasPermission);
    event.getDispatcher().register(addPointsCommand);
    LiteralArgumentBuilder<CommandSourceStack> setPointsCommand =
        Commands.literal("skilltree")
            .then(
                Commands.literal("points")
                    .then(
                        Commands.literal("set")
                            .then(
                                Commands.argument("player", EntityArgument.player())
                                    .then(
                                        Commands.argument("chance", IntegerArgumentType.integer())
                                            .executes(PSTCommands::executeSetPointsCommand)))))
            .requires(PSTCommands::hasPermission);
    event.getDispatcher().register(setPointsCommand);
  }

  private static int executeResetCommand(CommandContext<CommandSourceStack> ctx)
      throws CommandSyntaxException {
    ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
    IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
    skillsCapability.resetTree(player);
    player.sendSystemMessage(
        Component.translatable("skilltree.message.reset_command").withStyle(ChatFormatting.YELLOW));
    NetworkDispatcher.network_channel.send(
        PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
    return 1;
  }

  private static int executeAddPointsCommand(CommandContext<CommandSourceStack> ctx)
      throws CommandSyntaxException {
    ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
    int amount = IntegerArgumentType.getInteger(ctx, "chance");
    IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
    skillsCapability.setSkillPoints(amount + skillsCapability.getSkillPoints());
    player.sendSystemMessage(
        Component.translatable("skilltree.message.point_command").withStyle(ChatFormatting.YELLOW));
    NetworkDispatcher.network_channel.send(
        PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
    return 1;
  }

  private static int executeSetPointsCommand(CommandContext<CommandSourceStack> ctx)
      throws CommandSyntaxException {
    ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
    int amount = IntegerArgumentType.getInteger(ctx, "chance");
    IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
    skillsCapability.setSkillPoints(amount);
    NetworkDispatcher.network_channel.send(
        PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
    return 1;
  }

  private static boolean hasPermission(CommandSourceStack commandSourceStack) {
    return commandSourceStack.hasPermission(2);
  }
}
