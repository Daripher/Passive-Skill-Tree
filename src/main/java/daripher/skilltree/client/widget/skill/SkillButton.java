package daripher.skilltree.client.widget.skill;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.requirement.SkillRequirement;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SkillButton extends Button {
  private static final Style LESSER_TITLE_STYLE = Style.EMPTY.withColor(0xEAA169);
  private static final Style NOTABLE_TITLE_STYLE = Style.EMPTY.withColor(0x9B66D8);
  private static final Style CLASS_TITLE_STYLE = Style.EMPTY.withColor(0xFFD75F);
  private static final Style KEYSTONE_TITLE_STYLE = Style.EMPTY.withColor(0xEB7530);
  private static final Style GATEWAY_TITLE_STYLE = Style.EMPTY.withColor(0x849696);
  private static final Style DESCRIPTION_STYLE = Style.EMPTY.withColor(0x7B7BE5);
  private static final Style ID_STYLE = Style.EMPTY.withColor(0x545454);
  private final Supplier<Float> animationFunction;
  public final PassiveSkill skill;
  public float x;
  public float y;
  public boolean skillLearned;
  public boolean canLearn;
  public boolean searched;
  public boolean selected;

  public SkillButton(Supplier<Float> animationFunc, float x, float y, PassiveSkill skill) {
    super(
        (int) x,
        (int) y,
        skill.getSkillSize(),
        skill.getSkillSize(),
        Component.empty(),
        b -> {},
        Supplier::get);
    this.x = x;
    this.y = y;
    this.skill = skill;
    this.animationFunction = animationFunc;
    this.active = false;
  }

  @Override
  public void renderWidget(
      @NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    RenderSystem.enableBlend();
    graphics.pose().pushPose();
    graphics.pose().translate(x, y, 0);
    renderFavoriteSkillHighlight(graphics);
    renderBackground(graphics);
    graphics.pose().pushPose();
    graphics.pose().translate(width / 2d, height / 2d, 0);
    graphics.pose().scale(0.5F, 0.5F, 1);
    if (width == 32) {
      graphics.pose().scale(0.75F, 0.75F, 1);
    }
    graphics.pose().translate(-width / 2d, -height / 2d, 0);
    renderIcon(graphics);
    graphics.pose().popPose();
    float animation = (Mth.sin(animationFunction.get() / 3F) + 1) / 2;
    float rb = searched ? 0.1f : 1f;
    if (canLearn || searched) {
      graphics.setColor(rb, 1F, rb, 1 - animation);
    }
    if (!skillLearned) {
      renderDarkening(graphics);
    }
    if (canLearn || searched) {
      graphics.setColor(rb, 1F, rb, animation);
    }
    if (skillLearned || canLearn || searched) {
      renderFrame(graphics);
    }
    if (canLearn || searched || selected) {
      graphics.setColor(1F, 1F, 1F, 1F);
    }
    graphics.pose().popPose();
    RenderSystem.disableBlend();
  }

  private void renderFavoriteSkillHighlight(GuiGraphics graphics) {
    if (!ClientConfig.favorite_skills.contains(skill.getId())) return;
    ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/favorite_skill.png");
    int color;
    if (ClientConfig.favorite_color_is_rainbow) {
      color = Color.getHSBColor(animationFunction.get() / 240f, 1f, 1f).getRGB();
    } else {
      color = ClientConfig.favorite_color;
    }
    float r = ((color >> 16) & 0xFF) / 255f;
    float g = ((color >> 8) & 0xFF) / 255f;
    float b = ((color) & 0xFF) / 255f;
    graphics.setColor(r, g, b, 1f);
    int size = (int) (width * 1.4);
    graphics.pose().pushPose();
    graphics.pose().translate(width / 2f, height / 2f, 0f);
    float animation = 1 + 0.3f * (Mth.sin(animationFunction.get() / 3F) + 1) / 2;
    graphics.pose().scale(animation, animation, 1);
    graphics.pose().mulPose(Axis.ZP.rotationDegrees(animationFunction.get()));
    graphics.pose().translate(-size / 2f, -size / 2f, 0f);
    graphics.blit(texture, 0, 0, size, size, 0, 0, 80, 80, 80, 80);
    graphics.pose().popPose();
    graphics.setColor(1f, 1f, 1f, 1f);
  }

  private void renderFrame(GuiGraphics graphics) {
    ResourceLocation texture = skill.getFrameTexture();
    graphics.blit(texture, 0, 0, width, height, width * 2, 0, width, height, width * 3, height);
  }

  private void renderDarkening(GuiGraphics graphics) {
    ResourceLocation texture = skill.getFrameTexture();
    graphics.blit(texture, 0, 0, width, height, width, 0, width, height, width * 3, height);
  }

  private void renderIcon(GuiGraphics graphics) {
    ResourceLocation texture = skill.getIconTexture();
    graphics.blit(texture, 0, 0, width, height, 0, 0, width, height, width, height);
  }

  private void renderBackground(GuiGraphics graphics) {
    ResourceLocation texture = skill.getFrameTexture();
    graphics.blit(texture, 0, 0, width, height, 0, 0, width, height, width * 3, height);
  }

  public void setButtonSize(int size) {
    width = height = size;
  }

  public List<MutableComponent> getSkillTooltip(PassiveSkillTree skillTree) {
    ArrayList<MutableComponent> tooltip = new ArrayList<>();
    addTitleTooltip(tooltip);
    addLimitationsTooltip(skillTree, tooltip);
    List<MutableComponent> description = skill.getDescription();
    if (description != null) {
      tooltip.addAll(description);
    } else {
      addSkillBonusTooltip(tooltip);
    }
    addRequirementsTooltip(tooltip);
    addAdvancedTooltip(tooltip);
    return tooltip;
  }

  public void addRequirementsTooltip(ArrayList<MutableComponent> tooltip) {
    if (skill.getRequirements().isEmpty()) return;
    if (tooltip.size() > 1) {
      tooltip.add(Component.empty());
    }
    MutableComponent requirementsComponent = Component.translatable("skill.requirements");
    requirementsComponent = requirementsComponent.withStyle(TooltipHelper.getSkillBonusStyle(true));
    tooltip.add(requirementsComponent);
    skill.getRequirements().forEach(requirement -> addRequirementTooltip(tooltip, requirement));
  }

  private void addRequirementTooltip(
      ArrayList<MutableComponent> tooltip, SkillRequirement<?> requirement) {
    MutableComponent requirementTooltip = requirement.getTooltip();
    Player localPlayer = Minecraft.getInstance().player;
    Style style = TooltipHelper.getSkillRequirementStyle(requirement.isRequirementMet(localPlayer));
    requirementTooltip = requirementTooltip.withStyle(style);
    tooltip.add(Component.literal("  ").append(requirementTooltip));
  }

  public void addSkillBonusTooltip(List<MutableComponent> tooltip) {
    addDescriptionTooltip(tooltip);
    addInfoTooltip(tooltip);
  }

  private void addInfoTooltip(List<MutableComponent> tooltip) {
    if (!Screen.hasAltDown()) return;
    List<MutableComponent> info = new ArrayList<>();
    for (SkillBonus<?> skillBonus : skill.getBonuses()) {
      skillBonus.gatherInfo(
          component -> {
            component = component.withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
            info.add(component);
          });
    }
    if (!info.isEmpty()) {
      tooltip.add(Component.empty());
      tooltip.addAll(info);
    }
  }

  protected void addAdvancedTooltip(List<MutableComponent> tooltip) {
    Minecraft minecraft = Minecraft.getInstance();
    if (!minecraft.options.advancedItemTooltips) return;
    addIdTooltip(tooltip);
  }

  protected void addDescriptionTooltip(List<MutableComponent> tooltip) {
    skill.getBonuses().stream().map(SkillBonus::getTooltip).forEach(tooltip::add);
    String descriptionId = getSkillId() + ".description";
    String description = Component.translatable(descriptionId).getString();
    if (!description.equals(descriptionId)) {
      List<String> descriptionStrings = Arrays.asList(description.split("/n"));
      descriptionStrings.stream()
          .map(Component::translatable)
          .map(this::applyDescriptionStyle)
          .forEach(tooltip::add);
    }
  }

  private void addLimitationsTooltip(
      PassiveSkillTree skillTree, ArrayList<MutableComponent> tooltips) {
    boolean addedLimitTooltip = false;
    for (String tag : skill.getTags()) {
      int limit = skillTree.getSkillLimitations().getOrDefault(tag, 0);
      if (limit <= 0) continue;
      addedLimitTooltip = true;
      AtomicReference<MutableComponent> tagTooltip = new AtomicReference<>(Component.literal(tag));
      TooltipHelper.consumeTranslated("skill.tag.%s.name".formatted(tag), tagTooltip::set);
      tagTooltip.set(Component.literal(limit + " " + tagTooltip.get().getString()));
      tagTooltip.set(tagTooltip.get().withStyle(TooltipHelper.getSkillBonusSecondStyle(true)));
      MutableComponent tooltip = Component.translatable("skill.limitation", tagTooltip.get());
      tooltip = tooltip.withStyle(TooltipHelper.getSkillBonusStyle(true));
      tooltips.add(tooltip);
    }
    if (addedLimitTooltip) {
      tooltips.add(Component.empty());
    }
  }

  protected void addTitleTooltip(List<MutableComponent> tooltip) {
    MutableComponent title;
    if (skill.getTitle().isEmpty()) {
      title = Component.translatable(getSkillId() + ".name");
    } else {
      title = Component.literal(skill.getTitle());
    }
    tooltip.add(title.withStyle(getTitleStyle()));
  }

  private Style getTitleStyle() {
    String titleColor = skill.getTitleColor();
    if (titleColor.isEmpty()) {
      return width == 30
          ? GATEWAY_TITLE_STYLE
          : width == 24
              ? CLASS_TITLE_STYLE
              : width == 20
                  ? NOTABLE_TITLE_STYLE
                  : width == 32 ? KEYSTONE_TITLE_STYLE : LESSER_TITLE_STYLE;
    } else {
      try {
        return Style.EMPTY.withColor(Integer.parseInt(titleColor, 16));
      } catch (NumberFormatException e) {
        return Style.EMPTY;
      }
    }
  }

  protected void addIdTooltip(List<MutableComponent> tooltip) {
    MutableComponent idComponent = Component.literal(skill.getId().toString()).withStyle(ID_STYLE);
    tooltip.add(idComponent);
  }

  protected MutableComponent applyDescriptionStyle(MutableComponent component) {
    return component.withStyle(DESCRIPTION_STYLE);
  }

  public void setCanLearn() {
    canLearn = true;
  }

  public void setActive() {
    active = true;
  }

  private String getSkillId() {
    return "skill." + skill.getId().getNamespace() + "." + skill.getId().getPath();
  }
}
