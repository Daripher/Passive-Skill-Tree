package daripher.skilltree.client.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.SkillTreeClientData;
import daripher.skilltree.client.widget.PassiveSkillButton;
import daripher.skilltree.config.Config;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SkillTreeScreen extends Screen {
	private static final ResourceLocation CONNECTION_TEXTURE_LOCATION = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/skill_connection.png");
	private static final ResourceLocation BACKGROUND_TEXTURE_LOCATION = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/skill_tree_background.png");
	private final Map<ResourceLocation, PassiveSkillButton> skillButtons = new HashMap<>();
	private final List<Pair<PassiveSkillButton, PassiveSkillButton>> connections = new ArrayList<>();
	private final List<PassiveSkillButton> startingPoints = new ArrayList<>();
	private List<ResourceLocation> learnedSkills = new ArrayList<>();
	private final ResourceLocation skillTreeId;
	private int skillPoints;
	private boolean firstInitDone;
	private double scrollX;
	private double scrollY;
	private int maxScrollX;
	private int maxScrollY;
	public float animation;

	public SkillTreeScreen(ResourceLocation skillTreeId) {
		super(Component.empty());
		this.skillTreeId = skillTreeId;
	}

	@Override
	protected void init() {
		if (!firstInitDone) {
			firstInit();
			firstInitDone = true;
		}

		startingPoints.clear();
		connections.clear();
		skillButtons.clear();
		addSkillButtons();
		addSkillConnections();
		highlightSkillsThatCanBeLearned();
	}

	private void firstInit() {
		var skillsData = PlayerSkillsProvider.get(minecraft.player);
		skillsData.getPlayerSkills().stream().map(PassiveSkill::getId).forEach(learnedSkills::add);
		skillPoints = skillsData.getSkillPoints();
	}

	public void addSkillButtons() {
		SkillTreeClientData.getSkillsForTree(skillTreeId).forEach((skillId, skill) -> {
			var buttonX = (int) (skill.getPositionX() + scrollX + width / 2);
			var buttonY = (int) (skill.getPositionY() + scrollY + height / 2);
			var button = new PassiveSkillButton(this, buttonX, buttonY, skill);
			addRenderableWidget(button);
			skillButtons.put(skillId, button);

			if (skill.isStartingPoint()) {
				startingPoints.add(button);
			}

			if (learnedSkills.contains(skill.getId())) {
				button.isSkillLearned = true;
			}

			if (maxScrollX < Mth.abs(skill.getPositionX())) {
				maxScrollX = Mth.abs(skill.getPositionX());
			}

			if (maxScrollY < Mth.abs(skill.getPositionY())) {
				maxScrollY = Mth.abs(skill.getPositionY());
			}
		});
	}

	public void addSkillConnections() {
		SkillTreeClientData.getSkillsForTree(skillTreeId).forEach((skillId, skill) -> {
			skill.getConnectedSkills().forEach(connectedSkillId -> {
				connections.add(Pair.of(skillButtons.get(skillId), skillButtons.get(connectedSkillId)));
			});
		});
	}

	private void highlightSkillsThatCanBeLearned() {
		if (skillPoints == 0) {
			return;
		}

		if (learnedSkills.isEmpty()) {
			startingPoints.forEach(PassiveSkillButton::setCanLearnSkill);
			return;
		}

		if (learnedSkills.size() >= Config.COMMON_CONFIG.maximumSkillPoints.get()) {
			return;
		}

		connections.forEach(connection -> {
			var button1 = connection.getLeft();
			var button2 = connection.getRight();

			if (button1.isSkillLearned != button2.isSkillLearned) {
				if (!button1.isSkillLearned) {
					button1.setCanLearnSkill();
				}

				if (!button2.isSkillLearned) {
					button2.setCanLearnSkill();
				}
			}
		});
	}

	public void renderButtonTooltip(Button button, PoseStack poseStack, int mouseX, int mouseY) {
		if (button instanceof PassiveSkillButton skillButton) {
			var borderStyleStack = new ItemStack(Items.MUSIC_DISC_13);
			renderComponentTooltip(poseStack, skillButton.getTooltip(), mouseX, mouseY, borderStyleStack);
		}
	}

	public void buttonPressed(Button button) {
		if (button instanceof PassiveSkillButton skillButton) {
			var passiveSkill = skillButton.passiveSkill;

			if (skillButton.canLearnSkill) {
				learnedSkills.add(passiveSkill.getId());
				NetworkDispatcher.network_channel.sendToServer(new LearnSkillMessage(passiveSkill));
				skillPoints--;
				rebuildWidgets();
			} else if (passiveSkill.getConnectedTreeId() != null) {
				minecraft.setScreen(new SkillTreeScreen(passiveSkill.getConnectedTreeId()));
			}
		}
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		renderBackground(poseStack);
		renderConnections(poseStack, partialTick);
		super.render(poseStack, mouseX, mouseY, partialTick);
	}

	@Override
	public void renderBackground(PoseStack poseStack) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE_LOCATION);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		poseStack.pushPose();
		poseStack.translate(scrollX / 3F, scrollY / 3F, 0);
		blit(poseStack, width / 2 - 512, height / 2 - 512, 0, 0F, 0F, 1024, 1024, 1024, 1024);
		poseStack.popPose();
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragAmountX, double dragAmountY) {
		if (mouseButton != 0) {
			return false;
		} else {
			scrollX += dragAmountX;
			scrollY += dragAmountY;
			scrollX = Math.max(-maxScrollX, Math.min(maxScrollX, scrollX));
			scrollY = Math.max(-maxScrollY, Math.min(maxScrollY, scrollY));
			rebuildWidgets();
			return true;
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void renderConnections(PoseStack poseStack, float partialTick) {
		animation += partialTick;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, CONNECTION_TEXTURE_LOCATION);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		connections.forEach(connection -> renderConnection(poseStack, connection));
	}

	private void renderConnection(PoseStack poseStack, Pair<PassiveSkillButton, PassiveSkillButton> connection) {
		poseStack.pushPose();
		var button1 = connection.getLeft();
		var x1 = button1.x + button1.getWidth() / 2;
		var y1 = button1.y + button1.getHeight() / 2;
		var button2 = connection.getRight();
		var x2 = button2.x + button2.getWidth() / 2;
		var y2 = button2.y + button2.getHeight() / 2;
		poseStack.translate(x1, y1, 0);
		var angle = (float) Mth.atan2(y2 - y1, x2 - x1);
		poseStack.mulPose(Vector3f.ZP.rotation(angle));
		var glowing = button1.isSkillLearned && button2.isSkillLearned;
		var length = (int) Mth.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
		blit(poseStack, 0, -3, length, 6, 0, glowing ? 0 : 6, length, 6, 50, 12);
		var flashing = !glowing && (button1.isSkillLearned && button2.canLearnSkill || button2.isSkillLearned && button1.canLearnSkill);

		if (flashing) {
			RenderSystem.setShaderColor(1F, 1F, 1F, (Mth.sin(animation / 3F) + 1) / 2);
			blit(poseStack, 0, -3, length, 6, 0, 0, length, 6, 50, 12);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		}

		poseStack.popPose();
	}
}
