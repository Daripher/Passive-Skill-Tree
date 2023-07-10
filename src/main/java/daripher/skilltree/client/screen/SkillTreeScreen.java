package daripher.skilltree.client.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.SkillTreeClientData;
import daripher.skilltree.client.widget.ProgressBar;
import daripher.skilltree.client.widget.SkillButton;
import daripher.skilltree.client.widget.SkillTreeButton;
import daripher.skilltree.config.Config;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SkillTreeScreen extends Screen {
	private static final ResourceLocation CONNECTION_TEXTURE_LOCATION = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/skill_connection.png");
	private static final ResourceLocation BACKGROUND_TEXTURE_LOCATION = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/skill_tree_background.png");
	private static final ResourceLocation OVERLAY_TEXTURE_LOCATION = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/skill_tree_overlay.png");
	public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/skill_tree_widgets.png");
	private static final Style EXPERIENCE_COLOR = Style.EMPTY.withColor(0xFCE266);
	protected final Map<ResourceLocation, SkillButton> skillButtons = new HashMap<>();
	private final List<Pair<SkillButton, SkillButton>> connections = new ArrayList<>();
	private final List<SkillButton> startingPoints = new ArrayList<>();
	private final ResourceLocation skillTreeId;
	private List<ResourceLocation> learnedSkills;
	private AbstractWidget progressBar;
	private AbstractWidget buySkillButton;
	private int guiScale;
	protected double scrollSpeedX;
	protected double scrollSpeedY;
	protected double scrollX;
	protected double scrollY;
	protected int maxScrollX;
	protected int maxScrollY;
	public float renderAnimation;
	public int skillPoints;

	public SkillTreeScreen(ResourceLocation skillTreeId) {
		super(Component.empty());
		this.skillTreeId = skillTreeId;
		this.minecraft = Minecraft.getInstance();
		this.guiScale = minecraft.options.guiScale().get();
	}

	@Override
	public void init() {
		clearWidgets();
		initSkillsIfNeeded();
		addSkillButtons();
		maxScrollX -= width / 2 - 80;
		maxScrollY -= height / 2 - 80;
		if (maxScrollX < 0) maxScrollX = 0;
		if (maxScrollY < 0) maxScrollY = 0;
		addSkillConnections();
		highlightSkillsThatCanBeLearned();
		progressBar = new ProgressBar(width / 2 - 235 / 2, 2);
		addRenderableWidget(progressBar);
		buySkillButton = new SkillTreeButton(width / 2 - 145, height - 18, 140, 14, Component.translatable("widget.buy_skill_button"), b -> buySkill());
		addRenderableWidget(buySkillButton);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		updateScreen(partialTick);
		renderAnimation += partialTick;
		renderBackground(poseStack);
		renderConnections(poseStack, partialTick);
		poseStack.pushPose();
		poseStack.translate(scrollX, scrollY, 0);
		for (Widget widget : renderables) {
			if (widget == progressBar || widget == buySkillButton) continue;
			widget.render(poseStack, mouseX, mouseY, partialTick);
		}
		poseStack.popPose();
		renderOverlay(poseStack, mouseX, mouseY, partialTick);
		progressBar.render(poseStack, mouseX, mouseY, partialTick);
		prepareTextureRendering(WIDGETS_LOCATION);
		blit(poseStack, width / 2 + 5, height - 18, 0, 10, 140, 14);
		MutableComponent pointsLeft = Component.literal("" + skillPoints).withStyle(EXPERIENCE_COLOR);
		drawCenteredString(poseStack, font, Component.translatable("widget.skill_points_left", pointsLeft), width / 2 + 75, height - 15, 0xFFFFFF);
		buySkillButton.render(poseStack, mouseX, mouseY, partialTick);
		getChildAt(mouseX, mouseY).filter(SkillButton.class::isInstance).map(SkillButton.class::cast).ifPresent(button -> renderButtonTooltip(button, poseStack, mouseX, mouseY));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Optional<GuiEventListener> child = super.getChildAt(mouseX, mouseY);
		boolean clickedWidget = child.filter(SkillTreeButton.class::isInstance).isPresent();
		if (clickedWidget) return super.mouseClicked(mouseX, mouseY, button);
		return super.mouseClicked(mouseX - scrollX, mouseY - scrollY, button);
	}

	@Override
	public Optional<GuiEventListener> getChildAt(double mouseX, double mouseY) {
		var child = super.getChildAt(mouseX, mouseY);
		if (child.filter(ProgressBar.class::isInstance).isPresent()) return child;
		return super.getChildAt(mouseX - scrollX, mouseY - scrollY);
	}

	protected void initSkillsIfNeeded() {
		if (learnedSkills != null) return;
		learnedSkills = new ArrayList<>();
		var skillsCapability = PlayerSkillsProvider.get(minecraft.player);
		skillsCapability.getPlayerSkills().stream().map(PassiveSkill::getId).forEach(learnedSkills::add);
		skillPoints = skillsCapability.getSkillPoints();
	}

	public void addSkillButtons() {
		startingPoints.clear();
		skillButtons.clear();
		SkillTreeClientData.getSkillsForTree(skillTreeId).forEach(this::addSkillButton);
	}

	protected void addSkillButton(ResourceLocation skillId, PassiveSkill skill) {
		var buttonX = (int) (skill.getPositionX() + width / 2);
		var buttonY = (int) (skill.getPositionY() + height / 2);
		var button = new SkillButton(this::getAnimationProgress, buttonX, buttonY, skill, this::buttonPressed, this::renderButtonTooltip);
		addRenderableWidget(button);
		skillButtons.put(skillId, button);
		if (skill.isStartingPoint()) startingPoints.add(button);
		if (learnedSkills.contains(skill.getId())) button.highlighted = true;
		if (maxScrollX < Mth.abs(skill.getPositionX())) maxScrollX = Mth.abs(skill.getPositionX());
		if (maxScrollY < Mth.abs(skill.getPositionY())) maxScrollY = Mth.abs(skill.getPositionY());
	}

	public void addSkillConnections() {
		connections.clear();
		SkillTreeClientData.getSkillsForTree(skillTreeId).forEach(this::addSkillConnection);
	}

	private void addSkillConnection(ResourceLocation skillId, PassiveSkill skill) {
		skill.getConnectedSkills().forEach(connectedSkillId -> connectSkills(skillId, connectedSkillId));
	}

	protected void connectSkills(ResourceLocation skillId, ResourceLocation connectedSkillId) {
		var skillButton = skillButtons.get(skillId);
		var connectedSkillButton = skillButtons.get(connectedSkillId);
		connections.add(Pair.of(skillButton, connectedSkillButton));
	}

	private void highlightSkillsThatCanBeLearned() {
		if (skillPoints == 0) return;
		if (learnedSkills.isEmpty()) {
			startingPoints.forEach(SkillButton::animate);
			return;
		}
		if (learnedSkills.size() >= getMaximumSkillPoints()) return;
		connections.forEach(this::highlightSkillIfCanLearn);
	}

	protected void highlightSkillIfCanLearn(Pair<SkillButton, SkillButton> connection) {
		var button1 = connection.getLeft();
		var button2 = connection.getRight();
		if (button1.highlighted == button2.highlighted) return;
		if (!button1.highlighted) button1.animate();
		if (!button2.highlighted) button2.animate();
	}

	public void renderButtonTooltip(Button button, PoseStack poseStack, int mouseX, int mouseY) {
		if (!(button instanceof SkillButton)) return;
		var borderStyleStack = ((SkillButton) button).getTooltipBorderStyleStack();
		var tooltip = ((SkillButton) button).getTooltip();
		renderComponentTooltip(poseStack, tooltip, mouseX, mouseY, borderStyleStack);
	}

	public void buttonPressed(Button button) {
		if (button instanceof SkillButton skillButton) skillButtonPressed(skillButton);
	}

	private void buySkill() {
		var currentLevel = getCurrentLevel();
		if (!canBuySkillPoint(currentLevel)) return;
		var minecraft = Minecraft.getInstance();
		var skillCosts = Config.COMMON_CONFIG.getSkillPointCosts();
		NetworkDispatcher.network_channel.sendToServer(new GainSkillPointMessage());
		minecraft.player.giveExperiencePoints(-skillCosts.get(currentLevel));
	}

	private static boolean canBuySkillPoint(int currentLevel) {
		if (!Config.COMMON_CONFIG.experienceGainEnabled()) return false;
		if (isMaxLevel(currentLevel)) return false;
		var minecraft = Minecraft.getInstance();
		var skillCosts = Config.COMMON_CONFIG.getSkillPointCosts();
		return minecraft.player.totalExperience >= skillCosts.get(currentLevel);
	}

	private static boolean isMaxLevel(int currentLevel) {
		var levelupCosts = Config.COMMON_CONFIG.getSkillPointCosts();
		return currentLevel >= levelupCosts.size();
	}

	private static int getCurrentLevel() {
		var minecraft = Minecraft.getInstance();
		var skillsCapability = PlayerSkillsProvider.get(minecraft.player);
		var learnedSkills = skillsCapability.getPlayerSkills().size();
		var skillPoints = skillsCapability.getSkillPoints();
		var currentLevel = learnedSkills + skillPoints;
		return currentLevel;
	}

	protected void skillButtonPressed(SkillButton button) {
		if (button.animated) {
			learnSkill(button.skill);
			return;
		}
		var connectedTreeId = button.skill.getConnectedTreeId();
		if (connectedTreeId != null) {
			var connectedTreeScreen = new SkillTreeScreen(connectedTreeId);
			minecraft.setScreen(connectedTreeScreen);
		}
	}

	protected void learnSkill(PassiveSkill passiveSkill) {
		skillPoints--;
		learnedSkills.add(passiveSkill.getId());
		NetworkDispatcher.network_channel.sendToServer(new LearnSkillMessage(passiveSkill));
		rebuildWidgets();
	}

	private void updateScreen(float partialTick) {
		int currentLevel = getCurrentLevel();
		List<? extends Integer> pointCosts = Config.COMMON_CONFIG.getSkillPointCosts();
		int pointCost = pointCosts.get(currentLevel);
		buySkillButton.active = !isMaxLevel(currentLevel) && minecraft.player.totalExperience >= pointCost;
		if (scrollSpeedX != 0) {
			scrollX += scrollSpeedX * partialTick;
			scrollX = Math.max(-maxScrollX, Math.min(maxScrollX, scrollX));
			scrollSpeedX *= 0.8;
		}
		if (scrollSpeedY != 0) {
			scrollY += scrollSpeedY * partialTick;
			scrollY = Math.max(-maxScrollY, Math.min(maxScrollY, scrollY));
			scrollSpeedY *= 0.8;
		}
	}

	private void renderOverlay(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		prepareTextureRendering(OVERLAY_TEXTURE_LOCATION);
		blit(poseStack, 0, 0, 0, 0F, 0F, width, height, width, height);
	}

	@Override
	public void renderBackground(PoseStack poseStack) {
		prepareTextureRendering(BACKGROUND_TEXTURE_LOCATION);
		poseStack.pushPose();
		poseStack.translate(scrollX / 3F, scrollY / 3F, 0);
		var size = getBackgroundSize();
		blit(poseStack, (width - size) / 2, (height - size) / 2, 0, 0F, 0F, size, size, size, size);
		poseStack.popPose();
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragAmountX, double dragAmountY) {
		if (mouseButton != 0 && mouseButton != 2) return false;
		if (maxScrollX > 0) scrollSpeedX += dragAmountX * 0.25;
		if (maxScrollY > 0) scrollSpeedY += dragAmountY * 0.25;
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		var guiScale = minecraft.options.guiScale().get();
		if (amount > 0 && guiScale < 4) {
			minecraft.options.guiScale().set(guiScale + 1);
		}
		if (amount < 0 && guiScale > 1) {
			minecraft.options.guiScale().set(guiScale - 1);
		}
		minecraft.resizeDisplay();
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public void onClose() {
		minecraft.options.guiScale().set(guiScale);
		minecraft.resizeDisplay();
		super.onClose();
	}

	protected void renderConnections(PoseStack poseStack, float partialTick) {
		prepareTextureRendering(CONNECTION_TEXTURE_LOCATION);
		connections.forEach(connection -> renderConnection(poseStack, connection));
	}

	private void renderConnection(PoseStack poseStack, Pair<SkillButton, SkillButton> connection) {
		poseStack.pushPose();
		var button1 = connection.getLeft();
		var button2 = connection.getRight();
		var connectionX = button1.x + button1.getWidth() / 2;
		var connectionY = button1.y + button1.getHeight() / 2;
		poseStack.translate(connectionX + scrollX, connectionY + scrollY, 0);
		var rotation = getAngleBetweenButtons(button1, button2);
		poseStack.mulPose(Vector3f.ZP.rotation(rotation));
		var length = getDistanceBetweenButtons(button1, button2);
		var highlighted = button1.highlighted && button2.highlighted;
		blit(poseStack, 0, -3, length, 6, 0, highlighted ? 0 : 6, length, 6, 50, 12);
		var shouldAnimate = button1.highlighted && button2.animated || button2.highlighted && button1.animated;
		var isAnimated = !highlighted && shouldAnimate;
		if (isAnimated) {
			RenderSystem.setShaderColor(1F, 1F, 1F, getAnimationProgress());
			blit(poseStack, 0, -3, length, 6, 0, 0, length, 6, 50, 12);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		}
		poseStack.popPose();
	}

	public static void prepareTextureRendering(ResourceLocation textureLocation) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, textureLocation);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
	}

	public float getAnimationProgress() {
		return (Mth.sin(renderAnimation / 3F) + 1) / 2;
	}

	protected float getAngleBetweenButtons(Button button1, Button button2) {
		var x1 = button1.x + button1.getWidth() / 2;
		var y1 = button1.y + button1.getHeight() / 2;
		var x2 = button2.x + button2.getWidth() / 2;
		var y2 = button2.y + button2.getHeight() / 2;
		var angle = getAngleBetweenPoints(x1, y1, x2, y2);
		return angle;
	}

	protected int getDistanceBetweenButtons(Button button1, Button button2) {
		var x1 = button1.x + button1.getWidth() / 2;
		var y1 = button1.y + button1.getHeight() / 2;
		var x2 = button2.x + button2.getWidth() / 2;
		var y2 = button2.y + button2.getHeight() / 2;
		return getDistanceBetweenPoints(x1, y1, x2, y2);
	}

	protected int getDistanceBetweenPoints(int x1, int y1, int x2, int y2) {
		return (int) Mth.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	protected float getAngleBetweenPoints(int x1, int y1, int x2, int y2) {
		return (float) Mth.atan2(y2 - y1, x2 - x1);
	}

	protected int getBackgroundSize() {
		return 2048;
	}

	protected int getMaximumSkillPoints() {
		return Config.COMMON_CONFIG.getMaximumSkillPoints();
	}
}
