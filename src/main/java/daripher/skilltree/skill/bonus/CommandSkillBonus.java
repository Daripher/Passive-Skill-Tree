package daripher.skilltree.skill.bonus;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditor;
import daripher.skilltree.init.PSTSkillBonusSerializers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class CommandSkillBonus implements SkillBonus<CommandSkillBonus> {
  private final String command;

  public CommandSkillBonus(String command) {
    this.command = command;
  }

  @Override
  public void onSkillLearned(ServerPlayer player, boolean firstTime) {
    if (firstTime) executeCommand(player);
  }

  @Override
  public SkillBonus.Serializer<CommandSkillBonus> getSerializer() {
    return PSTSkillBonusSerializers.COMMAND_BONUS.get();
  }

  @Override
  public SkillBonus<CommandSkillBonus> copy() {
    return new CommandSkillBonus(command);
  }

  @Override
  public CommandSkillBonus multiply(double multiplier) {
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    return false;
  }

  @Override
  public boolean sameBonus(SkillBonus<?> other) {
    if (!(other instanceof CommandSkillBonus otherBonus)) return false;
    return otherBonus.command.equals(this.command);
  }

  @Override
  public SkillBonus<CommandSkillBonus> merge(SkillBonus<?> other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public MutableComponent getTooltip() {
    return Component.empty();
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int row) {}

  private void executeCommand(ServerPlayer player) {
    MinecraftServer server = player.getServer();
    if (server == null) return;
    CommandSourceStack commandSourceStack = server.createCommandSourceStack();
    String playerName = player.getGameProfile().getName();
    String command = this.command.replaceAll("<p>", playerName);
    server.getCommands().performPrefixedCommand(commandSourceStack, command);
  }

  public static class Serializer implements SkillBonus.Serializer<CommandSkillBonus> {
    @Override
    public CommandSkillBonus deserialize(JsonObject json) throws JsonParseException {
      String command = json.get("command").getAsString();
      return new CommandSkillBonus(command);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof CommandSkillBonus commandBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("command", commandBonus.command);
    }

    @Override
    public CommandSkillBonus deserialize(CompoundTag tag) {
      String command = tag.getString("Command");
      return new CommandSkillBonus(command);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof CommandSkillBonus commandBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putString("Command", commandBonus.command);
      return tag;
    }

    @Override
    public CommandSkillBonus deserialize(FriendlyByteBuf buf) {
      String command = buf.readUtf();
      return new CommandSkillBonus(command);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof CommandSkillBonus commandBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeUtf(commandBonus.command);
    }
  }
}
