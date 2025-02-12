package daripher.skilltree.client.widget.skill;

import daripher.skilltree.client.screen.ScreenHelper;
import daripher.skilltree.client.widget.group.ScrollableZoomableWidgetGroup;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SkillButtons extends ScrollableZoomableWidgetGroup<SkillButton> {
  private final PassiveSkillTree skillTree;
  private final List<SkillConnection> skillConnections = new ArrayList<>();
  private final Map<ResourceLocation, SkillButton> idToWidget = new HashMap<>();
  private final Supplier<Float> animationFunc;

  public SkillButtons(PassiveSkillTree skillTree, Supplier<Float> animationFunc) {
    super(0, 0, 0, 0);
    this.skillTree = skillTree;
    this.animationFunc = animationFunc;
  }

  @Override
  public <W extends SkillButton> @NotNull W addWidget(@NotNull W widget) {
    idToWidget.put(widget.skill.getId(), widget);
    return super.addWidget(widget);
  }

  @Override
  public void clearWidgets() {
    idToWidget.clear();
    super.clearWidgets();
  }

  @Override
  protected void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    renderConnections(graphics, mouseX, mouseY);
  }

  protected void renderConnections(GuiGraphics graphics, int mouseX, int mouseY) {
    skillConnections.stream()
        .filter(c -> c.getType() == SkillConnection.Type.DIRECT)
        .forEach(c -> renderDirectConnection(graphics, c));
    skillConnections.stream()
        .filter(c -> c.getType() == SkillConnection.Type.LONG)
        .forEach(c -> renderLongConnection(graphics, c, mouseX, mouseY));
    skillConnections.stream()
        .filter(c -> c.getType() == SkillConnection.Type.ONE_WAY)
        .forEach(c -> renderOneWayConnection(graphics, c));
  }

  private void renderDirectConnection(GuiGraphics graphics, SkillConnection connection) {
    ScreenHelper.renderConnection(graphics, connection, getZoom(), animationFunc.get());
  }

  private void renderLongConnection(GuiGraphics graphics, SkillConnection connection, int mouseX, int mouseY) {
    SkillButton hoveredSkill = getWidgetAt(mouseX, mouseY);
    if (hoveredSkill != connection.getFirstButton() && hoveredSkill != connection.getSecondButton()) {
      return;
    }
    ScreenHelper.renderGatewayConnection(graphics, connection, true, getZoom(), animationFunc.get());
  }

  private void renderOneWayConnection(GuiGraphics graphics, SkillConnection connection) {
    ScreenHelper.renderOneWayConnection(graphics, connection, true, getZoom(), animationFunc.get());
  }

  public void renderTooltip(GuiGraphics graphics, float tooltipX, float tooltipY) {
    SkillButton skill = getWidgetAt(tooltipX, tooltipY);
    if (skill == null) return;
    ScreenHelper.renderSkillTooltip(skillTree, skill, graphics, tooltipX, tooltipY, width, height);
  }

  public PassiveSkillTree getSkillTree() {
    return skillTree;
  }

  public SkillButton addSkillButton(PassiveSkill skill, Supplier<Float> animationFunc) {
    float skillX = skill.getPositionX();
    float skillY = skill.getPositionY();
    int skillSize = skill.getSkillSize();
    float buttonX = skillX - skillSize / 2F + width / 2F + skillX * (getZoom() - 1);
    float buttonY = skillY - skillSize / 2F + height / 2F + skillY * (getZoom() - 1);
    SkillButton button = new SkillButton(animationFunc, buttonX, buttonY, skill);
    return addWidget(button);
  }

  public void updateSkillConnections() {
    skillConnections.clear();
    getWidgets().forEach(this::addSkillConnections);
  }

  private void addSkillConnections(SkillButton skillButton) {
    PassiveSkill skill = skillButton.skill;
    readSkillConnections(skill, SkillConnection.Type.DIRECT, skill.getDirectConnections());
    readSkillConnections(skill, SkillConnection.Type.LONG, skill.getLongConnections());
    readSkillConnections(skill, SkillConnection.Type.ONE_WAY, skill.getOneWayConnections());
  }

  private void readSkillConnections(PassiveSkill skill, SkillConnection.Type type, List<ResourceLocation> connections) {
    for (ResourceLocation connectedSkillId : new ArrayList<>(connections)) {
      if (idToWidget.get(connectedSkillId) == null) {
        connections.remove(connectedSkillId);
        continue;
      }
      connectSkills(type, skill.getId(), connectedSkillId);
    }
  }

  protected void connectSkills(SkillConnection.Type type, ResourceLocation skillId1, ResourceLocation skillId2) {
    SkillButton button1 = idToWidget.get(skillId1);
    SkillButton button2 = idToWidget.get(skillId2);
    skillConnections.add(new SkillConnection(type, button1, button2));
  }

  public SkillButton getWidgetById(ResourceLocation id) {
    return idToWidget.get(id);
  }

  public List<SkillConnection> getSkillConnections() {
    return skillConnections;
  }
}
