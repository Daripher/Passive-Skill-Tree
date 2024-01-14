package daripher.skilltree.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.widget.*;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SkillTreeScreen extends Screen {
  public static final int BACKGROUND_SIZE = 2048;
  private final Map<ResourceLocation, SkillButton> skillButtons = new HashMap<>();
  private final List<SkillConnection> skillConnections = new ArrayList<>();
  private final List<SkillConnection> gatewayConnections = new ArrayList<>();
  private final List<ResourceLocation> learnedSkills = new ArrayList<>();
  public final List<ResourceLocation> newlyLearnedSkills = new ArrayList<>();
  private final List<SkillButton> startingPoints = new ArrayList<>();
  private final PassiveSkillTree skillTree;
  public float renderAnimation;
  public int skillPoints;
  protected double scrollSpeedX;
  protected double scrollSpeedY;
  protected double scrollX;
  protected double scrollY;
  protected int maxScrollX;
  protected int maxScrollY;
  private Button buyButton;
  private Label pointsInfo;
  private ProgressBar progressBar;
  private SkillBonusList statsInfo;
  private boolean firstInitDone;
  private boolean showStats;
  private boolean showProgressInNumbers;
  private int prevMouseX;
  private int prevMouseY;
  private float zoom = 1F;

  public SkillTreeScreen(ResourceLocation skillTreeId) {
    super(Component.empty());
    this.skillTree = SkillTreesReloader.getSkillTreeById(skillTreeId);
    this.minecraft = Minecraft.getInstance();
  }

  @Override
  public void init() {
    clearWidgets();
    progressBar =
        new ProgressBar(
            width / 2 - 235 / 2,
            height - 17,
            b -> {
              progressBar.showProgressInNumbers ^= true;
              showProgressInNumbers ^= true;
            });
    progressBar.showProgressInNumbers = showProgressInNumbers;
    addRenderableWidget(progressBar);
    addTopButtons();
    if (!SkillTreeClientData.enable_exp_exchange) {
      progressBar.visible = false;
      buyButton.visible = false;
    }
    if (!firstInitDone) firstInit();
    addSkillButtons();
    statsInfo = new SkillBonusList(48, height - 60);
    statsInfo.setStats(getMergedSkillBonusesTooltips());
    addRenderableWidget(statsInfo);
    maxScrollX -= width / 2 - 80;
    maxScrollY -= height / 2 - 80;
    if (maxScrollX < 0) maxScrollX = 0;
    if (maxScrollY < 0) maxScrollY = 0;
    addSkillConnections();
    addGatewayConnections();
    highlightSkillsThatCanBeLearned();
  }

  private void addTopButtons() {
    MutableComponent buyButtonText = Component.translatable("widget.buy_skill_button");
    MutableComponent pointsInfoText = Component.translatable("widget.skill_points_left", 100);
    MutableComponent confirmButtonText = Component.translatable("widget.confirm_button");
    MutableComponent cancelButtonText = Component.translatable("widget.cancel_button");
    MutableComponent showStatsButtonText = Component.translatable("widget.show_stats");
    int buttonWidth = Math.max(font.width(buyButtonText), font.width(pointsInfoText));
    buttonWidth = Math.max(buttonWidth, font.width(confirmButtonText));
    buttonWidth = Math.max(buttonWidth, font.width(cancelButtonText));
    buttonWidth += 20;
    int buttonsY = 8;
    Button showStatsButton =
        new Button(width - buttonWidth - 8, buttonsY, buttonWidth, 14, showStatsButtonText);
    showStatsButton.setPressFunc(b -> showStats ^= true);
    addRenderableWidget(showStatsButton);
    buyButton = new Button(width / 2 - 8 - buttonWidth, buttonsY, buttonWidth, 14, buyButtonText);
    buyButton.setPressFunc(b -> buySkillPoint());
    addRenderableWidget(buyButton);
    pointsInfo = new Label(width / 2 + 8, buttonsY, buttonWidth, 14, Component.empty());
    if (!SkillTreeClientData.enable_exp_exchange) {
      pointsInfo.setX(width / 2 - buttonWidth / 2);
    }
    addRenderableWidget(pointsInfo);
    buttonsY += 20;
    Button confirmButton =
        new Button(width / 2 - 8 - buttonWidth, buttonsY, buttonWidth, 14, confirmButtonText);
    confirmButton.setPressFunc(b -> confirmLearnSkills());
    addRenderableWidget(confirmButton);
    Button cancelButton = new Button(width / 2 + 8, buttonsY, buttonWidth, 14, cancelButtonText);
    cancelButton.setPressFunc(b -> cancelLearnSkills());
    addRenderableWidget(cancelButton);
    confirmButton.active = cancelButton.active = !newlyLearnedSkills.isEmpty();
  }

  @Override
  public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    updateScreen(partialTick);
    renderAnimation += partialTick;
    renderBackground(graphics);
    renderConnections(graphics, mouseX, mouseY);
    renderSkills(graphics, mouseX, mouseY, partialTick);
    renderOverlay(graphics);
    renderWidgets(graphics, mouseX, mouseY, partialTick);
    renderSkillTooltip(graphics, mouseX, mouseY, partialTick);
    prevMouseX = mouseX;
    prevMouseY = mouseY;
  }

  private void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    MutableComponent pointsLeft =
        Component.literal("" + skillPoints).withStyle(Style.EMPTY.withColor(0xFCE266));
    pointsInfo.setMessage(Component.translatable("widget.skill_points_left", pointsLeft));
    statsInfo.setX(width - statsInfo.getWidth() - 10);
    statsInfo.visible = showStats;
    for (Renderable widget : renderables) {
      if (widget instanceof SkillButton) continue;
      widget.render(graphics, mouseX, mouseY, partialTick);
    }
  }

  private void renderSkillTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    if (getWidgetAt(mouseX, mouseY).isPresent()) return;
    SkillButton skill = getSkillAt(mouseX, mouseY);
    if (skill == null) return;
    float tooltipX = mouseX + (prevMouseX - mouseX) * partialTick;
    float tooltipY = mouseY + (prevMouseY - mouseY) * partialTick;
    ScreenHelper.renderSkillTooltip(skill, graphics, tooltipX, tooltipY, width, height);
  }

  private void renderSkills(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    graphics.pose().pushPose();
    graphics.pose().translate(scrollX, scrollY, 0);
    for (SkillButton widget : skillButtons.values()) {
      graphics.pose().pushPose();
      graphics
          .pose()
          .translate(widget.x + widget.getWidth() / 2d, widget.y + widget.getHeight() / 2d, 0F);
      graphics.pose().scale(zoom, zoom, 1F);
      graphics
          .pose()
          .translate(-widget.x - widget.getWidth() / 2d, -widget.y - widget.getHeight() / 2d, 0F);
      widget.render(graphics, mouseX, mouseY, partialTick);
      graphics.pose().popPose();
    }
    graphics.pose().popPose();
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    Optional<GuiEventListener> widget = getWidgetAt(mouseX, mouseY);
    if (widget.isPresent()) return widget.get().mouseClicked(mouseX, mouseY, button);
    SkillButton skill = getSkillAt(mouseX, mouseY);
    if (skill == null) return false;
    if (button == 0) {
      return skill.mouseClicked(skill.x + 1, skill.y + 1, button);
    } else if (button == 1) {
      ClientConfig.toggleFavoriteSkill(skill.skill);
      getMinecraft()
          .getSoundManager()
          .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
      return true;
    }
    return false;
  }

  public Optional<GuiEventListener> getWidgetAt(double mouseX, double mouseY) {
    Predicate<GuiEventListener> isSkillButton = SkillButton.class::isInstance;
    return super.getChildAt(mouseX, mouseY).filter(isSkillButton.negate());
  }

  public @Nullable SkillButton getSkillAt(double mouseX, double mouseY) {
    mouseX -= scrollX;
    mouseY -= scrollY;
    for (SkillButton button : skillButtons.values()) {
      double skillSize = button.skill.getButtonSize() * zoom;
      double skillX = button.x + button.getWidth() / 2d - skillSize / 2;
      double skillY = button.y + button.getHeight() / 2d - skillSize / 2;
      if (mouseX >= skillX
          && mouseY >= skillY
          && mouseX < skillX + skillSize
          && mouseY < skillY + skillSize) {
        return button;
      }
    }
    return null;
  }

  private List<MutableComponent> getMergedSkillBonusesTooltips() {
    List<SkillBonus<?>> bonuses = new ArrayList<>();
    learnedSkills.stream()
        .map(skillButtons::get)
        .map(button -> button.skill)
        .map(PassiveSkill::getBonuses)
        .flatMap(List::stream)
        .forEach(b -> addToMergeList(b, bonuses));
    return bonuses.stream().sorted().map(SkillBonus::getTooltip).toList();
  }

  private static void addToMergeList(SkillBonus<?> b, List<SkillBonus<?>> bonuses) {
    Optional<SkillBonus<?>> same = bonuses.stream().filter(b::canMerge).findAny();
    if (same.isPresent()) {
      bonuses.remove(same.get());
      bonuses.add(same.get().merge(b));
    } else {
      bonuses.add(b);
    }
  }

  protected void firstInit() {
    IPlayerSkills capability = PlayerSkillsProvider.get(getPlayer());
    List<PassiveSkill> skills = capability.getPlayerSkills();
    skills.stream().map(PassiveSkill::getId).forEach(learnedSkills::add);
    skillPoints = capability.getSkillPoints();
    firstInitDone = true;
  }

  public void addSkillButtons() {
    startingPoints.clear();
    skillButtons.clear();
    skillTree.getSkillIds().forEach(this::addSkillButton);
  }

  protected void addSkillButton(ResourceLocation skillId) {
    PassiveSkill skill = SkillsReloader.getSkillById(skillId);
    if (skill == null) return;
    float buttonX = getSkillButtonX(skill);
    float buttonY = getSkillButtonY(skill);
    SkillButton button =
        new SkillButton(this::getAnimation, buttonX, buttonY, skill, this::buttonPressed);
    addRenderableWidget(button);
    skillButtons.put(skillId, button);
    if (skill.isStartingPoint()) startingPoints.add(button);
    if (isSkillLearned(skill)) button.highlighted = true;
    if (maxScrollX < Mth.abs(skill.getPositionX()))
      maxScrollX = (int) Mth.abs(skill.getPositionX());
    if (maxScrollY < Mth.abs(skill.getPositionY()))
      maxScrollY = (int) Mth.abs(skill.getPositionY());
  }

  private float getSkillButtonX(PassiveSkill skill) {
    float skillX = skill.getPositionX();
    return skillX - skill.getButtonSize() / 2F + width / 2F + skillX * (zoom - 1);
  }

  private float getSkillButtonY(PassiveSkill skill) {
    float skillY = skill.getPositionY();
    return skillY - skill.getButtonSize() / 2F + height / 2F + skillY * (zoom - 1);
  }

  protected boolean isSkillLearned(PassiveSkill skill) {
    return learnedSkills.contains(skill.getId()) || newlyLearnedSkills.contains(skill.getId());
  }

  public void addSkillConnections() {
    skillConnections.clear();
    getTreeSkills().forEach(this::addSkillConnections);
  }

  public void addGatewayConnections() {
    gatewayConnections.clear();
    getTreeSkills().forEach(this::addGatewayConnections);
  }

  private Stream<PassiveSkill> getTreeSkills() {
    return skillTree.getSkillIds().stream().map(SkillsReloader::getSkillById);
  }

  private void addSkillConnections(PassiveSkill skill) {
    skill
        .getConnections()
        .forEach(
            connectedSkillId -> connectSkills(skillConnections, skill.getId(), connectedSkillId));
  }

  private void addGatewayConnections(PassiveSkill skill) {
    skill
        .getGatewayConnections()
        .forEach(
            connectedSkillId -> connectSkills(gatewayConnections, skill.getId(), connectedSkillId));
  }

  protected void connectSkills(
      List<SkillConnection> connections, ResourceLocation skillId1, ResourceLocation skillId2) {
    SkillButton button1 = skillButtons.get(skillId1);
    SkillButton button2 = skillButtons.get(skillId2);
    connections.add(new SkillConnection(button1, button2));
  }

  private void highlightSkillsThatCanBeLearned() {
    if (skillPoints == 0) return;
    if (learnedSkills.isEmpty() && newlyLearnedSkills.isEmpty()) {
      startingPoints.forEach(SkillButton::animate);
      return;
    }
    if (learnedSkills.size() + newlyLearnedSkills.size() >= SkillTreeClientData.max_skill_points)
      return;
    skillConnections.forEach(SkillConnection::updateAnimation);
    gatewayConnections.forEach(SkillConnection::updateAnimation);
  }

  public void buttonPressed(net.minecraft.client.gui.components.Button button) {
    if (button instanceof SkillButton skillButton) skillButtonPressed(skillButton);
  }

  private void confirmLearnSkills() {
    newlyLearnedSkills.forEach(id -> learnSkill(skillButtons.get(id).skill));
    newlyLearnedSkills.clear();
  }

  private void cancelLearnSkills() {
    skillPoints += newlyLearnedSkills.size();
    newlyLearnedSkills.clear();
    rebuildWidgets();
  }

  private void buySkillPoint() {
    int currentLevel = getCurrentLevel();
    if (!canBuySkillPoint(currentLevel)) return;
    int cost = SkillTreeClientData.getSkillPointCost(currentLevel);
    NetworkDispatcher.network_channel.sendToServer(new GainSkillPointMessage());
    getPlayer().giveExperiencePoints(-cost);
  }

  private boolean canBuySkillPoint(int currentLevel) {
    if (!SkillTreeClientData.enable_exp_exchange) return false;
    if (isMaxLevel(currentLevel)) return false;
    int cost = SkillTreeClientData.getSkillPointCost(currentLevel);
    return getPlayer().totalExperience >= cost;
  }

  private boolean isMaxLevel(int currentLevel) {
    return currentLevel >= SkillTreeClientData.max_skill_points;
  }

  private int getCurrentLevel() {
    IPlayerSkills capability = PlayerSkillsProvider.get(getPlayer());
    int learnedSkills = capability.getPlayerSkills().size();
    int skillPoints = capability.getSkillPoints();
    return learnedSkills + skillPoints;
  }

  protected void skillButtonPressed(SkillButton button) {
    assert minecraft != null;
    PassiveSkill skill = button.skill;
    if (button.animated) {
      skillPoints--;
      newlyLearnedSkills.add(skill.getId());
      rebuildWidgets();
      return;
    }
    ResourceLocation connectedTree = skill.getConnectedTreeId();
    if (connectedTree != null) {
      minecraft.setScreen(new SkillTreeScreen(connectedTree));
    }
  }

  protected void learnSkill(PassiveSkill skill) {
    learnedSkills.add(skill.getId());
    NetworkDispatcher.network_channel.sendToServer(new LearnSkillMessage(skill));
    rebuildWidgets();
  }

  private void updateScreen(float partialTick) {
    updateBuyPointButton();
    scrollX += scrollSpeedX * partialTick;
    scrollX = Math.max(-maxScrollX * zoom, Math.min(maxScrollX * zoom, scrollX));
    scrollSpeedX *= 0.8;
    scrollY += scrollSpeedY * partialTick;
    scrollY = Math.max(-maxScrollY * zoom, Math.min(maxScrollY * zoom, scrollY));
    scrollSpeedY *= 0.8;
  }

  protected void updateBuyPointButton() {
    int currentLevel = getCurrentLevel();
    buyButton.active = false;
    if (isMaxLevel(currentLevel)) return;
    int pointCost = SkillTreeClientData.getSkillPointCost(currentLevel);
    buyButton.active = getPlayer().totalExperience >= pointCost;
  }

  private void renderOverlay(GuiGraphics graphics) {
    ResourceLocation texture =
        new ResourceLocation("skilltree:textures/screen/skill_tree_overlay.png");
    RenderSystem.enableBlend();
    graphics.blit(texture, 0, 0, 0, 0F, 0F, width, height, width, height);
    RenderSystem.disableBlend();
  }

  @Override
  public void renderBackground(GuiGraphics graphics) {
    ResourceLocation texture =
        new ResourceLocation("skilltree:textures/screen/skill_tree_background.png");
    graphics.pose().pushPose();
    graphics.pose().translate(scrollX / 3F, scrollY / 3F, 0);
    int size = BACKGROUND_SIZE;
    graphics.blit(
        texture, (width - size) / 2, (height - size) / 2, 0, 0F, 0F, size, size, size, size);
    graphics.pose().popPose();
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int mouseButton, double dragAmountX, double dragAmountY) {
    if (mouseButton != 0 && mouseButton != 2) return false;
    if (maxScrollX > 0) scrollSpeedX += dragAmountX * 0.25;
    if (maxScrollY > 0) scrollSpeedY += dragAmountY * 0.25;
    return true;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    if (getWidgetAt(mouseX, mouseY).filter(SkillBonusList.class::isInstance).isEmpty()) {
      if (amount > 0 && zoom < 2F) zoom += 0.05f;
      if (amount < 0 && zoom > 0.25F) zoom -= 0.05f;
      rebuildWidgets();
    }
    return super.mouseScrolled(mouseX, mouseY, amount);
  }

  protected void renderConnections(GuiGraphics graphics, int mouseX, int mouseY) {
    skillConnections.forEach(c -> renderConnection(graphics, c));
    gatewayConnections.forEach(c -> renderGatewayConnection(graphics, c, mouseX, mouseY));
  }

  private void renderGatewayConnection(
      GuiGraphics graphics, SkillConnection connection, int mouseX, int mouseY) {
    SkillButton button1 = connection.getFirstButton();
    SkillButton button2 = connection.getSecondButton();
    SkillButton hoveredSkill = getSkillAt(mouseX, mouseY);
    boolean learned = isSkillLearned(button1.skill) || isSkillLearned(button2.skill);
    if (learned) {
      if (learnedSkills.contains(button2.skill.getId())
          || newlyLearnedSkills.contains(button2.skill.getId())) {
        renderGatewayConnection(graphics, button2, button1);
      } else {
        renderGatewayConnection(graphics, button1, button2);
      }
      return;
    }
    boolean hovered = hoveredSkill == button1 || hoveredSkill == button2;
    if (hovered) {
      if (hoveredSkill == button2) {
        renderGatewayConnection(graphics, button2, button1);
      } else {
        renderGatewayConnection(graphics, button1, button2);
      }
    }
  }

  private void renderGatewayConnection(
      GuiGraphics graphics, SkillButton button1, SkillButton button2) {
    ResourceLocation texture =
        new ResourceLocation("skilltree:textures/screen/gateway_connection.png");
    graphics.pose().pushPose();
    double connectionX = button1.x + button1.getWidth() / 2F;
    double connectionY = button1.y + button1.getHeight() / 2F;
    graphics.pose().translate(connectionX + scrollX, connectionY + scrollY, 0);
    float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
    graphics.pose().mulPose(Axis.ZP.rotation(rotation));
    int length = (int) (ScreenHelper.getDistanceBetweenButtons(button1, button2) / zoom);
    boolean highlighted = isSkillLearned(button1.skill);
    graphics.pose().scale(zoom, zoom, 1F);
    graphics.blit(
        texture, 0, -3, length, 6, -renderAnimation, highlighted ? 0 : 6, length, 6, 30, 12);
    graphics.pose().popPose();
  }

  private void renderConnection(GuiGraphics graphics, SkillConnection connection) {
    ResourceLocation texture =
        new ResourceLocation("skilltree:textures/screen/skill_connection.png");
    graphics.pose().pushPose();
    SkillButton button1 = connection.getFirstButton();
    SkillButton button2 = connection.getSecondButton();
    double connectionX = button1.x + button1.getWidth() / 2F;
    double connectionY = button1.y + button1.getHeight() / 2F;
    graphics.pose().translate(connectionX + scrollX, connectionY + scrollY, 0);
    float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
    graphics.pose().mulPose(Axis.ZP.rotation(rotation));
    int length = (int) ScreenHelper.getDistanceBetweenButtons(button1, button2);
    boolean highlighted = button1.highlighted && button2.highlighted;
    graphics.pose().scale(1F, zoom, 1F);
    graphics.blit(texture, 0, -3, length, 6, 0, highlighted ? 0 : 6, length, 6, 50, 12);
    boolean shouldAnimate =
        button1.highlighted && button2.animated || button2.highlighted && button1.animated;
    if (!highlighted && shouldAnimate) {
      RenderSystem.setShaderColor(1F, 1F, 1F, (Mth.sin(getAnimation() / 3F) + 1) / 2);
      graphics.blit(texture, 0, -3, length, 6, 0, 0, length, 6, 50, 12);
      RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }
    graphics.pose().popPose();
  }

  public float getAnimation() {
    return renderAnimation;
  }

  private @Nonnull LocalPlayer getPlayer() {
    return Objects.requireNonNull(getMinecraft().player);
  }
}
