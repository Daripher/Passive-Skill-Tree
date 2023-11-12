package daripher.skilltree.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.client.screen.ScreenHelper;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SkillButton extends Button {
  private static final Style LESSER_TITLE_STYLE = Style.EMPTY.withColor(0xEAA169);
  private static final Style NOTABLE_TITLE_STYLE = Style.EMPTY.withColor(0x9B66D8);
  private static final Style KEYSTONE_TITLE_STYLE = Style.EMPTY.withColor(0xFFD75F);
  private static final Style GATEWAY_TITLE_STYLE = Style.EMPTY.withColor(0xA9A6B1);
  private static final Style DESCRIPTION_STYLE = Style.EMPTY.withColor(0x7B7BE5);
  private static final Style ID_STYLE = Style.EMPTY.withColor(0x545454);
  private static final Style LORE_STYLE = Style.EMPTY.withColor(0xB96526).withItalic(true);
  public final PassiveSkill skill;
  private final Supplier<Float> animationFunction;
  public double x;
  public double y;
  public boolean highlighted;
  public boolean animated;

  public SkillButton(
      Supplier<Float> animationFunc,
      double x,
      double y,
      PassiveSkill skill,
      OnPress pressFunc,
      OnTooltip tooltipFunc) {
    super(
        (int) x,
        (int) y,
        skill.getButtonSize(),
        skill.getButtonSize(),
        Component.empty(),
        pressFunc,
        tooltipFunc);
    this.x = x;
    this.y = y;
    this.skill = skill;
    this.animationFunction = animationFunc;
  }

  public SkillButton(
      Supplier<Float> animationFunc, double x, double y, PassiveSkill skill, OnPress pressFunc) {
    this(animationFunc, x, y, skill, pressFunc, (b, s, mx, my) -> {});
  }

  @Override
  public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    ScreenHelper.prepareTextureRendering(skill.getBackgroundTexture());
    poseStack.pushPose();
    poseStack.translate(x, y, 0);
    renderBackground(poseStack);
    poseStack.pushPose();
    poseStack.translate(width / 2d, height / 2d, 0);
    poseStack.scale(0.5F, 0.5F, 1);
    poseStack.translate(-width / 2d, -height / 2d, 0);
    RenderSystem.setShaderTexture(0, skill.getIconTexture());
    renderIcon(poseStack);
    poseStack.popPose();
    RenderSystem.setShaderTexture(0, skill.getBackgroundTexture());
    float animation = (Mth.sin(animationFunction.get() / 3F) + 1) / 2;
    if (animated) {
      RenderSystem.setShaderColor(1F, 1F, 1F, 1 - animation);
    }
    if (!highlighted) {
      renderDarkening(poseStack);
    }
    if (animated) {
      RenderSystem.setShaderColor(1F, 1F, 1F, animation);
    }
    if (highlighted || animated) {
      renderFrame(poseStack);
    }
    if (animated) {
      RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }
    poseStack.popPose();
  }

  private void renderFrame(PoseStack poseStack) {
    blit(poseStack, 0, 0, width, height, width * 2, 0, width, height, width * 3, height);
  }

  private void renderDarkening(PoseStack poseStack) {
    blit(poseStack, 0, 0, width, height, width, 0, width, height, width * 3, height);
  }

  private void renderIcon(PoseStack poseStack) {
    blit(poseStack, 0, 0, width, height, 0, 0, width, height, width, height);
  }

  private void renderBackground(PoseStack poseStack) {
    blit(poseStack, 0, 0, width, height, 0, 0, width, height, width * 3, height);
  }

  public void setButtonSize(int size) {
    width = height = size;
  }

  public void setPosition(double x, double y) {
    this.x = x;
    super.x = (int) x;
    this.y = y;
    super.y = (int) y;
  }

  public List<MutableComponent> getTooltip() {
    ArrayList<MutableComponent> tooltip = new ArrayList<>();
    addTitleTooltip(tooltip);
    addDescriptionTooltip(tooltip);
    addLoreTooltip(tooltip);
    Minecraft minecraft = Minecraft.getInstance();
    boolean useAdvancedTooltip = minecraft.options.advancedItemTooltips;
    if (useAdvancedTooltip) addAdvancedTooltip(tooltip);
    return tooltip;
  }

  protected void addAdvancedTooltip(ArrayList<MutableComponent> tooltip) {
    addIdTooltip(tooltip);
    String descriptionId = getSkillId() + ".description";
    String description = Component.translatable(descriptionId).getString();
    if (!description.equals(descriptionId)) {
      skill.getBonuses().stream().map(SkillBonus::getAdvancedTooltip).forEach(tooltip::add);
    }
  }

  protected void addDescriptionTooltip(ArrayList<MutableComponent> tooltip) {
    String descriptionId = getSkillId() + ".description";
    String description = Component.translatable(descriptionId).getString();
    if (description.equals(descriptionId)) {
      skill.getBonuses().stream().map(SkillBonus::getTooltip).forEach(tooltip::add);
    } else {
      List<String> descriptionStrings = Arrays.asList(description.split("/n"));
      descriptionStrings.stream()
          .map(Component::translatable)
          .map(this::applyDescriptionStyle)
          .forEach(tooltip::add);
    }
  }

  private void addLoreTooltip(ArrayList<MutableComponent> tooltip) {
    String loreId = getSkillId() + ".lore";
    MutableComponent loreComponent = Component.translatable(loreId);
    if (loreId.equals(loreComponent.getString())) return;
    tooltip.add(loreComponent.withStyle(LORE_STYLE));
  }

  protected void addTitleTooltip(ArrayList<MutableComponent> tooltip) {
    String titleId = getSkillId() + ".name";
    MutableComponent title = Component.translatable(titleId);
    tooltip.add(title.withStyle(getTitleStyle()));
  }

  private Style getTitleStyle() {
    return width == 33
        ? GATEWAY_TITLE_STYLE
        : width == 24
            ? KEYSTONE_TITLE_STYLE
            : width == 20 ? NOTABLE_TITLE_STYLE : LESSER_TITLE_STYLE;
  }

  protected void addIdTooltip(ArrayList<MutableComponent> tooltip) {
    MutableComponent idComponent = Component.literal(skill.getId().toString()).withStyle(ID_STYLE);
    tooltip.add(idComponent);
  }

  protected MutableComponent applyDescriptionStyle(MutableComponent component) {
    return component.withStyle(DESCRIPTION_STYLE);
  }

  public void animate() {
    animated = true;
  }

  private String getSkillId() {
    return "skill." + skill.getId().getNamespace() + "." + skill.getId().getPath();
  }

  public ItemStack getTooltipBorderStyleStack() {
    Item styleItem =
        width == 24 ? Items.EXPERIENCE_BOTTLE : width == 20 ? Items.SHULKER_SHELL : Items.BUCKET;
    return new ItemStack(styleItem);
  }
}
