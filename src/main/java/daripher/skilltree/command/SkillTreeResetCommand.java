package daripher.skilltree.command;

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
public class SkillTreeResetCommand {
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		var command = Commands.literal("skilltree").then(Commands.literal("reset").then(Commands.argument("player", EntityArgument.player()).executes(SkillTreeResetCommand::execute)));
		event.getDispatcher().register(command);
	}

	private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		var player = EntityArgument.getPlayer(ctx, "player");
		var skillsCapability = PlayerSkillsProvider.get(player);
		skillsCapability.resetTree(player);
		player.sendSystemMessage(Component.translatable("skilltree.message.reset_command").withStyle(ChatFormatting.YELLOW));
		NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
		return 1;
	}
}
