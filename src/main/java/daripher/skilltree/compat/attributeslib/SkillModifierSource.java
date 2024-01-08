package daripher.skilltree.compat.attributeslib;

import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.skill.PassiveSkill;
import dev.shadowsoffire.attributeslib.client.ModifierSource;
import java.util.Comparator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class SkillModifierSource extends ModifierSource<PassiveSkill> {
  public SkillModifierSource(PassiveSkill skill) {
    super(
        AttributesLibCompatibility.SKILL_MODIFIER_TYPE,
        Comparator.comparing(PassiveSkill::getId),
        skill);
  }

  @Override
  public void render(GuiGraphics graphics, Font font, int x, int y) {
    float scale = 0.5f;
    PoseStack stack = graphics.pose();
    stack.pushPose();
    stack.scale(scale, scale, 1f);
    stack.translate(x / scale, y / scale, 0f);
    graphics.blit(data.getIconTexture(), 0, 0, 0, 0, 16, 16, 16, 16);
    stack.popPose();
  }
}
