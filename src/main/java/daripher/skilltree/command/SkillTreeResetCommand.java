package daripher.skilltree.command;

import com.mojang.brigadier.context.CommandContext;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class SkillTreeResetCommand {
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		var command = Commands.literal("skilltree").then(Commands.literal("reset").executes(SkillTreeResetCommand::execute));
		event.getDispatcher().register(command);
	}

	private static int execute(CommandContext<CommandSourceStack> ctx) {
		var source = ctx.getSource().source;
		if (!(source instanceof Player)) {
			return 0;
		}
		var player = (Player) source;
		var skillsCapability = PlayerSkillsProvider.get(player);
		skillsCapability.setTreeReset(true);
		player.sendSystemMessage(Component.translatable("skilltree.message.reset_command").withStyle(ChatFormatting.YELLOW));
		return 1;
	}
}
