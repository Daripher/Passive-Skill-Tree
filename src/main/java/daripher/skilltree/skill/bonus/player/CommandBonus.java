package daripher.skilltree.skill.bonus.player;

import com.google.gson.*;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class CommandBonus implements SkillBonus<CommandBonus> {
  private String command;

  public CommandBonus(String command) {
    this.command = command;
  }

  @Override
  public void onSkillLearned(ServerPlayer player, boolean firstTime) {
    if (!firstTime) return;
    MinecraftServer server = player.getServer();
    if (server == null) return;
    CommandSourceStack commandSourceStack = server.createCommandSourceStack();
    String playerName = player.getGameProfile().getName();
    String command = this.command.replaceAll("<p>", playerName);
    server.getCommands().performPrefixedCommand(commandSourceStack, command);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.COMMAND.get();
  }

  @Override
  public CommandBonus copy() {
    return new CommandBonus(command);
  }

  @Override
  public CommandBonus multiply(double multiplier) {
    return this;
  }

  @Override
  public boolean canMerge(SkillBonus<?> other) {
    return false;
  }

  @Override
  public boolean sameBonus(SkillBonus<?> other) {
    if (!(other instanceof CommandBonus otherBonus)) return false;
    return otherBonus.command.equals(this.command);
  }

  @Override
  public SkillBonus<CommandBonus> merge(SkillBonus<?> other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public MutableComponent getTooltip() {
    return Component.empty();
  }

  @Override
  public boolean isPositive() {
    return true;
  }

  @Override
  public void addEditorWidgets(
      SkillTreeEditorScreen editor, int index, Consumer<CommandBonus> consumer) {
    editor.addLabel(0, 0, "Command", ChatFormatting.GOLD);
    editor.shiftWidgets(0, 19);
    editor
        .addTextField(0, 0, 200, 14, command)
        .setResponder(
            c -> {
              setCommand(c);
              consumer.accept(this.copy());
            });
    editor.shiftWidgets(0, 19);
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public CommandBonus deserialize(JsonObject json) throws JsonParseException {
      String command = json.get("command").getAsString();
      return new CommandBonus(command);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof CommandBonus commandBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("command", commandBonus.command);
    }

    @Override
    public CommandBonus deserialize(CompoundTag tag) {
      String command = tag.getString("command");
      return new CommandBonus(command);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof CommandBonus commandBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putString("command", commandBonus.command);
      return tag;
    }

    @Override
    public CommandBonus deserialize(FriendlyByteBuf buf) {
      String command = buf.readUtf();
      return new CommandBonus(command);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof CommandBonus commandBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeUtf(commandBonus.command);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new CommandBonus("give <p> minecraft:apple");
    }
  }
}
