package daripher.skilltree.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.SkillTreeClientData;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, value = Dist.CLIENT)
public class PSTClientCommands {
	public static final SuggestionProvider<CommandSourceStack> SUGGEST_SKILL_TREE_ID_PROVIDER = (ctx, builder) -> {
		return SharedSuggestionProvider.suggest(SkillTreeClientData.getTreeIds().stream().map(ResourceLocation::toString), builder);
	};

	@SubscribeEvent
	public static void registerCommands(RegisterClientCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> editorCommand = Commands.literal("skilltree")
				.then(Commands.literal("editor")
						.then(Commands.argument("treeId", StringArgumentType.greedyString())
								.suggests(SUGGEST_SKILL_TREE_ID_PROVIDER)
								.executes(PSTClientCommands::displaySkillTreeEditor)));
		event.getDispatcher().register(editorCommand);
	}

	private static int displaySkillTreeEditor(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Minecraft.getInstance().setScreen(new SkillTreeEditorScreen(new ResourceLocation(ctx.getArgument("treeId", String.class))));
		return 1;
	}
}
