package daripher.skilltree.client.widget.editor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import daripher.skilltree.client.screen.ScreenHelper;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SkillMirrorer extends AbstractWidget {
  private final SkillTreeEditor editor;
  private float mirrorCenterX;
  private float mirrorCenterY;
  private float mirrorAngle;
  private int mirrorSides = 2;

  public SkillMirrorer(SkillTreeEditor editor) {
    super(0, 0, 0, 0, Component.empty());
    this.editor = editor;
    this.active = false;
  }

  public void init() {
    editor.addLabel(19, 0, "Mirror", ChatFormatting.GOLD);
    editor.addCheckBox(0, 0, active).setResponder(v -> setActive(editor, v));
    editor.increaseHeight(19);
    if (!active) return;
    editor.addLabel(0, 0, "Sectors", ChatFormatting.GOLD);
    editor
        .addNumericTextField(160, 0, 40, 14, mirrorSides)
        .setNumericFilter(v -> v > 1)
        .setNumericResponder(v -> mirrorSides = v.intValue());
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Angle", ChatFormatting.GOLD);
    editor
        .addNumericTextField(160, 0, 40, 14, mirrorAngle)
        .setNumericResponder(v -> mirrorAngle = v.floatValue());
    editor.increaseHeight(19);
    editor.addLabel(0, 0, "Center", ChatFormatting.GOLD);
    editor
        .addNumericTextField(160, 0, 40, 14, mirrorCenterX)
        .setNumericResponder(v -> mirrorCenterX = v.floatValue());
    editor
        .addNumericTextField(115, 0, 40, 14, mirrorCenterY)
        .setNumericResponder(v -> mirrorCenterY = v.floatValue());
    if (editor.getSelectedSkills().size() != 1) return;
    editor.addButton(70, 0, 40, 14, "Set").setPressFunc(b -> setMirrorCenter(editor));
    editor.increaseHeight(19);
  }

  @Override
  protected void renderWidget(
      @NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    if (!active) return;
    graphics.pose().pushPose();
    int width = editor.getScreenWidth();
    int height = editor.getScreenHeight();
    float mirrorX = width / 2f + mirrorCenterX * editor.getZoom() + editor.getScrollX();
    float mirrorY = height / 2f + mirrorCenterY * editor.getZoom() + editor.getScrollY();
    graphics.pose().translate(mirrorX, mirrorY, -1);
    graphics.pose().mulPose(Axis.ZP.rotationDegrees(mirrorAngle));
    for (int i = 0; i < mirrorSides; i++) {
      graphics.pose().mulPose(Axis.ZP.rotationDegrees(360f / mirrorSides));
      graphics.fill(-1, -1, 1, width * 2, 0x55CFCFCF);
    }
    ScreenHelper.drawRectangle(graphics, -4, -4, 8, 8, 0x55CFCFCF);
    graphics.pose().popPose();
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
  }

  private void setActive(SkillTreeEditor editor, boolean active) {
    this.active = active;
    editor.rebuildWidgets();
  }

  private void setMirrorCenter(SkillTreeEditor editor) {
    PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
    if (selectedSkill == null) return;
    mirrorCenterX = selectedSkill.getPositionX();
    mirrorCenterY = selectedSkill.getPositionY();
    editor.rebuildWidgets();
  }

  public @Nullable PassiveSkill getMirroredSkill(PassiveSkill skill, int sector) {
    float skillX = skill.getPositionX();
    float skillY = skill.getPositionY();
    if (mirrorCenterX == skillX && mirrorCenterY == skillY) {
      return skill;
    }
    float originalAngle =
        (float) Math.toDegrees(Math.atan2(skillY - mirrorCenterY, skillX - mirrorCenterX)) + 90;
    float sectorSize = 360f / mirrorSides;
    float angle =
        (float)
            Math.toRadians(
                mirrorSides == 2
                    ? -originalAngle + mirrorAngle * 2
                    : originalAngle + sectorSize * sector);
    float distance = (float) Math.hypot(skillX - mirrorCenterX, skillY - mirrorCenterY);
    float mirroredSkillX = mirrorCenterX + Mth.sin(angle) * distance;
    float mirroredSkillY = mirrorCenterY + Mth.cos((float) (angle + Math.PI)) * distance;
    return getSkillAtPosition(mirroredSkillX, mirroredSkillY);
  }

  public void createSkills(float angle, float distance, SkillFactory skillFactory) {
    if (!active) return;
    float sectorSize = 360f / mirrorSides;
    for (int i = 1; i < mirrorSides; i++) {
      angle = mirrorSides == 2 ? -angle - mirrorAngle * 2 : angle - sectorSize;
      float finalAngle = (float) Math.toRadians(angle);
      int sector = i;
      editor
          .getSelectedSkills()
          .forEach(skill -> createSkill(distance, finalAngle, sector, skill, skillFactory));
    }
  }

  private void createSkill(
      float distance, float angle, int sector, PassiveSkill skill, SkillFactory skillFactory) {
    skill = getMirroredSkill(skill, sector);
    if (skill == null) return;
    float skillSize = skill.getSkillSize() / 2f + 8;
    float skillX = skill.getPositionX() + Mth.sin(angle) * (distance + skillSize);
    float skillY = skill.getPositionY() + Mth.cos(angle) * (distance + skillSize);
    skillFactory.accept(skillX, skillY, skill);
  }

  @Nullable
  private PassiveSkill getSkillAtPosition(float x, float y) {
    for (PassiveSkill skill : editor.getSkills()) {
      double distance = Math.hypot(x - skill.getPositionX(), y - skill.getPositionY());
      if (distance < skill.getSkillSize()) return skill;
    }
    return null;
  }

  @Override
  protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {}
}
