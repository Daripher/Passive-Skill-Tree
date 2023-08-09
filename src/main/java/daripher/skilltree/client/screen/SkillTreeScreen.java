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
import daripher.skilltree.capability.skill.IPlayerSkills;
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
	private static final ResourceLocation SKILL_CONNECTION_TEXTURE = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/skill_connection.png");
	private static final ResourceLocation GATEWAY_CONNECTION_TEXTURE = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/gateway_connection.png");
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/skill_tree_background.png");
	private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/skill_tree_overlay.png");
	public static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/skill_tree_widgets.png");
	private static final Style EXPERIENCE_COLOR = Style.EMPTY.withColor(0xFCE266);
	protected final Map<ResourceLocation, SkillButton> skillButtons = new HashMap<>();
	private final List<Pair<SkillButton, SkillButton>> connections = new ArrayList<>();
	private final List<Pair<SkillButton, SkillButton>> gatewayConnections = new ArrayList<>();
	private final List<SkillButton> startingPoints = new ArrayList<>();
	private final ResourceLocation skillTreeId;
	private List<ResourceLocation> openedGateways = new ArrayList<>();
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
		if (!Config.enable_exp_exchange) {
			progressBar.visible = false;
			buySkillButton.visible = false;
		}
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		updateScreen(partialTick);
		renderAnimation += partialTick;
		renderBackground(poseStack);
		renderConnections(poseStack, mouseX, mouseY, partialTick);
		poseStack.pushPose();
		poseStack.translate(scrollX, scrollY, 0);
		for (Widget widget : renderables) {
			if (widget == progressBar || widget == buySkillButton) continue;
			widget.render(poseStack, mouseX, mouseY, partialTick);
		}
		poseStack.popPose();
		renderOverlay(poseStack, mouseX, mouseY, partialTick);
		progressBar.render(poseStack, mouseX, mouseY, partialTick);
		renderPointsInfo(poseStack);
		buySkillButton.render(poseStack, mouseX, mouseY, partialTick);
		getChildAt(mouseX, mouseY).filter(SkillButton.class::isInstance).map(SkillButton.class::cast).ifPresent(button -> renderButtonTooltip(button, poseStack, mouseX, mouseY));
	}

	public void renderPointsInfo(PoseStack poseStack) {
		prepareTextureRendering(WIDGETS_TEXTURE);
		int x = width / 2 + 5;
		if (!Config.enable_exp_exchange) x = width / 2 - 70;
		blit(poseStack, x, height - 18, 0, 10, 140, 14);
		MutableComponent pointsLeft = Component.literal("" + skillPoints).withStyle(EXPERIENCE_COLOR);
		drawCenteredString(poseStack, font, Component.translatable("widget.skill_points_left", pointsLeft), x + 70, height - 15, 0xFFFFFF);
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
		Optional<GuiEventListener> child = super.getChildAt(mouseX, mouseY);
		if (child.filter(ProgressBar.class::isInstance).isPresent()) return child;
		return super.getChildAt(mouseX - scrollX, mouseY - scrollY);
	}

	protected void initSkillsIfNeeded() {
		if (learnedSkills != null) return;
		learnedSkills = new ArrayList<>();
		IPlayerSkills capability = PlayerSkillsProvider.get(minecraft.player);
		List<PassiveSkill> skills = capability.getPlayerSkills();
		// formatter:off
		skills.stream()
			.map(PassiveSkill::getId)
			.forEach(learnedSkills::add);
		skills.stream()
			.filter(PassiveSkill::isGateway)
			.map(PassiveSkill::getGatewayId)
			.map(Optional::get)
			.forEach(openedGateways::add);
		// formatter:on
		skillPoints = capability.getSkillPoints();
	}

	public void addSkillButtons() {
		startingPoints.clear();
		skillButtons.clear();
		SkillTreeClientData.getSkillsForTree(skillTreeId).forEach(this::addSkillButton);
	}

	protected void addSkillButton(ResourceLocation skillId, PassiveSkill skill) {
		var buttonX = (int) (skill.getPositionX() + width / 2);
		var buttonY = (int) (skill.getPositionY() + height / 2);
		var button = new SkillButton(this::getAnimation, buttonX, buttonY, skill, this::buttonPressed, this::renderButtonTooltip);
		addRenderableWidget(button);
		skillButtons.put(skillId, button);
		if (skill.isStartingPoint()) startingPoints.add(button);
		if (isSkillLearned(skill)) button.highlighted = true;
		if (maxScrollX < Mth.abs(skill.getPositionX())) maxScrollX = Mth.abs(skill.getPositionX());
		if (maxScrollY < Mth.abs(skill.getPositionY())) maxScrollY = Mth.abs(skill.getPositionY());
	}

	protected boolean isSkillLearned(PassiveSkill skill) {
		if (skill.isGateway() && openedGateways.contains(skill.getGatewayId().get())) return true;
		return learnedSkills.contains(skill.getId());
	}

	public void addSkillConnections() {
		connections.clear();
		gatewayConnections.clear();
		Map<ResourceLocation, PassiveSkill> skills = SkillTreeClientData.getSkillsForTree(skillTreeId);
		skills.forEach(this::addSkillConnection);
		Map<ResourceLocation, List<PassiveSkill>> gateways = new HashMap<ResourceLocation, List<PassiveSkill>>();
		skills.values().stream().filter(PassiveSkill::isGateway).forEach(skill -> {
			ResourceLocation gatewayId = skill.getGatewayId().get();
			if (!gateways.containsKey(gatewayId)) gateways.put(gatewayId, new ArrayList<>());
			gateways.get(gatewayId).add(skill);
		});
		gateways.forEach((gatewayId, list) -> {
			for (int i = 1; i < list.size(); i++) {
				SkillButton skill1 = skillButtons.get(list.get(0).getId());
				SkillButton skill2 = skillButtons.get(list.get(i).getId());
				gatewayConnections.add(Pair.of(skill1, skill2));
			}
		});
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
		var skillCosts = Config.level_up_costs;
		NetworkDispatcher.network_channel.sendToServer(new GainSkillPointMessage());
		minecraft.player.giveExperiencePoints(-skillCosts.get(currentLevel));
	}

	private static boolean canBuySkillPoint(int currentLevel) {
		if (!Config.enable_exp_exchange) return false;
		if (isMaxLevel(currentLevel)) return false;
		var minecraft = Minecraft.getInstance();
		var skillCosts = Config.level_up_costs;
		return minecraft.player.totalExperience >= skillCosts.get(currentLevel);
	}

	private static boolean isMaxLevel(int currentLevel) {
		var levelupCosts = Config.level_up_costs;
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
		Optional<ResourceLocation> connectedTree = button.skill.getConnectedTreeId();
		if (connectedTree.isPresent()) {
			minecraft.setScreen(new SkillTreeScreen(connectedTree.get()));
		}
	}

	protected void learnSkill(PassiveSkill skill) {
		skillPoints--;
		learnedSkills.add(skill.getId());
		if (skill.isGateway()) openedGateways.add(skill.getGatewayId().get());
		NetworkDispatcher.network_channel.sendToServer(new LearnSkillMessage(skill));
		rebuildWidgets();
	}

	private void updateScreen(float partialTick) {
		updateBuyPointButton();
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

	protected void updateBuyPointButton() {
		int currentLevel = getCurrentLevel();
		buySkillButton.active = false;
		if (isMaxLevel(currentLevel)) return;
		List<? extends Integer> pointCosts = Config.level_up_costs;
		int pointCost = pointCosts.get(currentLevel);
		buySkillButton.active = minecraft.player.totalExperience >= pointCost;
	}

	private void renderOverlay(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		prepareTextureRendering(OVERLAY_TEXTURE);
		blit(poseStack, 0, 0, 0, 0F, 0F, width, height, width, height);
	}

	@Override
	public void renderBackground(PoseStack poseStack) {
		prepareTextureRendering(BACKGROUND_TEXTURE);
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

	protected void renderConnections(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		prepareTextureRendering(SKILL_CONNECTION_TEXTURE);
		connections.forEach(connection -> renderConnection(poseStack, connection));
		prepareTextureRendering(GATEWAY_CONNECTION_TEXTURE);
		gatewayConnections.forEach(connection -> renderGatewayConnection(poseStack, connection, mouseX, mouseY));
	}

	private void renderGatewayConnection(PoseStack poseStack, Pair<SkillButton, SkillButton> connection, int mouseX, int mouseY) {
		SkillButton button1 = connection.getLeft();
		SkillButton button2 = connection.getRight();
		Optional<GuiEventListener> hoveredWidget = getChildAt(mouseX, mouseY);
		boolean learned = learnedSkills.contains(button1.skill.getId()) || learnedSkills.contains(button2.skill.getId());
		if (learned) {
			if (learnedSkills.contains(button2.skill.getId())) renderGatewayConnection(poseStack, button2, button1);
			else renderGatewayConnection(poseStack, button1, button2);
			return;
		}
		boolean hovered = !hoveredWidget.isEmpty() && (hoveredWidget.get() == button1 || hoveredWidget.get() == button2);
		if (hovered) {
			if (hoveredWidget.get() == button2) renderGatewayConnection(poseStack, button2, button1);
			else renderGatewayConnection(poseStack, button1, button2);
		}
	}

	private void renderGatewayConnection(PoseStack poseStack, SkillButton button1, SkillButton button2) {
		poseStack.pushPose();
		int connectionX = button1.x + button1.getWidth() / 2;
		int connectionY = button1.y + button1.getHeight() / 2;
		poseStack.translate(connectionX + scrollX, connectionY + scrollY, 0);
		float rotation = getAngleBetweenButtons(button1, button2);
		poseStack.mulPose(Vector3f.ZP.rotation(rotation));
		int length = getDistanceBetweenButtons(button1, button2);
		boolean highlighted = openedGateways.contains(button1.skill.getGatewayId().get());
		blit(poseStack, 0, -3, length, 6, -renderAnimation, highlighted ? 0 : 6, length, 6, 30, 12);
		poseStack.popPose();
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
			RenderSystem.setShaderColor(1F, 1F, 1F, (Mth.sin(getAnimation() / 3F) + 1) / 2);
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

	public float getAnimation() {
		return renderAnimation;
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
		return Config.max_skill_points;
	}
}
