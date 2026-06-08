package daripher.skilltree.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class PSTCommands {
    public static final SuggestionProvider<CommandSourceStack> SKILL_ID_SUGGESTION = (ctx, builder) -> SharedSuggestionProvider.suggest(gatherSkillIds(), builder);
    public static final String AMOUNT_ARGUMENT_NAME = "amount";
    public static final String PLAYER_ARGUMENT_NAME = "player";
    public static final String SKILL_ID_ARGUMENT_NAME = "skill_id";

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        var resetCommand = getRootCommand().then(getResetCommand().then(getPlayerArgument().executes(PSTCommands::executeResetCommand)));
        event.getDispatcher().register(resetCommand);

        var addPointsCommand = getRootCommand().then(getPointsSubCommand().then(getAddSubCommand().then(getPlayerArgument().then(getAmountArgument().executes(PSTCommands::executeAddPointsCommand)))));
        event.getDispatcher().register(addPointsCommand);

        var setPointsCommand = getRootCommand().then(getPointsSubCommand().then(getSetSubCommand().then(getPlayerArgument().then(getAmountArgument().executes(PSTCommands::executeSetPointsCommand)))));
        event.getDispatcher().register(setPointsCommand);

        var grantSkillCommand = getRootCommand().then(getGrantSkillSubCommand().then(getPlayerArgument().then(getSkillArgument().executes(PSTCommands::executeGrantSkillCommand))));
        event.getDispatcher().register(grantSkillCommand);
    }

    private static @NotNull LiteralArgumentBuilder<CommandSourceStack> getGrantSkillSubCommand() {
        return Commands.literal("grant_skill");
    }

    private static @NotNull LiteralArgumentBuilder<CommandSourceStack> getSetSubCommand() {
        return Commands.literal("set");
    }

    private static @NotNull LiteralArgumentBuilder<CommandSourceStack> getAddSubCommand() {
        return Commands.literal("add");
    }

    private static @NotNull LiteralArgumentBuilder<CommandSourceStack> getResetCommand() {
        return Commands.literal("reset");
    }

    private static @NotNull LiteralArgumentBuilder<CommandSourceStack> getPointsSubCommand() {
        return Commands.literal("points");
    }

    private static LiteralArgumentBuilder<CommandSourceStack> getRootCommand() {
        return Commands.literal("skilltree").requires(PSTCommands::hasPermission);
    }

    private static @NotNull RequiredArgumentBuilder<CommandSourceStack, EntitySelector> getPlayerArgument() {
        return Commands.argument(PLAYER_ARGUMENT_NAME, EntityArgument.player());
    }

    private static @NotNull RequiredArgumentBuilder<CommandSourceStack, Integer> getAmountArgument() {
        return Commands.argument(AMOUNT_ARGUMENT_NAME, IntegerArgumentType.integer());
    }

    private static @NotNull RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> getSkillArgument() {
        return Commands.argument(SKILL_ID_ARGUMENT_NAME, ResourceLocationArgument.id()).suggests(SKILL_ID_SUGGESTION);
    }

    private static int executeResetCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, PLAYER_ARGUMENT_NAME);
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        skillsCapability.resetTree(player);
        player.sendSystemMessage(Component.translatable("skilltree.message.reset_command").withStyle(ChatFormatting.YELLOW));
        NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
        return 1;
    }

    private static int executeAddPointsCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, PLAYER_ARGUMENT_NAME);
        int amount = IntegerArgumentType.getInteger(ctx, AMOUNT_ARGUMENT_NAME);
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        skillsCapability.setSkillPoints(amount + skillsCapability.getSkillPoints());
        player.sendSystemMessage(Component.translatable("skilltree.message.point_command").withStyle(ChatFormatting.YELLOW));
        NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
        return 1;
    }

    private static int executeSetPointsCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, PLAYER_ARGUMENT_NAME);
        int amount = IntegerArgumentType.getInteger(ctx, AMOUNT_ARGUMENT_NAME);
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        skillsCapability.setSkillPoints(amount);
        NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
        return 1;
    }

    private static int executeGrantSkillCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, PLAYER_ARGUMENT_NAME);
        ResourceLocation skillId = ctx.getArgument(SKILL_ID_ARGUMENT_NAME, ResourceLocation.class);
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        if (skillsCapability.grantSkill(SkillsReloader.getSkillById(skillId))) {
            NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
            Component skillName = TooltipHelper.getSkillTitle(skillId);
            player.sendSystemMessage(Component.translatable("skilltree.message.grant_skill_command", skillName)
                    .withStyle(ChatFormatting.YELLOW));
        }
        return 1;
    }

    private static boolean hasPermission(CommandSourceStack commandSourceStack) {
        return commandSourceStack.hasPermission(2);
    }

    @NotNull
    private static Stream<String> gatherSkillTreesIds() {
        return SkillTreesReloader.getSkillTrees().keySet().stream().map(ResourceLocation::toString);
    }

    @NotNull
    private static Stream<String> gatherSkillIds() {
        return SkillsReloader.getSkills().keySet().stream().map(ResourceLocation::toString);
    }
}
