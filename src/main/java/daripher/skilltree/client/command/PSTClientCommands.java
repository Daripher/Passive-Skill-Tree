package daripher.skilltree.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.screen.editor.SkillTreeEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, value = Dist.CLIENT)
public class PSTClientCommands {
	@SubscribeEvent
	public static void registerCommands(RegisterClientCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> editorCommand = Commands.literal("skilltree")
				.then(Commands.literal("editor")
						.then(Commands.argument("treeId", StringArgumentType.string())
								.executes(PSTClientCommands::displaySkillTreeEditor)));
		event.getDispatcher().register(editorCommand);
	}

	private static int displaySkillTreeEditor(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Minecraft.getInstance().setScreen(new SkillTreeEditorScreen(new ResourceLocation(ctx.getArgument("treeId", String.class))));
		return 1;
	}
}
