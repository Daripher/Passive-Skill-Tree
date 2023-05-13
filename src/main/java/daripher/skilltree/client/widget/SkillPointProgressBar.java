package daripher.skilltree.client.widget;

import java.util.ArrayList;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.config.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SkillPointProgressBar extends AbstractWidget {
	private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(SkillTreeMod.MOD_ID, "textures/screen/skill_tree_widgets.png");
	private final SkillTreeScreen parentScreen;

	public SkillPointProgressBar(SkillTreeScreen parentScreen, int x, int y) {
		super(x, y, 235, 19, Component.empty());
		this.parentScreen = parentScreen;
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		renderBackground(poseStack);
		renderExperienceBar(poseStack);
		if (isHoveredOrFocused()) {
			renderToolTip(poseStack, mouseX, mouseY);
		}
	}

	protected void renderExperienceBar(PoseStack poseStack) {
		var minecraft = Minecraft.getInstance();
		var skillsCapability = PlayerSkillsProvider.get(minecraft.player);
		var skillPointsGained = skillsCapability.getPlayerSkills().size() + skillsCapability.getSkillPoints();
		var levelupCosts = Config.COMMON_CONFIG.getSkillPointCosts();
		var levelupCost = 1;
		var experienceProgress = 1F;
		if (skillPointsGained < levelupCosts.size()) {
			levelupCost = levelupCosts.get(skillPointsGained);
			experienceProgress = (float) skillPointsGained / levelupCost;
			return;
		} else {
			skillPointsGained--;
		}
		RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
		var filledBarWidth = (int) (experienceProgress * 183);
		blit(poseStack, x + 26, y + 7, 0, 64, 182, 5);
		if (filledBarWidth > 0) {
			blit(poseStack, x + 26, y + 7, 0, 69, filledBarWidth, 5);
		}
		var levelText = "" + skillPointsGained;
		var font = minecraft.font;
		drawCenteredOutlinedText(poseStack, levelText, font, x + 17, y + 5);
		var nextLevelText = "" + (skillPointsGained + 1);
		drawCenteredOutlinedText(poseStack, nextLevelText, font, x + width - 17, y + 5);
	}

	protected void renderBackground(PoseStack poseStack) {
		parentScreen.prepareTextureRendering(WIDGETS_LOCATION);
		blit(poseStack, x, y, 0, 0, width, height);
	}

	@Override
	public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
		var borderStyleStack = new ItemStack(Items.STONE);
		var tooltip = new ArrayList<MutableComponent>();
		var minecraft = Minecraft.getInstance();
		var skillsCapability = PlayerSkillsProvider.get(minecraft.player);
		var skillPointsAvailable = skillsCapability.getSkillPoints();
		tooltip.add(Component.translatable("widget.skill_point_progress_bar.text").withStyle(ChatFormatting.GRAY));
		var skillPointsComponent = Component.literal("" + skillPointsAvailable).withStyle(ChatFormatting.GREEN);
		tooltip.add(Component.translatable("widget.skill_point_progress_bar.points", skillPointsComponent).withStyle(ChatFormatting.GRAY));
		parentScreen.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY, borderStyleStack);
	}

	protected void drawCenteredOutlinedText(PoseStack poseStack, String text, Font font, int x, int y) {
		x -= font.width(text) / 2;
		font.draw(poseStack, text, x + 1, y, 0);
		font.draw(poseStack, text, x - 1, y, 0);
		font.draw(poseStack, text, x, y + 1, 0);
		font.draw(poseStack, text, x, y - 1, 0);
		font.draw(poseStack, text, x, y, 8453920);
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {
	}
}
