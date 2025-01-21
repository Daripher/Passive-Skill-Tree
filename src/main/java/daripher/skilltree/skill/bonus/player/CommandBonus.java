package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.event.SkillLearnedEventListener;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public class CommandBonus implements EventListenerBonus<CommandBonus> {
  private @Nonnull String command;
  private @Nonnull String description;
  private @Nonnull SkillEventListener eventListener;

  public CommandBonus(@Nonnull String command, @Nonnull String description, @Nonnull SkillEventListener eventListener) {
    this.command = command;
    this.description = description;
    this.eventListener = eventListener;
  }

  @Override
  public void applyEffect(LivingEntity target) {
    if (!(target instanceof Player player)) return;
    if (command.isEmpty()) return;
    MinecraftServer server = player.getServer();
    if (server == null) return;
    CommandSourceStack commandSourceStack = createCommandSourceStack(player, (ServerLevel) player.level());
    Commands commands = server.getCommands();
    commands.performPrefixedCommand(commandSourceStack, command);
  }

  @Override
  public SkillBonus.Serializer getSerializer() {
    return PSTSkillBonuses.COMMAND.get();
  }

  @Override
  public CommandBonus copy() {
    return new CommandBonus(command, description, eventListener);
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
    if (!otherBonus.command.equals(this.command)) return false;
    return Objects.equals(otherBonus.eventListener, this.eventListener);
  }

  @Override
  public SkillBonus<EventListenerBonus<CommandBonus>> merge(SkillBonus<?> other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public MutableComponent getTooltip() {
    Style style = TooltipHelper.getSkillBonusStyle(isPositive());
    return Component.translatable(description)
        .withStyle(style);
  }

  @Override
  public boolean isPositive() {
    return true;
  }

  @Override
  public @NotNull SkillEventListener getEventListener() {
    return eventListener;
  }

  @Override
  public void addEditorWidgets(SkillTreeEditor editor, int index, Consumer<EventListenerBonus<CommandBonus>> consumer) {
    editor.addLabel(0, 0, "Command", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addTextArea(0, 0, 200, 70, command)
        .setResponder(v -> selectCommand(consumer, v));
    editor.increaseHeight(75);
    editor.addLabel(0, 0, "Description", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addTextArea(0, 0, 200, 70, description)
        .setResponder(text -> selectDescription(consumer, text));
    editor.increaseHeight(75);
    editor.addLabel(0, 0, "Event", ChatFormatting.GOLD);
    editor.increaseHeight(19);
    editor.addSelectionMenu(0, 0, 200, eventListener)
        .setResponder(eventListener -> selectEventListener(editor, consumer, eventListener))
        .setMenuInitFunc(() -> addEventListenerWidgets(editor, consumer));
    editor.increaseHeight(19);
  }

  private void selectDescription(Consumer<EventListenerBonus<CommandBonus>> consumer, String text) {
    setDescription(text);
    consumer.accept(this.copy());
  }

  private void selectCommand(Consumer<EventListenerBonus<CommandBonus>> consumer, String text) {
    setCommand(text);
    consumer.accept(this.copy());
  }

  private void selectEventListener(SkillTreeEditor editor, Consumer<EventListenerBonus<CommandBonus>> consumer, SkillEventListener eventListener) {
    setEventListener(eventListener);
    consumer.accept(this.copy());
    editor.rebuildWidgets();
  }

  private void addEventListenerWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<CommandBonus>> consumer) {
    eventListener.addEditorWidgets(editor, e -> {
      setEventListener(e);
      consumer.accept(this.copy());
    });
  }

  public void setCommand(@Nonnull String command) {
    this.command = command;
  }

  public void setDescription(@Nonnull String description) {
    this.description = description;
  }

  public void setEventListener(@Nonnull SkillEventListener eventListener) {
    this.eventListener = eventListener;
  }

  private static CommandSourceStack createCommandSourceStack(Player player, ServerLevel level) {
    return new CommandSourceStack(
        player,
        player.position(),
        player.getRotationVector(),
        level,
        4,
        player.getName()
            .getString(),
        player.getDisplayName(),
        level.getServer(),
        player);
  }

  public static class Serializer implements SkillBonus.Serializer {
    @Override
    public CommandBonus deserialize(JsonObject json) throws JsonParseException {
      String command = json.get("command")
          .getAsString();
      String description = json.has("description") ? json.get("description")
          .getAsString() : "";
      SkillEventListener eventListener = SerializationHelper.deserializeEventListener(json);
      return new CommandBonus(command, description, eventListener);
    }

    @Override
    public void serialize(JsonObject json, SkillBonus<?> bonus) {
      if (!(bonus instanceof CommandBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      json.addProperty("command", aBonus.command);
      json.addProperty("description", aBonus.description);
      SerializationHelper.serializeEventListener(json, aBonus.eventListener);
    }

    @Override
    public CommandBonus deserialize(CompoundTag tag) {
      String command = tag.getString("command");
      String description = tag.contains("description") ? tag.getString("description") : "";
      SkillEventListener eventListener;
      if (!tag.contains("event_listener")) {
        eventListener = new SkillLearnedEventListener();
      }
      else {
        eventListener = SerializationHelper.deserializeEventListener(tag);
      }
      return new CommandBonus(command, description, eventListener);
    }

    @Override
    public CompoundTag serialize(SkillBonus<?> bonus) {
      if (!(bonus instanceof CommandBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      CompoundTag tag = new CompoundTag();
      tag.putString("command", aBonus.command);
      tag.putString("description", aBonus.description);
      SerializationHelper.serializeEventListener(tag, aBonus.eventListener);
      return tag;
    }

    @Override
    public CommandBonus deserialize(FriendlyByteBuf buf) {
      String command = buf.readUtf();
      String description = buf.readUtf();
      SkillEventListener eventListener = NetworkHelper.readEventListener(buf);
      return new CommandBonus(command, description, eventListener);
    }

    @Override
    public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
      if (!(bonus instanceof CommandBonus aBonus)) {
        throw new IllegalArgumentException();
      }
      buf.writeUtf(aBonus.command);
      buf.writeUtf(aBonus.description);
      NetworkHelper.writeEventListener(buf, aBonus.eventListener);
    }

    @Override
    public SkillBonus<?> createDefaultInstance() {
      return new CommandBonus("give @p minecraft:apple", "Grants an apple when learned", new SkillLearnedEventListener());
    }
  }
}
