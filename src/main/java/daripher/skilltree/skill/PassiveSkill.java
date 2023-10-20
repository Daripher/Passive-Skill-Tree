package daripher.skilltree.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.common.CuriosHelper.SlotAttributeWrapper;

public class PassiveSkill {
  private final ResourceLocation id;
  private final List<Pair<Attribute, AttributeModifier>> attributeModifiers = new ArrayList<>();
  private final List<ResourceLocation> connectedSkills = new ArrayList<>();
  private final List<ResourceLocation> connectedAsGateways = new ArrayList<>();
  private ResourceLocation backgroundTexture;
  private ResourceLocation iconTexture;
  private ResourceLocation borderTexture;
  private @Nullable ResourceLocation connectedTreeId;
  private List<String> commands = new ArrayList<>();
  private float positionX, positionY;
  private int buttonSize;
  private boolean isStartingPoint;

  public PassiveSkill(
      ResourceLocation id,
      int buttonSize,
      ResourceLocation backgroundTexture,
      ResourceLocation iconTexture,
      ResourceLocation borderTexture,
      boolean isStartingPoint) {
    this.id = id;
    this.backgroundTexture = backgroundTexture;
    this.iconTexture = iconTexture;
    this.borderTexture = borderTexture;
    this.buttonSize = buttonSize;
    this.isStartingPoint = isStartingPoint;
  }

  public ResourceLocation getId() {
    return id;
  }

  public int getButtonSize() {
    return buttonSize;
  }

  public void setButtonSize(int buttonSize) {
    this.buttonSize = buttonSize;
  }

  public ResourceLocation getBackgroundTexture() {
    return backgroundTexture;
  }

  public void setBackgroundTexture(ResourceLocation texture) {
    this.backgroundTexture = texture;
  }

  public ResourceLocation getIconTexture() {
    return iconTexture;
  }

  public void setIconTexture(ResourceLocation texture) {
    this.iconTexture = texture;
  }

  public ResourceLocation getBorderTexture() {
    return borderTexture;
  }

  public void setBorderTexture(ResourceLocation texture) {
    this.borderTexture = texture;
  }

  public Optional<ResourceLocation> getConnectedTreeId() {
    return Optional.ofNullable(connectedTreeId);
  }

  public void setConnectedTree(@Nullable ResourceLocation treeId) {
    this.connectedTreeId = treeId;
  }

  public void setConnectedTree(Optional<ResourceLocation> treeId) {
    this.connectedTreeId = treeId.orElse(null);
  }

  public boolean isStartingPoint() {
    return isStartingPoint;
  }

  public void setStartingPoint(boolean isStartingPoint) {
    this.isStartingPoint = isStartingPoint;
  }

  public List<Pair<Attribute, AttributeModifier>> getAttributeModifiers() {
    return attributeModifiers;
  }

  public void addAttributeBonus(Attribute attribute, AttributeModifier modifier) {
    attributeModifiers.add(Pair.of(attribute, modifier));
  }

  public void addAttributeBonus(Pair<Attribute, AttributeModifier> bonus) {
    attributeModifiers.add(bonus);
  }

  public void connect(PassiveSkill otherSkill) {
    connectedSkills.add(otherSkill.getId());
  }

  public void setPosition(float x, float y) {
    positionX = x;
    positionY = y;
  }

  public float getPositionX() {
    return positionX;
  }

  public float getPositionY() {
    return positionY;
  }

  public List<ResourceLocation> getConnectedSkills() {
    return connectedSkills;
  }

  public List<ResourceLocation> getConnectedAsGateways() {
    return connectedAsGateways;
  }

  public boolean isGateway() {
    return !connectedAsGateways.isEmpty();
  }

  public List<String> getCommands() {
    return commands;
  }

  public void setCommands(NonNullList<String> commands) {
    this.commands = commands;
  }

  public void learn(ServerPlayer player, boolean restoring) {
    getAttributeModifiers()
        .forEach(pair -> addAttributeModifier(player, pair.getLeft(), pair.getRight(), restoring));
    if (!restoring) executeCommands(player);
  }

  private void executeCommands(ServerPlayer player) {
    MinecraftServer server = player.getServer();
    if (server == null) return;
    commands.forEach(
        command -> {
          command = command.replaceAll("<p>", player.getGameProfile().getName());
          server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
        });
  }

  @SuppressWarnings("deprecation")
  public void addAttributeModifier(
      ServerPlayer player, Attribute attribute, AttributeModifier modifier, boolean restoring) {
    if (attribute instanceof SlotAttributeWrapper wrapper) {
      if (!restoring)
        CuriosApi.getSlotHelper()
            .growSlotType(wrapper.identifier, (int) modifier.getAmount(), player);
      return;
    }
    AttributeInstance instance = player.getAttribute(attribute);
    if (instance == null) return;
    if (!instance.hasModifier(modifier)) instance.addTransientModifier(modifier);
  }

  public void remove(ServerPlayer player) {
    getAttributeModifiers()
        .forEach(pair -> removeAttributeModifier(player, pair.getLeft(), pair.getRight()));
  }

  @SuppressWarnings("deprecation")
  public void removeAttributeModifier(
      Player player, Attribute attribute, AttributeModifier modifier) {
    if (attribute instanceof SlotAttributeWrapper wrapper) {
      CuriosApi.getSlotHelper()
          .shrinkSlotType(wrapper.identifier, (int) modifier.getAmount(), player);
      return;
    }
    AttributeInstance instance = player.getAttribute(attribute);
    if (instance == null) return;
    if (instance.hasModifier(modifier)) instance.removeModifier(modifier);
  }
}
