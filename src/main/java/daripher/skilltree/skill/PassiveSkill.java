package daripher.skilltree.skill;

import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PassiveSkill {
    private final ResourceLocation id;
    private final List<SkillBonus<?>> bonuses = new ArrayList<>();
    private @Nullable List<SkillRequirement<?>> requirements;
    private @Nullable List<ResourceLocation> directConnections = new ArrayList<>();
    private @Nullable List<ResourceLocation> longConnections = new ArrayList<>();
    private @Nullable List<ResourceLocation> oneWayConnections = new ArrayList<>();
    private @Nullable List<String> tags = new ArrayList<>();
    private ResourceLocation backgroundTexture;
    private ResourceLocation iconTexture;
    private ResourceLocation borderTexture;
    private @Nullable String title;
    private @Nullable String titleColor;
    private float positionX, positionY;
    private int buttonSize;
    private boolean isStartingPoint;
    private boolean isAlwaysStartingPoint;
    private @Nullable List<MutableComponent> description;

    public PassiveSkill(ResourceLocation id, int buttonSize, ResourceLocation backgroundTexture, ResourceLocation iconTexture, ResourceLocation borderTexture, boolean isStartingPoint) {
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

    public int getSkillSize() {
        return buttonSize;
    }

    public void setButtonSize(int buttonSize) {
        this.buttonSize = buttonSize;
    }

    public ResourceLocation getFrameTexture() {
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

    public ResourceLocation getTooltipFrameTexture() {
        return borderTexture;
    }

    public void setBorderTexture(ResourceLocation texture) {
        this.borderTexture = texture;
    }

    public boolean isStartingPoint() {
        return isStartingPoint || isAlwaysStartingPoint;
    }

    public void setStartingPoint(boolean isStartingPoint) {
        this.isStartingPoint = isStartingPoint;
    }

    public boolean isAlwaysStartingPoint() {
        return isAlwaysStartingPoint;
    }

    public void setAlwaysStartingPoint(boolean alwaysStartingPoint) {
        isAlwaysStartingPoint = alwaysStartingPoint;
    }

    public List<SkillBonus<?>> getBonuses() {
        return bonuses;
    }

    public @Nonnull List<SkillRequirement<?>> getRequirements() {
        if (requirements == null) {
            return requirements = new ArrayList<>();
        }
        return requirements;
    }

    public void addSkillBonus(SkillBonus<?> bonus) {
        bonuses.add(bonus);
    }

    public void addSkillRequirement(SkillRequirement<?> requirement) {
        getRequirements().add(requirement);
    }

    public void connect(PassiveSkill otherSkill) {
        getDirectConnections().add(otherSkill.getId());
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

    @Nonnull
    public List<ResourceLocation> getDirectConnections() {
        if (directConnections == null) {
            return directConnections = new ArrayList<>();
        }
        return directConnections;
    }

    @Nonnull
    public List<ResourceLocation> getLongConnections() {
        if (longConnections == null) {
            return longConnections = new ArrayList<>();
        }
        return longConnections;
    }

    @Nonnull
    public List<ResourceLocation> getOneWayConnections() {
        if (oneWayConnections == null) {
            return oneWayConnections = new ArrayList<>();
        }
        return oneWayConnections;
    }

    @Nonnull
    public List<String> getTags() {
        if (tags == null) {
            return tags = new ArrayList<>();
        }
        return tags;
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

    public @Nullable List<MutableComponent> getDescription() {
        return description;
    }

    public void setDescription(@Nullable List<MutableComponent> description) {
        this.description = description;
    }

    public void remove(ServerPlayer player) {
        getBonuses().forEach(b -> b.onSkillRemoved(player));
    }

    public boolean isInvalid() {
        return getId() == null || getBonuses() == null || getFrameTexture() == null || getIconTexture() == null || getTooltipFrameTexture() == null;
    }
}
