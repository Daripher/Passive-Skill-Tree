package daripher.skilltree.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.skill.SkillTreeClientData;
import daripher.skilltree.client.widget.*;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.config.Config;
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
import net.minecraft.client.gui.components.Widget;
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
  private final List<ResourceLocation> newlyLearnedSkills = new ArrayList<>();
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
    this.skillTree = SkillTreeClientData.getSkillTree(skillTreeId);
    this.minecraft = Minecraft.getInstance();
  }

  private static int sortSkillBonusTooltips(Component a, Component b) {
    String regex = "\\+?-?[0-9]+\\.?[0-9]?%?";
    String as = a.getString().replaceAll(regex, "");
    String bs = b.getString().replaceAll(regex, "");
    return as.compareTo(bs);
  }

  private static void mergeSkillBonuses(
      List<SkillBonus<?>> allBonuses, List<SkillBonus<?>> skillBonuses) {
    skillBonuses.forEach(
        bonus -> {
          Optional<SkillBonus<?>> sameBonus = allBonuses.stream().filter(bonus::canMerge).findAny();
          if (sameBonus.isPresent()) {
            allBonuses.remove(sameBonus.get());
            allBonuses.add(sameBonus.get().merge(bonus));
          } else {
            allBonuses.add(bonus);
          }
        });
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
    if (!Config.enable_exp_exchange) {
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
    if (!Config.enable_exp_exchange) {
      pointsInfo.x = width / 2 - buttonWidth / 2;
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
  public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    updateScreen(partialTick);
    renderAnimation += partialTick;
    renderBackground(poseStack);
    renderConnections(poseStack, mouseX, mouseY);
    renderSkills(poseStack, mouseX, mouseY, partialTick);
    renderOverlay(poseStack);
    renderWidgets(poseStack, mouseX, mouseY, partialTick);
    renderSkillTooltip(poseStack, mouseX, mouseY, partialTick);
    prevMouseX = mouseX;
    prevMouseY = mouseY;
  }

  private void renderWidgets(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    MutableComponent pointsLeft =
        Component.literal("" + skillPoints).withStyle(Style.EMPTY.withColor(0xFCE266));
    pointsInfo.setMessage(Component.translatable("widget.skill_points_left", pointsLeft));
    statsInfo.x = width - statsInfo.getWidth() - 10;
    statsInfo.visible = showStats;
    for (Widget widget : renderables) {
      if (widget instanceof SkillButton) continue;
      widget.render(poseStack, mouseX, mouseY, partialTick);
    }
  }

  private void renderSkillTooltip(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    if (getWidgetAt(mouseX, mouseY).isPresent()) return;
    SkillButton skill = getSkillAt(mouseX, mouseY);
    if (skill == null) return;
    float tooltipX = mouseX + (prevMouseX - mouseX) * partialTick;
    float tooltipY = mouseY + (prevMouseY - mouseY) * partialTick;
    ScreenHelper.renderSkillTooltip(
        skill, poseStack, tooltipX, tooltipY, width, height, itemRenderer);
  }

  private void renderSkills(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    poseStack.pushPose();
    poseStack.translate(scrollX, scrollY, 0);
    for (SkillButton widget : skillButtons.values()) {
      poseStack.pushPose();
      poseStack.translate(
          widget.x + widget.getWidth() / 2d, widget.y + widget.getHeight() / 2d, 0F);
      poseStack.scale(zoom, zoom, 1F);
      poseStack.translate(
          -widget.x - widget.getWidth() / 2d, -widget.y - widget.getHeight() / 2d, 0F);
      widget.render(poseStack, mouseX, mouseY, partialTick);
      poseStack.popPose();
    }
    poseStack.popPose();
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
    List<SkillBonus<?>> allBonuses = new ArrayList<>();
    learnedSkills.stream()
        .map(skillButtons::get)
        .map(button -> button.skill)
        .map(PassiveSkill::getBonuses)
        .forEach(skillBonuses -> mergeSkillBonuses(allBonuses, skillBonuses));
    return allBonuses.stream()
        .map(SkillBonus::getTooltip)
        .sorted(SkillTreeScreen::sortSkillBonusTooltips)
        .toList();
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
    PassiveSkill skill = SkillTreeClientData.getSkill(skillId);
    double buttonX = getSkillButtonX(skill);
    double buttonY = getSkillButtonY(skill);
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
    return skillTree.getSkillIds().stream().map(SkillTreeClientData::getSkill);
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
    if (learnedSkills.size() + newlyLearnedSkills.size() >= Config.max_skill_points) return;
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
    int cost = Config.getSkillPointCost(currentLevel);
    NetworkDispatcher.network_channel.sendToServer(new GainSkillPointMessage());
    getPlayer().giveExperiencePoints(-cost);
  }

  private boolean canBuySkillPoint(int currentLevel) {
    if (!Config.enable_exp_exchange) return false;
    if (isMaxLevel(currentLevel)) return false;
    int cost = Config.getSkillPointCost(currentLevel);
    return getPlayer().totalExperience >= cost;
  }

  private boolean isMaxLevel(int currentLevel) {
    return currentLevel >= Config.max_skill_points;
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
    int pointCost = Config.getSkillPointCost(currentLevel);
    buyButton.active = getPlayer().totalExperience >= pointCost;
  }

  private void renderOverlay(PoseStack poseStack) {
    ScreenHelper.prepareTextureRendering(
        new ResourceLocation("skilltree:textures/screen/skill_tree_overlay.png"));
    blit(poseStack, 0, 0, 0, 0F, 0F, width, height, width, height);
  }

  @Override
  public void renderBackground(PoseStack poseStack) {
    ScreenHelper.prepareTextureRendering(
        new ResourceLocation("skilltree:textures/screen/skill_tree_background.png"));
    poseStack.pushPose();
    poseStack.translate(scrollX / 3F, scrollY / 3F, 0);
    int size = BACKGROUND_SIZE;
    blit(poseStack, (width - size) / 2, (height - size) / 2, 0, 0F, 0F, size, size, size, size);
    poseStack.popPose();
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

  protected void renderConnections(PoseStack poseStack, int mouseX, int mouseY) {
    ScreenHelper.prepareTextureRendering(
        new ResourceLocation("skilltree:textures/screen/skill_connection.png"));
    skillConnections.forEach(connection -> renderConnection(poseStack, connection));
    ScreenHelper.prepareTextureRendering(
        new ResourceLocation("skilltree:textures/screen/gateway_connection.png"));
    gatewayConnections.forEach(
        connection -> renderGatewayConnection(poseStack, connection, mouseX, mouseY));
  }

  private void renderGatewayConnection(
      PoseStack poseStack, SkillConnection connection, int mouseX, int mouseY) {
    SkillButton button1 = connection.getFirstButton();
    SkillButton button2 = connection.getSecondButton();
    SkillButton hoveredSkill = getSkillAt(mouseX, mouseY);
    boolean learned = isSkillLearned(button1.skill) || isSkillLearned(button2.skill);
    if (learned) {
      if (learnedSkills.contains(button2.skill.getId())
          || newlyLearnedSkills.contains(button2.skill.getId())) {
        renderGatewayConnection(poseStack, button2, button1);
      } else {
        renderGatewayConnection(poseStack, button1, button2);
      }
      return;
    }
    boolean hovered = hoveredSkill == button1 || hoveredSkill == button2;
    if (hovered) {
      if (hoveredSkill == button2) {
        renderGatewayConnection(poseStack, button2, button1);
      } else {
        renderGatewayConnection(poseStack, button1, button2);
      }
    }
  }

  private void renderGatewayConnection(
      PoseStack poseStack, SkillButton button1, SkillButton button2) {
    poseStack.pushPose();
    double connectionX = button1.x + button1.getWidth() / 2F;
    double connectionY = button1.y + button1.getHeight() / 2F;
    poseStack.translate(connectionX + scrollX, connectionY + scrollY, 0);
    float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
    poseStack.mulPose(Vector3f.ZP.rotation(rotation));
    int length = (int) (ScreenHelper.getDistanceBetweenButtons(button1, button2) / zoom);
    boolean highlighted = isSkillLearned(button1.skill);
    poseStack.scale(zoom, zoom, 1F);
    blit(poseStack, 0, -3, length, 6, -renderAnimation, highlighted ? 0 : 6, length, 6, 30, 12);
    poseStack.popPose();
  }

  private void renderConnection(PoseStack poseStack, SkillConnection connection) {
    poseStack.pushPose();
    SkillButton button1 = connection.getFirstButton();
    SkillButton button2 = connection.getSecondButton();
    double connectionX = button1.x + button1.getWidth() / 2F;
    double connectionY = button1.y + button1.getHeight() / 2F;
    poseStack.translate(connectionX + scrollX, connectionY + scrollY, 0);
    float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
    poseStack.mulPose(Vector3f.ZP.rotation(rotation));
    int length = (int) ScreenHelper.getDistanceBetweenButtons(button1, button2);
    boolean highlighted = button1.highlighted && button2.highlighted;
    poseStack.scale(1F, zoom, 1F);
    blit(poseStack, 0, -3, length, 6, 0, highlighted ? 0 : 6, length, 6, 50, 12);
    boolean shouldAnimate =
        button1.highlighted && button2.animated || button2.highlighted && button1.animated;
    if (!highlighted && shouldAnimate) {
      RenderSystem.setShaderColor(1F, 1F, 1F, (Mth.sin(getAnimation() / 3F) + 1) / 2);
      blit(poseStack, 0, -3, length, 6, 0, 0, length, 6, 50, 12);
      RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }
    poseStack.popPose();
  }

  public float getAnimation() {
    return renderAnimation;
  }

  private @Nonnull LocalPlayer getPlayer() {
    return Objects.requireNonNull(getMinecraft().player);
  }
}
