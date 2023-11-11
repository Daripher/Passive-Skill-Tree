package daripher.skilltree.mixin.minecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.api.SkillRequiringRecipe;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.skill.SkillTreeClientData;
import daripher.skilltree.client.widget.SkillButton;
import daripher.skilltree.skill.PassiveSkill;
import java.util.List;
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
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingScreen.class)
public abstract class CraftingScreenMixin extends AbstractContainerScreen<CraftingMenu> {
  private @Shadow @Final RecipeBookComponent recipeBookComponent;
  private SkillButton requiredSkillButton;

  @SuppressWarnings("DataFlowIssue")
  public CraftingScreenMixin() {
    super(null, null, null);
  }

  @Inject(method = "containerTick", at = @At("HEAD"))
  private void updateSkillButton(CallbackInfo callbackInfo) {
    RecipeBookPage recipeBookPage =
        ObfuscationReflectionHelper.getPrivateValue(
            RecipeBookComponent.class, recipeBookComponent, "f_100284_");
    if (!(recipeBookPage.getLastClickedRecipe()
        instanceof SkillRequiringRecipe lastClickedRecipe)) {
      if (requiredSkillButton != null) removeWidget(requiredSkillButton);
      return;
    }
    if (requiredSkillButton == null) {
      addRequiredSkillButton(lastClickedRecipe);
    } else if (!requiredSkillButton.skill.getId().equals(lastClickedRecipe.getRequiredSkillId())) {
      removeWidget(requiredSkillButton);
      addRequiredSkillButton(lastClickedRecipe);
    }
  }

  @Inject(method = "render", at = @At("TAIL"))
  private void render(
      PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo callbackInfo) {
    if (requiredSkillButton != null && requiredSkillButton.isHoveredOrFocused()) {
      requiredSkillButton.renderToolTip(poseStack, mouseX, mouseY);
    }
  }

  @Inject(method = "mouseClicked", at = @At("HEAD"))
  private void mouseClicked(
      double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> callbackInfo) {
    if (requiredSkillButton != null) {
      removeWidget(requiredSkillButton);
      requiredSkillButton = null;
    }
  }

  private void addRequiredSkillButton(SkillRequiringRecipe recipe) {
    PassiveSkill skill = SkillTreeClientData.getSkill(recipe.getRequiredSkillId());
    requiredSkillButton =
        new SkillButton(
            () -> 0F, leftPos + 132, topPos + 69, skill, b -> {}, this::renderButtonTooltip);
    requiredSkillButton.x -= requiredSkillButton.getWidth() / 2d;
    requiredSkillButton.y -= requiredSkillButton.getHeight() / 2d;
    Minecraft minecraft = Minecraft.getInstance();
    LocalPlayer player = minecraft.player;
    assert player != null;
    if (PlayerSkillsProvider.get(player).hasSkill(requiredSkillButton.skill.getId())) {
      requiredSkillButton.highlighted = true;
    }
    addRenderableWidget(requiredSkillButton);
  }

  private void renderButtonTooltip(Button button, PoseStack poseStack, int mouseX, int mouseY) {
    if (!(button instanceof SkillButton skillButton)) return;
    ItemStack borderStyleStack = skillButton.getTooltipBorderStyleStack();
    List<MutableComponent> tooltip = skillButton.getTooltip();
    LocalPlayer player = Minecraft.getInstance().player;
    assert player != null;
    if (!PlayerSkillsProvider.get(player).hasSkill(skillButton.skill.getId())) {
      tooltip.add(
          Component.translatable("widget.skill_button.not_learned").withStyle(ChatFormatting.RED));
    }
    renderComponentTooltip(poseStack, tooltip, mouseX, mouseY, borderStyleStack);
  }
}
