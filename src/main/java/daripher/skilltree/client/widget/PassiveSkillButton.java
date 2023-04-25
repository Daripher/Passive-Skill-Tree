package daripher.skilltree.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

public class PassiveSkillButton extends Button {
	private static final Style LESSER_TITLE_STYLE = Style.EMPTY.withColor(0xCF905D).withBold(true);
	private static final Style NOTABLE_TITLE_STYLE = Style.EMPTY.withColor(0xB475CF).withBold(true);
	private static final Style KEYSTONE_TITLE_STYLE = Style.EMPTY.withColor(0xFFD75F).withBold(true);
	private static final Style DESCRIPTION_STYLE = Style.EMPTY.withColor(0x7272D6);
	private final SkillTreeScreen parentScreen;
	public final PassiveSkill passiveSkill;
	public boolean isSkillLearned;
	public boolean canLearnSkill;

	public PassiveSkillButton(SkillTreeScreen screen, int x, int y, PassiveSkill passiveSkill) {
		super(x, y, passiveSkill.getButtonSize(), passiveSkill.getButtonSize(), Component.empty(), screen::buttonPressed, screen::renderButtonTooltip);
		this.parentScreen = screen;
		this.passiveSkill = passiveSkill;
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, passiveSkill.getBackgroundTexture());
		RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		blit(poseStack, x, y, width, height, 0, 0, width, height, width * 3, height);
		var iconSize = width / 34F;
		poseStack.pushPose();
		poseStack.translate(x + width / 2, y + height / 2, 0);
		poseStack.scale(iconSize, iconSize, 1);
		RenderSystem.setShaderTexture(0, passiveSkill.getIconTexture());
		blit(poseStack, -8, -8, 16, 16, 0, 0, 16, 16, 16, 16);
		poseStack.popPose();
		RenderSystem.setShaderTexture(0, passiveSkill.getBackgroundTexture());
		blit(poseStack, x, y, width, height, width + (isSkillLearned ? width : 0), 0, width, height, width * 3, height);

		if (canLearnSkill) {
			RenderSystem.setShaderColor(1F, 1F, 1F, (Mth.sin(parentScreen.animation / 3F) + 1) / 2);
			blit(poseStack, x, y, width, height, width * 2, 0, width, height, width * 3, height);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		}

		if (isHoveredOrFocused()) {
			renderToolTip(poseStack, mouseX, mouseY);
		}
	}

	public List<MutableComponent> getTooltip() {
		var titleStyle = width == 24 ? KEYSTONE_TITLE_STYLE : width == 20 ? NOTABLE_TITLE_STYLE : LESSER_TITLE_STYLE;
		var skillId = getSkillId();
		var skillTitle = Component.translatable(skillId + ".name").withStyle(titleStyle);
		var description = Component.translatable(skillId + ".description").getString();
		var descriptionStrings = Arrays.asList(description.split("/n"));
		var skillDescription = descriptionStrings.stream().map(Component::translatable).map(component -> component.withStyle(DESCRIPTION_STYLE)).toList();
		var tooltip = new ArrayList<MutableComponent>();
		tooltip.add(skillTitle);
		tooltip.addAll(skillDescription);
		return tooltip;
	}

	public void setCanLearnSkill() {
		canLearnSkill = true;
	}

	private String getSkillId() {
		return "skill." + passiveSkill.getId().getNamespace() + "." + passiveSkill.getId().getPath();
	}
}
