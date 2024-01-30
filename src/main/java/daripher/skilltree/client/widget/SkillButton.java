package daripher.skilltree.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SkillButton extends Button {
  private static final Style LESSER_TITLE_STYLE = Style.EMPTY.withColor(0xEAA169);
  private static final Style NOTABLE_TITLE_STYLE = Style.EMPTY.withColor(0x9B66D8);
  private static final Style KEYSTONE_TITLE_STYLE = Style.EMPTY.withColor(0xFFD75F);
  private static final Style GATEWAY_TITLE_STYLE = Style.EMPTY.withColor(0x849696);
  private static final Style DESCRIPTION_STYLE = Style.EMPTY.withColor(0x7B7BE5);
  private static final Style ID_STYLE = Style.EMPTY.withColor(0x545454);
  private static final Style LORE_STYLE = Style.EMPTY.withColor(0xB96526).withItalic(true);
  private final Supplier<Float> animationFunction;
  public final PassiveSkill skill;
  public final float x;
  public final float y;
  public boolean highlighted;
  public boolean animated;

  public SkillButton(
      Supplier<Float> animationFunc, float x, float y, PassiveSkill skill, OnPress pressFunc) {
    super(
        (int) x,
        (int) y,
        skill.getButtonSize(),
        skill.getButtonSize(),
        Component.empty(),
        pressFunc,
        Supplier::get);
    this.x = x;
    this.y = y;
    this.skill = skill;
    this.animationFunction = animationFunc;
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
    graphics.pose().translate(-width / 2d, -height / 2d, 0);
    renderIcon(graphics);
    graphics.pose().popPose();
    float animation = (Mth.sin(animationFunction.get() / 3F) + 1) / 2;
    if (animated) {
      graphics.setColor(1F, 1F, 1F, 1 - animation);
    }
    if (!highlighted) {
      renderDarkening(graphics);
    }
    if (animated) {
      graphics.setColor(1F, 1F, 1F, animation);
    }
    if (highlighted || animated) {
      renderFrame(graphics);
    }
    if (animated) {
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
    ResourceLocation texture = skill.getBackgroundTexture();
    graphics.blit(texture, 0, 0, width, height, width * 2, 0, width, height, width * 3, height);
  }

  private void renderDarkening(GuiGraphics graphics) {
    ResourceLocation texture = skill.getBackgroundTexture();
    graphics.blit(texture, 0, 0, width, height, width, 0, width, height, width * 3, height);
  }

  private void renderIcon(GuiGraphics graphics) {
    ResourceLocation texture = skill.getIconTexture();
    graphics.blit(texture, 0, 0, width, height, 0, 0, width, height, width, height);
  }

  private void renderBackground(GuiGraphics graphics) {
    ResourceLocation texture = skill.getBackgroundTexture();
    graphics.blit(texture, 0, 0, width, height, 0, 0, width, height, width * 3, height);
  }

  public void setButtonSize(int size) {
    width = height = size;
  }

  public List<MutableComponent> getSkillTooltip() {
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

  private void addLoreTooltip(ArrayList<MutableComponent> tooltip) {
    String loreId = getSkillId() + ".lore";
    MutableComponent loreComponent = Component.translatable(loreId);
    if (loreId.equals(loreComponent.getString())) return;
    tooltip.add(loreComponent.withStyle(LORE_STYLE));
  }

  protected void addTitleTooltip(ArrayList<MutableComponent> tooltip) {
    MutableComponent title;
    if (skill.getTitle().isEmpty()) {
      title = Component.translatable(getSkillId() + ".name");
    } else {
      title = Component.literal(skill.getTitle());
    }
    tooltip.add(title.withStyle(getTitleStyle()));
  }

  private Style getTitleStyle() {
    if (skill.getTitleColor().isEmpty()) {
      return width == 30
          ? GATEWAY_TITLE_STYLE
          : width == 24
              ? KEYSTONE_TITLE_STYLE
              : width == 20 ? NOTABLE_TITLE_STYLE : LESSER_TITLE_STYLE;
    } else {
      try {
        return Style.EMPTY.withColor(Integer.parseInt(skill.getTitleColor(), 16));
      } catch (NumberFormatException e) {
        return Style.EMPTY;
      }
    }
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
}
