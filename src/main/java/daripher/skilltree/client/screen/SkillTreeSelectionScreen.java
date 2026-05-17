package daripher.skilltree.client.screen;

import daripher.skilltree.client.widget.SkillTreeSelectionButton;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.skill.PassiveSkillTree;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SkillTreeSelectionScreen extends Screen {
  public static final int BUTTONS_SIZE = 19;
  public static final int BUTTONS_SPACING = 5;

  public SkillTreeSelectionScreen() {
    super(Component.empty());
  }

  @Override
  protected void init() {
    clearWidgets();
    addSkillTreeButtons();
  }

  private void addSkillTreeButtons() {
    List<PassiveSkillTree> skillTrees = getNonEmptySkillTrees();
    int buttonCount = skillTrees.size();
    int buttonRowWidth = buttonCount * BUTTONS_SIZE - (buttonCount - 1) * BUTTONS_SPACING;
    int x = width / 2 - buttonRowWidth / 2;
    int y = height / 2 - BUTTONS_SIZE / 2;
    for (PassiveSkillTree skillTree : skillTrees) {
      Button button =
          new SkillTreeSelectionButton(x, y, BUTTONS_SIZE, BUTTONS_SIZE, skillTree.getId());
      x += BUTTONS_SIZE + BUTTONS_SPACING;
      addRenderableWidget(button);
    }
  }

  @NotNull
  private static List<PassiveSkillTree> getNonEmptySkillTrees() {
    return SkillTreesReloader.getSkillTrees().values().stream()
        .filter(skillTree -> !skillTree.getSkillIds().isEmpty())
        .toList();
  }

  @Override
  public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    super.render(guiGraphics, mouseX, mouseY, partialTick);
    for (Renderable widget : renderables) {
      if (!(widget instanceof SkillTreeSelectionButton button)) continue;
      if (!button.isMouseOver(mouseX, mouseY)) continue;
      guiGraphics.renderTooltip(font, button.getMessage(), mouseX, mouseY);
    }
  }

  @Override
  public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    ResourceLocation texture =
        ResourceLocation.parse("skilltree:textures/screen/skill_tree_background.png");
    int size = SkillTreeScreen.BACKGROUND_SIZE;
    guiGraphics.blit(
        texture, (width - size) / 2, (height - size) / 2, 0, 0F, 0F, size, size, size, size);
  }
}
