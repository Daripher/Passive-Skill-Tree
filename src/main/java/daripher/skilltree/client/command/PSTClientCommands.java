package daripher.skilltree.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.data.SkillTreeEditorData;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, value = Dist.CLIENT)
public class PSTClientCommands {
  public static final SuggestionProvider<CommandSourceStack> SKILL_TREE_ID_PROVIDER =
      (ctx, builder) -> SharedSuggestionProvider.suggest(gatherSkillTreesPaths(), builder);

  @NotNull
  private static Stream<String> gatherSkillTreesPaths() {
    return Stream.concat(
            SkillTreesReloader.getSkillTrees().keySet().stream(),
            SkillTreeEditorData.getEditorTreesIDs().stream())
        .map(ResourceLocation::toString);
  }

  private static ResourceLocation tree_to_display;
  private static int timer;

  @SubscribeEvent
  public static void registerCommands(RegisterClientCommandsEvent event) {
    LiteralArgumentBuilder<CommandSourceStack> editorCommand =
        Commands.literal("skilltree")
            .then(
                Commands.literal("editor")
                    .then(
                        Commands.argument("treeId", StringArgumentType.greedyString())
                            .suggests(SKILL_TREE_ID_PROVIDER)
                            .executes(PSTClientCommands::displaySkillTreeEditor)));
    event.getDispatcher().register(editorCommand);
  }

  @SubscribeEvent
  public static void delayedCommandExecution(ClientTickEvent.Post event) {
    if (timer > 0) {
      timer--;
      return;
    }
    if (tree_to_display != null) {
      Minecraft.getInstance().setScreen(new SkillTreeEditorScreen(tree_to_display));
      tree_to_display = null;
    }
  }

  private static int displaySkillTreeEditor(CommandContext<CommandSourceStack> ctx) {
    String treeIdArg = ctx.getArgument("treeId", String.class).toLowerCase();
    PSTClientCommands.tree_to_display = ResourceLocation.parse(treeIdArg);
    PSTClientCommands.timer = 1;
    return 1;
  }
}
