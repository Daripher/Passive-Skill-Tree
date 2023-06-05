package daripher.skilltree.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class SkillTreeCommands {
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		var resetCommand = Commands.literal("skilltree")
				.then(Commands.literal("reset")
						.then(Commands.argument("player", EntityArgument.player())
								.executes(SkillTreeCommands::executeResetCommand)))
				.requires(SkillTreeCommands::hasPermission);
		event.getDispatcher().register(resetCommand);
		var addPointsCommand = Commands.literal("skilltree")
				.then(Commands.literal("points")
						.then(Commands.literal("add")
								.then(Commands.argument("player", EntityArgument.player())
										.then(Commands.argument("amount", IntegerArgumentType.integer())
												.executes(SkillTreeCommands::executeAddPointsCommand)))))
				.requires(SkillTreeCommands::hasPermission);
		event.getDispatcher().register(addPointsCommand);
		var setPointsCommand = Commands.literal("skilltree")
				.then(Commands.literal("points")
						.then(Commands.literal("set")
								.then(Commands.argument("player", EntityArgument.player())
										.then(Commands.argument("amount", IntegerArgumentType.integer())
												.executes(SkillTreeCommands::executeSetPointsCommand)))))
				.requires(SkillTreeCommands::hasPermission);
		event.getDispatcher().register(setPointsCommand);
	}

	private static int executeResetCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		var player = EntityArgument.getPlayer(ctx, "player");
		var skillsCapability = PlayerSkillsProvider.get(player);
		skillsCapability.resetTree(player);
		player.sendSystemMessage(Component.translatable("skilltree.message.reset_command").withStyle(ChatFormatting.YELLOW));
		NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
		return 1;
	}

	private static int executeAddPointsCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		var player = EntityArgument.getPlayer(ctx, "player");
		var amount = IntegerArgumentType.getInteger(ctx, "amount");
		var skillsCapability = PlayerSkillsProvider.get(player);
		skillsCapability.setSkillPoints(amount + skillsCapability.getSkillPoints());
		player.sendSystemMessage(Component.translatable("skilltree.message.point_command").withStyle(ChatFormatting.YELLOW));
		NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
		return 1;
	}

	private static int executeSetPointsCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		var player = EntityArgument.getPlayer(ctx, "player");
		var amount = IntegerArgumentType.getInteger(ctx, "amount");
		var skillsCapability = PlayerSkillsProvider.get(player);
		skillsCapability.setSkillPoints(amount);
		NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
		return 1;
	}

	private static boolean hasPermission(CommandSourceStack commandSourceStack) {
		return commandSourceStack.hasPermission(2);
	}
}
