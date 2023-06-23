package daripher.skilltree.mixin.minecraft;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.vertex.PoseStack;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.api.PSTRecipe;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.SkillTreeClientData;
import daripher.skilltree.client.widget.SkillButton;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@Mixin(CraftingScreen.class)
public abstract class MixinCraftingScreen extends AbstractContainerScreen<CraftingMenu> {
	private @Shadow @Final RecipeBookComponent recipeBookComponent;
	private SkillButton requiredSkillButton;

	public MixinCraftingScreen() {
		super(null, null, null);
	}

	@Inject(method = "containerTick", at = @At("HEAD"))
	private void updateSkillButton(CallbackInfo callbackInfo) {
		RecipeBookPage recipeBookPage = ObfuscationReflectionHelper.getPrivateValue(RecipeBookComponent.class, recipeBookComponent, "f_100284_");
		if (!(recipeBookPage.getLastClickedRecipe() instanceof PSTRecipe)) {
			if (requiredSkillButton != null) removeWidget(requiredSkillButton);
			return;
		}
		var lastClickedRecipe = (PSTRecipe) recipeBookPage.getLastClickedRecipe();
		if (requiredSkillButton == null) {
			addRequiredSkillButton(lastClickedRecipe);
		} else if (!requiredSkillButton.skill.getId().equals(lastClickedRecipe.getRequiredSkillId())) {
			removeWidget(requiredSkillButton);
			addRequiredSkillButton(lastClickedRecipe);
		}
	}
	
	@Inject(method = "render", at = @At("TAIL"))
	private void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo callbackInfo) {
		if (requiredSkillButton != null && requiredSkillButton.isHoveredOrFocused()) {
			requiredSkillButton.renderToolTip(poseStack, mouseX, mouseY);
		}
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"))
	private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (requiredSkillButton != null) {
			removeWidget(requiredSkillButton);
			requiredSkillButton = null;
		}
	}
	
	private void addRequiredSkillButton(PSTRecipe recipe) {
		PassiveSkill skill = SkillTreeClientData.getSkillsForTree(new ResourceLocation(SkillTreeMod.MOD_ID, "tree")).get(recipe.getRequiredSkillId());
		requiredSkillButton = new SkillButton(() -> 0F, leftPos + 132, topPos + 69, skill, this::buttonPressed, this::renderButtonTooltip);
		requiredSkillButton.x -= requiredSkillButton.getWidth() / 2;
		requiredSkillButton.y -= requiredSkillButton.getHeight() / 2;
		LocalPlayer player = Minecraft.getInstance().player;
		if (PlayerSkillsProvider.get(player).hasSkill(requiredSkillButton.skill.getId())) {
			requiredSkillButton.highlighted = true;
		}
		addRenderableWidget(requiredSkillButton);
	}

	private void buttonPressed(Button button) {
	}

	private void renderButtonTooltip(Button button, PoseStack poseStack, int mouseX, int mouseY) {
		if (!(button instanceof SkillButton)) return;
		SkillButton skillButton = (SkillButton) button;
		ItemStack borderStyleStack = skillButton.getTooltipBorderStyleStack();
		List<MutableComponent> tooltip = skillButton.getTooltip();
		LocalPlayer player = Minecraft.getInstance().player;
		if (!PlayerSkillsProvider.get(player).hasSkill(skillButton.skill.getId())) {
			tooltip.add(Component.translatable("widget.skill_button.not_learned").withStyle(ChatFormatting.RED));
		}
		renderComponentTooltip(poseStack, tooltip, mouseX, mouseY, borderStyleStack);
	}
}
