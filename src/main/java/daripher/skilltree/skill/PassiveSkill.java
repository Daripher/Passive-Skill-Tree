package daripher.skilltree.skill;

import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PassiveSkill {
  private final ResourceLocation id;
  private final List<SkillBonus<?>> bonuses = new ArrayList<>();
  private final List<ResourceLocation> connectedSkills = new ArrayList<>();
  private final List<ResourceLocation> connectedAsGateways = new ArrayList<>();
  private ResourceLocation backgroundTexture;
  private ResourceLocation iconTexture;
  private ResourceLocation borderTexture;
  private @Nullable ResourceLocation connectedTreeId;
  private @Nullable String title;
  private @Nullable String titleColor;
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

  public @Nullable ResourceLocation getConnectedTreeId() {
    return connectedTreeId;
  }

  public void setConnectedTree(@Nullable ResourceLocation treeId) {
    this.connectedTreeId = treeId;
  }

  public boolean isStartingPoint() {
    return isStartingPoint;
  }

  public void setStartingPoint(boolean isStartingPoint) {
    this.isStartingPoint = isStartingPoint;
  }

  public List<SkillBonus<?>> getBonuses() {
    return bonuses;
  }

  public void addSkillBonus(SkillBonus<?> bonus) {
    bonuses.add(bonus);
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

  public List<ResourceLocation> getConnections() {
    return connectedSkills;
  }

  public List<ResourceLocation> getGatewayConnections() {
    return connectedAsGateways;
  }

  public @Nonnull String getTitle() {
    return title == null ? "" : title;
  }

  public void setTitle(@Nonnull String title) {
    this.title = title.isEmpty() ? null : title;
  }

  public void learn(ServerPlayer player, boolean firstTime) {
    getBonuses().forEach(b -> b.onSkillLearned(player, firstTime));
  }

  public void setTitleColor(@Nullable String color) {
    this.titleColor = color;
  }

  public @Nonnull String getTitleColor() {
    return titleColor == null ? "" : titleColor;
  }

  public void remove(ServerPlayer player) {
    getBonuses().forEach(b -> b.onSkillRemoved(player));
  }

  public boolean isBroken() {
    return getId() == null
        || getBonuses() == null
        || getBackgroundTexture() == null
        || getIconTexture() == null
        || getBorderTexture() == null;
  }
}
